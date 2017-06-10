/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import com.cf.data.model.poloniex.PoloniexChartData;
import static java.lang.Math.pow;
import java.util.ArrayList;
import static java.util.Collections.sort;
import java.util.List;
import technicalindicators.TechnicalIndicators;

/**
 *
 * @author Lucas
 */
public class Coin implements Comparable<Coin>{

    private final String simbolo;
    private TechnicalIndicators technicalIndicators;
    private ArrayList<StockValues> futureValues;
    private double accuracy;

    private List<StockValues> historicValues;
    private double normalizerValue;
    private double variance;

    public Coin(String simbolo) {
        this.simbolo = simbolo;
    }

    public TechnicalIndicators getTechnicalIndicators() {
        return technicalIndicators;
    }

    public void calculateTechnicalIndicators() {
        this.technicalIndicators = new TechnicalIndicators(historicValues);
    }

    public List<StockValues> getHistoricValues() {
        return historicValues;
    }

    public void setHistoricValues(List<StockValues> historicValues) {
        this.historicValues = historicValues;
        this.calculateTechnicalIndicators();
        this.evaluateVariance();
    }

    public String getSimbolo() {
        return simbolo;
    }

    public double getNormalizerValue() {
        return normalizerValue;
    }

    public void setNormalizerValue(double normalizerValue) {
        this.normalizerValue = normalizerValue;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }

    public ArrayList<StockValues> getFutureValues() {
        return futureValues;
    }

    public void setFutureValues(ArrayList<StockValues> futureValues) {
        sort(futureValues);

//        for (StockValues futureValue : futureValues) {
//            System.out.println(futureValue.getDate().getTime());
//        }

        this.futureValues = futureValues;
    }

    public double getVariance() {
        return variance;
    }

    private void evaluateVariance() {

        int limit = this.getTechnicalIndicators().getTimeSeries().getEnd();

        double mean = 0d;

        for (int i = 0; i < limit; i++) {
            mean += this.getTechnicalIndicators().getClosePrice().getValue(i).toDouble();
        }

        mean /= limit;

        double total = 0d;
        for (int i = 0; i < limit; i++) {
            total += pow(getTechnicalIndicators().getClosePrice().getValue(i).toDouble() - mean, 2);
        }

        variance = total / limit - 1;

    }

    @Override
    public String toString() {
        return "Coin{" + "simbolo=" + simbolo + ", volume atual=" + historicValues.get(0).getVolume() + ", futureValues=" + futureValues + ", normalizerValue=" + normalizerValue + ", variance=" + variance + '}';
    }

    @Override
    public int compareTo(Coin t) {
        return historicValues.get(0).getVolume().compareTo(t.getHistoricValues().get(0).getVolume());
    }

}
