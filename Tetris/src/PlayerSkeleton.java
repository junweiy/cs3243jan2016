/**
*CS3243 Term Project - Learning to Play Tetris with Big Data
*Project Group: 12
*Names:
*Alan Tay Jun Wu		A0097972J
*Erika Leigh Schwartz 	A0149139X
*Sylvanus Quek Junwei	A0098790
*Verbena Ong Geok Bing	A0126240W
*Yang Junwei			E0013812
*
*File: PlayerSkeleton.java
*/

import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

public class PlayerSkeleton {
	
	public class TempState extends State {

		public boolean lost = false;
		public TLabel label;
		public static final int COLS = 10;
		public static final int ROWS = 21;
		public static final int N_PIECES = 7;
		public static final int ORIENT = 0;
		public static final int SLOT = 1;
		
		//current turn
		private int turn = 0;
		private int cleared = 0;
		
		//each square in the grid - int means empty - other values mean the turn it was placed
		private int[][] field = new int[ROWS][COLS];
		//top row+1 of each column
		//0 means empty
		private int[] top = new int[COLS];
		
		//number of next piece
		protected int nextPiece;

		public boolean hasLost() {
			return lost;
		}
		
		public int getTurnNumber() {
			return turn;
		}

		public void setTurnNumber(int turn) {
			this.turn = turn;
		}

		public int getRowsCleared() {
			return cleared;
		}

		public void setRowsCleared(int cleared) {
			this.cleared = cleared;
		}

		public int[][] getField() {
			return field;
		}

		public void setField(int[][] field) {
			for (int i = 0; i < State.ROWS;i++) {
				for (int j = 0; j < State.COLS; j++) {
					this.field[i][j] = field[i][j];
				}
			}
		}

		public int[] getTop() {
			return top;
		}

		public void setTop(int[] top) {
			for (int i = 0; i < State.COLS; i++) {
				this.top[i] = top[i];
			}
		}
		
		public int getNextPiece() {
			return nextPiece;
		}

		public void setNextPiece(int nextPiece) {
			this.nextPiece = nextPiece;
		}
		
		//possible orientations for a given piece type
		protected int[] pOrients = {1,2,4,4,4,2,2};
		
		//the next several arrays define the piece vocabulary in detail
		//width of the pieces [piece ID][orientation]
		protected int[][] pWidth = {
				{2},
				{1,4},
				{2,3,2,3},
				{2,3,2,3},
				{2,3,2,3},
				{3,2},
				{3,2}
		};
		//height of the pieces [piece ID][orientation]
		private int[][] pHeight = {
				{2},
				{4,1},
				{3,2,3,2},
				{3,2,3,2},
				{3,2,3,2},
				{2,3},
				{2,3}
		};
		private int[][][] pBottom = {
			{{0,0}},
			{{0},{0,0,0,0}},
			{{0,0},{0,1,1},{2,0},{0,0,0}},
			{{0,0},{0,0,0},{0,2},{1,1,0}},
			{{0,1},{1,0,1},{1,0},{0,0,0}},
			{{0,0,1},{1,0}},
			{{1,0,0},{0,1}}
		};
		private int[][][] pTop = {
			{{2,2}},
			{{4},{1,1,1,1}},
			{{3,1},{2,2,2},{3,3},{1,1,2}},
			{{1,3},{2,1,1},{3,3},{2,2,2}},
			{{3,2},{2,2,2},{2,3},{1,2,1}},
			{{1,2,2},{3,2}},
			{{2,2,1},{2,3}}
		};
		
		//initialize legalMoves
		{
			//for each piece type
			for(int i = 0; i < N_PIECES; i++) {
				//figure number of legal moves
				int n = 0;
				for(int j = 0; j < pOrients[i]; j++) {
					//number of locations in this orientation
					n += COLS+1-pWidth[i][j];
				}
				//allocate space
				legalMoves[i] = new int[n][2];
				//for each orientation
				n = 0;
				for(int j = 0; j < pOrients[i]; j++) {
					//for each slot
					for(int k = 0; k < COLS+1-pWidth[i][j];k++) {
						legalMoves[i][n][ORIENT] = j;
						legalMoves[i][n][SLOT] = k;
						n++;
					}
				}
			}
		
		}
		
