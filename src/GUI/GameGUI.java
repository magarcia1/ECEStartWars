/**************************************************************************************************/

/*
* File: GameGUI
* Author: Miguel A. Garcia, Philip Showers, Fonji 
* NetID: magarcia1
* Date: December 5, 2015
*
* Description: GameGUI class contains the the logic methods and the methods required to create the 
* Graphical User Interface for this assignment.
* pressed.
*
*/

/**************************************************************************************************/

package GUI;

/**************************************************************************************************/

//import java.awt.Container;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

//import javax.swing.JButton;
import javax.swing.JFrame;

/**************************************************************************************************/

public class GameGUI implements MouseListener {
	private JFrame background;

	private ImagePanel back;

	public static boolean paused;
	public static boolean crashed;
	public static boolean started;
	public static boolean playedOnce;

	public boolean goingUp;
	private double upCount;

	public static int distance;
	public static int maxDistance;
	public static String maxRank;

	public final int X_POSITION;
	public final int NUM_OBSTACLES;
	public final int OBSTACLE_HEIGHT;
	public final int OBSTACLE_WIDTH;

	private int numSmoke;

	private ArrayList<MovingImage> obstacles;
	private ArrayList<MovingImage> smoke;
	private MovingImage Fighter;
	private ArrayList<MovingImage> bar;
	public GameGUI() {
		NUM_OBSTACLES = 35; //number of bars we have in our borders 
		OBSTACLE_HEIGHT = 125;
		OBSTACLE_WIDTH = 29;
		X_POSITION = 200;
		playedOnce = false;
		maxDistance = 0;

		load(new File("Best.txt"));

		initiate();
	}

	/* This method loads from a file the maximum score reached by the user. */
	public void load(File file) {
		try {
			@SuppressWarnings("resource")
			Scanner reader = new Scanner(file);
			while (reader.hasNext()) {
				int value = reader.nextInt();
				String theRank = reader.next();
				if (value > maxDistance) {
					maxDistance = value;
					maxRank = theRank;
				}
			}
		} catch (IOException i) {
			System.out.println("Error. " + i);
		}
	}

	//Save the newest high score to the "Best.txt" file
	public void save() {
		FileWriter out;
		try {
			out = new FileWriter("Best.txt");
			out.write("" + maxDistance + " " + maxRank);
			out.close();
		} catch (IOException i) {
			System.out.println("Error: " + i.getMessage());
		}
	}

	/*
	 * After we open the file with the max score hit by the user, initiate
	 * creates the Graphical User Interface the Star Wars game will have
	 */
	public void initiate() {
		if (!playedOnce) {
			background = new JFrame("Star Wars");
			background.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // closes
																		// the
																		// program
																		// when
																		// the
																		// window
																		// is
																		// closed
			background.setResizable(false); // don't allow the user to resize
											// the window
			background.setSize(new Dimension(1000, 600)); // The dimensions of
															// the Frame
			background.setVisible(true);

			back = new ImagePanel("background.JPG");
			background.add(back);

			back.addMouseListener(this);
		}
		playedOnce = true;
		goingUp = false;
		paused = false;
		crashed = false;
		started = false;

		distance = 0;
		upCount = 0.0;

		numSmoke = 15;
		
		bar = new ArrayList<MovingImage>();//TODO:
		obstacles = new ArrayList<MovingImage>();
		obstacles = new ArrayList<MovingImage>();
		smoke = new ArrayList<MovingImage>();

		Fighter = new MovingImage("XWingFighter.png", X_POSITION, 270);
		bar.add( new MovingImage("bar.jpg", 0, 0));
		bar.add( new MovingImage("bar.jpg", 0, 501));

		obstacles.add(new MovingImage("Obstacle.png", 1392, randomMidHeight()));
		obstacles.add(new MovingImage("Obstacle.png", 1972, randomMidHeight()));

		drawObstacles();
	}

