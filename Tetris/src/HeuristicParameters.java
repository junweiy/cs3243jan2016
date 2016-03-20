import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

public class HeuristicParameters {
	// Four constant parameters
	protected double a;
	protected double b;
	protected double c;
	protected double d;
	
	// Value of fitness function, which is stored to ensure computed heuristics will not be computed
	// again based on the principle of sacrificing space for time
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

	/**
	 * The method initialise a heuristic combining two heuristics by setting parameters equal to the sum
	 * of (a.para * a.score) * (b.para * b.score) where para = a, b, c and d. However the score is zero, which
	 * is needed to be computed elsewhere.
	 * @param a Heuristic parameters a.
	 * @param b Heuristic parameters b.
	 */
	public HeuristicParameters(HeuristicParameters a, HeuristicParameters b) {
		this.a = (a.a * a.score + b.a * b.score);
		this.b = (a.b * a.score + b.b * b.score);
		this.c = (a.c * a.score + b.c * b.score);
		this.d = (a.d * a.score+ b.d * b.score);
		this.score = 0;
	}
	
	/**
	 * Load the first line of text that is used to store heuristics data and parse into a HP object.
	 * @return The first HP read from the text file.
	 * @throws FileNotFoundException File is not found.
	 * @throws IOException Cannot load properly.
	 */
	public static HeuristicParameters loadFirstHeuristicParameters() throws FileNotFoundException, IOException {
		BufferedReader br = new BufferedReader(new FileReader("./heuristic.txt"));
		String line;
		line = br.readLine();
		assert(line!=null);
		String tokens[] = line.split(" ");
		assert(tokens.length == 5);
		br.close();
		return new HeuristicParameters(Double.parseDouble(tokens[0]), Double.parseDouble(tokens[1]), 
				Double.parseDouble(tokens[2]), Double.parseDouble(tokens[3]),Double.parseDouble(tokens[4]));
	}
	
	/**
	 * Load all lines from the text without parsing.
	 * @return An arraylist of String.
	 * @throws IOException Cannot laod properly.
	 */
	public static ArrayList<String> loadAllLines() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("./heuristic.txt"));
		String line;
		ArrayList<String> lines = new ArrayList<String>();
		while ((line = br.readLine()) != null) {
			lines.add(line);
		}
		br.close();
		return lines;
	}
	
	/**
	 * Parse the line from the text into a HP object.
	 * @param line One line string from the text file.
	 * @return Parsed HP object.
	 */
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
