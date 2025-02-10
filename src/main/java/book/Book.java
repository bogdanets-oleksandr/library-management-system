package book;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.text.StringSubstitutor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class Book {
    private static final String templateURL = "https://openlibrary.org/api/books?bibkeys=ISBN:${ISBN}&format=json&jscmd=data";
    private int id;
    private String isbn;
    private String title;

    private List<String> authors;
    private int yearPublished;

    public Book(String isbn) {

        isbn = formatISBN(isbn);
        HashMap<String, String> map = new HashMap<>();
        map.put("ISBN", isbn);
        StringSubstitutor sub = new StringSubstitutor(map);

        String url = sub.replace(templateURL);

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                this.isbn = isbn;
                authors = new ArrayList<>();
                String body = response.body().string();
                System.out.println(body);
                ObjectMapper mapper = new ObjectMapper();
                JsonNode node = mapper.readTree(body).get("ISBN:" + isbn);
                title = node.get("title").asText();
                yearPublished = node.get("publish_date").asInt();
                var authorList = node.get("authors").findValues("name");
                for (JsonNode n : authorList) {
                    authors.add(n.asText());
                }
                System.out.println("Book: " + this + " was found");
            } else {
                System.out.println("Couldn't find the book. Try again later or enter the information manually");
                getManualInfo();
            }
        } catch (IOException e) {
            System.out.println("Something went wrong, try again or enter the information manually");
            getManualInfo();
        }
    }

    public String getIsbn() {
        return isbn;
    }
    public int getId() {
        return id;
    }

    private void getManualInfo() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the title:");
        title = scanner.next();
        System.out.println("Enter the author(s) separated by comma:");
        for (String a : scanner.next().split(",")) {
            authors.add(a.trim());
        }
        System.out.println("Enter publish year:");
        yearPublished = scanner.nextInt();
        System.out.println("Is everything correct? (Yes or No)");
        System.out.println(this);
        if (scanner.next().charAt(0) == 'N' || scanner.next().charAt(0) == 'n') {
            getManualInfo();
        }
    }

    private String formatISBN(String isbn) {

        return RegExUtils.replaceAll(isbn, "[^0-9]", "");
    }

    @Override
    public String toString() {
        return "Book{" +
                "isbn='" + isbn + '\'' +
                ", title='" + title + '\'' +
                ", authors=" + authors +
                ", yearPublished=" + yearPublished +
                '}';
    }
}
