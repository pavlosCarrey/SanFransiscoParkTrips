package org.paul.sanfransiscoparktrips;

public class Bicycle {
	private String address;
	private String location;
	private int racks;
	private int spaces;
	private double latitude;
	private double longitude;
	
	public static final String
		ADDRESS = "yr_inst",
		LOCATION = "addr_num",
		RACKS = "racks",
		SPACES = "spaces",
		LATITUDE = "latitude",
		LONGITUDE = "latitude";

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public int getRacks() {
		return racks;
	}

	public void setRacks(int racks) {
		this.racks = racks;
	}

	public int getSpaces() {
		return spaces;
	}

	public void setSpaces(int spaces) {
		this.spaces = spaces;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	
	
}
