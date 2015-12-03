package GUI;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;

public class GameGUI implements MouseListener
{
	private JFrame background;
    private Container container;
    private JButton button;
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
    private MovingImage helicopter;

    //private MP3 move = new MP3("HelicopterSound.mp3");

    /*Graphics information:
     *Background is 812 x 537
     *Floor is 74 and Ceiling is 72 pixels high
     *28 rectangles across that are 29 x 73
     */

    public GameGUI()
    {
        NUMRECS = 28;
        RECHEIGHT = 81;
        RECWIDTH = 271;
        XPOS = 200;
        playedOnce = false;
        maxDistance = 0;

        load(new File("Best.txt"));

        initiate();
    }

    public void load(File file)
    {
        try
        {
            Scanner reader = new Scanner(file);
            while(reader.hasNext())
            {
                int value = reader.nextInt();
                if(value > maxDistance)
                    maxDistance = value;
            }
        }
        catch(IOException i )
        {
            System.out.println("Error. "+i);
        }
    }

    public void save()
    {
        FileWriter out;
        try
        {
            out = new FileWriter("Best.txt");
            out.write("" + maxDistance);
            out.close();
        }
        catch(IOException i)
        {
            System.out.println("Error: "+i.getMessage());
        }
    }

    public void initiate()
    {
        if(!playedOnce)
        {
            background = new JFrame("Helicopter Game"); 
            background.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //closes the program when the window is closed
            background.setResizable(false); //don't allow the user to resize the window
            background.setSize(new Dimension(1500,1500));
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

        recs = new ArrayList<MovingImage>();
        toprecs = new ArrayList<MovingImage>();
        middlerecs = new ArrayList<MovingImage>();
        bottomrecs = new ArrayList<MovingImage>();
        smoke = new ArrayList<MovingImage>();

        helicopter = new MovingImage("XWingFighter.jpg",XPOS,270);

        for(int x = 0; x < NUMRECS; x++)
            toprecs.add(new MovingImage("CeilingFloor.png",RECWIDTH*x,30));
        for(int x = 0; x < NUMRECS; x++)
            bottomrecs.add(new MovingImage("CeilingFloor.png",RECWIDTH*x,450));

        middlerecs.add(new MovingImage("Obstacle.jpg",1392,randomMidHeight()));
        middlerecs.add(new MovingImage("Obstacle.jpg",1972,randomMidHeight()));

        drawRectangles();
    }

    public void drawRectangles()
    {
        long last = System.currentTimeMillis();
        long lastCopter = System.currentTimeMillis();
        long lastSmoke = System.currentTimeMillis();
        long lastSound = System.currentTimeMillis();
        int firstUpdates = 0;
        double lastDistance = (double)System.currentTimeMillis();
        while(true)
        {
            if(!paused && !crashed && started && (double)System.currentTimeMillis() - (double)(2900/40) > lastDistance)
            {    
                lastDistance = System.currentTimeMillis();
                distance++;
            }    

        /*    if(!paused && !crashed && started && System.currentTimeMillis() - 1300 > lastSound)
            {
                lastSound = System.currentTimeMillis();
                move.play();
            }
        */

            if(!paused && !crashed && started && System.currentTimeMillis() - 10 > lastCopter)
            {
                lastCopter = System.currentTimeMillis();
                updateCopter();
                updateMiddle();
            }
            if(!paused && !crashed && started && System.currentTimeMillis() - 100 > last)
            {
                last = System.currentTimeMillis();
                updateRecs();
            }
            if(!paused && !crashed && started && System.currentTimeMillis() - 75 > lastSmoke)
            {
                lastSmoke = System.currentTimeMillis();
                if (firstUpdates < numSmoke)
                {
                    firstUpdates++;
                    smoke.add(new MovingImage("smoke.GIF",187,helicopter.getY()));
                    for(int x = 0; x < firstUpdates; x++)
                        smoke.set(x,new MovingImage("smoke.GIF",smoke.get(x).getX() - 12, smoke.get(x).getY()));
                }
                else
                {
                    for(int x = 0; x < numSmoke - 1; x++)
                        smoke.get(x).setY(smoke.get(x+1).getY());
                    smoke.set(numSmoke - 1,new MovingImage("smoke.GIF",187,helicopter.getY()));
                }
                    }
                    back.updateImages(toprecs,middlerecs,bottomrecs,helicopter,smoke);
                }
    }

    public void updateRecs()
    {
        for(int x = 0; x < (NUMRECS - 1); x++) //move all but the last rectangle 1 spot to the left
        {
            toprecs.set(x,new MovingImage("Obstacle.jpg",RECWIDTH*x,toprecs.get(x+1).getY()));
            bottomrecs.set(x,new MovingImage("Obstacle.jpg",RECWIDTH*x,bottomrecs.get(x+1).getY()));
        }
        lastRec();
    }

    public void lastRec()
    {
        if(distance % 400 == 0)
            moveIncrement++;
        if(toprecs.get(26).getY() < 2) //if too high, move down
            moveDown();
        else if (bottomrecs.get(26).getY() > 463) //else if too low, move up
            moveUp();
        else //else move randomly
        {
            if((int)(Math.random() * 60) == 50)
                randomDrop();
            else
            {
                if((int)(Math.random() * 2) == 1)
                    moveUp();
                else
                    moveDown();
            }
        }
    }

    public void randomDrop()
    {
        toprecs.get(26).setY(toprecs.get(26).getY() + (463 - bottomrecs.get(26).getY()));
        bottomrecs.get(26).setY(463);
    }

    public void moveDown()
    {
        toprecs.set((NUMRECS - 1),new MovingImage("Obstacle.jpg",RECWIDTH*(NUMRECS - 1),toprecs.get(26).getY() + moveIncrement));
        bottomrecs.set((NUMRECS - 1),new MovingImage("Obstacle.jpg",RECWIDTH*(NUMRECS - 1),bottomrecs.get(26).getY() + moveIncrement));
    }

    public void moveUp()
    {
        bottomrecs.set((NUMRECS - 1),new MovingImage("Obstacle.jpg",RECWIDTH*(NUMRECS - 1),bottomrecs.get(26).getY() - moveIncrement));
        toprecs.set((NUMRECS - 1),new MovingImage("Obstacle.jpg",RECWIDTH*(NUMRECS - 1),toprecs.get(26).getY() - moveIncrement));
    }

    public int randomMidHeight()
    {
        int max = 10000;
        int min = 0;

        for(int x = 0; x < NUMRECS; x++)
        {
            if(toprecs.get(x).getY() > min)
                min = (int)toprecs.get(x).getY();
            if(bottomrecs.get(x).getY() < max)
                max = (int)bottomrecs.get(x).getY();
        }
        min += RECHEIGHT;
        max -= (RECHEIGHT + min);
        return min + (int)(Math.random() * max);
    }

    //moves the randomly generated middle rectangles
    public void updateMiddle()
    {
        if(middlerecs.get(0).getX() > -1 * RECWIDTH)
        {
            middlerecs.set(0,new MovingImage("Obstacle.jpg",middlerecs.get(0).getX() - (RECWIDTH/5), middlerecs.get(0).getY()));
            middlerecs.set(1,new MovingImage("Obstacle.jpg",middlerecs.get(1).getX() - (RECWIDTH/5), middlerecs.get(1).getY()));
        }
        else
        {
            middlerecs.set(0,new MovingImage("Obstacle.jpg",middlerecs.get(1).getX() - (RECWIDTH/5), middlerecs.get(1).getY()));
            middlerecs.set(1,new MovingImage("Obstacle.jpg",middlerecs.get(0).getX() + 580,randomMidHeight()));
        }
    }

    public boolean isHit()
    {
        for(int x = 3; x <= 7; x++)
            if(helicopter.getY() + 48 >= bottomrecs.get(x).getY())
                return true;

        for(int y = 3; y <= 7; y++)
                if(helicopter.getY() <= toprecs.get(y).getY() + RECHEIGHT)
                    return true;
        for(int z = 0; z <= 1; z++)
            if(isInMidRange(z))
                return true;
        return false;
    }

    public boolean isInMidRange(int num)
    {
        Rectangle middlecheck = new Rectangle((int)middlerecs.get(num).getX(),(int)middlerecs.get(num).getY(),RECWIDTH,RECHEIGHT);
        Rectangle coptercheck = new Rectangle((int)helicopter.getX(),(int)helicopter.getY(),106,48);
        return middlecheck.intersects(coptercheck);
    }

    public void crash()
    {
        crashed = true;
        if(distance > maxDistance) 
        {
            maxDistance = distance;
            save();
        }

        initiate();
    }

    //moves the helicopter
    public void updateCopter()
    {
        upCount += .08;
        if(goingUp)
        {
            if(upCount < 3.5)
                helicopter.setPosition(XPOS,(double)(helicopter.getY() - (.3 + upCount)));
            else
                helicopter.setPosition(XPOS,(double)(helicopter.getY() - (1.2 + upCount)));
            helicopter.setImage("upCopter.GIF");    
        }
        else
        {
            if(upCount < 1)
                helicopter.setPosition(XPOS,(double)(helicopter.getY() + upCount));
            else
                helicopter.setPosition(XPOS,(double)(helicopter.getY() + (1.2 + upCount)));
            helicopter.setImage("XWingFighter.jpg");
        }
        if(isHit())
            crash();
    }

    //Called when the mouse exits the game window
    public void mouseExited(MouseEvent e)
    {

        if(started)
        {
            paused = true;
            //move.close();    
        }

    }

    //Called when the mouse enters the game window
    public void mouseEntered(MouseEvent e)
    {

    }

    //Called when the mouse is released
    public void mouseReleased(MouseEvent e)
    {
        goingUp = false;
        upCount = 0;
        if(paused)
            paused = false;
    }

    //Called when the mouse is pressed
    public void mousePressed(MouseEvent e)
    {
        if (!started)
            started = true;
        goingUp = true;
        upCount = 0;
    }

    //Called when the mouse is released
    public void mouseClicked(MouseEvent e)
    {

    }
}
