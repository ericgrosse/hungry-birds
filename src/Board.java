import java.util.ArrayList;
import java.util.List;

public class Board {
	
	private String[] board = new String[64];
	private static String[] coordinateBoard = new String[64];
	
	/**
	 * Constructors
	 */
	
	public Board() {
		this.initializeBoard();
	}
	
	public Board(Board b) {
		this.setBoard(b.getBoard());
	}
	
	/**
	 * AI Functionality
	 */
	
	// Calculates the heuristic value for the current board configuration
	public int heuristic() {
		
		int larvaPosition = 0;
		int[] birdPositions = new int[4];
		int j = 0;
		
		// Determine the location of all your pieces
		for(int i = 0; i < board.length; ++i) {
			if(board[i].equals("L")) {
				larvaPosition = (i+1);
			}
			else if(board[i].equals("B")) {
				birdPositions[j] = (i+1);
				++j;
			}
		}
		
		int birdPositionSum = birdPositions[0] + birdPositions[1] + birdPositions[2] + birdPositions[3];
		int result = larvaPosition - birdPositionSum;
		
		return result;
		
	}

	
	/**
	 * Board logic
	 */
	
	public boolean movePiece(String coordinates1, String coordinates2) {
		
		int oldPosition = coordinatesToPosition(coordinates1);
		int newPosition = coordinatesToPosition(coordinates2);
		
		// Validate coordinate1
		if(oldPosition == -1) {
			System.out.println("Error: Position " + coordinates1 + " was not found on the board");
			return false;
		}
		
		// Validate that a piece is actually selected
		if( !( board[oldPosition].equals("L") || board[oldPosition].equals("B") ) ) {
			System.out.println("Error: A valid piece was not selected through coordinates " + coordinates1.toUpperCase());
			return false;
		}
		
		// Validate coordinate2
		if(newPosition == -1) {
			System.out.println("Error: Position " + coordinates2 + " was not found on the board");
			return false;
		}
		
		int positionDifference = oldPosition - newPosition;
		String direction = directionFromPositionDifference(positionDifference);
		
		if(validateMove(coordinates1, direction)) {
			board[newPosition] = board[oldPosition];
			board[oldPosition] = "O";
			return true;
		}
		System.out.println("Error: Invalid move");
		return false;
	}
	
	public ArrayList<String> determineMoves(String coordinates) {
		
		int piecePosition = coordinatesToPosition(coordinates);
		ArrayList<String> validMoves = new ArrayList<String>();
		
		if(validateMove(coordinates, "ul")) {
			validMoves.add(translatePiece(coordinates, "ul"));
		}
		if(validateMove(coordinates, "ur")) {
			validMoves.add(translatePiece(coordinates, "ur"));
		}
		// Only allow L to move down-left or down-right
		if(validateMove(coordinates, "dl") && board[piecePosition].equals("L")) {
			validMoves.add(translatePiece(coordinates, "dl"));
		}
		if(validateMove(coordinates, "dr") && board[piecePosition].equals("L")) {
			validMoves.add(translatePiece(coordinates, "dr"));
		}
		
		return validMoves;
		
	}
	
	private boolean validateMove(String coordinates, String direction) {
		
		int piecePos = coordinatesToPosition(coordinates);
		int newPos;
		
		switch(direction) {
		case "ul":
			newPos = piecePos -9;
			// Cannot move at left edge of board or if a piece is already occupying the new space
			if(piecePos <= 7 || piecePos % 8 == 0 || hasPiece(newPos)) {
				return false;
			}
			break;
		case "ur":
			newPos = piecePos - 7;
			// Cannot move at right edge of board or if a piece is already occupying the new space
			if(piecePos <= 7 || piecePos % 8 == 7 || hasPiece(newPos)) {
				return false;
			}
			break;
		case "dl":
			newPos = piecePos + 7;
			// Cannot move at left edge of board or if a piece is already occupying the new space
			if(piecePos >= 56 || piecePos % 8 == 0 || hasPiece(newPos)) {
				return false;
			}
			break;
		case "dr":
			newPos = piecePos + 9;
			// Cannot move at right edge of board or if a piece is already occupying the new space
			if(piecePos >= 56 || piecePos % 8 == 7 || hasPiece(newPos)) {
				return false;
			}
			break;
		default:
			return false;
		}
		return true;
	}
	
	private ArrayList<String> findAvailableBirdCoordinates() {
		String[] birdCoordinates = new String[4];
		ArrayList<String> birdValidatedCoordinates = new ArrayList<String>();
		int j = 0;
		// First need to find all birds
		for(int i = 0; i < board.length; ++i) {
			if(board[i].equals("B")) {
				birdCoordinates[j] = coordinateBoard[i];
				++j;
			}
		}
		// Filter out birds that cannot move
		for(int i = 0; i < birdCoordinates.length; ++i) {
			if(validateMove(birdCoordinates[i], "ul") || validateMove(birdCoordinates[i], "ur")) {
				birdValidatedCoordinates.add(birdCoordinates[i]);
			}
		}
		return birdValidatedCoordinates;
	}
	
