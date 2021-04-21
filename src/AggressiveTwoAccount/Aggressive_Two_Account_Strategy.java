package AggressiveTwoAccount;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;

import alpacaPojo.OrdersPojo;
import alpacaPojo.PositionsPojo;

public class Aggressive_Two_Account_Strategy {

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

	static Double qty = 2.00;
	static Double maxQty = 35*qty;
	static Double pointZeroOneFactor = qty/1000;
	static Double pointOneFactor = 0.999;
	static Double pointTwoFactor = 0.998;
	static Double onePointTwoFactor = 1.002;
	static Double onePointOneFactor = 1.001;
	static int agressiveBuyQuantity = 1, agressiveSellQuantity = 1;
	static int maxDoubleUp = 64;

	static String symbol = "SNAP";
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
	
	static Double openingBalance = 0.00, openingBalance2 = 0.00, exitProfitValue = 51.0;

	public static void main(String[] args) throws IOException, InterruptedException {

		alpacaAccount = new AlpacaApiCaller(alpacaKey, secretId, alpacaKeyValue, secretIdValue, headerContentType,
				headerContentTypeValue, alpacaKeyRealTime, secretIdRealTime, paperUrl, realtimeUrl, activeVersion, inactiveVersion, ordersEndpoint, accountEndpoint, 
				lastQuoteEndpoint, lastTradeEndpoint, positionsEndpoint, assetsEndpoint, clockEndpoint);
		alpacaAccount2 = new AlpacaApiCaller(alpacaKey, secretId, alpacaKeyValueAccount2, secretIdValueAccount2, headerContentType,
				headerContentTypeValue, alpacaKeyRealTime, secretIdRealTime, paperUrl, realtimeUrl, activeVersion, inactiveVersion, ordersEndpoint, accountEndpoint, 
				lastQuoteEndpoint, lastTradeEndpoint, positionsEndpoint, assetsEndpoint, clockEndpoint);
		pw = new PrintWriter(new FileOutputStream(file));
		fo = new FileOutputStream(file);
		PositionsPojo openPosition = null;
		PositionsPojo openPosition2 = null;
		Double currentPrice = null;
		Double netProfit = 0.00;
		openingBalance = Double.valueOf(alpacaAccount.getAccountDetails().getEquity());
		openingBalance2 = Double.valueOf(alpacaAccount2.getAccountDetails().getEquity());
		if(alpacaAccount.getClock().isIs_open()) {
			while(LocalDateTime.now().isBefore(marketCloseTime)) {
				Thread.sleep(15000);
				netProfit = calculateNetProfit();
				if(netProfit.compareTo(exitProfitValue) > 0) {
					System.out.println("Net profit is coming out to be more than Set Profit Value so freeing all positions." + "\n");
					pw.write("Net profit is coming out to be more than Set Profit Value so freeing all positions." + "\n");
					freeAllPositions();
				}
				//Sell in One account and sell in another
				openPosition2 = alpacaAccount2.getSymbolPosition(symbol);
				currentPrice = Double.valueOf(openPosition2.getQty() == null? "0.0":openPosition2.getCurrent_price());
				if (currentPrice.compareTo(0.0) != 0 && currentPrice.compareTo(lastSellOrderPrice*onePointOneFactor) > 0) {
					followDownTrendWave(qty*agressiveSellQuantity, pointOneFactor);
					agressiveSellQuantity = agressiveSellQuantity*2;
					if (agressiveSellQuantity == maxDoubleUp) agressiveSellQuantity = 1;
				}
				followUpTrendWave(qty, onePointTwoFactor);
				Thread.sleep(15000);
				openPosition = alpacaAccount.getSymbolPosition(symbol);
				currentPrice = Double.valueOf(openPosition.getQty() == null? "0.0":openPosition.getCurrent_price());
				if (currentPrice.compareTo(0.0) != 0 && currentPrice.compareTo(lastBuyOrderPrice*pointOneFactor) < 0) {
					followUpTrendWave(qty*agressiveBuyQuantity, onePointOneFactor);
					agressiveBuyQuantity = agressiveBuyQuantity*2;
					if (agressiveBuyQuantity == maxDoubleUp) agressiveBuyQuantity = 1;
				}
				followDownTrendWave(qty, pointTwoFactor);
				pw.close();
			}
		}
	}

