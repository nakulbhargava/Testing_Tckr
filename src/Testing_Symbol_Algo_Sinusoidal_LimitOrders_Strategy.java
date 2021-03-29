import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;

import alpacaPojo.OrdersPojo;
import alpacaPojo.PositionsPojo;

public class Testing_Symbol_Algo_Sinusoidal_LimitOrders_Strategy {

	private static final String alpacaKey = "APCA-API-KEY-ID";
	private static final String secretId = "APCA-API-SECRET-KEY";
	private static final String alpacaKeyValue = "PKXTC9CIEKM9SXKWR7CN";
	private static final String secretIdValue = "EWFyZnPGoQyTbSfPAcLO02ao5n6MXSvdbaQr19Y9";
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
	private static final LocalDateTime marketCloseTime = LocalDate.now().atTime(14,25);
	//private boolean isRealTimeTrading = false;
	static boolean buyFraction = true;
	static Double qty = 10.00;
	static Double maxQty = (qty*10) + 1;
	static Double pointZeroOneFactor = qty/1000;
	static Double priceToBuy = 0.00;
	static Double priceToSell = 0.0000;
	static Double pointOneFactor = 0.999;
	//static Double pointTwoFactor = .998;
	static Double onePointFourFactor = 1.004;
	static Double onePointOneFactor = 1.001;
	static Double pointFourFactor = .996;
	static boolean timeToSell = false;
	static String symbol = "ARKK";
	static AlpacaApiCaller alpacaAccount;
	static OrdersPojo placeBuyOrder;
	static OrdersPojo placedBuyOrder;
	static OrdersPojo placeSellOrder;
	static OrdersPojo placedSellOrder;
	static int secondsCounter = 0;
	static String localTime = String.valueOf(LocalDateTime.now().getDayOfYear()) + String.valueOf(LocalDateTime.now().getHour()) + String.valueOf(LocalDateTime.now().getMinute()) + String.valueOf(LocalDateTime.now().getSecond());
	static File file = new File("C:\\Users\\nakul\\Documents\\output - " + symbol + " - " + localTime + ".txt");
	static PrintWriter pw; 
	static FileOutputStream fo;
	static Double lastBuyOrderPrice = 0.00;
	static Double lastSellOrderPrice = 0.00;

