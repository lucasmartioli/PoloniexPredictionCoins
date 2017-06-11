package trader;


import java.util.Comparator;
import model.Coin;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Lucas
 */
public class CoinOBVComparator implements Comparator<Coin>{

    @Override
    public int compare(Coin t, Coin t1) {
        return t1.compareTo(t);
    }
    
}
