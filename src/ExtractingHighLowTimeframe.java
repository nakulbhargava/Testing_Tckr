import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;

import alpacaPojo.AlpacaApiCaller;
import alpacaPojo.AssetsPojo;
import alpacaPojo.BarsPojo;
import alpacaPojo.TimeframePojo;

//import com.fasterxml.jackson.databind.ObjectMapper;

// HTTP basic authentication example in Java using the RTC Server RESTful API
public class ExtractingHighLowTimeframe {

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

		String symbol = "SNAP";

		String localTime = String.valueOf(LocalDateTime.now().getDayOfYear()) + String.valueOf(LocalDateTime.now().getHour()) + String.valueOf(LocalDateTime.now().getMinute()) + String.valueOf(LocalDateTime.now().getSecond());		
		AssetsPojo asset = alpacaAccount.getAssetSymbol(symbol);

		File fileTrue = new File("C:\\Users\\nakul\\Documents\\outputTrueAssets - " + localTime + ".txt");
		PrintWriter pw1 = new PrintWriter(new FileOutputStream(fileTrue));
//		FileOutputStream fo1 = new FileOutputStream(fileTrue);

		pw1.write("Exchange" + "\t" + "Symbol" + "\t" + "Fractionable" 
				+ "\t" + "Shortable" + "\t" + "Tradable" + "\t"+ "Date" + "\t\t\t" + "Open"
				+ "\t" + "Close" + "\t" + "HIGH" + "\t" + "LOW" + "\t"+ "HL%" + "\t" + "Volume"+ "\n");
		System.out.println("Exchange" + "\t" + "Symbol" + "\t" + "Fractionable" 
				+ "\t" + "Shortable" + "\t" + "Tradable" + "\t"+ "Date" + "\t\t\t" + "Open"
				+ "\t" + "Close" + "\t" + "HIGH" + "\t" + "LOW" + "\t"+ "HL%" + "\t" + "Volume");

		if("active".equals(asset.getStatus()) && asset.isFractionable() && asset.isMarginable() && asset.isShortable() && asset.isEasy_to_borrow() && asset.isTradable()) {
			TimeframePojo timeframe = alpacaAccount.getTimeframe(asset.getSymbol(), "2021-04-12", "2021-04-16", "1Day");
			BarsPojo bars[] = timeframe.getBars();
			Double highLowPercent = 0.0;
			Double highTotal = 0.0;
			Double lowTotal = 0.0;
			DecimalFormat df = new DecimalFormat("#.##");
			for(BarsPojo bar: bars) {
				highLowPercent = (Double.valueOf(bar.getHighPrice()) - Double.valueOf(bar.getLowPrice()))*100/Double.valueOf(bar.getClosePrice());
				highTotal = highTotal + Double.valueOf(bar.getHighPrice());
				lowTotal = lowTotal + Double.valueOf(bar.getLowPrice());
				pw1.write(asset.getExchange() + "\t\t" + asset.getSymbol() + "\t"
						+ asset.isFractionable() + "\t\t" + asset.isShortable() + "\t\t" 
						+ asset.isTradable()  + "\t\t" + bar.getTime() + "\t" 
						+ bar.getOpenPrice() + "\t" + bar.getClosePrice() + "\t" + bar.getHighPrice() + "\t" + bar.getLowPrice() + "\t" 
						+ df.format(highLowPercent) + "\t" + bar.getVolume() + "\n");
				System.out.println(asset.getExchange() + "\t\t" + asset.getSymbol() + "\t"
						+ asset.isFractionable() + "\t\t" + asset.isShortable() + "\t\t" 
						+ asset.isTradable()  + "\t\t" + bar.getTime() + "\t" 
						+ bar.getOpenPrice() + "\t" + bar.getClosePrice() + "\t" + bar.getHighPrice() + "\t" + bar.getLowPrice() + "\t" 
						+ df.format(highLowPercent) + "\t" + bar.getVolume());
			}
			pw1.write("High Average: " + highTotal/bars.length + "\tLow Average: " + lowTotal/bars.length);
			System.out.println("High Average: " + highTotal/bars.length + "\tLow Average: " + lowTotal/bars.length);
		}
		pw1.close();

	}
}
