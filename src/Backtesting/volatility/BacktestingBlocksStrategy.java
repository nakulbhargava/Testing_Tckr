package Backtesting.volatility;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Stack;

public class BacktestingBlocksStrategy {
	static String localTime = String.valueOf(LocalDateTime.now().getDayOfYear()) + String.valueOf(LocalDateTime.now().getHour()) + String.valueOf(LocalDateTime.now().getMinute()) + String.valueOf(LocalDateTime.now().getSecond());		
	static List<String> ticker = null;
	static String filename = "MinData - ENVB_2020-08-01_2021-09-17";
	static File fileTrue = new File("C:\\Users\\nakul\\Documents\\" + filename + " - " + localTime + ".txt");
	static PrintWriter pw;
	static FileOutputStream fo;
	static String symbol = "";
	static List<Double> tickerOpen = new ArrayList<Double>();
	static List<Double> tickerClose = new ArrayList<Double>();
	static List<Double> tickerLow = new ArrayList<Double>();
	static List<Double> tickerHigh = new ArrayList<Double>();
	static List<String> tickerDate = new ArrayList<String>();
	static Stack<Double> buys = new Stack<Double>();
	static Stack<Double> correspondingSells = new Stack<Double>();
	static int totalSell = 0, totalBuy = 0, maxBuys = 0;

	static SimpleDateFormat format = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);

	static int oneTimeInvestment = 50;
	static Double profitPercent = 1.02;
	static Double lossPercent = 0.95;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		BacktestingBlocksStrategy demo = new BacktestingBlocksStrategy();
		demo.readFileAsList();
		for(lossPercent = 0.975; lossPercent > 0.845; lossPercent = lossPercent - 0.025) {
			for(profitPercent = 1.02; profitPercent < 1.051 ; profitPercent = profitPercent + 0.005) {
				for(int i = 0; i < tickerOpen.size(); i++) {
					if(buys.isEmpty()) {
						buys.push(tickerOpen.get(i));
						correspondingSells.push(profitPercent*tickerOpen.get(i));
						totalBuy++;
						//				System.out.println("Buy Performed at: " + tickerDate.get(i) + " with buy price: " + tickerOpen.get(i) + " for correspondingSell: " + profitPercent*tickerOpen.get(i));
					}
					/*
					 * Keeping this factor as 2 as in the real world it wont be possible to have more than 2 buys in a minute
					 */
					int maxOneMinBuys = 0;
					while(tickerLow.get(i).compareTo(lossPercent*buys.peek()) < 0 ) {
						maxOneMinBuys++;
						Double buyPrice = lossPercent*buys.peek();
						buys.push(buyPrice);
						correspondingSells.push(profitPercent*buyPrice);
						totalBuy++;
						//				System.out.println("Buy Performed at: " + tickerDate.get(i) + " with buy price: " + buyPrice + " for correspondingSell: " + profitPercent*buyPrice);
						if(maxOneMinBuys == 2) break;
					}
					if(buys.size() > maxBuys) maxBuys = buys.size();
					while(tickerHigh.get(i).compareTo(correspondingSells.peek()) > 0) {
						totalSell++;
						Double sellPrice = correspondingSells.pop();
						Double buyPrice = buys.pop();
//						System.out.println("Sell Performed at: " + tickerDate.get(i) + " with sell price: " + sellPrice + " for correspondingBuy: " + buyPrice);
						if(correspondingSells.isEmpty() || buys.isEmpty()) break;
						
					}

				}
				System.out.println(symbol + " - Profit Factor: " + profitPercent + " Buy Back Factor: " + lossPercent + " Total Buys: " + totalBuy + " Total Sells: " + totalSell);
				System.out.println(symbol + " - Remaining Buys: " + buys + " Max Buys: " + maxBuys);

				Iterator<Double> buysIterator = buys.iterator();

				Double remainingShares = 0.0;
				while(buysIterator.hasNext()) {
					Double buy = buysIterator.next();
					Double shareForThisBuy = oneTimeInvestment/buy;
					remainingShares = remainingShares + shareForThisBuy;
				}
				Double remainingInvestedAmount = Double.valueOf(buys.size()*oneTimeInvestment);
				Double marketValue = remainingShares*tickerClose.get(tickerClose.size() - 1);
				Double lossDueToHoldingShares = marketValue - remainingInvestedAmount;
				Double profitDueToSell = (profitPercent - 1.0)*totalSell*oneTimeInvestment;
				Double netProfit = profitDueToSell + lossDueToHoldingShares;

				System.out.println(symbol + " - Net Profit: " + netProfit + " Profit Due to Sell: " + profitDueToSell + " Loss Due to Holding: " + lossDueToHoldingShares + "\n");
				remainingInvestedAmount = 0.0;
				netProfit = 0.0;
				totalSell = 0; 
				totalBuy = 0; 
				maxBuys = 0;
				buys.clear();
				correspondingSells.clear();
			}
		}
	}
	private void readFileAsList() {
		String fileName = "/test/" + filename + ".txt";

		boolean firstLine = false;
		try {
			URI uri = this.getClass().getResource(fileName).toURI();
			List<String> lines = Files.readAllLines(Paths.get(uri),
					Charset.defaultCharset());
			for (String line : lines) {
				if(!firstLine) {
					firstLine = true;
					continue;
				}
				
				List<String> splitLine = Arrays.asList(line.split(",", -1));
				symbol = splitLine.get(0);
				tickerDate.add(splitLine.get(1));
				tickerOpen.add(Double.valueOf(splitLine.get(2)));
				tickerClose.add(Double.valueOf(splitLine.get(3)));
				tickerHigh.add(Double.valueOf(splitLine.get(4)));
				tickerLow.add(Double.valueOf(splitLine.get(5)));
				//					tickerVolume.add(Double.valueOf(splitLine.get(6)));
				//					tickerVwap.add(Double.valueOf(splitLine.get(8)));
			}
			//								ticker.add(splitLine.get(3));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
