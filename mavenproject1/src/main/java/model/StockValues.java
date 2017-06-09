/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import com.cf.data.model.poloniex.PoloniexChartData;
import java.math.BigDecimal;
import java.util.Calendar;

/**
 *
 * @author Lucas
 */
public class StockValues implements Comparable<StockValues>{

    private Calendar date;

    private Double open;
    private Double low;
    private Double high;
    private Double close;
    private Double predictedClose;
    private Double beforeClose;
    private Double beforeRealClose;
    private Double increase;

    //private BigDecimal adjClose;
    private Long volume;

    public StockValues(PoloniexChartData h) {
        this.date = Calendar.getInstance();
        this.date.setTimeInMillis(Long.getLong(h.date));
        this.open = new Double(h.open);
        this.low = new Double(h.low);
        this.volume = new Long(h.volume);
        this.high = new Double(h.high);

    }

    public StockValues(Calendar date, double close, double predictedClose, double beforeClose, double beforeRealClose) {
        this.date = date;
        this.close = close;
        this.predictedClose = predictedClose;
        this.beforeClose = beforeClose;
        this.beforeRealClose = beforeRealClose;
    }

//    public StockValues(PoloniexChartData consulta) {
//        this.date = Calendar.getInstance();
//        this.open = new Double(consulta.open);
//        this.volume = consulta.getQuote().getVolume();
//        this.low = consulta.getQuote().getDayLow();
//        this.high = consulta.getQuote().getDayHigh();
//    }

    public Double getBeforeRealClose() {
        return beforeRealClose;
    }
    

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public Double getOpen() {
        return open;
    }

    public void setOpen(Double open) {
        this.open = open;
    }

    public Double getLow() {
        return low;
    }

    public void setLow(Double low) {
        this.low = low;
    }

    public Double getHigh() {
        return high;
    }

    public void setHigh(Double high) {
        this.high = high;
    }

    public Double getClose() {
        return close;
    }

    public void setClose(Double close) {
        this.close = close;
    }

    public Long getVolume() {
        return volume;
    }

    public void setVolume(Long volume) {
        this.volume = volume;
    }

    public Double getIncrease() {
        return increase;
    }

    public void setIncrease(Double increase) {
        this.increase = increase;
    }

    public Double getPredictedClose() {
        return predictedClose;
    }

    public Double getBeforeClose() {
        return beforeClose;
    }

    @Override
    public String toString() {
        return "StockValues{" + "date=" + date.getTime() + ", open=" + open + ", low=" + low + ", high=" + high + ", close=" + close + ", predictedClose=" + predictedClose + ", beforeClose=" + beforeClose + ", increase=" + increase + '}';
    }

    @Override
    public int compareTo(StockValues t) {
        return date.compareTo(t.getDate());
        
    }

}