	public static void main(String[] args) throws IOException, InterruptedException {

		alpacaAccount = new AlpacaApiCaller(alpacaKey, secretId, alpacaKeyValue, secretIdValue, headerContentType,
				headerContentTypeValue, alpacaKeyRealTime, secretIdRealTime, paperUrl, realtimeUrl, activeVersion, inactiveVersion, ordersEndpoint, accountEndpoint, 
				lastQuoteEndpoint, lastTradeEndpoint, positionsEndpoint, assetsEndpoint, clockEndpoint);
		pw = new PrintWriter(new FileOutputStream(file));
		fo = new FileOutputStream(file);

		if(alpacaAccount.getClock().isIs_open()) {
			do {
				PositionsPojo openPosition = alpacaAccount.getSymbolPosition(symbol);
				if (maxQty.compareTo(Double.valueOf(openPosition.getQty() == null? "0":openPosition.getQty())) > 0) {	
					priceToBuy = returnCurrentPrice();
					placeBuyOrder = alpacaAccount.buySymbolLimitOrder(symbol, qty, priceToBuy);
					placedBuyOrder = alpacaAccount.getOrderDetails(placeBuyOrder.getId());
					pw.write("Buy Order Executed with following Details: " + placedBuyOrder.getSymbol() + "\t" + placedBuyOrder.getQty()+ "\t" + placedBuyOrder.getId() + 
							"\t" + placedBuyOrder.getSide() + "\t" + placedBuyOrder.getFilled_avg_price() + 
							"\t" + LocalDateTime.now().toString() + "\n");
					secondsCounter = 0; 
					while (!"filled".equals(placedBuyOrder.getStatus()) && secondsCounter < 10)
					{
						Thread.sleep(1000);
						placedBuyOrder = alpacaAccount.getOrderDetails(placeBuyOrder.getId());
						secondsCounter++;
					}
					//If Buy order is not placed till time = 10sec then its an uptrend, lets try to buy at higher current buy price and repeat. No Need to wait and No need to place a sell order as we will cancel the corresponding buy order.
					if (!"filled".equals(placedBuyOrder.getStatus()) && secondsCounter >= 10) {
						//Cancel the buy Order
						alpacaAccount.deleteOrder(placeBuyOrder.getId());
						OrdersPojo deletedOrder = alpacaAccount.getOrderDetails(placeBuyOrder.getId());
						pw.write("Delete Order Executed with following Details: " + deletedOrder.getSymbol() + "\t" + deletedOrder.getQty()+ "\t" + deletedOrder.getId() + 
								"\t" + deletedOrder.getStatus() + "\t" + LocalDateTime.now().toString() + "\n");
						while (!"canceled".equals(deletedOrder.getStatus()))
						{
							Thread.sleep(100);
							deletedOrder = alpacaAccount.getOrderDetails(placeBuyOrder.getId());
						}
						pw.write("Order Canceled with following Details: " + deletedOrder.getSymbol() + "\t" + deletedOrder.getQty()+ "\t" + deletedOrder.getId() + 
								"\t" + deletedOrder.getStatus() + "\t" + LocalDateTime.now().toString() + "\n");
						continue;
					}
					lastBuyOrderPrice = priceToBuy;
					placeSellOrder = alpacaAccount.sellSymbolLimitOrder(symbol, qty,  priceToBuy*onePointOneFactor);
					placedSellOrder = alpacaAccount.getOrderDetails(placeSellOrder.getId());
					pw.write("Sell Order Executed with following Details: " + placedSellOrder.getSymbol() + "\t" + placedSellOrder.getQty()+ "\t" + placedSellOrder.getId() + 
							"\t" + placedSellOrder.getSide() + "\t" + placedSellOrder.getFilled_avg_price() + 
							"\t" + LocalDateTime.now().toString() + "\n");
				}
				else 
				{
					Thread.sleep(1000);
					PositionsPojo openBuyPosition = alpacaAccount.getSymbolPosition(symbol);
					Double stopLossPrice = lastBuyOrderPrice*pointFourFactor;
					pw.write("Last Buy Price: " + lastBuyOrderPrice + "\t" + "Stop Loss Price: "+ stopLossPrice + 
							"\t" + "Current Price: " + openBuyPosition.getCurrent_price() + 
							"\t" + LocalDateTime.now().toString() + "\n");
					if (stopLossPrice.compareTo(Double.valueOf(openBuyPosition.getCurrent_price()))>0) {
						//Cancel all the open Orders for the symbol
						OrdersPojo[] openSymbolOrders = alpacaAccount.getSymbolOpenOrders(symbol);
						for(OrdersPojo openSymbolOrder:openSymbolOrders) {
							alpacaAccount.deleteOrder(openSymbolOrder.getId());
							pw.write("Delete Order Executed with following Details: " + openSymbolOrder.getSymbol() + "\t" + openSymbolOrder.getQty()+ "\t" + openSymbolOrder.getId() + 
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
						buyFraction = true;
					}
				}
			}while(LocalDateTime.now().isBefore(marketCloseTime));
			pw.close();
		}
	}

	public static Double returnCurrentPrice() throws IOException, InterruptedException {
		Double currentPrice = 0.00;
		//		if(buyFraction)
		//		{
		OrdersPojo placeBuyOrder = alpacaAccount.buySymbolQuantity(symbol, pointZeroOneFactor);
		OrdersPojo placedBuyOrder = alpacaAccount.getOrderDetails(placeBuyOrder.getId());
		while (!"filled".equals(placedBuyOrder.getStatus()))
		{
			placedBuyOrder = alpacaAccount.getOrderDetails(placeBuyOrder.getId());
		}
		currentPrice = Double.valueOf(placedBuyOrder.getFilled_avg_price());
		//			buyFraction = false;
		//			return currentPrice;
		//		}
		//		else
		//		{
		OrdersPojo placeSellOrder = alpacaAccount.sellSymbolQuantity(symbol, pointZeroOneFactor);
		OrdersPojo placedSellOrder = alpacaAccount.getOrderDetails(placeSellOrder.getId());
		while (!"filled".equals(placedSellOrder.getStatus()))
		{
			placedSellOrder = alpacaAccount.getOrderDetails(placedSellOrder.getId());
		}
		currentPrice = Double.valueOf(placedSellOrder.getFilled_avg_price());
		//			buyFraction = true;
		return currentPrice;
	}

	public static void followUpTrendWave() throws IOException, InterruptedException {
		do {
			PositionsPojo openPosition = alpacaAccount.getSymbolPosition(symbol);
			if (maxQty.compareTo(Double.valueOf(openPosition.getQty() == null? "0":openPosition.getQty())) > 0) {	
				priceToBuy = returnCurrentPrice();
				placeBuyOrder = alpacaAccount.buySymbolLimitOrder(symbol, qty, priceToBuy);
				placedBuyOrder = alpacaAccount.getOrderDetails(placeBuyOrder.getId());
				pw.write("Buy Order Executed with following Details: " + placedBuyOrder.getSymbol() + "\t" + placedBuyOrder.getQty()+ "\t" + placedBuyOrder.getId() + 
						"\t" + placedBuyOrder.getSide() + "\t" + placedBuyOrder.getFilled_avg_price() + 
						"\t" + LocalDateTime.now().toString() + "\n");
				secondsCounter = 0; 
				while (!"filled".equals(placedBuyOrder.getStatus()) && secondsCounter < 10)
				{
					Thread.sleep(1000);
					placedBuyOrder = alpacaAccount.getOrderDetails(placeBuyOrder.getId());
					secondsCounter++;
				}
				//If Buy order is not placed till time = 10sec then its an uptrend, lets try to buy at higher current buy price and repeat. No Need to wait and No need to place a sell order as we will cancel the corresponding buy order.
				if (!"filled".equals(placedBuyOrder.getStatus()) && secondsCounter >= 10) {
					//Cancel the buy Order
					alpacaAccount.deleteOrder(placeBuyOrder.getId());
					OrdersPojo deletedOrder = alpacaAccount.getOrderDetails(placeBuyOrder.getId());
					pw.write("Delete Order Executed with following Details: " + deletedOrder.getSymbol() + "\t" + deletedOrder.getQty()+ "\t" + deletedOrder.getId() + 
							"\t" + deletedOrder.getStatus() + "\t" + LocalDateTime.now().toString() + "\n");
					while (!"canceled".equals(deletedOrder.getStatus()))
					{
						Thread.sleep(100);
						deletedOrder = alpacaAccount.getOrderDetails(placeBuyOrder.getId());
					}
					pw.write("Order Canceled with following Details: " + deletedOrder.getSymbol() + "\t" + deletedOrder.getQty()+ "\t" + deletedOrder.getId() + 
							"\t" + deletedOrder.getStatus() + "\t" + LocalDateTime.now().toString() + "\n");
					continue;
				}
				lastBuyOrderPrice = priceToBuy;
				placeSellOrder = alpacaAccount.sellSymbolLimitOrder(symbol, qty,  priceToBuy*onePointOneFactor);
				placedSellOrder = alpacaAccount.getOrderDetails(placeSellOrder.getId());
				pw.write("Sell Order Executed with following Details: " + placedSellOrder.getSymbol() + "\t" + placedSellOrder.getQty()+ "\t" + placedSellOrder.getId() + 
						"\t" + placedSellOrder.getSide() + "\t" + placedSellOrder.getFilled_avg_price() + 
						"\t" + LocalDateTime.now().toString() + "\n");
			}
			else 
			{
				Thread.sleep(1000);
				PositionsPojo openBuyPosition = alpacaAccount.getSymbolPosition(symbol);
				Double stopLossPrice = lastBuyOrderPrice*pointFourFactor;
				pw.write("Last Buy Price: " + lastBuyOrderPrice + "\t" + "Stop Loss Price: "+ stopLossPrice + 
						"\t" + "Current Price: " + openBuyPosition.getCurrent_price() + 
						"\t" + LocalDateTime.now().toString() + "\n");
				if (stopLossPrice.compareTo(Double.valueOf(openBuyPosition.getCurrent_price()))>0) {
					//Cancel all the open Orders for the symbol
					OrdersPojo[] openSymbolOrders = alpacaAccount.getSymbolOpenOrders(symbol);
					for(OrdersPojo openSymbolOrder:openSymbolOrders) {
						alpacaAccount.deleteOrder(openSymbolOrder.getId());
						pw.write("Delete Order Executed with following Details: " + openSymbolOrder.getSymbol() + "\t" + openSymbolOrder.getQty()+ "\t" + openSymbolOrder.getId() + 
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
					buyFraction = true;
					followDownTrendWave();
				}
			}
		}while(LocalDateTime.now().isBefore(marketCloseTime));
	}
	public static void followDownTrendWave() throws IOException, InterruptedException {
		do {
			PositionsPojo openPosition = alpacaAccount.getSymbolPosition(symbol);
			if (maxQty.compareTo(Double.valueOf(openPosition.getQty() == null? "0":openPosition.getQty())) > 0) {	
				priceToSell = returnCurrentPrice();
				placeSellOrder = alpacaAccount.sellSymbolLimitOrder(symbol, qty, priceToSell);
				placedSellOrder = alpacaAccount.getOrderDetails(placeSellOrder.getId());
				pw.write("Short Sell Order Executed with following Details: " + placedSellOrder.getSymbol() + "\t" + placedSellOrder.getQty()+ "\t" + placedSellOrder.getId() + 
						"\t" + placedSellOrder.getSide() + "\t" + placedSellOrder.getFilled_avg_price() + 
						"\t" + LocalDateTime.now().toString() + "\n");
				secondsCounter = 0; 
				while (!"filled".equals(placedSellOrder.getStatus()) && secondsCounter < 10)
				{
					Thread.sleep(1000);
					placedSellOrder = alpacaAccount.getOrderDetails(placeSellOrder.getId());
					secondsCounter++;
				}
				//If Sell order is not placed till time = 10sec then its an downtrend, lets try to sell at lower current buy price and repeat. No Need to wait and No need to place a buy order as we will cancel the corresponding sell order.
				if (!"filled".equals(placedSellOrder.getStatus()) && secondsCounter >= 10) {
					//Cancel the Sell Order
					alpacaAccount.deleteOrder(placeSellOrder.getId());
					OrdersPojo deletedOrder = alpacaAccount.getOrderDetails(placeSellOrder.getId());
					pw.write("Delete Order Executed with following Details: " + deletedOrder.getSymbol() + "\t" + deletedOrder.getQty()+ "\t" + deletedOrder.getId() + 
							"\t" + deletedOrder.getStatus() + "\t" + LocalDateTime.now().toString() + "\n");
					while (!"canceled".equals(deletedOrder.getStatus()))
					{
						Thread.sleep(100);
						deletedOrder = alpacaAccount.getOrderDetails(placeSellOrder.getId());
					}
					pw.write("Order Canceled with following Details: " + deletedOrder.getSymbol() + "\t" + deletedOrder.getQty()+ "\t" + deletedOrder.getId() + 
							"\t" + deletedOrder.getStatus() + "\t" + LocalDateTime.now().toString() + "\n");
					continue;
				}
				lastSellOrderPrice = priceToSell;
				placeBuyOrder = alpacaAccount.buySymbolLimitOrder(symbol, qty,  priceToSell*pointOneFactor);
				placedBuyOrder = alpacaAccount.getOrderDetails(placeBuyOrder.getId());
				pw.write("Buy Order Executed with following Details: " + placedBuyOrder.getSymbol() + "\t" + placedBuyOrder.getQty()+ "\t" + placedBuyOrder.getId() + 
						"\t" + placedBuyOrder.getSide() + "\t" + placedBuyOrder.getFilled_avg_price() + 
						"\t" + LocalDateTime.now().toString() + "\n");
			}
			else 
			{
				Thread.sleep(1000);
				PositionsPojo openSellPosition = alpacaAccount.getSymbolPosition(symbol);
				Double stopLossPrice = lastSellOrderPrice*onePointFourFactor;
				pw.write("Last Buy Price: " + lastBuyOrderPrice + "\t" + "Stop Loss Price: "+ stopLossPrice + 
						"\t" + "Current Price: " + openSellPosition.getCurrent_price() + 
						"\t" + LocalDateTime.now().toString() + "\n");
				if (stopLossPrice.compareTo(Double.valueOf(openSellPosition.getCurrent_price()))<0) {
					//Cancel all the open Orders for the symbol
					OrdersPojo[] openSymbolOrders = alpacaAccount.getSymbolOpenOrders(symbol);
					for(OrdersPojo openSymbolOrder:openSymbolOrders) {
						alpacaAccount.deleteOrder(openSymbolOrder.getId());
						pw.write("Delete Order Executed with following Details: " + openSymbolOrder.getSymbol() + "\t" + openSymbolOrder.getQty()+ "\t" + openSymbolOrder.getId() + 
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
			}
		}while(LocalDateTime.now().isBefore(marketCloseTime));
	}
}