import java.io.FileNotFoundException;
import java.io.IOException;

public class PlayerSkeleton {
	
	HeuristicParameters para = new HeuristicParameters();
	
	public PlayerSkeleton(HeuristicParameters hp) {
		this.para = hp;
	}
	
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
	
	// Only difference is only 500 moves per run 
	public static int trainingRun(HeuristicParameters hp) {
		int moveCount = 0;
		State s = new State();
		PlayerSkeleton p = null;
		p = new PlayerSkeleton(hp);
		while(moveCount < 500) {
			s.makeMove(p.pickMove(s,s.legalMoves()));
			try {
				Thread.sleep(0);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			moveCount++;
		}
		return s.getRowsCleared();
	}

	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		HeuristicParameters hp = HeuristicParameters.loadFirstHeuristicParameters();
		run(hp);
	}
	
}
