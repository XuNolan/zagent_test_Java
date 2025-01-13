package org.example.refactor.sprocess.httpRegister;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.example.refactor.sprocess.httpRegister.entity.RegisterRequest;
import org.example.refactor.sprocess.httpRegister.entity.RegisterResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Slf4j
public class HttpRegister {
    public static RegisterResponse doRegister(RegisterRequest registerRequest, String url) throws IOException {
        URL urlObj = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        String jsonData = JSON.toJSONString(registerRequest);
        log.info("jsonData:{}", jsonData);

        OutputStream os = conn.getOutputStream();
        byte[] bytes = jsonData.getBytes(StandardCharsets.US_ASCII);
        os.write(bytes, 0, bytes.length);
        os.flush();

        int responseCode = conn.getResponseCode();
        System.out.println("Response Code: " + responseCode);

        //读取；
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }

        conn.disconnect();

        log.info("response:{}", response);

        return JSON.parseObject(response.toString(), RegisterResponse.class);

    }
}