		//random integer, returns 0-6
		private int randomPiece() {
			return (int)(Math.random()*N_PIECES);
		}
		
		//gives legal moves for 
		public int[][] legalMoves() {
			return legalMoves[nextPiece];
		}
		
		//make a move based on the move index - its order in the legalMoves list
		public void makeMove(int move) {
			makeMove(legalMoves[nextPiece][move]);
		}
		
		//make a move based on an array of orient and slot
		public void makeMove(int[] move) {
			makeMove(move[ORIENT],move[SLOT]);
		}
		
		//returns false if you lose - true otherwise
		public boolean makeMove(int orient, int slot) {
			turn++;
			//height if the first column makes contact
			int height = top[slot]-pBottom[nextPiece][orient][0];
			//for each column beyond the first in the piece
			for(int c = 1; c < pWidth[nextPiece][orient];c++) {
				height = Math.max(height,top[slot+c]-pBottom[nextPiece][orient][c]);
			}
			
			//check if game ended
			if(height+pHeight[nextPiece][orient] >= ROWS) {
				lost = true;
				return false;
			}
			
			//for each column in the piece - fill in the appropriate blocks
			for(int i = 0; i < pWidth[nextPiece][orient]; i++) {
				
				//from bottom to top of brick
				for(int h = height+pBottom[nextPiece][orient][i]; h < height+pTop[nextPiece][orient][i]; h++) {
					field[h][i+slot] = turn;
				}
			}
			
			//adjust top
			for(int c = 0; c < pWidth[nextPiece][orient]; c++) {
				top[slot+c]=height+pTop[nextPiece][orient][c];
			}
			
			int rowsCleared = 0;
			
			//check for full rows - starting at the top
			for(int r = height+pHeight[nextPiece][orient]-1; r >= height; r--) {
				//check all columns in the row
				boolean full = true;
				for(int c = 0; c < COLS; c++) {
					if(field[r][c] == 0) {
						full = false;
						break;
					}
				}
				//if the row was full - remove it and slide above stuff down
				if(full) {
					rowsCleared++;
					cleared++;
					//for each column
					for(int c = 0; c < COLS; c++) {

						//slide down all bricks
						for(int i = r; i < top[c]; i++) {
							field[i][c] = field[i+1][c];
						}
						//lower the top
						top[c]--;
						while(top[c]>=1 && field[top[c]-1][c]==0)	top[c]--;
					}
				}
			}
		
			//pick a new piece
			nextPiece = randomPiece();
			
			return true;
		}
		
		//visualization
		//clears the area where the next piece is shown (top)
		public void clearNext() {
			label.filledRectangleLL(0, ROWS+.9, COLS, 4.2, TLabel.DEFAULT_CLEAR_COLOR);
			label.line(0, 0, 0, ROWS+5);
			label.line(COLS, 0, COLS, ROWS+5);
		}
	}
	
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
		 * @param e: The coefficient assigned to the blockades heuristic.
		 * @param f: The coefficient assigned to the number of the edges touching the floor heuristic.
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

		public void setE(double e) {
			this.e = e;
		}

		public void setF(double f) {
			this.f = f;
		}
		
