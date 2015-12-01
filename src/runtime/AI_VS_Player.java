package runtime;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

import utils.Board;
import utils.TreeNode;

public class AI_VS_Player {
	
	private static int moveNumber = 1;
	
	public static void run() {
		
		PrintWriter writer = null;
		try {
			writer = new PrintWriter("mini-max-output/AI-vs-player-moves.txt", "UTF-8");
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

			// Player1 (AI) move
			System.out.println("\nAI's turn\n");
			
			TreeNode tree = new TreeNode(board, 1);
			tree.addChildren(4); // Create a minimax tree of depth n
			tree.computeMiniMax(tree, 0); // Calculate the heuristic value at the node
			tree.dumpMiniMax(tree, 0); // Dump the results into a text file
			tree.printTreeToFile(tree, 0); // Debugging only
			Board updatedBoard = tree.decideMove(tree); // Execute the move and return the updated board
			String AIMove = Board.compareBoards(board, updatedBoard); // Determine the AI's move as <old coordinates><new coordinates>
			board = new Board(updatedBoard); // Set the game board to the updated board
			board.drawBoard();
			writer.println("\nMove " + moveNumber + ": The AI chose the move " + AIMove + "\n");
			board.writeBoard(writer);
			writer.flush();
			System.out.println("\nThe AI chose the move " + AIMove);
			++moveNumber;
			
			if(board.checkForWin("player1")) {
				System.out.println("Player 1 wins");
				writer.close();
				System.exit(0);
			}

			// Player2 move
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
				writer.println("\nMove " + moveNumber + ": Player 2 chose the move " + input1 + input2 + "\n");
				board.writeBoard(writer);
				writer.flush();
				++moveNumber;
	
				if(board.checkForWin("player2")) {
					System.out.println("Player 2 wins");
					writer.close();
					System.exit(0);
				}		
				if(isValidMove) {
					break;
				}
			}
		}
	}
}