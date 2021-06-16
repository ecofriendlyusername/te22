package com.example.myStocks;

public class UserForRanking {
	private String username;
	private double rturn;
	public UserForRanking(String username, double rturn) {
		this.setUsername(username);
		this.setRturn(rturn);
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public double getRturn() {
		return rturn;
	}

	public void setRturn(double rturn) {
		this.rturn = rturn;
	}
}
