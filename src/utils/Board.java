package utils;
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
		this.initializeBoard();
		this.setBoard(b.getBoard());
	}

	/**
	 * AI Functionality
	 */

	// Calculates the heuristic value for the current board configuration
	public int heuristic() {
		
		int result = 0;
		
		String larvaCoordinates = this.locatePiece("L");
		ArrayList<String> moves = this.determineMoves(larvaCoordinates);
		
		/** Logic for checking wins */
		
		if(this.checkForWin("player1")) {
			result += 10000;
		}
		else if(this.checkForWin("player2")) {
			result -= 10000;
		}
		
		/** End of logic for checking wins */
		
		/** Logic for checking if the larva has a safe path to the finish */
		
		Board boardCopy = new Board(this);
		result += checkLarvaPath(boardCopy, 0);
		
		/** End of logic for checking if the larva has a safe path to the finish */
		
		/** Logic for row and column positions */
		
		// If a bird is above the larva and not blocking its move, the bird is being totally useless
		
		// Find the bird & larva positions
		ArrayList<Integer> birdPositions = new ArrayList<Integer>();
		int larvaPosition = coordinatesToPosition(larvaCoordinates);
		
		for(int i = 0; i < board.length; ++i) {
			if(board[i].equals("B")) {
				birdPositions.add(i);
			}
		}
		
		for(Integer birdPosition: birdPositions) {
			int birdRow = (int) Math.ceil((double) (birdPosition + 1) / 8);
			int larvaRow = (int) Math.ceil((double) (larvaPosition + 1) / 8);
			
			// Row logic
			if(birdRow <= larvaRow) {
				int difference = larvaRow - birdRow + 1;
				// Difference^2 to reinforce how bad it is for the bird to be several rows above
				result += difference * difference * 50;
			}
			else {
				int difference = birdRow - larvaRow + 1;
				// It's not a huge advantage if the larva is several rows above the bird
				result -= difference * 10;
			}
		}
		
		// Column logic
		// The birds prefer that the larva is on the edge of the board
		int larvaColumn = (larvaPosition % 8) + 1;
		
		if(larvaColumn <= 4) {
			result -= (4 - larvaColumn) * 10;
		}
		else if(larvaColumn >= 5) {
			result -= (larvaColumn - 5) * 10;
		}
		
		/** End of logic for row and column positions */
		
		/** Logic for blocking Larva moves */
		
		ArrayList<String> validMoves = new ArrayList<String>();

		if(validateMove(larvaCoordinates, "ul")) {
			validMoves.add("ul");
		}
		if(validateMove(larvaCoordinates, "ur")) {
			validMoves.add("ur");
		}
		if(validateMove(larvaCoordinates, "dl")) {
			validMoves.add("dl");
		}
		if(validateMove(larvaCoordinates, "dr")) {
			validMoves.add("dr");
		}
		
		// Larva can move anywhere
		if(moves.size() == 4) {
			result += 10;
		}
		else if(moves.size() == 3) {
			if(!validMoves.contains("dl") || !validMoves.contains("dr")) {
				// If the only move the bird prevents is downwards, this gets assigned a higher weight
				result -= 25; 
			}
			else if(!validMoves.contains("ul") || !validMoves.contains("ur")) {
				result -= 5;
			}
		}
		else if(moves.size() == 2) {
			if(validMoves.contains("ul") && validMoves.contains("ur")) {
				result -= 100;
			}
			else if(validMoves.contains("ur") && validMoves.contains("dr")) {
				result -= 50;
			}
			else if(validMoves.contains("dr") && validMoves.contains("dl")) {
				// If you don't block the larva's down-left and down-right movements, that's very unfavorable
				result -= 15;
			}
			else if(validMoves.contains("dl") && validMoves.contains("ul")) {
				result -= 50;
			}
		}
		else if(moves.size() == 1) {
			if(validMoves.contains("ul") || validMoves.contains("ur")) {
				result -= 1000;
			}
			else if(validMoves.contains("dl") || validMoves.contains("dr")) {
				// If the only valid move is something downwards, we're not too happy
				result -= 75; 
			}
		}
		// Larva cannot move
		else if(moves.size() == 0) {
			result -= 10000;
		}
		
		/** End of logic for blocking larva moves */
		
		return result;

	}

	public static String compareBoards(Board b1, Board b2) {
		
		String[] board1 = b1.getBoard();
		String[] board2 = b2.getBoard();
		
		int larvaPosition1 = 0;
		int larvaPosition2 = 0;
		ArrayList<Integer> birdPositions1 = new ArrayList<Integer>();
		ArrayList<Integer> birdPositions2 = new ArrayList<Integer>();

		for(int i = 0; i < board1.length; ++i) {
			if(board1[i].equals("L")) {
				larvaPosition1 = i;
			}
			else if(board1[i].equals("B")) {
				birdPositions1.add(i);
			}
			if(board2[i].equals("L")) {
				larvaPosition2 = i;
			}
			else if(board2[i].equals("B")) {
				birdPositions2.add(i);
			}
		}
		
		if(larvaPosition1 != larvaPosition2) {
			String larvaCoordinates1 = coordinateBoard[larvaPosition1];
			String larvaCoordinates2 = coordinateBoard[larvaPosition2];
			return (larvaCoordinates1 + larvaCoordinates2).toUpperCase();
		}
		
		ArrayList<Integer> birdPositions1Diff = new ArrayList<Integer>(birdPositions1);
		ArrayList<Integer> birdPositions2Diff = new ArrayList<Integer>(birdPositions2);

		birdPositions1Diff.removeAll(birdPositions2);
		birdPositions2Diff.removeAll(birdPositions1);
		
		if(birdPositions1Diff.size() == 1 && birdPositions2Diff.size() == 1) {
			return ( coordinateBoard[birdPositions1Diff.get(0)] + coordinateBoard[birdPositions2Diff.get(0)] ).toUpperCase();
		}
		
		return "";
	}
	
	public int checkLarvaPath(Board board, int depth) {
		
		String larvaCoordinates = board.locatePiece("L");
		int score = 0;
		
		if(board.validateMove(larvaCoordinates, "dl")) {
			Board boardCopy = new Board(board);
			String newCoordinates = boardCopy.translatePiece(larvaCoordinates, "dl");
			boardCopy.movePiece(larvaCoordinates, newCoordinates);
			if(boardCopy.checkForWin("player1")) {
				score += (int)(10000 / Math.pow(2, depth));
				return score;
			}
			else {
				score += checkLarvaPath(boardCopy, depth + 1);
			}	
		}
		if(board.validateMove(larvaCoordinates, "dr")) {
			Board boardCopy = new Board(board);
			String newCoordinates = boardCopy.translatePiece(larvaCoordinates, "dr");
			boardCopy.movePiece(larvaCoordinates, newCoordinates);
			if(boardCopy.checkForWin("player1")) {
				score += (int)(10000 / Math.pow(2, depth));
				return score;
			}
			else {
				score += checkLarvaPath(boardCopy, depth + 1);
			}	
		}
			
		return score;
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
		ArrayList<String> birds = new ArrayList<String>();
		
		// Retrieve the coordinates of all the birds
		for(int i = 0; i < board.length; ++i) {
			if(board[i].equals("B")) {
				birds.add(coordinateBoard[i]);
			}
		}
		
		if(player.equals("player1")) {

			// If L is on the last row, player1 wins
			if(player1Pos >= 56) {
				return true;
			}
			
			boolean flag = true;
			for(String bird: birds) {
				if(validateMove(bird, "ul") || validateMove(bird, "ur")) {
					flag = false; // If any bird can move in either permitted direction, set the flag to false
				}
			}
			// If none of the birds can move, player1 wins
			if(flag == true) {
				return true;
			}

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

		// Initialize the regular board
		
		for(int i = 0; i < board.length; ++i) {
			board[i] = "O";
		}

		board[51] = "L";
		board[56] = "B";
		board[58] = "B";
		board[60] = "B";
		board[62] = "B";

		// Test values
		//board[1] = "B";
		//board[3] = "B";
		//board[5] = "B";
		//board[14] = "B";
		
		// Initialize the coordinate board
		
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
