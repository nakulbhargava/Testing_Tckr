import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;

import alpacaPojo.AssetsPojo;
import alpacaPojo.BarsPojo;
import alpacaPojo.OrdersPojo;
import alpacaPojo.PositionsPojo;
import alpacaPojo.TimeframePojo;

//import com.fasterxml.jackson.databind.ObjectMapper;

// HTTP basic authentication example in Java using the RTC Server RESTful API
public class Testing_Symbol_Assets_Timeframe {

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
	private static final LocalDateTime marketCloseTime = LocalDate.now().atTime(15,55);
	private boolean isRealTimeTrading = false;
	public static void main(String[] args) throws IOException, InterruptedException {

		AlpacaApiCaller alpacaAccount = new AlpacaApiCaller(alpacaKey, secretId, alpacaKeyValue, secretIdValue, headerContentType,
				headerContentTypeValue, alpacaKeyRealTime, secretIdRealTime, paperUrl, realtimeUrl, activeVersion, inactiveVersion, ordersEndpoint, accountEndpoint, 
				lastQuoteEndpoint, lastTradeEndpoint, positionsEndpoint, assetsEndpoint, clockEndpoint);

		String symbol = "ARKK";
		Double initialPriceToBuy = 2.00;
		Double priceToBuy = initialPriceToBuy;
		Double priceToSell = 0.00;
		OrdersPojo placeOrder;
		OrdersPojo placedOrder;
		Double sellQty = 0.00;
		String localTime = String.valueOf(LocalDateTime.now().getDayOfYear()) + String.valueOf(LocalDateTime.now().getHour()) + String.valueOf(LocalDateTime.now().getMinute()) + String.valueOf(LocalDateTime.now().getSecond());
		//		File file = new File("C:\\Users\\nakul\\Documents\\output - " + symbol + " - " + localTime + ".txt");
		//		PrintWriter pw = new PrintWriter(new FileOutputStream(file));
		//		FileOutputStream fo = new FileOutputStream(file);
		//		placeOrder = alpacaAccount.buySymbolPrice(symbol, priceToBuy);
		//		placedOrder = alpacaAccount.getOrderDetails(placeOrder.getId());
		//		while (!"filled".equals(placedOrder.getStatus()))
		//		{
		//			Thread.sleep(500);
		//			placedOrder = alpacaAccount.getOrderDetails(placeOrder.getId());
		//		}
		//		do {
		//			PositionsPojo openPosition = alpacaAccount.getSymbolPosition(symbol);
		//			pw.write(LocalDateTime.now().toString() + "\t" + openPosition.getSymbol()+ "\t" + openPosition.getCurrent_price()+ "\t" + openPosition.getAvg_entry_price()+ 
		//					"\t" + openPosition.getLastday_price() + "\t" + openPosition.getMarket_value() + 
		//					"\t" + openPosition.getSide()+ "\n");
		//		}while(LocalDateTime.now().isBefore(marketCloseTime));
		//			
		AssetsPojo assets[] = alpacaAccount.getAssets();
		//LastTrade lt = alpacaAccount.getLastTrade(symbol);
		//		File file = new File("C:\\Users\\nakul\\Documents\\outputAssets - " + localTime + ".txt");
		//		PrintWriter pw = new PrintWriter(new FileOutputStream(file));
		//		FileOutputStream fo = new FileOutputStream(file);
		//		int datList = assets.length;
		//		pw.write("Exchange" + "\t" + "Symbol" + "\t" + "Status" + "\t" + "Fractionable" + "\t" + "Marginable" + "\t" + "Shortable" + "\t" + "ETB" + "\t" + "Tradable" + "\n");
		//		for (int i = 0; i < datList; i++){
		//			pw.write(assets[i].getExchange() + "\t\t" + assets[i].getSymbol() + "\t" + assets[i].getStatus() + "\t"
		//					+ assets[i].isFractionable() + "\t\t" + assets[i].isMarginable() + "\t\t" + assets[i].isShortable() + "\t\t" 
		//					+ assets[i].isEasy_to_borrow() + "\t" + assets[i].isTradable()  + "\n");
		//		}
		File fileTrue = new File("C:\\Users\\nakul\\Documents\\outputTrueAssets - " + localTime + ".txt");
		PrintWriter pw1 = new PrintWriter(new FileOutputStream(fileTrue));
		FileOutputStream fo1 = new FileOutputStream(fileTrue);
		int assetTotalList = assets.length;
		pw1.write("Exchange" + "\t" + "Symbol" + "\t" + "Fractionable" 
				+ "\t" + "Shortable" + "\t" + "Tradable" + "\t"+ "Date" + "\t\t\t" + "Open"
				+ "\t" + "Close" + "\t" + "HIGH" + "\t" + "LOW" + "\t"+ "HL%" + "\t" + "Volume"+ "\n");
		for (int i = 0; i < assetTotalList; i++){
			if("active".equals(assets[i].getStatus()) && assets[i].isFractionable() && assets[i].isMarginable() && assets[i].isShortable() && assets[i].isEasy_to_borrow() && assets[i].isTradable()) {
				TimeframePojo timeframe = alpacaAccount.getTimeframe(assets[i].getSymbol(), "2021-04-14", "2021-04-14", "1Day");
				if (timeframe.getBars() == null || timeframe.getBars().length == 0) continue;
				BarsPojo bars = timeframe.getBars()[0];
				Double highLowPercent = (Double.valueOf(bars.getHighPrice()) - Double.valueOf(bars.getLowPrice()))*100/Double.valueOf(bars.getClosePrice());
				DecimalFormat df = new DecimalFormat("#.##");
				if(highLowPercent.compareTo(3.00)>0)
				{
					pw1.write(assets[i].getExchange() + "\t\t" + assets[i].getSymbol() + "\t"
							+ assets[i].isFractionable() + "\t\t" + assets[i].isShortable() + "\t\t" 
							+ assets[i].isTradable()  + "\t\t" + bars.getTime() + "\t" 
							+ bars.getOpenPrice() + "\t" + bars.getClosePrice() + "\t" + bars.getHighPrice() + "\t" + bars.getLowPrice() + "\t" 
							+ df.format(highLowPercent) + "\t" + bars.getVolume() + "\n");
				}
			}
		}
		pw1.close();
		//placeOrder = alpacaAccount.buySymbolPrice(symbol, priceToBuy);
		//System.out.println(placeOrder);
		//AssetsPojo asset = alpacaAccount.getAssetSymbol(symbol);
		// Start with investing $1000 on any random stock
		//		if(alpacaAccount.getClock().isIs_open()) {
		//			do {
		//				placeOrder = alpacaAccount.buySymbolPrice(symbol, priceToBuy);
		//				placedOrder = alpacaAccount.getOrderDetails(placeOrder.getId());
		//
		//				while (!"filled".equals(placedOrder.getStatus()))
		//				{
		//					Thread.sleep(500);
		//					placedOrder = alpacaAccount.getOrderDetails(placeOrder.getId());
		//				}
		//				pw.write(placedOrder.getSymbol() + "\t" + placedOrder.getQty()+ "\t" + placedOrder.getFilled_qty() + 
		//						"\t" + placedOrder.getSide() + "\t" + placedOrder.getFilled_avg_price() + 
		//						"\t" + placedOrder.getFilled_at()+ "\n");
		//				priceToSell = Double.valueOf(placedOrder.getFilled_avg_price()); 
		//				do {
		//					//Thread.sleep(3000);
		//					/*
		//					LastQuotePojo quote = alpacaAccount.getQuote(symbol).getLast();
		//					if(quote != null && Integer.parseInt(quote.getAsksize()) > Integer.parseInt(quote.getBidsize())) {
		//						if (priceToSell < Double.valueOf(quote.getAskprice())) {
		//							priceToSell =  Double.valueOf(quote.getAskprice());
		//							continue;
		//						}
		//						else {
		//							pw.write(quote.getAskprice() + "\t" + quote.getAsksize()+ "\t" + quote.getBidprice() + 
		//									"\t" + quote.getBidsize() + "\t" + quote.getTimestamp() + "\n");
		//							break;
		//						}
		//					}
		//					else {
		//						if(quote == null) continue;
		//						if(priceToSell < Double.valueOf(quote.getBidprice())) {
		//							priceToSell =  Double.valueOf(quote.getBidprice());
		//							continue;
		//						}
		//						else {
		//							pw.write(quote.getAskprice() + "\t" + quote.getAsksize()+ "\t" + quote.getBidprice() + 
		//									"\t" + quote.getBidsize() + "\t" + quote.getTimestamp() + "\n");
		//							break;
		//						}
		//					}*/
		//					LastTradePojo lastTrade = alpacaAccount.getLastTrade(symbol).getLast();
		//					if(lastTrade!=null && priceToSell < Double.valueOf(lastTrade.getPrice())) {
		//						priceToSell =  Double.valueOf(lastTrade.getPrice());
		//						continue;
		//					}
		//					else {
		//						pw.write(lastTrade.getPrice() + "\t" + lastTrade.getSize()+ "\t" + lastTrade.getTimestamp() + "\n");
		//						break;
		//					}
		//				}while(true);
		//				pw.write("Calculated Price To sell: " + priceToSell + "\n");
		//				sellQty = Double.valueOf(placedOrder.getFilled_qty());
		//				placeOrder = alpacaAccount.sellSymbolQuantity(symbol, sellQty);
		//				placedOrder = alpacaAccount.getOrderDetails(placeOrder.getId());
		//
		//				while (!"filled".equals(placedOrder.getStatus()))
		//				{
		//					Thread.sleep(500);
		//					placedOrder = alpacaAccount.getOrderDetails(placeOrder.getId());
		//				}
		//				pw.write(placedOrder.getSymbol() + "\t" + placedOrder.getQty()+ "\t" + placedOrder.getFilled_qty() + 
		//						"\t" + placedOrder.getSide() + "\t" + placedOrder.getFilled_avg_price() + 
		//						"\t" + placedOrder.getFilled_at()+ "\n");
		//				priceToBuy = Double.valueOf(placedOrder.getFilled_avg_price()); 
		//				do {
		//					//Thread.sleep(3000);
		//					/*
		//					LastQuotePojo quote = alpacaAccount.getQuote(symbol).getLast();
		//					if(quote != null && Integer.parseInt(quote.getAsksize()) > Integer.parseInt(quote.getBidsize())) {
		//						if (priceToBuy > Double.valueOf(quote.getAskprice())) {
		//							priceToBuy =  Double.valueOf(quote.getAskprice());
		//							continue;
		//						}
		//						else {
		//							pw.write(quote.getAskprice() + "\t" + quote.getAsksize()+ "\t" + quote.getBidprice() + 
		//									"\t" + quote.getBidsize() + "\t" + quote.getTimestamp() + "\n");
		//							break;
		//						}
		//					}
		//					else {
		//						if(priceToBuy > Double.valueOf(quote.getBidprice())) {
		//							priceToBuy =  Double.valueOf(quote.getBidprice());
		//							continue;
		//						}
		//						else {
		//							pw.write(quote.getAskprice() + "\t" + quote.getAsksize()+ "\t" + quote.getBidprice() + 
		//									"\t" + quote.getBidsize() + "\t" + quote.getTimestamp() + "\n");
		//							break;
		//						}
		//					}*/
		//					LastTradePojo lastTrade = alpacaAccount.getLastTrade(symbol).getLast();
		//					if(lastTrade!=null && priceToBuy > Double.valueOf(lastTrade.getPrice())) {
		//						priceToBuy =  Double.valueOf(lastTrade.getPrice());
		//						continue;
		//					}
		//					else {
		//						pw.write(lastTrade.getPrice() + "\t" + lastTrade.getSize()+ "\t" + lastTrade.getTimestamp() + "\n");
		//						break;
		//					}
		//				}while(true);
		//				pw.write("Calculated Price To Buy: " + priceToBuy + "\n");
		//				//pw.close();
		//				priceToBuy = Double.valueOf(placedOrder.getFilled_avg_price())*Double.valueOf(sellQty);
		//			}while(LocalDateTime.now().isBefore(marketCloseTime));
		/*		String getLastQuote = getQuote("TSN");
		String getLastTrade = getLastTrade("TSN");
		String getAssets = getAssets();
		String getOpenPositions = getOpenPositions();
		String getAccountDetails = getAccountDetails();
		String getSymbolPosition = getSymbolPosition ("TSN");
		System.out.println("\n" + getLastQuote);
		System.out.println("\n" + getLastTrade);
		//System.out.println("/n" + getAccountDetails);
		//System.out.println("/n" + buySymbol("TSN"));
		//System.out.println("\n" + getOrders());
		//System.out.println("\n" + getOrderDetails("4a57e769-f297-4794-8eed-9c9fd69e444b"));
		System.out.println("\n" + getSymbolPosition);
		//System.out.println("\n" + getAssets);
		//System.out.println("\n" + getAssets());*/
	}
}
