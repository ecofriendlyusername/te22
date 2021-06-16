package com.example.myStocks;

public class TickerOnly {
	private String ticker;
	
	public TickerOnly(String ticker) {
		this.setTicker(ticker);
	}

	public String getTicker() {
		return ticker;
	}

	public void setTicker(String ticker) {
		this.ticker = ticker;
	}
}
