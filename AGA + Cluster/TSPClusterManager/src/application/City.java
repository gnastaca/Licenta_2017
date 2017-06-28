package application;

public class City {
	private double x;
	private double y;
	
	public City(double x, double y){
		this.x = x;
		this.y = y;
	}
	
	public double getXCoordinate(){
		return this.x;
	}
	
	public double getYCoordinate(){
		return this.y;
	}
	
	public double distanceUntilAnotherCity(City city){
		return Math.sqrt(Math.pow((this.x - city.x),2) + Math.pow((this.y - city.y),2));
	}
}
