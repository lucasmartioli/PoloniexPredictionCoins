
import com.cf.client.poloniex.PoloniexExchangeService;
import com.cf.data.model.poloniex.PoloniexChartData;
import static java.lang.System.out;
import static java.time.ZoneOffset.UTC;
import static java.time.ZonedDateTime.now;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import model.Coin;
import model.StockValues;
import model.StockValuesComparator;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Lucas
 */
public class Prediction {

    public static void main(String[] args) {
        // TODO code application logic here

        PoloniexExchangeService service = new PoloniexExchangeService(null, null);
        List<String> marketsList = service.returnAllMarkets();
        StringComparator stringComparator = new StringComparator();
        marketsList.sort(stringComparator);
        out.println(marketsList);
        ArrayList<Coin> coins = new ArrayList<>();
        marketsList.stream().filter((coinName) -> !(!coinName.startsWith("BTC"))).map((coinName) -> {
            List<PoloniexChartData> dailyChart = service.returnChartData(coinName, 300L, now(UTC).minusMinutes(10).toEpochSecond());
            ArrayList<StockValues> values = new ArrayList<>();
            for (PoloniexChartData data : dailyChart) {
                StockValues v = new StockValues(data);
                values.add(v);
            }
            StockValuesComparator comparator = new StockValuesComparator();
            values.sort(comparator);
            Coin coin = new Coin(coinName);
            coin.setHistoricValues(values);
            return coin;
        }).forEach((coin) -> {
            coins.add(coin);
//            out.println(coinName + " " + values.get(0).getDate().getCalendarType() + " " + values.get(0).getVolume());
        });
        CoinVolumeComparator coinVolumeComparator = new CoinVolumeComparator();
        coins.sort(coinVolumeComparator);

        for (int i = 0; i < 5; i++) {
            out.println(coins.get(i).toString());
        }

    }

}
