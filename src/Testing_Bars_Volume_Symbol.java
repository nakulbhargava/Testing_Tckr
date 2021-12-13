import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;

import alpacaPojo.AlpacaApiCaller;
import alpacaPojo.BarsPojo;
import alpacaPojo.PositionsPojo;
import alpacaPojo.TimeframePojo;

//import com.fasterxml.jackson.databind.ObjectMapper;

// HTTP basic authentication example in Java using the RTC Server RESTful API
public class Testing_Bars_Volume_Symbol {

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
	private static final String dataUrl = "https://data.alpaca.markets";
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
				headerContentTypeValue, alpacaKeyRealTime, secretIdRealTime, paperUrl, realtimeUrl, dataUrl, activeVersion, inactiveVersion, ordersEndpoint, accountEndpoint, 
				lastQuoteEndpoint, lastTradeEndpoint, positionsEndpoint, assetsEndpoint, clockEndpoint);
		String localTime = String.valueOf(LocalDateTime.now().getDayOfYear()) + String.valueOf(LocalDateTime.now().getHour()) + String.valueOf(LocalDateTime.now().getMinute()) + String.valueOf(LocalDateTime.now().getSecond());

//		String[] symbols = new String[]{ "XPEV", "UBER", "SNAP", "PLUG", "NIO", "MRO", "LYFT", "GPS", "GOTU", "DKNG", "CCL", "BNGO", "AMWL", "ACAD", "LCTX",
//				 "CLSN", "CMCM", "CPRX", "CRBP", "SOLO", "CYRN", "SNDL", "ISR", "EVFM", "HDSN", "REI", "NXE", "UUUU", 
//				 "NGD", "GSAT", "NMTR", "STAF", "SRGA", "WTER", "CWBR", "DARE", "GERN", "IDRA"};
//		String[] symbols = new String[]{"", "" , "", "" , "", "", "", "", "", "" , "", "" , "", "", "", "", "", "" , "", "" , "", "", "", ""};
//		String[] symbols = new String[]{"ARDX", "ANY" , "ADMP", "NXTD" , "NEPT", "XSPA", "VISL", "SONM", "ECOR", "XELA" , "VERB", "SHIP" , "SFET", 
//				"SNCR", "PSTV", "PROG", "POAI", "PIXY" , "PHUN", "PBTS" , "NAOV", "NAKD", "LPTH", "JUPW", "JAGX", "ITRM", "IO", "INFI", "HVBT", 
//				"GSAT", "GSMG", "GSS", "GLBS", "GEG", "FAMI", "EBON", "DYNT", "DWSN", "DTSS", "CTRM", "AMD", "BILI", "BNTX", "CVX", "EBAY", "ETSY", 
//				"EW", "FSLR", "FTV", "FUTU", "JCI", "PDD", "PENN", "PTON", "SAVA", "QSR", "SWKS", "SQ", "TEAM", "TSLA", "ZEN", "TNA", "BYND", "CHKP",
//				"COIN", "EXAS", "FIVN", "HAS", "HCA", "HLT", "MAR", "NVAX", "PII", "PKI", "PXD", "ROKU", "SE", "SHW", "TXRH", "UPST", "MRNA", "ABNB",
//				"WLTW", "WYNN", "ZEN", "ZLAB", "ZM", "Z", "BABA", "AON", "CHWY", "BIDU", "HAS", "NTES", "TWTR", "RCL"};
//		String[] symbols = new String[]{"BDR", "ACST" , "ADMA", "ADVM" , "AEI", "AKBA", "ALNA", "AMPE", "AREC", "ARPO" , "ASXC", "BIOL" , "BOXL", 
//				"BRQS", "BXRX", "CASI", "CCO", "CEI" , "CEMI", "CFMS" , "BSQR", "CIDM", "CIG", "COCP", "DNN", "DPW", "EDU", "ESGC", "EVOK", 
//				"GNUS", "GOED", "GTE", "FTFT", "IAG", "IDEX", "IGC", "IBIO", "ITP", "INUV", "IZEA", "JOB", "KOS", "LGHL", "LITB", "LLNW", "LMNL", 
//				"LPCN", "LYG", "METX", "MOTS", "MREO", "NAT", "NBRV", "NBY", "NCMI", "NEW", "NGL", "OGEN", "ONE", "PEI", "PLX", "PTN", "PXS", "QD",
//				"REED", "REPH", "RGLS", "SEEL", "SESN", "SOS", "SPPI", "TBLT", "TELL", "TLGT", "TLMD", "TMBR", "TNXP", "TRVN", "TTOO", "TXMD", "UEC", "URG",
//				"VEON", "VERO", "VTGN", "VYNE", "WATT", "WORX", "WPG", "WTRH", "XERS", "YCBD", "ZIOP", "ZOM", "ZSAN", "DFFN"};
		
