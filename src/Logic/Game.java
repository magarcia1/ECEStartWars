package Logic;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import GUI.GameGUI;

/**
 * 
 */

/**
 * @author phillipshowers
 *
 */
public class Game 
{
	//Holds the highest distance scored by the player
	public static int maxDistance;
	
	public Game()
	{
		load(new File("Best.txt"));
		
		GameGUI newGUI = new GameGUI();
		
	}

	public void load(File file)
	{
		try
        {
            Scanner reader = new Scanner(file);
            //this will store value as the newest maxDistance if value is > maxDistance.
            //Stores the new high score value
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
}
