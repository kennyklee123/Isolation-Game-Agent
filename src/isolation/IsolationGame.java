package isolation;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IsolationGame 
{	
	private Scanner kb;
	private char computer;
	private char player;
	private boolean computerTurn;
	private long moveTimeLimit;
	private Board initialBoard;
	private ArrayList<String> userMoves;
	private ArrayList<String> computerMoves;
	private ArrayList<String> totalMoves;
	
	public IsolationGame()
	{
		this.kb = new Scanner(System.in);
		this.computer = 'X';//Default computer will be 'X'
		this.player = 'O';
		this.computerTurn = true;
		this.moveTimeLimit = 20;
		this.initialBoard = new Board(8, 'X');//'X' will always go first
		this.userMoves = new ArrayList<String>(32);
		this.computerMoves = new ArrayList<String>(32);
		this.totalMoves = new ArrayList<String>(32);
		play();//Runs the game
	}//end Default Constructor
	
	public IsolationGame(int mTL, char startingPlayer) 
	{
		this.kb = new Scanner(System.in);
		determineTurnOrder(startingPlayer);
		this.moveTimeLimit = mTL;
		this.initialBoard = new Board(8, 'X');//'X' will always go first
		this.userMoves = new ArrayList<String>(32);
		this.computerMoves = new ArrayList<String>(32);
		this.totalMoves = new ArrayList<String>(32);
		play();//Runs the game
	}//end Constructor
	
	private void determineTurnOrder(char startingPlayer)
	{
		this.computer = startingPlayer == 'C' ? 'X' : 'O';
		this.player = this.computer == 'X' ? 'O' : 'X';		
		this.computerTurn = this.computer == 'X' ? true : false;
	}//end determineTurnOrder

	//Runs Isolation Game with an Iterative Deepening Search Strategy
	private void play()
	{	
		boolean gameFinished = false;
		Adversarial search = new Adversarial(this.moveTimeLimit, this.computer, this.player);
		Board currentBoard = this.initialBoard;
		Point userMove, computerMove;
		int turnCount = 0;
		
		System.out.println("\nInitial Board Layout:");
		printBoardWithTurnLog(currentBoard, turnCount);
		
		//Need to increase depth limit as game progresses (Iterative Deepening)
		while(!gameFinished)
		{	
			if(this.computerTurn)//Computer makes a move
			{
				currentBoard.setAvailableSpaces(currentBoard.findAvailableSpaces(this.computer));					
				if(currentBoard.noMovesRemaining())//Checks if there are no available moves remaining
				{
					System.out.println("Congratulations! You win! The computer is out of moves!");
					gameFinished = true;
				}//end if
				else//The computer still had additional moves available
				{
					computerMove = search.iterativeDeepening(currentBoard, turnCount); 
					this.computerMoves.add(formatPoint(computerMove));
					this.totalMoves.add(formatPoint(computerMove));
					currentBoard.movePlayer(this.computer, computerMove);
				}//end else
			}//end if
			
			else//User makes a move
			{
				currentBoard.setAvailableSpaces(currentBoard.findAvailableSpaces(this.player));
				userMove = getUserMove(currentBoard);
				if(userMove == null)//Checks if there are no available moves remaining
				{
					System.out.println("Sorry! You lose! You are out of moves!");
					gameFinished = true;
				}//end if 
				else//Moves were still available
				{
					this.userMoves.add(formatPoint(userMove));
					this.totalMoves.add(formatPoint(userMove));
					currentBoard.movePlayer(this.player, userMove);
				}//end else
			}//end else
			
			//Updates variable for next turn
			this.computerTurn = !this.computerTurn;
			turnCount++;
			//Prints updated board after each turn has been made
			printBoardWithTurnLog(currentBoard, turnCount);
			if(!this.computerTurn && this.computerMoves.size() > 0)
				System.out.println("\nComputer's Move: " + this.computerMoves.get(this.computerMoves.size() - 1));
		}//end while
	}//end play
	
	private Point getUserMove(Board currentBoard)
	{
		if(currentBoard.noMovesRemaining())
		{
			return null;
		}//end if
		
		//Used to validate user input for move
		String pattern = "[A-H][1-8]";
		Pattern r = Pattern.compile(pattern);
		
		Point userInput = new Point(-1, -1);
		String input = "";
		boolean valid = false;
		
		while(!valid)
		{
			System.out.print("\nEnter opponent's move: ");
			input = kb.nextLine();
			input = input.toUpperCase();
			//Used to match user input to expected format (via regular expression)
			Matcher m = r.matcher(input);
			
			if(input.length() != 2)
			{
				System.out.println("Input Must Be 2 Characters!");
			}//end if
			
			if(m.find() && input.length() == 2)
			{
				userInput = convertUserInput(input);
				if(currentBoard.getAvailableSpaces().contains(userInput))
				{
					valid = true;
				}//end if
				else
				{
					System.out.println("That space is not available as a valid move!");
				}//end else
			}//end if
			else
			{
				System.out.println("Incorrect Format. Format Examples: 'A1', 'D5', 'H7'");
			}
		}//end while
		return userInput;
	}//end getUserMove
	
	private void printBoardWithTurnLog(Board currentBoard, int turnCount)
	{	
		char[] boardLetters = {'A', 'B', 'C' , 'D', 'E', 'F', 'G', 'H'};
		System.out.print("\n  1 2 3 4 5 6 7 8");
		System.out.print(padRight("", 5) + "Computer vs. Opponent");
		if(turnCount <= 16) {
			for(int i = 0; i < currentBoard.getBoardDimension(); i++) {
				System.out.print("\n" + boardLetters[i] + " ");
				for(int j = 0; j < currentBoard.getBoardDimension(); j++) {
					System.out.print(String.valueOf(currentBoard.getBoardLayout()[i][j]) + " ");
				}//end inner for
				
				//If Computer Goes First
				if(i < computerMoves.size() && !(computerMoves.get(i).equals(""))) {
					if (i < computerMoves.size() && i < userMoves.size() && !(computerMoves.get(i).equals("")) && !(userMoves.get(i).equals("")))
						System.out.print(padRight("", 4) + (i+1) + ". " + computerMoves.get(i) + padRight("", 8) + userMoves.get(i));
					else
						System.out.print(padRight("", 4) + (i+1) + ". " + computerMoves.get(i));
				}//end if
				//If opponent goes first
				else {
					if(i < userMoves.size() && !(userMoves.get(i).equals(""))) {
						if (i < userMoves.size() && i < computerMoves.size() && !(userMoves.get(i).equals("")) && !(computerMoves.get(i).equals("")))
							System.out.print(padRight("", 4) +(i+1) + ". " + computerMoves.get(i) + padRight("", 8) + userMoves.get(i));
						else
							System.out.print((padRight("", 4) +(i+1) + ". " + padRight("", 10) + userMoves.get(i)));
					}
				}//end else
			}//end outer for
			System.out.println();
		}//end if(turnCount < 16)
		//Prints Moves beyond Row H
		else {//After 16 turns
			for(int i = 0; i < totalMoves.size(); i++) {
				if(i < 8) {
					System.out.print("\n" + boardLetters[i] + " ");
					for(int j = 0; j < currentBoard.getBoardDimension(); j++) {
						System.out.print(String.valueOf(currentBoard.getBoardLayout()[i][j]) + " ");
					}//end inner for
					//If Computer Goes First
					if(i < computerMoves.size() && !(computerMoves.get(i).equals(""))) {
						if (i < computerMoves.size() && i < userMoves.size() && !(computerMoves.get(i).equals("")) && !(userMoves.get(i).equals("")))
							System.out.print(padRight("", 4) + (i+1) + ". " + computerMoves.get(i) + padRight("", 8) + userMoves.get(i));
						else
							System.out.print(padRight("", 4) + (i+1) + ". " + computerMoves.get(i));
					}//end if
					//If opponent goes first
					else {
						if(i < userMoves.size() && !(userMoves.get(i).equals(""))) {
							if (i < userMoves.size() && i < computerMoves.size() && !(userMoves.get(i).equals("")) && !(computerMoves.get(i).equals("")))
								System.out.print(padRight("", 4) +(i+1) + ". " + computerMoves.get(i) + padRight("", 8) + userMoves.get(i));
							else
								System.out.print((padRight("", 4) +(i+1) + ". " + padRight("", 10) + userMoves.get(i)));
						}
					}//end else
				}//end if
				else {
					//If Computer Goes First
					if(i < computerMoves.size() && !(computerMoves.get(i).equals(""))) {
						if (i < computerMoves.size() && i < userMoves.size() && !(computerMoves.get(i).equals("")) && !(userMoves.get(i).equals("")))
							System.out.print("\n" + padRight("", 22) + (i+1) + ". " + computerMoves.get(i) + padRight("", 8) + userMoves.get(i));
						else
							System.out.print("\n" + padRight("", 22) + (i+1) + ". " + computerMoves.get(i));
					}//end if
					//If opponent goes first
					else {
						if(i < userMoves.size() && !(userMoves.get(i).equals(""))) {
							if (i < userMoves.size() && i < computerMoves.size() && !(userMoves.get(i).equals("")) && !(computerMoves.get(i).equals("")))
								System.out.print("\n" + padRight("", 22) +(i+1) + ". " + computerMoves.get(i) + padRight("", 8) + userMoves.get(i));
							else 
								System.out.print("\n" + (padRight("", 22) +(i+1) + ". " + padRight("", 10) + userMoves.get(i)));
						}//end if
					}//end else
				}//end else		
			}//end outer for
			System.out.println();
		}//end else
	}//end printBoardWithTurnLog
	
	public static String padRight(String s, int n) 
	{
	     return String.format("%-" + n + "s", s);  
	}//end padRight

	public static String padLeft(String s, int n) 
	{
	    return String.format("%" + n + "s", s);  
	}//end padLeft
	
	private Point convertUserInput(String input)
	{
		char letter, col;
		int x = -1, y = -1;
		char[] boardLetters = {'A', 'B', 'C' , 'D', 'E', 'F', 'G', 'H'};
		
		input = input.toUpperCase();
		letter = input.charAt(0);
		col = input.charAt(1);
		
		for(int i = 0; i < boardLetters.length; i++)
		{
			if(letter == boardLetters[i])
			{
				x = i;
				break;
			}//end if
		}//end for loop
		y = Integer.parseInt(String.valueOf(col)) - 1;
		Point userInput = new Point(x, y);
		return userInput;
	}//end convertUserInput
	
	private String formatPoint(Point move)
	{
		String formattedPoint = "";
		char[] boardLetters = {'A', 'B', 'C' , 'D', 'E', 'F', 'G', 'H'};
		
		for(int i = 0; i < boardLetters.length; i++)
		{
			if(move.getX() == i)
			{
				formattedPoint += boardLetters[i];
				break;
			}//end if
		}//end for loop
		formattedPoint += (int)move.getY() + 1;
		
		return formattedPoint;
	}//end formatPoint
}//end IsolationGame
