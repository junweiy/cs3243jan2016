import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.List;
import java.util.concurrent.Semaphore;

import org.jgap.Chromosome;
import org.jgap.Configuration;
import org.jgap.FitnessFunction;
import org.jgap.Gene;
import org.jgap.Genotype;
import org.jgap.IChromosome;
import org.jgap.InvalidConfigurationException;
import org.jgap.Population;
import org.jgap.audit.EvolutionMonitor;
import org.jgap.audit.IEvolutionMonitor;
import org.jgap.distr.IPopulationMerger;
import org.jgap.event.GeneticEvent;
import org.jgap.event.GeneticEventListener;
import org.jgap.impl.BestChromosomesSelector;
import org.jgap.impl.DefaultConfiguration;
import org.jgap.impl.DoubleGene;
import org.jgap.impl.GABreeder;
import org.jgap.impl.job.SimplePopulationSplitter;
import org.jgap.impl.FittestPopulationMerger;

public class GeneticAlgorithms {
	
	//Setting the final values for which the genetic algorithm will be tested against.
	private static final int POPULATION = 100;  
	private static final int EVOLVETIME = 10;
	
	//The number of threads that the program will be run on simultaneously: this will aid in testing larger populations.
	private static final int MAXTHREAD = 4;

	//To account for large populations, semaphores are used to achieve multi-threading.
	static Semaphore mutex = new Semaphore(1);

	public static void main(String[] args) throws Exception {
		
		//These interfaces/classes are all taken from Jgap, which is a Java library designed from genetic algorithm testing and projection.
		Configuration conf = new DefaultConfiguration();
		FitnessFunction myFunc = new TetrisFitnessFunction();
		SimplePopulationSplitter splitter = new SimplePopulationSplitter(MAXTHREAD);
		conf.setFitnessFunction(myFunc);
		
		//Chromosomes and genes are generated according to the JGAP library specifications.
		Gene[] heuristic = new Gene[4];
		heuristic[0] = new DoubleGene(conf, -1, 0);
		heuristic[1] = new DoubleGene(conf, 0, 1);
		heuristic[2] = new DoubleGene(conf, -1, 0);
		heuristic[3] = new DoubleGene(conf, -1, 0);	
		Chromosome heuristics = new Chromosome(conf, heuristic);
		conf.setSampleChromosome(heuristics);
		Population pop = new Population(conf);
			
		// For the first time write heuristics to text file
		// Uncommented if it is for the first time

		/*
		conf.setPopulationSize(POPULATION * MAXTHREAD);
		Genotype initPopulation = Genotype.randomInitialGenotype(conf);
		saveToFile(initPopulation.getPopulation().determineFittestChromosomes(POPULATION));
		*/
		
		// The population is read from the given heuristic.txt file, and then parsed in order to construct the chromosome and genes correctly. 
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
		conf.setPopulationSize(POPULATION * MAXTHREAD);
		Population[] pops = splitter.split(pop);
		
		//To achieve multithreading
		Population[] pops2=multiThreadedEvolve(pops, heuristics, myFunc);
		Population test = mergeEvolvedPopulations(pops2);
		
		//Once the best solution has been determined, it is printed and saved to a file for future use. 
		System.out.println("Best solution so far: " + test.determineFittestChromosome().getGenes()[0].getAllele() + " " + test.determineFittestChromosome().getGenes()[1].getAllele() + " " + test.determineFittestChromosome().getGenes()[2].getAllele() + " " + test.determineFittestChromosome().getGenes()[3].getAllele() + ", score: " + test.determineFittestChromosome().getFitnessValue());		
		saveToFile(test.determineFittestChromosomes(POPULATION));
	}
	
	/**
	 * This function, given an array of populations pops, will attempt to merge all of the populations together. It must account for a multithreaded environment.
	 * @param pops: The array of populations that must be merged.
	 * @return: The merged population, in line with multithreading.
	 */
	public static Population mergeEvolvedPopulations(Population[] pops){
		Population mergedPops= null;
		IPopulationMerger merger = new FittestPopulationMerger();
		mergedPops = merger.mergePopulations(pops[0], pops[1], POPULATION * 2);
		for(int i = 2; i< MAXTHREAD; i++){
			mergedPops = merger.mergePopulations(mergedPops, pops[i], POPULATION * (i+1));
		}
		return mergedPops;
	}
	
	/**
	 * This function aids in completing genetic algorithm projection across multiple populations on multiple threads.
	 * @param pops: The array of populations which will aid in making a new genotype.
	 * @param sampleChromosome: The sample chromosome which is added to the configuration.
	 * @param func: The fitness function with is added to the configuration.
	 * @return: The array of populations that have resolved as a result of genetic algorithm projections
	 * @throws InvalidConfigurationException: The configuration was unable to be set correctly. 
	 */
	public static Population[] multiThreadedEvolve(Population[] pops, Chromosome sampleChromosome, FitnessFunction func) throws InvalidConfigurationException {
		
		final Population[] newpops = new Population[MAXTHREAD];
		ThreadGroup tg = new ThreadGroup("main");
		
		//On multiple threads, the populations will be evolved.
		for (int i = 0; i < MAXTHREAD; i++) {
			//Begin the process of genetic algorithm testing/projection
			Configuration gaconf = new DefaultConfiguration(i + "", "multithreaded");
			gaconf.setSampleChromosome(sampleChromosome);
			gaconf.setPopulationSize(POPULATION);
			gaconf.setFitnessFunction(func);
			gaconf.getNaturalSelectors(false).clear();
			BestChromosomesSelector bcs = new BestChromosomesSelector(gaconf, 1.0d);
			bcs.setDoubletteChromosomesAllowed(false);
			gaconf.addNaturalSelector(bcs, false);
			Genotype genotype = new Genotype(gaconf, pops[i]);
			
			final IEvolutionMonitor monitor = new EvolutionMonitor();
	        genotype.setUseMonitor(true);
	        genotype.setMonitor(monitor);
	        gaconf.setMonitor(monitor);
	        final int j=i;
	        final Thread t1 = new Thread(tg,genotype);
	        gaconf.getEventManager().addEventListener(GeneticEvent.GENOTYPE_EVOLVED_EVENT, new GeneticEventListener() {
				@SuppressWarnings("deprecation")
				public void geneticEventFired(GeneticEvent a_firedEvent) {
					GABreeder genotype = (GABreeder) a_firedEvent.getSource();
	
					//Tries to acquire a permit from this semaphore
					try {
						mutex.acquire();
					} catch (InterruptedException e) {
						//e.printStackTrace();
					}
					
					int evno = genotype.getLastConfiguration().getGenerationNr();
					//Release the permit
					mutex.release();
					if (evno > EVOLVETIME) {
						t1.interrupt();
						System.out.println("thread "+j+" ended");
						Population p = genotype.getLastPopulation();
						
						//Tries to acquire a permit from this semaphore
						try {
							mutex.acquire();
						} catch (InterruptedException e) {
							//e.printStackTrace();
						}
						newpops[j] = p;
						//Release the permit
						mutex.release();
						t1.stop();
					}
				}	        	
	        });
	        t1.start();
		}
		while(true){
			try{
				if(tg.activeCount()>0){
					Thread.sleep(5000);
				}else{
					return newpops;
				}
			}catch(InterruptedException e){}
       		
		}
	}
	
	/**
	 * After the new best coefficients have been found as a result of genetic algorithm projects, they are saved to a file.
	 * @param chromosomes: The chromosomes that have the best score which will be written to the file.
	 * @throws IOException: The file is unable to be written to properly.
	 */
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
		output.close();
	}
}