package gaply;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import spark.ModelAndView;
import spark.template.velocity.VelocityTemplateEngine;

import static spark.Spark.*;

public class GaplyBackend {

    private static final VelocityTemplateEngine velocityTemplateEngine = new VelocityTemplateEngine();

    public static void main(String[] args) {
        port(8080);

        staticFiles.location("/public");

        get("/", (req, res) -> {
            return new ModelAndView(null, "index.vm");
        }, velocityTemplateEngine);

        post("/identify-gaps", (req, res) -> {
            String query = req.queryParams("message");
            List<String> gaps = findMarketGaps(query);

            // Using a HashMap instead of an anonymous object to store data
            HashMap<String, Object> model = new HashMap<>();
            model.put("gaps", gaps);

            return new ModelAndView(model, "results.vm");
        }, velocityTemplateEngine);
    }

    private static List<String> findMarketGaps(String query) {
        List<String> gaps = new ArrayList<>();
        try {
            String url = getTargetUrl(query);
            Document doc = Jsoup.connect(url).get();
            Elements elements = doc.select(".data-item");

            elements.forEach(element -> {
                String gap = element.text();
                gaps.add(gap);
            });
        } catch (IOException e) {
            e.printStackTrace();
            gaps.add("Error fetching data. Please try again later.");
        }
        return gaps;
    }

    private static String getTargetUrl(String query) {
        if (query.toLowerCase().contains("video production")) {
            return "https://www.statista.com/topics/841/media-production/";
        } else if (query.toLowerCase().contains("gaming")) {
            return "https://igda.org/";
        } else if (query.toLowerCase().contains("content creation")) {
            return "https://contentmarketinginstitute.com/";
        } else {
            return "https://example.com/search?q=" + query;
        }
    }
}



