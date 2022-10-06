package Backtesting;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import alpacaPojo.TickerOhlc;

public class PatternRecognitionBacktesting4Point {
	//static String filename;
	static List<ArrayList<TickerOhlc>> ticker = new ArrayList<ArrayList<TickerOhlc>>();
	static File targetFolder = new File("C:\\Users\\nakul\\Documents\\HistoricalData_VWAP");
	static File file = new File("C:\\Users\\nakul\\Documents\\outputPatternRecognition" + LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli() + ".csv");
	static PrintWriter pw1;
	public static void main(String[] args) throws FileNotFoundException {
		pw1 = new PrintWriter(new FileOutputStream(file), true);
		PatternRecognitionBacktesting4Point pr = new PatternRecognitionBacktesting4Point();
		System.out.println("Symbol,PatternCombination,Occurences,Increase1,Decrease1,%1Increase,%1Decrease,Increase3,Decrease3,%3Increase,%3Decrease,Increase5,Decrease5,%5Increase,%5Decrease");
		pw1.write("Symbol,PatternCombination,Occurences,Increase1,Decrease1,%1Increase,%1Decrease,Increase3,Decrease3,%3Increase,%3Decrease,Increase5,Decrease5,%5Increase,%5Decrease,Opening,Closing");
		//		ticker = new ArrayList<ArrayList<TickerOhlc>>();
		//		ticker = pr.readFileAsList("MinData - AADI_2021-06-01_2022-06-28.txt");
		//		pr.recognizePattern();
		for(File file : targetFolder.listFiles()) {
			ticker = new ArrayList<ArrayList<TickerOhlc>>();
			ticker = pr.readFileAsList(file.getName());
			pr.recognizePattern();
		}	
		pw1.close();
	}

