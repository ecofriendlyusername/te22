package com.example.myStocks;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity(name = "OwnedStock")
public class OwnedStock implements Serializable {
	/**
	 * 
	 */
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	private static final long serialVersionUID = 1L;
	private String ticker;
	private BigDecimal avgbprice;
	private int stockbalance;
	private int untilNow;
	//@JoinColumn(name = "user_id", referencedColumnName = "id", insertable=false, updatable=false)
	@ManyToOne(optional = false, cascade=CascadeType.ALL, fetch = FetchType.LAZY)
	private User user;
	
	
	public OwnedStock() {
		
	}
	
	public OwnedStock(String ticker, BigDecimal price, int bought_amount) {
		this.avgbprice = price;
		this.stockbalance = bought_amount;
		this.untilNow = bought_amount;
		this.ticker = ticker;
	}
	
	public OwnedStock(String ticker, BigDecimal price, int bought_amount, User user) {
		this.avgbprice = price;
		this.stockbalance = bought_amount;
		this.untilNow = bought_amount;
		this.ticker = ticker;
		this.user = user;
	}
	
	public Integer getId() {
		return id;
	}
	
	public void setUser(User user) {
		this.user = user;
	}
	
	public User getUser() {
		return user;
	}
	
	public void setAvgPrice(BigDecimal price) {
		this.avgbprice = price;
	}
	
	public void setAvg_bprice(BigDecimal price, int amount) {
		this.avgbprice = (price.multiply(new BigDecimal(amount)).add(avgbprice.multiply(new BigDecimal(untilNow)))).divide(new BigDecimal(amount + untilNow));
	}
	
	public void setStockbalance(int amount, boolean buy) {
		if (buy)
			this.stockbalance = this.stockbalance + amount;
		else 
			this.stockbalance = this.stockbalance - amount;
	}
	
	public void setTicker(String ticker) {
		this.ticker = ticker;
	}
	
	public String getTicker() {
		return ticker;
	}
	
	public int getUntilNow() {
		return this.untilNow;
	}
	
	public void setUntilNow(int amount) {
		this.untilNow = this.untilNow + amount;
	}
	
	public void setUntilNowFresh(int amount) {
		this.untilNow = 0;
	}
	public BigDecimal getAvg_bprice() {
		return avgbprice;
	}
	
	public int getStock_balance() {
		return stockbalance;
	}
	
	public void addStock(BigDecimal price, int bought_amount) {
		 avgbprice = (avgbprice.multiply(new BigDecimal(untilNow)).add(price.multiply(new BigDecimal(bought_amount)))).divide(new BigDecimal(untilNow + bought_amount));
		 stockbalance += bought_amount;
	}
}