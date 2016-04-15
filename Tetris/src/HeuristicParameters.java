import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

public class HeuristicParameters  {
	// Four constant parameters
	protected double a;
	protected double b;
	protected double c;
	protected double d;
	protected double e;
	protected double f;
	
	public HeuristicParameters() {
		this.a = 0;
		this.b = 0;
		this.c = 0;
		this.d = 0;
		this.e = 0;
		this.f = 0;
	}
	
	public HeuristicParameters(double a, double b, double c, double d, double e, double f) {
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
		this.e = e;
		this.f = f;
	}
	
	
	public static HeuristicParameters loadFirstHeuristicParameters() throws FileNotFoundException, IOException {
		BufferedReader br = new BufferedReader(new FileReader("./heuristic.txt"));
		String line;
		line = br.readLine();
		assert(line!=null);
		String tokens[] = line.split(" ");
		assert(tokens.length == 5);
		br.close();
		return new HeuristicParameters(Double.parseDouble(tokens[0]), Double.parseDouble(tokens[1]), 
				Double.parseDouble(tokens[2]), Double.parseDouble(tokens[3]), Double.parseDouble(tokens[4]), Double.parseDouble(tokens[5]));
	}
}
