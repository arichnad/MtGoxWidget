package org.openbitcoinwidget;

public class WidgetPreferences {

	private RateService rateService = RateService.getDefaultService();
	private ColorMode colorMode = ColorMode.Default;
	private CurrencyConversion currencyConversion = CurrencyConversion.getDefault();
	private CurrencyUnit currencyUnit = CurrencyUnit.getDefault();

	public ColorMode getColorMode() {
		return colorMode;
	}

	public void setColorMode(ColorMode colorMode) {
		this.colorMode = colorMode;
	}

	public CurrencyConversion getCurrencyConversion() {
		return currencyConversion;
	}

	public void setCurrencyConversion(CurrencyConversion currencyConversion) {
		this.currencyConversion = currencyConversion;
	}

	public CurrencyUnit getCurrencyUnit() {
		return currencyUnit;
	}

	public void setCurrencyUnit(CurrencyUnit currencyUnit) {
		this.currencyUnit = currencyUnit;
	}

	public RateService getRateService() {
		return rateService;
	}

	public void setRateService(RateService rateService) {
		this.rateService = rateService;
	}

	@Override
	public String toString() {
		return "WidgetPreferences{" +
				"colorMode=" + colorMode +
				", rateService=" + rateService +
				", currencyConversion=" + currencyConversion +
				", currencyUnit=" + currencyUnit +
				'}';
	}
}
