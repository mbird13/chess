import com.google.gson.Gson;
import exception.ResponseException;
import servicehelpers.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class ServerFacade {

  private final String serverUrl;

  public ServerFacade(String url) {
    serverUrl = url;
  }

  public LoginResult register(RegisterRequest request) throws ResponseException {
    return makeRequest("POST", "/user", request, LoginResult.class);
  }

  private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws ResponseException {
    try {
      URL url = (new URI(serverUrl + path)).toURL();
      HttpURLConnection http =(HttpURLConnection) url.openConnection();
      http.setRequestMethod(method);
      http.setDoOutput(true);

      writeBody(request, http);
      http.connect();
      throwIfNotSuccessful(http);
      return readBody(http, responseClass);
    } catch (Exception ex) {
      throw new ResponseException(500, ex.getMessage());
    }
  }

  private <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
    T response = null;
    if (http.getContentLength() < 0) {
      try (InputStream respBody=http.getInputStream()) {
        InputStreamReader reader=new InputStreamReader(respBody);
        if (responseClass != null) {
          response=new Gson().fromJson(reader, responseClass);
        }
      }
    }
    return response;
  }

  private void writeBody(Object request, HttpURLConnection http) throws IOException {
    if (request != null) {
      http.addRequestProperty("Content-type", "application/json");
      String reqData = new Gson().toJson(request);
      try(OutputStream reqBody  = http.getOutputStream()) {
        reqBody.write(reqData.getBytes());
      }
    }
  }

  private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
    var status = http.getResponseCode();
    if (status != 200) {
      throw new ResponseException(status, "failure: " + status);
    }
  }
}
