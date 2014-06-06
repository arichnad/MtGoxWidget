package org.openbitcoinwidget;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

import static org.openbitcoinwidget.CurrencyConversion.*;
import static org.openbitcoinwidget.TickerUtil.*;

public enum RateService {
	CAMPBX(1, "Camp BX", new TickerUrl(BTC_USD, "http://campbx.com/api/xticker.php")),

	BITSTAMP(2,"Bitstamp", new TickerUrl(BTC_USD, "https://www.bitstamp.net/api/ticker/")),

	BTCE (3, "BTC-e",
			new TickerUrl(BTC_USD, "https://btc-e.com/api/2/btc_usd/ticker"),
			new TickerUrl(BTC_EUR, "https://btc-e.com/api/2/btc_eur/ticker"),
			new TickerUrl(LTC_USD, "https://btc-e.com/api/2/ltc_usd/ticker"),
			new TickerUrl(LTC_EUR, "https://btc-e.com/api/2/ltc_eur/ticker"),
			new TickerUrl(LTC_BTC, "https://btc-e.com/api/2/ltc_btc/ticker")),

	COINBASE (4, "Coinbase", new TickerUrl(BTC_USD, "https://coinbase.com/api/v1/currencies/exchange_rates")),

	BITCUREX(5, "Bitcurex",
			new TickerUrl(BTC_PLN, "https://pln.bitcurex.com/data/ticker.json"),
			new TickerUrl(BTC_EUR, "https://eur.bitcurex.com/data/ticker.json")),

	BITPAY(6, "Bitpay", new TickerUrl(BTC_USD, "https://bitpay.com/api/rates")),

	BTER(7, "BTER",
			new TickerUrl(BTC_CNY, "https://bter.com/api/1/ticker/btc_cny"),
			new TickerUrl(LTC_CNY, "https://bter.com/api/1/ticker/ltc_cny"),
			new TickerUrl(LTC_BTC, "https://bter.com/api/1/ticker/ltc_btc")),

	BITKONAN(8, "BKonan", new TickerUrl(BTC_USD, "https://bitkonan.com/api/ticker/")),

	THEROCK(9, "TheRock",
			new TickerUrl(BTC_USD, "https://www.therocktrading.com/api/ticker/BTCUSD"),
			new TickerUrl(BTC_EUR, "https://www.therocktrading.com/api/ticker/BTCEUR"),
			new TickerUrl(LTC_BTC, "https://www.therocktrading.com/api/ticker/LTCBTC"),
			new TickerUrl(LTC_USD, "https://www.therocktrading.com/api/ticker/LTCUSD"),
			new TickerUrl(LTC_EUR, "https://www.therocktrading.com/api/ticker/LTCEUR")),

	BIT2C(10, "Bit2C", new TickerUrl(BTC_ILS, "https://www.bit2c.co.il/Exchanges/NIS/Ticker.json"));


	private final int id;
	private final String name;
	private final TickerUrl[] tickerUrls;
	private static final Map<Integer,RateService> lookup = new HashMap<Integer,RateService>();

	RateService(int id, String name, TickerUrl... tickerUrls) {
		this.id = id;
		this.name = name;
		this.tickerUrls = tickerUrls;
	}

	static {
		for(RateService s : EnumSet.allOf(RateService.class))
			lookup.put(s.getId(), s);
	}

