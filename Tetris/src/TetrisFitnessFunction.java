import java.io.FileNotFoundException;
import java.io.IOException;
import org.jgap.FitnessFunction;
import org.jgap.Gene;
import org.jgap.IChromosome;

public class TetrisFitnessFunction extends FitnessFunction {
	private static final long serialVersionUID = 1L;
	
	//This is how many times the fitness function is run in order to determine the correct coefficients.
	private static final int NUMBER_OF_ROUNDS = 5;
	
	/**
	 * This will evaluate an IChromosome a_subject and return the total score determined from the heuristic coefficients that are derived from the IChromosome.
	 */
	@Override
	protected double evaluate(IChromosome a_subject) {
		int totalScore = 0;
		Gene[] genes = a_subject.getGenes();
		double a = (double) genes[0].getAllele();
		double b = (double) genes[1].getAllele();
		double c = (double) genes[2].getAllele();
		double d = (double) genes[3].getAllele();
		double e = (double) genes[4].getAllele();
		double f = (double) genes[5].getAllele();
		PlayerSkeleton.HeuristicParameters heu = new PlayerSkeleton.HeuristicParameters(a, b, c, d, e, f);
		for (int i = 0; i < NUMBER_OF_ROUNDS; i++) {
			try {
				totalScore += getScore(heu);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		return (double)totalScore / NUMBER_OF_ROUNDS;
	}

	/**
	 * This function will, given a HeuristicParameters variable, determine the score assigned to the State when a move is made. 
	 * @param hp: the HeuristicParameters variable to be tested. hp will possess the specific coefficients relating to each heuristic function and how they are weighted.
	 * @return: The score that is determined from the state and heuristicparameters combination.
	 * @throws FileNotFoundException: If the file is unable to be found from the thread.
	 * @throws IOException: If the file is unable to be parsed from the thread. 
	 */
	public static int getScore(PlayerSkeleton.HeuristicParameters hp) throws FileNotFoundException, IOException {
		State s = new State();
		PlayerSkeleton p = null;
		p = new PlayerSkeleton(hp);
		while(!s.hasLost()) {
			//s.makeMove(p.pickMove(s,s.legalMoves()));
			try {
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
			}
			try {
				Thread.sleep(0);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return s.getRowsCleared();
	}
}
