package com.example.tradingapp.service;

import com.example.tradingapp.strategy.TradingStrategy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class StrategyService {

    private final Map<String, TradingStrategy> strategyMap;

    public StrategyService(List<TradingStrategy> strategies) {
        this.strategyMap = strategies.stream()
                .collect(Collectors.toMap(TradingStrategy::getName, Function.identity()));
    }

    public Optional<TradingStrategy> getStrategy(String name) {
        return Optional.ofNullable(strategyMap.get(name));
    }

    public List<String> getAllStrategyNames() {
        return strategyMap.keySet().stream().collect(Collectors.toList());
    }
}