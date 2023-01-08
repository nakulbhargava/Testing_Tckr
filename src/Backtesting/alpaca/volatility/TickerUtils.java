package Backtesting.alpaca.volatility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import alpacaPojo.AlpacaApiCaller;
import alpacaPojo.BarsPojo;
import alpacaPojo.OrdersPojo;
import alpacaPojo.PositionsPojo;
import alpacaPojo.Ticker;
import alpacaPojo.TimeframePojo;

public class TickerUtils {
	public static Properties loadProperties(String propertyFile) throws IOException {
		Properties prop = new Properties();
		InputStream inputStream = new FileInputStream(propertyFile);
		prop.load(inputStream);
		inputStream.close();
		return prop;
	}

	public static void addPropertyToFile(Properties props, String propertyFile) throws IOException {
		File configFile = new File(propertyFile);

		FileWriter writer = new FileWriter(configFile, true);
		props.store(writer, null);
		writer.close();
	}

	public static void updateNewPropertyToFile(Properties props, String propertyFile) throws IOException {
		File configFile = new File(propertyFile);

		FileWriter writer = new FileWriter(configFile);
		props.store(writer, null);
		writer.close();
	}

	public static void deletePropertyInFile(Properties props, String propertyFile) throws IOException {
		File configFile = new File(propertyFile);

		FileWriter writer = new FileWriter(configFile);
		props.store(writer, null);
		writer.close();
	}
	public static long getRandomNumber(long min, long max) {
		return (long) ((Math.random() * (max - min)) + min);
	}
	public static List<Ticker> readFileAsList(String fileName) {

		File file = new File(fileName);
		String line;
		List<Ticker> ticks = new ArrayList<Ticker>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			while((line = reader.readLine()) != null) {
				if ("\n".equals(line) || "".equals(line)) continue;
				List<String> splitLine = Arrays.asList(line.split(",", -1));
				Ticker tick = new Ticker();
				tick.symbol = splitLine.get(0);
				tick.qty = splitLine.get(1);
				tick.price = splitLine.get(2);
				ticks.add(tick);
			}
			reader.close();
			return ticks;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Double returnHighTrailingStopPrice(Double price) {
		return 1.52*price;
	}

	public static Double returnTrailingStopPrice(Double price) {
		return price*1.02/1.52;
	}

	public static boolean breakOperation(String symbol) throws IOException {
		Properties switchProperties = loadProperties(TickerConstants.switchStateConfig);
		if(!"Y".equals(switchProperties.getProperty(symbol)) || !"Y".equals(switchProperties.getProperty(TickerConstants.globalSwitch))){
			return true;
		}
		return false;
	}

	public static boolean breakCryptoOperation(String symbol) throws IOException {
		Properties switchCryptoProperties = loadProperties(TickerConstants.switchStateCryptoConfig);
		if(!"Y".equals(switchCryptoProperties.getProperty(symbol)) || !"Y".equals(switchCryptoProperties.getProperty(TickerConstants.globalSwitch))){
			return true;
		}
		return false;
	}

	/*
	 * Method returns following 3 values
	 * 1 - If its an uptrend
	 * 0 - If downtrend
	 * -1 - if Exception in calling the external apis
	 */
	public static int calculateUpTrendMinute(String symbol, AlpacaApiCaller alpacaAccount) {
		String pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		LocalDateTime currentDate = LocalDateTime.now();
		Date dateEnd = Date.from(currentDate.toInstant(ZoneOffset.ofHours(-12)));
		LocalDateTime startDate = currentDate.minusMinutes(30);
		Date dateStart = Date.from(startDate.toInstant(ZoneOffset.ofHours(-12)));
		String end = simpleDateFormat.format(dateEnd);
		String start = simpleDateFormat.format(dateStart);

		TimeframePojo time = alpacaAccount.getTimeframe(symbol, start, end, "1Min");
		if(time == null || time.getBars() == null || time.getBars().length == 0 || time.getBars().length < 15) {
			//returning exception
			System.out.println(symbol + " - Unable to get the Bars for timeframe or length was less than 15: " + start + " " + end);
			return -1;
		}
		BarsPojo[] bars = time.getBars();
		if (bars.length == 0) return 0;
		Double slowMA = calculateMovingAverage(bars);
		int fastBarLength = bars.length/4;
		if (fastBarLength == 0) return 0;
		BarsPojo[] fastBars = new BarsPojo[fastBarLength];
		int j = 0;
		for(int i = bars.length - fastBarLength; i < bars.length; i++) {
			fastBars[j] = bars[i];
			j++;
		}
		Double fastMA = calculateMovingAverage(fastBars);
		Double percentIncrease = ((Double.valueOf(bars[bars.length - 1].getHighPrice()) - Double.valueOf(bars[0].getLowPrice()))*100)/Double.valueOf(bars[0].getLowPrice());
		System.out.println(time.getSymbol() + " - Getting Time Frame for: " + fastBarLength + "MA: " + fastMA + " " + bars.length + "MA: " + slowMA + " %Increase: " + percentIncrease);
		if(fastMA.compareTo(slowMA) > 0 && percentIncrease.compareTo(5.0) < 0) 
			return 1;
		else return 0;	
	}
	/*
	 * Method returns following 3 values
	 * 1 - If its an uptrend
	 * 0 - If downtrend
	 * -1 - if Exception in calling the external apis
	 */
	public static int calculateUpTrendMinute(TimeframePojo time) {
		BarsPojo[] bars = time.getBars();
		if (bars.length == 0) return 0;
		Double slowMA = calculateMovingAverage(bars);
		int fastBarLength = bars.length/4;
		if (fastBarLength == 0) return 0;
		BarsPojo[] fastBars = new BarsPojo[fastBarLength];
		int j = 0;
		for(int i = bars.length - fastBarLength; i < bars.length; i++) {
			fastBars[j] = bars[i];
			j++;
		}
		Double fastMA = calculateMovingAverage(fastBars);
		Double percentIncrease = ((Double.valueOf(bars[bars.length - 1].getVwap()) - Double.valueOf(bars[0].getVwap()))*100)/Double.valueOf(bars[0].getVwap());
		System.out.println(time.getSymbol() + "\t- Getting Time Frame for: " + fastBarLength + "MA: " + fastMA + " " + bars.length + "MA: " + slowMA + " %Increase: " + percentIncrease);
		if(fastMA.compareTo(slowMA) > 0 && percentIncrease.compareTo(2.5) < 0 && Double.valueOf(bars[bars.length - 1].getVwap()).compareTo(fastMA) > 0) 
			return 1;
		else return 0;		
	}

	public static Double calculateMovingAverage(BarsPojo [] bars) {
		int barsLength = bars.length;
		Double sumLow = 0.0;
		for(BarsPojo bar : bars) {
			sumLow = sumLow + Double.valueOf(bar.getVwap());
		}
		return sumLow/barsLength;
	}
	public static AlpacaApiCaller getAlpacaAccountObject() throws IOException {
		Properties prop = loadProperties(TickerConstants.alpacaConfig);
		String alpacaKey = prop.getProperty(TickerConstants.alpacaKey);
		String secretId = prop.getProperty(TickerConstants.secretId);
		String alpacaKeyValue = prop.getProperty(TickerConstants.alpacaKeyValuePaper);
		String secretIdValue = prop.getProperty(TickerConstants.secretIdValuePaper);
		String headerContentType = prop.getProperty(TickerConstants.headerContentType);
		String headerContentTypeValue = prop.getProperty(TickerConstants.headerContentTypeValue);
		String alpacaKeyRealTime = prop.getProperty(TickerConstants.alpacaKeyRealTime);
		String secretIdRealTime = prop.getProperty(TickerConstants.secretIdRealTime);
		String paperUrl = prop.getProperty(TickerConstants.paperUrl);
		String realtimeUrl = prop.getProperty(TickerConstants.realtimeUrl);
		String dataUrl = prop.getProperty(TickerConstants.dataUrl);
		String activeVersion = prop.getProperty(TickerConstants.activeVersion);
		String activeCryptoVersion = prop.getProperty(TickerConstants.activeCryptoVersion);
		String inactiveVersion = prop.getProperty(TickerConstants.inactiveVersion);
		String ordersEndpoint = prop.getProperty(TickerConstants.ordersEndpoint);
		String accountEndpoint = prop.getProperty(TickerConstants.accountEndpoint);
		String lastQuoteEndpoint = prop.getProperty(TickerConstants.lastQuoteEndpoint);
		String lastTradeEndpoint = prop.getProperty(TickerConstants.lastTradeEndpoint);
		String positionsEndpoint = prop.getProperty(TickerConstants.positionsEndpoint);
		String assetsEndpoint = prop.getProperty(TickerConstants.assetsEndpoint);
		String clockEndpoint = prop.getProperty(TickerConstants.clockEndpoint);
		return new AlpacaApiCaller(alpacaKey, secretId, alpacaKeyValue, secretIdValue, headerContentType,
				headerContentTypeValue, alpacaKeyRealTime, secretIdRealTime, paperUrl, realtimeUrl, dataUrl, activeVersion, activeCryptoVersion, inactiveVersion, ordersEndpoint, accountEndpoint, 
				lastQuoteEndpoint, lastTradeEndpoint, positionsEndpoint, assetsEndpoint, clockEndpoint);
	}

	

	public static <T> T getJsonObject(String jsonString, Class objectClass) throws JsonParseException, JsonMappingException, IOException{
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true);
		T  responseObject = (T) mapper.readValue(jsonString,  objectClass);
		return responseObject;
	}
	public static <T> T[] getJsonObjectArray(String jsonString, Class objectClass) throws JsonParseException, JsonMappingException, IOException{
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		//		mapper.configure(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true);
		T[]  responseObject = (T[]) mapper.readValue(jsonString,  objectClass);
		return responseObject;
	}

