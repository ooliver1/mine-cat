package tootymc;

import java.net.URI;
import java.util.Map;
import java.io.IOException;
import java.net.http.HttpClient;
import java.util.logging.Logger;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient.Version;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpResponse.BodyHandlers;
import java.net.http.HttpRequest.BodyPublishers;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class HTTPClient {

    private HttpClient client;
    private Logger logger;

    public HTTPClient(Tooty plugin) {
        client = HttpClient
                .newBuilder()
                .version(Version.HTTP_2)
                .followRedirects(Redirect.ALWAYS)
                .build();
        logger = plugin.getServer().getLogger();
    }

    public void postJson(String uri, Map<String, String> map) {
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = null;
        try {
            requestBody = objectMapper
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(map);
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.info(String.format("POST: %s", requestBody));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(
                        String.format(
                                "http://65.21.247.55:6969/%s",
                                uri.toString())))
                .header("Content-Type", "application/json")
                .POST(BodyPublishers.ofString(requestBody))
                .build();
        client.sendAsync(request, BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(System.out::println).join();
    }
}
