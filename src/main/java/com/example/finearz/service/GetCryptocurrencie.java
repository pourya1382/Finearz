package com.example.finearz.service;

import com.example.finearz.model.Cryptocurrencie;
import com.example.finearz.repository.CryptocurrencieRepository;
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
public class GetCryptocurrencie extends Thread {
    @Override
    public void run() {
        while (true) {
            if (cryStatus){
                try {
                    getCryptocurrencieIRT();
                    getCryptocurrencieUSDT();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
            try {
                Thread.sleep(20000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    CryptocurrencieRepository repository;
    List<Cryptocurrencie> crys = new ArrayList<>();
    Cryptocurrencie cryptocurrencie;
    Boolean cryStatus = false;
    int giveIt = 0;

    public GetCryptocurrencie(CryptocurrencieRepository repository) {
        this.repository = repository;
    }

    @Bean
    public void getCryptocurrencieIRT() throws IOException, JSONException {
        URL url = new URL("https://api.finearz.net/api/v1/market/?fiat=IRT");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("accept", "application/json");
        InputStream responseStream = connection.getInputStream();
        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(responseStream, "utf-8"))) {
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine);
            }
            String reasonString = response.toString();
            getSymbols(getCrypto(reasonString));
            getMarketInfoIRT(reasonString);
        }
    }

    @Bean
    public void getCryptocurrencieUSDT() throws IOException, JSONException {
        StringBuilder responseUSDT = new StringBuilder();
        URL url = new URL("https://api.finearz.net/api/v1/market/?fiat=USDT");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("accept", "application/json");
        InputStream responseStream = connection.getInputStream();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(responseStream, "utf-8"))) {
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                responseUSDT.append(responseLine);
            }

        }
        String reasonString = responseUSDT.toString();
        getMarketInfoUSDT(reasonString);

        if (giveIt == 0) {
            crys = repository.saveAll(crys);
            giveIt++;
        }
    }

    public static String getCrypto(String responseBody) throws JSONException {
        JSONArray albums = new JSONArray(responseBody);
        String symbol = null;
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

    public void getSymbols(String responseBody) throws JSONException {
        String symbol = null;
        String name = null;
        JSONArray forsymbols = new JSONArray(responseBody);
        for (int i = 0; i < forsymbols.length(); i++) {
            JSONObject forsymbol = forsymbols.getJSONObject(i);
            Cryptocurrencie cryptocurrencie = new Cryptocurrencie();
            symbol = forsymbol.getString("symbol");
            cryptocurrencie.setSymbol(symbol);
            name = forsymbol.getString("name");
            cryptocurrencie.setName(name);
            crys.add(cryptocurrencie);
        }

    }

    public void getMarketInfoIRT(String responseBody) throws JSONException {
        JSONArray albums = new JSONArray(responseBody);
        String value = null;
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
        JSONArray forLastPrices = new JSONArray(values);
        for (int i = 0; i < forLastPrices.length(); i++) {
            JSONObject forLastPrice = forLastPrices.getJSONObject(i);
            float newPrice = Float.valueOf(forLastPrice.getString("lastPrice"));
            if (giveIt == 0) {
                crys.get(i).setPriceIRT(Float.valueOf(forLastPrice.getString("lastPrice")));
                crys.get(i).setLastDayChange(Float.valueOf(forLastPrice.getString("lastDayChange")));
            } else if (Float.valueOf(forLastPrice.getString("lastDayChange")) >= 2) {
                cryptocurrencie = repository.findById((long) (i + 1)).get();
                cryptocurrencie.setLastDayChange(Float.valueOf(forLastPrice.getString("lastDayChange")));
                cryptocurrencie.setPriceIRT(Float.valueOf(forLastPrice.getString("lastPrice")));
                repository.save(cryptocurrencie);
                cryStatus = true;

            }
        }
    }

    public void getMarketInfoUSDT(String responseBody) throws JSONException {
        int j = 0;
        JSONArray albums = new JSONArray(responseBody);
        String value = null;
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
        JSONArray forLastPricesAndChange = new JSONArray(values);
        for (int i = 0; i < forLastPricesAndChange.length(); i++) {
            JSONObject forLastPrice = forLastPricesAndChange.getJSONObject(i);
            if (giveIt == 0) {
                if (j == 8) {
                    j++;
                    i--;
                    continue;
                }
                crys.get(j).setPriceUSDT(Float.valueOf(forLastPrice.getString("lastPrice")));
                j++;
            } else if (cryStatus && i!=8) {
                 repository.findById((long) (i+1)).get().setPriceUSDT(Float.valueOf(forLastPrice.getString("lastPrice")));
            }
        }
    }
}


