package com.example.familymapclient;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import Request.LoginRequest;
import Request.RegisterRequest;
import Result.LoginResult;
import Result.MultipleEventResult;
import Result.MultiplePersonResult;
import Result.RegisterResult;

public class ServerProxy {

    private final String serverHost;
    private final String serverPort;

    public ServerProxy(String serverHost, String serverPort) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
    }

    public LoginResult login(LoginRequest request) {
        try {
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/user/login");

            HttpURLConnection http = (HttpURLConnection) url.openConnection();

            http.setRequestMethod("POST");
            http.setReadTimeout(5000);
            http.setDoOutput(true);
            System.out.println("connecting");
            http.connect();

            Gson gson = new Gson();
            String reqData = gson.toJson(request);

            OutputStream reqBody = http.getOutputStream();

            writeString(reqData, reqBody);
            System.out.println("writing");
            reqBody.close();

            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream respBody = http.getInputStream();

                String respData = readString(respBody);

                gson = new Gson();
                return gson.fromJson(respData, LoginResult.class);
            }
            else {
                System.out.println("ERROR: " + http.getResponseMessage());
                return new LoginResult(false, "login failed");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public RegisterResult register(RegisterRequest request) {
        try {
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/user/register");

            HttpURLConnection http = (HttpURLConnection) url.openConnection();

            http.setRequestMethod("POST");

            http.setDoOutput(true);

            http.connect();

            Gson gson = new Gson();
            String reqData = gson.toJson(request);

            OutputStream reqBody = http.getOutputStream();

            writeString(reqData, reqBody);
            reqBody.close();

            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream respBody = http.getInputStream();

                String respData = readString(respBody);

                gson = new Gson();
                return gson.fromJson(respData, RegisterResult.class);
            }
            else {
                System.out.println("ERROR: " + http.getResponseMessage());
                return new RegisterResult(false, "Register Failed");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public MultiplePersonResult getPeople(String authToken) {
         try {
             URL url = new URL("http://" + serverHost + ":" + serverPort + "/person");
             HttpURLConnection http = (HttpURLConnection) url.openConnection();

             http.setRequestMethod("GET");

             http.setDoOutput(false);

             http.addRequestProperty("Authorization", authToken);

             http.addRequestProperty("Accept", "application/json");

             http.connect();

             if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                 InputStream respBody = http.getInputStream();

                 String respData = readString(respBody);

                 Gson gson = new Gson();
                 return gson.fromJson(respData, MultiplePersonResult.class);
             }
             else {
                 System.out.println("ERROR: " + http.getResponseMessage());
                 return new MultiplePersonResult("Couldn't get people", false);
             }

         } catch (IOException e) {
             e.printStackTrace();
         }
         return null;
    }

    public MultipleEventResult getEvents(String authToken) {
        try {
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/event");
            HttpURLConnection http = (HttpURLConnection) url.openConnection();

            http.setRequestMethod("GET");

            http.setDoOutput(false);

            http.addRequestProperty("Authorization", authToken);

            http.addRequestProperty("Accept", "application/json");

            http.connect();

            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream respBody = http.getInputStream();

                String respData = readString(respBody);

                Gson gson = new Gson();
                return gson.fromJson(respData, MultipleEventResult.class);
            }
            else {
                System.out.println("ERROR: " + http.getResponseMessage());
                return new MultipleEventResult("Couldn't get events", false);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String readString(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        InputStreamReader sr = new InputStreamReader(is);
        char[] buf = new char[1024];
        int len;
        while ((len = sr.read(buf)) > 0) {
            sb.append(buf, 0, len);
        }
        return sb.toString();
    }

    private static void writeString(String str, OutputStream os) throws IOException {
        OutputStreamWriter sw = new OutputStreamWriter(os);
        sw.write(str);
        sw.flush();
    }

}
