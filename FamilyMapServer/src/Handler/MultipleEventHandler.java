package Handler;

import Result.MultipleEventResult;
import Service.MultipleEventService;
import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.util.Locale;

public class MultipleEventHandler implements HttpHandler {
  @Override
  public void handle(HttpExchange exchange) throws IOException {
    boolean success = false;

    try {
      if (exchange.getRequestMethod().toLowerCase(Locale.ROOT).equals("get")) {
        Headers reqHeaders = exchange.getRequestHeaders();

        if (reqHeaders.containsKey("Authorization")) {
          String authToken = reqHeaders.getFirst("Authorization");

          Gson gson = new Gson();

          MultipleEventService service = new MultipleEventService(authToken);
          MultipleEventResult result = service.eventService();
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
      }

    } catch (IOException e) {
      exchange.sendResponseHeaders(HttpURLConnection.HTTP_SERVER_ERROR, 0);
      exchange.getResponseBody().close();

      e.printStackTrace();
    }
  }
}
