package com.example.bot.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class CryptoService {

    private final String API_URL = "https://api.mexc.com/api/v3/ticker/price";

    @Autowired
    private TelegramBot telegramBot;

    private Map<String, Double> previousPrices;

    public void updateCryptoPrices(long chatId) {
        Map<String, Double> currentPrices = getCryptoPrices();
        if (currentPrices.isEmpty()) {
            log.error("Failed to update crypto prices: empty response.");
            return;
        }

        // Отправляем приветственное сообщение перед отправкой уведомлений о курсе
        telegramBot.sendWelcomeMessage(chatId, telegramBot.getBotUsername());

        if (previousPrices != null) {
            for (Map.Entry<String, Double> entry : currentPrices.entrySet()) {
                String symbol = entry.getKey();
                double currentPrice = entry.getValue();
                double previousPrice = previousPrices.getOrDefault(symbol, 0.0);
                double priceDifference = currentPrice - previousPrice;

                if (Math.abs(priceDifference) > 0.0) {
                    String message = String.format("Курс %s изменился на %.2f", symbol, priceDifference);
                    telegramBot.sendMessage(chatId, message);
                }
            }
        }

        previousPrices = currentPrices;
    }

    public Map<String, Double> getCryptoPrices() {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(API_URL);
            HttpResponse response = httpClient.execute(request);
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            return parseCryptoPrices(result.toString());
        } catch (IOException e) {
            log.error("Error while fetching crypto prices: {}", e.getMessage());
            return new HashMap<>();
        }
    }

    private Map<String, Double> parseCryptoPrices(String json) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(json);
            Map<String, Double> prices = new HashMap<>();
            for (JsonNode node : rootNode) {
                String symbol = node.get("symbol").asText();
                double price = node.get("price").asDouble();
                prices.put(symbol, price);
            }
            return prices;
        } catch (IOException e) {
            log.error("Error while parsing crypto prices: {}", e.getMessage());
            return new HashMap<>();
        }
    }
}
