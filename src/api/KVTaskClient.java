package api;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    private final String url;
    private final HttpClient httpClient;
    private String apiToken;


    public KVTaskClient(int kVServerPort) {
        this.url = "http://localhost:" + kVServerPort + "/";
        this.httpClient = HttpClient.newHttpClient();
        this.apiToken = getApiToken();
    }

    private String getApiToken() {

        URI urlFinal = URI.create(url + "register");
        HttpRequest request = HttpRequest.newBuilder().uri(urlFinal).GET().build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return response.body();
            }

        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            System.out.println("Во время выполнения запроса getApiToken возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
        return apiToken = "-1";
    }

    public void put(String key, String json) {

        URI urlFinal = URI.create(url + "save/" + key + "?API_TOKEN=" + apiToken);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(urlFinal).POST(body).build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Код ответа: " + response.statusCode());

        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            System.out.println("Во время выполнения запроса save возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }

    public String load(String key) {

        URI urlFinal = URI.create(url + "load/" + key + "?API_TOKEN=" + apiToken);
        HttpRequest request = HttpRequest.newBuilder().uri(urlFinal).GET().build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                return response.body();

            } else if (response.statusCode() == 404) {
                return "";

            } else {
                System.out.println("Код ответа: " + response.statusCode());
                return "";
            }

        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            System.out.println("Во время выполнения запроса load возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку. Объяснение: " + e.getMessage());
        }
        return "";
    }

}
