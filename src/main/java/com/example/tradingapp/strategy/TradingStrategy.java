package com.example.tradingapp.strategy;

import com.example.tradingapp.model.Order;
import com.example.tradingapp.model.Ticker;

import java.util.List;

public interface TradingStrategy {

    String getName();

    Order generateSignal(List<Ticker> tickers);
}