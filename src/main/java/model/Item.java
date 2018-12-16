package model;

import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

public class Item {
	
	public GeoJsonPoint getPoint() {
		return point;
	}

	public void setPoint(GeoJsonPoint point) {
		this.point = point;
	}

	private String id;
	
	private String name;
	
	private String number;
	
	private GeoJsonPoint point;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	
	public Item(String id, String name, String number, GeoJsonPoint point) {
		super();
		this.id = id;
		this.name = name;
		this.number = number;
		this.point = point;
	}

	public Item() {

	}
	
	@Override
	public String toString() {
		return "Item [id=" + id + ", name=" + name + ", number=" + number + "]";
	}
	
	

}
