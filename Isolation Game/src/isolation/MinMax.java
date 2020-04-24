package isolation;

import java.awt.Point;
import java.util.ArrayList;

//MinMax algorithm with Alpha-Beta Pruning
public class MinMax 
{
	private long moveTimeLimit;
	private long startTime;
	private long timeTaken;
	private int bestUtilityValue;
	private Point bestMove;
	private ArrayList<Board> successors;
	
	public MinMax()
	{
		this.moveTimeLimit = 20;//Seconds
		this.startTime = 0;
		this.timeTaken = 0;
		this.bestUtilityValue = Integer.MIN_VALUE;
		this.bestMove = new Point(-1, -1);
		this.successors = new ArrayList<Board>(64);
	}//end Default Constructor
	
	public MinMax(long mTL)
	{
		this.moveTimeLimit = mTL;
		this.timeTaken = 0;
		this.startTime = 0;
		this.bestUtilityValue = Integer.MIN_VALUE;
		this.bestMove = new Point(-1, -1);
		this.successors = new ArrayList<Board>(64);
	}//end Constructor
	
	//Pass in a depth search limit and the starting board layout of the turn
	public MinMax alphaBeta(Board initialLayout, int depthLimit, long startTime)
	{
		this.startTime = startTime;
		this.successors.clear();//Reinitializes list of successors for each computer turn
		this.bestUtilityValue = maxValue(initialLayout, Integer.MIN_VALUE, Integer.MAX_VALUE, depthLimit);
		findBestMove();
		return this;
	}//end alphaBeta
	
	private int maxValue(Board layout, int alpha, int beta, int depthLimit)
	{
		int utilityValue = Integer.MIN_VALUE;//Initialize to Negative Infitity
		
		//Converts the timeTaken to make a move into seconds (System.nanoTime() is 1e9)
		this.timeTaken = (long) ((System.nanoTime() - this.startTime) / 1e9);
		
		//Terminal State / Time Limit / Game Over Check
		if(layout.getDepth() >= depthLimit || layout.getAvailableSpaces().size() == 0
			|| this.timeTaken > .975 * this.moveTimeLimit)
		{
			return layout.evaluateBoard();//utility value of terminal state
		}//end if
		
		//Fills an ArrayList with available position the player can move to
		layout.findAvailableSpaces(layout.getCurrentPlayer());
		
		//Loops through available positions available to the current player
		for(int i = 0; i  < layout.getAvailableSpaces().size(); i++)
		{
			Point move = layout.getAvailableSpaces().get(i);
			Board successor = new Board(layout, move, layout.getCurrentPlayer());
			this.successors.add(successor);
			utilityValue = Math.max(utilityValue, minValue(successor, alpha, beta, depthLimit));
			//Utility value of the successor state
			successor.setUtilityValue(utilityValue);
			
			if(utilityValue >= beta)
			{
				return utilityValue;
			}//end if
			alpha = Math.max(alpha, utilityValue);
		}//end for
		return utilityValue;
	}//end maxValue
	
	private int minValue(Board layout, int alpha, int beta, int depthLimit)
	{
		int utilityValue = Integer.MAX_VALUE;//Initialize to Infitity
		
		//Converts the timeTaken to make a move into seconds (System.nanoTime() is 1e9)
		this.timeTaken = (long) ((System.nanoTime() - this.startTime) / 1e9);
		
		//Terminal State / Time Limit / Game Over Check
		if(layout.getDepth() >= depthLimit || layout.getAvailableSpaces().size() == 0
			|| this.timeTaken > .975 * this.moveTimeLimit)
		{
			return layout.evaluateBoard();//utility value of terminal state
		}//end if
		
		//Fills an ArrayList with available position the player can move to
		layout.findAvailableSpaces(layout.getCurrentPlayer());
				
		//Loops through available positions available to the current player
		for(int i = 0; i  < layout.getAvailableSpaces().size(); i++)
		{
			Point move = layout.getAvailableSpaces().get(i);
			Board successor = new Board(layout, move, layout.getCurrentPlayer());
			utilityValue = Math.min(utilityValue, maxValue(successor, alpha, beta, depthLimit));
			
			if(utilityValue <= alpha)
			{
				return utilityValue;
			}//end if
			beta = Math.min(beta, utilityValue);
		}//end for
		return utilityValue;
	}//minValue
	
	public boolean noMovesRemaining()
	{
		return successors.size() == 0 ? true: false;
	}
	
	public int getBestUtilityValue()
	{
		return this.bestUtilityValue;
	}//end getBestUtilityValue

	private void findBestMove()
	{
		for(int i = 0; i < this.successors.size(); i++)
		{
			if(this.bestUtilityValue == this.successors.get(i).getUtilityValue())
			{
				this.bestMove = successors.get(i).findPosition(successors.get(i).getCurrentPlayer());
			}//end if
		}//end for
	}//end findBestMove
	
	public Point getBestMove()
	{
		return this.bestMove;
	}//end getBestMove
}//end class MinMax