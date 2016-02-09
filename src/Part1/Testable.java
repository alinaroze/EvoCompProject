package Part1;

/* 
 * Alina Rozenbaum
 * Evolution Computation Project
 * Problem: Set Covering
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 * Modification of main program for unit testing purposes only
 *
 */

public class Testable {
	String dataFile = "src/Part1/";
	private int[][] matrix;
	int[][] population;
	private int[][] nextGeneration;
	int[] fitness;
	private int populationSize = 5;
	int[] colCost;
	int indexGen = 0;
	double penalty = .4;
	int best; // elitism index
	Random c;

	/*
	 * Main Loop
	 */
	public Testable(String fn, int gens, int pop) {
		dataFile = dataFile + fn;
		populationSize = pop;
		try {
			dataReader();
		} catch (IOException e) {
			e.printStackTrace();
		}
		c = new Random();
		population = new int[populationSize][matrix[0].length];
		nextGeneration = new int[populationSize][matrix[0].length];
		fitness = new int[population.length];
		makePop();

		printGen(population);
	}

	/**
	 * Reads in the data file to be used in the program. Format of Data file:
	 * number of rows (m), number of columns (n) the cost of each column
	 * c(j),j=1,...,n. for each row i (i=1,...,m): the number of columns which
	 * cover row i followed by a list of the columns which cover row i Puts the
	 * data into a 2D matrix
	 * 
	 * @throws IOException
	 */

