package alpacaPojo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LastTradePojoV2 {

	private String price;
	private String size;
	private String exchange;
	private String timestamp;
	private String tradeId;
	private String tape;
	
	@JsonProperty("p")
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	@JsonProperty("s")
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	@JsonProperty("x")
	public String getExchange() {
		return exchange;
	}
	public void setExchange(String exchange) {
		this.exchange = exchange;
	}
	@JsonProperty("t")
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	@JsonProperty("i")
	public String getTradeId() {
		return tradeId;
	}
	public void setTradeId(String tradeId) {
		this.tradeId = tradeId;
	}
	@JsonProperty("z")
	public String getTape() {
		return tape;
	}
	public void setTape(String tape) {
		this.tape = tape;
	}

}
