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
public class TendenciaDeCompraComparator implements Comparator<Coin>{

    @Override
    public int compare(Coin t, Coin t1) {
        return Double.compare(t1.getTendenciaDeCompra(), t.getTendenciaDeCompra());
    }
    
    
    
}
