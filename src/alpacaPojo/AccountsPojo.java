package alpacaPojo;

public class AccountsPojo {

	private String id;
	private String account_number;
	private String status;
	private String currency;
	private String cash;
	private String portfolio_value;
	private String pattern_day_trader;
	private String trade_suspended_by_user;
	private String trading_blocked;
	private String transfers_blocked;
	private String account_blocked;
	private String created_at;
	private String shorting_enabled;
	private String long_market_value;
	private String short_market_value;
	private String equity;
	private String last_equity;
	private String multiplier;
	private String buying_power;
	private String initial_margin;
	private String maintenance_margin;
	private String sma;
	private String daytrade_count;
	private String last_maintenance_margin;
	private String daytrading_buying_power;
	private String regt_buying_power;
	
	@Override
    public String toString() {
        return "{" +
                "id='" + id + '\'' +
                ", account_number='" + account_number + '\'' +
                ", status='" + status + '\'' +
                ", currency='" + currency + '\'' +
                ", cash='" + cash + '\'' +
                ", portfolio_value='" + portfolio_value + '\'' +
                ", pattern_day_trader='" + pattern_day_trader + '\'' +
                ", trade_suspended_by_user='" + trade_suspended_by_user + '\'' +
                ", trading_blocked='" + trading_blocked + '\'' +
                ", transfers_blocked='" + transfers_blocked + '\'' +
                ", created_at='" + created_at + '\'' +
                ", shorting_enabled='" + shorting_enabled + '\'' +
                ", long_market_value='" + long_market_value + '\'' +
                ", short_market_value='" + short_market_value + '\'' +
                ", equity='" + equity + '\'' +
                ", last_equity='" + last_equity + '\'' +
                ", multiplier='" + multiplier + '\'' +
                ", buying_power='" + buying_power + '\'' +
                ", initial_margin='" + initial_margin + '\'' +
                ", maintenance_margin='" + maintenance_margin + '\'' +
                ", sma='" + sma + '\'' +
                ", daytrade_count='" + daytrade_count + '\'' +
                ", last_maintenance_margin='" + last_maintenance_margin + '\'' +
                ", daytrading_buying_power='" + daytrading_buying_power + '\'' +
                ", regt_buying_power='" + regt_buying_power + '\'' +
                '}';
    }
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getAccount_number() {
		return account_number;
	}
	public void setAccount_number(String account_number) {
		this.account_number = account_number;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public String getCash() {
		return cash;
	}
	public void setCash(String cash) {
		this.cash = cash;
	}
	public String getPortfolio_value() {
		return portfolio_value;
	}
	public void setPortfolio_value(String portfolio_value) {
		this.portfolio_value = portfolio_value;
	}
	public String getPattern_day_trader() {
		return pattern_day_trader;
	}
	public void setPattern_day_trader(String pattern_day_trader) {
		this.pattern_day_trader = pattern_day_trader;
	}
	public String getTrade_suspended_by_user() {
		return trade_suspended_by_user;
	}
	public void setTrade_suspended_by_user(String trade_suspended_by_user) {
		this.trade_suspended_by_user = trade_suspended_by_user;
	}
	public String getTrading_blocked() {
		return trading_blocked;
	}
	public void setTrading_blocked(String trading_blocked) {
		this.trading_blocked = trading_blocked;
	}
	public String getTransfers_blocked() {
		return transfers_blocked;
	}
	public void setTransfers_blocked(String transfers_blocked) {
		this.transfers_blocked = transfers_blocked;
	}
	public String getAccount_blocked() {
		return account_blocked;
	}
	public void setAccount_blocked(String account_blocked) {
		this.account_blocked = account_blocked;
	}
	public String getCreated_at() {
		return created_at;
	}
	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}
	public String getShorting_enabled() {
		return shorting_enabled;
	}
	public void setShorting_enabled(String shorting_enabled) {
		this.shorting_enabled = shorting_enabled;
	}
	public String getLong_market_value() {
		return long_market_value;
	}
	public void setLong_market_value(String long_market_value) {
		this.long_market_value = long_market_value;
	}
	public String getShort_market_value() {
		return short_market_value;
	}
	public void setShort_market_value(String short_market_value) {
		this.short_market_value = short_market_value;
	}
	public String getEquity() {
		return equity;
	}
	public void setEquity(String equity) {
		this.equity = equity;
	}
	public String getLast_equity() {
		return last_equity;
	}
	public void setLast_equity(String last_equity) {
		this.last_equity = last_equity;
	}
	public String getMultiplier() {
		return multiplier;
	}
	public void setMultiplier(String multiplier) {
		this.multiplier = multiplier;
	}
	public String getBuying_power() {
		return buying_power;
	}
	public void setBuying_power(String buying_power) {
		this.buying_power = buying_power;
	}
	public String getInitial_margin() {
		return initial_margin;
	}
	public void setInitial_margin(String initial_margin) {
		this.initial_margin = initial_margin;
	}
	public String getMaintenance_margin() {
		return maintenance_margin;
	}
	public void setMaintenance_margin(String maintenance_margin) {
		this.maintenance_margin = maintenance_margin;
	}
	public String getSma() {
		return sma;
	}
	public void setSma(String sma) {
		this.sma = sma;
	}
	public String getDaytrade_count() {
		return daytrade_count;
	}
	public void setDaytrade_count(String daytrade_count) {
		this.daytrade_count = daytrade_count;
	}
	public String getLast_maintenance_margin() {
		return last_maintenance_margin;
	}
	public void setLast_maintenance_margin(String last_maintenance_margin) {
		this.last_maintenance_margin = last_maintenance_margin;
	}
	public String getDaytrading_buying_power() {
		return daytrading_buying_power;
	}
	public void setDaytrading_buying_power(String daytrading_buying_power) {
		this.daytrading_buying_power = daytrading_buying_power;
	}
	public String getRegt_buying_power() {
		return regt_buying_power;
	}
	public void setRegt_buying_power(String regt_buying_power) {
		this.regt_buying_power = regt_buying_power;
	}
	
}
