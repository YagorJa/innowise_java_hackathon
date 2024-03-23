package com.example.bot.controller;



import com.example.bot.service.CryptoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/crypto")
public class CryptoController {

    @Autowired
    private CryptoService cryptoService;

    @GetMapping("/prices")
    public ResponseEntity<Map<String, Double>> getCryptoPrices() {
        Map<String, Double> prices = cryptoService.getCryptoPrices();
        return new ResponseEntity<>(prices, HttpStatus.OK);
    }
}

