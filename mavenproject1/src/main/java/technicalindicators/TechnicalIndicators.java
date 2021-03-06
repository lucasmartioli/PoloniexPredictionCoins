/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package technicalindicators;

import eu.verdelhan.ta4j.Tick;
import eu.verdelhan.ta4j.TimeSeries;
import eu.verdelhan.ta4j.indicators.simple.ClosePriceIndicator;
import eu.verdelhan.ta4j.indicators.trackers.EMAIndicator;
import eu.verdelhan.ta4j.indicators.trackers.MACDIndicator;
import eu.verdelhan.ta4j.indicators.trackers.ROCIndicator;
import eu.verdelhan.ta4j.indicators.trackers.RSIIndicator;
import eu.verdelhan.ta4j.indicators.trackers.SMAIndicator;
import eu.verdelhan.ta4j.indicators.volume.OnBalanceVolumeIndicator;
import java.util.ArrayList;
import java.util.List;
import model.StockValues;
import org.joda.time.DateTime;

/**
 *
 * @author Lucas
 */
public class TechnicalIndicators {

    static final int maxDaysIndicators = 26;

    public static int getMaxDaysIndicators() {
        return maxDaysIndicators;
    }

    private final TimeSeries timeSeries;
    private final ClosePriceIndicator closePrice;
    private final SMAIndicator sma4days;
    private final SMAIndicator sma9days;
    private final SMAIndicator sma18days;
    private final EMAIndicator ema35days;
    private final EMAIndicator ema5days;
    private final EMAIndicator ema5daysN;
    private final MACDIndicator macd;
    private final RSIIndicator rsi14days;
    private final ROCIndicator roc5days;
    private final OnBalanceVolumeIndicator obv;

    public TechnicalIndicators(List<StockValues> historic) {

        ArrayList<Tick> tickList = new ArrayList<>();
        for (int j = historic.size() - 1; j > 0; j--) {
            StockValues historicalQuote = historic.get(j);

            DateTime date = new DateTime(historicalQuote.getDate());
            Tick dado = new Tick(date, historicalQuote.getOpen(), historicalQuote.getHigh(), historicalQuote.getLow(), historicalQuote.getClose(), historicalQuote.getVolume());

            tickList.add(dado);
        }

        this.timeSeries = new TimeSeries(tickList);

        this.closePrice = new ClosePriceIndicator(timeSeries);
        this.obv = new OnBalanceVolumeIndicator(timeSeries);
        this.rsi14days = new RSIIndicator(closePrice, 14);
        this.macd = new MACDIndicator(closePrice, 5, 35);
        this.sma18days = new SMAIndicator(closePrice, 18);
        this.sma9days = new SMAIndicator(closePrice, 9);
        this.sma4days = new SMAIndicator(closePrice, 4);
        this.ema5days = new EMAIndicator(closePrice, 5);
        this.ema35days = new EMAIndicator(closePrice, 35);
        this.ema5daysN = new EMAIndicator(closePrice, 5);
        this.roc5days = new ROCIndicator(closePrice, 5);

    }

    public TimeSeries getTimeSeries() {
        return timeSeries;
    }

    public ClosePriceIndicator getClosePrice() {
        return closePrice;
    }

    public SMAIndicator getSma4days() {
        return sma4days;
    }
    
    public ROCIndicator getRoc5days() {
        return roc5days;
    }

    public SMAIndicator getSma9days() {
        return sma9days;
    }

    public SMAIndicator getSma18days() {
        return sma18days;
    }

    public MACDIndicator getMacd() {
        return macd;
    }

    public RSIIndicator getRsi14days() {
        return rsi14days;
    }

    public EMAIndicator getEma35days() {
        return ema35days;
    }

    public EMAIndicator getEma5days() {
        return ema5days;
    }

    public EMAIndicator getEma5daysN() {
        return ema5daysN;
    }

    public OnBalanceVolumeIndicator getObv() {
        return obv;
    }

}
