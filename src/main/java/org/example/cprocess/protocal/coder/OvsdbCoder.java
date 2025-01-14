package org.example.cprocess.protocal.coder;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.newsclub.net.unix.AFUNIXSocket;
import org.newsclub.net.unix.AFUNIXSocketAddress;

import java.io.*;
import java.net.Socket;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Setter
@Getter
public class OvsdbCoder {
    private static int bufferLen = 1024;

    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;


    List<String> jsonMessage = new ArrayList<>();
    CharBuffer charBuffer = CharBuffer.allocate(bufferLen);

    private int left = 0;
    private int right = 0;
    private boolean inS = false;

    public static OvsdbCoder OpenOvsdbConnection(String ovsdbUnixSocketFilename) throws IOException {
        // 创建 UnixDomainSocketAddress
        File socketFile = new File(ovsdbUnixSocketFilename);
        OvsdbCoder ovsdbCoder;
        try (Socket socket = AFUNIXSocket.newInstance()) {// 创建客户端
            socket.connect(new AFUNIXSocketAddress(socketFile));// 连接服务端
            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();

            ovsdbCoder = new OvsdbCoder();
            ovsdbCoder.setSocket(socket);
            ovsdbCoder.setReader(new BufferedReader(new InputStreamReader(in)));
            ovsdbCoder.setWriter(new PrintWriter(out, true));
        } catch (IOException e) {
            log.error("ovsdb unix socket open failed, {}", e.getMessage(), e);
            throw e;
        }
        return ovsdbCoder;
    }

    public boolean hasMessage() {
        return !jsonMessage.isEmpty();
    }

    public String read() throws IOException {
        while(jsonMessage.isEmpty()) {
            char[] chars = new char[bufferLen];
            int n;
            try {
                n = reader.read(chars);
            }catch (IOException e) {
                log.error("json read error", e);
                throw e;
            }
            decode(chars, n);
        }
        String front = jsonMessage.get(0);
        jsonMessage.remove(0);
        return front;
    }

    public void decode(char[] chars, int n) {
        for(int i=0;i<n;i++) {
            char c = chars[i];
            switch (c){
                case '{':{
                    if (!inS) {
                        left++;
                    }
                    break;
                }
                case '}': {
                    if (!inS) {
                        right++;
                    }
                    break;
                }
                case '"': {
                    if(i==0 || chars[i-1]!='\\'){ //todo：可能会出现问题？
                        inS = !inS;
                    }
                }
                charBuffer.append(chars[i]);
                if(left !=0 && left == right && !inS){
                    jsonMessage.add(charBuffer.toString());
                    left = 0;
                    right = 0;
                    charBuffer.clear();
                }
            }
        }
    }


    public void write(String jsonStr) throws IOException {
        writer.write(jsonStr);
    }
}
