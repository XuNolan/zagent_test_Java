package org.example.openflow;

import lombok.extern.slf4j.Slf4j;
import org.example.openflow.entity.OpenflowPkgEntity;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

@Slf4j
public class OpenflowCoder {

    DataInputStream dataInputStream;
    OutputStream outputStream;

    public OpenflowCoder(InputStream inputStream, OutputStream outputStream) {
        this.dataInputStream = new DataInputStream(inputStream);
        this.outputStream = outputStream;
    }

    public int writeAndFlush(OpenflowPkgEntity openflowPkgEntity){
        ByteBuffer byteBuffer;

        if(openflowPkgEntity.header.type == 99){
            byteBuffer = ByteBuffer.allocate(openflowPkgEntity.length);
        } else
            byteBuffer = ByteBuffer.allocate(openflowPkgEntity.header.length);

        //写入头部；
        byteBuffer.put(openflowPkgEntity.header.version);
        byteBuffer.put(openflowPkgEntity.header.type);
        byteBuffer.putShort(openflowPkgEntity.header.length);
        byteBuffer.putInt(openflowPkgEntity.header.xid);
        if(openflowPkgEntity.header.type == 99){
            byteBuffer.putInt(openflowPkgEntity.length);
            byteBuffer.put(openflowPkgEntity.jsonStr.getBytes(StandardCharsets.UTF_8));
        }
        byteBuffer.order(ByteOrder.BIG_ENDIAN);
        byte[] bytes = byteBuffer.array();
        try {
            outputStream.write(bytes);
            outputStream.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
            try {
                dataInputStream.close();
                outputStream.close();
            } catch (IOException ex) {
                log.error(ex.getMessage());
            }
            return 0;
        }
        return bytes.length;
    }

    public OpenflowPkgEntity read(){
        OpenflowPkgEntity openflowPkgEntity = new OpenflowPkgEntity();
        try {
            openflowPkgEntity.header.version = dataInputStream.readByte();
            openflowPkgEntity.header.type = dataInputStream.readByte();
            openflowPkgEntity.header.length = dataInputStream.readShort();
            openflowPkgEntity.header.xid = dataInputStream.readInt();
            if((int) openflowPkgEntity.header.type == 99){
                openflowPkgEntity.length = dataInputStream.readInt();
                StringBuffer sb = new StringBuffer();
                for(int i=0;i< openflowPkgEntity.length;i++){
                    sb.append(dataInputStream.readUTF());
                }
                openflowPkgEntity.jsonStr = sb.toString();
            }
            return openflowPkgEntity;
        } catch (IOException e) {
            log.error(e.getMessage());
            try {
                dataInputStream.close();
                outputStream.close();
            } catch (IOException ex) {
                log.error(ex.getMessage());
            }
        }
        return null;
    }
}
