package Part1;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;
import java.util.StringTokenizer;

/* 
 * Alina Rozenbaum
 * Evolution Computation Project
 * Problem: Set Covering
 */

public class EVA {
	private double crossRate = .70;
	private double mutationRate = .05;
	private int generations = 50;
	private int currGeneration = 0;
	private boolean elitism = true;
	private boolean finished;
	// private String dataFile = "src/Part1/TrivialData";//XXX try other data
	// sets here
	private String dataFile = "src/Part1/SmallData";
	// private String dataFile = "src/Part1/MediumData";
	// private String dataFile = "src/Part1/LargeData";

	private int[][] matrix;
	private int[][] population;
	private int[][] nextGeneration;
	private int[] fitness;
	private int populationSize = 5;
	int[] colCost;
	int indexGen = 0;
	int best; // elitism index
	double average;
	Random c;
	double cost = 0;

	/*
	 * Main Loop
	 */
	
	public EVA() {
		try {
			dataReader();
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (int tries = 0; tries < 5; tries++) {
			c = new Random();
			population = new int[populationSize][matrix[0].length];
			nextGeneration = new int[populationSize][matrix[0].length];
			fitness = new int[population.length];
			makePop();
			System.out.println("Fitness of Initial Population: " + fitEval());
			int p1;
			int p2;
			while (!finished) {
				if (elitism)
					elite(); // protect elite
				for (indexGen = 1; indexGen < population.length - 2; indexGen += 2) {// start
																						// at
																						// 1
																						// to
																						// avoid
																						// overwriting
																						// elite
																						// chromosome
					p1 = selection2();// index of chromosome in pop //XXX change
										// selection here
					p2 = selection2();
					nextGeneration[indexGen] = population[p1];// copy as is
					nextGeneration[indexGen + 1] = population[p2];
					if (c.nextDouble() < crossRate) {
						crossOver1(p1, p2, best, indexGen); // crossover the 2
															// new in next gen
															// if needed //XXX
															// change crossover
															// here
						if (c.nextDouble() < mutationRate) {
							if (c.nextDouble() < mutationRate) {
								mutation2(indexGen, best);// mutate if needed
															// //XXX change
															// mutation here
							}
						}
					}
				}
				fitEval();
				if (currGeneration == generations) {
					finished = true;

					break;
				} else {
					currGeneration++;
				}
			}
			System.out.println("Fitness of chosen: " + fitness[best]);
			System.out.println("Cost of Chosen: " + getCost(population[best]));
			// * 1 1 1 1 0 trivial data for comparison
			// * 1 1 1 1 1
			// * 0 0 1 0 1
			// * 0 1 1 0 0
			// * 0 1 0 0 0
			average += getCost(population[best]);
		} // end repeat constructor twice
		System.out.println("Results average Cost: " + average / 5);
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
		// System.out.println("Making the population...");
		Random r = new Random();
		while (created < populationSize) {
			for (int col = 0; col < matrix[0].length; col++) {
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
	public void printGen() {
		for (int i = 0; i < populationSize; i++) {
			for (int j = 0; j < matrix[0].length; j++) {
				System.out.print(population[i][j]);
			}
			// System.out.println();
		}
	}

	/**
	 * prints the final population
	 */
	public void printFinal() {
		System.out.println("Simulation Complete. Final Population:");
		// printGen();
		System.out.println("Fitness of Final Population: " + fitEval());
	}

	/**
	 * roulette selection fitness maximized
	 */
	public int selection1() {
		double probability[] = new double[fitness.length];
		int fit = fitEval();
		int[] wheel = new int[fit];
		int loc = 0;
		int slots = 0;
		for (int i = 0; i < population.length; i++) {
			probability[i] = ((double) fitness[i] / fit);
			// System.out.println("Prob " + probability[i]);
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
	 * Tournament selection. Returns the index of the winner in population[] 1/3
	 * of the population competes.
	 */
	public int selection2() {
		fitEval();
		Random t = new Random();
		int[] tourney = new int[population.length / 3];
		for (int i = 0; i < tourney.length; i++) {
			tourney[i] = t.nextInt(population.length);
		}
		int winner = tourney[0];
		// System.out.println();
		for (int c = 0; c < tourney.length; c++) {

			if (fitness[tourney[c]] < fitness[winner]) {
				winner = tourney[c];
			}
		}
		return winner;
	}

	/**
	 * elitism copies first
	 */
	public void elite() {
		for (int i = 0; i < population[0].length; i++) {
			nextGeneration[0][i] = population[best][i];
		}
	}

	/**
	 * randomly flip a bit in specified chromosome
	 */
	public void mutation1(int cIndex, int best) {
		if (cIndex != best) {
			Random r = new Random();
			int random = r.nextInt(population.length);
			if (nextGeneration[cIndex][random] == 0) // mutate a random gene
				nextGeneration[cIndex][random] = 1;
			else
				nextGeneration[cIndex][random] = 0;
		} else
			System.out.println("Attempted to change best...Failed");

	}

	/**
	 * swaps the values of two randomly chosen bits.
	 * 
	 */
	public void mutation2(int cIndex, int best) {
		if (cIndex != best) {
			Random a = new Random();
			Random b = new Random();
			int randomA = a.nextInt(nextGeneration[0].length);
			int randomB = randomA;
			while (randomB == randomA) {
				randomB = b.nextInt(nextGeneration[0].length);
			}
			// System.out.println("Flipping bits: " + randomA + ", " + randomB);
			int temp = nextGeneration[cIndex][randomA];
			nextGeneration[cIndex][randomA] = nextGeneration[cIndex][randomB];
			nextGeneration[cIndex][randomB] = temp;
		} else
			System.out.println("Attempted to change best...Failed");
	}

	/**
	 * Double Point Crossover Cross 2 parents at cut points to make 2 new
	 * children. Avoids overwriting elitism chromosome.
	 */
	public int[][] crossOver2(int c1Index, int c2Index, int avoid, int place) {
		int cut1 = population[0].length / 3;
		int cut2 = population[0].length / 3 * 2;
		int temp;
		for (int i = 0; i < population[0].length; i++) {
			if (i < cut1 || i > cut2) {
				nextGeneration[place][i] = population[c1Index][i];
				nextGeneration[place + 1][i] = population[c2Index][i];
			} else {// cross all between cut1 and cut2 to create
					// children
				temp = population[c1Index][i];
				nextGeneration[place][i] = population[c2Index][i];
				nextGeneration[place + 1][i] = temp;
			}
		}
		return nextGeneration;
	}

	/**
	 * Single Point Crossover Avoids overwriting elitism chromosome.
	 */
	public int[][] crossOver1(int c1Index, int c2Index, int avoid, int nextAvail) {
		Random r = new Random();
		int cut1 = r.nextInt(population[0].length);
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
		return nextGeneration;
	}

	/**
	 * fitness of an individual i is calculated by Sij = value of the jth bit
	 * (column) in the ith row * cost of the column. If chromosome is
	 * infeasible, multiplies the fitness by an infeasible factor*number of rows
	 * not covered. Also updates the int best to reflect the index of the
	 * fittest chromosome in population. returns fitness of population
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
			if (fitness[b] < fitness[best] && fitness[b] != 0 && infeas[b] == 0) {// only
																					// feasible
																					// soln's
																					// can
																					// be
																					// best
				best = b;
			}
			fit += fitness[b];
		}
		// System.out.println("Print fit eval for testing");
		// for(int test = 0; test < fitness.length; test++){
		// System.out.println(fitness[test]);
		// }
		return fit;
	}

	/**
	 * Checks for feasibiltiy of chromosome.
	 * Are all rows covered?
	 * 
	 * @return
	 */
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
				// penalize infeasibles in the fitness function
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

	public int[][] getMatrix() {
		return matrix;
	}

	public int[] getFitness() {
		return fitness;
	}

	public double getCost(int[] chro) {
		double cost = 0;
		for (int j = 0; j < matrix.length; j++) {
			for (int k = 0; k < matrix[0].length; k++) {
				if (chro[k] == 1) {
					cost += matrix[j][k];
				}
			}
		}

		return cost;
	}

	public static void main(String[] args) {

		new EVA();
	}
}