	public void dataReader() throws IOException {
		StringTokenizer st;
		int rows;
		int cols;
		int coverCols;
		String entireFileText = "";

		try {
			entireFileText = new Scanner(new File(dataFile))
					.useDelimiter("\\A").next();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		st = new StringTokenizer(entireFileText);
		rows = Integer.parseInt(st.nextToken());
		cols = Integer.parseInt(st.nextToken());
		colCost = new int[cols];
		for (int i = 0; i < cols; i++) {// read column costs
			colCost[i] = Integer.parseInt(st.nextToken());
		}
		matrix = new int[rows][cols];
		for (int j = 0; j < rows; j++) { // fill up the 2D matrix
			coverCols = Integer.parseInt(st.nextToken()); // number of columns
															// which cover
			for (int i = 0; i < coverCols; i++) {
				matrix[j][(Integer.parseInt(st.nextToken())) - 1] = 1;
			}
		}
	}

	/**
	 * creates initial population randomly
	 */
	
	public int[][] makePop() {
		int created = 0;
		double colChance = .4; // chance to choose the selected col
		System.out.println("Making the population...");
		Random r = new Random();
		while (created < populationSize) {
			for (int col = 0; col < populationSize; col++) {
				if (r.nextDouble() < colChance) {
					population[created][col] = 1;
				}
			}
			created++;
		}
		return population;

	}

	/**
	 * prints the matrix containing all chromosomes in the current population
	 */
	
	public void printGen(int[][] generation) {
		for (int i = 0; i < populationSize; i++) {
			for (int j = 0; j < matrix[0].length; j++) {
				System.out.print(generation[i][j]);
			}
			System.out.println();
		}
	}

	/**
	 * prints the final population
	 */
	
	public void printFinal() {
		System.out.println("Simulation Complete. Final Population:");
		printGen(population);
		System.out.println("Fitness of Final Population: " + fitEval());
	}

	/**
	 * roulette selection fitness maximized
	 */
	
	public int selection1() {
		double probability[] = new double[fitness.length];
		int[] wheel = new int[fitEval()];
		int loc = 0;
		int slots = 0;
		fitEval();
		for (int i = 0; i < population.length; i++) {
			probability[i] = (((double) fitness[i]) / fitEval());
			System.out.println("Prob " + probability[i]);
		}
		for (int p = 0; p < probability.length; p++) {
			loc += slots;
			slots = (int) (wheel.length * probability[p]);
			for (int w = loc; w < slots + loc; w++) {
				wheel[w] = p;
			}
		}
		Random s = new Random();
		return wheel[s.nextInt(wheel.length)]; // indices of selection from
												// population
	}

	/**
	 * Tournament selection. 
	 * Returns the index of the winner in population[] 
	 * 1/3 of the population competes.
	 */
	
	public int selection2() {
		fitEval();
		Random t = new Random();
		int[] tourney = new int[population.length / 3];
		System.out.println("Tourney participants: ");
		for (int i = 0; i < tourney.length; i++) {
			tourney[i] = t.nextInt(population.length);
			System.out.print(tourney[i]);
		}
		int winner = tourney[0];
		System.out.println();
		for (int c = 0; c < tourney.length; c++) {
			System.out.println("Fitness of tourney index " + c + ": "
					+ fitness[tourney[c]]);
			if (fitness[tourney[c]] < fitness[winner]) {
				winner = tourney[c];
			}
			System.out.println("Fitness of winner: " + fitness[winner]);
		}
		return winner;
	}

	/**
	 * Elitism copies first
	 */
	
	public int[][] elite(int elite) {
		for (int i = 0; i < population[0].length; i++) {
			nextGeneration[0][i] = population[elite][i];
		}
		return nextGeneration;
	}

	/**
	 * Randomly flip a bit in specified chromosome
	 */
	
	public int[][] mutation1(int cIndex, int best) {
		if (cIndex != best) {
			Random r = new Random();
			int random = r.nextInt(population.length);
			if (population[cIndex][random] == 0) // mutate a random gene
				population[cIndex][random] = 1;
			else
				population[cIndex][random] = 0;
		} else
			System.out.println("Attempted to change best...Failed");
		return population;

	}

	/**
	 * Swaps the values of two randomly chosen bits. Does not change elite.
	 */
	
	public int[][] mutation2(int cIndex, int best) {
		if (cIndex != best) {
			Random a = new Random();
			Random b = new Random();
			int randomA = a.nextInt(population[0].length);
			int randomB = randomA;
			while (randomB == randomA) {
				randomB = b.nextInt(population[0].length);
			}
			System.out.println("Flipping bits: " + randomA + ", " + randomB);
			int temp = population[cIndex][randomA];
			population[cIndex][randomA] = population[cIndex][randomB];
			population[cIndex][randomB] = temp;
		} else
			System.out.println("Attempted to change best...Failed");
		return population;

	}

	/**
	 * Double Point Crossover Cross 2 parents at cut points to make 2 new
	 * children. Avoids overwriting elitism chromosome. Static cut locations.
	 */
	
	public int[][] crossOver2(int c1Index, int c2Index, int avoid) {
		if (c1Index != avoid && c2Index != avoid) {
			int cut1 = population[0].length / 3;
			int cut2 = population[0].length / 3 * 2;
			int temp;
			System.out.println("Cross Parents " + c1Index + ", " + c2Index
					+ ".");
			System.out.println("Cut points: " + cut1 + ", " + cut2 + ".");
			for (int i = 0; i < population[0].length; i++) {
				if (i < cut1 || i > cut2) {
					nextGeneration[c1Index][i] = population[c1Index][i];
					nextGeneration[c2Index][i] = population[c2Index][i];
				} else {// cross all between cut1 and cut2 to create
						// children
					temp = population[c1Index][i];
					nextGeneration[c1Index][i] = population[c2Index][i];
					nextGeneration[c2Index][i] = temp;
				}
			}
		} else
			System.out.println("Cannot crossover elite");
		return nextGeneration;
	}

	/**
	 * Single Point Crossover Avoids overwriting elite chromosome. Random cut
	 * point.
	 */
	
	public int[][] crossOver1(int c1Index, int c2Index, int avoid, int nextAvail) {
		if (c1Index != avoid && c2Index != avoid) {
			Random r = new Random();
			int cut1 = r.nextInt(population[0].length);
			System.out.println("Cross Parents " + c1Index + ", " + c2Index
					+ ".");
			System.out.println("Cut point: " + cut1 + ".");
			for (int i = 0; i < population[0].length; i++) {
				if (i < cut1) {
					nextGeneration[nextAvail][i] = population[c1Index][i];
					nextGeneration[nextAvail + 1][i] = population[c2Index][i];
				} else {// cross all between cut1 and cut2 to create
						// children
					nextGeneration[nextAvail][i] = population[c2Index][i];
					nextGeneration[nextAvail + 1][i] = population[c1Index][i];
				}
			}
		} else {
			System.out.println("Cannot crossover elite");
			System.out.println(c1Index + " " + c2Index + " " + avoid);
		}
		return nextGeneration;
	}

	/**
	 * fitness of an individual i is calculated by Sij = value of the jth bit
	 * (column) in the ith row * cost of the column Also updates the int best to
	 * reflect the index of the fittest chromosome in population. Highest Fit is
	 * the lowest cost.
	 */
	
	public int fitEval() {
		int[] infeas = checkFeasible();
		int infeasFactor = 5; // penalize infeasible solutions
		best = 0;
		int fit = 0; // fitness of entire population
		for (int i = 0; i < fitness.length; i++) {
			fitness[i] = 0;
		}
		for (int j = 0; j < population.length; j++) {
			for (int k = 0; k < population[0].length; k++) {
				fitness[j] += colCost[k] * population[j][k];
			}
			if (infeas[j] != 0) {
				fitness[j] = fitness[j] + infeas[j] * infeasFactor;
			}
		}
		for (int b = 0; b < fitness.length; b++) {
			if (fitness[b] < fitness[best] && fitness[b] != 0) {
				best = b;
			}
			fit += fitness[b];
		}
		System.out.println("Print fit eval for testing");
		for (int test = 0; test < fitness.length; test++) {
			System.out.println(fitness[test]);
		}
		return fit;
	}

	public int[] checkFeasible() {
		int feas;
		int[] rowsCov = new int[matrix.length]; // matrix of rows. 1 is covered
												// 0 not each col gets its own
												// matrix
		int[] chromosome;
		int[] infeas = new int[population.length];
		// read from the data matrix
		for (int popRow = 0; popRow < population.length; popRow++) {// iterates
																	// through
																	// population
			feas = 0;
			chromosome = population[popRow];
			for (int covered = 0; covered < chromosome.length; covered++) { // iterates
																			// through
																			// chromosome
																			// bits
				if (chromosome[covered] == 1) { // col at index covered is
												// turned on
					for (int rows = 0; rows < matrix.length; rows++) {// iterates
																		// through
																		// rows
																		// in
																		// matrix
						if (matrix[rows][covered] == 1) { // checks whether the
															// row at the
															// defined col is
															// covered
							rowsCov[rows] = 1; // if covered, adds to covered
												// row array
						}
					}
				}
			}
			for (int addFeas = 0; addFeas < rowsCov.length; addFeas++) { // adds
																			// the
																			// covered
																			// rows
																			// together
				feas += rowsCov[addFeas];
			}
			if (feas != rowsCov.length) { // checks against total number of
											// rows, if doesn't = then not
											// feasible as is not complete cover
				// penalize infeasibles in the fitnsess function
				infeas[popRow] = rowsCov.length - feas;
			}
			for (int clrCov = 0; clrCov < rowsCov.length; clrCov++) { // clears
																		// the
																		// rowsCov
																		// array
																		// for
																		// next
																		// pass
				rowsCov[clrCov] = 0;
			}
		}

		return infeas; // returns a list of the infeasible chromosomes, with
						// number of infeasibles, to the fitness function
	}

	public static void main(String[] args) {
		new EVA();
	}
}
