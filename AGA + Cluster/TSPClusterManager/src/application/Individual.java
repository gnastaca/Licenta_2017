package application;
import java.util.ArrayList;
import java.util.Collections;


public class Individual {
	private ArrayList<City> individual =  new ArrayList<City>();
	private double fitness;
	private int distance;
	private int numberOfcity;
	public static ArrayList<City> individualPrint =  new ArrayList<City>();
	
	Individual(int numberOfcity){
		this.numberOfcity = numberOfcity;
		for(int i = 0; i < this.numberOfcity; i++){
			this.individual.add(null);
		}
		fitness = 0;
		distance = 0;
	}
	
	public City getCityFromRoute(int cityIndexInRoute){
		return (City)this.individual.get(cityIndexInRoute);
	}
	
	public void setCityRoute(City city, int index){
		fitness = 0;
		distance = 0;
		individual.set(index, city);
	}
	
	public int getDistance(){
		int routeLength = 0;
		if(distance == 0){
			City startCity = null , destinationCity = null;
			for(int index  = 1; index < individual.size(); index++){
				startCity = (City) individual.get(index-1);
				destinationCity = (City) individual.get(index);
				routeLength += startCity.distanceUntilAnotherCity(destinationCity);
			}
			startCity = destinationCity;
			destinationCity = (City) individual.get(0);
			routeLength += startCity.distanceUntilAnotherCity(destinationCity);
		}
		return routeLength;
	}
	
	public int getIndividualSize(){
		return individual.size();
	}
  
    public void printIndividual(){
    	for(int i = 0; i < this.individual.size(); i++){
    		City city = this.individual.get(i);
    		System.out.print("|" +city.getXCoordinate() + " " + city.getYCoordinate() + "|");
    	}
    }
    
}