	public ArrayList<String> determineBirdMoves() {
		ArrayList<String> availableBirds = findAvailableBirdCoordinates();
		ArrayList<String> allBirdMoves = new ArrayList<String>();
		
		for(int i = 0; i < availableBirds.size(); ++i) {
			ArrayList<String> birdMoves = determineMoves(availableBirds.get(i));
			for(int j = 0; j < birdMoves.size(); ++j) {
				allBirdMoves.add(availableBirds.get(i) + birdMoves.get(j));
			}
		}
		return allBirdMoves;
	}
	
	public boolean checkForWin(String player) {
		
		String player1Coordinates = locatePiece("L");
		int player1Pos = coordinatesToPosition(player1Coordinates);
		
		if(player.equals("player1")) {
			
			// If L is on the last row, player1 wins
			if(player1Pos >= 56) {
				return true;
			}
			// TODO: If none of the birds can move, player 1 also presumably wins
			
			return false;
		}
		else if(player.equals("player2")) {
			
			// If L cannot move, player2 wins
			if(!validateMove(player1Coordinates, "ul") && !validateMove(player1Coordinates, "ur") && !validateMove(player1Coordinates, "dl") && !validateMove(player1Coordinates, "dr")) {
				return true;
			}
			
			return false;
			
		}
		else {
			return false;
		}
	}
	
	// Only useful for locating the position of piece L
	public String locatePiece(String piece) {
		for(int i = 0; i < board.length; ++i) {
			if (board[i].equals(piece)) {
				return coordinateBoard[i];
			}
		}
		
		return "";
	}

	private boolean hasPiece(int position) {
		if(board[position].equals("L") || board[position].equals("B")) {
			return true;
		}
		return false;
	}
	
	private String translatePiece(String coordinates, String direction) {
		int oldPosition = coordinatesToPosition(coordinates);
		switch(direction) {
			case "ul":
				return positionToCoordinates(oldPosition -9);
			case "ur":
				return positionToCoordinates(oldPosition - 7);
			case "dl":
				return positionToCoordinates(oldPosition + 7);
			case "dr":
				return positionToCoordinates(oldPosition + 9);
			default:
				return "";
		}
	}
	
	private int coordinatesToPosition(String coordinates) {
		coordinates = coordinates.toLowerCase();
		for(int i = 0; i < coordinateBoard.length; ++i) {
			if(coordinateBoard[i].equals(coordinates)) {
				return i;
			}
		}
		return -1;
	}
	
	private String positionToCoordinates(int position) {
		return coordinateBoard[position];
	}
	
	private String directionFromPositionDifference(int value) {
		if(value == 9) {
			return "ul";
		}
		else if(value == 7) {
			return "ur";
		}
		else if(value == -7) {
			return "dl";
		}
		else if(value == -9) {
			return "dr";
		}
		else {
			return "";
		}
	}
	
	/**
	 * Board UI
	 */
	
	public void initializeBoard() {
		
        for(int i = 0; i < board.length; ++i) {
        	board[i] = "O";
        }
        
        board[51] = "L";
        // Each bird given a distinct letter in order to simplify the UI
        board[56] = "B";
        board[58] = "B";
        board[60] = "B";
        board[62] = "B";
        
        String[] letters = {"a", "b", "c", "d", "e", "f", "g", "h"};
        
        int k = 9; // Row count, decrement each time you iterate through 8 values
        for(int i = 0; i < coordinateBoard.length; ++i) {
        	int j = i % 8;
        	
        	if(j == 0) {
        		--k; // On the first pass, this is reduced from 9 to 8 before actually writing anything to the coordinateBoard
        	}
        	
        	coordinateBoard[i] = letters[j] + k;
        
        }
	}
	
	public void drawBoard() {
		
		drawVeritcalBorder();
		System.out.println();
		
		for(int i = 0; i < board.length; ++i) {
		
			// Draws left border
        	if(i % 8 == 0) {
        		System.out.print((8 - i/8) + draw(" ") + draw(board[i]));
        	}
			
        	// Draws right border
        	else if(i % 8 == 7) {
        		System.out.println(draw(board[i]) + draw(" ") + (8 - i/8));
        	}
        	
        	else {
        		System.out.print(draw(board[i]));
        	}
        	
		}
		
		System.out.println();
		drawVeritcalBorder();
		
	}
	
	private static String draw(String c) {
		return " " + c + " ";
	}
	
	private static void drawVeritcalBorder() {
		System.out.println(draw("   ") + "A  B  C  D  E  F  G  H");
	}
	
	/**
	 * Getters and setters
	 */
	
	public String[] getBoard() {
		String[] boardCopy = new String[64];
		
		for(int i = 0; i < boardCopy.length; ++i) {
			boardCopy[i] = board[i];
		}
		
		return boardCopy;
	}
	
	public void setBoard(String[] board) {
		for(int i = 0; i < board.length; ++i) {
			this.board[i] = board[i]; 
		}
	}
	
}