	public void drawObstacles() {
		long lastCopter = System.currentTimeMillis();
		long lastSmoke = System.currentTimeMillis();
		// long lastSound = System.currentTimeMillis();
		int firstUpdates = 0;
		double lastDistance = (double) System.currentTimeMillis();
		while (true) {
			// If the game is not paused and the ship not crashed and the game
			// is running and the current time minus a constant is greater than 
			// the last distance, it means we do have to update the points of 
			// our ship since is moving. We do also have to update the distance 
			// to the distance we currently are
			//This also sets how fast the distance will increment
			if (!paused && !crashed && started ) {
				
				if( (double) System.currentTimeMillis() - (double) (2900 / 40) > lastDistance) {
					lastDistance = System.currentTimeMillis();  // updating the
					// distance to be the
					// distance in where
					// we are
					distance++; // the more distance the more the points
				}

				if ((double) System.currentTimeMillis() - 8 > lastCopter) {
					lastCopter = System.currentTimeMillis();
					updateFighter();
					updateMiddle();
				}
//				if ((double) System.currentTimeMillis() - 100 > obstacleTimeCounter) {
//					obstacleTimeCounter = System.currentTimeMillis();
//					updateObstacles();
//				}
				//controls the movement of the smoke in the fighter ship
				if ((double) System.currentTimeMillis() - 60 > lastSmoke) {
					lastSmoke = System.currentTimeMillis();
					if (firstUpdates < numSmoke) {
						firstUpdates++;
						smoke.add(new MovingImage("smoke.GIF", 187, Fighter.getY()));
						//setting the location of the specific smoke in the JFrame on the location it has to go
						for (int x = 0; x < firstUpdates; x++)
							smoke.set(x, new MovingImage("smoke.GIF", smoke.get(x).getX() - 12, smoke.get(x).getY()));
					} else {
						//It is important to know that the # of smokes is limited; however, the program makes the user
						//believe there is unlimited smokes
						for (int x = 0; x < numSmoke - 1; x++)
							smoke.get(x).setY(smoke.get(x + 1).getY());
						smoke.set(numSmoke - 1, new MovingImage("smoke.GIF", 187, Fighter.getY()));
					}
				}
			}
			//Repaints the JFrame with the new information
			back.updateImages(obstacles, Fighter, smoke, bar);
		}
	}

//	public void updateObstacles() {
//		for (int x = 0; x < (NUM_OBSTACLES - 1); x++) // move all but the last
//												// rectangle 1 spot to the left
//		{
//			//topObstacles.set(x, new MovingImage("Obstacle.png", OBSTACLE_WIDTH * x, topObstacles.get(x + 1).getY()));
//			bottomObstacles.set(x, new MovingImage("Obstacle.png", OBSTACLE_WIDTH * x, bottomObstacles.get(x + 1).getY()));
//		}
//		lastObstacle();
//	}

//	public void lastObstacle() {
//		if (distance % 400 == 0)
//			moveIncrement++;
//		if (40 < 2) // if too high, move down
//			moveDown();
//		else if (bottomObstacles.get(26).getY() > 463) // else if too low, move up
//			moveUp();
//		else // else move randomly
//		{
//			if ((int) (Math.random() * 60) == 50)
//				randomDrop();
//			else {
//				if ((int) (Math.random() * 2) == 1)
//					moveUp();
//				else
//					moveDown();
//			}
//		}
//	}

//	public void randomDrop() {
//		topObstacles.get(26).setY(topObstacles.get(26).getY() + (463 - bottomObstacles.get(26).getY()));
//		bottomObstacles.get(26).setY(463);
//	}

//	public void moveDown() {
////		topObstacles.set((NUM_OBSTACLES - 1),
////				new MovingImage("Obstacle.png", OBSTACLE_WIDTH * (NUM_OBSTACLES - 1), topObstacles.get(26).getY() + moveIncrement));
//		bottomObstacles.set((NUM_OBSTACLES - 1), new MovingImage("Obstacle.png", OBSTACLE_WIDTH * (NUM_OBSTACLES - 1),
//				bottomObstacles.get(26).getY() + moveIncrement));
//	}

//	public void moveUp() {
////		bottomObstacles.set((NUM_OBSTACLES - 1), new MovingImage("Obstacle.png", OBSTACLE_WIDTH * (NUM_OBSTACLES - 1),
////				bottomObstacles.get(26).getY() - moveIncrement));
////		topObstacles.set((NUM_OBSTACLES - 1),
////				new MovingImage("Obstacle.png", OBSTACLE_WIDTH * (NUM_OBSTACLES - 1), topObstacles.get(26).getY() - moveIncrement));
//	}

	public int randomMidHeight() {
		int max = 500; //Bottom of playable screen
		int min = 0;  //Top of playable screen

//		for (int x = 0; x < NUM_OBSTACLES; x++) {
////			if (topObstacles.get(x).getY() > min)
////				min = (int) topObstacles.get(x).getY();
//			if (bottomObstacles.get(x).getY() < max)
//				max = (int) bottomObstacles.get(x).getY();
//		}
		min += OBSTACLE_HEIGHT;
		max -= (OBSTACLE_HEIGHT + min);
		return min + (int) (Math.random() * max);
	}

