public class PlayerSkeleton {
	
	HeuristicParameters para = new HeuristicParameters(-0.510066, 0.760666, -0.35663, -0.184483);
	
	//implement this function to have a working system
	public int pickMove(State s, int[][] legalMoves) {
		double maxScore = Integer.MIN_VALUE;
		int maxIndex = 0;
		for (int i = 0; i < legalMoves.length; i ++) {
			State nextState = getNextState(s, legalMoves[i]);
			double score = getScore(s, nextState, para);
			if (score > maxScore) {
				maxScore = score;
				maxIndex = i;
			}
		}
		if (maxScore > 0) {
			System.out.println(maxScore);
		}
		
		return maxIndex;
	}
	
	public double getScore(State currentState, State nextState, HeuristicParameters para) {
		int completedLine = nextState.getRowsCleared() - currentState.getRowsCleared();
		return para.a * nextState.getAggregateHeights() + para.b * completedLine 
				+ para.c * nextState.getNumberOfHoles() + para.d * nextState.getBumpiness();
	}
	
	public State getNextState(State s, int[] legalMove) {
		State sTemp = s.copy();
		sTemp.makeMove(legalMove);
		return sTemp;
	}

	
	public static void main(String[] args) {
		State s = new State();
		new TFrame(s);
		PlayerSkeleton p = new PlayerSkeleton();
		while(!s.hasLost()) {
			s.makeMove(p.pickMove(s,s.legalMoves()));
			s.draw();
			s.drawNext(0,0);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("You have completed "+s.getRowsCleared()+" rows.");
	}
	
}
