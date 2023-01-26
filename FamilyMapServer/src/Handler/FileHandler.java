package Handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.nio.file.Files;
import java.util.Locale;

public class FileHandler implements HttpHandler {
  @Override
  public void handle(HttpExchange exchange) throws IOException {
    try {
      if (exchange.getRequestMethod().toLowerCase(Locale.ROOT).equals("get")) {
        String urlPath = exchange.getRequestURI().toString();
        if (urlPath.equals(null) || urlPath.equals("/")) {
          urlPath = "/index.html";
        }

        String filePath = "web" + urlPath;
        File file = new File(filePath);
        if (file.exists()) {
          exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
          OutputStream respBody = exchange.getResponseBody();
          Files.copy(file.toPath(), respBody);
          respBody.close();
        } else {
          exchange.sendResponseHeaders(HttpURLConnection.HTTP_NOT_FOUND, 0);
          file = new File("web/HTML/404.html");
          OutputStream respBody = exchange.getResponseBody();
          Files.copy(file.toPath(), respBody);
          respBody.close();
        }
      }
    } catch (IOException e) {
      exchange.sendResponseHeaders(HttpURLConnection.HTTP_SERVER_ERROR, 0);
      exchange.getResponseBody().close();
      e.printStackTrace();
    }
  }
}
