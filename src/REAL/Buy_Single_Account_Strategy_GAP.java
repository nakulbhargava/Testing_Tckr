package REAL;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import alpacaPojo.OrdersPojo;
import alpacaPojo.PositionsPojo;

//This is enhanced lazy limit strategy
public class Buy_Single_Account_Strategy_GAP {

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

	private static final String alpacaKeyRealTime = "AK7LQFK01P6RWA5UVNZT";
	private static final String secretIdRealTime = "CZJaF69oPrGsy2i4gvZsiSNNju6Pdtx9Gmh1tdyD";
	private static final String headerContentType = "Content-Type";
	private static final String headerContentTypeValue = "application/json";
	private static final String paperUrl = "https://paper-api.alpaca.markets";
	private static final String realtimeUrl = "https://api.alpaca.markets";
	private static final String activeVersion = "/v2";
	private static final String inactiveVersion = "/v1";
	private static final String ordersEndpoint = "/orders";
	private static final String accountEndpoint = "/account";
	private static final String lastQuoteEndpoint = "/last_quote/stocks";
	private static final String lastTradeEndpoint = "/last/stocks";
	private static final String positionsEndpoint = "/positions";
	private static final String assetsEndpoint = "/assets";
	private static final String clockEndpoint = "/clock";
	private static final LocalDateTime marketCloseTime = LocalDate.now().atTime(15,00);

	static Double qty = 0.05;
	static Double maxQty = 57.0;

	static Double pointZeroOneFactor = maxQty/500;

	//Below to be extracted from Extract Calibrating factor 5
	static Double profitFactor = 1.041;
	static Double upTrendFactor = 1.001;
	static Double downTrendFactor = .9955;

	static String symbol = "GPS";

