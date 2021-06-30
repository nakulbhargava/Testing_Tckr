package REAL;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import REAL_Enhanced.ConfigConstants;
import alpacaPojo.AlpacaApiCaller;
import alpacaPojo.OrdersPojo;
import alpacaPojo.PositionsPojo;

//This is enhanced lazy limit strategy
public class Short_Sell_Single_Account_Strategy {

	private static final String alpacaKey = "APCA-API-KEY-ID";
	private static final String secretId = "APCA-API-SECRET-KEY";

	//Nakul
//	private static final String alpacaKeyValue = "PKXTC9CIEKM9SXKWR7CN";
//	private static final String secretIdValue = "EWFyZnPGoQyTbSfPAcLO02ao5n6MXSvdbaQr19Y9";

	//Vaishali
	//	private static final String alpacaKeyValue = "PKN9AR7BFFZXH5X3MP6H";
	//	private static final String secretIdValue = "daalp83963QKdcmOqI7iaqBwkdZxFRdFAmcijLZH";

	//Distorted
		private static final String alpacaKeyValue = "PKW027OGK8HX515NRG66";
		private static final String secretIdValue = "yoxhLKh4FXtxNl1LLHaUpAtoDkaKHCOHBopo0baS";

	//V
		private static final String alpacaKeyRealTime = ConfigConstants.alpacaKeyRealTimeVaishali;
		private static final String secretIdRealTime = ConfigConstants.secretIdRealTimeVaishali;
		private static final String headerContentType = ConfigConstants.headerContentType;
		private static final String headerContentTypeValue = ConfigConstants.headerContentTypeValue;
		private static final String paperUrl = ConfigConstants.paperUrl;
		private static final String realtimeUrl = ConfigConstants.realtimeUrl;
		private static final String activeVersion = ConfigConstants.activeVersion;
		private static final String inactiveVersion = ConfigConstants.inactiveVersion;
		private static final String ordersEndpoint = ConfigConstants.ordersEndpoint;
		private static final String accountEndpoint = ConfigConstants.accountEndpoint;
		private static final String lastQuoteEndpoint = ConfigConstants.lastQuoteEndpoint;
		private static final String lastTradeEndpoint = ConfigConstants.lastTradeEndpoint;
		private static final String positionsEndpoint = ConfigConstants.positionsEndpoint;
		private static final String assetsEndpoint = ConfigConstants.assetsEndpoint;
		private static final String clockEndpoint = ConfigConstants.clockEndpoint;
		private static final LocalDateTime marketCloseTime = LocalDate.now().atTime(15,00);

	static Double qty = 1.0;
	static Double maxQty = 10.0;

	static Double pointZeroOneFactor = maxQty/10000;

	//Below to be extracted from Extract Calibrating factor 5
	static Double profitFactor = .969;
	static Double upTrendFactor = 1.004;
	static Double downTrendFactor = .999;

	static String symbol = "SNAP";

	static AlpacaApiCaller alpacaAccount;
	static String localTime = String.valueOf(LocalDateTime.now().getDayOfYear()) + String.valueOf(LocalDateTime.now().getHour()) + String.valueOf(LocalDateTime.now().getMinute()) + String.valueOf(LocalDateTime.now().getSecond());
	static File file = new File("C:\\Users\\nakul\\Documents\\output - " + symbol + " - " + localTime + ".txt");
	static PrintWriter pw; 
	
	
	static FileOutputStream fo;
	static Double currentPrice = 0.00;
	static OrdersPojo placeBuyOrder;
	static OrdersPojo placedBuyOrder;
	static OrdersPojo placeSellOrder;
	static OrdersPojo placedSellOrder;
	static Double lastBuyOrderPrice = 0.00;
	static Double lastSellOrderPrice = 0.00;
	//Below value to be driven from ExtractingHighLowTimeframe
	static Double stableLowPrice = ConfigConstants.SNAP_stablePrice;

	static Double totalSellCount = 0.0;
	static List<Double> filledSellPrices = new ArrayList<Double>();	
	static List<Double> potentialSellPrices = new ArrayList<Double>();
	static Double nextDayPotentialStablePrice = 0.0;
	static Double maxStockPrice = 0.0, minStockPrice = 100.0;
	static Double minPlacedOrder = 0.0;

