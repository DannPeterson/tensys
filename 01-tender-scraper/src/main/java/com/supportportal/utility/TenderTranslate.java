package com.supportportal.utility;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.supportportal.domain.CpvDb;
import com.supportportal.domain.TenderDb;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
public class TenderTranslate {
    @Value("${GOOGLE_API_KEY}")
    private String apiKey;
    private String target;

    public TenderTranslate() {
    }

    public TenderDb translate(TenderDb tenderDb, String target) {
        this.target = target;
        TenderDb result = cloneTender(tenderDb);
        translateTender(result);
        translateCpvs(result.getCpvDb());
        return result;
    }

    private void translateTender(TenderDb tenderDb) {
        tenderDb.setTitle(translateText(tenderDb.getTitle()));
        tenderDb.setDescription(translateText(tenderDb.getDescription()));
        tenderDb.setField(translateText(tenderDb.getField()));
    }

    private void translateCpvs(List<CpvDb> cpvDbs) {
        for(CpvDb cpvDb : cpvDbs) {
            cpvDb.setDescription(translateText(cpvDb.getDescription()));
        }
    }

    private String translateText(String text) {
        StringBuilder result = new StringBuilder();
        try {
            String encodedText = URLEncoder.encode(text, "UTF-8");
            String urlStr = "https://translation.googleapis.com/language/translate/v2?target=" + target + "&key=" + apiKey + "&q=" + encodedText;

            URL url = new URL(urlStr);

            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            InputStream stream;
            if (conn.getResponseCode() == 200) //success
            {
                stream = conn.getInputStream();
            } else
                stream = conn.getErrorStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            JsonParser parser = new JsonParser();

            JsonElement element = parser.parse(result.toString());

            if (element.isJsonObject()) {
                JsonObject obj = element.getAsJsonObject();
                if (obj.get("error") == null) {
                    String translatedText = obj.get("data").getAsJsonObject().
                            get("translations").getAsJsonArray().
                            get(0).getAsJsonObject().
                            get("translatedText").getAsString();
                    return translatedText;
                }
            }

            if (conn.getResponseCode() != 200) {
                System.err.println(result);
            }

        } catch (IOException | JsonSyntaxException ex) {
            System.err.println(ex.getMessage());
        }
        return null;
    }

    public String getLanguage(String text) {
        StringBuilder result = new StringBuilder();
        try {
            String encodedText = URLEncoder.encode(text, "UTF-8");
            String urlStr = "https://translation.googleapis.com/language/translate/v2?target=ru&key=" + apiKey + "&q&q=" + encodedText;

            URL url = new URL(urlStr);

            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            InputStream stream;
            if (conn.getResponseCode() == 200) //success
            {
                stream = conn.getInputStream();
            } else
                stream = conn.getErrorStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            JsonParser parser = new JsonParser();

            JsonElement element = parser.parse(result.toString());

            if (element.isJsonObject()) {
                JsonObject obj = element.getAsJsonObject();

                if (obj.get("error") == null) {
                    String language = obj.get("data").getAsJsonObject().
                            get("translations").getAsJsonArray().
                            get(1).getAsJsonObject().
                            get("detectedSourceLanguage").getAsString();
                    return language;
                }
            }

            if (conn.getResponseCode() != 200) {
                System.err.println(result);
            }

        } catch (IOException | JsonSyntaxException ex) {
            System.err.println(ex.getMessage());
        }
        return null;
    }

    public String translateText(String target, String text) {
        StringBuilder result = new StringBuilder();
        try {
            String encodedText = URLEncoder.encode(text, "UTF-8");
            String urlStr = "https://translation.googleapis.com/language/translate/v2?target=" + target + "&key=" + apiKey + "&q=" + encodedText;

            URL url = new URL(urlStr);

            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            InputStream stream;
            if (conn.getResponseCode() == 200) //success
            {
                stream = conn.getInputStream();
            } else
                stream = conn.getErrorStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            JsonParser parser = new JsonParser();

            JsonElement element = parser.parse(result.toString());

            if (element.isJsonObject()) {
                JsonObject obj = element.getAsJsonObject();

                if (obj.get("error") == null) {
                    String translatedText = obj.get("data").getAsJsonObject().
                            get("translations").getAsJsonArray().
                            get(0).getAsJsonObject().
                            get("translatedText").getAsString();
                    return translatedText;
                }
            }

            if (conn.getResponseCode() != 200) {
                System.err.println(result);
            }

        } catch (IOException | JsonSyntaxException ex) {
            System.err.println(ex.getMessage());
        }
        return null;
    }

    private TenderDb cloneTender(TenderDb tenderDb) {
        TenderDb cloned = new TenderDb();
        cloned.setId(tenderDb.getId());
        cloned.setSource(tenderDb.getSource());
        cloned.setSourceRefNumber(tenderDb.getSourceRefNumber());
        cloned.setLink(tenderDb.getLink());
        cloned.setTitle(tenderDb.getTitle());
        cloned.setDescription(tenderDb.getDescription());
        cloned.setField(tenderDb.getField());
        cloned.setClient(tenderDb.getClient());
        cloned.setDate(tenderDb.getDate());
        cloned.setDeadline(tenderDb.getDeadline());
        cloned.setCpvDb(cloneCpvs(tenderDb.getCpvDb()));
        return cloned;
    }

    private List<CpvDb> cloneCpvs(List<CpvDb> cpvDbs) {
        List<CpvDb> result = new ArrayList<>();
        for(CpvDb cpvDb : cpvDbs) {
            CpvDb cloned = new CpvDb();
            cloned.setId(cpvDb.getId());
            cloned.setCode(cpvDb.getCode());
            cloned.setDescription(cpvDb.getDescription());
            result.add(cloned);
        }
        return result;
    }
}