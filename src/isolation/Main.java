package isolation;

import java.util.Scanner;

public class Main 
{	
	public static void main(String[] args)
	{	
		//Need to add Input Validation Functionality
		Scanner kb = new Scanner(System.in);
		String startingPlayer, moveTimeLimit;
		int timeLimit;
		System.out.println("Welcome to the Isolation Game!");
		
		do
		{
			//Computer or Opponent? ('X' always goes first)
			System.out.print("Please Enter Which Player Goes First ('C' or 'O'): ");
			startingPlayer = kb.nextLine();
		}
		while(!(startingPlayer.equalsIgnoreCase("C") || startingPlayer.equalsIgnoreCase("O")
				&& startingPlayer.length() == 1));
		
		//Converted user input to char to be used by program
		char startingChar = startingPlayer.charAt(0);
		startingChar = Character.toUpperCase(startingChar);
		
		while(true)
		{
			System.out.print("Enter the Time Allowed for a Move: ");
			moveTimeLimit = kb.nextLine();
			
			try {
                timeLimit = Integer.parseInt(moveTimeLimit);
                break;
            }//end try 
			catch (NumberFormatException ne) 
			{
                System.out.println("Input is not a number, please try again.");
            }//end catch
		}//end while
		
		//Initialize Starting Layout of Isolation Game
		@SuppressWarnings("unused")
		IsolationGame game = new IsolationGame(timeLimit, startingChar); 
		
		System.out.println("Thank you for playing the Isolation Game!");
		kb.close();
	}//end main
}//end Main