package com.example.tradingapp.controller;

import com.example.tradingapp.service.KiteConnectService;
import com.example.tradingapp.service.StrategyService;
import com.example.tradingapp.service.TradingEngine;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/trading")
public class TradingController {

    private final KiteConnectService kiteConnectService;
    private final StrategyService strategyService;
    private final TradingEngine tradingEngine;

    public TradingController(KiteConnectService kiteConnectService, StrategyService strategyService, TradingEngine tradingEngine) {
        this.kiteConnectService = kiteConnectService;
        this.strategyService = strategyService;
        this.tradingEngine = tradingEngine;
    }

    @GetMapping("/login")
    public ResponseEntity<String> getLoginUrl() {
        return ResponseEntity.ok(kiteConnectService.getLoginURL());
    }

    @PostMapping("/session")
    public ResponseEntity<String> generateSession(@RequestParam String requestToken) {
        try {
            kiteConnectService.generateSession(requestToken);
            return ResponseEntity.ok("Session generated successfully.");
        } catch (KiteException | IOException e) {
            return ResponseEntity.status(500).body("Error generating session: " + e.getMessage());
        }
    }

    @GetMapping("/strategies")
    public ResponseEntity<List<String>> getStrategies() {
        return ResponseEntity.ok(strategyService.getAllStrategyNames());
    }

    @PostMapping("/strategy")
    public ResponseEntity<String> selectStrategy(@RequestParam String strategyName) {
        tradingEngine.setActiveStrategy(strategyName);
        return ResponseEntity.ok("Strategy set to: " + strategyName);
    }
}