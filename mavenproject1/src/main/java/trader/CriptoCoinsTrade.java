package trader;

import com.cf.client.poloniex.PoloniexExchangeService;
import com.cf.data.model.poloniex.PoloniexChartData;
import com.cf.data.model.poloniex.PoloniexCompleteBalance;
import eu.verdelhan.ta4j.indicators.trackers.SMAIndicator;
import static java.lang.System.out;
import java.math.BigDecimal;
import static java.time.ZoneOffset.UTC;
import static java.time.ZonedDateTime.now;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import model.Coin;
import model.StockValues;
import model.StockValuesComparator;
import model.TendenciaDeCompraComparator;
import static java.time.ZonedDateTime.now;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author pc
 */
public class CriptoCoinsTrade {

    public static void main(String[] args) {

        //Throwable f = new 
        CoinOperator co = new CoinOperator();

        while (true) {

            PoloniexExchangeService service = new PoloniexExchangeService(null, null);
            List<String> marketsList = service.returnAllMarkets();
//        marketsList.sort(String.);
            out.println(marketsList);
            ArrayList<Coin> coins = new ArrayList<>();

            int y = 0;
            for (String coinName : marketsList) {
                if (!coinName.startsWith("BTC")) {
                    continue;
                }

                List<PoloniexChartData> dailyChart = service.returnChartData(coinName, 300L, now(UTC).minusMinutes(300).toEpochSecond());
                ArrayList<StockValues> values = new ArrayList<>();
                for (PoloniexChartData data : dailyChart) {
                    StockValues v = new StockValues(data);
                    values.add(v);
                }
                StockValuesComparator comparator = new StockValuesComparator();
                values.sort(comparator);

                if (values.isEmpty()) {
                    continue;
                }

                Coin coin = new Coin(coinName);
                coin.setHistoricValues(values);

                coins.add(coin);
                y++;
            }
//            out.println(coinName + " " + values.get(0).getDate().getCalendarType() + " " + values.get(0).getVolume());

            CoinOBVComparator coinOBVComparator = new CoinOBVComparator();
            coins.sort(coinOBVComparator);

            int pond = coins.size();
            for (Coin coin : coins) {
                coin.addTendenciaDeCompra(pond);
                pond--;
                int end = coin.getTechnicalIndicators().getTimeSeries().getEnd();
                SMAIndicator sma4 = coin.getTechnicalIndicators().getSma4days();
                SMAIndicator sma9 = coin.getTechnicalIndicators().getSma9days();
                SMAIndicator sma18 = coin.getTechnicalIndicators().getSma18days();
                for (int i = 20; i <= end; i++) {
                    if (sma4.getValue(i).toDouble() > sma18.getValue(i).toDouble()) {
                        coin.addTendenciaDeCompra(i / 5);
                    } else {
                        coin.addTendenciaDeCompra(-(i / 5));
                    }
                    if (sma4.getValue(i).toDouble() < sma9.getValue(i).toDouble() && sma4.getValue(i).toDouble() > sma18.getValue(i).toDouble()) {
                        coin.addTendenciaDeCompra(-(i / 10));
                    }
                }

                Double rsiMedia = 0D;
                for (int i = 15; i <= end; i++) {
                    rsiMedia += coin.getTechnicalIndicators().getRsi14days().getValue(i).toDouble();
                }
                rsiMedia /= end;
                coin.addTendenciaDeCompra(-rsiMedia);
                coin.addTendenciaDeCompra(coin.getVariance());

//                coin.getTechnicalIndicators().getObv().getValue(coin.getTechnicalIndicators().getTimeSeries().getEnd())
            }

            TendenciaDeCompraComparator tendenciaDeCompraComparator = new TendenciaDeCompraComparator();
            coins.sort(tendenciaDeCompraComparator);
            Coin ultimacomprada = null;
            out.println("SALDO: " + co.getSaldo());
            for (int i = 0; i < coins.size(); i++) {                
                Coin coin = coins.get(i);
                if (i == 0) {
                    if (ultimacomprada != null)
                        co.sell(ultimacomprada, ultimacomprada.getHistoricValues().get(0).getClose());
                    
                    ultimacomprada = coin;
                    co.buy(coin, coin.getHistoricValues().get(0).getClose(), 10);                    
                }
                
                out.println(coin.getSimbolo() + " "
                        + coin.getTendenciaDeCompra() + " "
                        + coin.getHistoricValues().get(0).getClose() + " "
                        + coin.getHistoricValues().get(0).getDate().toString());

            }

        }

    }
}
