/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trader;

import model.Coin;

/**
 *
 * @author Lucas
 */
public class Operation {
    
    private final Coin coin;
    private final double buyPrice;
    private double sellPrice;
    private double quote;

    public double getQuote() {
        return quote;
    }
    private final double buyT;

    public Operation(Coin coin, double buyPrice, double buyT, double quote) {
        this.coin = coin;
        this.buyPrice = buyPrice;
        this.buyT = buyT;
        this.quote = quote;
    }   

    @Override
    public String toString() {
        return "Operation{" + "coin=" + coin + ", buyPrice=" + buyPrice + ", sellPrice=" + sellPrice + ", buyT=" + buyT + '}';
    }    

    public void setSellPrice(double sellPrice) {
        this.sellPrice = sellPrice;
    }    

    public Coin getCoin() {
        return coin;
    }

    public double getBuyPrice() {
        return buyPrice;
    }

    public double getSellPrice() {
        return sellPrice;
    }

    public double getBuyT() {
        return buyT;
    }

    
    
}
