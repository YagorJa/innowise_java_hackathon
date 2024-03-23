package com.example.bot.service;

import org.springframework.stereotype.Component;
import com.example.bot.config.BotConfig;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.Map;

@Component
@Slf4j
public class TelegramBot extends TelegramLongPollingBot {

    private final BotConfig botConfig;
    private final CryptoService cryptoService;

    public TelegramBot(BotConfig botConfig, CryptoService cryptoService) {
        this.botConfig = botConfig;
        this.cryptoService = cryptoService;
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            switch (messageText.toLowerCase()) {
                case "/start":
                    sendWelcomeMessage(chatId, update.getMessage().getChat().getFirstName());
                    break;
                case "/current_rate":
                    sendCurrentRates(chatId);
                    handleSetThresholdCommand(chatId);
                    break;
                default:
                    if (isThresholdSelection(messageText)) {
                        handleThresholdSelection(chatId, messageText);
                    } else {
                        sendMessage(chatId, "Прости, я не понимаю эту команду.");
                    }
            }
        }
    }

    private boolean isThresholdSelection(String messageText) {

        return messageText.contains("3%") || messageText.contains("5%") || messageText.contains("10%") || messageText.contains("15%");
    }

    private void handleThresholdSelection(long chatId, String threshold) {

        sendMessage(chatId, "Вы выбрали порог изменения курса: " + threshold);
        switch (threshold) {
            case "3%":

                sendMessage(chatId, "Вы выбрали порог 3%. Ваши действия для этого порога...");
                break;
            case "5%":

                sendMessage(chatId, "Вы выбрали порог 5%. Ваши действия для этого порога...");
                break;
            case "10%":

                sendMessage(chatId, "Вы выбрали порог 10%. Ваши действия для этого порога...");
                break;
            case "15%":

                sendMessage(chatId, "Вы выбрали порог 15%. Ваши действия для этого порога...");
                break;
            default:
                break;
        }
    }

    void sendWelcomeMessage(long chatId, String name) {
        String welcomeMessage = "Здарова, " + name + "! Я бот для отслеживания курсов криптовалют.";
        sendMessage(chatId, welcomeMessage);

    }

    private void sendCurrentRates(long chatId) {
        Map<String, Double> cryptoPrices = cryptoService.getCryptoPrices();
        StringBuilder message = new StringBuilder("Текущие курсы криптовалют:\n");
        cryptoPrices.forEach((symbol, price) -> message.append(symbol).append(": ").append(price).append("\n"));
        sendMessage(chatId, message.toString());
    }

    void sendMessage(long chatId, String textToSend) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(textToSend);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Ошибка при отправке сообщения: {}", e.getMessage());
        }
    }

    private void handleSetThresholdCommand(long chatId) {

        ReplyKeyboardMarkup replyMarkup = new ReplyKeyboardMarkup();
        replyMarkup.setSelective(true);
        replyMarkup.setResizeKeyboard(true);
        replyMarkup.setOneTimeKeyboard(true);


        KeyboardRow row1 = new KeyboardRow();
        row1.add("3%");
        row1.add("5%");

        KeyboardRow row2 = new KeyboardRow();
        row2.add("10%");
        row2.add("15%");


        replyMarkup.getKeyboard().add(row1);
        replyMarkup.getKeyboard().add(row2);


        String messageText = "Выберите порог изменения курса валюты:";
        sendMessageWithKeyboard(chatId, messageText, replyMarkup);
    }


    private void sendMessageWithKeyboard(long chatId, String textToSend, ReplyKeyboardMarkup keyboardMarkup) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(textToSend);
        sendMessage.setReplyMarkup(keyboardMarkup);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Ошибка при отправке сообщения: {}", e.getMessage());
        }
    }
}