import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.*;

enum State {
	INIT, PAUSE, PLAY, GAMEOVER;
}
 
public class Snake3D extends JPanel implements KeyListener {

	private static final long serialVersionUID = 1L;

	private final int HORIZ_SQUARES = 8, VERT_SQUARES = 5,
						HORIZ_BUFFER = 30, VERT_BUFFER = 20,
						HORIZ_STAGGER = 150, VERT_STAGGER = 100;
	
	private boolean[][][] occupied = new boolean[3][VERT_SQUARES][HORIZ_SQUARES];
	
	private ArrayList<Position> openPositions = new ArrayList<Position>();
	
	private State state = State.INIT;
	
	private SnakePart head, tail;
	
	private Position food;
	
	private int heading, gridSpacing;
	
	private boolean ateFood;

	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode()== KeyEvent.VK_W && head.getHeading()!=2) heading = 0;
        else if(e.getKeyCode()== KeyEvent.VK_D && head.getHeading()!=3) heading = 1;
        else if(e.getKeyCode()== KeyEvent.VK_S && head.getHeading()!=0) heading = 2;
        else if(e.getKeyCode()== KeyEvent.VK_A && head.getHeading()!=1) heading = 3;
        else if(e.getKeyCode()== KeyEvent.VK_Q && head.getHeading()!=5) heading = 4;
        else if(e.getKeyCode()== KeyEvent.VK_E && head.getHeading()!=4) heading = 5;
        else if(e.getKeyCode()== KeyEvent.VK_P) state = State.INIT;
    }
	
	public void keyReleased(KeyEvent e) {}
	public void keyTyped(KeyEvent e) {}
	
	public void paintComponent(Graphics g) {
		switch (state) {
		case INIT:
			// Set up variables for game
			int w = getWidth()-2*HORIZ_BUFFER-2*HORIZ_STAGGER;
			int h = getHeight()-2*VERT_BUFFER-2*VERT_STAGGER;
			gridSpacing = (w/HORIZ_SQUARES < h/VERT_SQUARES) ? w/HORIZ_SQUARES : h/VERT_SQUARES;
			occupied = new boolean[3][VERT_SQUARES][HORIZ_SQUARES];
			
			heading = 1;
			head = new SnakePart(HORIZ_SQUARES/2-1,VERT_SQUARES/2,1,heading,null,null);
			tail = head;
			occupied[1][VERT_SQUARES/2][HORIZ_SQUARES/2-1] = true;
			
			setNextFoodPosition();
			ateFood = false;
			
			state = State.PAUSE;
			break;
		case PAUSE:
			state = State.PLAY;
			break;
		case GAMEOVER:
			g.drawString("Game Over", getWidth()/2, getHeight()/2);
			break;
		case PLAY:
			// Move snake
			moveSnake();
			
			// Draw game
			if (state != State.GAMEOVER) {
				// Draw one layer at a time for proper overlapping
				for (int z = 2; z >= 0; z--) {
					drawGrid(g, z);
					drawFood(g,z);
					drawSnake(g, z);
				}	
			}
			break;
		}
	}
	
	private void moveSnake() {
		int nextX = head.getX(), nextY = head.getY(), nextZ = head.getZ();
		
		// Calculate position of head at next tick
		switch (heading) {
		case 0: 
			nextY--;
			if (nextY < 0) state = State.GAMEOVER;
			break;
		case 1: 
			nextX++;
			if (nextX >= HORIZ_SQUARES) state = State.GAMEOVER;
			break;
		case 2: 
			nextY++;
			if (nextY >= VERT_SQUARES) state = State.GAMEOVER;
			break;
		case 3: 
			nextX--;
			if (nextX < 0) state = State.GAMEOVER;
			break;
		case 4: 
			nextZ--;
			if (nextZ < 0) state = State.GAMEOVER;
			break;
		case 5: 
			nextZ++;
			if (nextZ > 2) state = State.GAMEOVER;
			break;
		}
		
		if (snakeHasHitItself()) state = State.GAMEOVER;
		
		if(state != State.GAMEOVER) {
			// Create new head and move pointer
			occupied[nextZ][nextY][nextX] = true;
			SnakePart temp = new SnakePart(nextX, nextY, nextZ, heading, null, head);
			head.setPrev(temp);
			head = temp;
			
			ateFood = nextX==food.getX() && nextY==food.getY() && nextZ==food.getZ();
			
			if (!ateFood) {
				// Don't allow snake to grow by removing tail
				occupied[tail.getZ()][tail.getY()][tail.getX()] = false;
				tail = tail.getPrev();
				tail.setNext(null);
			}
			else {
				setNextFoodPosition();
				ateFood = false;
			}
		}
	}
	
	private void setNextFoodPosition() {
		
		// Update openPositions
		openPositions = new ArrayList<Position>();
		for(int z = 0; z<=2; z++) {
			for(int y = 0; y<VERT_SQUARES; y++) {
				for(int x = 0; x<HORIZ_SQUARES; x++) {
					if(!occupied[z][y][x]) {openPositions.add(new Position(x,y,z));}
				}
			}
		}
		
		// Choose a random position
		Random rand = new Random();
		food = openPositions.get(rand.nextInt(openPositions.size()));
	}
	
	private void drawGrid(Graphics g, int z) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setStroke(new BasicStroke(2));

		int x,y;
		Color paneColor;
		
		if (z==0) { paneColor = new Color(246,234,0); }
		else if (z==1) { paneColor = new Color(247,0,139); }
		else { paneColor = new Color(0,170,234); }
		g2.setColor(paneColor);
			
		// Draw horizontal lines
		for(int i = 0; i <= VERT_SQUARES; i++) {
			x=HORIZ_BUFFER+(z)*HORIZ_STAGGER;
			y=VERT_BUFFER+(2-z)*VERT_STAGGER+i*gridSpacing;
			g2.drawLine(x, y, x+HORIZ_SQUARES*gridSpacing, y);
		}
		// Draw vertical lines
		for(int i = 0; i <= HORIZ_SQUARES; i++) {
			x=HORIZ_BUFFER+(z)*HORIZ_STAGGER+i*gridSpacing;
			y=VERT_BUFFER+(2-z)*VERT_STAGGER;
			g2.drawLine(x, y, x, y+VERT_SQUARES*gridSpacing);
		}
	}
	
	private void drawFood(Graphics g, int z) {
		if (z == food.getZ()) {
			g.setColor(new Color(255,255,255));
			g.fillOval(getXFromIndex(food.getX(), food.getY(), z) + gridSpacing/6,
						getYFromIndex(food.getX(), food.getY(), z) + gridSpacing/6, 
						gridSpacing - gridSpacing/3, 
						gridSpacing - gridSpacing/3);
			}
	}
	
	private void drawSnake(Graphics g, int z) {
		SnakePart snakePart = head;
		Color snakeColor;
		
		if (z==0) { snakeColor = new Color(255,255,0); }
		else if (z==1) { snakeColor = new Color(255,0,150); }
		else { snakeColor = new Color(0,190,255); }
		g.setColor(snakeColor);

		do {
			if (z == snakePart.getZ()) {
				g.fillOval(getXFromIndex(snakePart.getX(), snakePart.getY(), z) + gridSpacing/10,
							getYFromIndex(snakePart.getX(), snakePart.getY(), z) + gridSpacing/10, 
							gridSpacing - gridSpacing/5, 
							gridSpacing - gridSpacing/5);
			}
			snakePart = snakePart.getNext();
		} while (snakePart != null);
	}
	
	private int getXFromIndex(int x, int y, int z) {
		return HORIZ_BUFFER+(z)*HORIZ_STAGGER+x*gridSpacing;
	}
	
	private int getYFromIndex(int x, int y, int z) {
		return VERT_BUFFER+(2-z)*VERT_STAGGER+y*gridSpacing;
	}
	
	private boolean snakeHasHitItself() {
		SnakePart snakePart = head;
		while (snakePart.getNext() != null) {
			snakePart = snakePart.getNext();
			if(head.getX() == snakePart.getX() && head.getY() == snakePart.getY() && head.getZ() == snakePart.getZ()) return true;	
		}
		return false;
	}
 
	public static void main(String[] args) throws InterruptedException {
		
		JFrame frame = new JFrame("Snake 3D");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBackground(Color.black);
	    frame.setSize(1080, 720);
	 
	   	Snake3D panel = new Snake3D();
	    frame.add(panel);
	    frame.setVisible(true);
	    frame.addKeyListener(panel);
	    
	    while (true) {
		    Thread.sleep(300);
		    frame.repaint();
	    }
	}
}