		/**
		 * This function reads from the given heuristic.txt and then loads the given heuristic function into a new HeuristicParameter constructor.
		 * 
		 * @return: The new heuristic parameter as read from the the text file. 
		 * @throws FileNotFoundException: If the heuristic.txt file is unable to be found.
		 * @throws IOException: If the heuristic.txt file is unable to be parsed correctly.
		 */
		/*public static HeuristicParameters loadFirstHeuristicParameters() throws FileNotFoundException, IOException {
			BufferedReader br = new BufferedReader(new FileReader("./heuristic.txt"));
			String line;
			line = br.readLine();
			assert(line!=null);
			String tokens[] = line.split(" ");
			assert(tokens.length == 5);
			br.close();
			return new HeuristicParameters(Double.parseDouble(tokens[0]), Double.parseDouble(tokens[1]), Double.parseDouble(tokens[2]), Double.parseDouble(tokens[3]), Double.parseDouble(tokens[4]), Double.parseDouble(tokens[5]));
		}*/
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
		return para.a * getAggregateHeights(nextState) + para.b * completedLine + para.c * getNumberOfHoles(nextState) + para.d * getBumpiness(nextState) + para.e * getNumberOfBlockades(nextState) + para.f * getNumberOfEdgesTouchingFloor(nextState);
	}
		
	public State getNextState(State s, int[] legalMove) {
		TempState sTemp = new TempState();
		sTemp = copy(s);
		sTemp.makeMove(legalMove);
		return sTemp;
	}
	
	// Get a deep copy of a state
	public TempState copy(State s) {
		TempState newState = new TempState();
		newState.lost = s.lost;
		newState.label = s.label;
		newState.setTurnNumber(s.getTurnNumber());
		newState.setRowsCleared(s.getRowsCleared());
		newState.setField(s.getField());
		newState.setTop(s.getTop());
		newState.setNextPiece(s.getNextPiece());
		return newState;
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
		HeuristicParameters hp = new HeuristicParameters(-0.03681001571959497, 0.04269430141070918, -0.9117823255744231, -0.2292021240729596, -0.013137330556975746, -0.02362316923451724);
		run(hp);
	}
}

// Below are the files we used to train our agent - GeneticAlgorithms.java and TetrisFitnessFunction.java
// GeneticAlgorithms.java
// import java.io.BufferedReader;
// import java.io.BufferedWriter;
// import java.io.FileReader;
// import java.io.FileWriter;
// import java.io.IOException;
// import java.io.PrintWriter;
// import java.io.Writer;
// import java.util.List;
// import java.util.concurrent.Semaphore;

// import org.jgap.Chromosome;
// import org.jgap.Configuration;
// import org.jgap.FitnessFunction;
// import org.jgap.Gene;
// import org.jgap.Genotype;
// import org.jgap.IChromosome;
// import org.jgap.InvalidConfigurationException;
// import org.jgap.Population;
// import org.jgap.audit.EvolutionMonitor;
// import org.jgap.audit.IEvolutionMonitor;
// import org.jgap.distr.IPopulationMerger;
// import org.jgap.event.GeneticEvent;
// import org.jgap.event.GeneticEventListener;
// import org.jgap.impl.BestChromosomesSelector;
// import org.jgap.impl.DefaultConfiguration;
// import org.jgap.impl.DoubleGene;
// import org.jgap.impl.GABreeder;
// import org.jgap.impl.job.SimplePopulationSplitter;
// import org.jgap.impl.FittestPopulationMerger;

// public class GeneticAlgorithms {
	
// 	//Setting the final values for which the genetic algorithm will be tested against.
// 	private static final int POPULATION = 100;  
// 	//The number of threads that the program will be run on simultaneously: this will aid in testing larger populations.
// 	private static final int EVOLVETIME = 3;
// 	private static final int MAXTHREAD = 4;

// 	//To account for large populations, semaphores are used to achieve multi-threading.
// 	static Semaphore mutex = new Semaphore(1);

// 	public static void main(String[] args) throws Exception {
		
// 		//These interfaces/classes are all taken from Jgap, which is a Java library designed from genetic algorithm testing and projection.
// 		Configuration conf = new DefaultConfiguration();
// 		FitnessFunction myFunc = new TetrisFitnessFunction();
// 		SimplePopulationSplitter splitter = new SimplePopulationSplitter(MAXTHREAD);
// 		conf.setFitnessFunction(myFunc);
		
