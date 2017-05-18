package edu.uw.nan.account;

import edu.uw.ext.framework.account.Address;


public class AddressLaplace implements Address {
 

	/** 
	 * Version Id.
	 */
	private static final long serialVersionUID = 6841030734859962365L;
	
	/**
	 * Street address.
	 */
	private String streetAddress;
	/**
	 * The city.
	 */
	private String city;
	/**
	 * The state.
	 */
	private String state;
	/**
	 * The zip code.
	 */
	private String zipCode;
	
	// simple constructor
	
	
	/**
	 * Gets the city.
	 * @return the city.
	 */
	@Override
	public String getCity() {
		return this.city;
	}
	/**
	 * Gets the State.
	 * @return the state.
	 */
	@Override
	public String getState() {
		return this.state;
	}
	/**
	 * Get Street address.
	 * @return the street address.
	 */
	@Override
	public String getStreetAddress() {
		return this.streetAddress;
	}
	/**
	 * Get the zip code
	 * @return the zip code.
	 */
	@Override
	public String getZipCode() {
		return this.zipCode;
	}
	/**
	 * Sets the city.
	 * @param city - city to set.
	 */
	@Override
	public void setCity(final String city) {
		this.city = city;
	}
	/**
	 * Sets the state.
	 * @param state - the state to set.
	 */
	@Override
	public void setState(final String state) {
		this.state =state;
		
	}
	/**
	 * Sets the street address.
	 * @param streetAddress - the street address to set.
	 */
	@Override
	public void setStreetAddress(final String streetAddress) {
		this.streetAddress = streetAddress;
		
	}
	/**
	 * Sets the zip code.
	 * @param zipCode - the zip code to set.
	 */
	@Override
	public void setZipCode(String zipCode) {
		this.zipCode =zipCode;
		
	}
	
	public String toString() {
		return String.format("%s, %s, %s, %s", this.city, this.state, this.streetAddress, this.zipCode);
	}
	
}
