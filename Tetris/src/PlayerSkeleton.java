import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class PlayerSkeleton {
	
	public static class HeuristicParameters  {
		// Four constant parameters
		protected double a;
		protected double b;
		protected double c;
		protected double d;
		protected double e;
		protected double f;
		
		/**
		 * The default constructor for the heuristic parameter. All coefficients are originally assigned to 0.
		 */
		public HeuristicParameters() {
			this.a = 0;
			this.b = 0;
			this.c = 0;
			this.d = 0;
			this.e = 0;
			this.f = 0;
		}
		
		/**
		 * Constructing a heurstic parameter with the given a, b, c, and d coefficients.
		 * @param a: The coefficient assigned to the aggregate height heuristic.
		 * @param b: The coefficient assigned to the complete lines heuristic.
		 * @param c: The coefficient assigned to the holes heuristic.
		 * @param d: The coefficient assigned to the bumpiness heuristic.
		 * @param e:
		 * @param f:
		 */
		public HeuristicParameters(double a, double b, double c, double d, double e, double f) {
			this.a = a;
			this.b = b;
			this.c = c;
			this.d = d;
			this.e = e;
			this.f = f;
		}
		
		/**
		 * Setter to set a value
		 * @param a the a to set
		 */
		public void setA(double a) {
			this.a = a;
		}

		/**
		 * Setter to set b value
		 * @param b the b to set
		 */
		public void setB(double b) {
			this.b = b;
		}

		/**
		 * Setter to set c value
		 * @param c the c to set
		 */
		public void setC(double c) {
			this.c = c;
		}

		/**
		 * Setter to set d value
		 * @param d the d to set
		 */
		public void setD(double d) {
			this.d = d;
		}
		
		/**
		 * This function reads from the given heuristic.txt and then loads the given heuristic function into a new HeuristicParameter constructor.
		 * 
		 * @return: The new heuristic parameter as read from the the text file. 
		 * @throws FileNotFoundException: If the heuristic.txt file is unable to be found.
		 * @throws IOException: If the heuristic.txt file is unable to be parsed correctly.
		 */
		public static HeuristicParameters loadFirstHeuristicParameters() throws FileNotFoundException, IOException {
			BufferedReader br = new BufferedReader(new FileReader("./heuristic.txt"));
			String line;
			line = br.readLine();
			assert(line!=null);
			String tokens[] = line.split(" ");
			assert(tokens.length == 5);
			br.close();
			return new HeuristicParameters(Double.parseDouble(tokens[0]), Double.parseDouble(tokens[1]), Double.parseDouble(tokens[2]), Double.parseDouble(tokens[3]), Double.parseDouble(tokens[4]), Double.parseDouble(tokens[5]));
		}
	}
	
	HeuristicParameters para = new HeuristicParameters();
	
	public PlayerSkeleton(HeuristicParameters hp) {
		this.para = hp;
	}
	
	//implement this function to have a working system
	public int pickMove(State s, int[][] legalMoves) {
		double maxScore = Integer.MIN_VALUE;
		int maxIndex = 0;
		for (int i = 0; i < legalMoves.length; i++) {
			State nextState = getNextState(s, legalMoves[i]);
			double score = getScore(s, nextState, para);
			if (score > maxScore) {
				maxScore = score;
				maxIndex = i;
			}
		}
		return maxIndex;
	}
	
	/**
	 * Calculate the score/value of fitness function by a * aggregatedHeight + b * completedLine +
	 * c * numberOfHoles + d * bumpiness
	 * @param currentState The current state.
	 * @param nextState The next state for calculation of completedLine.
	 * @param para Heuristic parameters.
	 * @return The value of the fitness function.
	 */
	public double getScore(State currentState, State nextState, HeuristicParameters para) {
		int completedLine = nextState.getRowsCleared() - currentState.getRowsCleared();
		if (nextState.hasLost()) {
			return Double.NEGATIVE_INFINITY;
		}
		return para.a * getAggregateHeights(nextState) + para.b * completedLine 
				+ para.c * getNumberOfHoles(nextState) + para.d * getBumpiness(nextState) + 
				para.e * getNumberOfBlockades(nextState) + para.f * getNumberOfEdgesTouchingFloor(nextState);
	}
		
	public State getNextState(State s, int[] legalMove) {
		State sTemp = s.copy();
		sTemp.makeMove(legalMove);
		return sTemp;
	}
	
	/**
	 * This function obtains all of the heights of each column so that they may be reused easily for the other
	 * heuristics functions below.
	 * 
	 * @param nextState: The state to determine the column heights from.
	 * @return: An array of the heights of all of the columns.
	 */
	public int[] getHeights(State nextState) {
		int[] heights = new int[State.COLS];
		for (int column = 0; column < State.COLS; column++) {
			int row = State.ROWS - 1;
			while (row >= 0 && nextState.getField()[row][column] == 0) {
				row--;
			}
			if (row != -1) {
				heights[column] = row + 1;
			} else {
				heights[column] = 0;
			}
		}
		return heights;
	}
	
	/**
	 * This function determines the aggregate heights of the board.
	 * The aggregate height is determined by getting the height of each row and adding them all together.
	 * 
	 * @param nextState: The state to determine the aggregate height against.
	 * @return: The aggregate height of the next state.
	 */
	public int getAggregateHeights(State nextState) {
		int[] heights = getHeights(nextState);
		int aggregateHeight = 0;
		for (int height : heights) {
			aggregateHeight += height;
		}
		return aggregateHeight;
	}
		
	/**
	 * This function determines the bumpiness of a state.
	 * The bumpiness of a state is determined by taking the absolute difference of every pair of columns next to
	 * each other and then adding these differences together. 
	 * 
	 * @param nextState: The state that will be tested for a given bumpiness.
	 * @return The calculated bumpiness of the nextState.
	 */
	public int getBumpiness(State nextState) {
		int[] heights = getHeights(nextState);
		int bumpiness = 0;
		for (int column = 0; column < State.COLS - 1; column++) {
			bumpiness += Math.abs(heights[column] - heights[column + 1]);
		}
		return bumpiness;
	}
		
	/**
	 * This function returns the number of complete lines in the field for a given piece configuration.
	 * A complete line is defined as a line where every single piece is filled in.
	 * 
	 * @param nextState: This is the nextState which will be tested against the number of complete lines.
	 * @return The number of complete lines.
	 */
	public int getNumberOfCompleteLines(State nextState) {
		int completeLines = 0;
		for (int row = 0; row < State.ROWS; row++) {
			for (int column = 0; column < State.COLS; column++) {
				if (nextState.getField()[row][column] == 0) {
					break;
				} else if (column == State.COLS - 1) {
					completeLines++;
				}
			}
		}
		return completeLines;
	}

	/**
	 * This function is responsible for calculating the number of holes that are currently in the field.
	 * A hole is defined as a space that does not have a Tetris piece currently in it but the space directly above it does.
	 * 
	 * @param nextState: The next state which will determine the number of holes on the board.
	 * @return
	 */
	public int getNumberOfHoles(State nextState) {
		int holes = 0;
		int[] heights = getHeights(nextState);
		for (int row = 0; row < State.ROWS; row++) {
			for (int column = 0; column < State.COLS; column++) {
				if (nextState.getField()[row][column] == 0 && row < heights[column]) {
					holes++;
				}
			}
		}
		return holes;
	}

	/**
	 * This function determines the number of blockades, which is defined as the number of blocks directly above a hole. This number should be minimised.
	 * 
	 * @param nextState: The state against which this function will be tested.
	 * @return: The number of blockades.
	 */
	public int getNumberOfBlockades(State nextState) {
		int blockades = 0;
		int[] heights = getHeights(nextState);
		boolean[] hasHole = new boolean[State.COLS];
		for (int i = 0 ; i < State.COLS; i++) {
			hasHole[i] = false;
		}
		for (int row = 0; row < State.ROWS; row++) {
			for (int column = 0; column < State.COLS; column++) {
				if (!hasHole[column] && nextState.getField()[row][column] == 0 && row < heights[column]) {
					hasHole[column] = true;
				}
			}	
		}
		for (int row = 0; row < State.ROWS; row++) {
			for (int column = 0; column < State.COLS; column++) {
				if (hasHole[column] && nextState.getField()[row][column] != 0 && row <= heights[column]) {
					blockades++;
				}
			}
		}
		return blockades;
	}
	
	/**
	 * This function determines the number of edges touching the floor. This value should be maximised.
	 * 
	 * @param nextState: The state against which this function will be tested.
	 * @return: The number of edges touching the floor.
	 */
	public int getNumberOfEdgesTouchingFloor(State nextState) {
		int count = 0;
		for (int row = 0; row < State.ROWS; row++) {
			for (int column = 0; column < State.COLS; column++) {
				if (nextState.getField()[row][column] != 0 && row == 0) {
					count++;
				}
			}
		}
		return count;
	}

	
	public static int run(HeuristicParameters hp) throws FileNotFoundException, IOException {
		State s = new State();
		new TFrame(s);
		PlayerSkeleton p = null;
		p = new PlayerSkeleton(hp);
		while(!s.hasLost()) {
			s.makeMove(p.pickMove(s,s.legalMoves()));
			s.draw();
			s.drawNext(0,0);
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("You have completed "+s.getRowsCleared()+" rows.");
		return s.getRowsCleared();
	}
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		HeuristicParameters hp = HeuristicParameters.loadFirstHeuristicParameters();
		System.out.println("a, b, c, d: " + hp.a + " " + hp.b + " "+ hp.c + " "+ hp.d);
		run(hp);
	}
}
