package Handler;

import Request.LoadRequest;
import Result.LoadResult;
import Service.LoadService;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.net.HttpURLConnection;
import java.util.Locale;

public class LoadHandler implements HttpHandler {
  @Override
  public void handle(HttpExchange exchange) throws IOException {
    boolean success = false;

    try {
      if (exchange.getRequestMethod().toLowerCase(Locale.ROOT).equals("post")) {
        InputStream reqBody = exchange.getRequestBody();

        String reqData = readString(reqBody);

        System.out.println(reqData);
        Gson gson = new Gson();
        LoadRequest request = gson.fromJson(reqData, LoadRequest.class);

        LoadService service = new LoadService();
        LoadResult result = service.load(request);
        success = result.isSuccess();

        if (!success) {
          exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
          exchange.getResponseBody().close();
        } else {
          exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
        }

        Writer resBody = new OutputStreamWriter(exchange.getResponseBody());
        gson.toJson(result, resBody);
        resBody.close();
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
