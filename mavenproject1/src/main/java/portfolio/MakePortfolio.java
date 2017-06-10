/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package portfolio;

import eu.verdelhan.ta4j.Tick;
import eu.verdelhan.ta4j.TimeSeries;
import java.io.IOException;
import static java.lang.System.out;
import java.util.ArrayList;
import static java.util.Locale.ROOT;
import loadingcompany.LoadingCoin;
import model.Coin;
import model.StockValues;
import static neuralnetworks.TrainingNeuralNetwork.getMaxPrice;
import static neuralnetworks.TrainingNeuralNetwork.toPredict;
import static neuralnetworks.TrainingNeuralNetwork.toTrain;

/**
 *
 * @author Lucas
 */
public class MakePortfolio {

    private final ArrayList<String> companySymbols;
    private ArrayList<Portfolio> portfolios;
    private ArrayList<Coin> companies;
    private final Period period;
    private final int portfolioSize;
    private final int tradesToPredict = 1;

    public MakePortfolio(ArrayList<String> companySymbols, Period period, int portfolioSize) {
        this.companySymbols = companySymbols;
        this.period = period;
        this.portfolioSize = portfolioSize;
    }

    public ArrayList<Portfolio> getPortfolios() {
        return portfolios;
    }

    public void make() throws IOException {

        this.testsNNs();

        int indexFutureValues = 0;
        boolean finalizou = false;
        portfolios = new ArrayList<>();
        while (true) {
            ArrayList<Ativo> ativosDoDia = new ArrayList<>();

            for (Coin company : companies) {
                finalizou = indexFutureValues >= company.getFutureValues().size();
                if (finalizou) {

                    break;
                }

                Ativo a = new Ativo(company.getSimbolo(), company.getFutureValues().get(indexFutureValues), company.getVariance(), company.getAccuracy());
                ativosDoDia.add(a);
            }

            if (finalizou) {
                break;
            }

//            System.out.println("Ativos do dia: ");
//            for (Ativo ativo : ativosDoDia) {
//                System.out.println(ativo);
//                
//            }
            GeneticPortfolio geneticPortfolio = new GeneticPortfolio(portfolioSize, ativosDoDia);
            portfolios.add(geneticPortfolio.generate());
            indexFutureValues++;
        }
    }

    private void testsNNs() throws IOException {

        companies = new ArrayList<>();

        for (String companySymbol : companySymbols) {
            out.println("Testes para " + companySymbol + " iniciados.");

            Coin company = LoadingCoin.loading(companySymbol, period.getDateInitial(), period.getDateFinal());

            TimeSeries timeSeries = company.getTechnicalIndicators().getTimeSeries();
            int indicadorInicial = timeSeries.getBegin();
            int indicadorFinal = timeSeries.getEnd();
            company.setNormalizerValue(getMaxPrice(company.getTechnicalIndicators()));

            indicadorInicial += 36; // desconsiderando valores iniciais onde os indicadores não fazem sentido, pois ainda não tem informações suficientes
            
            toTrain(company, indicadorInicial, indicadorFinal, company.getNormalizerValue());
            double anteriorCalculado = toPredict(company, indicadorInicial - tradesToPredict, company.getNormalizerValue());
            double anteriorReal = timeSeries.getTick(indicadorInicial - tradesToPredict).getClosePrice().toDouble();
            double totalTests = 0d;
            double acerto = 0d;
            ArrayList<StockValues> futureValues = new ArrayList<>();

            for (int i = indicadorInicial; i < indicadorFinal;) {
//                System.out.println("Lu" + timeSeries.getTick(i).getEndTime().toCalendar(Locale.ROOT).getTime());

                double saida = toPredict(company, i, company.getNormalizerValue());

                Tick tick = timeSeries.getTick(i);

                i += tradesToPredict;
                if (i >= indicadorFinal) {
                    break;
                }

                if (indicadorFinal - 1 == i) {

                    StockValues futureStockValue;
                    out.println(tick.toString());
//                    if (anteriorCalculado == 0d) {
//                        futureStockValue = new StockValues(timeSeries.getTick(i).getEndTime().toCalendar(Locale.ROOT), timeSeries.getTick(i).getClosePrice().toDouble(), saida, tick.getClosePrice().toDouble());
//                    } else {
//                    System.out.println(timeSeries.getTick(i).toString());
                    futureStockValue = new StockValues(timeSeries.getTick(i).getEndTime().toCalendar(ROOT), timeSeries.getTick(i).getClosePrice().toDouble(), saida, anteriorCalculado, tick.getClosePrice().toDouble());
//                    }
                    double increase = ((saida - timeSeries.getTick(i).getClosePrice().toDouble()) / timeSeries.getTick(i).getClosePrice().toDouble()) * 100d;
                    futureStockValue.setIncrease(increase);
                    futureValues.add(futureStockValue);
                } else {
                    if ((anteriorCalculado < saida) && (anteriorReal < timeSeries.getTick(i).getClosePrice().toDouble())
                            || (anteriorCalculado > saida) && (anteriorReal > timeSeries.getTick(i).getClosePrice().toDouble())) {
                        acerto += 1;
                    }
                    totalTests++;
                }

                anteriorCalculado = saida;
                anteriorReal = timeSeries.getTick(i).getClosePrice().toDouble();

            }

            double accuracy = acerto / totalTests;
            company.setAccuracy(accuracy);
            out.println(accuracy);

            company.setFutureValues(futureValues);
            companies.add(company);
            out.println("Finalizado.");
        }

    }

}