// 		//Chromosomes and genes are generated according to the JGAP library specifications.
// 		Gene[] heuristic = new Gene[6];
		
// 		heuristic[0] = new DoubleGene(conf, -1, 0);
// 		heuristic[1] = new DoubleGene(conf, 0, 1);
// 		heuristic[2] = new DoubleGene(conf, -1, 0);
// 		heuristic[3] = new DoubleGene(conf, -1, 0);	
// 		heuristic[4] = new DoubleGene(conf, -1, 1);	
// 		heuristic[5] = new DoubleGene(conf, -1, 1);	
		
// 		Chromosome heuristics = new Chromosome(conf, heuristic);
// 		conf.setSampleChromosome(heuristics);
// 		Population pop = new Population(conf);
			
// 		// For the first time write heuristics to text file
// 		// Uncommented if it is for the first time

// 		/*
// 		conf.setPopulationSize(POPULATION * MAXTHREAD);
// 		Genotype initPopulation = Genotype.randomInitialGenotype(conf);
// 		saveToFile(initPopulation.getPopulation().determineFittestChromosomes(POPULATION));
// 		*/

// 		// The population is read from the given heuristic.txt file, and then parsed in order to construct the chromosome and genes correctly. 
// 		BufferedReader br = new BufferedReader(new FileReader("./heuristic.txt"));
// 		String line;
// 		while ((line = br.readLine()) != null) {
// 			String hrus[] = line.split(" ");
// 			assert(hrus.length == 5);
// 			Gene newGen[] = new Gene[6];
// 			newGen[0] = new DoubleGene(conf, -1, 0);
// 			newGen[0].setAllele(Double.parseDouble(hrus[0]));
// 			newGen[1] = new DoubleGene(conf, 0, 1);
// 			newGen[1].setAllele(Double.parseDouble(hrus[1]));
// 			newGen[2] = new DoubleGene(conf, -1, 0);
// 			newGen[2].setAllele(Double.parseDouble(hrus[2]));
// 			newGen[3] = new DoubleGene(conf, -1, 0);
// 			newGen[3].setAllele(Double.parseDouble(hrus[3]));
// 			newGen[4] = new DoubleGene(conf, -1, 1);
// 			newGen[4].setAllele(Double.parseDouble(hrus[4]));
// 			newGen[5] = new DoubleGene(conf, -1, 1);
// 			newGen[5].setAllele(Double.parseDouble(hrus[5]));
// 			Chromosome newChr = new Chromosome(conf, newGen);
// 			pop.addChromosome(newChr);
// 		}
// 		br.close();
// 		conf.setPopulationSize(POPULATION * MAXTHREAD);
// 		Population[] pops = splitter.split(pop);
		
// 		//To achieve multithreading
// 		Population[] pops2=multiThreadedEvolve(pops, heuristics, myFunc);
// 		Population test = mergeEvolvedPopulations(pops2);
		
// 		//Once the best solution has been determined, it is printed and saved to a file for future use. 
// 		System.out.println("Best solution so far: " + test.determineFittestChromosome().getGenes()[0].getAllele() + " " + test.determineFittestChromosome().getGenes()[1].getAllele() + 
// 				" " + test.determineFittestChromosome().getGenes()[2].getAllele() + " " + test.determineFittestChromosome().getGenes()[3].getAllele() + " " + test.determineFittestChromosome().getGenes()[4].getAllele() 
// 				+ " " + test.determineFittestChromosome().getGenes()[5].getAllele() +
// 				", score: " + test.determineFittestChromosome().getFitnessValue());		
// 		saveToFile(test.determineFittestChromosomes(POPULATION));
// 	}
	
