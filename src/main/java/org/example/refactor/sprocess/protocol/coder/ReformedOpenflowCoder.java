package org.example.refactor.sprocess.protocol.coder;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.example.refactor.sprocess.protocol.reformedOpenflow.OFPHeader;
import org.example.refactor.sprocess.protocol.reformedOpenflow.OFPTType;
import org.example.refactor.sprocess.protocol.reformedOpenflow.ReformedOpenflowPkgEntity;
import org.example.refactor.sprocess.protocol.reformedOpenflow.payload.ReformedOpenflowData;
import org.example.refactor.sprocess.protocol.reformedOpenflow.payload.ReformedOvsdbData;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Getter
public class ReformedOpenflowCoder {

    Socket socket;
    DataInputStream dataInputStream;
    OutputStream outputStream;

    public ReformedOpenflowCoder(InputStream inputStream, OutputStream outputStream, Socket socket) {
        this.dataInputStream = new DataInputStream(inputStream);
        this.outputStream = outputStream;
        this.socket = socket;
    }

    public int writeAndFlush(ReformedOpenflowPkgEntity reformedOpenflowPkgEntity) throws IOException {
        ByteBuffer byteBuffer;

        if(reformedOpenflowPkgEntity.header.type == OFPTType.OFPT_OVSDB.getValue()){
            ReformedOvsdbData reformedOvsdbData = (ReformedOvsdbData) reformedOpenflowPkgEntity.getPayload();
            byteBuffer = ByteBuffer.allocate(reformedOvsdbData.getLength());
            //头部；
            byteBuffer.put(reformedOpenflowPkgEntity.header.version);
            byteBuffer.put(reformedOpenflowPkgEntity.header.type);
            byteBuffer.putShort(reformedOpenflowPkgEntity.header.length);
            byteBuffer.putInt(reformedOpenflowPkgEntity.header.xid);
            //payload；
            byteBuffer.putInt(reformedOvsdbData.length);
            byteBuffer.put(reformedOvsdbData.jsonStr.getBytes(StandardCharsets.US_ASCII));
        } else {
            byteBuffer = ByteBuffer.allocate(reformedOpenflowPkgEntity.header.length);
            //头部；
            byteBuffer.put(reformedOpenflowPkgEntity.header.version);
            byteBuffer.put(reformedOpenflowPkgEntity.header.type);
            byteBuffer.putShort(reformedOpenflowPkgEntity.header.length);
            byteBuffer.putInt(reformedOpenflowPkgEntity.header.xid);
            //payload；
            if (reformedOpenflowPkgEntity.getPayload()!=null){
                byte[] datas = ((ReformedOpenflowData) reformedOpenflowPkgEntity.getPayload()).getReformedOpenflowData();
                if(datas != null && datas.length > 0)
                    byteBuffer.put(datas);
            }
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
            throw new IOException(e);
        }
        return bytes.length;
    }

    public ReformedOpenflowPkgEntity read() throws IOException {
        ReformedOpenflowPkgEntity reformedOpenflowPkgEntity = new ReformedOpenflowPkgEntity();
        try {
            OFPHeader header = new OFPHeader(
                    dataInputStream.readByte(),
                    dataInputStream.readByte(),
                    dataInputStream.readShort(),
                    dataInputStream.readInt());

            reformedOpenflowPkgEntity.setHeader(header);

            if(reformedOpenflowPkgEntity.header.type == org.example.refactor.sprocess.protocol.reformedOpenflow.OFPTType.OFPT_OVSDB.getValue()){
                log.info("read ovsdb pkg, header: {}", header);
                ReformedOvsdbData reformedOvsdbData = new ReformedOvsdbData();

                reformedOvsdbData.length = dataInputStream.readInt();

                int jsonStrLen = reformedOvsdbData.length - ReformedOpenflowPkgEntity.OVSDBHEADERLEN;
                byte[] jsonStrData = new byte[jsonStrLen];
                int readn = dataInputStream.read(jsonStrData, 0, jsonStrLen);
                log.info("read byte {}", readn);

                reformedOvsdbData.jsonStr = new String(jsonStrData, 0, jsonStrLen, StandardCharsets.US_ASCII);
                reformedOpenflowPkgEntity.setPayload(reformedOvsdbData);
            } else {
                log.info("read openflow pkg, header: {}", header);

                ReformedOpenflowData openflowData = new ReformedOpenflowData();

                byte[] datas = null;
                if(reformedOpenflowPkgEntity.header.length > ReformedOpenflowPkgEntity.OFPHEADERLEN) {
                    int validLen =  reformedOpenflowPkgEntity.header.length - ReformedOpenflowPkgEntity.OFPHEADERLEN;
                    datas = new byte[validLen];
                    for(int i=0;i<validLen;i++)
                        datas[i] = dataInputStream.readByte();
                }
                openflowData.setReformedOpenflowData(datas);
                reformedOpenflowPkgEntity.setPayload(openflowData);
            }
            return reformedOpenflowPkgEntity;
        } catch (IOException e) {
            log.error(e.getMessage());
            log.error("close socket streams");
            try {
                dataInputStream.close();
                outputStream.close();
            } catch (IOException ex) {
                log.error(ex.getMessage());
            }
            throw new IOException(e);
        }
    }
}
