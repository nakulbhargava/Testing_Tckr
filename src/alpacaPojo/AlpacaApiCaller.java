package alpacaPojo;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import alpacaPojo.AccountsPojo;
import alpacaPojo.AssetsPojo;
import alpacaPojo.ClockPojo;
import alpacaPojo.LastQuote;
import alpacaPojo.LastQuoteV2;
import alpacaPojo.LastTrade;
import alpacaPojo.OrdersPojo;
import alpacaPojo.PositionsPojo;
import alpacaPojo.TimeframePojo;

public class AlpacaApiCaller extends AlpacaRestParams {
	protected String paperUrl;
	protected String realtimeUrl;
	protected String activeVersion;
	protected String inactiveVersion;
	protected String ordersEndpoint;
	protected String accountEndpoint;
	protected String lastQuoteEndpoint;
	protected String lastTradeEndpoint;
	protected String positionsEndpoint;
	protected String assetsEndpoint;
	protected String clockEndpoint;
	public AlpacaApiCaller(String alpacaKey, String secretId, String alpacaKeyValue, String secretIdValue,
			String headerContentType, String headerContentTypeValue, String alpacaKeyRealTime, String secretIdRealTime, String paperUrl, String realtimeUrl,
			String activeVersion, String inactiveVersion, String ordersEndpoint, String accountEndpoint,
			String lastQuoteEndpoint, String lastTradeEndpoint, String positionsEndpoint, String assetsEndpoint, String clockEndpoint) {
		super(alpacaKey, secretId, alpacaKeyValue, secretIdValue, headerContentType, headerContentTypeValue, alpacaKeyRealTime, secretIdRealTime);
		this.paperUrl = paperUrl;
		this.realtimeUrl = realtimeUrl;
		this.activeVersion = activeVersion;
		this.inactiveVersion = inactiveVersion;
		this.ordersEndpoint = ordersEndpoint;
		this.accountEndpoint = accountEndpoint;
		this.lastQuoteEndpoint = lastQuoteEndpoint;
		this.lastTradeEndpoint = lastTradeEndpoint;
		this.positionsEndpoint = positionsEndpoint;
		this.assetsEndpoint = assetsEndpoint;
		this.clockEndpoint = clockEndpoint;
	}
	public AccountsPojo getAccountDetails() throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(paperUrl + activeVersion + accountEndpoint))
				.GET()
				.header(alpacaKey, alpacaKeyValue)
				.header(headerContentType, headerContentTypeValue)
				.header(secretId, secretIdValue)
				.build();
		HttpResponse<String> response = client.send(request,
				HttpResponse.BodyHandlers.ofString()); 
		return getJsonObject(response.body(), AccountsPojo.class);
	}
	public AccountsPojo getRealAccountDetails() throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(realtimeUrl + activeVersion + accountEndpoint))
				.GET()
				.header(alpacaKey, alpacaKeyRealTime)
				.header(headerContentType, headerContentTypeValue)
				.header(secretId, secretIdRealTime)
				.build();
		HttpResponse<String> response = client.send(request,
				HttpResponse.BodyHandlers.ofString()); 
		return getJsonObject(response.body(), AccountsPojo.class);
	}
	public ClockPojo getClock() throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(paperUrl + activeVersion + clockEndpoint))
				.GET()
				.header(alpacaKey, alpacaKeyValue)
				.header(headerContentType, headerContentTypeValue)
				.header(secretId, secretIdValue)
				.build();
		HttpResponse<String> response = client.send(request,
				HttpResponse.BodyHandlers.ofString()); 
		return getJsonObject(response.body(), ClockPojo.class);
	}
	public OrdersPojo buySymbolQuantity(String symbol, Double qty) throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		String requestBody = "{\r\n"
				+ "    \"symbol\": \"" + symbol + "\",\r\n"
				+ "    \"qty\": \""+ qty + "\",\r\n"
				+ "    \"side\": \"buy\",\r\n"
				+ "    \"type\": \"market\",\r\n"
				+ "    \"time_in_force\": \"day\"\r\n"
				+ "}";
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(paperUrl + activeVersion + ordersEndpoint))
				.POST(HttpRequest.BodyPublishers.ofString(requestBody))
				.header(alpacaKey, alpacaKeyValue)
				.header(headerContentType, headerContentTypeValue)
				.header(secretId, secretIdValue)
				.build();
		HttpResponse<String> response = client.send(request,
				HttpResponse.BodyHandlers.ofString());
		if(response.statusCode() != 200) {
			System.out.println("Buy Error:" + response.body()); 
		}
		return getJsonObject(response.body(), OrdersPojo.class);
	}
	public OrdersPojo buyRealSymbolQuantity(String symbol, Double qty) throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		String requestBody = "{\r\n"
				+ "    \"symbol\": \"" + symbol + "\",\r\n"
				+ "    \"qty\": \""+ qty + "\",\r\n"
				+ "    \"side\": \"buy\",\r\n"
				+ "    \"type\": \"market\",\r\n"
				+ "    \"time_in_force\": \"day\"\r\n"
				+ "}";
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(realtimeUrl + activeVersion + ordersEndpoint))
				.POST(HttpRequest.BodyPublishers.ofString(requestBody))
				.header(alpacaKey, alpacaKeyRealTime)
				.header(headerContentType, headerContentTypeValue)
				.header(secretId, secretIdRealTime)
				.build();
		HttpResponse<String> response = client.send(request,
				HttpResponse.BodyHandlers.ofString());
		if(response.statusCode() != 200) {
			System.out.println("Buy Error:" + response.body()); 
			return null;
		}
		return getJsonObject(response.body(), OrdersPojo.class);
	}
	public OrdersPojo buySymbolLimitOrder(String symbol, Double qty, Double price) throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		String requestBody = "{\r\n"
				+ "    \"symbol\": \"" + symbol + "\",\r\n"
				+ "    \"qty\": \""+ qty + "\",\r\n"
				+ "    \"side\": \"buy\",\r\n"
				+ "    \"type\": \"limit\",\r\n"
				+ "    \"time_in_force\": \"gtc\",\r\n"
				+ "    \"limit_price\": \"" + price + "\"\r\n"
				+ "}";
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(paperUrl + activeVersion + ordersEndpoint))
				.POST(HttpRequest.BodyPublishers.ofString(requestBody))
				.header(alpacaKey, alpacaKeyValue)
				.header(headerContentType, headerContentTypeValue)
				.header(secretId, secretIdValue)
				.build();
		HttpResponse<String> response = client.send(request,
				HttpResponse.BodyHandlers.ofString());
		return getJsonObject(response.body(), OrdersPojo.class);
	}
	public OrdersPojo buyRealSymbolLimitOrder(String symbol, Double qty, Double price) throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		String requestBody = "{\r\n"
				+ "    \"symbol\": \"" + symbol + "\",\r\n"
				+ "    \"qty\": \""+ qty + "\",\r\n"
				+ "    \"side\": \"buy\",\r\n"
				+ "    \"type\": \"limit\",\r\n"
				+ "    \"time_in_force\": \"gtc\",\r\n"
				+ "    \"limit_price\": \"" + price + "\"\r\n"
				+ "}";
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(realtimeUrl + activeVersion + ordersEndpoint))
				.POST(HttpRequest.BodyPublishers.ofString(requestBody))
				.header(alpacaKey, alpacaKeyRealTime)
				.header(headerContentType, headerContentTypeValue)
				.header(secretId, secretIdRealTime)
				.build();
		HttpResponse<String> response = client.send(request,
				HttpResponse.BodyHandlers.ofString());
		return getJsonObject(response.body(), OrdersPojo.class);
	}
	public OrdersPojo buySymbolBracketOrder(String symbol, Double qty, Double limitPriceToBuy, Double takeProfit, Double stopLoss) throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		String requestBody = "{\r\n"
				+ "    \"symbol\": \"" + symbol + "\",\r\n"
				+ "    \"qty\": \""+ qty + "\",\r\n"
				+ "    \"side\": \"buy\",\r\n"
				+ "    \"type\": \"limit\",\r\n"
				+ "    \"time_in_force\": \"day\",\r\n" 
				+ "    \"limit_price\": \"" + limitPriceToBuy + "\",\r\n"
				+ "    \"order_class\": \"bracket\",\r\n"
				+ "    \"take_profit\": {\r\n"
				+ "        \"limit_price\": \"" + takeProfit + "\"\r\n"
				+ "    },\r\n"
				+ "    \"stop_loss\": {\r\n"
				+ "        \"stop_price\": \"" + stopLoss + "\"\r\n"
				+ "    }\r\n"
				+ "}";
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(paperUrl + activeVersion + ordersEndpoint))
				.POST(HttpRequest.BodyPublishers.ofString(requestBody))
				.header(alpacaKey, alpacaKeyValue)
				.header(headerContentType, headerContentTypeValue)
				.header(secretId, secretIdValue)
				.build();
		HttpResponse<String> response = client.send(request,
				HttpResponse.BodyHandlers.ofString());
		return getJsonObject(response.body(), OrdersPojo.class);
	}
	public OrdersPojo buySymbolPrice(String symbol, Double price) throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		String requestBody = "{\r\n"
				+ "    \"symbol\": \"" + symbol + "\",\r\n"
				+ "    \"notional\": \""+ price + "\",\r\n"
				+ "    \"side\": \"buy\",\r\n"
				+ "    \"type\": \"market\",\r\n"
				+ "    \"time_in_force\": \"day\"\r\n"
				+ "}";
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(paperUrl + activeVersion + ordersEndpoint))
				.POST(HttpRequest.BodyPublishers.ofString(requestBody))
				.header(alpacaKey, alpacaKeyValue)
				.header(headerContentType, headerContentTypeValue)
				.header(secretId, secretIdValue)
				.build();
		HttpResponse<String> response = client.send(request,
				HttpResponse.BodyHandlers.ofString());
		return getJsonObject(response.body(), OrdersPojo.class);
	}
	public OrdersPojo buyRealSymbolPrice(String symbol, Double price) throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		String requestBody = "{\r\n"
				+ "    \"symbol\": \"" + symbol + "\",\r\n"
				+ "    \"notional\": \""+ price + "\",\r\n"
				+ "    \"side\": \"buy\",\r\n"
				+ "    \"type\": \"market\",\r\n"
				+ "    \"time_in_force\": \"day\"\r\n"
				+ "}";
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(realtimeUrl + activeVersion + ordersEndpoint))
				.POST(HttpRequest.BodyPublishers.ofString(requestBody))
				.header(alpacaKey, alpacaKeyRealTime)
				.header(headerContentType, headerContentTypeValue)
				.header(secretId, secretIdRealTime)
				.build();
		HttpResponse<String> response = client.send(request,
				HttpResponse.BodyHandlers.ofString());
		return getJsonObject(response.body(), OrdersPojo.class);
	}
	public String getLiveDataStream(String symbol) throws URISyntaxException, InterruptedException {
		WebSocketClient dataStream =  new WebSocketClient( new URI( "wss://stream.data.alpaca.markets/v2/sip" ) )
		{
			@Override
			public void onMessage( String message ) {
				System.out.print(message);
			}

			@Override
			public void onOpen( ServerHandshake handshake ) {
				System.out.println( "opened connection" );
			}

			@Override
			public void onClose( int code, String reason, boolean remote ) {
				System.out.println( "closed connection" );
			}

			@Override
			public void onError( Exception ex ) {
				ex.printStackTrace();
			}
		};
		dataStream.connect();
		String message = "{\r\n"
				+ "    \"action\": \"" + "auth" + "\",\r\n"
				+ "    \"key\": \""+ alpacaKeyRealTime + "\",\r\n"
				+ "    \"secret\": \"" + secretIdRealTime + "\",\r\n"
				+ "}";
		//String simpleText = "{\"action\": \"auth\, " + "\"key\: " + alpacaKeyRealTime + " , \"secret\" : " + secretIdRealTime +"}";
		dataStream.send(message);
		String subscribe = "{\r\n"
				+ "    \"action\": \"" + "subscribe" + "\",\r\n"
				+ "    \"trades\": [\""+ symbol + "\"],\r\n"
				+ "    \"quotes\": [\"" + symbol + "\"]\r\n"
				+ "}";

		dataStream.send(subscribe);
		Thread.sleep(60000);
		dataStream.close();
		return "Empty";
	}
	public OrdersPojo sellSymbolQuantity(String symbol, Double qty) throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		String requestBody = "{\r\n"
				+ "    \"symbol\": \"" + symbol + "\",\r\n"
				+ "    \"qty\": \""+ qty + "\",\r\n"
				+ "    \"side\": \"sell\",\r\n"
				+ "    \"type\": \"market\",\r\n"
				+ "    \"time_in_force\": \"day\"\r\n"
				+ "}";
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(paperUrl + activeVersion + ordersEndpoint))
				.POST(HttpRequest.BodyPublishers.ofString(requestBody))
				.header(alpacaKey, alpacaKeyValue)
				.header(headerContentType, headerContentTypeValue)
				.header(secretId, secretIdValue)
				.build();
		HttpResponse<String> response = client.send(request,
				HttpResponse.BodyHandlers.ofString());
		if(response.statusCode() != 200) {
			System.out.println("Buy Error:" + response.body()); 
		}
		return getJsonObject(response.body(), OrdersPojo.class);
	}
	public OrdersPojo sellRealSymbolQuantity(String symbol, Double qty) throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		String requestBody = "{\r\n"
				+ "    \"symbol\": \"" + symbol + "\",\r\n"
				+ "    \"qty\": \""+ qty + "\",\r\n"
				+ "    \"side\": \"sell\",\r\n"
				+ "    \"type\": \"market\",\r\n"
				+ "    \"time_in_force\": \"day\"\r\n"
				+ "}";
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(realtimeUrl + activeVersion + ordersEndpoint))
				.POST(HttpRequest.BodyPublishers.ofString(requestBody))
				.header(alpacaKey, alpacaKeyRealTime)
				.header(headerContentType, headerContentTypeValue)
				.header(secretId, secretIdRealTime)
				.build();
		HttpResponse<String> response = client.send(request,
				HttpResponse.BodyHandlers.ofString());
