package main;

//import Search.AlphaBetaAI;
import Search.AlphaBetaAI;
import app.StartDesktopApp;

import utils.AIRegistry;

/**
 * The main method of this launches the Ludii application with its GUI, and registers
 * the example AIs from this project such that they are available inside the GUI.
 *
 * @author Dennis Soemers
 */
public class LaunchLudii
{
	
	/**
	 * The main method
	 * @param args
	 */
	public static void main(final String[] args)
	{

		if (!AIRegistry.registerAI("Maria Alpha-BetaAI", () -> {return new AlphaBetaAI();}, (game) -> {return true;}))
			System.err.println("WARNING! Failed to register AI because one with that name already existed!");

//		// Run Ludii
		StartDesktopApp.main(new String[0]);
	}

}
