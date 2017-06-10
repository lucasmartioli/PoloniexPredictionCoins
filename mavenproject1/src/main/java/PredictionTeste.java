
import com.cf.client.poloniex.PoloniexExchangeService;
import com.cf.data.model.poloniex.PoloniexChartData;
import eu.verdelhan.ta4j.Decimal;
import eu.verdelhan.ta4j.Indicator;
import eu.verdelhan.ta4j.Tick;
import eu.verdelhan.ta4j.TimeSeries;
import java.io.IOException;
import static java.lang.Math.abs;
import static java.lang.Math.round;
import static java.lang.System.out;
import java.text.SimpleDateFormat;
import static java.time.ZoneOffset.UTC;
import static java.time.ZonedDateTime.now;
import java.util.ArrayList;
import java.util.List;
import model.Coin;
import model.StockValues;
import model.StockValuesComparator;
import static neuralnetworks.TrainingNeuralNetwork.toPredict;
import static neuralnetworks.TrainingNeuralNetwork.toTrain;
import static org.jfree.chart.ChartFactory.createTimeSeriesChart;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import static org.jfree.ui.RefineryUtilities.centerFrameOnScreen;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author pc
 */
public class PredictionTeste {

    public static void main(String[] args) {
        // TODO code application logic here

        PoloniexExchangeService service = new PoloniexExchangeService(null, null);
        List<String> marketsList = service.returnAllMarkets();
        StringComparator stringComparator = new StringComparator();
        marketsList.sort(stringComparator);
        out.println(marketsList);
        ArrayList<Coin> coins = new ArrayList<>();
        marketsList.stream().filter((coinName) -> !(!coinName.startsWith("BTC_ETH"))).map((coinName) -> {
            List<PoloniexChartData> dailyChart = service.returnChartData(coinName, 86400L, now(UTC).minusMonths(12).toEpochSecond());
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

        int tradesToPredict = 1;
        Coin coinT = coins.get(0);

        TimeSeries timeSeries = coinT.getTechnicalIndicators().getTimeSeries();
        int indicadorInicial = timeSeries.getBegin();
        int indicadorFinal = timeSeries.getEnd();
        double normalizerValue = 1;//getMaxPrice(coinT.getTechnicalIndicators());

        indicadorInicial += 36; // desconsiderando valores iniciais onde os indicadores não fazem tanto sentido.

        TimeSeriesCollection dataset = new TimeSeriesCollection();

        int inicioTreinamento = indicadorInicial;
        int finalTreinamento = round(((float) (indicadorFinal * 0.88d)));
        int inicioTestes = finalTreinamento + 1;
        int finalTestes = indicadorFinal;

        toTrain(coinT, inicioTreinamento, finalTreinamento, normalizerValue);

        org.jfree.data.time.TimeSeries chartResultados = new org.jfree.data.time.TimeSeries("Calculado");
        org.jfree.data.time.TimeSeries chartCP = new org.jfree.data.time.TimeSeries("Real");
        org.jfree.data.time.TimeSeries chartM4 = new org.jfree.data.time.TimeSeries("SMA 4 dias");
        org.jfree.data.time.TimeSeries chartM9 = new org.jfree.data.time.TimeSeries("SMA 9 dias");
        org.jfree.data.time.TimeSeries chartM18 = new org.jfree.data.time.TimeSeries("SMA 18 dias");
        org.jfree.data.time.TimeSeries chartDiferencas = new org.jfree.data.time.TimeSeries("Diferenca");
        org.jfree.data.time.TimeSeries chartCandleC = new org.jfree.data.time.TimeSeries("Candle Calculado");
        org.jfree.data.time.TimeSeries chartCandleR = new org.jfree.data.time.TimeSeries("Candle Real");
        double anteriorCalculado = 0d;
        double anteriorReal = 0d;
        double totalTests = 0d;
        double acerto = 0d;
        double diferencaTotal = 0d;
        for (int i = inicioTestes; i < finalTestes;) {

            double saida = toPredict(coinT, i, normalizerValue);
                System.out.println("Saida do algoritmo: " + saida);
                System.out.println("");
                System.out.println("Dia sendo analisado: " + timeSeries.getTick(i));
            i += tradesToPredict;

            if (i < finalTestes) {
//                    System.out.println("Diferença: " + (saida - timeSeries.getTick(i).getClosePrice().toDouble()) + " Dia no futuro: " + timeSeries.getTick(i));
                Tick tick = timeSeries.getTick(i);
                chartResultados.add(new Day(tick.getEndTime().toDate()), saida);
                chartCP.add(new Day(tick.getEndTime().toDate()), timeSeries.getTick(i).getClosePrice().toDouble());
                chartM4.add(new Day(tick.getEndTime().toDate()), coinT.getTechnicalIndicators().getSma4days().getValue(i).toDouble());
                chartM9.add(new Day(tick.getEndTime().toDate()), coinT.getTechnicalIndicators().getSma9days().getValue(i).toDouble());
                chartM18.add(new Day(tick.getEndTime().toDate()), coinT.getTechnicalIndicators().getSma18days().getValue(i).toDouble());
                chartDiferencas.add(new Day(tick.getEndTime().toDate()), saida - timeSeries.getTick(i).getClosePrice().toDouble());
                chartCandleR.add(new Day(tick.getEndTime().toDate()), timeSeries.getTick(i).getClosePrice().toDouble());
                diferencaTotal += abs(saida - timeSeries.getTick(i).getClosePrice().toDouble());
                if ((anteriorCalculado < saida) && (anteriorReal < timeSeries.getTick(i).getClosePrice().toDouble())
                        || (anteriorCalculado > saida) && (anteriorReal > timeSeries.getTick(i).getClosePrice().toDouble())) {
                    acerto += 1;
                }

                totalTests++;
                chartCandleC.add(new Day(tick.getEndTime().toDate()), (anteriorCalculado < saida) ? 1 : 0);
//                chartCandleR.add(new Day(tick.getEndTime().toDate()), (anteriorReal < timeSeries.getTick(i).getClosePrice().toDouble()) ? 1 : 0);
                anteriorCalculado = saida;
                anteriorReal = timeSeries.getTick(i).getClosePrice().toDouble();
            } else {
                Tick tick = timeSeries.getTick(i);
                chartResultados.add(new Day(tick.getEndTime().toDate()), saida);
            }
//                System.out.println("");
        }

        out.println("Total de testes: " + totalTests + " Total de acertos: " + acerto + " Diferença total: " + diferencaTotal);
        out.println("Percentual de acertos: " + (acerto / totalTests) * 100d + "%");
//            System.out.println(company2 + "\t" + (acerto / totalTests) * 100d);

//            System.out.println("Média de diferença: " + (diferencaTotal / totalTests));
//            dataset.addSeries(buildChartTimeSeries(inicioTestes, timeSeries, company.getTechnicalIndicators().getClosePrice(), "Real"));
//            dataset.addSeries(buildChartTimeSeries(inicioTestes, timeSeries, company.getTechnicalIndicators().getSma18days(), "SMA 18 dias"));
//            dataset.addSeries(buildChartTimeSeries(inicioTestes, timeSeries, company.getTechnicalIndicators().getSma4days(), "SMA 4 dias"));
//            dataset.addSeries(buildChartTimeSeries(inicioTestes, timeSeries, company.getTechnicalIndicators().getSma9days(), "SMA 9 dias"));
//            dataset.addSeries(chartCP);
        dataset.addSeries(chartM9);
        dataset.addSeries(chartM4);
        dataset.addSeries(chartM18);

        dataset.addSeries(chartResultados);

//            dataset.addSeries(chartDiferencas);
//            dataset.addSeries(chartCandleC);
            dataset.addSeries(chartCandleR);
        /**
         * Creating the chart
         */
        JFreeChart chart = createTimeSeriesChart(
                coinT.getSimbolo() + " - " + tradesToPredict + " trades", // title
                "Date", // x-axis label
                "Price Per Unit", // y-axis label
                dataset, // data
                true, // create legend?
                true, // generate tooltips?
                false // generate URLs?
        );
        XYPlot plot = (XYPlot) chart.getPlot();
        DateAxis axis = (DateAxis) plot.getDomainAxis();
        axis.setDateFormatOverride(new SimpleDateFormat("dd-MM-yyyy"));

        displayChart(chart);
    }

    private static org.jfree.data.time.TimeSeries buildChartTimeSeries(int initial, TimeSeries tickSeries, Indicator<Decimal> indicator, String name) {
        org.jfree.data.time.TimeSeries chartTimeSeries = new org.jfree.data.time.TimeSeries(name);
        for (int i = initial; i < tickSeries.getTickCount(); i++) {
            Tick tick = tickSeries.getTick(i);
            chartTimeSeries.add(new Day(tick.getEndTime().toDate()), indicator.getValue(i).toDouble());
        }
        return chartTimeSeries;
    }

    private static void displayChart(JFreeChart chart) {

        //http://www.jfree.org/jfreechart/api/javadoc/org/jfree/chart/plot/CombinedDomainCategoryPlot.html
        //http://www.java2s.com/Code/Java/Chart/JFreeChartCombinedCategoryPlotDemo1.htm
        // Chart panel
        ChartPanel panel = new ChartPanel(chart);
        panel.setFillZoomRectangle(true);
        panel.setMouseWheelEnabled(true);
        panel.setPreferredSize(new java.awt.Dimension(500, 270));
        // Application frame
        ApplicationFrame frame = new ApplicationFrame("Testes");

        frame.setContentPane(panel);
        frame.pack();
        centerFrameOnScreen(frame);
        frame.setVisible(true);

    }

}
