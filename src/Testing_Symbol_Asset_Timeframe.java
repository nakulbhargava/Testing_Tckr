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
public class Testing_Symbol_Asset_Timeframe {

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
		AssetsPojo asset = alpacaAccount.getAssetSymbol(symbol);

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

		pw1.write("Exchange" + "\t" + "Symbol" + "\t" + "Fractionable" 
				+ "\t" + "Shortable" + "\t" + "Tradable" + "\t"+ "Date" + "\t\t\t" + "Open"
				+ "\t" + "Close" + "\t" + "HIGH" + "\t" + "LOW" + "\t"+ "HL%" + "\t" + "Volume"+ "\n");

		if("active".equals(asset.getStatus()) && asset.isFractionable() && asset.isMarginable() && asset.isShortable() && asset.isEasy_to_borrow() && asset.isTradable()) {
			TimeframePojo timeframe = alpacaAccount.getTimeframe(asset.getSymbol(), "2021-04-26", "2021-04-26", "1Day");
			BarsPojo bars = timeframe.getBars()[0];
			Double highLowPercent = (Double.valueOf(bars.getHighPrice()) - Double.valueOf(bars.getLowPrice()))*100/Double.valueOf(bars.getClosePrice());
			DecimalFormat df = new DecimalFormat("#.##");
			if(highLowPercent.compareTo(3.00)>0)
			{
				pw1.write(asset.getExchange() + "\t\t" + asset.getSymbol() + "\t" + asset.getName() + "\t"
						+ asset.isFractionable() + "\t\t" + asset.isShortable() + "\t\t" 
						+ asset.isTradable()  + "\t\t" + bars.getTime() + "\t" 
						+ bars.getOpenPrice() + "\t" + bars.getClosePrice() + "\t" + bars.getHighPrice() + "\t" + bars.getLowPrice() + "\t" 
						+ df.format(highLowPercent) + "\t" + bars.getVolume() + "\n");
			}
		}
		pw1.close();

	}
}
