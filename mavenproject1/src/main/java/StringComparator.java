
import java.util.Comparator;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Lucas
 */
public class StringComparator implements Comparator<String> {

    @Override
    public int compare(String t, String t1) {
        return t1.compareToIgnoreCase(t);
    }
    
}
