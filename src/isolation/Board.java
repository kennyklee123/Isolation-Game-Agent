package isolation;

import java.util.ArrayList;
import java.awt.Point;

public class Board
{
	private int boardDimension;
	private Board parent;
	private char[][] boardLayout;
	private int depth;
	private ArrayList<Point> availableSpaces;
	private char initialPlayer; //Whose goes first?
	
	public Board(int bD, char player)
	{
		this.boardDimension = bD;
		this.parent = null;
		this.boardLayout = new char[this.boardDimension][this.boardDimension];
		this.depth = 0;
		this.availableSpaces = new ArrayList<Point>(64);//64 is max available (prevents resizing)
		this.initialPlayer = player;//The player who will go first
		initializeBoardLayout();
		this.availableSpaces = this.findAvailableSpaces(initialPlayer);
	}//end Default Constructor
	
	//Calls this constructor to generate Board state of next move
	//char player is the player who is making the move on the board
	public Board(Board parent, Point destination, char cP) //char cp is the passed in "currentPlayer"
	{
		this.parent = parent;
		this.boardDimension = this.parent.boardDimension;
		this.boardLayout = new char[this.boardDimension][this.boardDimension];
		copyBoardLayout(this.parent, this.boardLayout);
		this.depth = this.parent.depth + 1;
		movePlayer(cP, destination);	
	}//end Constructor

	public void movePlayer(char cP, Point destination)
	{
		//Need to get current position of player to mark it as '#' after moving
		Point lastPosition = new Point(this.findPosition(cP));
		//Move player to destination position	
		this.boardLayout[(int)destination.getX()][(int)destination.getY()] = cP;
		//Overwrite previous position of player with '#'
		this.boardLayout[(int)lastPosition.getX()][(int)lastPosition.getY()] = '#';
	}//end movePlayer
	
	private void initializeBoardLayout()
	{
		for(int row = 0; row < this.boardDimension; row++)
		{
			for(int col = 0; col < this.boardDimension; col++)
			{
				this.boardLayout[row][col] = '-';
			}//end inner for
		}//end outer for
		
		//Initializes Starting Positions for Both Players ('O' will always be bottom right)
		this.boardLayout[0][0] = 'X';
		this.boardLayout[this.boardDimension - 1][this.boardDimension - 1] = 'O';
	}//end initializeBoardLayout
	
	private void copyBoardLayout(Board parent, char[][] childBoardLayout)
	{
		for(int row = 0; row < this.boardDimension; row++)
		{
			for(int col = 0; col < this.boardDimension; col++)
			{
				childBoardLayout[row][col] = parent.boardLayout[row][col];
			}//end inner for
		}//end outer for
	}//end copyBoardLayout
	
	public Point findPosition(char player)
	{
		Point position = new Point();
		for(int row = 0; row < this.boardDimension; row++)
		{
			for(int col = 0; col < this.boardDimension; col++)
			{
				if(this.boardLayout[row][col] == player)
				{
					position.setLocation(row, col);
				}//end if
			}//end inner for
		}//end outer for
		return position;
	}//end findPosition
	
	public ArrayList<Point> findAvailableSpaces(char player)
	{
		ArrayList<Point> availableSpaces = new ArrayList<Point>(64);
		//Where the player 'X' or 'O' is currently located
		Point currentPosition = new Point(findPosition(player));
		
		//Find Available Moves in directions (up, down, left, right)
		findUDLRSpaces(availableSpaces, currentPosition);
		//Find Available Moves on diagonals
		findDiagonalSpaces(availableSpaces, currentPosition);
		
		return availableSpaces;
	}//end findAvailableSpaces
	