	public static void main(String[] args) throws IOException, InterruptedException {

		alpacaAccount = new AlpacaApiCaller(alpacaKey, secretId, alpacaKeyValue, secretIdValue, headerContentType,
				headerContentTypeValue, alpacaKeyRealTime, secretIdRealTime, paperUrl, realtimeUrl, activeVersion, inactiveVersion, ordersEndpoint, accountEndpoint, 
				lastQuoteEndpoint, lastTradeEndpoint, positionsEndpoint, assetsEndpoint, clockEndpoint);
		pw = new PrintWriter(new FileOutputStream(file));
		fo = new FileOutputStream(file);

		if(alpacaAccount.getClock().isIs_open()) {
			do {
				boolean isDownTrend = isDownTrend();
				PositionsPojo openPosition = alpacaAccount.getRealSymbolPosition(symbol);
				Double currentQty = Double.valueOf(openPosition.getQty() == null? "0.00":openPosition.getQty())*-1.0;
				Double currentPrice = returnCurrentPrice();
				if(isDownTrend && maxQty.compareTo(currentQty) > 0 && returnCurrentPrice().compareTo(stableLowPrice) > 0) {
					placeRealSellOrder(symbol, qty);
				}
				if(isDownTrend) {
					potentialSellPrices.add(currentPrice);
				}
//				Thread.sleep(1500000);
			}while(LocalDateTime.now().isBefore(marketCloseTime));
			System.out.println("List of Sell Orders: " + filledSellPrices.toString());
			pw.write("List of Sell Orders: " + filledSellPrices.toString() + "\n");			
			Double filledAveragePrice = 0.0;

			for (Double filledSellPrice: filledSellPrices) {
				filledAveragePrice = filledAveragePrice + filledSellPrice;
				if(filledSellPrice.compareTo(minPlacedOrder) < 0) {
					minPlacedOrder = filledSellPrice;
				}
				System.out.println("Trying to place Buy Order for Shares: " + qty + "\tWith Limit Price: " + filledSellPrice*profitFactor);
				pw.write("Trying to place Buy Order for Shares: " + qty + "\tWith Limit Price: " + filledSellPrice*profitFactor + "\n");
				placeBuyOrder = alpacaAccount.buyRealSymbolLimitOrder(symbol, qty, filledSellPrice*profitFactor);
				Thread.sleep(100);
				placedBuyOrder = alpacaAccount.getRealOrderDetails(placeBuyOrder.getId());
				pw.write("Buy Order Executed with following Details: " + placedBuyOrder.getSymbol() + "\t" + placedBuyOrder.getQty()+ "\t" + placedBuyOrder.getId() + 
						"\t" + placedBuyOrder.getLimit_price() + "\t" + LocalDateTime.now().toString() + "\n");
				System.out.println("Buy Order Executed with following Details: " + placedBuyOrder.getSymbol() + "\t" + placedBuyOrder.getQty()+ "\t" + placedBuyOrder.getId() + 
						"\t" + placedBuyOrder.getLimit_price() + "\t" + LocalDateTime.now().toString());
			}
			
			Double potentialAverageSellPrice = 0.0;
			for (Double potentialSellPrice: potentialSellPrices) {
				potentialAverageSellPrice = potentialAverageSellPrice + potentialSellPrice;
			}	
			potentialAverageSellPrice = potentialAverageSellPrice/potentialSellPrices.size();
			filledAveragePrice = filledAveragePrice/potentialSellPrices.size();
			System.out.println("Min Placed Sell Order Price: " + minPlacedOrder + "\tAverageFilledPrice: " + filledAveragePrice + "\tFor Stocks: " + filledSellPrices.size() + "\n");
			pw.write("Min Placed Sell Order Price: " + minPlacedOrder + "\tAverageFilledPrice: " + filledAveragePrice + "\tFor Stocks: " + filledSellPrices.size() + "\n");	
//			System.out.println("Trying to place Buy Order for Shares: " + wholeNumber + "\tWith Limit Price: " + filledAveragePrice*profitFactor);
//			pw.write("Trying to place Buy Order for Shares: " + wholeNumber + "\tWith Limit Price: " + filledAveragePrice*profitFactor + "\n");
//			placeBuyOrder = alpacaAccount.buyRealSymbolLimitOrder(symbol, Double.valueOf(wholeNumber), filledAveragePrice*profitFactor);
//			Thread.sleep(100);
//			placedBuyOrder = alpacaAccount.getRealOrderDetails(placeBuyOrder.getId());
//			pw.write("Buy Order Executed with following Details: " + placedBuyOrder.getSymbol() + "\t" + placedBuyOrder.getQty()+ "\t" + placedBuyOrder.getId() + 
//					"\t" + placedBuyOrder.getLimit_price() + "\t" + LocalDateTime.now().toString() + "\n");
//			System.out.println("Buy Order Executed with following Details: " + placedBuyOrder.getSymbol() + "\t" + placedBuyOrder.getQty()+ "\t" + placedBuyOrder.getId() + 
//					"\t" + placedBuyOrder.getLimit_price() + "\t" + LocalDateTime.now().toString());
			nextDayPotentialStablePrice = (maxStockPrice + maxStockPrice/profitFactor + minStockPrice + minStockPrice*profitFactor)/4;
			System.out.println("MaxStock: " + maxStockPrice + "\tMaxStockPrice/ProfitFactor: " + maxStockPrice/profitFactor + "\tMin Stock Price: " + minStockPrice + "MinStockPrice*ProfitFactor: " + minStockPrice*profitFactor + "\tNextDayPotentialStableHighPrice: " + nextDayPotentialStablePrice);
			pw.write("MaxStock: " + maxStockPrice + "\tMaxStockPrice/ProfitFactor: " + maxStockPrice/profitFactor + "\tMin Stock Price: " + minStockPrice + "MinStockPrice*ProfitFactor: " + minStockPrice*profitFactor + "\tNextDayPotentialStableHighPrice: " + nextDayPotentialStablePrice + "\n");
			System.out.println("Another Potential Stable Low Price: " + potentialAverageSellPrice);
			pw.write("Another Potential Stable Low Price: " + potentialAverageSellPrice + "\n");	
			pw.close();
		}
	}

