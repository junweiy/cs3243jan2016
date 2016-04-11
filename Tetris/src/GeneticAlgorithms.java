import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.List;

import org.jgap.Chromosome;
import org.jgap.Configuration;
import org.jgap.FitnessFunction;
import org.jgap.Gene;
import org.jgap.Genotype;
import org.jgap.IChromosome;
import org.jgap.InvalidConfigurationException;
import org.jgap.Population;
import org.jgap.impl.DefaultConfiguration;
import org.jgap.impl.DoubleGene;

public class GeneticAlgorithms {
	
	private static final int POPULATION = 100;  
	
	public static void main(String[] args) throws InvalidConfigurationException, IOException {
		Configuration conf = new DefaultConfiguration();
		
		FitnessFunction myFunc = new TetrisFitnessFunction();
		
		conf.setFitnessFunction(myFunc);
		
		Gene[] heuristic = new Gene[4];
		
		heuristic[0] = new DoubleGene(conf, 0, 1);
		heuristic[1] = new DoubleGene(conf, -1, 0);
		heuristic[2] = new DoubleGene(conf, 0, 1);
		heuristic[3] = new DoubleGene(conf, 0, 1);
		
		Chromosome heuristics = new Chromosome(conf, heuristic);
		
		conf.setSampleChromosome(heuristics);
		
		Population pop = new Population(conf);
				
		// For the first time write heuristics to text file
		// Uncommented if it is for the first time
//		conf.setPopulationSize(POPULATION);
//		Genotype initPopulation = Genotype.randomInitialGenotype(conf);
//		saveToFile(initPopulation.getPopulation().determineFittestChromosomes(POPULATION));
		
		// Load population from file
		BufferedReader br = new BufferedReader(new FileReader("./heuristic.txt"));
		String line;
		while ((line = br.readLine()) != null) {
			String hrus[] = line.split(" ");
			assert(hrus.length == 4);
			Gene newGen[] = new Gene[4];
			newGen[0] = new DoubleGene(conf, -1, 0);
			newGen[0].setAllele(Double.parseDouble(hrus[0]));
			newGen[1] = new DoubleGene(conf, 0, 1);
			newGen[1].setAllele(Double.parseDouble(hrus[1]));
			newGen[2] = new DoubleGene(conf, -1, 0);
			newGen[2].setAllele(Double.parseDouble(hrus[2]));
			newGen[3] = new DoubleGene(conf, -1, 0);
			newGen[3].setAllele(Double.parseDouble(hrus[3]));
			Chromosome newChr = new Chromosome(conf, newGen);
			pop.addChromosome(newChr);
		}
		br.close();
		conf.setPopulationSize(POPULATION);
		Genotype population = new Genotype(conf, pop);
		
		for (int i = 0; i < 500; i++) {
			population.evolve();
			System.out.println("Best: " + population.getFittestChromosome().getGenes()[0].getAllele() + " " + population.getFittestChromosome().getFitnessValue());
			saveToFile(population.getPopulation().determineFittestChromosomes(POPULATION));
		}
	}
	
	public static void saveToFile(List<Chromosome> chromosomes) throws IOException {
		
		// Clear text file before saving
		PrintWriter writer = new PrintWriter("./heuristic.txt");
		writer.print("");
		writer.close();
				
		Writer output;
		output = new BufferedWriter(new FileWriter("./heuristic.txt", true));
				
		for (IChromosome chr: chromosomes) {
			for (Gene gen: chr.getGenes()) {
				output.append(gen.getAllele().toString() + " ");
			}
			output.append("\n");
		}
		System.out.println("Saved.");
		output.close();
	}
	
}