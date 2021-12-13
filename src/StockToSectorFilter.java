import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class StockToSectorFilter {
	public static void main(String args[])
	{
		try
		{
			File file = new File("C:\\Users\\nakul\\Desktop\\Ticker\\nasdaq_screener.csv");
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = "", oldtext = "";
			String symbols[] = new String[]{ "XPEV", "UBER", "SNAP", "PLUG", "NIO", "MRO", "LYFT", "GPS", "GOTU", "DKNG", "CCL", "BNGO", "AMWL", "ACAD", "LCTX",
					"CLSN", "CMCM", "CPRX", "CRBP", "SOLO", "CYRN", "SNDL", "ISR", "EVFM", "HDSN", "REI", "NXE", "UUUU", 
					"NGD", "GSAT", "NMTR", "STAF", "SRGA", "WTER", "CWBR", "DARE", "GERN", "IDRA", "ARDX", "ANY" , "ADMP", "NXTD" , "NEPT", "XSPA", "VISL", "SONM", "ECOR", "XELA" , "VERB", "SHIP" , "SFET", 
					"SNCR", "PSTV", "PROG", "POAI", "PIXY" , "PHUN", "PBTS" , "NAOV", "NAKD", "LPTH", "JUPW", "JAGX", "ITRM", "IO", "INFI", "HVBT", 
					"GSAT", "GSMG", "GSS", "GLBS", "GEG", "FAMI", "EBON", "DYNT", "DWSN", "DTSS", "CTRM", "AMD", "BILI", "BNTX", "CVX", "EBAY", "ETSY", 
					"EW", "FSLR", "FTV", "FUTU", "JCI", "PDD", "PENN", "PTON", "SAVA", "QSR", "SWKS", "SQ", "TEAM", "TSLA", "ZEN", "TNA", "BYND", "CHKP",
					"COIN", "EXAS", "FIVN", "HAS", "HCA", "HLT", "MAR", "NVAX", "PII", "PKI", "PXD", "ROKU", "SE", "SHW", "TXRH", "UPST", "MRNA", "ABNB",
					"WLTW", "WYNN", "ZEN", "ZLAB", "ZM", "Z", "BABA", "AON", "CHWY", "BIDU", "HAS", "NTES", "TWTR", "RCL",
					"BDR", "ACST" , "ADMA", "ADVM" , "AEI", "AKBA", "ALNA", "AMPE", "AREC", "ARPO" , "ASXC", "BIOL" , "BOXL", 
					"BRQS", "BXRX", "CASI", "CCO", "CEI" , "CEMI", "CFMS" , "BSQR", "CIDM", "CIG", "COCP", "DNN", "DPW", "EDU", "ESGC", "EVOK", 
					"GNUS", "GOED", "GTE", "FTFT", "IAG", "IDEX", "IGC", "IBIO", "ITP", "INUV", "IZEA", "JOB", "KOS", "LGHL", "LITB", "LLNW", "LMNL", 
					"LPCN", "LYG", "METX", "MOTS", "MREO", "NAT", "NBRV", "NBY", "NCMI", "NEW", "NGL", "OGEN", "ONE", "PEI", "PLX", "PTN", "PXS", "QD",
					"REED", "REPH", "RGLS", "SEEL", "SESN", "SOS", "SPPI", "TBLT", "TELL", "TLGT", "TLMD", "TMBR", "TNXP", "TRVN", "TTOO", "TXMD", "UEC", "URG",
					"VEON", "VERO", "VTGN", "VYNE", "WATT", "WORX", "WPG", "WTRH", "XERS", "YCBD", "ZIOP", "ZOM", "ZSAN", "DFFN"};
			
			boolean headerNotPresent = true;
			FileWriter writer = new FileWriter("C:\\Users\\nakul\\Desktop\\Ticker\\StockToSectorMapping.csv");
			while((line = reader.readLine()) != null)
			{
				if(headerNotPresent) {
					writer.write(line + "\n");
					headerNotPresent = false;
					continue;
				}
				String[] words = line.split(",");
				for(String symbol:symbols) {
					if(symbol.equals(words[0]))
						writer.append(line + "\n");
				}
			}
			reader.close();
			writer.close();
		
//			FileWriter writer = new FileWriter("C:\\Users\\nakul\\eclipse-workspace\\TCKR\\src\\test\\FinamFound.txt");
//			writer.write(newtext);writer.close();
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
		}
	}
	public static boolean hasWord(File logFile, String word){ 
		try{
			BufferedReader buff=new BufferedReader(new FileReader(logFile));
			String s;
			while((s=buff.readLine())!=null){ 
				if(s.trim().contains(word)){
					buff.close();
					return true;
				}               
			}
			buff.close();
		}catch(Exception e){e.printStackTrace();}
		
		return false;
	}
}