//		String symbols[] = new String[]{"TIGR", "TASK", "SUNW", "SQL",
//				"SPRT", "SPI" , "SOL", "SOAC" , "SNMP", "SNGX", "SMFR", "SLQT", "SLI", "SKIN" , "SIOX", "SIGA" , 
//				"SHCR", "SFTW", "RSKD", "ROOT", "RKLB", "RIDE" , "RIBT", "REKR" , "REE", "REAL", "RDW", "PXLW", "DQ", "MGNX", "HGEN", "ACTG",
//				"ABUS", "ACI" , "ADAP", "AERI" , "AFRM", "AIHS", "ADXS", "AGFY", "ALIT", "ARRY" , "APLS", "ARQQ" , 
//				"ALZN", "APP", "ASTR", "AVGR", "AUPH", "BBIG" , "ATER", "BEST" , "ATOS", "BEKE", "BCTX", "BHAT"};
		
		String symbols[] = new String[]{"BODY", "BLDE", "BGFV", "BITF",
				"BMBL", "CANO" , "BTRS", "CAN" , "CCCS", "BTBT", "BTCM", "BTX", "CBAT", "CAR" , "CCJ", "CEMI" , 
				"CERT", "CELH", "CGEN", "CLOV", "COOK", "CRDF" , "CRIS", "CWEB" , "CXM", "DATS", "CURV", "DSS", "DIDI", "DOCS", "DOMA", "DOYU",
				"DVAX", "DTST" , "EFTR", "ELOX" , "DYN", "ENDP", "EOLS", "EEIQ", "ENVB", "FBRX" , "FCEL", "FIGS" , 
				"HLBZ", "HIMS" , "HUT", "HXL" , "HYZN", "IPDN", "IMPL", "INO", "IOVA", "IMGN" , "IRNT", "ISEE" ,
				"IVC", "IS" , "JKS", "JOBY" , "KNDI", "KPLT", "KAVL", "LOVE", "LAC", "MARA" , "MOHO", "MILE" , 
				"FINV", "FIZZ", "GEVO", "GOCO", "GOL", "GOED" , "GNLN", "GRNQ" , "HIMX", "HEXO", "HIPO", "GSM"};
		File file = new File("C:\\Users\\nakul\\Documents\\outputTimeframe - " + localTime + ".txt");
		PrintWriter pw = new PrintWriter(new FileOutputStream(file));
//		FileOutputStream fo = new FileOutputStream(file);

		//				AssetsPojo assets = alpacaAccount.getAssetSymbol(symbol);
		//PositionsPojo[] openPositions = alpacaAccount.getOpenPositions();
		for (String symbol : symbols) {
			TimeframePojo timeframe = alpacaAccount.getTimeframe(symbol, "2021-05-29", "2021-09-10", "1Day");
			BarsPojo bars[] = timeframe.getBars();
			Double sumHighLow = 0.00;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
			pw.write(symbol + "\t" + "Day" + "\t"
					+ "Open" + "\t" + "Close" + "\t" + "High" + "\t" + "Low" + "\t" 
					+ "HL%" + "\t" + "Volume" + "\n");
			System.out.println(symbol + "\t" + "Day" + "\t"
					+ "Open" + "\t" + "Close" + "\t" + "High" + "\t" + "Low" + "\t" 
					+ "HL%" + "\t" + "Volume");
			for(BarsPojo bar:bars) {
				Double highLowPercent = (Double.valueOf(bar.getHighPrice()) - Double.valueOf(bar.getLowPrice()))*100/Double.valueOf(bar.getClosePrice());
				DecimalFormat df = new DecimalFormat("#.##");
				pw.write(symbol + "\t" + sdf.parse(bar.getTime()) + "\t"
						+ bar.getOpenPrice() + "\t" + bar.getClosePrice() + "\t" + bar.getHighPrice() + "\t" + bar.getLowPrice() + "\t" 
						+ df.format(highLowPercent) + "\t" + bar.getVolume() + "\n");
				System.out.println(symbol + "\t" + sdf.parse(bar.getTime()) + "\t"
						+ bar.getOpenPrice() + "\t" + bar.getClosePrice() + "\t" + bar.getHighPrice() + "\t" + bar.getLowPrice() + "\t" 
						+ df.format(highLowPercent) + "\t" + bar.getVolume());
				sumHighLow = sumHighLow + highLowPercent;
			}
			pw.write("Weighted Average HighLow: " + sumHighLow/bars.length + "\n" );
			System.out.println("Weighted Average HighLow: " + sumHighLow/bars.length);
			Thread.sleep(1000);
		}
		pw.close();

	}
}