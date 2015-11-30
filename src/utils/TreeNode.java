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
	private int player;
	private int minValue;
	private int maxValue;
	private static int moveNumber = 0;
	// Debugging
	public int nodeDepth;

	private TreeNode parent;
	private List<TreeNode> children;

	private PrintWriter writer;

	public TreeNode(Board data, int player) {
		this.data = data;
		this.player = player;
		this.children = new LinkedList<TreeNode>();
		this.minValue = 999999;
		this.maxValue = -999999;
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

	public void computeMiniMax(TreeNode node, int depth) {
		
		node.nodeDepth = depth;
		
		if(node.children.size() != 0) {
			for(TreeNode t : node.children) {
				computeMiniMax(t, depth + 1);

				if(node.player == 1) {
					node.maxValue = Math.max(node.maxValue, t.minValue);
				}
				else if(node.player == 2) {
					node.minValue = Math.min(node.minValue, t.maxValue);
				}
			}
		}
		// At a leaf node
		else {
			if(node.player == 1) {
				node.maxValue = node.data.heuristic();
			}
			else if(node.player == 2) {
				node.minValue = node.data.heuristic();
			}
		}
	}

	public Board decideMove(TreeNode node) {
		if(node.children.size() != 0) {
			for(TreeNode t : node.children) {
				if(node.player == 1) {
					if(node.maxValue == t.minValue) {
						return t.getBoard();
					}
				}
				else if(node.player == 2) {
					if(node.minValue == t.maxValue) {
						return t.getBoard();
					}		
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
		node.data.drawBoard();
		System.out.println("\nDepth: " + depth + ", Heuristic: " + node.maxValue);
		Board boardCopy = new Board(node.data);
		System.out.println("Larva path score: " + boardCopy.checkLarvaPath(boardCopy, 0) + "\n");
	}
	
	public void printTreeToFile(TreeNode node, int depth, PrintWriter p) {
		// Recursion
		if(node.children != null) {
			for(TreeNode t : node.children) {
				printTreeToFile(t, depth + 1, p);
			}
		}

		node.data.writeBoard(p);
		p.println("\n\nDepth: " + depth + ", Mini-Max Heuristic: " + node.maxValue);
		Board boardCopy = new Board(node.data);
		p.println("True heuristic: " + boardCopy.heuristic());
		p.println("Check for win bonus: " + boardCopy.checkForWin);
		for(int i = 0; i < 4; ++i) {
			p.println("Bird row position " + i + " logic: " + boardCopy.birdPositionLogic[i]);
		}
		p.println("Larva column position logic: " + boardCopy.larvaColumnLogic);
		p.println("Larva new column position logic: " + boardCopy.larvaNewColumnLogic);
		p.println("Larva spaces blocked score: " + boardCopy.blockedSpaceScore);
		p.println("Next row logic1: " + boardCopy.nextRowLogic1);
		p.println("Next row logic2: " + boardCopy.nextRowLogic2);
		p.println("Larva path score: " + boardCopy.checkLarvaPath(boardCopy, 0) + "\n\n");
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
		
		if(node.player == 1) {
			writer.println("Depth: " + depth + ", heuristic: " + node.maxValue);
		}
		else if(node.player == 2) {
			writer.println("Depth: " + depth + ", heuristic: " + node.minValue);
		}

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

	public Board getBoard() {
		return new Board(this.data);
	}
}