	public static void followUpTrendWave(Double quantity, Double sellProfitFactor) throws IOException, InterruptedException {
		PositionsPojo openPosition = alpacaAccount.getSymbolPosition(symbol);
		if (maxQty.compareTo(Double.valueOf(openPosition.getQty() == null? "0":openPosition.getQty())) > 0) {	
			//priceToBuy = returnCurrentPrice();
			placeBuyOrder = alpacaAccount.buySymbolQuantity(symbol, quantity);
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
			placeSellOrder = alpacaAccount.sellSymbolLimitOrder(symbol, quantity,  lastBuyOrderPrice*sellProfitFactor);
			placedSellOrder = alpacaAccount.getOrderDetails(placeSellOrder.getId());
			pw.write("Account 1 - Sell Order Executed: " + placedSellOrder.getSymbol() + "\t" + placedSellOrder.getQty()+ "\t" + placedSellOrder.getId() + 
					"\t" + placedSellOrder.getSide() + "\t" + placedSellOrder.getFilled_avg_price() + 
					"\t" + LocalDateTime.now().toString() + "\n");
			System.out.println("Account 1 - Sell Order Executed: " + placedSellOrder.getSymbol() + "\t" + placedSellOrder.getQty()+ "\t" + placedSellOrder.getId() + 
					"\t" + placedSellOrder.getSide() + "\t" + placedSellOrder.getFilled_avg_price() + 
					"\t" + LocalDateTime.now().toString() + "\n");
		}
	}
	public static void followDownTrendWave(Double quantity, Double buyProfitFactor) throws IOException, InterruptedException {
		PositionsPojo openPosition2 = alpacaAccount2.getSymbolPosition(symbol);
		if (maxQty.compareTo(Double.valueOf(openPosition2.getQty() == null? "0":openPosition2.getQty())*-1) > 0) {	
			placeSellOrder2 = alpacaAccount2.sellSymbolQuantity(symbol, quantity);
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
			lastSellOrderPrice = Double.valueOf(placedSellOrder2.getFilled_avg_price());;
			placeBuyOrder2 = alpacaAccount2.buySymbolLimitOrder(symbol, quantity,  lastSellOrderPrice*buyProfitFactor);
			placedBuyOrder2 = alpacaAccount2.getOrderDetails(placeBuyOrder2.getId());
			pw.write("Account 2 - Buy Order Executed: " + placedBuyOrder2.getSymbol() + "\t" + placedBuyOrder2.getQty()+ "\t" + placedBuyOrder2.getId() + 
					"\t" + placedBuyOrder2.getSide() + "\t" + placedBuyOrder2.getFilled_avg_price() + 
					"\t" + LocalDateTime.now().toString() + "\n");
			System.out.println("Account 2 - Buy Order Executed: " + placedBuyOrder2.getSymbol() + "\t" + placedBuyOrder2.getQty()+ "\t" + placedBuyOrder2.getId() + 
					"\t" + placedBuyOrder2.getSide() + "\t" + placedBuyOrder2.getFilled_avg_price() + 
					"\t" + LocalDateTime.now().toString() + "\n");
		}
	}
	
