/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.Comparator;

/**
 *
 * @author Lucas
 */
public class StockValuesComparator implements Comparator<StockValues> {

    @Override
    public int compare(StockValues t, StockValues t1) {
        return t1.compareTo(t);
    }
    
}
