package alpacaPojo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AssetsPojo {

	private String id;
	private String json_class;
	private String exchange;
	private String symbol;
	private String status;
	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	private boolean tradable;
	private boolean marginable;
	private boolean shortable;
	private boolean easy_to_borrow;
	private boolean fractionable;
	
	@Override
    public String toString() {
        return "{" +
                "id='" + id + '\'' +
                ", class='" + json_class + '\'' +
                ", exchange='" + exchange + '\'' +
                ", symbol='" + symbol + '\'' +
                ", status='" + status + '\'' +
                ", tradable='" + tradable + '\'' +
                ", marginable='" + marginable + '\'' +
                ", shortable='" + shortable + '\'' +
                ", easy_to_borrow='" + easy_to_borrow + '\'' +
                ", fractionable='" + fractionable + '\'' +
                '}';
    }
	@JsonProperty("class")
	public String getJson_class() {
		return json_class;
	}
	public void setJson_class(String json_class) {
		this.json_class = json_class;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getExchange() {
		return exchange;
	}
	public void setExchange(String exchange) {
		this.exchange = exchange;
	}
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public boolean isTradable() {
		return tradable;
	}
	public void setTradable(boolean tradable) {
		this.tradable = tradable;
	}
	public boolean isMarginable() {
		return marginable;
	}
	public void setMarginable(boolean marginable) {
		this.marginable = marginable;
	}
	public boolean isShortable() {
		return shortable;
	}
	public void setShortable(boolean shortable) {
		this.shortable = shortable;
	}
	public boolean isEasy_to_borrow() {
		return easy_to_borrow;
	}
	public void setEasy_to_borrow(boolean easy_to_borrow) {
		this.easy_to_borrow = easy_to_borrow;
	}
	public boolean isFractionable() {
		return fractionable;
	}
	public void setFractionable(boolean fractionable) {
		this.fractionable = fractionable;
	}
}
