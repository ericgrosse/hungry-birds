package utils;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/* All AI behavior actually occurs here */
public class TreeNode {

	private Board data;
	private int heuristic;
	private int player;
	private int minValue;
	private int maxValue;
	private static int moveNumber = 0;

	private TreeNode parent;
	private List<TreeNode> children;

	private PrintWriter writer;

	public TreeNode(Board data, int player) {
		this.data = data;
		this.player = player;
		this.heuristic = data.heuristic();
		this.children = new LinkedList<TreeNode>();
		// Experimental
		this.minValue = 1000;
		this.maxValue = -1000;
	}

	public TreeNode addChild(Board child) {

		int player = this.togglePlayer(); // Children nodes have a different player than the parent nodes

		TreeNode childNode = new TreeNode(child, player);
		childNode.parent = this;
		this.children.add(childNode);
		return childNode;
	}

	public void addChildren(int depth) {

		if(depth <= 0) {
			return;
		}

		ArrayList<String> moves = new ArrayList<String>();
		TreeNode childNode = null;

		if(this.player == 1) {
			String startPosition = this.data.locatePiece("L");
			moves = new ArrayList<String>(this.data.determineMoves(startPosition));
			for(String endPosition: moves) {
				Board boardCopy = new Board(this.data);
				boardCopy.movePiece(startPosition, endPosition);
				childNode = this.addChild(boardCopy);
			}	    	
		}
		else if(this.player == 2) {
			moves = this.data.determineBirdMoves();

			// Filter out the starting coordinates since we only want the end coordinates
			for(String s : moves) {
				Board boardCopy = new Board(this.data);
				String startPosition = s.substring(0,2);
				String endPosition = s.substring(2, 4);
				boardCopy.movePiece(startPosition, endPosition);
				childNode = this.addChild(boardCopy);
			}    		
		}

		// Recursion
		for(TreeNode child: this.children) {
			child.addChildren(depth - 1);
		}
	}

	public void computeMiniMax(TreeNode node) {

		if(node.children.size() != 0) {
			for(TreeNode t : node.children) {
				computeMiniMax(t);

				if(node.player == 1) {
					node.maxValue = Math.max(node.maxValue, t.maxValue);
					node.minValue = node.maxValue;
				}
				else if(node.player == 2) {
					node.minValue = Math.min(node.minValue, t.minValue);
					node.maxValue = node.minValue;
				}
			}
		}
		// At a leaf node
		else {
			node.minValue = node.heuristic;
			node.maxValue = node.heuristic;
		}

		// Returned to the root
		if(node.parent == null) {
			node.heuristic = node.maxValue;
		}
	}

	public Board decideMove(TreeNode node) {
		if(node.children.size() != 0) {
			for(TreeNode t : node.children) {
				if(node.heuristic == t.maxValue) {
					return t.getBoard();
				}
			}
		}
		return new Board(); // Keeps the compiler happy
	}

	public void printTree(TreeNode node, int depth) {
		// Recursion
		if(node.children != null) {
			for(TreeNode t : node.children) {
				printTree(t, depth + 1);
			}
		}
		System.out.println(depth + ", " + node.maxValue);
	}

	// Identical to printTree but writes the output to a file
	public void dumpMiniMax(TreeNode node, int depth) {

		if(node.parent == null) {
			++moveNumber;
			try {
				writer = new PrintWriter("mini-max-output/mini-max-output-" + moveNumber + ".txt", "UTF-8");
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if(node.children != null) {
			for(TreeNode t : node.children) {
				dumpMiniMax(t, depth + 1);
			}
		}

		writer.println("Depth: " + depth + ", heuristic: " + node.maxValue);

		if(node.parent == null) {
			writer.close();
		}
	}

	private int togglePlayer() {
		if(this.player == 1) {
			return 2;
		}
		else if(this.player == 2) {
			return 1;
		}
		else {
			return -1;
		}
	}

	public int getHeuristic() {
		return this.heuristic;
	}

	public Board getBoard() {
		return new Board(this.data);
	}

	public static void main(String[] args) {

		Board board = new Board();
		TreeNode tree = new TreeNode(board, 2);
		tree.addChildren(3);
		tree.computeMiniMax(tree);
		tree.dumpMiniMax(tree, 0);
		Board newBoard = tree.decideMove(tree);
		//newBoard.drawBoard();
	}
}