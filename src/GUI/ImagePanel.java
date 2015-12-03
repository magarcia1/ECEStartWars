package GUI;
//import javax.swing.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import Logic.Game;
import javax.swing.JComponent;

import javax.swing.ImageIcon;

/**
 * 
 */

/**
 * @author phillipshowers
 *
 */
public class ImagePanel 
{
	private Image background;                    //The background image
    private ArrayList<MovingImage> top;    //An array list of foreground images
    private ArrayList<MovingImage> bottom;
    private ArrayList<MovingImage> middle;
  	private MovingImage copter;
  	private ArrayList<MovingImage> smoke;
  	

  //Constructs a new ImagePanel with the background image specified by the file path given
    public ImagePanel(String img) 
    {
        this(new ImageIcon(img).getImage());    
            //The easiest way to make images from file paths in Swing
    }

  //Constructs a new ImagePanel with the background image given
    public ImagePanel(Image img)
    {
      background = img;
      Dimension size = new Dimension(img.getWidth(null), img.getHeight(null));    
          //Get the size of the image
      //Thoroughly make the size of the panel equal to the size of the image
      //(Various layout managers will try to mess with the size of things to fit everything)
//      setPreferredSize(size);
      setMinimumSize(size);
//      setMaximumSize(size);
//      setSize(size);
      setPreferredSize(size);

      top = new ArrayList<MovingImage>();
      middle = new ArrayList<MovingImage>();
      bottom = new ArrayList<MovingImage>();

      smoke = new ArrayList<MovingImage>();
    }


	//This is called whenever the computer decides to repaint the window
  //It's a method in JPanel that I've overwritten to paint the background and foreground images
    public void paintComponent(Graphics g) 
    {
        //Paint the background with its upper left corner at the upper left corner of the panel
      g.drawImage(background, 0, 0, null); 
      //Paint each image in the foreground where it should go
      for(MovingImage img : top)
          g.drawImage(img.getImage(), (int)(img.getX()), (int)(img.getY()), null);
      for(MovingImage img : middle)
          g.drawImage(img.getImage(), (int)(img.getX()), (int)(img.getY()), null);
      for(MovingImage img : bottom)
          g.drawImage(img.getImage(), (int)(img.getX()), (int)(img.getY()), null);
      for(MovingImage img : smoke)
          g.drawImage(img.getImage(), (int)(img.getX()), (int)(img.getY()), null);
      if(copter != null)
          g.drawImage(copter.getImage(), (int)(copter.getX()), (int)(copter.getY()), null);
      drawStrings(g);
    }

   public void drawStrings(Graphics g)
   {
       g.setFont(new Font("Arial",Font.BOLD,20));
      g.drawString("Distance: " + GameGUI.getDistance(),30,500);
      g.setFont(new Font("Arial",Font.BOLD,20));
      if (GameGUI.distance > GameGUI.maxDistance)
          g.drawString("Best: " + GameGUI.distance,650,500);
      else
          g.drawString("Best: " + GameGUI.maxDistance,650,500);
      if(GameGUI.paused)
      {
              g.setColor(Color.WHITE);
              g.setFont(new Font("Chiller",Font.BOLD,72));
              g.drawString("Paused",325,290);
              g.setFont(new Font("Chiller",Font.BOLD,30));
              g.drawString("Click to unpause.",320,340);
      }
   }

    //Replaces the list of foreground images with the one given, and repaints the panel
    public void updateImages(ArrayList<MovingImage> newTop,ArrayList<MovingImage> newMiddle,ArrayList<MovingImage> newBottom,MovingImage newCopter,ArrayList<MovingImage> newSmoke)
    {
        top = newTop;
        copter = newCopter;
        middle = newMiddle;
        bottom = newBottom;
        smoke = newSmoke;
        repaint();    //This repaints stuff... you don't need to know how it works
    }

	public void addMouseListener(GameGUI gameGUI) 
	{
		// TODO Auto-generated method stub
		
	}


}
