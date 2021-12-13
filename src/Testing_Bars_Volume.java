import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

import com.fasterxml.jackson.databind.deser.std.DateDeserializers.DateDeserializer;

import alpacaPojo.AlpacaApiCaller;
import alpacaPojo.AssetsPojo;
import alpacaPojo.BarsPojo;
import alpacaPojo.PositionsPojo;
import alpacaPojo.TimeframePojo;

//import com.fasterxml.jackson.databind.ObjectMapper;

// HTTP basic authentication example in Java using the RTC Server RESTful API
public class Testing_Bars_Volume {

	private static final String alpacaKey = "APCA-API-KEY-ID";
	private static final String secretId = "APCA-API-SECRET-KEY";
	private static final String alpacaKeyValue = "PKXTC9CIEKM9SXKWR7CN";
	private static final String secretIdValue = "EWFyZnPGoQyTbSfPAcLO02ao5n6MXSvdbaQr19Y9";
	private static final String alpacaKeyRealTime = "";
	private static final String secretIdRealTime = "";
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
	public static void main(String[] args) throws IOException, InterruptedException, ParseException {

		AlpacaApiCaller alpacaAccount = new AlpacaApiCaller(alpacaKey, secretId, alpacaKeyValue, secretIdValue, headerContentType,
				headerContentTypeValue, alpacaKeyRealTime, secretIdRealTime, paperUrl, realtimeUrl, activeVersion, inactiveVersion, ordersEndpoint, accountEndpoint, 
				lastQuoteEndpoint, lastTradeEndpoint, positionsEndpoint, assetsEndpoint, clockEndpoint);
		String localTime = String.valueOf(LocalDateTime.now().getDayOfYear()) + String.valueOf(LocalDateTime.now().getHour()) + String.valueOf(LocalDateTime.now().getMinute()) + String.valueOf(LocalDateTime.now().getSecond());

//		String symbol = "GOTU";
		File file = new File("C:\\Users\\nakul\\Documents\\outputTimeframe - " + localTime + ".txt");
		PrintWriter pw = new PrintWriter(new FileOutputStream(file));
		FileOutputStream fo = new FileOutputStream(file);

		//		AssetsPojo assets = alpacaAccount.getAssetSymbol(symbol);
		PositionsPojo[] openPositions = alpacaAccount.getOpenPositions();
		for (PositionsPojo openPosition : openPositions) {
			TimeframePojo timeframe = alpacaAccount.getTimeframe(openPosition.getSymbol(), "2019-05-29", "2021-05-29", "1Day");
			BarsPojo bars[] = timeframe.getBars();
			Double sumHighLow = 0.00;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
			for(BarsPojo bar:bars) {
				Double highLowPercent = (Double.valueOf(bar.getHighPrice()) - Double.valueOf(bar.getLowPrice()))*100/Double.valueOf(bar.getClosePrice());
				DecimalFormat df = new DecimalFormat("#.##");
				pw.write(openPosition.getSymbol() + "\t" + sdf.parse(bar.getTime()) + "\t"
						+ bar.getOpenPrice() + "\t" + bar.getClosePrice() + "\t" + bar.getHighPrice() + "\t" + bar.getLowPrice() + "\t" 
						+ df.format(highLowPercent) + "\t" + bar.getVolume() + "\n");
				System.out.println(openPosition.getSymbol() + "\t" + sdf.parse(bar.getTime()) + "\t"
						+ bar.getOpenPrice() + "\t" + bar.getClosePrice() + "\t" + bar.getHighPrice() + "\t" + bar.getLowPrice() + "\t" 
						+ df.format(highLowPercent) + "\t" + bar.getVolume());
				sumHighLow = sumHighLow + highLowPercent;
			}
			pw.write("Weighted Average HighLow: " + sumHighLow/bars.length + "\n" );
			System.out.println("Weighted Average HighLow: " + sumHighLow/bars.length);
		}
		pw.close();

	}
}