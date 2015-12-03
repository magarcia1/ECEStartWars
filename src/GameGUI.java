import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JFrame;

/**
 * 
 */

/**
 * @author phillipshowers
 *
 */
public class GameGUI 
{
	public final int XPOS = 200;
	public final int NUM_OBSTACLES = 28;
    public final int OBSTACLE_HEIGHT = 73;
    public final int OBSTACLE_WIDTH = 29;


    JFrame background; 
    ImagePanel back;
	boolean playedOnce;
    boolean goingUp;
    static boolean paused;
    boolean crashed;
    boolean started;

    static int distance;
    static int maxDistance;

    int upCount;

    int moveIncrement = 2;
    int numSmoke = 15;

    private ArrayList<MovingImage> topObstacles;
    private ArrayList<MovingImage> bottomObstacles;
    private ArrayList<MovingImage> middleObstacles;
    private ArrayList<MovingImage> Obstacles;
    private ArrayList<MovingImage> smoke;
    private MovingImage helicopter;


	
	public GameGUI()
	{
		playedOnce = false;
		initiate();
	}
	
	public void initiate()
    {
        if(!playedOnce)
        {
            background = new JFrame("Helicopter Game"); 
            background.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //closes the program when the window is closed
            background.setResizable(false); //don't allow the user to resize the window
            background.setSize(new Dimension(818,568));
            background.setVisible(true);

            back = new ImagePanel("back.JPG");
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

        Obstacles = new ArrayList<MovingImage>();
        topObstacles = new ArrayList<MovingImage>();
        middleObstacles = new ArrayList<>();
        bottomObstacles = new ArrayList<MovingImage>();
        smoke = new ArrayList<MovingImage>();

        helicopter = new MovingImage("helicopter.GIF",XPOS,270);

        for(int i = 0; i < NUM_OBSTACLES; i++)
            topObstacles.add(new MovingImage("Obstacle1.png",OBSTACLE_WIDTH*i,30));
        for(int i = 0; i < NUM_OBSTACLES; i++)
            bottomObstacles.add(new MovingImage("rec2.JPG",OBSTACLE_WIDTH*i,450));

        middleObstacles.add(new MovingImage("rec2.JPG",1392,randomMidHeight()));
        middleObstacles.add(new MovingImage("rec2.JPG",1972,randomMidHeight()));

//        drawRectangles();
    }

	public int randomMidHeight()
    {
        int max = 10000;
        int min = 0;

        for(int x = 0; x < NUM_OBSTACLES; x++)
        {
            if(topObstacles.get(x).getY() > min)
                min = (int)topObstacles.get(x).getY();
            if(bottomObstacles.get(x).getY() < max)
                max = (int)bottomObstacles.get(x).getY();
        }
        min += OBSTACLE_HEIGHT;
        max -= (OBSTACLE_HEIGHT + min);
        return min + (int)(Math.random() * max);
    }

	public static int getDistance()
	{
		return distance;
	}


}
