package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Extractor {

    public static List<String> extractEmails(String sourceCode) {
        List<String> emails = new ArrayList<>();
        String regex =  "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
                + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(sourceCode);

        while (matcher.find()) {
            emails.add(matcher.group());
        }

        return emails;
    }

    public static List<String> extractNumbers(String sourceCode) {
        List<String> phoneNumbers = new ArrayList<>();
        // Regex para números de telefone nos formatos (123) 456-7890 e 987-654-3210
        String regex = "\\b\\(?\\d{3}\\)?[-.\\s]?\\d{3}[-.\\s]?\\d{4}\\b";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(sourceCode);

        while (matcher.find()) {
            phoneNumbers.add(matcher.group());
        }

        return phoneNumbers;
    }


    public static List<String> extractUrls(String sourceCode) {
        List<String> urlsNotClean = new ArrayList<>();
        // Regex para números de telefone nos formatos (123) 456-7890 e 987-654-3210
        String regex = "https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(sourceCode);

        while (matcher.find()) {
            urlsNotClean.add(matcher.group());
        }
        List<String> urls = new ArrayList<>();
        for (String urlNotClean : urlsNotClean ) {
            String url = urlNotClean;
            if (url.contains(".jpg") || url.contains(".png") || url.contains(".svg") || url.contains(".css") || url.contains(".js"))
                continue;
            if (url.contains("?")) {
                url = url.split("\\?")[0];
            }
            if (url.endsWith("/")) {
                url = url.substring(0, url.length() - 1);
            }
            urls.add(url);
        }
        return urls;
    }

}
