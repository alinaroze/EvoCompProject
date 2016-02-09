package Part3;

/*
 * Alina Rozenbaum
 * Evolutionary Computation Project
 * Problem: Set Covering
 */

import java.util.Random;

import Part1.EVA;

public class FoolishClimber {

	private double lastEnergy;
	private Random random;
	private int temperature;
	private double energy;
	private int[] chromosome;
	private int[] lastChromosome;
	int[][] pop;
	EVA eva = new EVA();
	int counter;
	int maxCounter = 5;
	double bestCost;
	int[] bestChro;
	boolean accept = false;
	double beta = .2;
	int indexInPop; // which chromosome are we using
	int[][] matrix;
	int iterations = 0;
	double averageCost = 0;
	
	public FoolishClimber() {
		random = new Random();

		temperature = 1000;
		pop = eva.makePop();
		matrix = eva.getMatrix();
		indexInPop = random.nextInt(pop.length);
		chromosome = pop[indexInPop]; // randomly created
										// chromosome
		bestChro = new int[chromosome.length];
		bestCost = 0;
		counter = 0;
		System.out.print("Initial chromosome: ");
		for (int i = 0; i < chromosome.length; i++) {
			System.out.print(" " + chromosome[i]);
		}
		System.out.println();
		System.out.println("Initial Cost: " + getCost(chromosome));
		search();
		System.out.print("Result chromosome: ");
		for (int i = 0; i < bestChro.length; i++) {
			System.out.print(" " + bestChro[i]);
		}
		System.out.println();
		System.out.println("Result Cost: " + fitEval(chromosome));
		System.out.println("Average Cost: " + averageCost / iterations);
	}

	/**
	 * iterates through the search space to find the best chromosome
	 * 
	 * @param iterations
	 */
	public void search() {
		bestCost = getCost(chromosome);
		// bestChro = chromosome;
		while (temperature > 0) {
//			System.out.println("Temp: " + temperature);
			counter = 0;
			while (counter < maxCounter) {
				iterations++;
				averageCost += getCost(chromosome);
				accept = false;
				// System.out.println("Counter: " + counter);
				int[] temp = chromosome;
				lastChromosome = chromosome;

				lastEnergy = getCost(lastChromosome);

				chromosome = perturb(lastChromosome); // modifies chromosome

				energy = getCost(chromosome);

				if (energy <= lastEnergy) {
					lastEnergy = energy;
					accept = true;

				} else {// undo
					chromosome = lastChromosome;
					energy = lastEnergy;
				}

				if (accept) {
					if (energy < bestCost) {
						counter = 0;
						bestCost = energy;
						for (int i = 0; i < chromosome.length; i++) {
							bestChro[i] = chromosome[i];
						}
					} else {
						counter++;
					}
				}
			}
			temperature = (int) (temperature - temperature * beta);
		}
		chromosome = bestChro;
	}

	/**
	 * Randomly adds or removes a column from current chromosome by flipping a
	 * bit 50/50 chance to add or remove. 
	 * Will always change chromosome.
	 */
	public int[] perturb(int[] chro) {
		int r;
		int choice = 1;
		int unstick = 0;
		for (int stuck = 0; stuck < chro.length; stuck++) {
			unstick += chro[stuck];
		}
		if (unstick == 0) {
			choice = 1;
		} else if (unstick == chro.length) {
			choice = 2;
		} else if (random.nextDouble() > .5) { // 50/50 add or remove column
			choice = 2;
		}
		boolean added = false;
		switch (choice) {
		case 1:
			while (!added) {
				r = random.nextInt(chro.length);
				if (chro[r] == 0) {
					chro[r] = 1; // add a column
					added = true;
				}
			}
			break;
		case 2:
			while (!added) {
				r = random.nextInt(chro.length);
				if (chro[r] == 1) {
					chro[r] = 0; // remove a column
					added = true;
				}
			}
			break;
		}
		return chro;
	}

	public double getCost(int[] chro) {
		double cost = fitEval(chro);
		return cost;
	}

	/**
	 * Fitness of an individual chromosome
	 * 
	 * @param chromosome
	 *            []
	 * @return int fit
	 */
	public double fitEval(int[] chro) {
		int infeas = checkFeasible(chro);
		int infeasFactor = 2; // penalize infeasible solutions
		double fit = 0;

		for (int j = 0; j < matrix.length; j++) {
			for (int k = 0; k < matrix[0].length; k++) {
				if (chro[k] == 1) {
					fit += 1 * matrix[j][k];
				}
			}
			if (infeas != 0) {
				fit = fit + infeas * infeasFactor;
			}
		}

		return fit;
	}

	/**
	 * Checks feasibility of individual chromosome so fiteval can penalize
	 * infeasibles.
	 * 
	 * @param chro
	 * @return
	 */
	public int checkFeasible(int[] chro) {
		int feas;
		int[] rowsCov = new int[matrix.length]; // matrix of rows. 1 is covered
												// 0 not each col gets its own
												// matrix
		int[] chromosome;
		int infeas = 0;
		feas = 0;
		chromosome = chro;
		for (int covered = 0; covered < chromosome.length; covered++) { // iterates
																		// through
																		// chromosome
																		// bits
			if (chromosome[covered] == 1) { // col at index covered is turned on
				for (int rows = 0; rows < matrix.length; rows++) {// iterates
																	// through
																	// rows in
																	// matrix
					if (matrix[rows][covered] == 1) { // checks whether the row
														// at the defined col is
														// covered
						rowsCov[rows] = 1; // if covered, adds to covered row
											// array
					}
				}
			}
		}
		for (int addFeas = 0; addFeas < rowsCov.length; addFeas++) { // adds the
																		// covered
																		// rows
																		// together
			feas += rowsCov[addFeas];
		}
		if (feas != rowsCov.length) { // checks against total number of rows, if
										// not = then not feasible as is not
										// complete cover
			// penalize infeasibles in the fitness function
			infeas = rowsCov.length - feas;
		}
		return infeas; // returns number of infeasibles to the fitness function
	}

	public static void main(String[] args) {
		new FoolishClimber();
	}
}
