package Backtesting.alpaca.volatility;

public class TickerConstants {
	public static final String alpacaKey = "alpacaKey";
	public static final String secretId = "secretId";
	public static final String alpacaKeyValuePaper = "alpacaKeyValuePaper";
	public static final String secretIdValuePaper = "secretIdValuePaper";
	public static final String headerContentType = "headerContentType";
	public static final String headerContentTypeValue = "headerContentTypeValue";
	public static final String alpacaKeyRealTime = "alpacaKeyRealTime";
	public static final String secretIdRealTime = "secretIdRealTime";
	public static final String paperUrl = "paperUrl";
	public static final String realtimeUrl = "realtimeUrl";
	public static final String dataUrl = "dataUrl";
	public static final String activeVersion = "activeVersion";
	public static final String activeCryptoVersion = "activeCryptoVersion";
	public static final String inactiveVersion = "inactiveVersion";
	public static final String ordersEndpoint = "ordersEndpoint";
	public static final String accountEndpoint = "accountEndpoint";
	public static final String lastQuoteEndpoint = "lastQuoteEndpoint";
	public static final String lastTradeEndpoint = "lastTradeEndpoint";
	public static final String positionsEndpoint = "positionsEndpoint";
	public static final String assetsEndpoint = "assetsEndpoint";
	public static final String clockEndpoint = "clockEndpoint";
	public static final String maxTickerPerDay = "maxTickerPerDay";
	public static final String maxCryptoPerDay = "maxCryptoPerDay";
	public static final String trailingStop = "trailingStop";
	public static final String maxTickerAmount = "maxTickerAmount";
	public static final String priceToBuy = "priceToBuy";
	public static final String maxAmountPerTransaction = "maxAmountPerTransaction";
	public static final String maxCryptoAmountPerTransaction = "maxCryptoAmountPerTransaction";
	public static final String maxOrdersPerDay = "maxOrdersPerDay";
	public static final String maxExtendedOrdersPerDay = "maxExtendedOrdersPerDay";
	public static final String maxCryptoOrdersPerDay = "maxCryptoOrdersPerDay";
	public static final String profitFactorDTS = "profitFactorDTS";
	public static final String profitFactorCryptoDTS = "profitFactorCryptoDTS";
	public static final String rebuyFactorDTS = "rebuyFactorDTS";
	public static final String rebuyFactorCryptoDTS = "rebuyFactorCryptoDTS";
	public static final String volatilityMonths = "volatilityMonths";
	public static final String volatilityCount = "volatilityCount";
	public static final String volatilitySpread = "volatilitySpread";
	public static final String volatilityCountHourly = "volatilityCountHourly";
	public static final String volatilitySpreadHourly = "volatilitySpreadHourly";
	public static final String spacMonths = "spacMonths";
	public static final String spacVolCount = "spacVolCount";
	public static final String spacSpread = "spacSpread";
	public static final String priorityCount = "priorityCount";
	public static final String volatilityListIndiv = "volatilityListIndiv";
	
	//Config Files
	public static final String alpacaConfig = "resources/alpacaConfig.properties";
	public static final String tickerFactors = "resources/tickerConfig.properties";
	public static final String stablePrices = "resources/stablePriceConfig.properties";
	public static final String eodOrders = "resources/EODOrders.txt";
	public static final String eodTrailingStopOrders = "resources/EODTrailingStopOrders.txt";
	public static final String switchStateConfig = "resources/switchConfig.properties";
	public static final String tickerLongTermFactors = "resources/tickerLongTermConfig.properties";
	public static final String longTermCheckPrices = "resources/longTermPriceConfig.properties";
	public static final String longTermSwitchConfig = "resources/longTermSwitchConfig.properties";
	public static final String stableVolatilePrices = "resources/stableVolatilePriceConfig.properties";
	public static final String dtStableVolatilePrices = "resources/dtStableVolatilePriceConfig.properties";
	public static final String switchVolatileConfig = "resources/volatileSwitchConfig.properties";
	public static final String dtSwitchVolatileConfig = "resources/volatileDTSwitchConfig.properties";
	public static final String dtSwitchVolatileConfigBackup = "resources/backup/volatileDTSwitchConfig.properties";
	public static final String eodEquity = "resources/eodEquity.properties";
	public static final String dailyAverageVolumeConfig = "resources/dailyAverageVolumeConfig.properties";
	public static final String dailyAverageVolume = "dailyAverageVolume";
	
