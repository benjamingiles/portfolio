package Handler;

import Request.SingleEventRequest;
import Result.SingleEventResult;
import Service.SingleEventService;
import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.AuthTokenDAO;
import dao.DataAccessException;
import dao.Database;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.sql.Connection;
import java.util.Locale;

public class SingleEventHandler implements HttpHandler {
  @Override
  public void handle(HttpExchange exchange) throws IOException {
    boolean success = false;

    try {
      if (exchange.getRequestMethod().toLowerCase(Locale.ROOT).equals("get")) {
        Headers reqHeaders = exchange.getRequestHeaders();

        if (reqHeaders.containsKey("Authorization")) {
          String authToken = reqHeaders.getFirst("Authorization");

          String eventID = exchange.getRequestURI().toString().substring(7);

          Gson gson = new Gson();

          SingleEventRequest request = new SingleEventRequest(eventID);
          SingleEventService service = new SingleEventService(authToken);
          SingleEventResult result = service.singleEvent(request);
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
