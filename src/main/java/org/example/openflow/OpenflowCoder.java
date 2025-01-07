package org.example.openflow;

import lombok.extern.slf4j.Slf4j;
import org.example.openflow.entity.openflow.OFPHeader;
import org.example.openflow.entity.openflow.OpenflowPkgEntity;
import org.example.openflow.entity.openflow.payload.OpenflowData;
import org.example.openflow.entity.openflow.payload.OvsdbData;

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

    public int writeAndFlush(OpenflowPkgEntity openflowPkgEntity) throws IOException {
        ByteBuffer byteBuffer;

        if(openflowPkgEntity.header.type == 99){
            OvsdbData ovsdbData = (OvsdbData) openflowPkgEntity.getPayload();
            byteBuffer = ByteBuffer.allocate(ovsdbData.getLength());
            //头部；
            byteBuffer.put(openflowPkgEntity.header.version);
            byteBuffer.put(openflowPkgEntity.header.type);
            byteBuffer.putShort(openflowPkgEntity.header.length);
            byteBuffer.putInt(openflowPkgEntity.header.xid);
            //payload；
            byteBuffer.putInt(ovsdbData.length);
            byteBuffer.put(ovsdbData.jsonStr.getBytes(StandardCharsets.UTF_8));
        } else {
            byteBuffer = ByteBuffer.allocate(openflowPkgEntity.header.length);
            //头部；
            byteBuffer.put(openflowPkgEntity.header.version);
            byteBuffer.put(openflowPkgEntity.header.type);
            byteBuffer.putShort(openflowPkgEntity.header.length);
            byteBuffer.putInt(openflowPkgEntity.header.xid);
            //payload；
            if (openflowPkgEntity.getPayload()!=null){
                byte[] datas = ((OpenflowData)openflowPkgEntity.getPayload()).getOpenflowData();
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

    public OpenflowPkgEntity read() throws IOException {
        OpenflowPkgEntity openflowPkgEntity = new OpenflowPkgEntity();
        try {
            OFPHeader header = new OFPHeader(
                    dataInputStream.readByte(),
                    dataInputStream.readByte(),
                    dataInputStream.readShort(),
                    dataInputStream.readInt());

            openflowPkgEntity.setHeader(header);

            if((int) openflowPkgEntity.header.type == 99){
                openflowPkgEntity.setPayload(new OvsdbData());
                OvsdbData ovsdbData = (OvsdbData) openflowPkgEntity.getPayload();
                ovsdbData.length = dataInputStream.readInt();
                StringBuilder sb = new StringBuilder();
                for(int i=0;i< ovsdbData.length;i++){
                    sb.append(dataInputStream.readUTF());
                }
                ovsdbData.jsonStr = sb.toString();
            } else {
                OpenflowData openflowData = new OpenflowData();

                byte[] datas = null;
                if(openflowPkgEntity.header.length > OpenflowPkgEntity.OFPHEADERLEN) {
                    int validLen =  openflowPkgEntity.header.length - OpenflowPkgEntity.OFPHEADERLEN;
                    datas = new byte[validLen];
                    for(int i=0;i<validLen;i++)
                        datas[i] = dataInputStream.readByte();
                }
                openflowData.setOpenflowData(datas);
                openflowPkgEntity.setPayload(openflowData);
            }
            return openflowPkgEntity;
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
