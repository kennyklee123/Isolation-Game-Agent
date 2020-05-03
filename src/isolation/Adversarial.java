package isolation;

import java.awt.Point;

//MinMax algorithm with Alpha-Beta Pruning
public class Adversarial 
{
	private long moveTimeLimit;
	private char computer;
	private char player;
	private long startTime;
	private int bestValue;
	
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
	
	public Point iterativeDeepening(Board layout, int turnCount)
	{
		//Converts current time to seconds to match moveTimeLimit
		this.startTime = System.nanoTime() / (long)1e9;
		Point move = new Point(-1,-1);
		int depth = 3;
		
		try {
			while(timeRemaining())
			{
				move = alphaBeta(layout, depth);
				if(depth < 6)
					depth++;
				else if(turnCount > 30)
					depth++;
			}//end while
		}
		catch(NoTimeRemainingException e) {}		
		return move;
	}//end IterativeDeepening
	
	//Terminates Iterative Deepening if Move Time Limit is exceeded
	private boolean timeRemaining()
	{ 
		//Converts current time to seconds to match moveTimeLimit
		long currentTime = System.nanoTime() / (long)1e9;
		if(currentTime - this.startTime < this.moveTimeLimit)
			return true;		
		return false;
	}//end timeRemaining
	
	//Pass in a depth search limit and the starting board layout of the turn
	private Point alphaBeta(Board layout, int depthLimit) throws NoTimeRemainingException
	{
		this.bestValue = Integer.MIN_VALUE;
		int alpha = Integer.MIN_VALUE;
		int beta = Integer.MAX_VALUE;
		Point bestMove = new Point(-1,-1);
		
		layout.setAvailableSpaces(layout.findAvailableSpaces(this.computer));
		for(int i = 0; i < layout.getAvailableSpaces().size(); i++)
		{
			Point move = layout.getAvailableSpaces().get(i);//gets the next move
			Board successor = new Board(layout, move, this.computer);//moves the computer to that point	
			alpha = Math.max(alpha, minValue(successor, alpha, beta, depthLimit-1));	
			
			if(alpha > this.bestValue)
			{
				this.bestValue = alpha;
				bestMove = move;
			}
		}//end for	
		return bestMove;
	}//end alphaBeta
	
	private int maxValue(Board layout, int alpha, int beta, int depthLimit) throws NoTimeRemainingException
	{	
		//Time Limit Check 
		if(!timeRemaining()) throw new NoTimeRemainingException("");
		//Depth Limit Check
		if(depthLimit == 0) return evaluateBoard(this.computer, layout);//utility value of terminal state
		layout.setAvailableSpaces(layout.findAvailableSpaces(this.computer));
		// Game Over Check
		if(layout.getAvailableSpaces().size() == 0)return evaluateBoard(this.computer, layout);
		
		int value = Integer.MIN_VALUE;//Initialize to Negative Infinity		
		
		for(int i = 0; i  < layout.getAvailableSpaces().size(); i++)
		{	
			Point move = layout.getAvailableSpaces().get(i);// returns a point the computer can move too			
			Board successor = new Board(layout, move, this.computer);//moves the computer to that point	
			value = Math.max(value, minValue(successor, alpha, beta, depthLimit-1));//CALL MIN FOR PLAYEr	

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
		if(depthLimit == 0) return evaluateBoard(this.player, layout);//utility value of terminal state
		// Game Over Check
		layout.setAvailableSpaces(layout.findAvailableSpaces(this.player));	
		if(layout.getAvailableSpaces().size() == 0) return evaluateBoard(this.player, layout);
		
		int value = Integer.MAX_VALUE;//Initialize to Infinity
		
		for(int i = 0; i  < layout.getAvailableSpaces().size(); i++)
		{
			Point move = layout.getAvailableSpaces().get(i);
			Board successor = new Board(layout, move, this.player);//makes the optimal move for the player
			value = Math.min(value, maxValue(successor, alpha, beta, depthLimit-1));
			
			if(value <= alpha)
			{
				return value;
			}//end if
			beta = Math.min(beta, value);
		}//end for
		return value;
	}//minValue
	
	private int evaluateBoard(char caller, Board layout)
	{
		//Multipliers to adjust the weight of a good or bad move
		int x = 3, y = 1, z = 3;
		char player = caller;
		char opponent;
		
		if(caller =='X')
		{
			opponent = 'O';
		}
		else
			opponent = 'X';
		
		Point playerPosition = layout.findPosition(player);
		Point opponentPosition = layout.findPosition(opponent);
		int utilityValue = x * layout.findAvailableSpaces(player).size();
		utilityValue -= y * layout.findAvailableSpaces(opponent).size();
		
		//Reduces utility value by a factor of ten if the player is next to one of board's edges
		if(playerPosition.getX() == 0 || playerPosition.getX() == layout.getBoardDimension() - 1)
			utilityValue -= 10;
		//Reduces utility value by a factor of ten if the player is next to one of board's edges
		if(playerPosition.getY() == 0 || playerPosition.getY() == layout.getBoardDimension() - 1)
			utilityValue -= 10;	
		//Subtracts a value for each surrounding space filled
		for(int i = -1; i <= 1; i++)
		{
			for(int j = -1; j < 1; j++)
			{
				if(i + playerPosition.getX() >= 0 && i + playerPosition.getX() <= layout.getBoardDimension() - 1
					&& j + playerPosition.getY() >= 0 && j + playerPosition.getY() <= layout.getBoardDimension() - 1)
				{
					if(layout.getBoardLayout()[(int)playerPosition.getX() + i][(int)playerPosition.getY() + j] != '-')
						utilityValue -= z;
				}//end if
				if(i + opponentPosition.getX() >= 0 && i + opponentPosition.getX() <= layout.getBoardDimension() - 1
					&& j + opponentPosition.getY() >= 0 && j + opponentPosition.getY() <= layout.getBoardDimension() - 1)
				{
					if(layout.getBoardLayout()[(int)opponentPosition.getX() + i][(int)opponentPosition.getY() + j] != '-')
						utilityValue += z;
				}//end if
			}//end inner for
		}//end outer for
		return utilityValue;
	}//end evaluateBoard
}//end class MinMax