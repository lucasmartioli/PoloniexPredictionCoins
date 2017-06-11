/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trader;

import java.util.ArrayList;
import model.Coin;

/**
 *
 * @author Lucas
 */
public class CoinOperator {

    private ArrayList<Operation> operations;
    private ArrayList<Operation> operadas;
    private double saldo;

    public CoinOperator() {
        operations = new ArrayList<>();
        operadas = new ArrayList<>();
        saldo = 1;
    }

    public double getSaldo() {
        return saldo;
    }

    public void buy(Coin coin, double price, double quote) {
        Operation o = new Operation(coin, price, coin.getTendenciaDeCompra(), quote);
        operations.add(o);
        saldo -= (price * quote);
    }

    public double sell(Coin coin, double price) {
        double r = 0;

        for (Operation operation : operations) {
            if (operation.getCoin().equal(coin)) {
                r += (operation.getQuote() * price);
            }

            operadas.add(operation);
            operations.remove(operation);
        }
        
        saldo += r;

        return r;
    }

    public ArrayList<Operation> getOperations() {
        return operations;
    }

}