	private void findUDLRSpaces(ArrayList<Point> availableSpaces, Point currentPosition)
	{
		int row = (int)currentPosition.getX();
		int col = (int)currentPosition.getY();
		
		//Checks for availableSpaces moving 'DOWN'
		for(int i = row + 1; i < this.boardDimension; i++)
		{
			//'-' denotes an available/empty space
			if(this.boardLayout[i][col] == '-')
			{
				availableSpaces.add(new Point(i, col));
			}//end if
			else
			{
				break;
			}//end else
		}//end for
		
		//Checks for availableSpaces moving 'UP'
		for(int i = row - 1; i >= 0; i--)
		{
			//'-' denotes an available/empty space
			if(this.boardLayout[i][col] == '-')
			{
				availableSpaces.add(new Point(i, col));
			}//end if
			else
			{
				break;
			}//end else
		}//end for
		
		//Checks for availableSpaces moving 'RIGHT'
		for(int i = col + 1; i < this.boardDimension; i++)
		{
			//'-' denotes an available/empty space
			if(this.boardLayout[row][i] == '-')
			{
				availableSpaces.add(new Point(row, i));
			}//end if
			else
			{
				break;
			}//end else
		}//end for
		
		//Checks for availableSpaces moving 'LEFT'
		for(int i = col - 1; i >= 0; i--)
		{
			//'-' denotes an available/empty space
			if(this.boardLayout[row][i] == '-')
			{
				availableSpaces.add(new Point(row, i));
			}//end if
			else
			{
				break;
			}//end else
		}//end for
	}//end findULDRSpaces
	
	private void findDiagonalSpaces(ArrayList<Point> availableSpaces, Point currentPosition)
	{
		int row = (int)currentPosition.getX();
		int col = (int)currentPosition.getY();
		int diagonalDistance;
		
		//Checks for availableSpaces moving 'TOP LEFT'
		diagonalDistance = (row > col ? col: row);
		for(int i = 1; i <= diagonalDistance; i++)
		{
			if(this.boardLayout[row - i][col - i] == '-')
			{
				availableSpaces.add(new Point(row - i, col - i));
			}//end if
			else
			{
				break;
			}//end else
		}//end for
		
		//Checks for availableSpaces moving 'TOP RIGHT'
		diagonalDistance = (row > this.boardDimension - col - 1 ? this.boardDimension - col - 1: row);
		for(int i = 1; i <= diagonalDistance; i++)
		{
			if(this.boardLayout[row - i][col + i] == '-')
			{
				availableSpaces.add(new Point(row - i, col + i));
			}//end if
			else
			{
				break;
			}//end else
		}//end for
		
		//Checks for availableSpaces moving 'BOTTOM LEFT'
		diagonalDistance = (this.boardDimension - row - 1 < col ? this.boardDimension - row - 1: col);
		for(int i = 1; i <= diagonalDistance; i++)
		{
			if(this.boardLayout[row + i][col - i] == '-')
			{
				availableSpaces.add(new Point(row + i, col - i));
			}//end if
			else
			{
				break;
			}//end else
		}//end for
		
		//Checks for availableSpaces moving 'BOTTOM RIGHT'
		diagonalDistance = (this.boardDimension - row - 1 < this.boardDimension - col - 1 
							? this.boardDimension - row - 1: this.boardDimension - col - 1);
		for(int i = 1; i <= diagonalDistance; i++)
		{
			if(this.boardLayout[row + i][col + i] == '-')
			{
				availableSpaces.add(new Point(row + i, col + i));
			}//end if
			else
			{
				break;
			}//end else
		}//end for
	}//findDiagonalSpaces
	
	public void setAvailableSpaces(ArrayList<Point> availSpaces)
	{
		this.availableSpaces = availSpaces;
	}//end setAvailableSpaces()
	
	public ArrayList<Point> getAvailableSpaces()
	{
		return this.availableSpaces;
	}//end getAvailableSpaces
	
	public void clearAvailableSpaces()
	{
		this.availableSpaces.clear();
	}//end clearAvailableSpaces
	
	public Board getParent()
	{
		return this.parent;
	}//end getParent
	
	public int getDepth()
	{
		return this.depth;
	}//end getDepth
	
	public char[][] getBoardLayout()
	{
		return this.boardLayout;
	}//end getBoardLayout
	
	public int getBoardDimension()
	{
		return this.boardDimension;
	}//ends getBoardDimension
	
	public boolean noMovesRemaining()
	{
		return this.availableSpaces.size() == 0 ? true: false;
	}//end noMovesRemaining

}//end Board