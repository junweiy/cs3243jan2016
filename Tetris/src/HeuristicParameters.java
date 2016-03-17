import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

public class HeuristicParameters {
	
	protected double a;
	protected double b;
	protected double c;
	protected double d;
	
	public HeuristicParameters() {
		this.a = 0;
		this.b = 0;
		this.c = 0;
		this.d = 0;
	}
	
	public HeuristicParameters(double a, double b, double c, double d) {
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
	}
	
	public HeuristicParameters(HeuristicParameters a, HeuristicParameters b, double aScore, double bScore) {
		this.a = (a.a * aScore + b.a * bScore) / (aScore + bScore);
		this.b = (a.b * aScore + b.b * bScore) / (aScore + bScore);
		this.c = (a.c * aScore + b.c * bScore) / (aScore + bScore);
		this.d = (a.d * aScore + b.d * bScore) / (aScore + bScore);
	}
	
	public static HeuristicParameters loadFirstHeuristicParameters() throws FileNotFoundException, IOException {
		BufferedReader br = new BufferedReader(new FileReader("./src/heuristic.txt"));
		String line;
		line = br.readLine();
		assert(line!=null);
		String tokens[] = line.split(" ");
		assert(tokens.length == 4);
		br.close();
		return new HeuristicParameters(Double.parseDouble(tokens[0]), Double.parseDouble(tokens[1]), 
				Double.parseDouble(tokens[2]), Double.parseDouble(tokens[3]));
	}
	
	public static ArrayList<String> loadAllLines() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("./src/heuristic.txt"));
		String line;
		ArrayList<String> lines = new ArrayList<String>();
		while ((line = br.readLine()) != null) {
			lines.add(line);
		}
		br.close();
		return lines;
	}
	
	public static HeuristicParameters parseParameters(String line) { 
		String[] tokens = line.split(" ");
		assert(tokens.length == 4);
		return new HeuristicParameters(Double.parseDouble(tokens[0]), Double.parseDouble(tokens[1])
				, Double.parseDouble(tokens[2]), Double.parseDouble(tokens[3]));
	}
	
	@Override
	public String toString() {
		NumberFormat formatter = new DecimalFormat("#0.000000");
		return (formatter.format(a) + " " + formatter.format(b) + " " + formatter.format(c) 
					+ " " + formatter.format(d) + "\n");
	}
}
