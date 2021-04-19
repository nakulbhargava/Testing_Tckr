package Strategies;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;

import alpacaPojo.OrdersPojo;
import alpacaPojo.PositionsPojo;

public class Two_Account_Strategy {

	private static final String alpacaKey = "APCA-API-KEY-ID";
	private static final String secretId = "APCA-API-SECRET-KEY";
	private static final String alpacaKeyValue = "PKXTC9CIEKM9SXKWR7CN";
	private static final String secretIdValue = "EWFyZnPGoQyTbSfPAcLO02ao5n6MXSvdbaQr19Y9";
	private static final String alpacaKeyValueAccount2 = "PKN9AR7BFFZXH5X3MP6H";
	private static final String secretIdValueAccount2 = "daalp83963QKdcmOqI7iaqBwkdZxFRdFAmcijLZH";
	private static final String alpacaKeyRealTime = "AK7LQFK01P6RWA5UVNZT";
	private static final String secretIdRealTime = "CZJaF69oPrGsy2i4gvZsiSNNju6Pdtx9Gmh1tdyD";
	private static final String headerContentType = "Content-Type";
	private static final String headerContentTypeValue = "application/json";
	private static final String paperUrl = "https://paper-api.alpaca.markets";
	private static final String realtimeUrl = "https://data.alpaca.markets";
	private static final String activeVersion = "/v2";
	private static final String inactiveVersion = "/v1";
	private static final String ordersEndpoint = "/orders";
	private static final String accountEndpoint = "/account";
	private static final String lastQuoteEndpoint = "/last_quote/stocks";
	private static final String lastTradeEndpoint = "/last/stocks";
	private static final String positionsEndpoint = "/positions";
	private static final String assetsEndpoint = "/assets";
	private static final String clockEndpoint = "/clock";
	private static final LocalDateTime marketCloseTime = LocalDate.now().atTime(14,59);

	static Double qty = 10.00;
	static Double maxQty = (qty*10);
	static Double pointZeroOneFactor = qty/1000;
	static Double pointOneFactor = 0.999;
	static Double pointTwoFactor = 0.998;
	static Double onePointTwoFactor = 1.002;
	//static Double pointTwoFactor = .998;
	static Double onePointFourFactor = 1.005;
	static Double onePointOneFactor = 1.001;
	static Double pointFourFactor = .995;

	static String symbol = "LMND";
	static AlpacaApiCaller alpacaAccount, alpacaAccount2;
	static OrdersPojo placeBuyOrder;
	static OrdersPojo placedBuyOrder;
	static OrdersPojo placeSellOrder;
	static OrdersPojo placedSellOrder;
	static OrdersPojo placeBuyOrder2;
	static OrdersPojo placedBuyOrder2;
	static OrdersPojo placeSellOrder2;
	static OrdersPojo placedSellOrder2;

	static String localTime = String.valueOf(LocalDateTime.now().getDayOfYear()) + String.valueOf(LocalDateTime.now().getHour()) + String.valueOf(LocalDateTime.now().getMinute()) + String.valueOf(LocalDateTime.now().getSecond());
	static File file = new File("C:\\Users\\nakul\\Documents\\output - " + symbol + " - " + localTime + ".txt");
	static PrintWriter pw; 
	static FileOutputStream fo;
	static Double lastBuyOrderPrice = 0.00;
	static Double lastSellOrderPrice = 0.00;
	static Double lastBuyOrderPrice2 = 0.00;
	static Double lastSellOrderPrice2 = 0.00;
	static boolean isNewCycle = false;