	static AlpacaApiCaller alpacaAccount;
	static int secondsCounter = 0;
	static String localTime = String.valueOf(LocalDateTime.now().getDayOfYear()) + String.valueOf(LocalDateTime.now().getHour()) + String.valueOf(LocalDateTime.now().getMinute()) + String.valueOf(LocalDateTime.now().getSecond());
	static File file = new File("C:\\Users\\nakul\\Documents\\outputReal - " + symbol + " - " + localTime + ".txt");
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
	static Double stableHighPrice = 33.69;
	static Double totalBuyCount = 0.0;
	static List<Double> filledBuyPrices = new ArrayList<Double>();	
	static List<Double> potentialBuyPrices = new ArrayList<Double>();
	static Double nextDayPotentialStablePrice = 0.0;
	static Double maxStockPrice = 0.0, minStockPrice = 100.0;
	static Double maxPlacedOrder = 0.0;
	public static void main(String[] args) throws IOException, InterruptedException {

		alpacaAccount = new AlpacaApiCaller(alpacaKey, secretId, alpacaKeyValue, secretIdValue, headerContentType,
				headerContentTypeValue, alpacaKeyRealTime, secretIdRealTime, paperUrl, realtimeUrl, activeVersion, inactiveVersion, ordersEndpoint, accountEndpoint, 
				lastQuoteEndpoint, lastTradeEndpoint, positionsEndpoint, assetsEndpoint, clockEndpoint);
		pw = new PrintWriter(new FileOutputStream(file), true);
		fo = new FileOutputStream(file, true);

		if(alpacaAccount.getClock().isIs_open()) {
			do {
				boolean isUpTrend = isUpTrend();
				PositionsPojo openPosition = alpacaAccount.getRealSymbolPosition(symbol);
				Double currentQty = Double.valueOf(openPosition.getQty() == null? "0.00":openPosition.getQty());
				Double currentPrice = returnCurrentPrice();
				if(isUpTrend && maxQty.compareTo(currentQty) > 0 && currentPrice.compareTo(stableHighPrice) < 0) {
					placeRealBuyOrder(symbol, qty);
				}
				if(isUpTrend) {
					potentialBuyPrices.add(currentPrice);
				}
			}while(LocalDateTime.now().isBefore(marketCloseTime));
			
			System.out.println("List of Buy Orders: " + filledBuyPrices.toString());
			pw.write("List of Buy Orders: " + filledBuyPrices.toString() + "\n");			
			Double totalBuyStockForTheDay = Double.valueOf(filledBuyPrices.size())*qty;
			int wholeNumber = totalBuyStockForTheDay.intValue();
			Double filledAveragePrice = 0.0;

			for (Double filledBuyPrice: filledBuyPrices) {
				filledAveragePrice = filledAveragePrice + filledBuyPrice;
				if(filledBuyPrice.compareTo(maxPlacedOrder) > 0) {
					maxPlacedOrder = filledBuyPrice;
				}
			}
			
			Double potentialAverageBuyPrice = 0.0;
			for (Double potentialBuyPrice: potentialBuyPrices) {
				potentialAverageBuyPrice = potentialAverageBuyPrice + potentialBuyPrice;
			}	
			potentialAverageBuyPrice = potentialAverageBuyPrice/potentialBuyPrices.size();
			filledAveragePrice = filledAveragePrice/filledBuyPrices.size();
			System.out.println("Max Placed Buy Order Price: " + maxPlacedOrder + "\tAverageFilledPrice: " + filledAveragePrice + "\tFor Stocks: " + filledBuyPrices.size() + "\n");
			pw.write("Max Placed Buy Order Price: " + maxPlacedOrder + "\tAverageFilledPrice: " + filledAveragePrice + "\tFor Stocks: " + filledBuyPrices.size() + "\n");	
			System.out.println("Trying to place Sell Order for Shares: " + wholeNumber + "\tWith Limit Price: " + filledAveragePrice*profitFactor);
			pw.write("Trying to place Sell Order for Shares: " + wholeNumber + "\tWith Limit Price: " + filledAveragePrice*profitFactor + "\n");
			placeSellOrder = alpacaAccount.sellRealSymbolLimitOrder(symbol, Double.valueOf(wholeNumber), filledAveragePrice*profitFactor);
			Thread.sleep(100);
			placedSellOrder = alpacaAccount.getRealOrderDetails(placeSellOrder.getId());
			pw.write("Sell Order Executed with following Details: " + placedSellOrder.getSymbol() + "\t" + placedSellOrder.getQty()+ "\t" + placedSellOrder.getId() + 
					"\t" + placedSellOrder.getLimit_price() + "\t" + LocalDateTime.now().toString() + "\n");
			System.out.println("Sell Order Executed with following Details: " + placedSellOrder.getSymbol() + "\t" + placedSellOrder.getQty()+ "\t" + placedSellOrder.getId() + 
					"\t" + placedSellOrder.getLimit_price() + "\t" + LocalDateTime.now().toString());
//			for (Double filledBuyPrice: filledBuyPrices) {
//				placeSellOrder = alpacaAccount.sellRealSymbolLimitOrder(symbol, qty, filledBuyPrice*profitFactor);
//				Thread.sleep(100);
//				placedSellOrder = alpacaAccount.getRealOrderDetails(placeSellOrder.getId());
//				pw.write("Sell Order Executed with following Details: " + placedSellOrder.getSymbol() + "\t" + placedSellOrder.getQty()+ "\t" + placedSellOrder.getId() + 
//						"\t" + placedSellOrder.getLimit_price() + "\t" + LocalDateTime.now().toString() + "\n");
//				System.out.println("Sell Order Executed with following Details: " + placedSellOrder.getSymbol() + "\t" + placedSellOrder.getQty()+ "\t" + placedSellOrder.getId() + 
//						"\t" + placedSellOrder.getLimit_price() + "\t" + LocalDateTime.now().toString());
//			}
			nextDayPotentialStablePrice = (maxStockPrice + maxStockPrice/profitFactor + minStockPrice + minStockPrice*profitFactor)/4;
			System.out.println("MaxStock: " + maxStockPrice + "\tMaxStockPrice/ProfitFactor: " + maxStockPrice/profitFactor + "\tMin Stock Price: " + minStockPrice + "\tMinStockPrice*ProfitFactor: " + minStockPrice*profitFactor + "\tNextDayPotentialStableHighPrice: " + nextDayPotentialStablePrice);
			pw.write("MaxStock: " + maxStockPrice + "\tMaxStockPrice/ProfitFactor: " + maxStockPrice/profitFactor + "\tMin Stock Price: " + minStockPrice + "\tMinStockPrice*ProfitFactor: " + minStockPrice*profitFactor + "\tNextDayPotentialStableHighPrice: " + nextDayPotentialStablePrice + "\n");
			System.out.println("Another Potential Stable High Price: " + potentialAverageBuyPrice);
			pw.write("Another Potential Stable High Price: " + potentialAverageBuyPrice + "\n");	
			pw.close();
		}
	}

