package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Extractor {

    public static List<String> extractEmails(String sourceCode) {
        List<String> emails = new ArrayList<>();
        //https://regex101.com/r/vO2aL0/1

        String regex = "\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\\b";
        Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(sourceCode);

        while (matcher.find()) {
            emails.add(matcher.group(0));
        }

        return emails;
    }

    public static List<String> extractNumbers(String sourceCode) {

        // https://www.portugal-a-programar.pt/forums/topic/51048-express%C3%A3o-regular-para-valida%C3%A7%C3%A3o-de-n%C3%BAmeros-de-telefone/
        //Este Regex Aceita numeros Nacionais Portugueses+´´´´´´´´´´´´´´´´´´´´´´´´´´´´´´´´´´´´
        List<String> phoneNumbers = new ArrayList<>();
        final String regex = "9[1236][0-9]{7}|2[1-9][0-9]{7}";
        final Pattern pattern = Pattern.compile(regex);
        final Matcher matcher = pattern.matcher(sourceCode);

        while (matcher.find()) {
            phoneNumbers.add(matcher.group(0));

        }

        return phoneNumbers;
    }


    public static List<String> extractUrls(String sourceCode) {
        List<String> urlsNotClean = new ArrayList<>();
        // https://stackoverflow.com/questions/3809401/what-is-a-good-regular-expression-to-match-a-url
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
