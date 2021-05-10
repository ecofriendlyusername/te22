package com.example.myStocks;

import java.math.BigDecimal;

public class MyStocks {
	private String ticker;
	private int stockbalance;
	private BigDecimal avgbprice;
	
	public MyStocks(OwnedStock os) {
		this.ticker = os.getTicker();
		this.stockbalance = os.getStock_balance();
		this.avgbprice = os.getAvg_bprice();
	}
	
	public String getTicker() {
		return this.ticker;
	}
	
	public int getStockbalance() {
		return this.stockbalance;
	} 
	
	public BigDecimal getAvgbprice() {//getter에서 property name이 정해지는 듯
		return this.avgbprice;
	}
}
