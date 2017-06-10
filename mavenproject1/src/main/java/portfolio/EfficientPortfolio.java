/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package portfolio;

import java.io.IOException;
import static java.lang.System.out;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import static java.util.Calendar.getInstance;
import static java.util.TimeZone.getTimeZone;
import static java.util.logging.Level.SEVERE;
import static java.util.logging.Logger.getLogger;
import static org.jfree.chart.ChartFactory.createTimeSeriesChart;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import static org.jfree.ui.RefineryUtilities.centerFrameOnScreen;
import static portfolio.Portfolio.cabecExcel;

/**
 *
 * @author Lucas
 */
public class EfficientPortfolio {

    public static void main(String[] args) {
        ArrayList<String> companies = new ArrayList<>();

       
        TimeSeriesCollection dataset = new TimeSeriesCollection();

        for (int i = 0; i < 1; i++) {

            Calendar di = getInstance(getTimeZone("America/Sao Paulo"));
            di.set(15, 0, 1, 12, 0);
            Calendar df = getInstance(getTimeZone("America/Sao Paulo"));
            df.set(17, 1, 21, 12, 0);

            Period period = new Period(di, df, 0.99, 0.01);

            MakePortfolio makePortfolio = new MakePortfolio(companies, period, 10);

            try {
                makePortfolio.make();
            } catch (IOException ex) {
                getLogger(EfficientPortfolio.class.getName()).log(SEVERE, null, ex);
            }

            org.jfree.data.time.TimeSeries chartProfit = new org.jfree.data.time.TimeSeries("Profit");
            org.jfree.data.time.TimeSeries chartID = new org.jfree.data.time.TimeSeries("ID");
            org.jfree.data.time.TimeSeries chartEstimatedProfit = new org.jfree.data.time.TimeSeries("Estimated Profit");
            org.jfree.data.time.TimeSeries chartInvestiment = new org.jfree.data.time.TimeSeries("Investiment");
            org.jfree.data.time.TimeSeries chartVariance = new org.jfree.data.time.TimeSeries("Variance");
            org.jfree.data.time.TimeSeries chartAccuracy = new org.jfree.data.time.TimeSeries("Accuracy");

            double totalDeInvestimento = 0d;
            double totalInvestimentoEfetuado = 0d;
            double lucroBruto = 0d;
            double lucroDeInvestimento = 0d;
            double totalDeAccuracy = 0d;
            int totalDePortfolios = 0;
            out.println(cabecExcel());
            for (Portfolio p : makePortfolio.getPortfolios()) {
                if (p.getEstimatedProfit() > 0) {
                    lucroDeInvestimento += p.getProfit();
                    totalDeInvestimento += p.getInvestment();
                }
                out.println(p.toExcel());

                lucroBruto += p.getProfit();
                totalInvestimentoEfetuado += p.getInvestment();

//            System.out.println(p.toString());
//                System.out.println(p.toStringResum());
                chartProfit.add(new Day(p.getDate().getTime()), p.getProfit());
                chartID.add(new Day(p.getDate().getTime()), p.getId());
                chartEstimatedProfit.add(new Day(p.getDate().getTime()), p.getInvestment() + (p.getEstimatedProfit() * p.getInvestment()));
                chartInvestiment.add(new Day(p.getDate().getTime()), p.getInvestment());
                chartVariance.add(new Day(p.getDate().getTime()), p.getVariance());
                chartAccuracy.add(new Day(p.getDate().getTime()), p.getAccuracy());
//                System.out.println("p.getAccuracy(): " + p.getAccuracy());
                totalDeAccuracy += p.getAccuracy();
                totalDePortfolios++;
            }
//            System.out.println("Total de portfolios: " + totalDePortfolios);
//            System.out.println("Accuracy: " + totalDeAccuracy / totalDePortfolios);
//
//            System.out.println(GeneticPortfolio.objectiveFunction);
//            System.out.println("Investimento: " + totalInvestimentoEfetuado);
//            System.out.println("Lucro: " + lucroBruto + "(ou prejuizo) e lucro liquido: " + lucroDeInvestimento);
//            System.out.println("Lucro %: " + (lucroBruto / totalInvestimentoEfetuado) * 100d + "(ou prejuizo) e lucro liquido %: " + (lucroDeInvestimento / totalDeInvestimento) * 100d);

            dataset.addSeries(chartProfit);
            dataset.addSeries(chartEstimatedProfit);
//        dataset.addSeries(chartAccuracy);
//        dataset.addSeries(chartInvestiment);
//        dataset.addSeries(chartVariance);
//        dataset.addSeries(chartID);
        }

        /**
         * Creating the chart
         */
        JFreeChart chart = createTimeSeriesChart(
                "Portfolios", // title
                "Date", // x-axis label
                "R$", // y-axis label
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

    private static void displayChart(JFreeChart chart) {
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
