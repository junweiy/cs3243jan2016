

/**
 * THIS CODE WAS ALL COMMENTED OUT IN VARIOUS CLASSES. I DELETED IT TO START CLEANING UP ALL OF OUR CODE.
 * @author admin
 *
 */
public class CommentedOutCode {
	
/*public void fillNonHoles(int[][] holes, int row, int column) {
	if (row >= ROWS || column >= COLS || row < 0 || column < 0 || field[row][column] != 0
			|| holes[row][column] != 0) {
		return;
	} 
	holes[row][column] = 1;
	fillNonHoles(holes, row + 1, column);
	fillNonHoles(holes, row - 1, column);
	fillNonHoles(holes, row, column + 1);
	fillNonHoles(holes, row, column - 1);
}

//This function calculates the number of holes in the field
public int getNumberOfHoles() {
	int holes = 0;
	
	int [][] filledArray = new int[ROWS][COLS];
	for (int i = 0; i < COLS; i++) {
		fillNonHoles(filledArray, ROWS - 1, i);
	}
	
	for (int row = 0; row < ROWS; row++) {
		for (int column = 0; column < COLS; column++) {
			if (filledArray[row][column] == 0 && field[row][column] == 0) {
				holes++;
			}
		}
	}
	return holes;
}*/
	
	// Get a deep copy of a state
	/*public State copy(State s) {
		State newState = new State();
		newState.lost = s.lost;
		newState.label = s.label;
		newState.turn = s.getTurnNumber();
		newState.cleared = s.getRowsCleared();
		for (int i = 0; i < State.ROWS;i++) {
			for (int j = 0; j < State.COLS; j++) {
				newState.getField()[i][j] = s.getField()[i][j];
			}
		}
		for (int i = 0; i < State.COLS; i++) {
			newState.top[i] = s.getTop()[i];
		}
		newState.nextPiece = s.nextPiece;
		return newState;
	}*/
	
	//This function returns the heights of all columns as an array
		/*public int[] getHeights() {
			int[] heights = new int[COLS];
			for (int column = 0; column < COLS; column++) {
				int row = ROWS - 1;
				while (row >= 0 && field[row][column] == 0) {
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
		
		//This function gets the aggregate heights of each column
		public int getAggregateHeights() {
			int[] heights = getHeights();
			int aggregateHeight = 0;
			for (int height : heights) {
				aggregateHeight += height;
			}
			return aggregateHeight;
		}
		
		//This function gets the bumpiness of each column
		public int getBumpiness() {
			int[] heights = getHeights();
			int bumpiness = 0;
			for (int column = 0; column < COLS - 1; column++) {
				bumpiness += Math.abs(heights[column] - heights[column + 1]);
			}
			return bumpiness;
		}
		
		//This function returns the number of complete lines in the field
		public int getNumberOfCompleteLines() {
			int completeLines = 0;
			for (int row = 0; row < ROWS; row++) {
				for (int column = 0; column < COLS; column++) {
					if (field[row][column] == 0) {
						break;
					} else if (column == COLS - 1) {
						completeLines++;
					}
				}
			}
			return completeLines;
		}

		//This function calculates the number of holes in the field
		public int getNumberOfHoles() {
			int holes = 0;
			int[] heights = getHeights();
			for (int row = 0; row < ROWS; row++) {
				for (int column = 0; column < COLS; column++) {
					if (field[row][column] == 0 && row < heights[column]) {
						holes++;
					}
				}
			}
			return holes;
		}*/
		
		/*public void fillNonHoles(int[][] holes, int row, int column) {
			if (row >= ROWS || column >= COLS || row < 0 || column < 0 || field[row][column] != 0
					|| holes[row][column] != 0) {
				return;
			} 
			holes[row][column] = 1;
			fillNonHoles(holes, row + 1, column);
			fillNonHoles(holes, row - 1, column);
			fillNonHoles(holes, row, column + 1);
			fillNonHoles(holes, row, column - 1);
		}
		
		//This function calculates the number of holes in the field
		public int getNumberOfHoles() {
			int holes = 0;
			
			int [][] filledArray = new int[ROWS][COLS];
			for (int i = 0; i < COLS; i++) {
				fillNonHoles(filledArray, ROWS - 1, i);
			}
			
			for (int row = 0; row < ROWS; row++) {
				for (int column = 0; column < COLS; column++) {
					if (filledArray[row][column] == 0 && field[row][column] == 0) {
						holes++;
					}
				}
			}
			return holes;
		}*/
	
	
	/*try {
	s.makeMove(p.pickMove(s,s.legalMoves()));
} catch (ArrayIndexOutOfBoundsException e) {
	//Printing of legalMoves array for debugging use
    for (int i = 0; i < s.legalMoves.length; i++) {
        for (int j = 0; j < s.legalMoves[i].length; j++) {
            System.out.print(s.legalMoves[i][j] + " ");
        }
        System.out.println();
    }
    e.printStackTrace();
}*/
	
