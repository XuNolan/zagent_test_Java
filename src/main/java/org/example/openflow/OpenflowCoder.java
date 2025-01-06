package org.example.openflow;

import lombok.extern.slf4j.Slf4j;
import org.example.openflow.entity.openflow.OpenflowPkgEntity;
import org.example.openflow.entity.openflow.payload.OpenflowData;
import org.example.openflow.entity.openflow.payload.OpenflowPayload;
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

    public int writeAndFlush(OpenflowPkgEntity openflowPkgEntity){
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
            byte[] datas = ((OpenflowData)openflowPkgEntity.getPayload()).getOpenflowData();
            if(datas.length > 0)
                byteBuffer.put(datas);
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
                openflowPkgEntity.setPayload(new OvsdbData());
                OvsdbData ovsdbData = (OvsdbData) openflowPkgEntity.getPayload();
                ovsdbData.length = dataInputStream.readInt();
                StringBuffer sb = new StringBuffer();
                for(int i=0;i< ovsdbData.length;i++){
                    sb.append(dataInputStream.readUTF());
                }
                ovsdbData.jsonStr = sb.toString();
            } else {
                openflowPkgEntity.setPayload(new OpenflowData());
                OpenflowData openflowData = (OpenflowData) openflowPkgEntity.getPayload();
                byte[] datas = new byte[OpenflowPkgEntity.OFPHEADERLEN - openflowPkgEntity.header.length];
                for(int i=OpenflowPkgEntity.OFPHEADERLEN;i<openflowPkgEntity.header.length;i++){
                    datas[i] = dataInputStream.readByte();
                }
                openflowData.setOpenflowData(datas);
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
