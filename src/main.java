/**
 * 
 * TODO selection between options (2 player or 1 player vs AI)
 * 
 */

import java.util.Arrays;
import java.util.Scanner;

public class main {

    public static void main(String[] args) {
    	
    	Board board = new Board();        
    	board.initializeBoard();
        
        // Loop for the entire game
        while(true) {
        	
        	Scanner keyboard = new Scanner(System.in);
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
	    	        	String[] moves = board.determineMoves("L");
	    	        	input = keyboard.next().toLowerCase();
	    	        	
	    	        	if (Arrays.asList(moves).contains(input)) {
	    	        		board.movePiece("L", input);
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
	            	
	            	// Player2 loop (to choose piece)
	            	while(true) {
	            	
	    	        	System.out.println("Player 2's move. Which piece would you like to move?");
	    	        	System.out.println("Available options: ");
	    	        	String[] birds =  board.showAvailableBirds();
	    	        	System.out.println();
	    	        	chosenPiece = keyboard.next().toUpperCase();
	    	        	
	    	        	if (Arrays.asList(birds).contains(chosenPiece)) {
	    	        		break;
	    	        	}
	    	        	else {
	    	        		System.out.println("Invalid selection. Please choose one of Q, W, E or R.");
	    	        	}
	            	
	            	}
	            	
	            	// Player2 loop (for the rest)
	            	while(true) {
	            		
	            		System.out.println("Where would you like to move " + chosenPiece + "?");
	    	        	String[] moves = board.determineMoves(chosenPiece);
	    	        	input = keyboard.next().toLowerCase();
	    	        	
	    	        	if (Arrays.asList(moves).contains(input)) {
	    	        		board.movePiece(chosenPiece, input);
	    	        		board.drawBoard();
	    	        		if(board.checkForWin("player2")) {
	    	        			System.out.println("Player 2 wins");
	    	        			System.exit(0);
	    	        		}
	    	        		break;
	    	        	}
	    	        	else {
	    	        		System.out.println("Invalid move. Please select one of the moves offered.");
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