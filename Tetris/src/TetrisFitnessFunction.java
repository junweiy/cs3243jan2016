import java.io.FileNotFoundException;
import java.io.IOException;

import org.jgap.FitnessFunction;
import org.jgap.Gene;
import org.jgap.IChromosome;

public class TetrisFitnessFunction extends FitnessFunction {
	
	private static final long serialVersionUID = 1L;
	//private static final int NUMBER_OF_BRICKS = 1000;
	private static final int NUMBER_OF_ROUNDS = 5;
	
	@Override
	protected double evaluate(IChromosome a_subject) {
		int totalScore = 0;
		Gene[] genes = a_subject.getGenes();
		double a = (double) genes[0].getAllele();
		double b = (double) genes[1].getAllele();
		double c = (double) genes[2].getAllele();
		double d = (double) genes[3].getAllele();
		HeuristicParameters heu = new HeuristicParameters(a, b, c, d);
		
		for (int i = 0; i < NUMBER_OF_ROUNDS; i++) {
			try {
				totalScore += getScore(heu);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		return (double)totalScore / NUMBER_OF_ROUNDS;
	}

	
	public static int getScore(HeuristicParameters hp) throws FileNotFoundException, IOException {
		State s = new State();
		int moveCount = 0;
		PlayerSkeleton p = null;
		p = new PlayerSkeleton(hp);
		//while(moveCount < NUMBER_OF_BRICKS && !s.hasLost()) {
		while(!s.hasLost()) {
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
}
