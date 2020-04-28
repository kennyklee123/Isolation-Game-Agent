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
	public Point alphaBeta(Board layout, int depthLimit, long sT)
	{
		//Reinitializes list of successors for each computer turn
		this.successors.clear();
		this.startTime = sT;
		this.bestUtilityValue = maxValue(layout, Integer.MIN_VALUE, Integer.MAX_VALUE, depthLimit);
		
		//System.out.println("Successors Generated: " + this.successors.size());
		return findBestMove(layout.getCurrentPlayer());
	}//end alphaBeta
	
	private int maxValue(Board layout, int alpha, int beta, int depthLimit)
	{
		//Converts the timeTaken to make a move into seconds (System.nanoTime() is 1e9)
		this.timeTaken = (long) ((System.nanoTime() - this.startTime) / 1e9);
		
		//Terminal State / Time Limit / Game Over Check
		if(layout.getDepth() >= depthLimit || layout.getAvailableSpaces().size() == 0
			|| this.timeTaken > .95 * this.moveTimeLimit)
		{
			return layout.evaluateBoard();//utility value of terminal state
		}//end if
		
		int utilityValue = Integer.MIN_VALUE;//Initialize to Negative Infinity	
		//Loops through available positions available to the current player
		for(int i = 0; i  < layout.getAvailableSpaces().size(); i++)
		{
			Point move = layout.getAvailableSpaces().get(i);
			Board successor = new Board(layout, move, layout.getCurrentPlayer());
			this.successors.add(successor);
			utilityValue = Math.max(utilityValue, minValue(successor, alpha, beta, depthLimit));
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
		//Converts the timeTaken to make a move into seconds (System.nanoTime() is 1e9)
		this.timeTaken = (long) ((System.nanoTime() - this.startTime) / 1e9);
		
		//Terminal State / Time Limit / Game Over Check
		if(layout.getDepth() >= depthLimit || layout.getAvailableSpaces().size() == 0
			|| this.timeTaken > .95 * this.moveTimeLimit)
		{
			return layout.evaluateBoard();//utility value of terminal state
		}//end if
		
		int utilityValue = Integer.MAX_VALUE;//Initialize to Infinity	
		//Loops through available positions available to the current player
		for(int i = 0; i  < layout.getAvailableSpaces().size(); i++)
		{
			Point move = layout.getAvailableSpaces().get(i);
			Board successor = new Board(layout, move, layout.getCurrentPlayer());
			utilityValue = Math.min(utilityValue, maxValue(successor, alpha, beta, depthLimit));
			successor.setUtilityValue(utilityValue);
			
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

	private Point findBestMove(char currentPlayer)
	{
		for(int i = 0; i < this.successors.size(); i++)
		{
			if(this.bestUtilityValue == this.successors.get(i).getUtilityValue())
			{
				this.bestMove = successors.get(i).findPosition(currentPlayer);
				break;//If multiple successors had same best utility value, use the first one
			}//end if
		}//end for
		return this.bestMove;
	}//end findBestMove
}//end class MinMax