	public static final String volatileTickerFactors = "resources/tickerVolatileConfig.properties";
	public static final String trailingPrices = "resources/trailingPrices.properties";
	public static final String globalSwitch = "globalSwitch";
	
	//Crypto Config
	public static final String cryptoFactors = "resources/cryptoConfig.properties";
	public static final String stablePricesCrypto = "resources/cryptoStablePriceConfig.properties";
	public static final String switchStateCryptoConfig = "resources/cryptoSwitchConfig.properties";
	public static final String dtCryptoStableVolatilePrices = "resources/dtCryptoStableVolatilePriceConfig.properties";
	public static final String dtCryptoSwitchVolatileConfig = "resources/volatileDTCryptoSwitchConfig.properties";
	public static final String dailyCryptoAverageVolumeConfig = "resources/dailyCryptoAverageVolumeConfig.properties";
	public static final String cryptoDecimalIncrement = "resources/CryptoDecimalFormat.properties";
	
	//Factors Constant
	public static final String quantity = ".qty";
	public static final String maxQuantity = ".maxQty";
	public static final String profitFactor = ".profitFactor";
	public static final String upTrendFactor = ".upTrendFactor";
	public static final String downTrendFactor = ".downTrendFactor";
	
	public static final String oneTimeTickerAmount = ".oneTimePrice";
	public static final String volatilityProfit = ".exitFactor";
	public static final String maxCostBasis = ".maxCostBasis";
	public static final String volatilityLossFactor = ".buyAgainFactor";
	
	public static final Double pdtLimit = 25752.00;
	public static final Double lowLimit = 450.00;
	
	public static final String atbCbs = "ATB-CBS-";
	public static final String atbCbTsss = "ATB-CBTSSS-";
	public static final String atbCbsPdt = "ATB-CBS-PDT-";
	public static final String atbCbsTssPdt = "ATB-CBTSSS-PDT-";
	public static final String atbVolPdt = "ATB-VOL-PDT-";
	
	public static final String acbCrypto = "ACB-CRYPTO-";
	
	public static final String newStatus = "new";
	public static final String acceptedStatus = "accepted";
	public static final String filledStatus = "filled";
	public static final String replacedStatus = "replaced";
	public static final String canceledStatus = "canceled";
	public static final String pendingCancel = "pending_cancel";
	
	//FINNHUB Constants
	public static final String finnhubToken = "finnhubToken";
	public static final String tokenValue = "tokenValue";
	public static final String finnhubUrl = "finnhubUrl";
	public static final String dataFinnhubUrl = "dataFinnhubUrl";
	public static final String version1 = "version1";
	public static final String version2 = "version2";
	public static final String stocksEndpoint = "stocksEndpoint";
	public static final String basicFinancialsEndpoint = "basicFinancialsEndpoint";
	public static final String companyProfileEndpoint = "companyProfileEndpoint";
	public static final String financialsEndpoint = "financialsEndpoint";
	public static final String recommendationEndpoint = "recommendationEndpoint";
	public static final String patternEndpoint = "patternEndpoint";
	public static final String priceTargetEndpoint = "priceTargetEndpoint";
	public static final String earningEstimatesEndpoint = "earningEstimatesEndpoint";
	public static final String revenueEstimatesEndpoint = "revenueEstimatesEndpoint";
	public static final String earningCalendarEndpoint = "earningCalendarEndpoint";
	public static final String upgradeDowngradeEndpoint = "upgradeDowngradeEndpoint";
	public static final String dividendEndpoint = "dividendEndpoint";
	public static final String companyNewsEndpoint = "companyNewsEndpoint";
	public static final String companyfilingsEndpoint = "companyfilingsEndpoint";
	public static final String finnhubConfig = "resources/finnhubConfig.properties";
	
	public static final String assetClassCrypto = "crypto";
	public static final String assetClassUSStocks = "us_equity";
	
	public static final String zoneOffset = "zoneOffset";
	public static final String preMarket = "PreMarket";
	public static final String morning = "Morning";
	public static final String afternoon = "Afternoon";
	public static final String postMarket = "PostMarket";
	public static final String midDay = "MidDay";
}
