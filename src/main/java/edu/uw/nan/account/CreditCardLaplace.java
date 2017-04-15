package edu.uw.nan.account;

import edu.uw.ext.framework.account.CreditCard;

@SuppressWarnings("serial")
public class CreditCardLaplace implements CreditCard {

	private String accountNumber;
	private String expirationDate;
	private String holder;
	private String issuer;
	private String type;
	
	
	@Override
	public String getAccountNumber() {
		return this.accountNumber;
	}

	@Override
	public String getExpirationDate() {
		return this.expirationDate;
	}

	@Override
	public String getHolder() {
		return this.holder;
	}

	@Override
	public String getIssuer() {
		return this.issuer;
	}

	@Override
	public String getType() {
		return this.type;
	}

	@Override
	public void setAccountNumber(String accountNumber ) {
		this.accountNumber = accountNumber == null ? "" : accountNumber;
		
	}

	@Override
	public void setExpirationDate(String expirationDate) {
		this.expirationDate = expirationDate == null ? "" : expirationDate;
	}

	@Override
	public void setHolder(String holder) {
		this.holder = holder == null ? "" : holder;
	}

	@Override
	public void setIssuer(String issuer) {
		this.issuer = issuer == null ? "" : issuer;
	}

	@Override
	public void setType(String type) {
		this.type = type == null ? "" : type;
	}

}
