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
	// private Container container;
	// private JButton button;

	private ImagePanel back;

	public static boolean paused;
	public static boolean crashed;
	public static boolean started;
	public static boolean playedOnce;

	public boolean goingUp;
	private double upCount;

	public static int distance;
	public static int maxDistance;

	public final int XPOS;
	public final int NUMRECS;
	public final int RECHEIGHT;
	public final int RECWIDTH;

	private int moveIncrement;
	private int numSmoke;

	private ArrayList<MovingImage> toprecs;
	private ArrayList<MovingImage> bottomrecs;
	private ArrayList<MovingImage> middlerecs;
	private ArrayList<MovingImage> recs;
	private ArrayList<MovingImage> smoke;
	private MovingImage Fighter;
	private ArrayList<MovingImage> bar;
	public GameGUI() {
		NUMRECS = 35; //number of rectangles we have in our borders 
		RECHEIGHT = 73;
		RECWIDTH = 29;
		XPOS = 200;
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
				if (value > maxDistance)
					maxDistance = value;
			}
		} catch (IOException i) {
			System.out.println("Error. " + i);
		}
	}

	public void save() {
		FileWriter out;
		try {
			out = new FileWriter("Best.txt");
			out.write("" + maxDistance);
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
		upCount = 0;

		moveIncrement = 2;
		numSmoke = 15;
		
		bar = new ArrayList<MovingImage>();
		recs = new ArrayList<MovingImage>();
		middlerecs = new ArrayList<MovingImage>();
		bottomrecs = new ArrayList<MovingImage>();
		smoke = new ArrayList<MovingImage>();

		Fighter = new MovingImage("XWingFighter.png", XPOS, 270);
		bar.add( new MovingImage("bar.jpg", 0, 0));
		bar.add( new MovingImage("bar.jpg", 0, 501));
	
		for (int x = 0; x < NUMRECS; x++)
			bottomrecs.add(new MovingImage("CeilingFloor.png", RECWIDTH * x, 501));

		middlerecs.add(new MovingImage("CeilingFloor.png", 1392, randomMidHeight()));
		middlerecs.add(new MovingImage("CeilingFloor.png", 1972, randomMidHeight()));

		drawRectangles();
	}

	public void drawRectangles() {
		long last = System.currentTimeMillis();
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
			if (!paused && !crashed && started
					&& (double) System.currentTimeMillis() - (double) (2900 / 40) > lastDistance) {
				lastDistance = System.currentTimeMillis();  // updating the
															// distance to be the
															// distance in where
															// we are
				distance++; // the more distance the more the points
			}

			if (!paused && !crashed && started && System.currentTimeMillis() - 10> lastCopter) {
				lastCopter = System.currentTimeMillis();
				updateFighter();
				updateMiddle();
			}
			if (!paused && !crashed && started && System.currentTimeMillis() - 100 > last) {
				last = System.currentTimeMillis();
				updateRecs();
			}
			//controls the movement of the smoke in the fighter ship
			if (!paused && !crashed && started && System.currentTimeMillis() - 75 > lastSmoke) {
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
			//Repaints the JFrame with the new information
			back.updateImages(middlerecs,Fighter, smoke, bar);
		}
	}

	public void updateRecs() {
		for (int x = 0; x < (NUMRECS - 1); x++) // move all but the last
												// rectangle 1 spot to the left
		{
			bottomrecs.set(x, new MovingImage("CeilingFloor.png", RECWIDTH * x, bottomrecs.get(x + 1).getY()));
		}
		lastRec();
	}

	public void lastRec() {
		if (distance % 400 == 0)
			moveIncrement++;
		if (40 < 2) // if too high, move down
			moveDown();
		else if (bottomrecs.get(26).getY() > 463) // else if too low, move up
			moveUp();
		else // else move randomly
		{
			if ((int) (Math.random() * 60) == 50)
				randomDrop();
			else {
				if ((int) (Math.random() * 2) == 1)
					moveUp();
				else
					moveDown();
			}
		}
	}

	public void randomDrop() {
		toprecs.get(26).setY(toprecs.get(26).getY() + (463 - bottomrecs.get(26).getY()));
		bottomrecs.get(26).setY(463);
	}

	public void moveDown() {
//		toprecs.set((NUMRECS - 1),
//				new MovingImage("CeilingFloor.png", RECWIDTH * (NUMRECS - 1), toprecs.get(26).getY() + moveIncrement));
		bottomrecs.set((NUMRECS - 1), new MovingImage("CeilingFloor.png", RECWIDTH * (NUMRECS - 1),
				bottomrecs.get(26).getY() + moveIncrement));
	}

	public void moveUp() {
//		bottomrecs.set((NUMRECS - 1), new MovingImage("CeilingFloor.png", RECWIDTH * (NUMRECS - 1),
//				bottomrecs.get(26).getY() - moveIncrement));
//		toprecs.set((NUMRECS - 1),
//				new MovingImage("CeilingFloor.png", RECWIDTH * (NUMRECS - 1), toprecs.get(26).getY() - moveIncrement));
	}

	public int randomMidHeight() {
		int max = 10000;
		int min = 0;

		for (int x = 0; x < NUMRECS; x++) {
			if (bottomrecs.get(x).getY() < max)
				max = (int) bottomrecs.get(x).getY();
		}
		min += RECHEIGHT;
		max -= (RECHEIGHT + min);
		return min + (int) (Math.random() * max);
	}

	// moves the randomly generated middle rectangles
	public void updateMiddle() {
		if (middlerecs.get(0).getX() > -1 * RECWIDTH) {
			middlerecs.set(0, new MovingImage("CeilingFloor.png", middlerecs.get(0).getX() - (RECWIDTH / 5),
					middlerecs.get(0).getY()));
			middlerecs.set(1, new MovingImage("CeilingFloor.png", middlerecs.get(1).getX() - (RECWIDTH / 5),
					middlerecs.get(1).getY()));
		} else {
			middlerecs.set(0, new MovingImage("CeilingFloor.png", middlerecs.get(1).getX() - (RECWIDTH / 5),
					middlerecs.get(1).getY()));
			middlerecs.set(1, new MovingImage("CeilingFloor.png", middlerecs.get(0).getX() + 580, randomMidHeight()));
		}
	}

	public boolean isInMidRange(int num) {
		Rectangle middlecheck = new Rectangle((int) middlerecs.get(num).getX(), (int) middlerecs.get(num).getY(),
				RECWIDTH, RECHEIGHT);
		Rectangle coptercheck = new Rectangle((int) Fighter.getX(), (int) Fighter.getY(), 106, 48);
		return middlecheck.intersects(coptercheck);
	}

	// moves the Fighter
	public void updateFighter() {
		upCount += .08;
		if (goingUp) {
			if (upCount < 3.5)
				Fighter.setPosition(XPOS, (double) (Fighter.getY() - (.3 + upCount)));
			else
				Fighter.setPosition(XPOS, (double) (Fighter.getY() - (1.2 + upCount)));
			Fighter.setImage("XWingFighter.png");
		} else {
			if (upCount < 1)
				Fighter.setPosition(XPOS, (double) (Fighter.getY() + upCount));
			else
				Fighter.setPosition(XPOS, (double) (Fighter.getY() + (1.2 + upCount)));
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
		for (int x = 3; x <= 7; x++)
			//Determines the lowest the ship can go
			if (Fighter.getY() + 48 >= bottomrecs.get(x).getY())
				return true;
		if (Fighter.getY() <= 70){
			return true;
		}

		for (int z = 0; z <= 1; z++)
			if (isInMidRange(z))
				return true;
		return false;
	}

	// Called when the mouse exits the game window
	public void mouseExited(MouseEvent e) {

		if (started) {
			//paused = true;
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