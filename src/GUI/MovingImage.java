package GUI;
import java.awt.Image;
import javax.swing.ImageIcon;

/**
 * 
 */

/**
 * @author phillipshowers
 *
 */
class MovingImage
{
    private Image image;        //The picture
    private double x;            //X position
    private double y;            //Y position

    //Construct a new Moving Image with image, x position, and y position given
    public MovingImage(Image img, double xPos, double yPos)
    {
        image = img;
        x = xPos;
        y = yPos;
    }

    //Construct a new Moving Image with image (from file path), x position, and y position given
    public MovingImage(String path, double xPos, double yPos)
    {
        this(new ImageIcon(path).getImage(), xPos, yPos);    
            //easiest way to make an image from a file path in Swing
    }

    //They are set methods.  I don't feel like commenting them.
    public void setPosition(double xPos, double yPos)
    {
        x = xPos;
        y = yPos;
    }

    public void setImage(String path)
    {
        image = new ImageIcon(path).getImage();
    }

    public void setY(double newY)
    {
        y = newY;
    }

    public void setX(double newX)
    {
        x = newX;
    }

    //Get methods which I'm also not commenting
    public double getX()
    {
        return x;
    }

    public double getY()
    {
        return y;
    }

    public Image getImage()
    {
        return image;
    }
}
/*
class MP3 {
    private String filename;
    private Player player; 

    // constructor that takes the name of an MP3 file
    public MP3(String filename) {
        this.filename = filename;
    }

    public void close() { if (player != null) player.close(); }

    // play the MP3 file to the sound card
    public void play() {
        try {
            FileInputStream fis     = new FileInputStream(filename);
            BufferedInputStream bis = new BufferedInputStream(fis);
            player = new Player(bis);
        }
        catch (Exception e) {
            System.out.println("Problem playing file " + filename);
            System.out.println(e);
        }

        // run in new thread to play in background
        new Thread() {
            public void run() {
                try { player.play(); }
                catch (Exception e) { System.out.println(e); }
            }
        }.start();
    }

}
*/