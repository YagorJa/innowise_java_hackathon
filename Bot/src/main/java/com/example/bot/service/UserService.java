package com.example.bot.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {

    private final Map<Long, Double> userThresholds = new HashMap<>();


    public void setThreshold(long userId, double threshold) {
        userThresholds.put(userId, threshold);
    }


    public Double getThreshold(long userId) {
        return userThresholds.getOrDefault(userId, 0.0);
    }
}
