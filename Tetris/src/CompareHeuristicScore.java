import java.util.Comparator;

public class CompareHeuristicScore implements Comparator<HeuristicParameters> {
	public int compare(HeuristicParameters a, HeuristicParameters b) {
		if (a.score > b.score) {
			return -1; // highest score first
		}
		if (a.score < b.score) {
			return 1;
		}
		return 0;
	}
}
