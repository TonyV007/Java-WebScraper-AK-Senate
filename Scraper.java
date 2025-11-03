package org.example; // This should be at the top

// Import the libraries we need
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class Scraper {

    public static void main(String[] args) {

        final String url = "https://akleg.gov/senate.php";

        try {
            // 1. Connect to the website and get the document
            Document doc = Jsoup.connect(url).get();

            // 2. Find all the senator <li> elements
            // We use the selector you found: "ul.item > li"
            // This selector says: "First find 'ul.people-holder', THEN find the 'ul.item > li' inside it."
            Elements senatorItems = doc.select("div#list-view ul.people-holder ul.item > li");

            System.out.println("Found " + senatorItems.size() + " senators.");

            // 3. Loop through each senator item
            for (Element senatorItem : senatorItems) {
                // Let's just get the name for now to test it
                String name = senatorItem.select("strong.name").text();
                System.out.println(name);
            }

        } catch (IOException e) {
            System.out.println("Something went wrong connecting to the URL.");
            e.printStackTrace();
        }
    }
}