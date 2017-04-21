package edu.uw.nan.account;

import edu.uw.ext.framework.account.CreditCard;

/**
 * @author Neil Nevitt
 * A pure JavaBean implementation of a credit card.
 */

public class CreditCardLaplace implements CreditCard {

	/**
	 * Version Id.
	 */
	private static final long serialVersionUID = 1980405198903931349L;

	/**
	 * Account number.
	 */
	private String accountNumber;
	/**
	 * Expiration date.
	 */
	private String expirationDate;
	/**
	 * The holder of the card.
	 */
	private String holder;
	/**
	 * The issuer of the card.
	 */
	private String issuer;
	/**
	 * The type of card.
	 */
	private String type;

	/**
	 * Constructor.  
	 */
	public CreditCardLaplace() {
		
	}
	
	/**
	 * Gets the account number.
	 * @return the account number.
	 */
	@Override
	public String getAccountNumber() {
		return this.accountNumber;
	}
	/**
	 * Gets the expiration date.
	 * @return the expiration date.
	 */
	@Override
	public String getExpirationDate() {
		return this.expirationDate;
	}
	/**
	 * Gets the holder.
	 * @return the holder.
	 */
	@Override
	public String getHolder() {
		return this.holder;
	}
	/**
	 * Gets the issuer.
	 * @return the card issuer.
	 */
	@Override
	public String getIssuer() {
		return this.issuer;
	}
	/**
	 * Get the type of card.
	 * @return the card type.
	 */
	@Override
	public String getType() {
		return this.type;
	}
	/**
	 * Sets the account number.
	 * @param accountNumber - the account number.
	 */
	@Override
	public void setAccountNumber(String accountNumber ) {
		this.accountNumber = accountNumber;
		
	}
	/**
	 * Sets the expiration date.
	 * @param expirationDate - the expiration date to set.
	 */
	@Override
	public void setExpirationDate(String expirationDate) {
		this.expirationDate = expirationDate;
	}
	/**
	 * Sets the holder.
	 * @param holder - the holder to set.
	 */
	@Override
	public void setHolder(String holder) {
		this.holder = holder;
	}
	/**
	 * Sets the issuer.
	 * @param issuer - the issuer to set.
	 */
	@Override
	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}
	/**
	 * Sets the type.
	 * @param type - the type to set.
	 */
	@Override
	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return String.format("%s, %s, %s, %s, %s", this.accountNumber, this.expirationDate, this.holder, this.issuer, this.type);
	} 

}
