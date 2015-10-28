public class Board {
	
	private String[] board = new String[64];
	
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
	
	public void movePiece(String piece, String direction) {
		
		int piecePos = locatePiece(piece);
		int newPos;
		boolean errorFlag = false;
		
		switch(direction) {
			case "ul":
						
				newPos = piecePos -9;
				
				// Cannot move at left edge of board or if a piece is already occupying the new space
				if(piecePos <= 7 || piecePos % 8 == 0 || hasPiece(newPos)) {
					System.out.println("Error: Invalid move");
					errorFlag = true;
					break;
				}
				
				board[newPos] = piece;
				break;
				
			case "ur":
				
				newPos = piecePos - 7;
				
				// Cannot move at right edge of board or if a piece is already occupying the new space
				if(piecePos <= 7 || piecePos % 8 == 7 || hasPiece(newPos)) {
					System.out.println("Error: Invalid move");
					errorFlag = true;
					break;
				}
				
				board[newPos] = piece;
				break;
			
			case "dl":
			
				newPos = piecePos + 7;
				
				// Cannot move at left edge of board or if a piece is already occupying the new space
				if(piecePos >= 56 || piecePos % 8 == 0 || hasPiece(newPos)) {
					System.out.println("Error: Invalid move");
					errorFlag = true;
					break;
				}
				
				board[newPos] = piece;
				break;
				
			case "dr":
				
				newPos = piecePos + 9;
				
				// Cannot move at right edge of board or if a piece is already occupying the new space
				if(piecePos >= 56 || piecePos % 8 == 7 || hasPiece(newPos)) {
					System.out.println("Error: Invalid move");
					errorFlag = true;
					break;
				}
				
				board[newPos] = piece;
				break;
				
			default:
				System.out.println("Error: Invalid move");
				errorFlag = true;
			

				
		}
		
		if(!errorFlag) {
			board[piecePos] = "O";
		}
		
	}
	
	public String[] determineMoves(String piece) {
		
		String[] validMoves = new String[4];
		
		if(validateMove(piece, "ul")) {
			System.out.println("ul: Up-left");
			validMoves[0] = "ul";
		}
		if(validateMove(piece, "ur")) {
			System.out.println("ur: Up-right");
			validMoves[1] = "ur";
		}
		// Only allow L to move down-left or down-right
		if(validateMove(piece, "dl") && piece.equals("L")) {
			System.out.println("dl: Down-left");
			validMoves[2] = "dl";
		}
		if(validateMove(piece, "dr") && piece.equals("L")) {
			System.out.println("dr: Down-right");
			validMoves[3] = "dr";
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
			
			int piecePos = locatePiece("L");
			
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
	
	private boolean hasPiece(int position) {
		if(board[position].equals("L") || board[position].equals("Q") || board[position].equals("W") || board[position].equals("E") || board[position].equals("R")) {
			return true;
		}
		return false;
	}
	
	private static String draw(String c) {
		return " " + c + " ";
	}
	
	private static void drawVeritcalBorder() {
		System.out.println(draw("   ") + "A  B  C  D  E  F  G  H");
	}
	
}
