package Backtesting;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

import alpacaPojo.AccountsPojo;
import alpacaPojo.AssetsPojo;
import alpacaPojo.LastQuotePojo;
import alpacaPojo.LastTradePojo;
import alpacaPojo.OrdersPojo;
import alpacaPojo.PositionsPojo;

//import com.fasterxml.jackson.databind.ObjectMapper;

// HTTP basic authentication example in Java using the RTC Server RESTful API
public class Testing_Symbol_Algo_LimitOrders_Strategy2 {

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
	private boolean isRealTimeTrading = false;
	static boolean buyFraction = true;
	static Double qty = 10.00;
	static Double maxBuyQty = (qty*10) + 1;
	static Double pointZeroOneFactor = qty/1000;
	static Double priceToBuy = 0.00;
	static Double priceToSell = 0.0000;
	static Double pointTwoFactor = .998;
	static Double onePointTwoFactor = 1.002;
	static Double onePointOneFactor = 1.001;
	static Double onePercentFactor = .99;
	static boolean timeToSell = false;
	static String symbol = "ARKK";
	static AlpacaApiCaller alpacaAccount;

	public static void main(String[] args) throws IOException, InterruptedException {

		alpacaAccount = new AlpacaApiCaller(alpacaKey, secretId, alpacaKeyValue, secretIdValue, headerContentType,
				headerContentTypeValue, alpacaKeyRealTime, secretIdRealTime, paperUrl, realtimeUrl, activeVersion, inactiveVersion, ordersEndpoint, accountEndpoint, 
				lastQuoteEndpoint, lastTradeEndpoint, positionsEndpoint, assetsEndpoint, clockEndpoint);

		OrdersPojo placeBuyOrder;
		OrdersPojo placeHighBuyOrder;
		OrdersPojo placeLowBuyOrder;
		Double highBuyPrice = 0.00;
		Double highProfitPrice =0.00;
		Double highStopLossPrice = 0.00;
		Double lowBuyPrice = 0.00;
		Double lowProfitPrice = 0.00;
		Double lowStopLossPrice = 0.00;

		Double highestPrice=0.00;
		Double lowestPrice=0.00;
		String highestOrderId;
		String lowestOrderId = null;
		int secondsCounter = 0;

		Double lastBuyOrderPrice = 0.00;

		String localTime = String.valueOf(LocalDateTime.now().getDayOfYear()) + String.valueOf(LocalDateTime.now().getHour()) + String.valueOf(LocalDateTime.now().getMinute()) + String.valueOf(LocalDateTime.now().getSecond());
		File file = new File("C:\\Users\\nakul\\Documents\\output - " + symbol + " - " + localTime + ".txt");
		PrintWriter pw = new PrintWriter(new FileOutputStream(file)); 
		FileOutputStream fo = new FileOutputStream(file);

		if(alpacaAccount.getClock().isIs_open()) {
			do {
				PositionsPojo openPosition = alpacaAccount.getSymbolPosition(symbol);
				OrdersPojo[] openOrders = alpacaAccount.getOpenOrders(symbol);
				Double openOrderQty = 0.00;
				for (OrdersPojo openOrder:openOrders)
					openOrderQty = openOrderQty + Double.valueOf(openOrder.getQty()==null?"0":openOrder.getQty());

				if (maxBuyQty.compareTo(openOrderQty + Double.valueOf(openPosition.getQty() == null? "0":openPosition.getQty())) > 0) {	
					priceToBuy = returnCurrentPrice(); 
					highBuyPrice = priceToBuy*onePointOneFactor;
					highProfitPrice = highBuyPrice*onePointTwoFactor;
					highStopLossPrice = highBuyPrice*onePercentFactor;
					lowBuyPrice = priceToBuy*pointTwoFactor;
					lowProfitPrice = lowBuyPrice*onePointOneFactor;
					lowStopLossPrice = lowBuyPrice*onePercentFactor;
					placeBuyOrder = alpacaAccount.buySymbolBracketOrder(symbol, qty, priceToBuy, priceToBuy*onePointTwoFactor, priceToBuy*onePercentFactor);
					placeHighBuyOrder = alpacaAccount.buySymbolBracketOrder(symbol, qty, highBuyPrice
							, highProfitPrice, highStopLossPrice);
					placeLowBuyOrder = alpacaAccount.buySymbolBracketOrder(symbol, qty, lowBuyPrice
							, lowProfitPrice, lowStopLossPrice);
					pw.write("Buy Order Executed with following Details: " + placeBuyOrder.getSymbol() + "\t" + placeBuyOrder.getQty()+ "\t" + placeBuyOrder.getId() + 
							"\t" + priceToBuy + "\t" + priceToBuy*onePointTwoFactor + "\t" + priceToBuy*onePercentFactor +
							"\t" + LocalDateTime.now().toString() + "\n");
					pw.write("High Buy Order Executed with following Details: " + placeHighBuyOrder.getSymbol() + "\t" + placeHighBuyOrder.getQty()+ "\t" + placeHighBuyOrder.getId() + 
							"\t" + highBuyPrice + "\t" + highProfitPrice + "\t" + highStopLossPrice +
							"\t" + LocalDateTime.now().toString() + "\n");
					pw.write("Low Buy Order Executed with following Details: " + placeLowBuyOrder.getSymbol() + "\t" + placeLowBuyOrder.getQty()+ "\t" + placeLowBuyOrder.getId() + 
							"\t" + lowBuyPrice + "\t" + lowProfitPrice + "\t" + lowStopLossPrice +
							"\t" + LocalDateTime.now().toString() + "\n");
				}
				else 
				{
					secondsCounter++;
					if(secondsCounter >=30)
					{
						//Check if its up-trend by checking if all the orders are open and checking current price is more than highest open order price
						Double currentPrice = returnCurrentPrice();
						OrdersPojo[] openWaitingOrders = alpacaAccount.getOpenOrders(symbol);
						for(OrdersPojo openOrder: alpacaAccount.getOpenOrders(symbol))
						{
							highestPrice = highestPrice > Double.valueOf(openOrder.getLimit_price())?highestPrice:Double.valueOf(openOrder.getLimit_price());
							if (openOrder.getStop_price() !=null)
							{
								if (lowestPrice == null || lowestPrice<Double.valueOf(openOrder.getStop_price())) {
									lowestPrice = Double.valueOf(openOrder.getStop_price());
									lowestOrderId = openOrder.getId();
								}
							} 
						}
						if(highestPrice < currentPrice) {
							//Cancel all open Orders so the process can begin again
							for(OrdersPojo openOrder: alpacaAccount.getOpenOrders(symbol))
							{
								alpacaAccount.deleteOrder(openOrder.getId());
							}
						}
						else {
							//replace the order id with the lowest price to sell at currentprice
							alpacaAccount.replaceStopLossOrder(lowestOrderId, currentPrice.toString());
						}
						secondsCounter =0;
					}
					Thread.sleep(1000);
				}
			}while(LocalDateTime.now().isBefore(marketCloseTime));
			pw.close();
		}
	}

