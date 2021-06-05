package REAL_Enhanced;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import alpacaPojo.AlpacaApiCaller;
import alpacaPojo.OrdersPojo;
import alpacaPojo.PositionsPojo;


public class AlpacaCommonClass {
	private static final LocalDateTime marketCloseTime = LocalDate.now().atTime(15,00);
	AlpacaApiCaller alpacaAccount;
	OrdersPojo placeBuyOrder;
	OrdersPojo placedBuyOrder;
	OrdersPojo placeSellOrder;
	OrdersPojo placedSellOrder;
	Double lastBuyOrderPrice = 0.0;
	Double lastSellOrderPrice = 0.0;
	Double currentPrice = 0.0;
	String symbol;
	Double qty;
	PrintWriter pw; 
	Double maxStockPrice = 0.0;
	Double minStockPrice = 4000.0;
	Double profitFactor;
	Double upTrendFactor;
	Double downTrendFactor;
	Double stableHighPrice;
	Double stableLowPrice;
	Double totalBuyCount = 0.0;
	List<Double> filledBuyPrices = new ArrayList<Double>();
	List<Double> potentialBuyPrices = new ArrayList<Double>();
	Double totalSellCount = 0.0;
	List<Double> filledSellPrices = new ArrayList<Double>();	
	List<Double> potentialSellPrices = new ArrayList<Double>();
	Double nextDayPotentialStablePrice = 0.0;
	Double maxPlacedOrder = 0.0;
	Double minPlacedOrder = 0.0;
	Double maxQty;
	long secondsTimer;
	
	public OrdersPojo placeBuyOrderWithLimitSell() throws IOException, InterruptedException {
		placeBuyOrder = alpacaAccount.buySymbolQuantity(symbol, qty);
		placedBuyOrder = alpacaAccount.getOrderDetails(placeBuyOrder.getId());
		pw.write("Trying to Buy Order: " + placedBuyOrder.getSymbol() + "\t" + placedBuyOrder.getQty()+ "\t" + placedBuyOrder.getId() + 
				"\t" + currentPrice + "\t" + LocalDateTime.now().toString() + "\n");
		System.out.println("Trying to Buy Order: " + placedBuyOrder.getSymbol() + "\t" + placedBuyOrder.getQty()+ "\t" + placedBuyOrder.getId() + 
				"\t" + currentPrice + "\t" + LocalDateTime.now().toString());
		if (placedBuyOrder.getId() == null) return null;
		while (!"filled".equals(placedBuyOrder.getStatus()))
		{		
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

	public OrdersPojo placeRealBuyOrder() throws IOException, InterruptedException {
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
		filledBuyPrices.add(Double.valueOf(placeBuyOrder.getFilled_avg_price()));
		return placedBuyOrder;
	}
	
	public OrdersPojo placeSellOrderWithLimitBuy() throws IOException, InterruptedException {
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
	public OrdersPojo placeRealSellOrder() throws IOException, InterruptedException {
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
	
	//Returning Current price from paper account
	public Double returnCurrentPrice() throws IOException, InterruptedException {
		PositionsPojo openPosition = alpacaAccount.getSymbolPosition(symbol);
		if (openPosition.getQty()!=null)
		{
			return Double.valueOf(openPosition.getCurrent_price());
		}
		Double currentPrice = 0.00;
		OrdersPojo placeBuyOrder = alpacaAccount.buySymbolQuantity(symbol, 1.0);
		OrdersPojo placedBuyOrder = alpacaAccount.getOrderDetails(placeBuyOrder.getId());
		while (!"filled".equals(placedBuyOrder.getStatus()))
		{
			if (placedBuyOrder.getId() == null) {
				Thread.sleep(60000);
				placeBuyOrder = alpacaAccount.buySymbolQuantity(symbol, 1.0);
			}
			placedBuyOrder = alpacaAccount.getOrderDetails(placeBuyOrder.getId());
		}
		currentPrice = Double.valueOf(placedBuyOrder.getFilled_avg_price());
		OrdersPojo placeSellOrder = alpacaAccount.sellSymbolQuantity(symbol, 1.0);
		while (placeSellOrder.getId() == null) {
			placeSellOrder = alpacaAccount.sellSymbolQuantity(symbol, 1.0);
		}
		OrdersPojo placedSellOrder = alpacaAccount.getOrderDetails(placeSellOrder.getId());
		while (!"filled".equals(placedSellOrder.getStatus()))
		{
			placedSellOrder = alpacaAccount.getOrderDetails(placeSellOrder.getId());
		}
		currentPrice = Double.valueOf(placedSellOrder.getFilled_avg_price());
		return currentPrice;
	}
	
	public boolean isUpTrend() throws IOException, InterruptedException {
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
	
	public boolean isDownTrend(long timer) throws IOException, InterruptedException {
		Double testPrice = returnCurrentPrice();
		System.out.println("Initial Price: \t" + testPrice);
		if(testPrice.compareTo(maxStockPrice) > 0) {
			maxStockPrice = testPrice;
		}
		if(testPrice.compareTo(minStockPrice) < 0) {
			minStockPrice = testPrice;
		}
		do {
			Thread.sleep(timer);
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
	
	public void performContinousBuyOperations() throws NumberFormatException, IOException, InterruptedException {
		if(alpacaAccount.getClock().isIs_open()) {
			do {
				boolean isUpTrend = isUpTrend();
				PositionsPojo openPosition = alpacaAccount.getRealSymbolPosition(symbol);
				Double currentQty = Double.valueOf(openPosition.getQty() == null? "0.00":openPosition.getQty());
				Double currentPrice = returnCurrentPrice();
				if(isUpTrend && maxQty.compareTo(currentQty) > 0 && currentPrice.compareTo(stableHighPrice) < 0) {
					placeRealBuyOrder();
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
			if (wholeNumber > 0) {
				placeSellOrder = alpacaAccount.sellRealSymbolLimitOrder(symbol, Double.valueOf(wholeNumber), filledAveragePrice*profitFactor);
				Thread.sleep(100);
				placedSellOrder = alpacaAccount.getRealOrderDetails(placeSellOrder.getId());
				pw.write("Sell Order Executed with following Details: " + placedSellOrder.getSymbol() + "\t" + placedSellOrder.getQty()+ "\t" + placedSellOrder.getId() + 
						"\t" + placedSellOrder.getLimit_price() + "\t" + LocalDateTime.now().toString() + "\n");
				System.out.println("Sell Order Executed with following Details: " + placedSellOrder.getSymbol() + "\t" + placedSellOrder.getQty()+ "\t" + placedSellOrder.getId() + 
						"\t" + placedSellOrder.getLimit_price() + "\t" + LocalDateTime.now().toString());
			}
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
	
	public void performContinousSellOperations() throws NumberFormatException, IOException, InterruptedException {
		if(alpacaAccount.getClock().isIs_open()) {
			do {
				boolean isDownTrend = isDownTrend(secondsTimer);
				PositionsPojo openPosition = alpacaAccount.getRealSymbolPosition(symbol);
				Double currentQty = Double.valueOf(openPosition.getQty() == null? "0.00":openPosition.getQty())*-1.0;
				Double currentPrice = returnCurrentPrice();
				if(isDownTrend && maxQty.compareTo(currentQty) > 0 && returnCurrentPrice().compareTo(stableLowPrice) > 0) {
					placeRealSellOrder();
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
}
