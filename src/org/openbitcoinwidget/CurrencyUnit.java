package org.openbitcoinwidget;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum CurrencyUnit {

	BTC(1, "BTC (bitcoin)", 1e0),
	MBTC(2, "mBTC", 1e-3),
	BIT(3, "Bit", 1e-6);

	public final Integer id;
	public final String description;
	public final double scale;

	CurrencyUnit(int id, String description, double scale) {
		this.id = id;
		this.description = description;
		this.scale = scale;
	}

	public static CurrencyUnit getDefault() {
		return MBTC;
	}
}
