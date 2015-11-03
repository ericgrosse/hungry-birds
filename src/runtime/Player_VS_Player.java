package runtime;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

import utils.Board;

public class Player_VS_Player {

	public static void run() {

		Board board = new Board();
		board.drawBoard();

		while(true) {

			Scanner keyboard = new Scanner(System.in);
			String junk; // Used to collect unwanted newlines from the scanner
			String input;
			String chosenPiece;

			// Player1 loop
			while(true) {

				System.out.println("\nPlayer 1's move. Where would you like to move L?");

				String larvaCoordinates = board.locatePiece("L");
				ArrayList<String> moves = board.determineMoves(larvaCoordinates);

				System.out.print("Possible moves: ");
				main.printArrayList(moves);
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

				System.out.println("\nPlayer 2's move. Where would you like to move one of your birds? Please specify two co-ordinates (bird's current position and bird's new position)");
				ArrayList<String> moves = board.determineBirdMoves();

				System.out.print("Possible moves: ");
				main.printArrayList(moves);
				System.out.println();

				input = keyboard.nextLine().toLowerCase();
				ArrayList<String> words = new ArrayList<String>();
				String input1 = "";
				String input2 = "";

				StringTokenizer st = new StringTokenizer(input);
				while(st.hasMoreTokens()) {
					words.add(st.nextToken());
				}

				// Input is something like a1b2
				if(words.size() == 1) {
					String word = words.get(0);
					if(word.length() == 4) {
						input1 = word.substring(0,2);
						input2 = word.substring(2,4);
					}
					else {
						System.out.println("Error: Invalid input. Please try again.");
						continue;
					}
				}
				// Input is something like a1 b2
				else if(words.size() == 2) {
					String word1 = words.get(0);
					String word2 = words.get(1);
					if(word1.length() == 2 && word2.length() == 2) {
						input1 = word1;
						input2 = word2;
					}
					else {
						System.out.println("Error: Invalid input. Please try again.");
						continue;
					}
				}
				else {
					System.out.println("Error: Invalid input. Please try again.");
					continue;
				}

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
}