	public static boolean isDownTrend() throws IOException, InterruptedException {
		Double testPrice = returnCurrentPrice();
		System.out.println("Initial Price: \t" + testPrice);
		if(testPrice.compareTo(maxStockPrice) > 0) {
			maxStockPrice = testPrice;
		}
		if(testPrice.compareTo(minStockPrice) < 0) {
			minStockPrice = testPrice;
		}
		do {
			Thread.sleep(460000);
			currentPrice = returnCurrentPrice();
			if(currentPrice.compareTo(maxStockPrice) > 0) {
				maxStockPrice = currentPrice;
			}
			if(currentPrice.compareTo(minStockPrice) < 0) {
				minStockPrice = currentPrice;
			}
			System.out.println("Initial Price: \t" + testPrice + "\tCurrent Price: \t" + currentPrice + "\t" + LocalDateTime.now().toString());
			pw.write("Initial Price: " + testPrice + "\tCurrent Price: " + currentPrice + "\t" + LocalDateTime.now().toString() + "\n");
			if (testPrice*downTrendFactor > currentPrice) {
				pw.write("Returning Downtrend to short sell - Initial Price: " + testPrice + " Current Price: " + currentPrice + "\t" + LocalDateTime.now().toString() + "\n");
				System.out.println("Returning Downtrend to short sell - Initial Price: " + testPrice + " Current Price: " + currentPrice + "\t" + LocalDateTime.now().toString());
				return true;
			}
			if ( testPrice*upTrendFactor < currentPrice) {
				pw.write("Returning Uptrend to do nothing - Initial Price: " + testPrice + " Current Price: " + currentPrice + LocalDateTime.now().toString() + "\n");
				System.out.println("Returning Uptrend to do nothing - Initial Price: " + testPrice + " Current Price: " + currentPrice + "\t" + LocalDateTime.now().toString());
				return false;
			}
		}while(LocalDateTime.now().isBefore(marketCloseTime));
		return false;
	}	

	public static Double returnCurrentPrice() throws IOException, InterruptedException {
		PositionsPojo openPosition = alpacaAccount.getSymbolPosition(symbol);
		if (openPosition.getQty()!=null)
		{
			return Double.valueOf(openPosition.getCurrent_price());
		}
		Double currentPrice = 0.00;
		OrdersPojo placeBuyOrder = alpacaAccount.buySymbolQuantity(symbol, pointZeroOneFactor);
		OrdersPojo placedBuyOrder = alpacaAccount.getOrderDetails(placeBuyOrder.getId());
		while (!"filled".equals(placedBuyOrder.getStatus()))
		{
			placedBuyOrder = alpacaAccount.getOrderDetails(placeBuyOrder.getId());
		}
		currentPrice = Double.valueOf(placedBuyOrder.getFilled_avg_price());
		OrdersPojo placeSellOrder = alpacaAccount.sellSymbolQuantity(symbol, pointZeroOneFactor);
		while (placeSellOrder.getId() == null) {
			placeSellOrder = alpacaAccount.sellSymbolQuantity(symbol, pointZeroOneFactor);
		}
		OrdersPojo placedSellOrder = alpacaAccount.getOrderDetails(placeSellOrder.getId());
		while (!"filled".equals(placedSellOrder.getStatus()))
		{
			placedSellOrder = alpacaAccount.getOrderDetails(placeSellOrder.getId());
		}
		currentPrice = Double.valueOf(placedSellOrder.getFilled_avg_price());
		return currentPrice;
	}

