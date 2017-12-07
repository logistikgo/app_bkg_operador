package com.example.logistik.logistikgobkg.Htpp;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by logistik on 15/11/2017.
 */

public class HttpClient {
    private String url;
    private HttpURLConnection con;
    private OutputStream os;

    private String delimiter = "--";
    private String boundary = "SwA" + Long.toString(System.currentTimeMillis()) + "SwA";

    public HttpClient(String url) {
        this.url = url;
    }

    public byte[] downloadImage(String imgName) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            System.out.println("URL [" + url + "] - Name [" + imgName + "]");

            HttpURLConnection con = (HttpURLConnection) (new URL(url)).openConnection();
            con.setRequestMethod("POST");
            con.setDoInput(true);
            con.setDoOutput(true);
            con.connect();
            con.getOutputStream().write(("name=" + imgName).getBytes());

            InputStream is = con.getInputStream();
            byte[] b = new byte[1024];

            while (is.read(b) != -1)
                baos.write(b);

            con.disconnect();
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return baos.toByteArray();
    }

    public void connectForMultipart(String strFormat) throws Exception {
        con = (HttpURLConnection) (new URL(url)).openConnection();

        con.setRequestMethod("POST");
        //  con.getAllowUserInteraction ();
        con.setDoInput(true);
        con.setDoOutput(true);
        con.setRequestProperty("Host", "localhost:63520");
        con.setRequestProperty("Connection", "Keep-Alive");
        if (strFormat == "Image") {
            con.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        }
        else {
            con.setRequestProperty("Content-Type", "application/json; charset=utf-8");
        }
        con.connect();
        os = con.getOutputStream();
    }

    public void addFormPart(String paramName, String value) throws Exception {
        writeParamData(paramName, value);
    }

    public void addParamJson(JSONObject jsonObject) throws Exception {
        writeParamDescription(jsonObject);
    }

    public void addFilePart(String paramName, String fileName, byte[] data) throws Exception {
        os.write((delimiter + boundary + "\r\n").getBytes());
        os.write(("Content-Disposition: form-data; name=\"" + paramName + "\"; filename=\"" + fileName + "\"\r\n").getBytes());
        os.write(("Content-Type: application/octet-stream\r\n").getBytes());
        os.write(("Content-Transfer-Encoding: binary\r\n").getBytes());
        os.write("\r\n".getBytes());

        os.write(data);

        os.write("\r\n".getBytes());
    }

    public void finishMultipart() throws Exception {
        os.write((delimiter + boundary + delimiter + "\r\n").getBytes());
    }


    public JSONObject getResponse() throws Exception {
        InputStream is = con.getInputStream();
        byte[] b1 = new byte[1024];
        StringBuffer buffer = new StringBuffer();

        while (is.read(b1) != -1)
            buffer.append(new String(b1));

        con.disconnect();

        String JRes = buffer.toString();

        return new JSONObject(JRes);
        //comentariado
    }

    private void writeParamData(String paramName, String value) throws Exception {

        os.write((delimiter + boundary + "\r\n").getBytes());
        os.write("Content-Type: text/plain\r\n".getBytes());
        os.write(("Content-Disposition: form-data; name=\"" + paramName + "\"\r\n").getBytes());
        ;
        os.write(("\r\n" + value + "\r\n").getBytes());


    }

    private void writeParamDescription(JSONObject jsonObject) throws Exception {
        os.write(jsonObject.toString().getBytes("UTF-8"));
    }
}
