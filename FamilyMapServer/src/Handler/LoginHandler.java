package Handler;

import Request.LoginRequest;
import Result.LoginResult;
import Service.LoginService;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.net.HttpURLConnection;
import java.util.Locale;

public class LoginHandler implements HttpHandler {
  @Override
  public void handle(HttpExchange exchange) throws IOException {
    boolean success;

    try {
      if (exchange.getRequestMethod().toLowerCase(Locale.ROOT).equals("post")) {
        InputStream reqBody = exchange.getRequestBody();

        String reqData = readString(reqBody);

        System.out.println(reqData);
        Gson gson = new Gson();
        LoginRequest request = gson.fromJson(reqData, LoginRequest.class);

        LoginService service = new LoginService();
        LoginResult result = service.login(request);
        success = result.isSuccess();

        if (!success) {
          exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
        } else {
          exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
        }
        Writer resBody = new OutputStreamWriter(exchange.getResponseBody());
        gson.toJson(result, resBody);
        resBody.close();
        System.out.println(success);
        System.out.println(result.getMessage());
      }
    } catch (IOException e) {
      exchange.sendResponseHeaders(HttpURLConnection.HTTP_SERVER_ERROR, 0);
      exchange.getResponseBody().close();
      e.printStackTrace();
    }
  }

  private String readString(InputStream is) throws IOException {
    StringBuilder sb = new StringBuilder();
    InputStreamReader sr = new InputStreamReader(is);
    char[] buf = new char[1024];
    int len;
    while ((len = sr.read(buf)) > 0) {
      sb.append(buf, 0, len);
    }
    return sb.toString();
  }
}
