package Handler;

import Request.FillRequest;
import Result.FillResult;
import Service.FillService;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.net.HttpURLConnection;
import java.util.Locale;

public class FillHandler implements HttpHandler {
  @Override
  public void handle(HttpExchange exchange) throws IOException {
    boolean success = false;

    try {
      if (exchange.getRequestMethod().toLowerCase(Locale.ROOT).equals("post")) {
        StringBuilder urlPath = new StringBuilder(exchange.getRequestURI().toString().substring(6));
        String username;
        String generations;
        if (urlPath.toString().contains("/")) {
          username = urlPath.substring(0, urlPath.indexOf("/"));
          generations = urlPath.substring(urlPath.indexOf("/") + 1);
        } else {
          username = urlPath.toString();
          generations = "4";
        }
        int gen = Integer.parseInt(generations);

        Gson gson = new Gson();
        FillRequest request = new FillRequest(username, gen);

        FillService service = new FillService();
        FillResult result = service.fill(request);
        success = result.isSuccess();

        if (!success) {
          exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
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
}
