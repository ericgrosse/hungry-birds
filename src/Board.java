public class Board {
	
	private String[] board = new String[64];
	private String[] coordinateBoard = new String[64];
	
	public void initializeBoard() {
		
        for(int i = 0; i < board.length; ++i) {
        	board[i] = "O";
        }
        
        board[51] = "L";
        // Each bird given a distinct letter in order to simplify the UI
        board[56] = "Q";
        board[58] = "W";
        board[60] = "E";
        board[62] = "R";
        
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
	
	public void movePiece(String piece, String coordinates) {
		
		int oldPiecePos = locatePiece(piece);
		int newPiecePos = locatePiece(coordinates, coordinateBoard);
		int positionDifference = newPiecePos - oldPiecePos;
		String direction = "";
		boolean errorFlag = false;
		
		// Translate the position difference into a direction string
		if(positionDifference == -9) {
			direction = "ul";
		}
		else if(positionDifference == -7) {
			direction = "ur";
		}
		else if(positionDifference == 7) {
			direction = "dl";
		}
		else if(positionDifference == 9) {
			direction = "dr";
		}
		
		switch(direction) {
			case "ul":
						
				newPiecePos = oldPiecePos -9;
				
				// Cannot move at left edge of board or if a piece is already occupying the new space
				if(oldPiecePos <= 7 || oldPiecePos % 8 == 0 || hasPiece(newPiecePos)) {
					System.out.println("Error: Invalid move");
					errorFlag = true;
					break;
				}
				
				board[newPiecePos] = piece;
				break;
				
			case "ur":
				
				newPiecePos = oldPiecePos - 7;
				
				// Cannot move at right edge of board or if a piece is already occupying the new space
				if(oldPiecePos <= 7 || oldPiecePos % 8 == 7 || hasPiece(newPiecePos)) {
					System.out.println("Error: Invalid move");
					errorFlag = true;
					break;
				}
				
				board[newPiecePos] = piece;
				break;
			
			case "dl":
			
				newPiecePos = oldPiecePos + 7;
				
				// Cannot move at left edge of board or if a piece is already occupying the new space
				if(oldPiecePos >= 56 || oldPiecePos % 8 == 0 || hasPiece(newPiecePos)) {
					System.out.println("Error: Invalid move");
					errorFlag = true;
					break;
				}
				
				board[newPiecePos] = piece;
				break;
				
			case "dr":
				
				newPiecePos = oldPiecePos + 9;
				
				// Cannot move at right edge of board or if a piece is already occupying the new space
				if(oldPiecePos >= 56 || oldPiecePos % 8 == 7 || hasPiece(newPiecePos)) {
					System.out.println("Error: Invalid move");
					errorFlag = true;
					break;
				}
				
				board[newPiecePos] = piece;
				break;
				
			default:
				System.out.println("Error: Invalid move");
				errorFlag = true;
		}
		
		if(!errorFlag) {
			board[oldPiecePos] = "O";
		}
		
	}
	
	public String[] determineMoves(String piece) {
		
		int piecePosition = locatePiece(piece);
		String newCoordinates = "";
		
		String[] validMoves = new String[4];
		
		if(validateMove(piece, "ul")) {
			newCoordinates = coordinateBoard[translatePiece(piecePosition, "ul")];
			System.out.println("Up-left: " + newCoordinates.toUpperCase());
			validMoves[0] = newCoordinates;
		}
		if(validateMove(piece, "ur")) {
			newCoordinates = coordinateBoard[translatePiece(piecePosition, "ur")];
			System.out.println("Up-right: " + newCoordinates.toUpperCase());
			validMoves[1] = newCoordinates;
		}
		// Only allow L to move down-left or down-right
		if(validateMove(piece, "dl") && piece.equals("L")) {
			newCoordinates = coordinateBoard[translatePiece(piecePosition, "dl")];
			System.out.println("Down-left: " + newCoordinates.toUpperCase());
			validMoves[2] = newCoordinates;
		}
		if(validateMove(piece, "dr") && piece.equals("L")) {
			newCoordinates = coordinateBoard[translatePiece(piecePosition, "dr")];
			System.out.println("Down-right: " + newCoordinates.toUpperCase());
			validMoves[3] = newCoordinates;
		}
		
		return validMoves;
		
	}
	
	public String[] showAvailableBirds() {
		
		String[] birds = new String[4];
		String[] testBirds = {"Q", "W", "E", "R"};
		
		for(int i = 0; i < testBirds.length; ++i) {
			if(validateMove(testBirds[i], "ul") || validateMove(testBirds[i], "ur") || validateMove(testBirds[i], "dl") || validateMove(testBirds[i], "dr")) {
				System.out.print(testBirds[i] + " ");
				birds[i] = testBirds[i];
			}
		}
		
		return birds;
		
	}
	
	public boolean checkForWin(String player) {
		
		if(player.equals("player1")) {
			int piecePos = locatePiece("L");
			
			// If L is on the last row, player1 wins
			if(piecePos >= 56) {
				return true;
			}
			
			return false;
		}
		else if(player.equals("player2")) {
			
			// If L cannot move, player2 wins
			if(!validateMove("L", "ul") && !validateMove("L", "ur") && !validateMove("L", "dl") && !validateMove("L", "dr")) {
				return true;
			}
			
			return false;
			
		}
		else {
			return false;
		}
	}
	
	private boolean validateMove(String piece, String direction) {
		
		int piecePos = locatePiece(piece);
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
	
	private int locatePiece(String piece) {
		for(int i = 0; i < board.length; ++i) {
			if (board[i].equals(piece)) {
				return i;
			}
		}
		
		return -1;
	}

	// Overload of previous function to allow for locating on the coordinate board
	private int locatePiece(String value, String[] myBoard) {
		for(int i = 0; i < myBoard.length; ++i) {
			if (myBoard[i].equals(value)) {
				return i;
			}
		}
		
		return -1;
	}
	
	private boolean hasPiece(int position) {
		if(board[position].equals("L") || board[position].equals("Q") || board[position].equals("W") || board[position].equals("E") || board[position].equals("R")) {
			return true;
		}
		return false;
	}
	
	// Given a position on the board, determines your new position on the board array given the direction you move in
	// Make sure you validate the move before using this function
	private int translatePiece(int oldPosition, String direction) {
		switch(direction) {
			case "ul":
				return oldPosition -9;
			case "ur":
				return oldPosition - 7;
			case "dl":
				return oldPosition + 7;
			case "dr":
				return oldPosition + 9;
			default:
				return -1;
		}
	}
	
	private static String draw(String c) {
		return " " + c + " ";
	}
	
	private static void drawVeritcalBorder() {
		System.out.println(draw("   ") + "A  B  C  D  E  F  G  H");
	}
	
}