// 	/**
// 	 * This function, given an array of populations pops, will attempt to merge all of the populations together. It must account for a multithreaded environment.
// 	 * @param pops: The array of populations that must be merged.
// 	 * @return: The merged population, in line with multithreading.
// 	 */
// 	public static Population mergeEvolvedPopulations(Population[] pops){
// 		Population mergedPops= null;
// 		IPopulationMerger merger = new FittestPopulationMerger();
// 		mergedPops = merger.mergePopulations(pops[0], pops[1], POPULATION * 2);
// 		for(int i = 2; i< MAXTHREAD; i++){
// 			mergedPops = merger.mergePopulations(mergedPops, pops[i], POPULATION * (i+1));
// 		}
// 		return mergedPops;
// 	}
	
// 	/**
// 	 * This function aids in completing genetic algorithm projection across multiple populations on multiple threads.
// 	 * @param pops: The array of populations which will aid in making a new genotype.
// 	 * @param sampleChromosome: The sample chromosome which is added to the configuration.
// 	 * @param func: The fitness function with is added to the configuration.
// 	 * @return: The array of populations that have resolved as a result of genetic algorithm projections
// 	 * @throws InvalidConfigurationException: The configuration was unable to be set correctly. 
// 	 */
// 	public static Population[] multiThreadedEvolve(Population[] pops, Chromosome sampleChromosome, FitnessFunction func) throws InvalidConfigurationException {
		
// 		final Population[] newpops = new Population[MAXTHREAD];
// 		ThreadGroup tg = new ThreadGroup("main");
		
// 		//On multiple threads, the populations will be evolved.
// 		for (int i = 0; i < MAXTHREAD; i++) {
// 			//Begin the process of genetic algorithm testing/projection
// 			Configuration gaconf = new DefaultConfiguration(i + "", "multithreaded");
// 			gaconf.setSampleChromosome(sampleChromosome);
// 			gaconf.setPopulationSize(POPULATION);
// 			gaconf.setFitnessFunction(func);
// 			gaconf.getNaturalSelectors(false).clear();
// 			BestChromosomesSelector bcs = new BestChromosomesSelector(gaconf, 1.0d);
// 			bcs.setDoubletteChromosomesAllowed(false);
// 			gaconf.addNaturalSelector(bcs, false);
// 			Genotype genotype = new Genotype(gaconf, pops[i]);
			
// 			final IEvolutionMonitor monitor = new EvolutionMonitor();
// 	        genotype.setUseMonitor(true);
// 	        genotype.setMonitor(monitor);
// 	        gaconf.setMonitor(monitor);
// 	        final int j=i;
// 	        final Thread t1 = new Thread(tg,genotype);
// 	        gaconf.getEventManager().addEventListener(GeneticEvent.GENOTYPE_EVOLVED_EVENT, new GeneticEventListener() {
// 				@SuppressWarnings("deprecation")
// 				public void geneticEventFired(GeneticEvent a_firedEvent) {
// 					GABreeder genotype = (GABreeder) a_firedEvent.getSource();
	
// 					//Tries to acquire a permit from this semaphore
// 					try {
// 						mutex.acquire();
// 					} catch (InterruptedException e) {
// 						//e.printStackTrace();
// 					}
					
// 					int evno = genotype.getLastConfiguration().getGenerationNr();
// 					//Release the permit
// 					mutex.release();
// 					if (evno > EVOLVETIME) {
// 						t1.interrupt();
// 						System.out.println("thread "+j+" ended");
// 						Population p = genotype.getLastPopulation();
						
// 						//Tries to acquire a permit from this semaphore
// 						try {
// 							mutex.acquire();
// 						} catch (InterruptedException e) {
// 							//e.printStackTrace();
// 						}
// 						newpops[j] = p;
// 						//Release the permit
// 						mutex.release();
// 						t1.stop();
// 					}
// 				}	        	
// 	        });
// 	        t1.start();
// 		}
// 		while(true){
// 			try{
// 				if(tg.activeCount()>0){
// 					Thread.sleep(5000);
// 				}else{
// 					return newpops;
// 				}
// 			}catch(InterruptedException e){}
       		
// 		}
// 	}
	
