package runtime;
/**
 * 
 * TODO selection between options (2 player or 1 player vs AI)
 * 
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.StringTokenizer;

import utils.Board;

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
		Scanner keyboard = new Scanner(System.in);
		String input;
		// Loop for the entire game
		while(true) {
			// Select game option
			System.out.println("Would you like to play Hungry Birds with two players (1), or one player and an AI? (2) ");
			input = keyboard.next();
			if(input.equals("1")) {
				Player_VS_Player.run();
			}
			else if(input.equals("2")) {

				while(true) {
					System.out.println("Do you wish to play as the larva (1), or as the birds? (2) ");
					input = keyboard.next();
					if(input.equals("1")) {
						Player_VS_AI.run();
					}
					else if(input.equals("2")) {
						AI_VS_Player.run();
					}
					else {
						System.out.println("Invalid input. Please enter 1 to play as the larva, or 2 to play as the birds.");
					}
				}
			}
			else {
				System.out.println("Invalid input. Please enter 1 to play with two players, or 2 to play with one player and an AI.");
			}
		}
	}
}