	public static <T> T getJsonObjectMap(String jsonString, Class objectClass) throws JsonParseException, JsonMappingException, IOException{
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true);
		T  responseObject = (T) mapper.readValue(jsonString,  objectClass);
		return responseObject;
	}
	public static <T> T getJsonObjectConvertor(Object jsonObject, Class objectClass) throws JsonParseException, JsonMappingException, IOException{
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true);
		T  responseObject = (T) mapper.convertValue(jsonObject,  objectClass);
		return responseObject;
	}

	public static int validateInputNumber() throws IOException {
		// Reading data using readLine
		String number = null;
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(System.in));
		while(number == null) {
			number = reader.readLine();
			if (!number.matches("^[0-9]\\d*(\\.\\d+)?$")) {
				System.out.println("Invalid number, Enter again:");
				number = null;
			}
		}
		return Integer.parseInt(number);
	}
	
	public static HashMap<String, List<OrdersPojo>> getOrdersHashMap(AlpacaApiCaller alpacaAccount, OrdersPojo[] orders ){
		HashMap<String, List<OrdersPojo>> ordersSymbols = new HashMap<String, List<OrdersPojo>>();
		for(OrdersPojo order : orders) {
			List<OrdersPojo> orderList = ordersSymbols.get(order.getSymbol());
			if(orderList == null) orderList = new ArrayList<OrdersPojo>();
			orderList.add(order);
			ordersSymbols.put(order.getSymbol(), orderList);
		}
		return ordersSymbols;
	}
	
	public static HashMap<String, PositionsPojo> getPositionsHashMap(AlpacaApiCaller alpacaAccount, PositionsPojo[] positions ){
		HashMap<String, PositionsPojo> positionSymbols = new HashMap<String, PositionsPojo>();
		for(PositionsPojo position : positions) {
			positionSymbols.put(position.getSymbol(), position);
		}
		return positionSymbols;
	}
	
	public static void copyFileUsingStream(File source, File dest) throws IOException {
	    InputStream is = null;
	    OutputStream os = null;
	    try {
	        is = new FileInputStream(source);
	        os = new FileOutputStream(dest);
	        byte[] buffer = new byte[1024];
	        int length;
	        while ((length = is.read(buffer)) > 0) {
	            os.write(buffer, 0, length);
	        }
	    } finally {
	        is.close();
	        os.close();
	    }
	}
	
	public static boolean isDaylightSaving(LocalDateTime date) {
		LocalDateTime dayLightStart = LocalDateTime.of(date.getYear(), 03, 1, 02, 0, 0).with(TemporalAdjusters.dayOfWeekInMonth(2, DayOfWeek.SUNDAY));
		LocalDateTime dayLightEnd = LocalDateTime.of(date.getYear(), 11, 1, 02, 0, 0).with(TemporalAdjusters.dayOfWeekInMonth(1, DayOfWeek.SUNDAY));
		
		if(date.isAfter(dayLightStart) && date.isBefore(dayLightEnd)) return true;
		else return false;
	}
	
	public static String TimeLabel(String dateString) {
		/*
		 * When Daylight the market is open from 1330 UTC to 2000 UTC
		 * else Market is open from 1430 UTC to 2100 UTC
		 */
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
		LocalDateTime date = LocalDateTime.parse(dateString, formatter);
		LocalDateTime marketOpenDaylight = date.withHour(13).withMinute(30).withSecond(0);
		LocalDateTime morningEndsDaylight = date.withHour(15).withMinute(0).withSecond(0);
		LocalDateTime noonEndsDaylight = date.withHour(18).withMinute(0).withSecond(0);
		LocalDateTime marketCloseDaylight = date.withHour(20).withMinute(0).withSecond(0);
		
		boolean isDaylight = isDaylightSaving(date);
		if(isDaylight) {
			if(date.isBefore(marketOpenDaylight.plusMinutes(1))) return TickerConstants.preMarket;
			if(date.isAfter(marketOpenDaylight) && date.isBefore(morningEndsDaylight.plusMinutes(1))) return TickerConstants.morning;
			if(date.isAfter(morningEndsDaylight) && date.isBefore(noonEndsDaylight.plusMinutes(1))) return TickerConstants.midDay;
			if(date.isAfter(noonEndsDaylight) && date.isBefore(marketCloseDaylight.plusMinutes(1))) return TickerConstants.afternoon;
			if(date.isAfter(marketCloseDaylight)) return TickerConstants.postMarket;
		}
		else {
			if(date.isBefore(marketOpenDaylight.plusHours(1).plusMinutes(1))) return TickerConstants.preMarket;
			if(date.isAfter(marketOpenDaylight.plusHours(1)) && date.isBefore(morningEndsDaylight.plusHours(1).plusMinutes(1))) return TickerConstants.morning;
			if(date.isAfter(morningEndsDaylight.plusHours(1)) && date.isBefore(noonEndsDaylight.plusHours(1).plusMinutes(1))) return TickerConstants.midDay;
			if(date.isAfter(noonEndsDaylight.plusHours(1)) && date.isBefore(marketCloseDaylight.plusHours(1).plusMinutes(1))) return TickerConstants.afternoon;
			if(date.isAfter(marketCloseDaylight.plusHours(1))) return TickerConstants.postMarket;
		}
		return "??";
		
	}
}
