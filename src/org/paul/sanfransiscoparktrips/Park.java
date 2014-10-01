package org.paul.sanfransiscoparktrips;

public class Park {
	private String name;
	private String type;
	private String manager;
	private String email;
	private String phone;
	private double latitude;
	private double longitude;
	
	public static final String
		NAME = "parkname",
		TYPE = "parktype",
		MANAGER = "psamanager",
		EMAIL = "email",
		PHONE = "number",
		LATITUDE = "location_1",
		LONGITUDE = "location_1";

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getManager() {
		return manager;
	}

	public void setManager(String manager) {
		this.manager = manager;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
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