	public static Double returnCurrentPrice() throws IOException, InterruptedException {
		Double currentPrice = 0.00;
		if(buyFraction)
		{
			OrdersPojo placeBuyOrder = alpacaAccount.buySymbolQuantity(symbol, pointZeroOneFactor);
			OrdersPojo placedBuyOrder = alpacaAccount.getOrderDetails(placeBuyOrder.getId());
			while (!"filled".equals(placedBuyOrder.getStatus()))
			{
				placedBuyOrder = alpacaAccount.getOrderDetails(placeBuyOrder.getId());
			}
			currentPrice = Double.valueOf(placedBuyOrder.getFilled_avg_price());
			buyFraction = false;
			return currentPrice;
		}
		else
		{
			OrdersPojo placeSellOrder = alpacaAccount.sellSymbolQuantity(symbol, pointZeroOneFactor);
			OrdersPojo placedSellOrder = alpacaAccount.getOrderDetails(placeSellOrder.getId());
			while (!"filled".equals(placedSellOrder.getStatus()))
			{
				placedSellOrder = alpacaAccount.getOrderDetails(placedSellOrder.getId());
			}
			currentPrice = Double.valueOf(placedSellOrder.getFilled_avg_price());
			buyFraction = true;
			return currentPrice;
		}
	}
}
