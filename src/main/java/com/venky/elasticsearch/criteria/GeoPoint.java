package com.venky.elasticsearch.criteria;

import org.codehaus.jackson.annotate.JsonProperty;


public class GeoPoint {
	
	private double latitude;
	private double longitude;
	
	/**
	 * @return the latitude
	 */
	@JsonProperty("lat")
	public double getLatitude() {
		return latitude;
	}
	/**
	 * @param latitude the latitude to set
	 */
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	/**
	 * @return the longitude
	 */
	@JsonProperty("lon")
	public double getLongitude() {
		return longitude;
	}
	/**
	 * @param longitude the longitude to set
	 */
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	
}
