package com.citi.tts.solidityDemo;

import java.math.BigInteger;

public class OrderDetail {

	private String tokenName;
	
	private BigInteger quantity;

	private BigInteger cost;

	public String getQuantity() {
		return quantity.toString(10);
	}

	public void setQuantity(BigInteger quantity) {
		this.quantity = quantity;
	}

	public String getCost() {
		return cost.toString(10);
	}

	public void setCost(BigInteger cost) {
		this.cost = cost;
	}

	public String getTokenName() {
		return tokenName;
	}

	public void setTokenName(String tokenName) {
		this.tokenName = tokenName;
	}

}
