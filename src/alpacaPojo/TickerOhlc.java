package alpacaPojo;

public class TickerOhlc {
	public String symbol;
	public String date;
	public Double open;
	public Double high;
	public Double low;
	public Double close;
	public long volume;
	public long trades;
	public Double vwap;
	public Double dayAverage;
	public Double mean;
	@Override
	public String toString() {
		return "[date=" + date + ", open=" + open + ", high=" + high + ", low=" + low
				+ ", close=" + close + ", volume=" + volume + ", trades=" + trades + ", vwap=" + vwap + ", dayAverage="
				+ dayAverage + ", mean=" + mean + "]";
	}
}
