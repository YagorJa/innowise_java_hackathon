package com.example.bot.scheduler;

import com.example.bot.service.CryptoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class UpdateScheduler {

    private static final Logger logger = LoggerFactory.getLogger(UpdateScheduler.class);

    private final CryptoService cryptoService;

    @Autowired
    public UpdateScheduler(CryptoService cryptoService) {
        this.cryptoService = cryptoService;
    }

    @Scheduled(fixedRate = 20000)
    public void updateCryptoPrices(long chatID, String name) {
        try {
            cryptoService.updateCryptoPrices(chatID);
            logger.info("Курсы криптовалют успешно обновлены.");
        } catch (Exception e) {
            logger.error("Ошибка при обновлении курсов криптовалют: {}", e.getMessage());
        }
    }
}

