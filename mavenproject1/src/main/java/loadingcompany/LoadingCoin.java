/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package loadingcompany;

import com.cf.client.poloniex.PoloniexExchangeService;
import com.cf.data.model.poloniex.PoloniexTradeHistory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import model.Coin;
import model.StockValues;

/**
 *
 * @author Lucas
 */
public class LoadingCoin {

    private static final String commonSuffix = ".SA";

    public static ArrayList<Coin> loading(String[] listaDeCoins, Calendar dataInicial, Calendar dataFinal) throws IOException {

        ArrayList<Coin> coins = new ArrayList<>();
        PoloniexExchangeService service = new PoloniexExchangeService(null, null);
        List<String> marketsList = service.returnAllMarkets();
        
        Map<String, Stock> consulta = YahooFinance.get(listaDeCoins, dataInicial, dataFinal, Interval.DAILY);

        for (Map.Entry<String, Stock> entry : consulta.entrySet()) {
            String simbolo = entry.getKey();
            Stock value = entry.getValue();

            Coin company = new Coin(simbolo);

            company.setHistoricValues(value.getHistory());
            coins.add(company);
        }

        return coins;
    }

    public static Coin loading(String symbol, Calendar dataInicial, Calendar dataFinal) throws IOException {

        YahooFinance.logger.setLevel(Level.OFF);
        Stock consulta = YahooFinance.get(symbol + commonSuffix, dataInicial, dataFinal, Interval.DAILY);

        Coin company = new Coin(symbol);
        company.setHistoricValues(consulta.getHistory());

        return company;
    }

    public static Coin loadingCoin(String symbol, Calendar dataInicial, Calendar dataFinal) throws IOException {

        YahooFinance.logger.setLevel(Level.OFF);
        Stock consulta = YahooFinance.get(symbol, dataInicial, dataFinal, Interval.DAILY);

        Coin company = new Coin(symbol);
        company.setHistoricValues(consulta.getHistory());

        return company;
    }

    public static Coin loading(String empresa) throws IOException {

        YahooFinance.logger.setLevel(Level.OFF);
        Stock consulta = YahooFinance.get(empresa + commonSuffix);

        StockValues sv = new StockValues(consulta);

        Coin company = new Coin(empresa);
        company.setCurrentValues(sv);

        return company;
    }

    public static Coin loadingCoin(String empresa) throws IOException {

        YahooFinance.logger.setLevel(Level.OFF);
        Stock consulta = YahooFinance.get(empresa);

        StockValues sv = new StockValues(consulta);

        Coin company = new Coin(empresa);
        company.setCurrentValues(sv);

        return company;
    }
}
