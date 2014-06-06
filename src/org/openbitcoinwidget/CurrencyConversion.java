package org.openbitcoinwidget;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum CurrencyConversion {

	// Bitcoin
	BTC_USD(1, "BTC/USD", "$"),
	BTC_EUR(2, "BTC/EUR", "€"),
	BTC_GBP(3, "BTC/GBP", "£"),
	BTC_CAD(4, "BTC/CAD", "$"),
	BTC_AUD(5, "BTC/AUD", "$"),
	BTC_CNY(6, "BTC/CNY", "¥"),
	BTC_JPY(7, "BTC/JPY", "¥"),
	BTC_RUB(8, "BTC/RUB", "р"),
	BTC_SEK(9, "BTC/SEK", "k"),
	BTC_PLN(10,"BTC/PLN", "z"),
	BTC_ILS(11,"BTC/ILS", "₪"),

	// Litecoin
	LTC_BTC(106, "LTC/BTC", "฿", DigitalCurrency.LITECOIN),
	LTC_USD(107, "LTC/USD", "$", DigitalCurrency.LITECOIN),
	LTC_EUR(108, "LTC/EUR", "€", DigitalCurrency.LITECOIN),
	LTC_CNY(109, "LTC/CNY", "¥", DigitalCurrency.LITECOIN);

	public static enum DigitalCurrency {BITCOIN, LITECOIN};

	public final Integer id;
	public final String description;
	public final String symbol;
	public final DigitalCurrency digitalCurrency;
	private static final Map<Integer,CurrencyConversion> lookup = new HashMap<Integer,CurrencyConversion>();

	CurrencyConversion(int id, String description, String symbol) {
		this(id, description, symbol, DigitalCurrency.BITCOIN); // Default Bitcoin
	}

	CurrencyConversion(int id, String description, String symbol, DigitalCurrency digitalCurrency) {
		this.id = id;
		this.description = description;
		this.symbol = symbol;
		this.digitalCurrency = digitalCurrency;
	}

	static {
		for(CurrencyConversion s : EnumSet.allOf(CurrencyConversion.class))
			lookup.put(s.id, s);
	}


	public static CurrencyConversion getDefault() {
		return BTC_USD;
	}

	public static CurrencyConversion getById(int id) {
		return lookup.get(id);
	}
}