	public static void main(String[] args) throws IOException, InterruptedException {

		alpacaAccount = new AlpacaApiCaller(alpacaKey, secretId, alpacaKeyValue, secretIdValue, headerContentType,
				headerContentTypeValue, alpacaKeyRealTime, secretIdRealTime, paperUrl, realtimeUrl, activeVersion, inactiveVersion, ordersEndpoint, accountEndpoint, 
				lastQuoteEndpoint, lastTradeEndpoint, positionsEndpoint, assetsEndpoint, clockEndpoint);
		alpacaAccount2 = new AlpacaApiCaller(alpacaKey, secretId, alpacaKeyValueAccount2, secretIdValueAccount2, headerContentType,
				headerContentTypeValue, alpacaKeyRealTime, secretIdRealTime, paperUrl, realtimeUrl, activeVersion, inactiveVersion, ordersEndpoint, accountEndpoint, 
				lastQuoteEndpoint, lastTradeEndpoint, positionsEndpoint, assetsEndpoint, clockEndpoint);
		pw = new PrintWriter(new FileOutputStream(file));
		fo = new FileOutputStream(file);

		if(alpacaAccount.getClock().isIs_open()) {
			followUpTrendWave();
			pw.close();
		}
	}

	public static void followUpTrendWave() throws IOException, InterruptedException {
		do {
			PositionsPojo openPosition = alpacaAccount.getSymbolPosition(symbol);
			if (maxQty.compareTo(Double.valueOf(openPosition.getQty() == null? "0":openPosition.getQty())) > 0) {	
				//priceToBuy = returnCurrentPrice();
				placeBuyOrder = alpacaAccount.buySymbolQuantity(symbol, qty);
				placedBuyOrder = alpacaAccount.getOrderDetails(placeBuyOrder.getId());
				pw.write("Account 1 - Buy Order Executed: " + placedBuyOrder.getSymbol() + "\t" + placedBuyOrder.getQty()+ "\t" + placedBuyOrder.getId() + 
						"\t" + placedBuyOrder.getSide() + "\t" + placedBuyOrder.getFilled_avg_price() + 
						"\t" + LocalDateTime.now().toString() + "\n");
				System.out.println("Account 1 - Buy Order Executed: " + placedBuyOrder.getSymbol() + "\t" + placedBuyOrder.getQty()+ "\t" + placedBuyOrder.getId() + 
						"\t" + placedBuyOrder.getSide() + "\t" + placedBuyOrder.getFilled_avg_price() + 
						"\t" + LocalDateTime.now().toString() + "\n");
				while (!"filled".equals(placedBuyOrder.getStatus()))
				{
					Thread.sleep(100);
					placedBuyOrder = alpacaAccount.getOrderDetails(placeBuyOrder.getId());

				}
				lastBuyOrderPrice = Double.valueOf(placedBuyOrder.getFilled_avg_price());
				placeSellOrder = alpacaAccount.sellSymbolLimitOrder(symbol, qty,  lastBuyOrderPrice*onePointOneFactor);
				placedSellOrder = alpacaAccount.getOrderDetails(placeSellOrder.getId());
				pw.write("Account 1 - Sell Order Executed: " + placedSellOrder.getSymbol() + "\t" + placedSellOrder.getQty()+ "\t" + placedSellOrder.getId() + 
						"\t" + placedSellOrder.getSide() + "\t" + placedSellOrder.getFilled_avg_price() + 
						"\t" + LocalDateTime.now().toString() + "\n");
				System.out.println("Account 1 - Sell Order Executed: " + placedSellOrder.getSymbol() + "\t" + placedSellOrder.getQty()+ "\t" + placedSellOrder.getId() + 
						"\t" + placedSellOrder.getSide() + "\t" + placedSellOrder.getFilled_avg_price() + 
						"\t" + LocalDateTime.now().toString() + "\n");
			}
			else 
			{
				Thread.sleep(100);
				PositionsPojo openBuyPosition = alpacaAccount.getSymbolPosition(symbol);
				Double stopLossPrice = lastBuyOrderPrice*pointFourFactor;
				pw.write("Account 1 - Last Buy Price: " + lastBuyOrderPrice + "\t" + "Stop Loss Price: "+ stopLossPrice + 
						"\t" + "Current Price: " + openBuyPosition.getCurrent_price() + 
						"\t" + LocalDateTime.now().toString() + "\n");
				System.out.println("Account 1 - Last Buy Price: " + lastBuyOrderPrice + "\t" + "Stop Loss Price: "+ stopLossPrice + 
						"\t" + "Current Price: " + openBuyPosition.getCurrent_price() + 
						"\t" + LocalDateTime.now().toString() + "\n");
				if (stopLossPrice.compareTo(Double.valueOf(openBuyPosition.getCurrent_price()))>0) {
					//Cancel all the open Orders for the symbol
					OrdersPojo[] openSymbolOrders = alpacaAccount.getSymbolOpenOrders(symbol);
					for(OrdersPojo openSymbolOrder:openSymbolOrders) {
						alpacaAccount.deleteOrder(openSymbolOrder.getId());
						pw.write("Account 1 - Delete Order Executed: " + openSymbolOrder.getSymbol() + "\t" + openSymbolOrder.getQty()+ "\t" + openSymbolOrder.getId() + 
								"\t" + openSymbolOrder.getStatus() + "\t" + LocalDateTime.now().toString() + "\n");
						System.out.println("Account 1 - Delete Order Executed: " + openSymbolOrder.getSymbol() + "\t" + openSymbolOrder.getQty()+ "\t" + openSymbolOrder.getId() + 
								"\t" + openSymbolOrder.getStatus() + "\t" + LocalDateTime.now().toString() + "\n");
						OrdersPojo canceledOrder = alpacaAccount.getOrderDetails(openSymbolOrder.getId());
						while(!"canceled".equals(canceledOrder.getStatus())) {
							canceledOrder = alpacaAccount.getOrderDetails(openSymbolOrder.getId());
							pw.write("Delete Order Not yet completed: " + canceledOrder.getSymbol() + "\t" + canceledOrder.getQty()+ "\t" + canceledOrder.getId() + 
									"\t" + canceledOrder.getStatus() + "\t" + LocalDateTime.now().toString() + "\n");
						}
					} 

					OrdersPojo sellAllOpenPositions = alpacaAccount.sellSymbolQuantity(symbol, Double.valueOf(openBuyPosition.getQty()));
					while (sellAllOpenPositions.getStatus() !=null && !"filled".equals(sellAllOpenPositions.getStatus()))
					{
						Thread.sleep(100);
						sellAllOpenPositions = alpacaAccount.getOrderDetails(sellAllOpenPositions.getId());
					}
					followDownTrendWave();
				}

				//If its not time to cancel then its time to place the short sell orders in Account2
				PositionsPojo openPosition2 = alpacaAccount2.getSymbolPosition(symbol);
				Double currentPrice = Double.valueOf(openPosition2.getCurrent_price() == null? "0.00":openPosition2.getCurrent_price());
				Double currentQty = Double.valueOf(openPosition2.getQty() == null? "0.00":openPosition2.getQty())*-1;
				if (maxQty.compareTo(currentQty) > 0 && (currentQty.compareTo(-0.00) == 0 || currentPrice.compareTo(lastSellOrderPrice2) < 0)) {	
					placeSellOrder2 = alpacaAccount2.sellSymbolQuantity(symbol, qty);
					if(placeSellOrder2.getId() == null) continue;
					placedSellOrder2 = alpacaAccount2.getOrderDetails(placeSellOrder2.getId());
					pw.write("Account 2 - Short Sell Order Executed: " + placedSellOrder2.getSymbol() + "\t" + placedSellOrder2.getQty()+ "\t" + placedSellOrder2.getId() + 
							"\t" + placedSellOrder2.getSide() + "\t" + placedSellOrder2.getFilled_avg_price() + 
							"\t" + LocalDateTime.now().toString() + "\n");
					System.out.println("Account 2 - Short Sell Order Executed: " + placedSellOrder2.getSymbol() + "\t" + placedSellOrder2.getQty()+ "\t" + placedSellOrder2.getId() + 
							"\t" + placedSellOrder2.getSide() + "\t" + placedSellOrder2.getFilled_avg_price() + 
							"\t" + LocalDateTime.now().toString() + "\n");
					while (!"filled".equals(placedSellOrder2.getStatus()))
					{
						Thread.sleep(100);
						placedSellOrder2 = alpacaAccount2.getOrderDetails(placeSellOrder2.getId());
					}
					lastSellOrderPrice2 = Double.valueOf(placedSellOrder2.getFilled_avg_price());;
					placeBuyOrder2 = alpacaAccount2.buySymbolLimitOrder(symbol, qty,  lastSellOrderPrice2*pointOneFactor);
					placedBuyOrder2 = alpacaAccount2.getOrderDetails(placeBuyOrder2.getId());
					pw.write("Account 2 - Buy Order Executed: " + placedBuyOrder2.getSymbol() + "\t" + placedBuyOrder2.getQty()+ "\t" + placedBuyOrder2.getId() + 
							"\t" + placedBuyOrder2.getSide() + "\t" + placedBuyOrder2.getFilled_avg_price() + 
							"\t" + LocalDateTime.now().toString() + "\n");
					System.out.println("Account 2 - Buy Order Executed: " + placedBuyOrder2.getSymbol() + "\t" + placedBuyOrder2.getQty()+ "\t" + placedBuyOrder2.getId() + 
							"\t" + placedBuyOrder2.getSide() + "\t" + placedBuyOrder2.getFilled_avg_price() + 
							"\t" + LocalDateTime.now().toString() + "\n");
				}
				else {
					//To Introduce - Release the position in second account if the trend is changing again at the average filled price

					if(currentPrice.compareTo(lastSellOrderPrice2*onePointTwoFactor) > 0) {
						OrdersPojo[] openOrders2 = alpacaAccount2.getOpenOrders(symbol);
						int openOrderNumber = openOrders2.length;
						if (openOrderNumber != 0 && openOrders2[0].getLimit_price() != null) {
							Double minSellPrice =Double.valueOf(openOrders2[0].getLimit_price());
							OrdersPojo minSellOrder = openOrders2[0];
							for(OrdersPojo openOrder2 : openOrders2) {
								Double limitPrice = Double.valueOf(openOrder2.getLimit_price());
								if (minSellPrice.compareTo(limitPrice) > 0) {
									minSellPrice = limitPrice;
									minSellOrder = openOrder2;
								}
							}
							if (minSellOrder.getId() != null) {
								//						for (OrdersPojo openOrders: openOrders2) {
								alpacaAccount2.deleteOrder(minSellOrder.getId());
								pw.write("Account 2 - Delete Order Executed: " + minSellOrder.getSymbol() + "\t" + minSellOrder.getQty()+ "\t" + minSellOrder.getId() + 
										"\t" + minSellOrder.getStatus() + "\t" + LocalDateTime.now().toString() + "\n");
								System.out.println("Account 2 - Delete Order Executed: " + minSellOrder.getSymbol() + "\t" + minSellOrder.getQty()+ "\t" + minSellOrder.getId() + 
										"\t" + minSellOrder.getStatus() + "\t" + LocalDateTime.now().toString() + "\n");
								Thread.sleep(1000);
							}
							openPosition2 = alpacaAccount2.getSymbolPosition(symbol);
							if(openPosition2.getQty() != null) {
								placeBuyOrder2 = alpacaAccount2.buySymbolQuantity(symbol, Double.valueOf(openPosition2.getQty())*-1);
								placedBuyOrder2 = alpacaAccount2.getOrderDetails(placeBuyOrder2.getId());
								pw.write("Account 2 - Buy Order Executed: " + placedBuyOrder2.getSymbol() + "\t" + placedBuyOrder2.getQty()+ "\t" + placedBuyOrder2.getId() + 
										"\t" + placedBuyOrder2.getSide() + "\t" + placedBuyOrder2.getFilled_avg_price() + 
										"\t" + LocalDateTime.now().toString() + "\n");
								System.out.println("Account 2 - Buy Order Executed: " + placedBuyOrder2.getSymbol() + "\t" + placedBuyOrder2.getQty()+ "\t" + placedBuyOrder2.getId() + 
										"\t" + placedBuyOrder2.getSide() + "\t" + placedBuyOrder2.getFilled_avg_price() + 
										"\t" + LocalDateTime.now().toString() + "\n");
							}
						}
					}
				}
			}
		}while(LocalDateTime.now().isBefore(marketCloseTime));
	}
	public static void followDownTrendWave() throws IOException, InterruptedException {
		do {
			PositionsPojo openPosition = alpacaAccount.getSymbolPosition(symbol);
			if (maxQty.compareTo(Double.valueOf(openPosition.getQty() == null? "0":openPosition.getQty())*-1) > 0) {	
				placeSellOrder = alpacaAccount.sellSymbolQuantity(symbol, qty);
				placedSellOrder = alpacaAccount.getOrderDetails(placeSellOrder.getId());
				pw.write("Account 1 - Short Sell Order Executed: " + placedSellOrder.getSymbol() + "\t" + placedSellOrder.getQty()+ "\t" + placedSellOrder.getId() + 
						"\t" + placedSellOrder.getSide() + "\t" + placedSellOrder.getFilled_avg_price() + 
						"\t" + LocalDateTime.now().toString() + "\n");
				System.out.println("Account 1 - Short Sell Order Executed: " + placedSellOrder.getSymbol() + "\t" + placedSellOrder.getQty()+ "\t" + placedSellOrder.getId() + 
						"\t" + placedSellOrder.getSide() + "\t" + placedSellOrder.getFilled_avg_price() + 
						"\t" + LocalDateTime.now().toString() + "\n");
				while (!"filled".equals(placedSellOrder.getStatus()))
				{
					Thread.sleep(100);
					placedSellOrder = alpacaAccount.getOrderDetails(placeSellOrder.getId());
				}
				lastSellOrderPrice = Double.valueOf(placedSellOrder.getFilled_avg_price());;
				placeBuyOrder = alpacaAccount.buySymbolLimitOrder(symbol, qty,  lastSellOrderPrice*pointOneFactor);
				placedBuyOrder = alpacaAccount.getOrderDetails(placeBuyOrder.getId());
				pw.write("Account 1 - Buy Order Executed: " + placedBuyOrder.getSymbol() + "\t" + placedBuyOrder.getQty()+ "\t" + placedBuyOrder.getId() + 
						"\t" + placedBuyOrder.getSide() + "\t" + placedBuyOrder.getFilled_avg_price() + 
						"\t" + LocalDateTime.now().toString() + "\n");
				System.out.println("Account 1 - Buy Order Executed: " + placedBuyOrder.getSymbol() + "\t" + placedBuyOrder.getQty()+ "\t" + placedBuyOrder.getId() + 
						"\t" + placedBuyOrder.getSide() + "\t" + placedBuyOrder.getFilled_avg_price() + 
						"\t" + LocalDateTime.now().toString() + "\n");
			}
			else 
			{
				Thread.sleep(100);
				PositionsPojo openSellPosition = alpacaAccount.getSymbolPosition(symbol);
				Double stopLossPrice = lastSellOrderPrice*onePointFourFactor;
				pw.write("Account 1 - Last Buy Price: " + lastBuyOrderPrice + "\t" + "Stop Loss Price: "+ stopLossPrice + 
						"\t" + "Current Price: " + openSellPosition.getCurrent_price() + 
						"\t" + LocalDateTime.now().toString() + "\n");
				System.out.println("Account 1 - Last Buy Price: " + lastBuyOrderPrice + "\t" + "Stop Loss Price: "+ stopLossPrice + 
						"\t" + "Current Price: " + openSellPosition.getCurrent_price() + 
						"\t" + LocalDateTime.now().toString() + "\n");
				if (stopLossPrice.compareTo(Double.valueOf(openSellPosition.getCurrent_price()))<0) {
					//Cancel all the open Orders for the symbol
					OrdersPojo[] openSymbolOrders = alpacaAccount.getSymbolOpenOrders(symbol);
					for(OrdersPojo openSymbolOrder:openSymbolOrders) {
						alpacaAccount.deleteOrder(openSymbolOrder.getId());
						pw.write("Account 1 - Delete Order Executed: " + openSymbolOrder.getSymbol() + "\t" + openSymbolOrder.getQty()+ "\t" + openSymbolOrder.getId() + 
								"\t" + openSymbolOrder.getStatus() + "\t" + LocalDateTime.now().toString() + "\n");
						System.out.println("Account 1 - Delete Order Executed: " + openSymbolOrder.getSymbol() + "\t" + openSymbolOrder.getQty()+ "\t" + openSymbolOrder.getId() + 
								"\t" + openSymbolOrder.getStatus() + "\t" + LocalDateTime.now().toString() + "\n");
						OrdersPojo canceledOrder = alpacaAccount.getOrderDetails(openSymbolOrder.getId());
						while(!"canceled".equals(canceledOrder.getStatus())) {
							canceledOrder = alpacaAccount.getOrderDetails(openSymbolOrder.getId());
							pw.write("Delete Order Not yet completed: " + canceledOrder.getSymbol() + "\t" + canceledOrder.getQty()+ "\t" + canceledOrder.getId() + 
									"\t" + canceledOrder.getStatus() + "\t" + LocalDateTime.now().toString() + "\n");
						}
					} 

					OrdersPojo buyAllOpenPositions = alpacaAccount.buySymbolQuantity(symbol, Double.valueOf(openSellPosition.getQty()));
					while (buyAllOpenPositions.getStatus() !=null && !"filled".equals(buyAllOpenPositions.getStatus()))
					{
						Thread.sleep(100);
						buyAllOpenPositions = alpacaAccount.getOrderDetails(buyAllOpenPositions.getId());
					}
					//buyFraction = false;
					//Return after closing all positions for up-trend
					return;
				}

				//If its not time to cancel then its time to buy in account2
				PositionsPojo openPosition2 = alpacaAccount2.getSymbolPosition(symbol);
				Double currentPrice = Double.valueOf(openPosition2.getCurrent_price() == null? "0.00":openPosition2.getCurrent_price());
				Double currentQty = Double.valueOf(openPosition2.getQty() == null? "0.00":openPosition2.getQty());
				if (maxQty.compareTo(currentQty) > 0 && (currentQty.compareTo(0.00) == 0 || currentPrice.compareTo(lastBuyOrderPrice2) > 0)) {	
					//priceToBuy = returnCurrentPrice();
					placeBuyOrder2 = alpacaAccount2.buySymbolQuantity(symbol, qty);
					if (placeBuyOrder2.getId() == null) continue;
					placedBuyOrder2 = alpacaAccount2.getOrderDetails(placeBuyOrder2.getId());
					pw.write("Account 2 - Buy Order Executed: " + placedBuyOrder2.getSymbol() + "\t" + placedBuyOrder2.getQty()+ "\t" + placedBuyOrder2.getId() + 
							"\t" + placedBuyOrder2.getSide() + "\t" + placedBuyOrder2.getFilled_avg_price() + 
							"\t" + LocalDateTime.now().toString() + "\n");
					System.out.println("Account 2 - Buy Order Executed: " + placedBuyOrder2.getSymbol() + "\t" + placedBuyOrder2.getQty()+ "\t" + placedBuyOrder2.getId() + 
							"\t" + placedBuyOrder2.getSide() + "\t" + placedBuyOrder2.getFilled_avg_price() + 
							"\t" + LocalDateTime.now().toString() + "\n");
					while (!"filled".equals(placedBuyOrder2.getStatus()))
					{
						Thread.sleep(100);
						placedBuyOrder2 = alpacaAccount2.getOrderDetails(placeBuyOrder2.getId());

					}
					lastBuyOrderPrice2 = Double.valueOf(placedBuyOrder2.getFilled_avg_price());
					placeSellOrder2 = alpacaAccount2.sellSymbolLimitOrder(symbol, qty,  lastBuyOrderPrice2*onePointOneFactor);
					placedSellOrder2 = alpacaAccount2.getOrderDetails(placeSellOrder2.getId());
					pw.write("Account 2 - Sell Order Executed: " + placedSellOrder2.getSymbol() + "\t" + placedSellOrder2.getQty()+ "\t" + placedSellOrder2.getId() + 
							"\t" + placedSellOrder2.getSide() + "\t" + placedSellOrder2.getFilled_avg_price() + 
							"\t" + LocalDateTime.now().toString() + "\n");
					System.out.println("Account 2 - Sell Order Executed: " + placedSellOrder2.getSymbol() + "\t" + placedSellOrder2.getQty()+ "\t" + placedSellOrder2.getId() + 
							"\t" + placedSellOrder2.getSide() + "\t" + placedSellOrder2.getFilled_avg_price() + 
							"\t" + LocalDateTime.now().toString() + "\n");
				}
				else 
				{
					//To Introduce - Release the position in second account if the trend is changing again at the average filled price

					if(currentPrice.compareTo(lastBuyOrderPrice2*pointTwoFactor) < 0) {
						OrdersPojo[] openOrders2 = alpacaAccount2.getOpenOrders(symbol);
						int openOrderNumber = openOrders2.length;
						if (openOrderNumber != 0 && openOrders2[0].getLimit_price() != null) {
							Double maxBuyPrice =Double.valueOf(openOrders2[0].getLimit_price());
							OrdersPojo maxBuyOrder = openOrders2[0];
							for(OrdersPojo openOrder2 : openOrders2) {
								Double limitPrice = Double.valueOf(openOrder2.getLimit_price());
								if (maxBuyPrice.compareTo(limitPrice) < 0) {
									maxBuyPrice = limitPrice;
									maxBuyOrder = openOrder2;
								}
							}
							if (maxBuyOrder.getId() != null) {
								//						for (OrdersPojo openOrders: openOrders2) {
								alpacaAccount2.deleteOrder(maxBuyOrder.getId());
								pw.write("Account 2 - Delete Order Executed: " + maxBuyOrder.getSymbol() + "\t" + maxBuyOrder.getQty()+ "\t" + maxBuyOrder.getId() + 
										"\t" + maxBuyOrder.getStatus() + "\t" + LocalDateTime.now().toString() + "\n");
								System.out.println("Account 2 - Delete Order Executed: " + maxBuyOrder.getSymbol() + "\t" + maxBuyOrder.getQty()+ "\t" + maxBuyOrder.getId() + 
										"\t" + maxBuyOrder.getStatus() + "\t" + LocalDateTime.now().toString() + "\n");
								Thread.sleep(1000);
							}
							openPosition2 = alpacaAccount2.getSymbolPosition(symbol);
							//placeBuyOrder2 = alpacaAccount2.buySymbolQuantity(symbol, Double.valueOf(openPosition2.getQty()));
							if(openPosition2.getQty() != null) {
								placeSellOrder2 = alpacaAccount2.sellSymbolQuantity(symbol, Double.valueOf(openPosition2.getQty()));
								placedSellOrder2 = alpacaAccount2.getOrderDetails(placeSellOrder2.getId());
								pw.write("Account 2 - Sell Order Executed: " + placedSellOrder2.getSymbol() + "\t" + placedSellOrder2.getQty()+ "\t" + placedSellOrder2.getId() + 
										"\t" + placedSellOrder2.getSide() + "\t" + placedSellOrder2.getFilled_avg_price() + 
										"\t" + LocalDateTime.now().toString() + "\n");
								System.out.println("Account 2 - Sell Order Executed: " + placedSellOrder2.getSymbol() + "\t" + placedSellOrder2.getQty()+ "\t" + placedSellOrder2.getId() + 
										"\t" + placedSellOrder2.getSide() + "\t" + placedSellOrder2.getFilled_avg_price() + 
										"\t" + LocalDateTime.now().toString() + "\n");
							}
						}
					}
				}
			}
		}while(LocalDateTime.now().isBefore(marketCloseTime));
	}
}