	public TickerData parseJSON(String json) {
		TickerData tickerData = new TickerData();

		tickerData.setRateService(this);
		switch (this) {
			case CAMPBX:
				// {"Last Trade":"11.75","Best Bid":"11.40","Best Ask":"11.67"}
				tickerData.setLast(tryToParseDouble(getJSONTickerKey(parseJSONObject(json), "Last Trade")));
				tickerData.setBuy(tryToParseDouble(getJSONTickerKey(parseJSONObject(json), "Best Bid")));
				tickerData.setSell(tryToParseDouble(getJSONTickerKey(parseJSONObject(json), "Best Ask")));
				break;
			case COINBASE:
				// {..."btc_to_usd":"598.56472"...
				tickerData.setLast(tryToParseDouble(getJSONTickerKey(parseJSONObject(json), "btc_to_usd")));
				break;
			case THEROCK:
				// {"result":[{"symbol":"BTCUSD","bid":"610.0200","ask":"660.0000","last":"658.0000","volume":"228.9800","open":"658.0000","high":"660.0000","low":"658.0000","close":"660.0000"}]}
				try {
					json = parseJSONObject(json).getJSONArray("result").getString(0);
				} catch (JSONException e) {
				  break;
				}
			case BITSTAMP:
				// {"high": "5.19", "last": "5.17", "bid": "5.17", "volume": "479.80406816", "low": "5.10", "ask": "5.20"}
			case BITKONAN:
				// {"last":"660.00","high":"660.00","low":"650.00","bid":"555.00","ask":"700.00","open":"553.00","volume":"1.36182191"}
				tickerData.setLast(tryToParseDouble(getJSONTickerKey(parseJSONObject(json), "last")));
				tickerData.setLow(tryToParseDouble(getJSONTickerKey(parseJSONObject(json), "low")));
				tickerData.setHigh(tryToParseDouble(getJSONTickerKey(parseJSONObject(json), "high")));
				tickerData.setBuy(tryToParseDouble(getJSONTickerKey(parseJSONObject(json), "bid")));
				tickerData.setSell(tryToParseDouble(getJSONTickerKey(parseJSONObject(json), "ask")));
				break;
			case BTCE:
				// {"ticker":{"high":143.99001,"low":52.999,"avg":98.494505,"vol":7945244.62099,"vol_cur":99804.06692,"last":65.989,"buy":65.201,"sell":65.11,"server_time":1365771883}}
				tickerData.setLast(tryToParseDouble(getJSONTickerKeyFromObject(parseJSONObject(json), "ticker", "last")));
				tickerData.setLow(tryToParseDouble(getJSONTickerKeyFromObject(parseJSONObject(json), "ticker", "low")));
				tickerData.setHigh(tryToParseDouble(getJSONTickerKeyFromObject(parseJSONObject(json), "ticker", "high")));
				tickerData.setBuy(tryToParseDouble(getJSONTickerKeyFromObject(parseJSONObject(json), "ticker", "buy")));
				tickerData.setSell(tryToParseDouble(getJSONTickerKeyFromObject(parseJSONObject(json), "ticker", "sell")));
				break;
			case BITCUREX:
				// {"high":2199.76,"low":1954,"avg":2076.88,"vwap":2082.0488574,"vol":528.33722382,"last":2088,"buy":2088,"sell":2089,"time":1387543316}
			case BTER:
				// {"result":"true","last":0.03099,"high":0.0319,"low":0.02589,"avg":0.02842,"sell":0.03099,"buy":0.03,"vol_ltc":6513.7652,"vol_btc":185.14607}
				tickerData.setLast(tryToParseDouble(getJSONTickerKey(parseJSONObject(json), "last")));
				tickerData.setLow(tryToParseDouble(getJSONTickerKey(parseJSONObject(json), "low")));
				tickerData.setHigh(tryToParseDouble(getJSONTickerKey(parseJSONObject(json), "high")));
				tickerData.setBuy(tryToParseDouble(getJSONTickerKey(parseJSONObject(json), "buy")));
				tickerData.setSell(tryToParseDouble(getJSONTickerKey(parseJSONObject(json), "sell")));
				break;
			case BIT2C:
				tickerData.setLast(tryToParseDouble(getJSONTickerKey(parseJSONObject(json), "ll")));
				tickerData.setBuy(tryToParseDouble(getJSONTickerKey(parseJSONObject(json), "h")));
				tickerData.setSell(tryToParseDouble(getJSONTickerKey(parseJSONObject(json), "l")));
				break;
			case BITPAY:
				// [{"code":"USD","name":"US Dollar","rate":597.4889},{"code":"EUR","name":"Eurozone Euro","rate":442.3828},...
				try {
					final JSONArray currencyRates = new JSONArray(json);
					for (int i=0; i<currencyRates.length(); i++) {
						final JSONObject currencyRate = currencyRates.getJSONObject(i);
						if (currencyRate.getString("code").equalsIgnoreCase("USD")) {
							tickerData.setLast(tryToParseDouble(getJSONTickerKey(currencyRate, "rate")));
							break;
						}
					}
				} catch (JSONException e) {
					// Do nothing
				}
				break;
			default:
				throw new IllegalStateException("Do not know about this service: " + this);
		}

		return tickerData;
	}

	private JSONObject parseJSONObject(String json) {
		try {
			return new JSONObject(json);
		} catch (JSONException e) {
			return new JSONObject();
		}
	}

	public String getName() {
		return name;
	}

	public String getTickerUrl(CurrencyConversion currencyConversion) {
		for (TickerUrl tickerUrl : tickerUrls) {
			if (tickerUrl.currencyConversion.equals(currencyConversion))
				return tickerUrl.tickerUrl;
		}
		throw new IllegalArgumentException("Currency " + currencyConversion + " is not supported by " + name);
	}

	public Integer getId() {
		return id;
	}

	public static RateService getDefaultService() {
		return BITSTAMP;
	}

	public static RateService getById(int id) {
		return lookup.get(id);
	}

	public List<CurrencyConversion> getCurrencyConversions() {
		List<CurrencyConversion> currencyConversions = new ArrayList<CurrencyConversion>();
		for (TickerUrl tickerUrl : tickerUrls) {
			currencyConversions.add(tickerUrl.currencyConversion);
		}
		return currencyConversions;
	}

	private static class TickerUrl {
		private final CurrencyConversion currencyConversion;
		private final String tickerUrl;

		TickerUrl(CurrencyConversion currencyConversion, String tickerUrl) {
			this.currencyConversion = currencyConversion;
			this.tickerUrl = tickerUrl;
		}
	}
}
