package Backtesting;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import alpacaPojo.BarsPojo;

public class Dataframes {
	List<BarsPojo> extendedHoursBars = new ArrayList<BarsPojo>();
	List<BarsPojo> inMarketBars = new ArrayList<BarsPojo>();;

	public Dataframes populateBars(BarsPojo allBars[]) throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
		Date marketStartDate = format.parse("2021-07-20T13:30:00Z");
		Date marketEndDate = format.parse("2021-07-20T20:00:00Z");
		//format.setTimeZone(TimeZone.getTimeZone("UTC"));
		for (BarsPojo bar : allBars) {
			Date currentBarDate = format.parse(bar.getTime());
			if(currentBarDate.before(marketStartDate) || currentBarDate.after(marketEndDate)) {
				extendedHoursBars.add(bar);
			}
			else {
				inMarketBars.add(bar);
			}
		}

		return this;
	}
}