//		System.out.println(response.body());
//		System.out.println(response.statusCode());
//		System.out.println(response.request());
		if(response.statusCode() != 200) {
			System.out.println("Sell Error:" + response.body()); 
			return null;
		}
		return getJsonObject(response.body(), OrdersPojo.class);
	}
	public OrdersPojo sellSymbolLimitOrder(String symbol, Double qty, Double price) throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		String requestBody = "{\r\n"
				+ "    \"symbol\": \"" + symbol + "\",\r\n"
				+ "    \"qty\": \""+ qty + "\",\r\n"
				+ "    \"side\": \"sell\",\r\n"
				+ "    \"type\": \"limit\",\r\n"
				+ "    \"time_in_force\": \"gtc\",\r\n"
				+ "    \"limit_price\": \"" + price + "\"\r\n"
				+ "}";
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(paperUrl + activeVersion + ordersEndpoint))
				.POST(HttpRequest.BodyPublishers.ofString(requestBody))
				.header(alpacaKey, alpacaKeyValue)
				.header(headerContentType, headerContentTypeValue)
				.header(secretId, secretIdValue)
				.build();
		HttpResponse<String> response = client.send(request,
				HttpResponse.BodyHandlers.ofString());
		return getJsonObject(response.body(), OrdersPojo.class);
	}
	public OrdersPojo sellRealSymbolLimitOrder(String symbol, Double qty, Double price) throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		String requestBody = "{\r\n"
				+ "    \"symbol\": \"" + symbol + "\",\r\n"
				+ "    \"qty\": \""+ qty + "\",\r\n"
				+ "    \"side\": \"sell\",\r\n"
				+ "    \"type\": \"limit\",\r\n"
				+ "    \"time_in_force\": \"gtc\",\r\n"
				+ "    \"limit_price\": \"" + price + "\"\r\n"
				+ "}";
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(realtimeUrl + activeVersion + ordersEndpoint))
				.POST(HttpRequest.BodyPublishers.ofString(requestBody))
				.header(alpacaKey, alpacaKeyRealTime)
				.header(headerContentType, headerContentTypeValue)
				.header(secretId, secretIdRealTime)
				.build();
		HttpResponse<String> response = client.send(request,
				HttpResponse.BodyHandlers.ofString());
		return getJsonObject(response.body(), OrdersPojo.class);
	}
	public LastTrade getLastTrade(String symbol) throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();

		// Create HTTP request object
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(realtimeUrl + inactiveVersion + "/last/stocks/" + symbol))
				.GET()
				.header(alpacaKey, alpacaKeyValue)
				.header(headerContentType, headerContentTypeValue)
				.header(secretId, secretIdValue)
				.build();
		// Send HTTP request
		HttpResponse<String> response = client.send(request,
				HttpResponse.BodyHandlers.ofString());

		return getJsonObject(response.body(), LastTrade.class);
	}
	public LastTradePojoV2 getLastTradeV2(String symbol) throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();

		// Create HTTP request object
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(realtimeUrl + activeVersion + "/stocks/" + symbol + "/trades/latest"))
				.GET()
				.header(alpacaKey, alpacaKeyValue)
				.header(headerContentType, headerContentTypeValue)
				.header(secretId, secretIdValue)
				.build();
		// Send HTTP request
		HttpResponse<String> response = client.send(request,
				HttpResponse.BodyHandlers.ofString());

		return getJsonObject(response.body(), LastTradePojoV2.class);
	}
	public LastQuotePojoV2 getLastQuoteV2(String symbol) throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();

		// Create HTTP request object
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(realtimeUrl + activeVersion + "/stocks/" + symbol + "/quotes/latest"))
				.GET()
				.header(alpacaKey, alpacaKeyValue)
				.header(headerContentType, headerContentTypeValue)
				.header(secretId, secretIdValue)
				.build();
		// Send HTTP request
		HttpResponse<String> response = client.send(request,
				HttpResponse.BodyHandlers.ofString());

		return getJsonObject(response.body(), LastQuotePojoV2.class);
	}
	@SuppressWarnings("static-access")
	public LastQuote getQuote(String symbol) throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();

		// Create HTTP request object
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(realtimeUrl + inactiveVersion + "/last_quote/stocks/" + symbol))
				.GET()
				.header(alpacaKey, alpacaKeyValue)
				.header(headerContentType, headerContentTypeValue)
				.header(secretId, secretIdValue)
				.build();
		// Send HTTP request
		HttpResponse<String> response = client.send(request,
				HttpResponse.BodyHandlers.ofString());

		return getJsonObject(response.body(), LastQuote.class);
	}
	@SuppressWarnings("static-access")
	public LastQuoteV2 getQuoteV2(String symbol, String startTime, String endTime) throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();

		// Create HTTP request object
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(realtimeUrl + activeVersion + "/stocks/" + symbol + "/quotes?start=" + startTime + "&end=" + endTime))
				.GET()
				.header(alpacaKey, alpacaKeyValue)
				.header(headerContentType, headerContentTypeValue)
				.header(secretId, secretIdValue)
				.build();
		// Send HTTP request
		HttpResponse<String> response = client.send(request,
				HttpResponse.BodyHandlers.ofString());

		return getJsonObject(response.body(), LastQuoteV2.class);
	}
	@SuppressWarnings("static-access")
	public LastTradeV2 getTradesV2(String symbol, String startTime, String endTime) throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();

		// Create HTTP request object
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(realtimeUrl + activeVersion + "/stocks/" + symbol + "/trades?start=" + startTime + "&end=" + endTime))
				.GET()
				.header(alpacaKey, alpacaKeyValue)
				.header(headerContentType, headerContentTypeValue)
				.header(secretId, secretIdValue)
				.build();
		// Send HTTP request
		HttpResponse<String> response = client.send(request,
				HttpResponse.BodyHandlers.ofString());

		return getJsonObject(response.body(), LastTradeV2.class);
	}
	@SuppressWarnings("static-access")
	public OrdersPojo getOrderDetails(String orderId) throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		// Create HTTP request object
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(paperUrl + activeVersion + ordersEndpoint + "/" + orderId))
				.GET()
				.header(alpacaKey, alpacaKeyValue)
				.header(headerContentType, headerContentTypeValue)
				.header(secretId, secretIdValue)
				.build();
		// Send HTTP request
		HttpResponse<String> response = client.send(request,
				HttpResponse.BodyHandlers.ofString());

		return getJsonObject(response.body(), OrdersPojo.class);
	}
	@SuppressWarnings("static-access")
	public OrdersPojo getRealOrderDetails(String orderId) throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		// Create HTTP request object
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(realtimeUrl + activeVersion + ordersEndpoint + "/" + orderId))
				.GET()
				.header(alpacaKey, alpacaKeyRealTime)
				.header(headerContentType, headerContentTypeValue)
				.header(secretId, secretIdRealTime)
				.build();
		// Send HTTP request
		HttpResponse<String> response = client.send(request,
				HttpResponse.BodyHandlers.ofString());
		if(response.statusCode() != 200) {
			System.out.println("Buy Error:" + response.body()); 
			return null;
		}
		return getJsonObject(response.body(), OrdersPojo.class);
	}
	@SuppressWarnings("static-access")
	public void deleteOrder(String orderId) throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		// Create HTTP request object
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(paperUrl + activeVersion + ordersEndpoint + "/" + orderId))
				.DELETE()
				.header(alpacaKey, alpacaKeyValue)
				.header(headerContentType, headerContentTypeValue)
				.header(secretId, secretIdValue)
				.build();
		// Send HTTP request
		HttpResponse<String> response = client.send(request,
				HttpResponse.BodyHandlers.ofString());
		//TODO: return valid responses based on error codes
		//return getJsonObject(response.body(), OrdersPojo.class);
	}
	@SuppressWarnings("static-access")
	public void deleteRealOrder(String orderId) throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		// Create HTTP request object
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(realtimeUrl + activeVersion + ordersEndpoint + "/" + orderId))
				.DELETE()
				.header(alpacaKey, alpacaKeyRealTime)
				.header(headerContentType, headerContentTypeValue)
				.header(secretId, secretIdRealTime)
				.build();
		// Send HTTP request
		HttpResponse<String> response = client.send(request,
				HttpResponse.BodyHandlers.ofString());
		//TODO: return valid responses based on error codes
		//return getJsonObject(response.body(), OrdersPojo.class);
	}
	@SuppressWarnings("static-access")
	public OrdersPojo replaceStopLossOrder(String orderId, String stopPrice) throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		String requestBody = "{\r\n"
				+ "    \"stop_price\": \"" + stopPrice + "\"\r\n"
				+ "}";
		// Create HTTP request object
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(paperUrl + activeVersion + ordersEndpoint + "/" + orderId))
				.method("PATCH", HttpRequest.BodyPublishers.ofString(requestBody))
				.header(alpacaKey, alpacaKeyValue)
				.header(headerContentType, headerContentTypeValue)
				.header(secretId, secretIdValue)
				.build();
		// Send HTTP request
		HttpResponse<String> response = client.send(request,
				HttpResponse.BodyHandlers.ofString());
		return getJsonObject(response.body(), OrdersPojo.class);
	}
	@SuppressWarnings("static-access")
	public OrdersPojo[] getOrders() throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		// Create HTTP request object
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(paperUrl + activeVersion + ordersEndpoint + "?status=all&limit=500"))
				.GET()
				.header(alpacaKey, alpacaKeyValue)
				.header(headerContentType, headerContentTypeValue)
				.header(secretId, secretIdValue)
				.build();
		// Send HTTP request
		HttpResponse<String> response = client.send(request,
				HttpResponse.BodyHandlers.ofString());

		return getJsonObjectArray(response.body(), OrdersPojo[].class);
	}
	@SuppressWarnings("static-access")
	public OrdersPojo[] getRealOrders() throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		// Create HTTP request object
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(realtimeUrl + activeVersion + ordersEndpoint + "?status=all&limit=500"))
				.GET()
				.header(alpacaKey, alpacaKeyRealTime)
				.header(headerContentType, headerContentTypeValue)
				.header(secretId, secretIdRealTime)
				.build();
		// Send HTTP request
		HttpResponse<String> response = client.send(request,
				HttpResponse.BodyHandlers.ofString());

		return getJsonObjectArray(response.body(), OrdersPojo[].class);
	}
	@SuppressWarnings("static-access")
	public OrdersPojo[] getOpenOrders(String symbol) throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		// Create HTTP request object
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(paperUrl + activeVersion + ordersEndpoint + "?status=open&symbols="+symbol))
				.GET()
				.header(alpacaKey, alpacaKeyValue)
				.header(headerContentType, headerContentTypeValue)
				.header(secretId, secretIdValue)
				.build();
		// Send HTTP request
		HttpResponse<String> response = client.send(request,
				HttpResponse.BodyHandlers.ofString());

		return getJsonObjectArray(response.body(), OrdersPojo[].class);
	}
	@SuppressWarnings("static-access")
	public OrdersPojo[] getRealOpenOrders(String symbol) throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		// Create HTTP request object
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(realtimeUrl + activeVersion + ordersEndpoint + "?status=open&symbols="+symbol))
				.GET()
				.header(alpacaKey, alpacaKeyRealTime)
				.header(headerContentType, headerContentTypeValue)
				.header(secretId, secretIdRealTime)
				.build();
		// Send HTTP request
		HttpResponse<String> response = client.send(request,
				HttpResponse.BodyHandlers.ofString());

		return getJsonObjectArray(response.body(), OrdersPojo[].class);
	}
	@SuppressWarnings("static-access")
	public OrdersPojo[] getOrdersUntil(String untilTimestamp) throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		// Create HTTP request object
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(paperUrl + activeVersion + ordersEndpoint + "?status=all&until="+untilTimestamp))
				.GET()
				.header(alpacaKey, alpacaKeyValue)
				.header(headerContentType, headerContentTypeValue)
				.header(secretId, secretIdValue)
				.build();
		// Send HTTP request
		HttpResponse<String> response = client.send(request,
				HttpResponse.BodyHandlers.ofString());

		return getJsonObjectArray(response.body(), OrdersPojo[].class);
	}
	@SuppressWarnings("static-access")
	public OrdersPojo[] getSymbolOrdersWithTime(String symbol, String afterTimestamp, String untilTimestamp) throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		// Create HTTP request object
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(paperUrl + activeVersion + ordersEndpoint + "?symbol="+symbol+"&status=all&until="+untilTimestamp+"&after="+afterTimestamp))
				.GET()
				.header(alpacaKey, alpacaKeyValue)
				.header(headerContentType, headerContentTypeValue)
				.header(secretId, secretIdValue)
				.build();
		// Send HTTP request
		HttpResponse<String> response = client.send(request,
				HttpResponse.BodyHandlers.ofString());

		return getJsonObjectArray(response.body(), OrdersPojo[].class);
	}
	@SuppressWarnings("static-access")
	public OrdersPojo[] getSymbolOpenOrders(String symbol) throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		// Create HTTP request object
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(paperUrl + activeVersion + ordersEndpoint + "?status=open&symbols="+symbol))
				.GET()
				.header(alpacaKey, alpacaKeyValue)
				.header(headerContentType, headerContentTypeValue)
				.header(secretId, secretIdValue)
				.build();
		// Send HTTP request
		HttpResponse<String> response = client.send(request,
				HttpResponse.BodyHandlers.ofString());

		return getJsonObjectArray(response.body(), OrdersPojo[].class);
	}
	@SuppressWarnings("static-access")
	public PositionsPojo[] getOpenPositions() throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		// Create HTTP request object
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(paperUrl + activeVersion + positionsEndpoint))
				.GET()
				.header(alpacaKey, alpacaKeyValue)
				.header(headerContentType, headerContentTypeValue)
				.header(secretId, secretIdValue)
				.build();
		// Send HTTP request
		HttpResponse<String> response = client.send(request,
				HttpResponse.BodyHandlers.ofString());

		return getJsonObjectArray(response.body(), PositionsPojo[].class);
	}
	@SuppressWarnings("static-access")
	public PositionsPojo getSymbolPosition(String symbol) throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		// Create HTTP request object
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(paperUrl + activeVersion + positionsEndpoint + "/" + symbol))
				.GET()
				.header(alpacaKey, alpacaKeyValue)
				.header(headerContentType, headerContentTypeValue)
				.header(secretId, secretIdValue)
				.build();
		// Send HTTP request
		HttpResponse<String> response = client.send(request,
				HttpResponse.BodyHandlers.ofString());

		return getJsonObject(response.body(), PositionsPojo.class);
	}
	@SuppressWarnings("static-access")
	public PositionsPojo getRealSymbolPosition(String symbol) throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		// Create HTTP request object
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(realtimeUrl + activeVersion + positionsEndpoint + "/" + symbol))
				.GET()
				.header(alpacaKey, alpacaKeyRealTime)
				.header(headerContentType, headerContentTypeValue)
				.header(secretId, secretIdRealTime)
				.build();
		// Send HTTP request
		HttpResponse<String> response = client.send(request,
				HttpResponse.BodyHandlers.ofString());

		return getJsonObject(response.body(), PositionsPojo.class);
	}
	@SuppressWarnings("static-access")
	public AssetsPojo getAssetSymbol(String symbol) throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		// Create HTTP request object
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(paperUrl + activeVersion + assetsEndpoint + "/" + symbol))
				.GET()
				.header(alpacaKey, alpacaKeyValue)
				.header(headerContentType, headerContentTypeValue)
				.header(secretId, secretIdValue)
				.build();
		// Send HTTP request
		HttpResponse<String> response = client.send(request,
				HttpResponse.BodyHandlers.ofString());

		return getJsonObject(response.body(), AssetsPojo.class);
	}
	@SuppressWarnings("static-access")
	public TimeframePojo getTimeframe(String symbol, String startTime, String endTime, String timeframe) throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		// Create HTTP request object
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(realtimeUrl + activeVersion + "/stocks/" + symbol + "/bars?start=" + startTime + "&end=" + endTime + "&timeframe=" + timeframe))
				.GET()
				.header(alpacaKey, alpacaKeyValue)
				.header(headerContentType, headerContentTypeValue)
				.header(secretId, secretIdValue)
				.build();
		// Send HTTP request
		HttpResponse<String> response = client.send(request,
				HttpResponse.BodyHandlers.ofString());

		return getJsonObject(response.body(), TimeframePojo.class);
	}
	@SuppressWarnings("static-access")
	public TimeframePojo getSnapshot(String symbols, String startTime, String endTime, String timeframe) throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		// Create HTTP request object

		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(realtimeUrl + activeVersion + "/stocks/snapshots?symbols=" + symbols ))
				.GET()
				.header(alpacaKey, alpacaKeyValue)
				.header(headerContentType, headerContentTypeValue)
				.header(secretId, secretIdValue)
				.build();
		// Send HTTP request
		HttpResponse<String> response = client.send(request,
				HttpResponse.BodyHandlers.ofString());

		return getJsonObject(response.body(), TimeframePojo.class);
	}
	@SuppressWarnings("static-access")
	public AssetsPojo[] getAssets() throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		// Create HTTP request object
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(paperUrl + activeVersion + assetsEndpoint))
				.GET()
				.header(alpacaKey, alpacaKeyValue)
				.header(headerContentType, headerContentTypeValue)
				.header(secretId, secretIdValue)
				.build();
		// Send HTTP request
		HttpResponse<String> response = client.send(request,
				HttpResponse.BodyHandlers.ofString());

		return getJsonObjectArray(response.body(), AssetsPojo[].class);
	}
	public SnapshotPojo getSnapshot(String symbol) throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();

		// Create HTTP request object
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(realtimeUrl + activeVersion + "/stocks/" + symbol + "/snapshot"))
				.GET()
				.header(alpacaKey, alpacaKeyValue)
				.header(headerContentType, headerContentTypeValue)
				.header(secretId, secretIdValue)
				.build();
		// Send HTTP request
		HttpResponse<String> response = client.send(request,
				HttpResponse.BodyHandlers.ofString());

		return getJsonObject(response.body(), SnapshotPojo.class);
	}
	public SnapshotPojo[] getSnapshots(String symbols) throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		
		// Create HTTP request object
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(realtimeUrl + activeVersion + "/stocks/snapshots?symbols=" + symbols))
				.GET()
				.header(alpacaKey, alpacaKeyValue)
				.header(headerContentType, headerContentTypeValue)
				.header(secretId, secretIdValue)
				.build();
		// Send HTTP request
		HttpResponse<String> response = client.send(request,
				HttpResponse.BodyHandlers.ofString());
		Map<String, String> snapshotMaps = getJsonObjectMap(response.body(), Object.class);
		SnapshotPojo[] snapshots = new SnapshotPojo[snapshotMaps.size()];
		int i = 0;
		for (Map.Entry<String, String> entry : snapshotMaps.entrySet()) {
			SnapshotPojo snapshot = getJsonObjectConvertor(entry.getValue(), SnapshotPojo.class);
			snapshot.setSymbol(entry.getKey());
			snapshots[i] = snapshot;
			i++;
		}
		return snapshots;
	}
	public <T> T getJsonObject(String jsonString, Class objectClass) throws JsonParseException, JsonMappingException, IOException{
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true);
		T  responseObject = (T) mapper.readValue(jsonString,  objectClass);
		return responseObject;
	}
	public <T> T[] getJsonObjectArray(String jsonString, Class objectClass) throws JsonParseException, JsonMappingException, IOException{
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//		mapper.configure(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true);
		T[]  responseObject = (T[]) mapper.readValue(jsonString,  objectClass);
		return responseObject;
	}
	
	public <T> T getJsonObjectMap(String jsonString, Class objectClass) throws JsonParseException, JsonMappingException, IOException{
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true);
		T  responseObject = (T) mapper.readValue(jsonString,  objectClass);
		return responseObject;
	}
	public <T> T getJsonObjectConvertor(Object jsonObject, Class objectClass) throws JsonParseException, JsonMappingException, IOException{
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true);
		T  responseObject = (T) mapper.convertValue(jsonObject,  objectClass);
		return responseObject;
	}
}
