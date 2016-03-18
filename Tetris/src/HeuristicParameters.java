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
	protected double score;
	
	public HeuristicParameters() {
		this.a = 0;
		this.b = 0;
		this.c = 0;
		this.d = 0;
		this.score = 0;
	}
	
	public HeuristicParameters(double a, double b, double c, double d, double score) {
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
		this.score = score;
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

	public HeuristicParameters(HeuristicParameters a, HeuristicParameters b) {
		this.a = (a.a * a.score + b.a * b.score);
		this.b = (a.b * a.score + b.b * b.score);
		this.c = (a.c * a.score + b.c * b.score);
		this.d = (a.d * a.score+ b.d * b.score);
		this.score = 0;
	}
	
	public static HeuristicParameters loadFirstHeuristicParameters() throws FileNotFoundException, IOException {
		BufferedReader br = new BufferedReader(new FileReader("./src/heuristic.txt"));
		String line;
		line = br.readLine();
		assert(line!=null);
		String tokens[] = line.split(" ");
		assert(tokens.length == 5);
		br.close();
		return new HeuristicParameters(Double.parseDouble(tokens[0]), Double.parseDouble(tokens[1]), 
				Double.parseDouble(tokens[2]), Double.parseDouble(tokens[3]),Double.parseDouble(tokens[4]));
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
		assert(tokens.length == 5);
		return new HeuristicParameters(Double.parseDouble(tokens[0]), Double.parseDouble(tokens[1])
				, Double.parseDouble(tokens[2]), Double.parseDouble(tokens[3]), Double.parseDouble(tokens[4]));
	}
	
	@Override
	public String toString() {
		NumberFormat formatter = new DecimalFormat("#0.000000");
		return (formatter.format(a) + " " + formatter.format(b) + " " + formatter.format(c) 
					+ " " + formatter.format(d) + " " + formatter.format(score) + "\n");
	}
}
