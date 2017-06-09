
import com.cf.client.poloniex.PoloniexExchangeService;
import com.cf.data.model.poloniex.PoloniexChartData;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Iterator;
import java.util.List;

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
        for (Iterator<String> iterator = marketsList.iterator(); iterator.hasNext();) {
            String next = iterator.next();
            System.out.println(next);

            List<PoloniexChartData> dailyChart
                    = service.returnChartData(next,
                            86400L,
                            ZonedDateTime.now(ZoneOffset.UTC).minusDays(3).toEpochSecond());

            for (Iterator<PoloniexChartData> iterator1 = dailyChart.iterator(); iterator1.hasNext();) {
                PoloniexChartData next1 = iterator1.next();
                
                System.out.println(next1.toString());
            }

        }
    }

}
