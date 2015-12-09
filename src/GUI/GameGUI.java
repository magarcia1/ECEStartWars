/**************************************************************************************************/

/*
* File: GameGUI
* Author: Miguel A. Garcia, Philip Showers, Tembong Fonji 
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

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

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

	//Constants used in the GUI 
	public final int X_POSITION;
	public final int OBSTACLE_HEIGHT;
	public final int OBSTACLE_WIDTH;

	private int numSmoke;

	private ArrayList<MovingImage> obstacles;
	private ArrayList<MovingImage> smoke;
	private MovingImage Fighter;
	private ArrayList<MovingImage> bar;
	public GameGUI() {
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
				int value = reader.nextInt();  //Read in the high score
				String theRank = reader.nextLine();  //Read in the Highest Rank 
				
				//Set new high score and rank
				if (value > maxDistance) {
					maxDistance = value;
					maxRank = theRank;
				}
			}
		} catch (IOException i) {
			System.out.println("Error. " + i);
		}
	}

	//Save the newest high score and rank to the "Best.txt" file
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
			background.setSize(new Dimension(1100, 600)); // The dimensions of
															// the Frame
			background.setVisible(true);

			back = new ImagePanel("background.JPG");
			background.add(back);

			back.addMouseListener(this);
		}
		
		//Initialize all of the global variables
		playedOnce = true;
		goingUp = false;
		paused = false;
		crashed = false;
		started = false;

		distance = 0;
		upCount = 0.0;
		numSmoke = 15;
		
		bar = new ArrayList<MovingImage>();
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

	public int randomMidHeight() {
		int max = 500; //Bottom of playable screen
		int min = -20;  //Top of playable screen

		min += OBSTACLE_HEIGHT;
		max -= (OBSTACLE_HEIGHT + min);
		return min + (int) (Math.random() * max);
	}

	// moves the randomly generated middle obstacles.
	//Use a linear equation that constantly makes the obstacles get generated faster.
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

	//This is called if the fighter crashes into an obstacle.
	//It will set the new high score if applicable and save it to Best.txt
	public void crash() {
		crashed = true;
		if (distance > maxDistance) {
			maxDistance = distance;
			maxRank = getRank(maxDistance);
			save();
		}

		//Re-initiate the game. (Start new game)
		initiate();
	}

	//This will return the rank of the player based on the Score achieved
	private String getRank(int maxDistance) {
		String theRank;
		if (maxDistance < 200){
            theRank = "Youngling";
            return theRank;
        }
        else if (maxDistance >= 200 && maxDistance < 500){
            theRank = "Padawan";
            return theRank;
        }
        else if (maxDistance >= 1000 && maxDistance < 3000){
            theRank = "Jedi Knight";
            return theRank;
        }
        else {
            theRank = "Jedi Master";
            return theRank;
        }
	}

	//Checks to see if the fighterShip has the same coordinates as the bottom of the 
	//GUI screen or the top of the GUI screen. 
	//It also checks to see if it has the same coordinates as an obstacle.
	//If any are true, the ship is hit, and the crash sequence starts.
	public boolean isHit() {	
		if (Fighter.getY() <= 70){
			return true;
		}
		
		if (Fighter.getY() >= 455){
			return true;
		}
		
		for (int i = 0; i <= 1; i++)
		{
			if (isInMidRange(i)) return true;
		}
		return false;
	}

	// Called when the mouse exits the game window
	public void mouseExited(MouseEvent e) {

		if (started) {
			paused = true;
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