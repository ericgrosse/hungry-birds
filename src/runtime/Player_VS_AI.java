package runtime;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

import utils.Board;
import utils.TreeNode;

public class Player_VS_AI {
	
	private static int moveNumber = 1;
	
	public static void run() {
		
		PrintWriter writer = null;
		try {
			writer = new PrintWriter("mini-max-output/player-vs-AI-moves.txt", "UTF-8");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
					writer.println("\nMove " + moveNumber + ": Player 1 chose the move " + larvaCoordinates + input + "\n");
					board.writeBoard(writer);
					writer.flush();
					++moveNumber;

					if(board.checkForWin("player1")) {
						System.out.println("Player 1 wins");
						writer.close();
						System.exit(0);
					}

					break;
				}
				else {
					System.out.println("Invalid move. Please select one of the moves offered.");
				}

			}
			
			// Player2 (AI) move
			System.out.println("\nAI's turn\n");
			
			TreeNode tree = new TreeNode(board, 2);
			tree.addChildren(4); // Create a minimax tree of depth n
			tree.computeMiniMax(tree, 0); // Calculate the heuristic value at the node
			tree.dumpMiniMax(tree, 0); // Debugging
			tree.printTreeToFile(tree, 0); // Debugging
			Board updatedBoard = tree.decideMove(tree); // Execute the move and return the updated board
			String AIMove = Board.compareBoards(board, updatedBoard); // Determine the AI's move as <old coordinates><new coordinates>
			board = new Board(updatedBoard); // Set the game board to the updated board
			board.drawBoard();
			writer.println("\nMove " + moveNumber + ": The AI chose the move " + AIMove + "\n");
			board.writeBoard(writer);
			writer.flush();
			System.out.println("\nThe AI chose the move " + AIMove);
			++moveNumber;
			
			if(board.checkForWin("player2")) {
				System.out.println("Player 2 wins");
				writer.close();
				System.exit(0);
			}
		}
	}
}