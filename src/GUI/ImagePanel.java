package GUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import GUI.MovingImage;

class ImagePanel extends JPanel {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Image background; // The background image
    private ArrayList<MovingImage> bottom;
    private ArrayList<MovingImage> middle;
    private MovingImage fighter;
    private ArrayList<MovingImage> smoke;
    private ArrayList<MovingImage> bar;
    public String theRank = "";
    
    // Constructs a new ImagePanel with the background image specified by the
    // file path given
    public ImagePanel(String img) {
        this(new ImageIcon(img).getImage());
        // The easiest way to make images from file paths in Swing
    }
    
    // Constructs a new ImagePanel with the background image given
    public ImagePanel(Image img) {
        background = img;
        Dimension size = new Dimension(img.getWidth(null), img.getHeight(null));
        // Get the size of the image
        // Thoroughly make the size of the panel equal to the size of the image
        // (Various layout managers will try to mess with the size of things to
        // fit everything)
        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);
        setSize(size);
        
        middle = new ArrayList<MovingImage>();
        bottom = new ArrayList<MovingImage>();
        bar = new ArrayList<MovingImage>();
        
        smoke = new ArrayList<MovingImage>();
    }
    
    // This is called whenever the computer decides to repaint the window
    // It's a method in JPanel that I've overwritten to paint the background and
    // foreground images
    public void paintComponent(Graphics g) {
        // Paint the background with its upper left corner at the upper left
        // corner of the panel
        g.drawImage(background, 0, 0, null);
        // Paint each image in the foreground where it should go
        for (MovingImage img : middle)
            g.drawImage(img.getImage(), (int) (img.getX()), (int) (img.getY()), null);
        for (MovingImage img : bottom)
            g.drawImage(img.getImage(), (int) (img.getX()), (int) (img.getY()), null);
        for (MovingImage img : smoke)
            g.drawImage(img.getImage(), (int) (img.getX()), (int) (img.getY()), null);
        for (MovingImage img : bar)
            g.drawImage(img.getImage(), (int) (img.getX()), (int) (img.getY()), null);
        if (fighter != null)
            g.drawImage(fighter.getImage(), (int) (fighter.getX()), (int) (fighter.getY()), null);
        drawStrings(g);
    }
    
    //Draw string method is used to draw the distance(score) and the maximum distance(record) of
    //the user. In addition, this method is useful to display that the Game is on pause mode.
    public void drawStrings(Graphics g) {
        
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.setColor(Color.WHITE);
        g.drawString("Distance: " + GameGUI.distance, 30, 540);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.setColor(Color.WHITE);
        if (GameGUI.distance > GameGUI.maxDistance){
            g.drawString("Best: " + GameGUI.distance, 850, 520);
            g.drawString("Rank: " + theRank, 850, 550);
        }
        else{
            g.drawString("Best: " + GameGUI.maxDistance, 850, 520);
            g.drawString("Rank: " + GameGUI.maxRank, 850, 550);
        }
        if (GameGUI.paused) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Chiller", Font.BOLD, 72));
            g.drawString("Paused", 325, 290);
            g.setFont(new Font("Chiller", Font.BOLD, 30));
            g.drawString("Click to unpause.", 320, 340);
        }
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.setColor(Color.WHITE);
        if (GameGUI.distance < 200){
            theRank = "Youngling";
            g.drawString("Rank: " + theRank, 400, 540);
        }
        else if (GameGUI.distance >= 200 && GameGUI.distance < 1000){
            theRank = "Padawan";
            g.drawString("Rank: " + theRank, 400, 540);
        }
        else if (GameGUI.distance >= 1000 && GameGUI.distance < 3000){
            theRank = "Jedi Knight";
            g.drawString("Rank: " + theRank, 400, 540);
        }
        else {
            theRank = "Jedi Master";
            g.drawString("Rank: " + theRank, 400, 540);
        }
    }
    
    // Replaces the list of foreground images with the one given, and repaints
    // the panel
    public void updateImages(ArrayList<MovingImage> newMiddle,MovingImage newFighter, ArrayList<MovingImage> newSmoke, ArrayList<MovingImage> newBar) {
        
        fighter = newFighter;
        middle = newMiddle;
        smoke = newSmoke;
        bar = newBar;
        repaint(); // Repaint the GUI
    }
}