	private List<ArrayList<TickerOhlc>> readFileAsList(String file) {
		String fileName = "C:\\Users\\nakul\\Documents\\HistoricalData_VWAP\\" + file;
		List<ArrayList<TickerOhlc>> tickers = new ArrayList<ArrayList<TickerOhlc>>();
		//		String fileName = "/test/" + filename + ".txt";
		String dayDate = "";
		boolean firstLine = false;
		try {
			List<String> lines = Files.readAllLines(Paths.get(fileName),
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
		//		System.out.println("Time1, Point1, Time2, Point2, Time3, Point3, Time4, Point4, TimeMax, ValueMax, TimeLow, ValueLow, %Increase, %Decrease");
		for(int descent1 = -10; descent1 <=10 ; descent1++) {
			if(descent1 == 0) continue;
			for(int descent2 = -10; descent2 <=10 ; descent2= descent2+2) {
				String symbol = ticker.get(0).get(0).symbol;
				Double open = ticker.get(0).get(0).open;
				Double close = ticker.get(ticker.size() - 1).get(ticker.get(ticker.size() - 1).size() - 1).close;
				for(int descent3 = -10; descent3 <=10 ; descent3=descent3+2) {
					int foundCount = 0;
					int decreaseCount1 = 0;
					int increaseCount1 = 0;
					int decreaseCount3 = 0;
					int increaseCount3 = 0;
					int decreaseCount5 = 0;
					int increaseCount5 = 0;
					//					System.out.println("Pattern Combination: " + descent1 + ";" + descent2 + ";" + descent3);
					for(List<TickerOhlc> tickerList : ticker) {
						Pattern[] pattern = new Pattern[4];
						for(int i = 0 ; i < 4; i++) {
							pattern[i] = new Pattern();
						}
						boolean patternFound = false;
						for(int i = 1 ; i < tickerList.size() ; i++) {
							if(descent1 >= 0) {
								Double tempLow = pattern[0].point == null ? tickerList.get(i-1).low : pattern[0].point;
								Double percentIncrease = 100*(tickerList.get(i).high - tempLow)/tempLow;
								if(tickerList.get(i).low.compareTo(tempLow) < 0 && percentIncrease.compareTo((double)descent1) < 0 && pattern[1].point == null){
									pattern[0].point = tickerList.get(i).low;
									pattern[0].time = tickerList.get(i).date;
									continue;
								}
								if( percentIncrease.compareTo((double)descent1) > 0 && pattern[1].point == null) {
									pattern[1].point = tickerList.get(i).high;
									pattern[1].time = tickerList.get(i).date;
									continue;
								}
							}
							if(descent1 < 0) {
								Double tempHigh = pattern[0].point == null ? tickerList.get(i-1).high : pattern[0].point;
								Double percentDecrease = 100*(tickerList.get(i).low - tempHigh)/tempHigh;
								if(tickerList.get(i).high.compareTo(tempHigh) > 0 && percentDecrease.compareTo((double)descent1) > 0 && pattern[1].point == null){
									pattern[0].point = tickerList.get(i).high;
									pattern[0].time = tickerList.get(i).date;
									continue;
								}
								if( percentDecrease.compareTo((double)descent1) < 0 && pattern[1].point == null) {
									pattern[1].point = tickerList.get(i).low;
									pattern[1].time = tickerList.get(i).date;
									continue;
								}
							}
							if(descent2 >= 0 && pattern[0].point != null && pattern[1].point !=null) {
								Double percentIncrease = 100*(tickerList.get(i).high - pattern[1].point)/pattern[1].point;
								if(percentIncrease.compareTo((double)descent2) > 0  && pattern[2].point == null) {
									pattern[2].point = tickerList.get(i).high;
									pattern[2].time = tickerList.get(i).date;
									continue;
								}
							}
							if(descent2 < 0 && pattern[0].point != null && pattern[1].point !=null) {
								Double percentDecrease = 100*(tickerList.get(i).low - pattern[1].point)/pattern[1].point;
								if(percentDecrease.compareTo((double)descent2) < 0  && pattern[2].point == null) {
									pattern[2].point = tickerList.get(i).low;
									pattern[2].time = tickerList.get(i).date;
									continue;
								}
							}

							if(descent3 >= 0 && pattern[0].point != null && pattern[1].point !=null && pattern[2].point !=null) {
								Double percentIncrease = 100*(tickerList.get(i).high - pattern[2].point)/pattern[2].point;
								if(percentIncrease.compareTo((double)descent3) > 0  && pattern[3].point == null) {
									pattern[3].point = tickerList.get(i).high;
									pattern[3].time = tickerList.get(i).date;
									postHigh.point = pattern[3].point;
									postHigh.time = pattern[3].time;
									postLow.time = pattern[3].time;
									postLow.point = pattern[3].point;
									patternFound = true;
									continue;
								}
							}
							if(descent3 < 0 && pattern[0].point != null && pattern[1].point !=null && pattern[2].point !=null) {
								Double percentDecrease = 100*(tickerList.get(i).low - pattern[2].point)/pattern[2].point;
								if(percentDecrease.compareTo((double)descent3) < 0  && pattern[3].point == null) {
									pattern[3].point = tickerList.get(i).high;
									pattern[3].time = tickerList.get(i).date;
									postHigh.point = pattern[3].point;
									postHigh.time = pattern[3].time;
									postLow.time = pattern[3].time;
									postLow.point = pattern[3].point;
									patternFound = true;
									continue;
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
							Double increase = 100*(postHigh.point - pattern[3].point)/pattern[3].point;
							Double decrease = 100*(postLow.point - pattern[3].point)/pattern[3].point;
							//							System.out.println(pattern[0].toString() + ", " + pattern[1].toString() + ", " + pattern[2].toString() + ", " 
							//									+ pattern[3].toString() + ", " + postHigh.toString() + ", " + postLow.toString() + ", " + increase + ", " + decrease);	
							foundCount++;
							if(increase.compareTo(1.0) > 0) increaseCount1++;
							if(decrease.compareTo(-1.0) < 0) decreaseCount1++;
							if(increase.compareTo(3.0) > 0) increaseCount3++;
							if(decrease.compareTo(-3.0) < 0) decreaseCount3++;
							if(increase.compareTo(5.0) > 0) increaseCount5++;
							if(decrease.compareTo(-5.0) < 0) decreaseCount5++;
						}
					}

					Double increasePercent1 = (double)increaseCount1*100/foundCount;
					Double decreasePercent1 = (double)decreaseCount1*100/foundCount;
					Double increasePercent3 = (double)increaseCount3*100/foundCount;
					Double decreasePercent3 = (double)decreaseCount3*100/foundCount;
					Double increasePercent5 = (double)increaseCount5*100/foundCount;
					Double decreasePercent5 = (double)decreaseCount5*100/foundCount;
					if(foundCount > 23 && (increasePercent1.compareTo(94.00) > 0 || increasePercent3.compareTo(84.00) > 0 || increasePercent5.compareTo(74.00) > 0 ||
							decreasePercent1.compareTo(89.00) > 0 || decreasePercent3.compareTo(84.00) > 0 || decreasePercent5.compareTo(74.00) > 0 )) {
						System.out.println(symbol + "," + descent1 + ";" + descent2 + ";" + descent3 + "," + foundCount + "," + increaseCount1 + "," + decreaseCount1 + "," + increasePercent1 + "," + decreasePercent1
								+ "," + increaseCount3 + "," + decreaseCount3 + "," + increasePercent3 + "," + decreasePercent3
								+ "," + increaseCount5 + "," + decreaseCount5 + "," + increasePercent5 + "," + decreasePercent5 + "," + open + "," + close);
						pw1.write(symbol + "," + descent1 + ";" + descent2 + ";" + descent3 + "," + foundCount + "," + increaseCount1 + "," + decreaseCount1 + "," + increasePercent1 + "," + decreasePercent1
								+ "," + increaseCount3 + "," + decreaseCount3 + "," + increasePercent3 + "," + decreasePercent3
								+ "," + increaseCount5 + "," + decreaseCount5 + "," + increasePercent5 + "," + decreasePercent5 + "," + open + "," + close + "\n");
					}
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
