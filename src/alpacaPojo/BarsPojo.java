package alpacaPojo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BarsPojo {

	//Time Format: "2021-03-24T13:30:00Z"
	private String time;
	private String openPrice;
	private String highPrice;
	private String lowPrice;
	private String closePrice;
	private String volume;
	@Override
    public String toString() {
        return "{" +
                "time='" + time + '\'' +
                ", openPrice='" + openPrice + '\'' +
                ", highPrice='" + highPrice + '\'' +
                ", lowPrice='" + lowPrice + '\'' +
                ", closePrice='" + closePrice + '\'' +
                ", volume='" + volume + '\'' +
                '}';
    }
	@JsonProperty("t")
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	@JsonProperty("o")
	public String getOpenPrice() {
		return openPrice;
	}
	public void setOpenPrice(String openPrice) {
		this.openPrice = openPrice;
	}
	@JsonProperty("h")
	public String getHighPrice() {
		return highPrice;
	}
	public void setHighPrice(String highPrice) {
		this.highPrice = highPrice;
	}
	@JsonProperty("l")
	public String getLowPrice() {
		return lowPrice;
	}
	public void setLowPrice(String lowPrice) {
		this.lowPrice = lowPrice;
	}
	@JsonProperty("c")
	public String getClosePrice() {
		return closePrice;
	}
	public void setClosePrice(String closePrice) {
		this.closePrice = closePrice;
	}
	@JsonProperty("v")
	public String getVolume() {
		return volume;
	}
	public void setVolume(String volume) {
		this.volume = volume;
	}
}