	public static boolean isUpTrend() throws IOException, InterruptedException {
		Double testPrice = returnCurrentPrice();
		if(testPrice.compareTo(maxStockPrice) > 0) {
			maxStockPrice = testPrice;
		}
		if(testPrice.compareTo(minStockPrice) < 0) {
			minStockPrice = testPrice;
		}
		do {
			Thread.sleep(3000);
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
				pw.write("Returning Downtrend to do nothing - Initial Price: " + testPrice + "\t Current Price: " + currentPrice + "\t" + LocalDateTime.now().toString() + "\n");
				System.out.println("Returning Downtrend to do nothing - Initial Price: \t" + testPrice + "\t Current Price: " + currentPrice + "\t" + LocalDateTime.now().toString());
				return false;
			}
			if ( testPrice*upTrendFactor < currentPrice) {
				pw.write("Returning Uptrend to Buy - Initial Price: " + testPrice + " \tCurrent Price: " + currentPrice + "\t" + LocalDateTime.now().toString() + "\n");
				System.out.println("Returning Uptrend to Buy - Initial Price: \t" + testPrice + "\t Current Price: " + currentPrice + "\t" + LocalDateTime.now().toString());
				return true;
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

	public static OrdersPojo placeBuyOrderWithLimitSell(String symbol, Double qty) throws IOException, InterruptedException {
		placeBuyOrder = alpacaAccount.buySymbolQuantity(symbol, qty);
		placedBuyOrder = alpacaAccount.getOrderDetails(placeBuyOrder.getId());
		pw.write("Trying to Buy Order: " + placedBuyOrder.getSymbol() + "\t" + placedBuyOrder.getQty()+ "\t" + placedBuyOrder.getId() + 
				"\t" + currentPrice + "\t" + LocalDateTime.now().toString() + "\n");
		System.out.println("Trying to Buy Order: " + placedBuyOrder.getSymbol() + "\t" + placedBuyOrder.getQty()+ "\t" + placedBuyOrder.getId() + 
				"\t" + currentPrice + "\t" + LocalDateTime.now().toString());
		if (placedBuyOrder.getId() == null) return null;
		while (!"filled".equals(placedBuyOrder.getStatus()))
		{		
			if(!alpacaAccount.getClock().isIs_open()) return null;
			Thread.sleep(100);
			placedBuyOrder = alpacaAccount.getOrderDetails(placeBuyOrder.getId());

		}
		pw.write("Buy Order filled at: " + placedBuyOrder.getSymbol() + "\t" + placedBuyOrder.getQty()+ "\t" + placedBuyOrder.getId() + 
				"\t" + placedBuyOrder.getFilled_avg_price() + "\t" + LocalDateTime.now().toString() + "\n");
		System.out.println("Buy Order filled: " + placedBuyOrder.getSymbol() + "\t" + placedBuyOrder.getQty()+ "\t" + placedBuyOrder.getId() + 
				"\t" + placedBuyOrder.getFilled_avg_price() + "\t" + LocalDateTime.now().toString());

		lastBuyOrderPrice = Double.valueOf(placedBuyOrder.getFilled_avg_price());
		placeSellOrder = alpacaAccount.sellSymbolLimitOrder(symbol, qty,  lastBuyOrderPrice*profitFactor);
		placedSellOrder = alpacaAccount.getOrderDetails(placeSellOrder.getId());
		pw.write("Sell Order Executed with following Details: " + placedSellOrder.getSymbol() + "\t" + placedSellOrder.getQty()+ "\t" + placedSellOrder.getId() + 
				"\t" + placedSellOrder.getFilled_avg_price() + "\t" + LocalDateTime.now().toString() + "\n");
		System.out.println("Sell Order Executed with following Details: " + placedSellOrder.getSymbol() + "\t" + placedSellOrder.getQty()+ "\t" + placedSellOrder.getId() + 
				"\t" + placedSellOrder.getFilled_avg_price() + "\t" + LocalDateTime.now().toString());
		return placedBuyOrder;
	}

	public static OrdersPojo placeRealBuyOrder(String symbol, Double qty) throws IOException, InterruptedException {
		placeBuyOrder = alpacaAccount.buyRealSymbolQuantity(symbol, qty);
		if (placeBuyOrder == null) return null;
		placedBuyOrder = alpacaAccount.getRealOrderDetails(placeBuyOrder.getId());
		pw.write("Trying to Buy Order: " + placedBuyOrder.getSymbol() + "\t" + placedBuyOrder.getQty()+ "\t" + placedBuyOrder.getId() + 
				"\t" + currentPrice + "\t" + LocalDateTime.now().toString() + "\n");
		System.out.println("Trying to Buy Order: " + placedBuyOrder.getSymbol() + "\t" + placedBuyOrder.getQty()+ "\t" + placedBuyOrder.getId() + 
				"\t" + currentPrice + "\t" + LocalDateTime.now().toString());
		if (placedBuyOrder.getId() == null) return null;
		while (!"filled".equals(placedBuyOrder.getStatus()))
		{		
			if(!alpacaAccount.getClock().isIs_open()) return null;
			Thread.sleep(100);
			placedBuyOrder = alpacaAccount.getRealOrderDetails(placeBuyOrder.getId());

		}
		pw.write("Buy Order filled: " + placedBuyOrder.getSymbol() + "\t" + placedBuyOrder.getQty()+ "\t" + placedBuyOrder.getId() + 
				"\t" + placedBuyOrder.getFilled_avg_price() + "\t" + LocalDateTime.now().toString() + "\n");
		System.out.println("Buy Order filled: " + placedBuyOrder.getSymbol() + "\t" + placedBuyOrder.getQty()+ "\t" + placedBuyOrder.getId() + 
				"\t" + placedBuyOrder.getFilled_avg_price() + "\t" + LocalDateTime.now().toString());
		totalBuyCount = totalBuyCount + qty;
		filledBuyPrices.add(Double.valueOf(placedBuyOrder.getFilled_avg_price()));
		return placedBuyOrder;
	}
}