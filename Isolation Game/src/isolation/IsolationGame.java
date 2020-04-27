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
	private int depthLimit;
	private ArrayList<String> userMoves;
	private ArrayList<String> computerMoves;
	
	public IsolationGame()
	{
		this.kb = new Scanner(System.in);
		this.computer = 'X';//Default computer will be 'X'
		this.player = 'O';
		this.computerTurn = true;
		this.moveTimeLimit = 20;
		this.initialBoard = new Board(8, 'X');//'X' will always go first
		this.depthLimit = 6;
		this.userMoves = new ArrayList<String>(32);
		this.computerMoves = new ArrayList<String>(32);
		play();//Runs the game
	}//end Default Constructor
	
	public IsolationGame(int mTL, char startingPlayer) 
	{
		this.kb = new Scanner(System.in);
		determineTurnOrder(startingPlayer);
		this.moveTimeLimit = mTL;
		this.initialBoard = new Board(8, 'X');//'X' will always go first
		this.depthLimit = 6;
		this.userMoves = new ArrayList<String>(32);
		this.computerMoves = new ArrayList<String>(32);
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
		MinMax search = new MinMax(this.moveTimeLimit);
		Board currentBoard = this.initialBoard;
		Point userMove, computerMove;
		
		System.out.println("\nInitial Board Layout:");
		printBoardWithTurnLog(currentBoard);
		
		//Need to increase depth limit as game progresses (Iterative Deepening)
		while(!gameFinished)
		{	
			//Clear just incase there is stale data
			currentBoard.clearAvailableSpaces();
			//Determine available spaces for the current player before getting move
			currentBoard.setAvailableSpaces(currentBoard.findAvailableSpaces(currentBoard.getCurrentPlayer()));
			
			if(this.computerTurn)//Computer makes a move
			{
				computerMove = search.alphaBeta(currentBoard, this.depthLimit, System.nanoTime());
				
				//For testing User on User
				//computerMove = getUserMove(currentBoard);
				
				if(currentBoard.noMovesRemaining())//Checks if there are no available moves remaining
				{
					System.out.println("Congratulations! You win! The computer is out of moves!");
					gameFinished = true;
				}//end if
				else//The computer still had additional moves available
				{
					this.computerMoves.add(formatPoint(computerMove));
					currentBoard.movePlayer(this.computer, computerMove);
					//Sets currentPlayer value for next turn
					currentBoard.setCurrentPlayer(this.player);
				}//end else
			}//end if
			
			else//User makes a move
			{
				userMove = getUserMove(currentBoard);
				if(userMove == null)//Checks if there are no available moves remaining
				{
					System.out.println("Sorry! You lose! You are out of moves!");
					gameFinished = true;
				}//end if 
				else//Moves were still available
				{
					this.userMoves.add(formatPoint(userMove));
					currentBoard.movePlayer(this.player, userMove);
					//Sets currentPlayer value for next turn
					currentBoard.setCurrentPlayer(this.computer);
				}//end else
			}//end else
			
			//Updates variable for next turn
			this.computerTurn = !this.computerTurn;
			
			//Prints updated board after each turn has been made
			printBoardWithTurnLog(currentBoard);
			System.out.println("\nUser Moves: " + this.userMoves);
			System.out.println("Computer Moves: " + this.computerMoves + "\n");
		}//end while
	}//end play
	
	//Prints an update turn log after computer/user makes a move
	private void printBoardWithTurnLog(Board currentBoard)
	{	
		char[] boardLetters = {'A', 'B', 'C' , 'D', 'E', 'F', 'G', 'H'};
		
		System.out.print("\n  1 2 3 4 5 6 7 8");
		for(int i = 0; i < currentBoard.getBoardDimension(); i++)
		{
			System.out.print("\n" + boardLetters[i] + " ");
			for(int j = 0; j < currentBoard.getBoardDimension(); j++)
			{
				System.out.print(String.valueOf(currentBoard.getBoardLayout()[i][j]) + " ");
			}//end inner for
		}//end outer for
		System.out.println();
	}//end printTurnLog
	
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
			System.out.print("Enter opponent's move: ");
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
}//end IterativeDeepening