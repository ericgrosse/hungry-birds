/**
 * 
 * TODO selection between options (2 player or 1 player vs AI)
 * 
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.StringTokenizer;

public class main {

	public static void printArrayList(ArrayList<String> arr) {
		for(int i = 0; i < arr.size(); ++i) {
			if(i == arr.size() - 1) {
				System.out.print(arr.get(i).toUpperCase());
			}
			else {
				System.out.print(arr.get(i).toUpperCase() + ", ");
			}
		}
	}
	
    public static void main(String[] args) {
    	
    	Board board = new Board();
        
        // Loop for the entire game
        while(true) {
        	
        	Scanner keyboard = new Scanner(System.in);
        	String junk; // Used to collect unwanted newlines from the scanner
        	String input;
        	String chosenPiece;
        	
        	// Select game option
        	
        	System.out.println("Would you like to play Hungry Birds with two players (1), or one player and an AI? (2)");
        	
        	input = keyboard.next();
        	
        	if(input.equals("1")) {
        		
        		board.drawBoard();
        		
        		while(true) {
        		
	            	// Player1 loop
	            	while(true) {
	            	
	    	        	System.out.println("Player 1's move. Where would you like to move L?");
	    	        	
	    	        	String larvaCoordinates = board.locatePiece("L");
	    	        	ArrayList<String> moves = board.determineMoves(larvaCoordinates);
	    	        	
	    	        	System.out.print("Possible moves: ");
	    	        	printArrayList(moves);
	    	        	System.out.println();
	    	        	
	    	        	input = keyboard.next().toLowerCase();
	    	        	junk = keyboard.nextLine();
	    	        	
	    	        	if (moves.contains(input)) {
	    	        		board.movePiece(larvaCoordinates, input);
	    	        		board.drawBoard();
	    	        		
	    	        		if(board.checkForWin("player1")) {
	    	        			System.out.println("Player 1 wins");
	    	        			System.exit(0);
	    	        		}
	    	        		
	    	        		break;
	    	        	}
	    	        	else {
	    	        		System.out.println("Invalid move. Please select one of the moves offered.");
	    	        	}
	            	
	            	}

	            	// Player2 loop
	            	while(true) {
	            		
	            		System.out.println("Player 2's move. Where would you like to move one of your birds? Please specify two co-ordinates (bird's current position and bird's new position)");
	            		ArrayList<String> moves = board.determineBirdMoves();
	            		
	            		System.out.print("Possible moves: ");
	            		printArrayList(moves);
	            		System.out.println();
	            		
	            		input = keyboard.nextLine().toLowerCase();
	    	        	
	    	        	StringTokenizer st = new StringTokenizer(input);
	    	        	String input1 = st.nextToken();
	    	        	String input2 = st.nextToken();
	    	        	
    	        		boolean isValidMove = board.movePiece(input1, input2);
    	        		board.drawBoard();
    	        		
    	        		if(board.checkForWin("player2")) {
    	        			System.out.println("Player 2 wins");
    	        			System.exit(0);
    	        		}		
    	        		if(isValidMove) {
    	        			break;
    	        		}
	            	}
        		}
        	}
        	else if(input.equals("2")) {
        		System.out.println("One player vs an AI will be implemented in the future");
        		break;
        	}
        	else {
        		System.out.println("Invalid input. Please enter 1 to play with two players, or 2 to play with one player and an AI.");
        	}
        }
    }
}