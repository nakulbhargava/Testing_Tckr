import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/*
 * This program will take the input and split it into multiple files with daily, weekly and monthly splits
 */
public class SplitTickerFiles {

	static List<String> ticker = null;

	static String localTime = String.valueOf(LocalDateTime.now().getDayOfYear()) + String.valueOf(LocalDateTime.now().getHour()) + String.valueOf(LocalDateTime.now().getMinute()) + String.valueOf(LocalDateTime.now().getSecond());		
	static boolean dailyExtract = true, weeklyExtract = true, monthlyExtract = true; //Note: Keep only one as true
	public static void main(String[] args) throws FileNotFoundException {
		SplitTickerFiles demo = new SplitTickerFiles();
		demo.readAndWriteFileAsList();

	} 

	private void readAndWriteFileAsList() {
		String fileName = "/test/US1.MRO_210101_210515.txt";

		try {
			URI uri = this.getClass().getResource(fileName).toURI();
			List<String> lines = Files.readAllLines(Paths.get(uri),
					Charset.defaultCharset());
			List<String> tickerDailyData = new ArrayList<String>();
			List<String> tickerWeeklyData = new ArrayList<String>();
			List<String> tickerMonthlyData = new ArrayList<String>();
			TickerData daily, weekly, monthly;
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
			String dailyCompare = null;
			int firstDayOfMonth = 1;
			String firstDayOfWeek = "MONDAY";
			Date date;
			boolean newDailyFileNeeded = true, newWeeklyFileNeeded = true, newMonthlyFileNeeded = true;
			boolean firstDayOfWeekNotMet = true, firstDayOfMonthNotMet = true;
			boolean firstLine = false;
			String firstline = null;
			List<String> ticker = new ArrayList();
			File fileDaily, fileWeekly, fileMonthly;
			PrintWriter pwDaily = null, pwWeekly = null, pwMonthly = null;
			for (String line : lines) {
				List<String> splitLine = Arrays.asList(line.split(",", -1));
				if(!firstLine) {
					firstline = line; 
					firstLine = true;
					continue;
				}
				if(dailyExtract) {
					date = format.parse(splitLine.get(1));
					LocalDateTime currentDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
					if(newDailyFileNeeded) {
						fileDaily = new File("C:\\Users\\nakul\\eclipse-workspace\\TCKR\\src\\Ticker\\" + splitLine.get(0) + "_" + splitLine.get(1) + ".txt");
						pwDaily = new PrintWriter(new FileOutputStream(fileDaily));
						dailyCompare = currentDate.getDayOfWeek().toString();
						pwDaily.write(firstline + '\n');
						newDailyFileNeeded = false;						
					}
					if (dailyCompare.equals(currentDate.getDayOfWeek().toString())) {
						pwDaily.write(line + '\n');
					}
					else {
						newDailyFileNeeded = true;
						pwDaily.close();
					}
				}
				if(weeklyExtract) {
					date = format.parse(splitLine.get(1));
					LocalDateTime currentDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
					if(newWeeklyFileNeeded) {
						fileWeekly = new File("C:\\Users\\nakul\\eclipse-workspace\\TCKR\\src\\Ticker\\" + splitLine.get(0) + "_" + splitLine.get(1) + "_Weekly.txt");
						pwWeekly = new PrintWriter(new FileOutputStream(fileWeekly));
						pwWeekly.write(firstline + '\n');
						newWeeklyFileNeeded = false;						
					}
					if (!firstDayOfWeek.equals(currentDate.getDayOfWeek().toString())) {
						pwWeekly.write(line + '\n');
						firstDayOfWeekNotMet = false;
					}
					else if (firstDayOfWeekNotMet){
						pwWeekly.write(line + '\n');
					}
					else if(firstDayOfWeek.equals(currentDate.getDayOfWeek().toString())){
						newWeeklyFileNeeded = true;
						firstDayOfWeekNotMet = true;
						pwWeekly.close();
					}
				}
				if(monthlyExtract) {
					date = format.parse(splitLine.get(1));
					LocalDateTime currentDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
					if(newMonthlyFileNeeded) {
						fileMonthly = new File("C:\\Users\\nakul\\eclipse-workspace\\TCKR\\src\\Ticker\\" + splitLine.get(0) + "_" + splitLine.get(1) + "_Monthly.txt");
						pwMonthly = new PrintWriter(new FileOutputStream(fileMonthly));
						pwMonthly.write(firstline + '\n');
						newMonthlyFileNeeded = false;						
					}
					if (!(firstDayOfMonth == currentDate.getDayOfMonth())) {
						pwMonthly.write(line + '\n');
						firstDayOfMonthNotMet = false;
					}
					else if (firstDayOfMonthNotMet){
						pwMonthly.write(line + '\n');
					}
					else if(firstDayOfMonth == currentDate.getDayOfMonth()){
						newMonthlyFileNeeded = true;
						firstDayOfMonthNotMet = true;
						pwMonthly.close();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public class TickerData{
		String startDate;
		String endDate;
		public String getStartDate() {
			return startDate;
		}
		public void setStartDate(String startDate) {
			this.startDate = startDate;
		}
		public String getEndDate() {
			return endDate;
		}
		public void setEndDate(String endDate) {
			this.endDate = endDate;
		}
		public List<String> getTickerData() {
			return tickerData;
		}
		public void setTickerData(List<String> tickerData) {
			this.tickerData = tickerData;
		}
		List<String> tickerData;
	}
}

