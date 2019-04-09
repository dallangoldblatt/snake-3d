
public class SnakePart {
	
	private int x, y, z, heading;
	private SnakePart prevPart = null, nextPart = null;
	
	// HEADING
	//    0 5
	//    |/
	// 3--.--1
	//   /|
	//  4 2   
	
	public SnakePart (int x, int y, int z, int heading, SnakePart prevPart, SnakePart nextPart) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.heading = heading;
		this.prevPart = prevPart;
		this.nextPart = nextPart;
	}

	public int getX() {return x;}
	public int getY() {return y;}
	public int getZ() {return z;}
	public int getHeading() {return heading;}
	public SnakePart getPrev() {return prevPart;}
	public SnakePart getNext() {return nextPart;}
	public void setHeading(int heading) {this.heading=heading;}
	public void setPrev(SnakePart prevPart) {this.prevPart=prevPart;}
	public void setNext(SnakePart nextPart) {this.nextPart=nextPart;}
}
