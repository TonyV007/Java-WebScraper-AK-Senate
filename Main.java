package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        List<Senator> senators = new ArrayList<>();
        // This is the correct URL from your first prompt
        final String mainUrl = "https://akleg.gov/senate.php";

        try {
            // 1. Connect to the main page
            Document doc = Jsoup.connect(mainUrl).get();

            // 2. Find the correct list of senators (the 2nd one on the page)
            Elements listContainers = doc.select("ul.people-holder");
            Element mainListContainer = listContainers.get(1); // Get the 2nd list
            Elements senatorItems = mainListContainer.select("ul.item > li");

            System.out.println("Found " + senatorItems.size() + " senators. Processing...");

            for (Element senatorItem : senatorItems) {

                Senator senator = new Senator();

                // --- 3. GET DATA FROM MAIN LIST PAGE ---
                senator.name = senatorItem.select("strong.name").text();
                senator.url = senatorItem.select("a").attr("href");
                senator.party = senatorItem.select("dt:contains(Party) + dd").text();
                senator.phone = senatorItem.select("dt:contains(Phone) + dd").text();

                // --- 4. GO TO THE DETAIL PAGE FOR MORE DATA ---
                System.out.println("Visiting detail page for: " + senator.name);
                try {
                    Document detailPage = Jsoup.connect(senator.url).get();

                    // --- FINAL, CORRECT SELECTORS ---

                    // Find the Title (e.g., "Senator") - This worked before
                    Element titleElement = detailPage.selectFirst("span.formal_name");
                    if (titleElement != null) {
                        String fullTitle = titleElement.text();
                        if (fullTitle != null && !fullTitle.isEmpty()) {
                            senator.title = fullTitle.split(" ")[0]; // Gets first word
                        }
                    }

                    // Find the Position (e.g., "Majority Leader")
                    Element positionElement = detailPage.selectFirst("h1.member-name span.position");
                    if (positionElement != null) {
                        senator.position = positionElement.text();
                    }

                    // Find the main contact block on the right
                    Element bioRight = detailPage.selectFirst("div.bioright");
                    if (bioRight != null) {
                        // Find the Email (from <div class="bioright">)
                        Element emailElement = bioRight.selectFirst("a[href^=mailto]");
                        if (emailElement != null) {
                            senator.email = emailElement.attr("href").replace("mailto:", "");
                        }

                        // Find the Address (from <div class="bioright">) - This worked before
                        Element addressElement = bioRight.selectFirst("div:first-of-type");
                        if (addressElement != null) {
                            senator.address = addressElement.text();
                        }
                    }

                } catch (IOException e) {
                    System.out.println("Error visiting detail page for " + senator.name + ": " + e.getMessage());
                }

                senators.add(senator);
            }

            System.out.println("\n--- All scraping complete ---");

            // --- 5. EXPORT TO JSON FILE ---
            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .serializeNulls() // This keeps all fields in the JSON
                    .create();

            try (FileWriter writer = new FileWriter("senators.json")) {
                gson.toJson(senators, writer);
                System.out.println("Successfully created senators.json file!");
            } catch (IOException e) {
                System.out.println("Error writing JSON file: " + e.getMessage());
            }

        } catch (IOException e) {
            System.out.println("Something went wrong connecting to the MAIN URL.");
            e.printStackTrace();
        }
    }
}