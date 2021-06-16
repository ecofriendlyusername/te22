package com.example.myStocks;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

public class UserPortfolio {
	private String username;
	private BigDecimal rturn; // total return
	private HashMap<String, ArrayList<BigDecimal>> stocks;
	// 키 - 종목이름, value -  1) 평단 2) 밸런스 
	
	public UserPortfolio(String username) {
		this.setUsername(username);
		setStocks(new HashMap<String, ArrayList<BigDecimal>>());
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public HashMap<String, ArrayList<BigDecimal>> getStocks() {
		return stocks;
	}

	public void setStocks(HashMap<String, ArrayList<BigDecimal>> stocks) {
		this.stocks = stocks;
	}

	public BigDecimal getRturn() {
		return rturn;
	}

	public void setRturn(BigDecimal rturn) {
		this.rturn = rturn;
	}
}

class SortbyReturn implements Comparator<UserPortfolio> {
    // Used for sorting in ascending order of
    // roll number
    public int compare(UserPortfolio a, UserPortfolio b)
    {
        return b.getRturn().compareTo(a.getRturn());
    }
}
