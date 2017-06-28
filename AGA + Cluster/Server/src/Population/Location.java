package Population;

public class Location {
	private int x;
	private int y;

	public Location(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	public int GetXCoordinate(){
		return this.x;
	}
	
	public int GetYCoordinate(){
		return this.y;
	}
}
