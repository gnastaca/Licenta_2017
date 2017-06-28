package Population;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.json.simple.JSONObject;

public class Map {
	private Collection<Location> map = new ArrayList<Location>();
	
	public void addLocation(Location location){
		this.map.add(location);
	}
	
	public Location getLocationByIndex(int index){
		int i = 0;
		for (Location location : map) {
			if(i == index)
				return location;
		}
		return null;
	}
	
	public void createMap(String fileName){
		try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
			String line;
			String coordinate[];
			int x, y;
			while ((line = br.readLine()) != null) {
				coordinate = line.split(" ");
				x = Integer.parseInt(coordinate[0])+3;
				y = Integer.parseInt(coordinate[1])+3;
				Location city = new Location(x,y);
				addLocation(city);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void ShowMap(){
		for (Location location : map) {
			System.out.print("|" + location.GetXCoordinate() + "-" + location.GetYCoordinate());
		}
		System.out.println();
	}
	

}
