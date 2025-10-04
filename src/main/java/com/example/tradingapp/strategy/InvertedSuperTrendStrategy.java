package com.example.tradingapp.strategy;

import com.example.tradingapp.model.Order;
import com.example.tradingapp.model.Ticker;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class InvertedSuperTrendStrategy implements TradingStrategy {

    private static final String STRATEGY_NAME = "InvertedSuperTrendStrategy";
    private static final int PERIOD = 10;
    private static final double MULTIPLIER = 3.0;
    private static final double ORDER_VALUE = 1400000;
    private static final double STOP_LOSS_PERC = 0.01;
    private static final double TAKE_PROFIT_PERC = 0.03;


    private boolean inLongPosition = false;
    private boolean inShortPosition = false;

    @Override
    public String getName() {
        return STRATEGY_NAME;
    }

    @Override
    public Order generateSignal(List<Ticker> tickers) {
        if (tickers == null || tickers.size() < PERIOD) {
            return null; // Not enough data
        }

        List<SuperTrendResult> superTrendResults = calculateSuperTrend(tickers);

        if (superTrendResults.isEmpty()) {
            return null;
        }

        SuperTrendResult lastResult = superTrendResults.get(superTrendResults.size() - 1);
        Ticker lastTicker = tickers.get(tickers.size() - 1);
        Order order = null;

        // On bearish supertrend, enter long if no long position
        if (lastResult.getTrend() == -1 && !inLongPosition) {
            order = createOrder(lastTicker, "BUY");
            inLongPosition = true;
            inShortPosition = false; // Close any short position
        }
        // On bullish supertrend, enter short if no short position
        else if (lastResult.getTrend() == 1 && !inShortPosition) {
            order = createOrder(lastTicker, "SELL");
            inShortPosition = true;
            inLongPosition = false; // Close any long position
        }

        return order;
    }

    private int calculateQty(double price) {
        return (int) Math.round(ORDER_VALUE / price);
    }

    private Order createOrder(Ticker ticker, String transactionType) {
        Order order = new Order();
        double closePrice = ticker.getClose();
        order.setSymbol(ticker.getSymbol());
        order.setTransactionType(transactionType);
        order.setQuantity(calculateQty(closePrice));
        order.setPrice(closePrice);
        order.setExchange("NSE");

        if ("BUY".equals(transactionType)) {
            order.setStopLoss(closePrice * (1 - STOP_LOSS_PERC));
            order.setTakeProfit(closePrice * (1 + TAKE_PROFIT_PERC));
        } else { // SELL
            order.setStopLoss(closePrice * (1 + STOP_LOSS_PERC));
            order.setTakeProfit(closePrice * (1 - TAKE_PROFIT_PERC));
        }

        return order;
    }

    private List<SuperTrendResult> calculateSuperTrend(List<Ticker> tickers) {
        List<SuperTrendResult> results = new ArrayList<>();
        int n = tickers.size();
        if (n <= PERIOD) return results;

        double[] atr = calculateATR(tickers, PERIOD);
        double[] longStop = new double[n];
        double[] shortStop = new double[n];
        int[] trend = new int[n];
        double[] superTrend = new double[n];

        for (int i = PERIOD; i < n; i++) {
            double source = (tickers.get(i).getHigh() + tickers.get(i).getLow()) / 2.0;
            longStop[i] = source - MULTIPLIER * atr[i];
            shortStop[i] = source + MULTIPLIER * atr[i];

            if (i > PERIOD) {
                longStop[i] = (tickers.get(i - 1).getClose() > longStop[i - 1]) ? Math.max(longStop[i], longStop[i - 1]) : longStop[i];
                shortStop[i] = (tickers.get(i - 1).getClose() < shortStop[i - 1]) ? Math.min(shortStop[i], shortStop[i - 1]) : shortStop[i];
            }

            if (i == PERIOD) {
                trend[i] = 1;
            } else {
                int prevTrend = trend[i-1];
                if (prevTrend == -1 && tickers.get(i).getClose() > shortStop[i - 1]) {
                    trend[i] = 1;
                } else if (prevTrend == 1 && tickers.get(i).getClose() < longStop[i - 1]) {
                    trend[i] = -1;
                } else {
                    trend[i] = prevTrend;
                }
            }

            superTrend[i] = (trend[i] == 1) ? longStop[i] : shortStop[i];
            results.add(new SuperTrendResult(superTrend[i], trend[i]));
        }
        return results;
    }

    private double[] calculateATR(List<Ticker> tickers, int period) {
        double[] tr = new double[tickers.size()];
        double[] atr = new double[tickers.size()];

        for (int i = 1; i < tickers.size(); i++) {
            double high = tickers.get(i).getHigh();
            double low = tickers.get(i).getLow();
            double prevClose = tickers.get(i - 1).getClose();
            tr[i] = Math.max(high - low, Math.max(Math.abs(high - prevClose), Math.abs(low - prevClose)));
        }

        double sumTr = 0;
        for (int i = 1; i <= period; i++) {
            sumTr += tr[i];
        }
        atr[period] = sumTr / period;

        for (int i = period + 1; i < tickers.size(); i++) {
            atr[i] = (atr[i - 1] * (period - 1) + tr[i]) / period;
        }
        return atr;
    }

    private static class SuperTrendResult {
        private final double superTrend;
        private final int trend;

        public SuperTrendResult(double superTrend, int trend) {
            this.superTrend = superTrend;
            this.trend = trend;
        }

        public double getSuperTrend() {
            return superTrend;
        }

        public int getTrend() {
            return trend;
        }
    }
}