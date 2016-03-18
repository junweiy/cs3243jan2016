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
	
	private static final int NUMBER_OF_ROUNDS = 5;
	private static final double PERCENT_OF_HPS_TO_ADD = 0.3;
	private static final double PERCENT_FOR_TOURNAMENT = 0.1;
	private static final double INITIAL_INTERVAL = 0.6;
	private static final double CHANCE_OF_MUTATION = 0.05;
	private static final double ADJUSTMENT_OF_MUTATION = 0.2;
	
	public void generateNewParametersFromTheBeginning() throws IOException {
		// Create if not exist
		
		
		// Clear text file before generating parameters
		PrintWriter writer = new PrintWriter("./heuristic.txt");
		writer.print("");
		writer.close();
		
		Writer output;
		output = new BufferedWriter(new FileWriter("./heuristic.txt", true));
		NumberFormat formatter = new DecimalFormat("#0.000000");
		
		double score;
		
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
					}
				}
			}
		}
		output.close();
	}
	
	public static double computeAvgScore(HeuristicParameters hp) {
		double score = 0;
		for (int i = 0; i < NUMBER_OF_ROUNDS; i++) {
			score += PlayerSkeleton.trainingRun(hp);
		}
		return score / NUMBER_OF_ROUNDS;
	}
	
	public void initialise() throws IOException {
		lines = HeuristicParameters.loadAllLines();
		for (String l: lines) {
			hps.add(HeuristicParameters.parseParameters(l));
		}
	}
	
	public ArrayList<HeuristicParameters> takeRandomItemsFromAllParameters() {
		Random randomGenerator = new Random();
		ArrayList<HeuristicParameters> temp = new ArrayList<HeuristicParameters>();
		while (temp.size() < hps.size() * PERCENT_FOR_TOURNAMENT) {
			int index = randomGenerator.nextInt(hps.size());
			temp.add(hps.get(index));
		}
		return temp;
	}
	
	public HeuristicParameters getCrossOver(HeuristicParameters a, HeuristicParameters b) {
		HeuristicParameters temp = new HeuristicParameters(a, b);
		applyMutation(temp);
		temp.score = computeAvgScore(temp);
		return temp;
	}
	
	public ArrayList<HeuristicParameters> getHPsOfCrossOvers() {
		ArrayList<HeuristicParameters> temp = new ArrayList<HeuristicParameters>();
		Collections.sort(hps, new CompareHeuristicScore());
		while (temp.size() < hps.size() * PERCENT_OF_HPS_TO_ADD) {
			HeuristicParameters tempHp = getCrossOver(hps.get(temp.size()), hps.get(temp.size() + 1));
			if (!isRedundant(tempHp)) {
				temp.add(getNormalisedHP(tempHp));
			}
		}
		return temp;
	}
	
	public ArrayList<HeuristicParameters> getHPsAfterTournament() {
		int originalSize = hps.size();
		System.out.println("os: "+originalSize);
		hps.addAll(getHPsOfCrossOvers());
		System.out.println("ns: "+hps.size());
		Collections.sort(hps, new CompareHeuristicScore());
		return new ArrayList<HeuristicParameters>(hps.subList(0, originalSize));
	}
	
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
	
	
	public static void main(String[] args) throws IOException {
		// Try to implement genetic algorithm
		Training tr = new Training();
		// Only uncomment generateNewParameter method when restarting training
		tr.generateNewParametersFromTheBeginning();
		System.out.println("Generated");
		tr.initialise();
		System.out.println("Initialised");
		Collections.sort(tr.hps, new CompareHeuristicScore());
		
		int count = 0;
		while(true) {
			tr.hps = tr.getHPsAfterTournament();
			tr.saveToFile();
			count++;
			System.out.println(count);
		}
	}
	
}
