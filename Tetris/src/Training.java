import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Training {
	
	private ArrayList<String> lines;
	private HashMap<HeuristicParameters, Double> hm = new HashMap<HeuristicParameters, Double>();
	
	private static final int NUMBER_OF_ROUNDS = 5;
	private static final int NUMBER_OF_HEU_TO_ADD = 3;
	private static final int TOP_NUMBER = 100;
	private static final double INTERVAL = 0.6;
	
	public static void generateNewParameters() throws IOException {
		// Clear text file before generating parameters
		PrintWriter writer = new PrintWriter("./src/heuristic.txt");
		writer.print("");
		writer.close();
		
		Writer output;
		output = new BufferedWriter(new FileWriter("./src/heuristic.txt", true));
		NumberFormat formatter = new DecimalFormat("#0.000000");
		
		// only b is positive
		// generate all permutations where -1 < a < 0, 0 < b < 1, -1 < c < 0, -1 < d < 0
		
		for (double a = -1; a <= 0; a+=INTERVAL) {
			for (double b = 0; b <= 1; b+=INTERVAL) {
				for (double c = -1; c <= 0; c+=INTERVAL) {
					for (double d = -1; d <= 0; d+=INTERVAL) {
						output.append(formatter.format(a) + " " + formatter.format(b) + " " 
					+ formatter.format(c) + " " + formatter.format(d) + "\n");
					}
				}
			}
		}
		output.close();
	}
	
	public void initialise() throws IOException {
		lines = HeuristicParameters.loadAllLines();
		for (String l: lines) {
			hm.put(HeuristicParameters.parseParameters(l), (double) 0);
		}
	}
	
	public void updateHashMapWithScores() {
		for (Map.Entry<HeuristicParameters, Double> item: hm.entrySet()) {
			for (int i = 0; i < NUMBER_OF_ROUNDS; i++) {
				HeuristicParameters hp = item.getKey();
				double score = PlayerSkeleton.trainingRun(hp);
				item.setValue(item.getValue() + score);
			}
			item.setValue(item.getValue() / NUMBER_OF_ROUNDS);
			System.out.println("Score: " + item.getValue());
		}
	}
	
	
	
	public HashMap<HeuristicParameters, Double> getTopNumbersFromHashMap(int num) {
		int i = 0;
		
		List<Map.Entry<HeuristicParameters, Double>> list = new LinkedList<Map.Entry<HeuristicParameters, Double>>(hm.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<HeuristicParameters, Double>>() {
			public int compare(Map.Entry<HeuristicParameters, Double> o1, Map.Entry<HeuristicParameters, Double> o2) {
				return - ((o1).getValue()).compareTo((o2).getValue());
			}
		});
		
		HashMap<HeuristicParameters, Double> result = new HashMap<HeuristicParameters, Double>();
		
		for (Iterator<Map.Entry<HeuristicParameters, Double>> it = list.iterator(); it.hasNext();) {
			Map.Entry<HeuristicParameters, Double> entry = it.next();
			result.put(entry.getKey(), entry.getValue());
			i++;
			if (i >= num) {
				break;
			}
		}
		
		return result;
	}
	
	public HashMap<HeuristicParameters, Double> getHashMapWithMoreParameters() {
		int count = 0;
		HeuristicParameters prev = null;
		HashMap<HeuristicParameters, Double> temp = new HashMap<HeuristicParameters, Double>(hm);
		double prevScore = 0;
		for (Map.Entry<HeuristicParameters, Double> item: temp.entrySet()) {
			if (prev == null) {
				prev = item.getKey();
				prevScore = item.getValue();
			} else {
				HeuristicParameters hp = new HeuristicParameters(prev, item.getKey(), prevScore, item.getValue());
				double score = 0;
				for (int i = 0; i < NUMBER_OF_ROUNDS; i++) {
					score += PlayerSkeleton.trainingRun(hp);
				}
				hm.put(hp, score / NUMBER_OF_ROUNDS);
				count++;
				if (count >= NUMBER_OF_HEU_TO_ADD) {
					return hm;
				}
			}
		}
		
		assert(false);
		return hm;
	}
	
	public void saveToFile() throws IOException {
		// Clear text file before saving
		PrintWriter writer = new PrintWriter("./src/heuristic.txt");
		writer.print("");
		writer.close();
		
		Writer output;
		output = new BufferedWriter(new FileWriter("./src/heuristic.txt", true));
		
		// Sort and appending together to deal with generated parameters
		
		List<Map.Entry<HeuristicParameters, Double>> list = new LinkedList<Map.Entry<HeuristicParameters, Double>>(hm.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<HeuristicParameters, Double>>() {
			public int compare(Map.Entry<HeuristicParameters, Double> o1, Map.Entry<HeuristicParameters, Double> o2) {
				return - ((o1).getValue()).compareTo((o2).getValue());
			}
		});

		for (Iterator<Map.Entry<HeuristicParameters, Double>> it = list.iterator(); it.hasNext();) {
			Map.Entry<HeuristicParameters, Double> entry = it.next();
			output.append(entry.getKey().toString());
			
		}

		System.out.println("Saved.");
		output.close();
	}
	
	
	public static void main(String[] args) throws IOException {
		Training tr = new Training();
		// Only uncomment generateNewParameter method when restarting training
		tr.generateNewParameters();
		tr.initialise();
		tr.updateHashMapWithScores();
		int count = 0;
		while(true) {
			tr.hm = tr.getTopNumbersFromHashMap(TOP_NUMBER);
			tr.hm = tr.getHashMapWithMoreParameters();
			tr.saveToFile();
			count++;
			System.out.println(count);
		}
		
		
		
		
	}
	
}
