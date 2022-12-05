package com.example.finearz.service;

import com.example.finearz.model.Cryptocurrencie;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class GetCryptocurrencie {
    List<Cryptocurrencie> cryptocurrencies = new ArrayList<>();
    @Bean
    public void GetCryptocurrencie() throws IOException, JSONException {
        URL url = new URL("https://api.finearz.net/api/v1/market/?");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("accept", "application/json");
        InputStream responseStream = connection.getInputStream();
        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(responseStream, "utf-8"))) {
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine);
            }


        }
        String reasonString = response.toString();
        getSymbols(reasonString);
        getMarketInfo(reasonString);

    }
    public static String getCrypto(String responseBody)throws JSONException{
        JSONArray albums = new JSONArray(responseBody);
        String symbol = null;
        String name = null;
        String values = null;
        for (int i = 0; i < albums.length(); i++) {
            JSONObject album = albums.getJSONObject(i);
            symbol = album.getString("market");
            if (i == 0) {
                values = "[" + symbol + ",";
            } else if (i == albums.length() - 1) {
                values = values + symbol + "]";
            } else {
                values = values + symbol + ",";
            }
        }
        JSONArray forCryptos = new JSONArray(values);
        for (int i = 0; i < forCryptos.length(); i++) {
            JSONObject forCrypto = forCryptos.getJSONObject(i);
            symbol = forCrypto.getString("crypto");
            if (i == 0) {
                values = "[" + symbol + ",";
            } else if (i == albums.length() - 1) {
                values = values + symbol + "]";
            } else {
                values = values + symbol + ",";
            }
        }
        return values;
    }
    public static List<String> getSymbols(String responseBody) throws JSONException {
        String symbol = null;
        String name = null;
        List<String> symbols = new ArrayList<>();
        List<String> names = new ArrayList<>();
        JSONArray forsymbols = new JSONArray(getCrypto(responseBody));
        for (int i = 0; i < forsymbols.length(); i++) {
            JSONObject forsymbol = forsymbols.getJSONObject(i);
            symbol = forsymbol.getString("symbol");
            name = forsymbol.getString("name");
            symbols.add(symbol);
            names.add(name);
        }
        System.out.println("values: "+ symbols);
        System.out.println("names :" + names);
        return symbols;
    }
    public static List<String> getMarketInfo(String responseBody) throws JSONException{
        JSONArray albums = new JSONArray(responseBody);
        String value = null;
        List<Float> prices = new ArrayList<>();
        String values = null;
        for (int i = 0; i < albums.length(); i++) {
            JSONObject album = albums.getJSONObject(i);
            value = album.getString("marketInfo");
            if (i == 0) {
                values = "[" + value + ",";
            } else if (i == albums.length() - 1) {
                values = values + value + "]";
            } else {
                values = values + value + ",";
            }
        }
        System.out.println(values);

        JSONArray forLastPrices = new JSONArray(values);
        for (int i = 0; i < forLastPrices.length(); i++) {
            JSONObject forLastPrice = forLastPrices.getJSONObject(i);
            prices.add(Float.valueOf(forLastPrice.getString("lastPrice")));
        }
        System.out.println(prices);
        return null;
    }

}


