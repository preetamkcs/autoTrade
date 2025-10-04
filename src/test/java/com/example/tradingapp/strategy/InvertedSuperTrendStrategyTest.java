package com.example.tradingapp.strategy;

import com.example.tradingapp.model.Order;
import com.example.tradingapp.model.Ticker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class InvertedSuperTrendStrategyTest {

    private InvertedSuperTrendStrategy invertedSuperTrendStrategy;

    @BeforeEach
    void setUp() {
        invertedSuperTrendStrategy = new InvertedSuperTrendStrategy();
    }

    @Test
    void testGenerateSignal_shouldReturnBuyOrderOnBearishTrend() {
        // 1. Arrange: Create a list of tickers that will cause a BUY signal (bearish trend)
        List<Ticker> tickers = new ArrayList<>();
        double price = 100.0;
        LocalDateTime timestamp = LocalDateTime.now().minusMinutes(30);

        // Initial data to establish a trend
        for (int i = 0; i < 15; i++) {
            price += 0.5; // Upward trend
            tickers.add(createTicker("INFY", price, timestamp.plusMinutes(i)));
        }

        // Add a ticker that causes a bearish trend
        price = 90.0; // Sharp decrease
        tickers.add(createTicker("INFY", price, timestamp.plusMinutes(15)));


        // 2. Act: Generate the signal
        Order order = invertedSuperTrendStrategy.generateSignal(tickers);

        // 3. Assert: Check if a BUY order was generated
        assertNotNull(order, "Order should not be null for a bearish trend");
        assertEquals("BUY", order.getTransactionType());
        assertEquals("INFY", order.getSymbol());
        assertEquals(15556, order.getQuantity()); // 1400000 / 90
        assertEquals(89.1, order.getStopLoss(), 0.01); // 90 * (1 - 0.01)
        assertEquals(92.7, order.getTakeProfit(), 0.01); // 90 * (1 + 0.03)
    }

    private Ticker createTicker(String symbol, double close, LocalDateTime timestamp) {
        Ticker ticker = new Ticker();
        ticker.setSymbol(symbol);
        ticker.setOpen(close - 1);
        ticker.setHigh(close + 1);
        ticker.setLow(close - 2);
        ticker.setClose(close);
        ticker.setVolume(100000);
        ticker.setTimestamp(timestamp);
        return ticker;
    }
}