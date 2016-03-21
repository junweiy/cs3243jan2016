import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Training {
	
	private ArrayList<String> lines = new ArrayList<String>();
	private ArrayList<HeuristicParameters> hps = new ArrayList<HeuristicParameters>();
	
	private static final double ONE_QUARTER = 0.25;
	
	// Configuration for run on cluster
	private static final int NUMBER_OF_ROUNDS = 100;
	private static final double PERCENT_OF_HPS_TO_ADD = 0.3;
	private static final double PERCENT_FOR_TOURNAMENT = 0.1;
	private static final double INITIAL_INTERVAL = 0.15;
	private static final double CHANCE_OF_MUTATION = 0.05;
	private static final double ADJUSTMENT_OF_MUTATION = 0.2;
	
	// Configuration for debugging
//	private static final int NUMBER_OF_ROUNDS = 2;
//	private static final double PERCENT_OF_HPS_TO_ADD = 0.3;
//	private static final double PERCENT_FOR_TOURNAMENT = 0.3;
//	private static final double INITIAL_INTERVAL = 0.6;
//	private static final double CHANCE_OF_MUTATION = 0.05;
//	private static final double ADJUSTMENT_OF_MUTATION = 0.2;
	
	
	/**
	 * The method generate lines that contain all permutations of parameters with given interval and write
	 * all lines into a text file. 
	 * @throws IOException Cannot write file.
	 */
	public void generateNewParametersFromTheBeginning() throws IOException {		
		
		// Clear text file before generating parameters
		PrintWriter writer = new PrintWriter("./heuristic.txt");
		writer.print("");
		writer.close();
		
		
		Writer output;
		output = new BufferedWriter(new FileWriter("./heuristic.txt", true));
		NumberFormat formatter = new DecimalFormat("#0.000000");
		
		double score;
		int count = 0;
		
		// only b is positive
		// generate all permutations where -1 < a < 0, 0 < b < 1, -1 < c < 0, -1 < d < 0
		
		for (double a = -1; a <= 0; a+=INITIAL_INTERVAL) {
			for (double b = 0; b <= 1; b+=INITIAL_INTERVAL) {
				for (double c = -1; c <= 0; c+=INITIAL_INTERVAL) {
					for (double d = -1; d <= 0; d+=INITIAL_INTERVAL) {
						HeuristicParameters hp = new HeuristicParameters(a, b, c, d, 0);
						score = computeAvgScore(hp);
						output.append(formatter.format(a) + " " + formatter.format(b) + " " 
					+ formatter.format(c) + " " + formatter.format(d) + " " + formatter.format(score) +  "\n");
						count++;
						System.out.println(count);
					}
				}
			}
		}
		output.close();
	}
	
	/**
	 * Compute/Normalise the score of HP for computing the score for multiple times.
	 * @param hp HP function used to run the game.
	 * @return A normalised score.
	 */
	public static double computeAvgScore(HeuristicParameters hp) {
		double score = 0;
		for (int i = 0; i < NUMBER_OF_ROUNDS; i++) {
			score += PlayerSkeleton.trainingRun(hp);
		}
		return score / NUMBER_OF_ROUNDS;
	}
	
	/**
	 * Initialisation by loading all lines into an arraylist of HPs.
	 * @throws IOException Cannot load properly.
	 */
	public void initialise() throws IOException {
		lines = HeuristicParameters.loadAllLines();
		for (String l: lines) {
			hps.add(HeuristicParameters.parseParameters(l));
		}
	}
	
	/**
	 * Take random items for a given percent into an arraylist.
	 * @return An arraylist of random picked HPs.
	 */
	public ArrayList<HeuristicParameters> takeRandomItemsFromAllParameters() {
		Random randomGenerator = new Random();
		ArrayList<HeuristicParameters> temp = new ArrayList<HeuristicParameters>();
		while (temp.size() < hps.size() * PERCENT_FOR_TOURNAMENT) {
			int index = randomGenerator.nextInt(hps.size());
			temp.add(hps.get(index));
		}
		return temp;
	}
	
	/**
	 * Get the crossover of two HPs.
	 * @param a HP a.
	 * @param b HP b.
	 * @return Crossed HP.
	 */
	public HeuristicParameters getCrossOver(HeuristicParameters a, HeuristicParameters b) {
		HeuristicParameters temp = new HeuristicParameters(a, b);
		applyMutation(temp);
		temp.score = computeAvgScore(temp);
		return temp;
	}
	
	/**
	 * Get given number of HPs from crossovers combined by random picked HPs from all HPs.
	 * @return An arraylist of all generated crossovers.
	 */
	public ArrayList<HeuristicParameters> getHPsOfCrossOvers() {
		ArrayList<HeuristicParameters> tournamementHPs = takeRandomItemsFromAllParameters();
		ArrayList<HeuristicParameters> temp = new ArrayList<HeuristicParameters>();
		Collections.sort(tournamementHPs, new CompareHeuristicScore());
		while (temp.size() < hps.size() * PERCENT_OF_HPS_TO_ADD) {
			HeuristicParameters tempHp;
			if (temp.size() < (int) (hps.size() * PERCENT_FOR_TOURNAMENT)) {
				tempHp = getCrossOver(tournamementHPs.get(temp.size()), tournamementHPs.get(temp.size() + 1));
			} else {
				int ranNum1 = (int)(getRandomNumberFromInterval(0, tournamementHPs.size()));
				int ranNum2 = (int)(getRandomNumberFromInterval(0, tournamementHPs.size()));
				tempHp = getCrossOver(tournamementHPs.get(ranNum1), tournamementHPs.get(ranNum2));
			}
			
			if (!isRedundant(tempHp)) {
				temp.add(getNormalisedHP(tempHp));
			}
		}
		return temp;
	}
	
	/**
	 * Get the New arraylist of HPs by adding all crossovers and taking the tops with given percent.
	 * @return An arraylist of sorted and updated HPs.
	 */
	public ArrayList<HeuristicParameters> getHPsAfterTournament() {
		int originalSize = hps.size();
		hps.addAll(getHPsOfCrossOvers());
		Collections.sort(hps, new CompareHeuristicScore());
		return new ArrayList<HeuristicParameters>(hps.subList(0, originalSize));
	}
	
	/**
	 * Check if a HP already exists or is a multiple vector of an existing HP. 
	 * @param hp A HP object for checking.
	 * @return A boolean value to indicate if it is redundant.
	 */
	public boolean isRedundant(HeuristicParameters hp) {
		for (int i = 0; i < hps.size(); i++) {
			double quotientA = hps.get(i).a / hp.a;
			double quotientB = hps.get(i).b / hp.b;
			double quotientC = hps.get(i).c / hp.c;
			double quotientD = hps.get(i).d / hp.d;
			if (quotientA == quotientB && quotientB == quotientC && quotientC == quotientD) {
				return true;
			}
		}
		return false;
	}
	
	public double getRandomNumberFromInterval(double a, double b) {
		return a + Math.random() * (b - a);
	}
	
	public HeuristicParameters getNormalisedHP(HeuristicParameters hp) {
		double length = Math.sqrt(hp.a * hp.a + hp.b * hp.b + hp.c * hp.c + hp.d * hp.d);
		double newA = hp.a / length;
		double newB = hp.b / length;
		double newC = hp.c / length;
		double newD = hp.d / length;
		return new HeuristicParameters(newA, newB, newC, newD, hp.score);
	}
	
	/**
	 * Randomly apply mutation by randomly altering one of four parameters with random adjustment.
	 * @param hp A HP object to be applied mutation.
	 */
	public void applyMutation(HeuristicParameters hp) {
		if (Math.random() < CHANCE_OF_MUTATION) {
			System.out.println("Mutated.");
			double ranNum = Math.random();
			if (ranNum < ONE_QUARTER) {
				hp.setA(hp.a + getRandomNumberFromInterval(-ADJUSTMENT_OF_MUTATION, ADJUSTMENT_OF_MUTATION));
			} else if (ranNum < 2 * ONE_QUARTER) {
				hp.setB(hp.b + getRandomNumberFromInterval(-ADJUSTMENT_OF_MUTATION, ADJUSTMENT_OF_MUTATION));
			} else if (ranNum < 3 * ONE_QUARTER) {
				hp.setC(hp.c + getRandomNumberFromInterval(-ADJUSTMENT_OF_MUTATION, ADJUSTMENT_OF_MUTATION));
			} else {
				hp.setD(hp.d + getRandomNumberFromInterval(-ADJUSTMENT_OF_MUTATION, ADJUSTMENT_OF_MUTATION));
			}
		}
		
	}
	
	
	public void saveToFile() throws IOException {
		// Clear text file before saving
		PrintWriter writer = new PrintWriter("./heuristic.txt");
		writer.print("");
		writer.close();
		
		Writer output;
		output = new BufferedWriter(new FileWriter("./heuristic.txt", true));
		
		for (HeuristicParameters hp: hps) {
			output.append(hp.toString());
		}

		System.out.println("Saved.");
		output.close();
	}
	
	
	public static void main(String[] args) {
		// Try to implement genetic algorithm
		Training tr = new Training();
		// Only uncomment generateNewParameter method when restarting training
		//tr.generateNewParametersFromTheBeginning();
		//System.out.println("Generated");
		try {
			tr.initialise();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("Initialised");
		Collections.sort(tr.hps, new CompareHeuristicScore());
		int count = 0;
		while(true) {
			tr.hps = tr.getHPsAfterTournament();
			try {
				tr.saveToFile();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			count++;
			System.out.println(count);
		}
	}
	
}
