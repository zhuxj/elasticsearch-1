package com.venky.elasticsearch.criteria;

import org.elasticsearch.common.unit.DistanceUnit;


public class GeoFilter {

	private GeoPoint pinLocation = new GeoPoint();
	
	private double distance;
	
	private DistanceUnit distanceUnit = DistanceUnit.KILOMETERS;

	public GeoPoint getPinLocation() {
		return pinLocation;
	}

	public void setPinLocation(GeoPoint pinLocation) {
		this.pinLocation = pinLocation;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public DistanceUnit getDistanceUnit() {
		return distanceUnit;
	}

	public void setDistanceUnit(DistanceUnit distanceUnit) {
		this.distanceUnit = distanceUnit;
	}
}
