package Backtesting;

import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import alpacaPojo.TickerOhlc;

public class PatternRecognitionBacktesting {
	static String filename = "MinData - AAME_2021-06-01_2022-06-28";
	static List<ArrayList<TickerOhlc>> ticker = new ArrayList<ArrayList<TickerOhlc>>();

	public static void main(String[] args) {
		PatternRecognitionBacktesting pr = new PatternRecognitionBacktesting();
		ticker = pr.readFileAsList();
		pr.recognizePattern();
	}

	private List<ArrayList<TickerOhlc>> readFileAsList() {
		List<ArrayList<TickerOhlc>> tickers = new ArrayList<ArrayList<TickerOhlc>>();
		String fileName = "/test/" + filename + ".txt";
		String dayDate = "";
		boolean firstLine = false;
		try {
			URI uri = this.getClass().getResource(fileName).toURI();
			List<String> lines = Files.readAllLines(Paths.get(uri),
					Charset.defaultCharset());
			ArrayList<TickerOhlc> tickerList = new ArrayList<TickerOhlc>();

			for (String line : lines) {
				List<String> splitLine = Arrays.asList(line.split(",", -1));
				if(!firstLine) {
					firstLine = true;
					dayDate = Arrays.asList(lines.get(1).split(",", -1)).get(1).substring(0, 11);
					continue;
				}
				TickerOhlc ticker = new TickerOhlc();
				ticker.symbol = splitLine.get(0);
				ticker.date = splitLine.get(1);
				ticker.open = Double.valueOf(splitLine.get(2));
				ticker.close = (Double.valueOf(splitLine.get(3)));
				ticker.high = (Double.valueOf(splitLine.get(4)));
				ticker.low = (Double.valueOf(splitLine.get(5)));
				if(dayDate.equals(splitLine.get(1).substring(0, 11))) {
					tickerList.add(ticker);
				}
				else {
					tickers.add(tickerList);
					tickerList = new ArrayList<TickerOhlc>();
					dayDate = splitLine.get(1).substring(0, 11);
					tickerList.add(ticker);

				}
			}
			return tickers;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	public void recognizePattern() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);
		Pattern postHigh = new Pattern();
		Pattern postLow = new Pattern();
		for(int descent1 = -10; descent1 <=10 ; descent1++) {
			if(descent1 == 0) continue;
			for(int descent2 = -10; descent2 <=10 ; descent2++) {
				for(int descent3 = -10; descent3 <=10 ; descent3++) {
					int descent4 =0;
					//					for(int descent4 = -10; descent4 <=10 ; descent4++) {
					System.out.println("Pattern Combination: " + descent1 + "," + descent2 + "," + descent3 + "," + descent4);
					for(List<TickerOhlc> tickerList : ticker) {
						Pattern[] pattern = new Pattern[5];
						for(int i = 0 ; i < 5; i++) {
							pattern[i] = new Pattern();
						}
						boolean patternFound = false;
						for(int i = 1 ; i < tickerList.size() ; i++) {
							if(descent1 >= 0) {
								Double percentIncrease = 100*(tickerList.get(i).high - tickerList.get(i-1).low)/tickerList.get(i-1).low;
								if(tickerList.get(i).low.compareTo(tickerList.get(i-1).low) < 0 && percentIncrease.compareTo((double)descent1) < 0 && pattern[1].point == null){
									pattern[0].point = tickerList.get(i-1).low;
									pattern[0].time = tickerList.get(i-1).date;
									continue;
								}
								if( percentIncrease.compareTo((double)descent1) > 0 && pattern[1].point == null) {
									pattern[1].point = tickerList.get(i).high;
									pattern[1].time = tickerList.get(i).date;
								}
							}
							if(descent1 < 0) {
								Double percentDecrease = 100*(tickerList.get(i).low - tickerList.get(i-1).high)/tickerList.get(i-1).low;
								if(tickerList.get(i).high.compareTo(tickerList.get(i-1).high) < 0 && percentDecrease.compareTo((double)descent1) > 0 && pattern[1].point == null){
									pattern[0].point = tickerList.get(i-1).high;
									pattern[0].time = tickerList.get(i-1).date;
									continue;
								}
								if( percentDecrease.compareTo((double)descent1) < 0 && pattern[1].point == null) {
									pattern[1].point = tickerList.get(i).low;
									pattern[1].time = tickerList.get(i).date;
								}
							}
							if(descent2 >= 0 && pattern[0].point != null && pattern[1].point !=null) {
								Double percentIncrease = 100*(tickerList.get(i).high - pattern[1].point)/pattern[1].point;
								if(percentIncrease.compareTo((double)descent2) > 0  && pattern[2].point == null) {
									pattern[2].point = tickerList.get(i).high;
									pattern[2].time = tickerList.get(i).date;
								}
							}
							if(descent2 < 0 && pattern[0].point != null && pattern[1].point !=null) {
								Double percentDecrease = 100*(tickerList.get(i).low - pattern[1].point)/pattern[1].point;
								if(percentDecrease.compareTo((double)descent2) < 0  && pattern[2].point == null) {
									pattern[2].point = tickerList.get(i).high;
									pattern[2].time = tickerList.get(i).date;
								}
							}

							if(descent3 >= 0 && pattern[0].point != null && pattern[1].point !=null && pattern[2].point !=null) {
								Double percentIncrease = 100*(tickerList.get(i).high - pattern[2].point)/pattern[2].point;
								if(percentIncrease.compareTo((double)descent3) > 0  && pattern[3].point == null) {
									pattern[3].point = tickerList.get(i).high;
									pattern[3].time = tickerList.get(i).date;
								}
							}
							if(descent3 < 0 && pattern[0].point != null && pattern[1].point !=null && pattern[2].point !=null) {
								Double percentDecrease = 100*(tickerList.get(i).low - pattern[2].point)/pattern[2].point;
								if(percentDecrease.compareTo((double)descent3) < 0  && pattern[3].point == null) {
									pattern[3].point = tickerList.get(i).high;
									pattern[3].time = tickerList.get(i).date;
								}
							}

							if(descent4 >= 0 && pattern[0].point != null && pattern[1].point !=null && pattern[2].point !=null  && pattern[3].point !=null) {
								Double percentIncrease = 100*(tickerList.get(i).high - pattern[3].point)/pattern[3].point;
								if(percentIncrease.compareTo((double)descent4) > 0  && pattern[4].point == null) {
									pattern[4].point = tickerList.get(i).high;
									pattern[4].time = tickerList.get(i).date;
									postHigh.point = pattern[4].point;
									postLow.point = pattern[4].point;
									patternFound = true;
								}
							}
							if(descent4 < 0 && pattern[0].point != null && pattern[1].point !=null && pattern[2].point !=null && pattern[3].point !=null) {
								Double percentDecrease = 100*(tickerList.get(i).low - pattern[3].point)/pattern[3].point;
								if(percentDecrease.compareTo((double)descent4) < 0  && pattern[4].point == null) {
									pattern[4].point = tickerList.get(i).high;
									pattern[4].time = tickerList.get(i).date;
									postHigh.point = pattern[4].point;
									postHigh.time = pattern[4].time;
									postLow.time = pattern[4].time;
									postLow.point = pattern[4].point;
									patternFound = true;
								}
							}
							if(patternFound) {
								if(tickerList.get(i).high.compareTo(postHigh.point) > 0) {
									postHigh.point = tickerList.get(i).high;
									postHigh.time = tickerList.get(i).date;
								}
								if(tickerList.get(i).low.compareTo(postLow.point) < 0) {
									postLow.point = tickerList.get(i).low;
									postLow.time = tickerList.get(i).date;
								}
							}
						}
						if(patternFound) {
							Double increase = 100*(postHigh.point - pattern[4].point)/pattern[4].point;
							Double decrease = 100*(postLow.point - pattern[4].point)/pattern[4].point;
							System.out.println(pattern[0].toString() + "," + pattern[1].toString() + "," + pattern[2].toString() + "," 
									+ pattern[3].toString() + "," + pattern[4].toString() + "," + postHigh.toString() + "," + postLow.toString() + "," + increase + "," + decrease);	
						}
					}
					//					}
				}
			}
		}
	}

	public class Pattern{
		Double point;
		String time;
		@Override
		public String toString() {
			return time + ", " + point;
		}

	}
}
