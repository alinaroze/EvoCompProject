package Part1;

/* 
 * Alina Rozenbaum
 * Evolution Computation Project
 * Problem: Set Covering
 */

import java.io.IOException;

/**
 * This class is for testing purposes and is run on the Testable class rather than EVA.
 *  Each method may be tested individually here on the trivial data set using the constructor
 */
public class UnitTests {
	Testable test;
	String fileName = "TrivialData";
	
	/**
	 * Constructor. 
	 * Un-comment the method you wish to test.
	 */
	
	public UnitTests() {

//		try {// XXX test dataReader();
//			dataReader();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		System.out.println();
//		makePop();
//		System.out.println();
//		mutation1();
//		System.out.println();
//		mutation2();
//		System.out.println();
//		crossOver1();
//		System.out.println();
//		crossOver2();
//		System.out.println();
//		elite();
//		System.out.println();
//		fitEval();
//		System.out.println();
//		selection1();
//		System.out.println();
//		selection2();
//		System.out.println();
//		checkFeasible();
	}

	/**
	 * pass in trivial test file, # generations, pop size. First Generation
	 * Output should print (randomized) as: 
	 * 1 1 1 1 0
	 * 1 1 1 1 1
	 * 0 0 1 0 1
	 * 0 1 1 0 0
	 * 0 1 0 0 0
	 */
	
	public void dataReader() throws IOException {// XXX working
		System.out.println("Reading in data");
		new Testable(fileName, 1, 5);
	}

	/**
	 * Creates initial population using a random subset of full population
	 */
	
	public void makePop() {// XXX working
		System.out.println("Creating population randomly from data size 5");
		new Testable(fileName, 1, 5);
	}

	/**
	 * Randomly flip a bit in specified chromosome. Will not change the elite
	 * chromosome. 
	 * mutation1(index of chromosome) print result
	 */
	
	public void mutation1() { // XXX working
		System.out
				.println("Testing Mutation 1. Flip bit in chromosome 2 (0-2).");
		test = new Testable(fileName, 1, 3);
		int[][] populationTest = test.mutation1(1, 0);
		System.out.println("Best: " + test.best);
		System.out.println("Result of Mutation");
		test.printGen(populationTest);
	}

	/**
	 * Swaps the values of two randomly chosen bits. Does not change elite.
	 */
	
	public void mutation2() {// XXX working
		System.out
				.println("Testing Mutation 2. Flip bits in chromosome 2 (0-2).");
		test = new Testable(fileName, 1, 3);
		int[][] populationTest = test.mutation2(2, 0);
		System.out.println("Best: " + test.best);
		System.out.println("Result of Mutation");
		test.printGen(populationTest);
	}

	/**
	 * Double Point Crossover Cross 2 parents at cut points to make 2 new children. 
	 * Avoids overwriting elitism chromosome. (int c1Index, int c2Index, int avoid)
	 */
	
	public void crossOver2() {// XXX working
		System.out.println("Testing Crossover 2.");
		test = new Testable(fileName, 1, 3);
		int[][] populationTest = test.crossOver2(1, 2, test.best);
		System.out.println("Best: " + test.best);
		System.out.println("Result of Crossover");
		test.printGen(populationTest);
	}

	/**
	 * Single Point Crossover Avoids overwriting elitism chromosome. Randomly
	 * chooses cut point and swaps all indices after cut. (int c1Index, int
	 * c2Index, int avoid, int nextAvail)
	 */
	public void crossOver1() {// XXX working
		System.out.println("Testing Crossover 1.");
		test = new Testable(fileName, 1, 3);
		int[][] populationTest = test.crossOver1(1, 2, test.best, 1);
		System.out.println("Best: " + test.best);
		System.out.println("Result of Crossover");
		test.printGen(populationTest);
	}

	/**
	 * Copies the elite chromosome to the next generation. Leaves the rest
	 * untouched. 
	 * Test should result in elite chromosome copied and remainder of
	 * matrix 0.
	 */
	public void elite() {// XXX working
		System.out.println("Testing elite copy.");
		test = new Testable(fileName, 1, 3);
		int[][] eliteTestPost = test.elite(test.best);
		System.out.println("Best: " + test.best);
		System.out.println("Result of elite copy");
		test.printGen(eliteTestPost);
	}

	/**
	 * Fitness of an individual i is calculated by Sij = value of the jth bit
	 * (column) in the ith row * cost of the column.
	 * Also updates the int best to reflect the index of the 
	 * fittest chromosome in population. 
	 * Returns the fitness of the entire population. 
	 * Punishes the infeasibles. 
	 * Trivial fitness test should return [4 5 20 2 10].
	 */
	public void fitEval() { // XXX working
		System.out.println("Testing Fitness Evaluation.");
		test = new Testable(fileName, 1, 5);
		System.out.println("Original Best: " + test.best);
		int[][] populationTest = test.population;
		System.out.println("Result of FitEval");
		test.printGen(populationTest);
		System.out.println("Fitness of the population " + test.fitEval());
		System.out.println("New Best: " + test.best);
		for (int i = 0; i < test.fitness.length; i++) {
			System.out.println(test.fitness[i]);
		}
	}

	/**
	 * Roulette selection fitness maximized
	 */
	
	public void selection1() { // XXX working
		System.out.println("Testing selection1.");
		test = new Testable(fileName, 1, 3);
		System.out.println("Result of selection1 (Roulette Wheel):");
		int populationTest = test.selection1();
		System.out.println("Index of selected chromosome: " + populationTest);
	}
	
	/**
	 * Returns an array marking infeasible solutions. 
	 * For trivial data set should return array: 0 0 1 0 1
	 */
	
	public void checkFeasible(){
		System.out.println("Testing checkFeasible.");
		test = new Testable(fileName, 1, 5);
		int[] result = test.checkFeasible();
		System.out.println("Result");
		test.printFinal();
		for(int i = 0; i < result.length; i++){
			System.out.print(result[i] + ", ");
		}
	}

	/**
	 * Tournament selection. 
	 * Returns index of the winner in population[]
	 */
	
	public void selection2() {// XXX working
		System.out.println("Testing selection2.");
		test = new Testable(fileName, 1, 5);
		System.out.println("Result of selection2 (Tournament):");
		int populationTest = test.selection2();
		System.out.println("index of winner in population: " + populationTest);
	}

	public static void main(String[] args) {
		new UnitTests();
	}
}
