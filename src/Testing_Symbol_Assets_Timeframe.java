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

public class Testing_Symbol_Assets_Timeframe {

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
	private static final LocalDateTime marketCloseTime = LocalDate.now().atTime(15,55);

	public static void main(String[] args) throws IOException, InterruptedException {

		AlpacaApiCaller alpacaAccount = new AlpacaApiCaller(alpacaKey, secretId, alpacaKeyValue, secretIdValue, headerContentType,
				headerContentTypeValue, alpacaKeyRealTime, secretIdRealTime, paperUrl, realtimeUrl, activeVersion, inactiveVersion, ordersEndpoint, accountEndpoint, 
				lastQuoteEndpoint, lastTradeEndpoint, positionsEndpoint, assetsEndpoint, clockEndpoint);

		String localTime = String.valueOf(LocalDateTime.now().getDayOfYear()) + String.valueOf(LocalDateTime.now().getHour()) + String.valueOf(LocalDateTime.now().getMinute()) + String.valueOf(LocalDateTime.now().getSecond());
		
		AssetsPojo assets[] = alpacaAccount.getAssets();

		File filetrue = new File("C:\\Users\\nakul\\Documents\\outputActiveAssets - " + localTime + ".txt");
		File fileHighVol = new File("C:\\Users\\nakul\\Documents\\outputHighVol - " + localTime + ".txt");
		File fileLowVol = new File("C:\\Users\\nakul\\Documents\\outputLowVol - " + localTime + ".txt");
		File fileHighVolHighPrice = new File("C:\\Users\\nakul\\Documents\\outputHighVolHighPrice - " + localTime + ".txt");
		File fileUltraLowVol = new File("C:\\Users\\nakul\\Documents\\outputUltraLowVol - " + localTime + ".txt");
		File fileLowCost = new File("C:\\Users\\nakul\\Documents\\outputLowCost - " + localTime + ".txt");
		File filePennyStocks = new File("C:\\Users\\nakul\\Documents\\outputPennyStocks - " + localTime + ".txt");
		PrintWriter pw1 = new PrintWriter(new FileOutputStream(filetrue, true));
		PrintWriter pw2 = new PrintWriter(new FileOutputStream(fileHighVol, true));
		PrintWriter pw3 = new PrintWriter(new FileOutputStream(fileLowVol, true));
		PrintWriter pw4 = new PrintWriter(new FileOutputStream(fileHighVolHighPrice, true));
		PrintWriter pw5 = new PrintWriter(new FileOutputStream(fileUltraLowVol, true));
		PrintWriter pw6 = new PrintWriter(new FileOutputStream(fileLowCost, true));
		PrintWriter pw7 = new PrintWriter(new FileOutputStream(filePennyStocks, true));
		int assetTotalList = assets.length;
		pw1.write("Exchange" + "\t" + "Symbol" + "\t" + "Fractionable" 
				+ "\t" + "Shortable" + "\t" + "Tradable" + "\t"+ "Date" + "\t\t\t" + "Open"
				+ "\t" + "Close" + "\t" + "HIGH" + "\t" + "LOW" + "\t"+ "HL%" + "\t" + "Volume"+  "\t" + "Name" + "\n");
		pw2.write("Exchange" + "\t" + "Symbol" + "\t" + "Fractionable" 
				+ "\t" + "Shortable" + "\t" + "Tradable" + "\t"+ "Date" + "\t\t\t" + "Open"
				+ "\t" + "Close" + "\t" + "HIGH" + "\t" + "LOW" + "\t"+ "HL%" + "\t" + "Volume"+  "\t" + "Name" + "\n");
		pw3.write("Exchange" + "\t" + "Symbol" + "\t" + "Fractionable" 
				+ "\t" + "Shortable" + "\t" + "Tradable" + "\t"+ "Date" + "\t\t\t" + "Open"
				+ "\t" + "Close" + "\t" + "HIGH" + "\t" + "LOW" + "\t"+ "HL%" + "\t" + "Volume"+  "\t" + "Name" + "\n");
		pw4.write("Exchange" + "\t" + "Symbol" + "\t" + "Fractionable" 
				+ "\t" + "Shortable" + "\t" + "Tradable" + "\t"+ "Date" + "\t\t\t" + "Open"
				+ "\t" + "Close" + "\t" + "HIGH" + "\t" + "LOW" + "\t"+ "HL%" + "\t" + "Volume"+  "\t" + "Name" + "\n");
		pw5.write("Exchange" + "\t" + "Symbol" + "\t" + "Fractionable" 
				+ "\t" + "Shortable" + "\t" + "Tradable" + "\t"+ "Date" + "\t\t\t" + "Open"
				+ "\t" + "Close" + "\t" + "HIGH" + "\t" + "LOW" + "\t"+ "HL%" + "\t" + "Volume"+  "\t" + "Name" + "\n");
		pw6.write("Exchange" + "\t" + "Symbol" + "\t" + "Fractionable" 
				+ "\t" + "Shortable" + "\t" + "Tradable" + "\t"+ "Date" + "\t\t\t" + "Open"
				+ "\t" + "Close" + "\t" + "HIGH" + "\t" + "LOW" + "\t"+ "HL%" + "\t" + "Volume"+  "\t" + "Name" + "\n");
		pw7.write("Exchange" + "\t" + "Symbol" + "\t" + "Fractionable" 
				+ "\t" + "Shortable" + "\t" + "Tradable" + "\t"+ "Date" + "\t\t\t" + "Open"
				+ "\t" + "Close" + "\t" + "HIGH" + "\t" + "LOW" + "\t"+ "HL%" + "\t" + "Volume"+  "\t" + "Name" + "\n");
		for (int i = 0; i < assetTotalList; i++){
//			if("active".equals(assets[i].getStatus()) && assets[i].isFractionable() && assets[i].isMarginable() && assets[i].isShortable() && assets[i].isEasy_to_borrow() && assets[i].isTradable()) {
			if("active".equals(assets[i].getStatus()) && assets[i].isTradable()) {
				TimeframePojo timeframe = alpacaAccount.getTimeframe(assets[i].getSymbol(), "2021-06-22", "2021-06-22", "1Day");
				if (timeframe.getBars() == null || timeframe.getBars().length == 0) {
					System.out.println("Error getting Bar for symbol: " + assets[i].getSymbol());
					continue;
				}
				BarsPojo bars = timeframe.getBars()[0];
				Double highLowPercent = (Double.valueOf(bars.getHighPrice()) - Double.valueOf(bars.getLowPrice()))*100/Double.valueOf(bars.getClosePrice());
				DecimalFormat df = new DecimalFormat("#.##");
				System.out.println(assets[i].getExchange() + "\t\t" + assets[i].getSymbol() + "\t"
						+ assets[i].isFractionable() + "\t\t" + assets[i].isShortable() + "\t\t" 
						+ assets[i].isTradable()  + "\t\t" + bars.getTime() + "\t" 
						+ bars.getOpenPrice() + "\t" + bars.getClosePrice() + "\t" + bars.getHighPrice() + "\t" + bars.getLowPrice() + "\t" 
						+ df.format(highLowPercent) + "\t" + bars.getVolume() + "\t" + assets[i].getName());
				pw1.write(assets[i].getExchange() + "\t\t" + assets[i].getSymbol() + "\t"
						+ assets[i].isFractionable() + "\t\t" + assets[i].isShortable() + "\t\t" 
						+ assets[i].isTradable()  + "\t\t" + bars.getTime() + "\t" 
						+ bars.getOpenPrice() + "\t" + bars.getClosePrice() + "\t" + bars.getHighPrice() + "\t" + bars.getLowPrice() + "\t" 
						+ df.format(highLowPercent) + "\t" + bars.getVolume() + "\t" + assets[i].getName() + "\n");
				if(highLowPercent.compareTo(3.00)>0 && Double.valueOf(bars.getClosePrice()).compareTo(100.0) < 0 && Double.valueOf(bars.getVolume()).compareTo(3000000.0) > 0)
				{
					pw2.write(assets[i].getExchange() + "\t\t" + assets[i].getSymbol() + "\t"
							+ assets[i].isFractionable() + "\t\t" + assets[i].isShortable() + "\t\t" 
							+ assets[i].isTradable()  + "\t\t" + bars.getTime() + "\t" 
							+ bars.getOpenPrice() + "\t" + bars.getClosePrice() + "\t" + bars.getHighPrice() + "\t" + bars.getLowPrice() + "\t" 
							+ df.format(highLowPercent) + "\t" + bars.getVolume() + "\t" + assets[i].getName() + "\n");
				}
				if(highLowPercent.compareTo(3.00)>0 && Double.valueOf(bars.getClosePrice()).compareTo(60.0) > 0 && Double.valueOf(bars.getVolume()).compareTo(3000000.0) > 0)
				{
					pw4.write(assets[i].getExchange() + "\t\t" + assets[i].getSymbol() + "\t"
							+ assets[i].isFractionable() + "\t\t" + assets[i].isShortable() + "\t\t" 
							+ assets[i].isTradable()  + "\t\t" + bars.getTime() + "\t" 
							+ bars.getOpenPrice() + "\t" + bars.getClosePrice() + "\t" + bars.getHighPrice() + "\t" + bars.getLowPrice() + "\t" 
							+ df.format(highLowPercent) + "\t" + bars.getVolume() + "\t" + assets[i].getName() + "\n");
				}
				if(Double.valueOf(bars.getVolume()).compareTo(500000.0) < 0 && Double.valueOf(bars.getVolume()).compareTo(100000.0) > 0)
				{
					pw3.write(assets[i].getExchange() + "\t\t" + assets[i].getSymbol() + "\t"
							+ assets[i].isFractionable() + "\t\t" + assets[i].isShortable() + "\t\t" 
							+ assets[i].isTradable()  + "\t\t" + bars.getTime() + "\t" 
							+ bars.getOpenPrice() + "\t" + bars.getClosePrice() + "\t" + bars.getHighPrice() + "\t" + bars.getLowPrice() + "\t" 
							+ df.format(highLowPercent) + "\t" + bars.getVolume() + "\t" + assets[i].getName() + "\n");
				}
				if(Double.valueOf(bars.getVolume()).compareTo(100000.0) < 0)
				{
					pw5.write(assets[i].getExchange() + "\t\t" + assets[i].getSymbol() + "\t"
							+ assets[i].isFractionable() + "\t\t" + assets[i].isShortable() + "\t\t" 
							+ assets[i].isTradable()  + "\t\t" + bars.getTime() + "\t" 
							+ bars.getOpenPrice() + "\t" + bars.getClosePrice() + "\t" + bars.getHighPrice() + "\t" + bars.getLowPrice() + "\t" 
							+ df.format(highLowPercent) + "\t" + bars.getVolume() + "\t" + assets[i].getName() + "\n");
				}
				if(highLowPercent.compareTo(3.00)>0 && Double.valueOf(bars.getClosePrice()).compareTo(10.0) < 0 && Double.valueOf(bars.getVolume()).compareTo(3000000.0) > 0)
				{
					pw6.write(assets[i].getExchange() + "\t\t" + assets[i].getSymbol() + "\t"
							+ assets[i].isFractionable() + "\t\t" + assets[i].isShortable() + "\t\t" 
							+ assets[i].isTradable()  + "\t\t" + bars.getTime() + "\t" 
							+ bars.getOpenPrice() + "\t" + bars.getClosePrice() + "\t" + bars.getHighPrice() + "\t" + bars.getLowPrice() + "\t" 
							+ df.format(highLowPercent) + "\t" + bars.getVolume() + "\t" + assets[i].getName() + "\n");
				}
				if(Double.valueOf(bars.getClosePrice()).compareTo(4.0) < 0)
				{
					pw7.write(assets[i].getExchange() + "\t\t" + assets[i].getSymbol() + "\t"
							+ assets[i].isFractionable() + "\t\t" + assets[i].isShortable() + "\t\t" 
							+ assets[i].isTradable()  + "\t\t" + bars.getTime() + "\t" 
							+ bars.getOpenPrice() + "\t" + bars.getClosePrice() + "\t" + bars.getHighPrice() + "\t" + bars.getLowPrice() + "\t" 
							+ df.format(highLowPercent) + "\t" + bars.getVolume() + "\t" + assets[i].getName() + "\n");
				}
				continue;
			}
			if("active".equals(assets[i].getStatus()) && assets[i].isMarginable() && assets[i].isShortable() && assets[i].isEasy_to_borrow() && assets[i].isTradable()) {
				TimeframePojo timeframe = alpacaAccount.getTimeframe(assets[i].getSymbol(), "2021-06-08", "2021-06-08", "1Day");
				if (timeframe.getBars() == null || timeframe.getBars().length == 0) {
					System.out.println("Error getting Bar for symbol: " + assets[i].getSymbol());
					continue;
				}
				BarsPojo bars = timeframe.getBars()[0];
				Double highLowPercent = (Double.valueOf(bars.getHighPrice()) - Double.valueOf(bars.getLowPrice()))*100/Double.valueOf(bars.getClosePrice());
				DecimalFormat df = new DecimalFormat("#.##");
				if(Double.valueOf(bars.getClosePrice()).compareTo(7.0) < 0)
				{
					pw7.write(assets[i].getExchange() + "\t\t" + assets[i].getSymbol() + "\t"
							+ assets[i].isFractionable() + "\t\t" + assets[i].isShortable() + "\t\t" 
							+ assets[i].isTradable()  + "\t\t" + bars.getTime() + "\t" 
							+ bars.getOpenPrice() + "\t" + bars.getClosePrice() + "\t" + bars.getHighPrice() + "\t" + bars.getLowPrice() + "\t" 
							+ df.format(highLowPercent) + "\t" + bars.getVolume() + "\t" + assets[i].getName() + "\n");
				}
			}
		}
		pw1.close();
		pw2.close();
		pw3.close();
		pw4.close();
		pw5.close();
		pw6.close();
		pw7.close();
	}
}
