import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RideTheWaveWithVwap {
	static List<Double> tickerOpen = new ArrayList<Double>();
	static List<Double> tickerClose = new ArrayList<Double>();
	static List<Double> tickerLow = new ArrayList<Double>();
	static List<Double> tickerHigh = new ArrayList<Double>();
	static List<String> tickerDate = new ArrayList<String>();
	static List<Double> tickerVolume = new ArrayList<Double>();
	static List<Double> tickerVwap = new ArrayList<Double>();
	static Double[] tickerOneMinVwap = null;
	static Double[] tickerVolumeAverage = null;
//	static Double[] tickerTwoMinVwap = null;
	static SimpleDateFormat format = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
	static Date marketStartDate, marketEndDate;
	static PrintWriter pw;

	static String localTime = String.valueOf(LocalDateTime.now().getDayOfYear()) + String.valueOf(LocalDateTime.now().getHour()) + String.valueOf(LocalDateTime.now().getMinute()) + String.valueOf(LocalDateTime.now().getSecond());		

	//	static File fileTrue = new File("C:\\Users\\nakul\\Documents\\" + file + " - " + localTime + ".txt");
	//	static PrintWriter pw;
	//	static FileOutputStream fo;
	public static void main(String[] args) throws FileNotFoundException, ParseException {
		//		pw = new PrintWriter(new FileOutputStream(fileTrue, true));
		//		fo = new FileOutputStream(fileTrue, true);
		//		String file = "ADXS - 20210915.txt";
		File sourceFolder = new File("C:\\Users\\nakul\\Documents\\HistoricalData - VWAP\\SplitFiles");
		File fileTrue = new File("C:\\Users\\nakul\\Documents\\TickerFactors_VWAP\\" + "DYNT_VWAP" + " - " + localTime + ".txt");
		pw = new PrintWriter(new FileOutputStream(fileTrue, true));


		marketStartDate = format.parse("2021-07-20T12:45:00Z");
		marketEndDate = format.parse("2021-07-20T21:00:00Z");
		Double profit = 0.0, netProfit = 0.0;
		int lossDays = 0, profitDays = 0, noInvestmentDays = 0;
		for(File file : sourceFolder.listFiles()) {
			readFileAsList(file.getName());
			if(tickerOpen.size() == 0) {
				System.out.println("Empty File: " + file);
				pw.write("Empty File: " + file + "\n");
				continue;
			}
			tickerOneMinVwap = new Double[tickerLow.size()];
			tickerVolumeAverage = new Double[tickerLow.size()];
			calculateOneMinVwap(0);
//			calculateTwoMinVwap();
			profit = rideTheWave();
			if(profit.compareTo(0.0) > 0) {
				profitDays++;
			}
			else if(profit.compareTo(0.0) < 0) {
				lossDays++;
			}
			else {
				noInvestmentDays++;
			}
			netProfit = netProfit + profit;
		}
		System.out.println("Net Profit: " + netProfit + "\tProfit Days: " + profitDays + "\tLoss Days: " + lossDays + "\tNo Investment Days: " + noInvestmentDays);
		pw.write("Net Profit: " + netProfit + "\tProfit Days: " + profitDays + "\tLoss Days: " + lossDays + "\tNo Investment Days: " + noInvestmentDays);
		pw.close();
	} 

	private static void readFileAsList(String file) {
		String fileName = "C:\\Users\\nakul\\Documents\\HistoricalData - VWAP\\SplitFiles\\" + file;
		System.out.println("File Name: " + file);
		pw.write("File Name: " + file + "\n");
		boolean firstLine = false;
		try {
			//			URI uri = this.getClass().getResource(fileName).toURI();
			List<String> lines = Files.readAllLines(Paths.get(fileName),
					Charset.defaultCharset());
			for (String line : lines) {
				if(!firstLine) {
					firstLine = true;
					continue;
				}

				List<String> splitLine = Arrays.asList(line.split(",", -1));
				Date date = format.parse(splitLine.get(1));
				if (date.getHours() > marketStartDate.getHours() && date.getHours() < marketEndDate.getHours()) {
					tickerDate.add(splitLine.get(1));
					tickerOpen.add(Double.valueOf(splitLine.get(2)));
					tickerClose.add(Double.valueOf(splitLine.get(3)));
					tickerHigh.add(Double.valueOf(splitLine.get(4)));
					tickerLow.add(Double.valueOf(splitLine.get(5)));
					tickerVolume.add(Double.valueOf(splitLine.get(6)));
					tickerVwap.add(Double.valueOf(splitLine.get(8)));
				}
			}
			//								ticker.add(splitLine.get(3));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void calculateOneMinVwap(int position) {
		
		Double zeroPrice = (tickerLow.get(position) + tickerHigh.get(position) + tickerClose.get(position))/3;
		tickerOneMinVwap[position] = zeroPrice/tickerVolume.get(position);
		for (int i = position + 1 ; i < tickerOpen.size(); i++) {
			Double vwapNum = 0.0;
			Double vwapDenom = 0.0;
			Double avgPrice = 0.0;
			Double avgVolume = 0.0;
			for(int j = position ; j <= i; j++) {
				avgPrice = (tickerClose.get(j) + tickerHigh.get(j) + tickerLow.get(j))/3;
				vwapNum = vwapNum + avgPrice*tickerVolume.get(j);
				vwapDenom = vwapDenom + tickerVolume.get(j);
				avgVolume = tickerVolume.get(j) + avgVolume;
			}
			tickerOneMinVwap[i] = vwapNum/vwapDenom;
			tickerVolumeAverage[i] = avgVolume/i;
		}
	}

//	private static void calculateTwoMinVwap() {
//		tickerTwoMinVwap = new Double[tickerLow.size()];
//		Double zeroPrice = (tickerLow.get(0) + tickerHigh.get(0) + tickerClose.get(0))/3;
//		tickerTwoMinVwap[0] = zeroPrice/tickerVolume.get(0);
//		Double firstPrice = (tickerLow.get(1) + tickerHigh.get(1) + tickerClose.get(1))/3;
//		tickerTwoMinVwap[1] = (zeroPrice*tickerVolume.get(0) + firstPrice*tickerVolume.get(1))/(tickerVolume.get(0) + tickerVolume.get(1));
//		Double high, low;
//		for (int i = 2 ; i < tickerOpen.size(); i++) {
//			Double vwapNum = 0.0;
//			Double vwapDenom = 0.0;
//			Double avgPrice = 0.0;
//			for(int j = 0 ; j < i; j = j+2) {
//				if(j-i == 2 && i%2 == 0) {
//					avgPrice = (tickerClose.get(i) + tickerHigh.get(i) + tickerLow.get(i))/3;
//					vwapNum = vwapNum + avgPrice*tickerVolume.get(i);
//					vwapDenom = vwapDenom + tickerVolume.get(i);
//				}
//					high = tickerHigh.get(j).compareTo(tickerHigh.get(j+1)) > 0 ? tickerHigh.get(j) : tickerHigh.get(j+1);
//					low = tickerLow.get(j).compareTo(tickerLow.get(j+1)) < 0 ? tickerLow.get(j) : tickerLow.get(j+1);
//					avgPrice = (tickerClose.get(j+1) + high + low)/3;
//					vwapNum = vwapNum + avgPrice*(tickerVolume.get(j) + tickerVolume.get(j+1));
//					vwapDenom = vwapDenom + tickerVolume.get(j) + tickerVolume.get(j+1);
//			}
//			tickerTwoMinVwap[i] = vwapNum/vwapDenom;
//		}
//	}

	private static boolean isUpTrend(int position) {
		Double currentVwap = tickerVwap.get(position);
		Double prevVwap = tickerVwap.get(position-1);
		Double prevPrevVwap = tickerVwap.get(position-2);
		Double prevThreeVwap = tickerVwap.get(position-3);
		Double prevFourVwap = tickerVwap.get(position-4);
		
		Double currentLow = tickerLow.get(position);
		Double prevLow = tickerLow.get(position-1);
		Double prevPrevLow = tickerLow.get(position-2);
		Double prevThreeLow = tickerLow.get(position-3);
		Double prevFourLow = tickerLow.get(position-4);
		
		if((currentVwap.compareTo(prevVwap) > 0 && prevVwap.compareTo(prevPrevVwap) >= 0 
					&& prevPrevVwap.compareTo(prevThreeVwap) <=0 && prevThreeVwap.compareTo(prevFourVwap) < 0) ||
				(currentLow.compareTo(prevVwap) > 0 && prevLow.compareTo(prevPrevLow) >= 0 
					&& prevPrevLow.compareTo(prevThreeLow) <=0 && prevThreeLow.compareTo(prevFourLow) < 0)) return true;
		
		return false;
	}
	
	private static boolean isDownTrend(int position) {
		Double currentVwap = tickerVwap.get(position);
		Double prevVwap = tickerVwap.get(position-1);
		Double prevPrevVwap = tickerVwap.get(position-2);
		Double prevThreeVwap = tickerVwap.get(position-3);
		Double prevFourVwap = tickerVwap.get(position-4);
		
		Double currentHigh = tickerHigh.get(position);
		Double prevHigh = tickerHigh.get(position-1);
		Double prevPrevHigh = tickerHigh.get(position-2);
		Double prevThreeHigh = tickerHigh.get(position-3);
		Double prevFourHigh = tickerHigh.get(position-4);
		
		if((currentVwap.compareTo(prevVwap) < 0 && prevVwap.compareTo(prevPrevVwap) <= 0 
					&& prevPrevVwap.compareTo(prevThreeVwap) >=0 && prevThreeVwap.compareTo(prevFourVwap) > 0) || 
				(currentHigh.compareTo(prevHigh) < 0 && prevHigh.compareTo(prevPrevHigh) <= 0 
					&& prevPrevHigh.compareTo(prevThreeHigh) >=0 && prevThreeHigh.compareTo(prevFourHigh) > 0)) return true;
		return false;
	}
	private static double rideTheWave() throws ParseException {
		Double profit = 0.0;
		boolean isBought = false, isSold = false;
		boolean shortFirst = false, boughtFirst = false;
		Double buyPrice = 0.0, sellPrice = 0.0;
		for (int i=0; i< tickerOpen.size(); i++) {
			Date currentDate = format.parse(tickerDate.get(i));
			if(currentDate.getHours() < 14 || (currentDate.getHours() <= 14 && currentDate.getMinutes() <=30)) {
				i++;
				continue;
			}
			Double currentTicker = tickerOpen.get(i);
			Double priceToBuyOrSell = tickerClose.get(i);
			System.out.println("Time: " + tickerDate.get(i) + "\tOpen: " + tickerOpen.get(i) + "\tClose: " + tickerClose.get(i) 
			+ "\tHigh: " + tickerHigh.get(i) + "\tLow: " + tickerLow.get(i) + "\tVWAP: " + tickerVwap.get(i) + "\tVWAP - 1Min: " + tickerOneMinVwap[i]
					+ "\t Volume: " + tickerVolume.get(i));
			pw.write("Time: " + tickerDate.get(i) + "\tOpen: " + tickerOpen.get(i) + "\tClose: " + tickerClose.get(i) 
			+ "\tHigh: " + tickerHigh.get(i) + "\tLow: " + tickerLow.get(i) + "\tVWAP: " + tickerVwap.get(i) + "\tVWAP - 1Min: " + tickerOneMinVwap[i]
					+ "\t Volume: " + tickerVolume.get(i) + "\n");
			if(!isBought && isUpTrend(i)
					&& tickerHigh.get(i).compareTo(tickerOneMinVwap[i]) < 0) { //&& tickerHigh.get(i).compareTo(tickerTwoMinVwap[i]) < 0) {
				buyPrice = buyPrice + priceToBuyOrSell;
				System.out.println("Bought at: " + tickerDate.get(i) + ", " + priceToBuyOrSell + "\tVWAP: " + tickerVwap.get(i) + "\tVWAP - 1Min: " + tickerOneMinVwap[i] + "\t Volume: " + tickerVolume.get(i));
				pw.write("Bought at: " + tickerDate.get(i) + ", " + priceToBuyOrSell + "\tVWAP: " + tickerVwap.get(i) + "\tVWAP - 1Min: " + tickerOneMinVwap[i] + "\t Volume: " + tickerVolume.get(i));
				isBought = true;
				isSold = false;
				calculateOneMinVwap(i);
				i=i+5;
				if(!shortFirst && !boughtFirst) boughtFirst = true;
				continue;
			}
			if(!isSold && isDownTrend(i)
					&& tickerLow.get(i).compareTo(tickerOneMinVwap[i]) > 0 ){//&& tickerLow.get(i).compareTo(tickerTwoMinVwap[i]) > 0) {
				sellPrice = sellPrice + priceToBuyOrSell;
				System.out.println("Sold   at: " + tickerDate.get(i) + ", " + priceToBuyOrSell + "\tVWAP - 1Min: " + tickerOneMinVwap[i] + "\t Volume: " + tickerVolume.get(i));
				pw.write("Sold   at: " + tickerDate.get(i) + ", " + priceToBuyOrSell + "\tVWAP - 1Min: " + tickerOneMinVwap[i] + "\t Volume: " + tickerVolume.get(i));
				isBought = false;
				isSold = true;
				calculateOneMinVwap(i);
				i=i+5;
				if(!shortFirst && !boughtFirst) shortFirst = true;
			}
		}


		//		returnMultiplierProfitLoss();
		System.out.println("Start Price: " + tickerOpen.get(0) + "\tEnd Price:" + tickerOpen.get(tickerOpen.size()-1));
		System.out.println("Total Buy Price: " + buyPrice + "\tTotal Sold Price:" + sellPrice);
		System.out.print("Holding: " + isBought + "\t" + "Shorting: " + isSold + "\n");
		pw.write("Start Price: " + tickerOpen.get(0) + "\tEnd Price:" + tickerOpen.get(tickerOpen.size()-1) + "\n");
		pw.write("Total Buy Price: " + buyPrice + "\tTotal Sold Price:" + sellPrice + "\n");
		pw.write("Holding: " + isBought + "\t" + "Shorting: " + isSold + "\n");
		if(isBought) {
			if(shortFirst) {
				profit = sellPrice - buyPrice;
			}else {
				profit = sellPrice - buyPrice + Double.valueOf(tickerClose.get(tickerClose.size() - 1));
			}
		}
		if(isSold) {
			if(boughtFirst) {
				profit = sellPrice - buyPrice;
			}else {
				profit = sellPrice - buyPrice - Double.valueOf(tickerClose.get(tickerClose.size() - 1));
			}
		}
		System.out.println("Total Profit: " + profit + "\n");
		pw.write("Total Profit: " + profit + "\n");
		tickerOpen.clear();
		tickerLow.clear();
		tickerHigh.clear();
		tickerClose.clear();
		tickerVolume.clear();
		tickerDate.clear();
		tickerVwap.clear();
		return profit;
	}

}