	public static void freeAllPositions() throws IOException, InterruptedException {
		//Cancel all orders in Account2 and Buy all the short positions
		PositionsPojo openPosition2 = alpacaAccount2.getSymbolPosition(symbol);
		OrdersPojo[] openSymbolOrders2 = alpacaAccount2.getSymbolOpenOrders(symbol);
		if(openPosition2.getQty() != null) {
			for(OrdersPojo openSymbolOrder:openSymbolOrders2) {
				alpacaAccount2.deleteOrder(openSymbolOrder.getId());
				pw.write("Delete Order Executed with following Details: " + openSymbolOrder.getSymbol() + "\t" + openSymbolOrder.getQty()+ "\t" + openSymbolOrder.getId() + 
						"\t" + openSymbolOrder.getStatus() + "\t" + LocalDateTime.now().toString() + "\n");
				OrdersPojo canceledOrder = alpacaAccount2.getOrderDetails(openSymbolOrder.getId());
				while(!"canceled".equals(canceledOrder.getStatus())) {
					if("filled".equals(canceledOrder.getStatus())) break;
					canceledOrder = alpacaAccount2.getOrderDetails(openSymbolOrder.getId());
					pw.write("Delete Order Not yet completed: " + canceledOrder.getSymbol() + "\t" + canceledOrder.getQty()+ "\t" + canceledOrder.getId() + 
							"\t" + canceledOrder.getStatus() + "\t" + LocalDateTime.now().toString() + "\n");
					System.out.println("Delete Order Not yet completed: " + canceledOrder.getSymbol() + "\t" + canceledOrder.getQty()+ "\t" + canceledOrder.getId() + 
							"\t" + canceledOrder.getStatus() + "\t" + LocalDateTime.now().toString() + "\n");
				}
			}
			PositionsPojo openSellPosition = alpacaAccount2.getSymbolPosition(symbol);
			if(openSellPosition != null) {
				OrdersPojo buyAllOpenPositions = alpacaAccount2.buySymbolQuantity(symbol, Double.valueOf(openSellPosition.getQty())*-1);
				while (buyAllOpenPositions.getStatus() !=null && !"filled".equals(buyAllOpenPositions.getStatus()))
				{
					Thread.sleep(100);
					buyAllOpenPositions = alpacaAccount2.getOrderDetails(buyAllOpenPositions.getId());
					System.out.println("Buying All Open Positions" + "\n");
				}
			}
		}
		
		//Cancel All orders in account one and sell everything
		PositionsPojo openPosition = alpacaAccount.getSymbolPosition(symbol);
		OrdersPojo[] openSymbolOrders = alpacaAccount.getSymbolOpenOrders(symbol);
		if(openPosition.getQty() != null) {
			for(OrdersPojo openSymbolOrder:openSymbolOrders) {
				alpacaAccount.deleteOrder(openSymbolOrder.getId());
				pw.write("Delete Order Executed with following Details: " + openSymbolOrder.getSymbol() + "\t" + openSymbolOrder.getQty()+ "\t" + openSymbolOrder.getId() + 
						"\t" + openSymbolOrder.getStatus() + "\t" + LocalDateTime.now().toString() + "\n");
				System.out.println("Delete Order Executed with following Details: " + openSymbolOrder.getSymbol() + "\t" + openSymbolOrder.getQty()+ "\t" + openSymbolOrder.getId() + 
						"\t" + openSymbolOrder.getStatus() + "\t" + LocalDateTime.now().toString() + "\n");
				OrdersPojo canceledOrder = alpacaAccount.getOrderDetails(openSymbolOrder.getId());
				while(!"canceled".equals(canceledOrder.getStatus())) {
					if("filled".equals(canceledOrder.getStatus())) break;
					canceledOrder = alpacaAccount.getOrderDetails(openSymbolOrder.getId());
					pw.write("Delete Order Not yet completed: " + canceledOrder.getSymbol() + "\t" + canceledOrder.getQty()+ "\t" + canceledOrder.getId() + 
							"\t" + canceledOrder.getStatus() + "\t" + LocalDateTime.now().toString() + "\n");
					System.out.println("Delete Order Not yet completed: " + canceledOrder.getSymbol() + "\t" + canceledOrder.getQty()+ "\t" + canceledOrder.getId() + 
							"\t" + canceledOrder.getStatus() + "\t" + LocalDateTime.now().toString() + "\n");
				}
			}
			PositionsPojo openBuyPosition = alpacaAccount.getSymbolPosition(symbol);
			if (openBuyPosition.getQty() != null) {
				OrdersPojo sellAllOpenPositions = alpacaAccount.sellSymbolQuantity(symbol, Double.valueOf(openBuyPosition.getQty()));
				while (sellAllOpenPositions.getStatus() !=null && !"filled".equals(sellAllOpenPositions.getStatus()))
				{
					Thread.sleep(100);
					sellAllOpenPositions = alpacaAccount.getOrderDetails(sellAllOpenPositions.getId());
					System.out.println("Selling All Open Positions" + "\n");
				}
			}
		}
	}
	public static Double calculateNetProfit() throws IOException, InterruptedException {
		Double profitAccount1 = 0.00, profitAccount2 = 0.00;
		Double equityAccount1 = 0.00, equityAccount2 = 0.00;
		equityAccount1 = Double.valueOf(alpacaAccount.getAccountDetails().getEquity());
		equityAccount2 = Double.valueOf(alpacaAccount2.getAccountDetails().getEquity());
		PositionsPojo openPosition = alpacaAccount.getSymbolPosition(symbol);
		PositionsPojo openPosition2 = alpacaAccount2.getSymbolPosition(symbol);
		
		Double netMarketBuy = 0.00, netMarketSell = 0.00;
		if (openPosition.getQty() != null) {
			netMarketBuy = Double.valueOf(openPosition.getQty())*Double.valueOf(openPosition.getCurrent_price());
		}	
		if (openPosition2.getQty() != null) {
			netMarketSell = Double.valueOf(openPosition.getQty())*Double.valueOf(openPosition.getCurrent_price());
		}
		
		profitAccount1 = equityAccount1 - openingBalance - netMarketBuy;
		profitAccount2 = equityAccount2 - openingBalance2 + netMarketSell;
			
		return profitAccount1 + profitAccount2;
	}
}