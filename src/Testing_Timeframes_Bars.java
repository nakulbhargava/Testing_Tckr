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
public class Testing_Timeframes_Bars {

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
	private static final LocalDateTime marketCloseTime = LocalDate.now().atTime(12,35);
	private boolean isRealTimeTrading = false;
	public static void main(String[] args) throws IOException, InterruptedException {

		AlpacaApiCaller alpacaAccount = new AlpacaApiCaller(alpacaKey, secretId, alpacaKeyValue, secretIdValue, headerContentType,
				headerContentTypeValue, alpacaKeyRealTime, secretIdRealTime, paperUrl, realtimeUrl, activeVersion, inactiveVersion, ordersEndpoint, accountEndpoint, 
				lastQuoteEndpoint, lastTradeEndpoint, positionsEndpoint, assetsEndpoint, clockEndpoint);

		String symbol = "BNGO";
		File file = new File("C:\\Users\\nakul\\Documents\\outputTimeframe.txt");
		PrintWriter pw = new PrintWriter(new FileOutputStream(file));
//		FileOutputStream fo = new FileOutputStream(file);

		AssetsPojo assets = alpacaAccount.getAssetSymbol(symbol);
		TimeframePojo timeframe = alpacaAccount.getTimeframe(assets.getSymbol(), "2021-06-03", "2021-06-03", "1Day");
		BarsPojo bars = timeframe.getBars()[0];
		Double highLowPercent = (Double.valueOf(bars.getHighPrice()) - Double.valueOf(bars.getLowPrice()))*100/Double.valueOf(bars.getClosePrice());
		DecimalFormat df = new DecimalFormat("#.##");
		pw.write(assets.getExchange() + "\t\t" + assets.getSymbol() + "\t"
				+ assets.isFractionable() + "\t\t" + assets.isShortable() + "\t\t" 
				+ "\t" + assets.isTradable()  + "\t" + bars.getTime() + "\t" 
				+ bars.getOpenPrice() + "\t" + bars.getClosePrice() + "\t" + bars.getHighPrice() + "\t" + bars.getLowPrice() + "\t" 
				+ df.format(highLowPercent) + "\t" + bars.getVolume() + "\n");
		System.out.println(assets.getExchange() + "\t\t" + assets.getSymbol() + "\t"
				+ assets.isFractionable() + "\t\t" + assets.isShortable() + "\t\t" 
				+ "\t" + assets.isTradable()  + "\t" + bars.getTime() + "\t" 
				+ bars.getOpenPrice() + "\t" + bars.getClosePrice() + "\t" + bars.getHighPrice() + "\t" + bars.getLowPrice() + "\t" 
				+ df.format(highLowPercent) + "\t" + bars.getVolume() + "\n");
		pw.close();

	}
}