// 	/**
// 	 * After the new best coefficients have been found as a result of genetic algorithm projects, they are saved to a file.
// 	 * @param chromosomes: The chromosomes that have the best score which will be written to the file.
// 	 * @throws IOException: The file is unable to be written to properly.
// 	 */
// 	public static void saveToFile(List<Chromosome> chromosomes) throws IOException {
		
// 		// Clear text file before saving
// 		PrintWriter writer = new PrintWriter("./heuristic.txt");
// 		writer.print("");
// 		writer.close();		
// 		Writer output;
// 		output = new BufferedWriter(new FileWriter("./heuristic.txt", true));	
// 		for (IChromosome chr: chromosomes) {
// 			for (Gene gen: chr.getGenes()) {
// 				output.append(gen.getAllele().toString() + " ");
// 			}
// 			output.append("\n");
// 		}
// 		output.close();
// 	}
// }


// TetrisFitnessFunction.java
// import java.io.FileNotFoundException;
// import java.io.IOException;
// import org.jgap.FitnessFunction;
// import org.jgap.Gene;
// import org.jgap.IChromosome;

// public class TetrisFitnessFunction extends FitnessFunction {
// 	private static final long serialVersionUID = 1L;
	
// 	//This is how many times the fitness function is run in order to determine the correct coefficients.
// 	private static final int NUMBER_OF_ROUNDS = 5;
	
// 	/**
// 	 * This will evaluate an IChromosome a_subject and return the total score determined from the heuristic coefficients that are derived from the IChromosome.
// 	 */
// 	@Override
// 	protected double evaluate(IChromosome a_subject) {
// 		int totalScore = 0;
// 		Gene[] genes = a_subject.getGenes();
// 		double a = (double) genes[0].getAllele();
// 		double b = (double) genes[1].getAllele();
// 		double c = (double) genes[2].getAllele();
// 		double d = (double) genes[3].getAllele();
// 		double e = (double) genes[4].getAllele();
// 		double f = (double) genes[5].getAllele();
// 		PlayerSkeleton.HeuristicParameters heu = new PlayerSkeleton.HeuristicParameters(a, b, c, d, e, f);
// 		for (int i = 0; i < NUMBER_OF_ROUNDS; i++) {
// 			try {
// 				totalScore += getScore(heu);
// 			} catch (IOException e1) {
// 				e1.printStackTrace();
// 			}
// 		}
// 		return (double)totalScore / NUMBER_OF_ROUNDS;
// 	}

// 	/**
// 	 * This function will, given a HeuristicParameters variable, determine the score assigned to the State when a move is made. 
// 	 * @param hp: the HeuristicParameters variable to be tested. hp will possess the specific coefficients relating to each heuristic function and how they are weighted.
// 	 * @return: The score that is determined from the state and heuristicparameters combination.
// 	 * @throws FileNotFoundException: If the file is unable to be found from the thread.
// 	 * @throws IOException: If the file is unable to be parsed from the thread. 
// 	 */
// 	public static int getScore(PlayerSkeleton.HeuristicParameters hp) throws FileNotFoundException, IOException {
// 		State s = new State();
// 		PlayerSkeleton p = null;
// 		p = new PlayerSkeleton(hp);
// 		while(!s.hasLost()) {
// 			try {
// 				s.makeMove(p.pickMove(s,s.legalMoves()));
// 			} catch (ArrayIndexOutOfBoundsException e) {
// 				//Printing of legalMoves array for debugging use
// 			    for (int i = 0; i < s.legalMoves.length; i++) {
// 			        for (int j = 0; j < s.legalMoves[i].length; j++) {
// 			            System.out.print(s.legalMoves[i][j] + " ");
// 			        }
// 			        System.out.println();
// 			    }
// 			    e.printStackTrace();
// 			}
// 			try {
// 				Thread.sleep(0);
// 			} catch (InterruptedException e) {
// 				e.printStackTrace();
// 			}
// 		}
// 		return s.getRowsCleared();
// 	}
// }