package isolation;

import java.awt.Point;
import java.util.ArrayList;

//MinMax algorithm with Alpha-Beta Pruning
public class Adversarial 
{
	private long moveTimeLimit;
	private char computer;
	private char player;
	private long startTime;
	public Adversarial()
	{
		this.moveTimeLimit = 20;//Seconds
		this.computer = 'X';
		this.player = 'O';
	}//end Default Constructor
	
	public Adversarial(long mTL, char computerSymbol, char playerSymbol)
	{
		this.moveTimeLimit = mTL;
		this.computer = computerSymbol;
		this.player = playerSymbol;
	}//end Constructor
	
	public Point iterativeDeepening(Board layout)
	{
		this.startTime = System.nanoTime() / (long)1e9;
		Point move = new Point(-1,-1);
		int depthLimit = 6;
		int round = 0;
		int i = 1;
		try {
			while(timeRemaining())
			{
				//System.out.println("Call: " + i);
				move = alphaBeta(layout, depthLimit);	
				//System.out.println("---------------------------------------------");
				//i++;
			}//end while
		}
		catch(NoTimeRemainingException e) {}		
		return move;
	}//end IterativeDeepening
	
	private boolean timeRemaining()
	{ 
		long currentTime = System.nanoTime() / (long)1e9;
		if(currentTime - this.startTime < this.moveTimeLimit)
			return true;		
		return false;
	}//end timeRemaining
	
	//Pass in a depth search limit and the starting board layout of the turn
	public Point alphaBeta(Board layout, int depthLimit) throws NoTimeRemainingException
	{
		int bestValue = Integer.MIN_VALUE;
		int alpha = Integer.MIN_VALUE;
		int beta = Integer.MAX_VALUE;
		Point bestMove = new Point(-1,-1);
		//Point currentPoint = new Point(layout.findPosition(this.computer));
		for(int i = 0; i < layout.getAvailableSpaces().size(); i++)
		{
			Point move = layout.getAvailableSpaces().get(i);//gets the next move
			Board successor = new Board(layout, move, this.computer);//moves the computer to that point	
			//layout.movePlayer(this.computer, move);
			alpha = Math.max(alpha, minValue(successor, alpha, beta, depthLimit-1));	
			//layout.resetPlayer(this.computer, currentPoint);
			if(alpha > bestValue)
			{
				bestValue = alpha;
				bestMove = move;
				//System.out.println(i + " "  + move + " value: " + bestValue);
			}
		}	
		return bestMove;
	}//end alphaBeta
	
	private int maxValue(Board layout, int alpha, int beta, int depthLimit) throws NoTimeRemainingException
	{	
		//Time Limit Check 
		if(!timeRemaining()) throw new NoTimeRemainingException("");
		//Depth Limit Check
		if(depthLimit == 0) return layout.evaluateBoard(this.computer);//utility value of terminal state
		layout.setAvailableSpaces(layout.findAvailableSpaces(this.computer));
		// Game Over Check
		if(layout.getAvailableSpaces().size() == 0)return layout.evaluateBoard(this.computer);
		
		//Point currentPoint = new Point(layout.findPosition(this.computer));
		
		int value = Integer.MIN_VALUE;//Initialize to Negative Infinity	
		for(int i = 0; i  < layout.getAvailableSpaces().size(); i++)
		{	
			Point move = layout.getAvailableSpaces().get(i);// returns a point the computer can move too			
			Board successor = new Board(layout, move, this.computer);	//moves the computer to that point	
			//layout.movePlayer(this.computer, move);
			value = Math.max(value, minValue(successor, alpha, beta, depthLimit-1));//CALL MIN FOR PLAYEr	
			//layout.resetPlayer(this.computer, currentPoint);
			if(value >= beta)
			{
				return value;
			}//end if
			
			alpha = Math.max(alpha, value);
		}//end for
		return value;
	}//end maxValue
	
	private int minValue(Board layout, int alpha, int beta, int depthLimit) throws NoTimeRemainingException
	{
		//Time Limit Check 
		if(!timeRemaining()) throw new NoTimeRemainingException("");
		//Depth Limit Check
		if(depthLimit == 0) return layout.evaluateBoard(this.player);//utility value of terminal state
		// Game Over Check
		layout.setAvailableSpaces(layout.findAvailableSpaces(this.player));	
		if(layout.getAvailableSpaces().size() == 0) return layout.evaluateBoard(this.player);
		
		int value = Integer.MAX_VALUE;//Initialize to Infinity	
		//Loops through available positions available to the current player
		//Point currentPoint = new Point(layout.findPosition(this.player));
		
		for(int i = 0; i  < layout.getAvailableSpaces().size(); i++)
		{
			Point move = layout.getAvailableSpaces().get(i);
			Board successor = new Board(layout, move, this.player);//makes the optimal move for the player
			//layout.movePlayer(this.player, move);
			value = Math.min(value, maxValue(successor, alpha, beta, depthLimit-1));
			//layout.resetPlayer(this.player, currentPoint);
			if(value <= alpha)
			{
				return value;
			}//end if
			beta = Math.min(beta, value);
		}//end for
		return value;
	}//minValue
	
}//end class MinMax