	// moves the randomly generated middle obstacles.
	//Use a linear eq that constantly makes the obstacles get genereated faster.
	//As distance increases, the difficulty increases.
	public void updateMiddle() {
		if (obstacles.get(0).getX() > -1 * OBSTACLE_WIDTH) {
			obstacles.set(0, new MovingImage("Obstacle.png", obstacles.get(0).getX() - (OBSTACLE_WIDTH / (-0.001 * distance + 5)),
					obstacles.get(0).getY()));
			obstacles.set(1, new MovingImage("Obstacle.png", obstacles.get(1).getX() - (OBSTACLE_WIDTH / (-0.001 * distance + 5)),
					obstacles.get(1).getY()));
		} else {
			obstacles.set(0, new MovingImage("Obstacle.png", obstacles.get(1).getX() - (OBSTACLE_WIDTH / (-0.001 * distance + 5)),
					obstacles.get(1).getY()));
			obstacles.set(1, new MovingImage("Obstacle.png", obstacles.get(0).getX() + 580, randomMidHeight()));
		}
	}

	//Check to see if the obstacle and the fighter ship are at the same coordinates on the screen.
	//If they are, then the fighter ship is hit by an obstacle.
	public boolean isInMidRange(int num) {  
		Rectangle middleCheck = new Rectangle((int) obstacles.get(num).getX(), (int) obstacles.get(num).getY(),
				OBSTACLE_WIDTH, OBSTACLE_HEIGHT);
		Rectangle fighterCheck = new Rectangle((int) Fighter.getX(), (int) Fighter.getY(), 145, 48);
		return middleCheck.intersects(fighterCheck);
	}

	// moves the Fighter
	public void updateFighter() {
		upCount += .08;  //Increment the upCount
		if (goingUp) { //If the mouse is pressed
			if (upCount < 3.5)  //If the mouse is pressed for less time, the fighter moves slower
				Fighter.setPosition(X_POSITION, (double) (Fighter.getY() - (.3 + upCount)));
			else  //If the mouse is pressed longer, the fighter moves faster upward
				Fighter.setPosition(X_POSITION, (double) (Fighter.getY() - (1.2 + upCount)));
			Fighter.setImage("XWingFighter.png");
		}
		else {  //If the mouse is released, the fighter moves downward
			if (upCount < 1)  //If mouse is released for short amount of time, decelerate slower
				Fighter.setPosition(X_POSITION, (double) (Fighter.getY() + upCount));
			else //If mouse is released for longer amount of time, decelerate faster
				Fighter.setPosition(X_POSITION, (double) (Fighter.getY() + (1.2 + upCount)));
			Fighter.setImage("XWingFighter.png");
		}
		if (isHit())
			crash();
	}

	public void crash() {
		crashed = true;
		if (distance > maxDistance) {
			maxDistance = distance;
			save();
		}

		initiate();
	}

	public boolean isHit() {
//		for (int x = 3; x <= 7; x++)
			//Determines the lowest the ship can go
//			if (Fighter.getY() + 48 >= bottomObstacles.get(x).getY())
//				return true;
			
		if (Fighter.getY() <= 70){
			return true;
		}
		
		if (Fighter.getY() >= 455){
			return true;
		}
//		for (int y = 3; y <= 7; y++)
//			//Determines the highest the ship can go
//			if (Fighter.getY() <= topObstacles.get(y).getY() + OBSTACLE_HEIGHT)
//				return true;
		for (int z = 0; z <= 1; z++)
			if (isInMidRange(z))
				return true;
		return false;
	}

	// Called when the mouse exits the game window
	public void mouseExited(MouseEvent e) {

		if (started) {
			paused = true;
			// move.close();
		}

	}

	// Called when the mouse enters the game window
	public void mouseEntered(MouseEvent e) {

	}

	// Called when the mouse is released
	public void mouseReleased(MouseEvent e) {
		goingUp = false;
		upCount = 0;
		if (paused)
			paused = false;
	}

	// Called when the mouse is pressed
	public void mousePressed(MouseEvent e) {
		if (!started) {
			started = true;
		}
		goingUp = true;
		upCount = 0;
	}

	// Called when the mouse is clicked
	public void mouseClicked(MouseEvent e) {

	}
}

/**************************************************************************************************/