	public static OrdersPojo placeSellOrderWithLimitBuy(String symbol, Double qty) throws IOException, InterruptedException {
		placeSellOrder = alpacaAccount.sellSymbolQuantity(symbol, qty);
		placedSellOrder = alpacaAccount.getOrderDetails(placeSellOrder.getId());
		pw.write("Trying to short sell: " + placedSellOrder.getSymbol() + "\t" + placedSellOrder.getQty()+ "\t" + placedSellOrder.getId() + 
				"\t" + currentPrice + "\t" + LocalDateTime.now().toString() + "\n");
		System.out.println("Trying to short sell: " + placedSellOrder.getSymbol() + "\t" + placedSellOrder.getQty()+ "\t" + placedSellOrder.getId() + 
				"\t" + currentPrice + "\t" + LocalDateTime.now().toString());

		if (placedSellOrder.getId() == null) return null;
		while (!"filled".equals(placedSellOrder.getStatus()))
		{
			Thread.sleep(100);
			placedSellOrder = alpacaAccount.getOrderDetails(placeSellOrder.getId());
		}
		pw.write("Short Sell Filled: " + placedSellOrder.getSymbol() + "\t" + placedSellOrder.getQty()+ "\t" + placedSellOrder.getId() + 
				"\t" + placedSellOrder.getFilled_avg_price() + "\t" + LocalDateTime.now().toString() + "\n");
		System.out.println("Short Sell Filled: " + placedSellOrder.getSymbol() + "\t" + placedSellOrder.getQty()+ "\t" + placedSellOrder.getId() + 
				"\t" + placedSellOrder.getFilled_avg_price() + "\t" + LocalDateTime.now().toString());
		lastSellOrderPrice = Double.valueOf(placedSellOrder.getFilled_avg_price());;
		placeBuyOrder = alpacaAccount.buySymbolLimitOrder(symbol, qty,  lastSellOrderPrice*profitFactor);
		placedBuyOrder = alpacaAccount.getOrderDetails(placeBuyOrder.getId());
		pw.write("Buy Order Executed with following Details: " + placedBuyOrder.getSymbol() + "\t" + placedBuyOrder.getQty()+ "\t" + placedBuyOrder.getId() + 
				"\t" + placedBuyOrder.getSide() + "\t" + placedBuyOrder.getFilled_avg_price() + 
				"\t" + LocalDateTime.now().toString() + "\n");
		System.out.println("Buy Order Executed with following Details: " + placedBuyOrder.getSymbol() + "\t" + placedBuyOrder.getQty()+ "\t" + placedBuyOrder.getId() + 
				"\t" + placedBuyOrder.getSide() + "\t" + placedBuyOrder.getFilled_avg_price() + 
				"\t" + LocalDateTime.now().toString());
		return placedSellOrder;
	}
	public static OrdersPojo placeRealSellOrder(String symbol, Double qty) throws IOException, InterruptedException {
		placeSellOrder = alpacaAccount.sellRealSymbolQuantity(symbol, qty);
		placedSellOrder = alpacaAccount.getRealOrderDetails(placeSellOrder.getId());
		pw.write("Trying to Sell Order: " + placedSellOrder.getSymbol() + "\t" + placedSellOrder.getQty()+ "\t" + placedSellOrder.getId() + 
				"\t" + currentPrice + "\t" + LocalDateTime.now().toString() + "\n");
		System.out.println("Trying to Sell Order: " + placedSellOrder.getSymbol() + "\t" + placedSellOrder.getQty()+ "\t" + placedSellOrder.getId() + 
				"\t" + currentPrice + "\t" + LocalDateTime.now().toString());
		if (placedSellOrder.getId() == null) return null;
		while (!"filled".equals(placedSellOrder.getStatus()))
		{		
			if(!alpacaAccount.getClock().isIs_open()) return null;
			Thread.sleep(100);
			placedSellOrder = alpacaAccount.getRealOrderDetails(placeSellOrder.getId());

		}
		pw.write("Sell Order filled: " + placedSellOrder.getSymbol() + "\t" + placedSellOrder.getQty()+ "\t" + placedSellOrder.getId() + 
				"\t" + placedSellOrder.getFilled_avg_price() + "\t" + LocalDateTime.now().toString() + "\n");
		System.out.println("Sell Order filled: " + placedSellOrder.getSymbol() + "\t" + placedSellOrder.getQty()+ "\t" + placedSellOrder.getId() + 
				"\t" + placedSellOrder.getFilled_avg_price() + "\t" + LocalDateTime.now().toString());
		totalSellCount = totalSellCount + qty;
		filledSellPrices.add(Double.valueOf(placedSellOrder.getFilled_avg_price()));
		return placedSellOrder;
	}
}