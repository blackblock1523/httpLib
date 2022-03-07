package com.blackblock.http.factory;

import com.blackblock.http.listener.IResultListener;
import com.blackblock.http.model.Request;
import com.blackblock.http.model.Response;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.Map;
import java.util.UUID;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author blackblock1523
 * @create-time 2022/3/7 17:03
 * @desc 文件上传
 */
public class FileRequest extends BaseRequest {

    private static final String BOUNDARY = UUID.randomUUID().toString(); //边界标识 随机生成
    private static final String PREFIX = "--";
    private static final String NEW_LINE = "\r\n";

    public FileRequest(Request request, IResultListener resultListener) {
        super(request, resultListener);
    }

    @Override
    public void execute() {
        super.execute();
        URL url;
        OutputStream os = null;

        try {
            url = new URL(request.getUrl());
            initHttpConfig(url);
            // default http header
            httpConn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + BOUNDARY);
            // user custom header
            initHeader();
            httpConn.connect();

            os = httpConn.getOutputStream();
            DataOutputStream dos = new DataOutputStream(os);
            dos.write(getParams(request.getParams()).getBytes());
            dos.flush();

            // file upload.
            StringBuilder sb = new StringBuilder();
            for (File file : request.getFiles()) {
                String fileName = file.getName();
                sb.append(PREFIX)
                        .append(BOUNDARY)
                        .append(NEW_LINE)
                        .append("Content-Disposition: form-data; name=\"file\"; filename=\"")
                        .append(fileName)
                        .append("\"")
                        .append(NEW_LINE);
                String lowerCase = fileName.toLowerCase();
                if (lowerCase.endsWith(".jpg") ||
                        lowerCase.endsWith(".png") ||
                        lowerCase.endsWith(".bmp")) {
                    sb.append("Content-Type: image/jpeg")
                            .append(NEW_LINE);
                } else {
                    sb.append("Content-Type: multipart/form-data; charset=utf-8")
                            .append(NEW_LINE);
                }
                sb.append("Content-Length: ")
                        .append(file.length())
                        .append(NEW_LINE);

                dos.write(sb.toString().getBytes());
                dos.flush();

                FileInputStream fileInputStream = new FileInputStream(file);
                byte[] bytes = new byte[1024];
                int len;
                while ((len = fileInputStream.read()) != -1) {
                    dos.write(bytes, 0, len);
                }
                dos.write(NEW_LINE.getBytes());
                fileInputStream.close();
            }
            // file ended signal
            dos.write((PREFIX + BOUNDARY + PREFIX + NEW_LINE).getBytes());
            dos.flush();
            dos.close();

            int responseCode = httpConn.getResponseCode();
            if (responseCode == -1) {
                throw new IOException("Could not retrieve response code from HttpUrlConnection.");
            }

            onResponse();
        } catch (IOException ex) {
            ex.printStackTrace();
            resultListener.onFailed(ex, new Response(0, null));
        } finally {
            try {
                if (os != null) os.close();
            } catch (Exception e) {

            }
            httpConn.disconnect();
        }
    }

    private String getParams(Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        JSONObject jsonObject = new JSONObject(params);

        sb.append(PREFIX)
                .append(BOUNDARY)
                .append(NEW_LINE)
                .append("Content-Type: multipart/form-data; charset=")
                .append(UTF_8)
                .append(NEW_LINE)
                .append("Content-Length: ")
                .append(jsonObject.toString().length())
                .append(NEW_LINE)
                .append(jsonObject.toString())
                .append(NEW_LINE);

        for (Map.Entry<String, String> entry : params.entrySet()) {
            sb.append(PREFIX)
                    .append(BOUNDARY)
                    .append(NEW_LINE)
                    .append("Content-Disposition: form-data; name=\"")
                    .append(entry.getKey())
                    .append("\"")
                    .append(NEW_LINE)
                    .append("Content-Length: ")
                    .append(entry.getValue().length())
                    .append(NEW_LINE)
                    .append(entry.getValue())
                    .append(NEW_LINE);
        }

        return sb.toString();
    }
}