	//while(moveCount < NUMBER_OF_BRICKS && !s.hasLost()) {
	
	//Without MultiThreading
			/*for (int i = 0; i < EVOLVETIME; i++) {
				population.evolve();
				System.out.println("Best solution so far: " + population.getFittestChromosome().getGenes()[0].getAllele() + " " + population.getFittestChromosome().getGenes()[1].getAllele() + " " + population.getFittestChromosome().getGenes()[2].getAllele() + " " + population.getFittestChromosome().getGenes()[3].getAllele() + ", score: " + population.getFittestChromosome().getFitnessValue());
				saveToFile(population.getPopulation().determineFittestChromosomes(POPULATION));
			}*/
	
	//Comment out the two lines above and uncomment the line below, it will work
	//State sTemp = s.copy();
}

//public int pickMove(State s, int[][] legalMoves, double[] weightFactors) {
//
//	//TreeMap<Double, Integer> scores = new TreeMap<Double, Integer>();
//	//Map<Double, List<Integer>> scores = new Map<Double, int[]>(); 
//	ArrayList<Integer> possibleMoves = new ArrayList<Integer>();
//
//	int orient, slot;
//	// TreeMap<Double, Integer> scores = new TreeMap<Double, Integer>();
//	int moveKey = 0;
//	double highestScore = -10000;
//
//	// System.out.println("** Curr piece " + s.getNextPiece());
//	int pWidth[][] = s.getpWidth();
//	int pHeight[][] = s.getpHeight();
//	int pTop[][][] = s.getpTop();
//	int pBottom[][][] = s.getpBottom();
//	int top[] = s.getTop();
//	int field[][] = s.getField();
//	int nextPiece = s.getNextPiece();
//	int turnNumber = s.getTurnNumber();
//	// System.out.println(legalMoves.length);
//	// Go through every possible move
//	for(int i = 0; i < legalMoves.length; i++){
//
//		orient = legalMoves[i][0];
//		slot = legalMoves[i][1];
//		
//		// Perform deep copies, we don't want to mess up our original board
//		int[][] newField = deepCopy(field);
//		int[] newTop = Arrays.copyOf(top, top.length);
//
//		// Get the representation of the field when the piece is dropped and collisions calculated (newField will change and passed as a pointer)
//		if(makeTheoreticalMove(orient, slot, newField, pWidth, pHeight, pTop, pBottom, newTop, nextPiece, turnNumber)){
//			// How well does this state perform?
//			// for(int k = 0; k  < newField.length; k ++) { 
//			// 	for(int j = 0; j<newField[0].length; j++) {
//			// 		System.out.printf("%03d  ", newField[k][j]);
//			// 	}
//			// 	System.out.printf("\n");
//			// }
//			// System.out.printf("\n");
//			double score = newFitnessFunction(newField, newTop, weightFactors);
//			//assert score != brute_force_score;
//
//			/*if(Math.abs(score - brute_force_score) > 0.000000001)
//				System.out.println("** DIFF IN BRUTE AND FITNESS");*/
//				
//			// Equal to highest
//			if(Math.abs(score - highestScore) < 0.000000001){
//				//System.out.println("Warning, scores are equal!");
//				possibleMoves.add(i);
//			}
//
//			// New highest score
//			else if(score > highestScore){
//				possibleMoves.clear();
//				possibleMoves.add(i);
//				highestScore = score;
//			}
//		}
//	}
//
//	return possibleMoves.size() == 0 ? 0 : possibleMoves.get(randnum.nextInt(possibleMoves.size()));
//
//}
