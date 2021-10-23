package tootymc;

import java.net.URI;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Logger;
import java.io.InputStreamReader;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import org.json.simple.parser.ParseException;

public class TestHandler implements HttpHandler {
    private Logger logger;

    public TestHandler(Tooty plugin) {
        logger = plugin.getServer().getLogger();
    }

    public void handle(HttpExchange t) throws IOException {
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = null;
        try {
            jsonObject = (JSONObject) jsonParser.parse(
                    new InputStreamReader(t.getRequestBody(), "UTF-8"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        logger.info(jsonObject.get("message").toString());
        String response = "METHOD TYPE NOT ALLOWED";
        int code = 405;
        if (t.getRequestMethod().toUpperCase() == "POST") {
            response = "OK";
            code = 200;
        }
        t.sendResponseHeaders(code, response.getBytes().length);
        OutputStream os = t.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}
