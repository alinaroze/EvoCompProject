package Part2;

/* 
 * Alina Rozenbaum
 * Evolution Computation Project
 * Problem: Set Covering
 */

/**
 * This class is for testing purposes and is run on the Testable class rather than EVA.
 * Each method may be tested individually here on the trivial data set using the constructor
 */
public class UnitTests {
	SimulatedAnnealing test;
	String fileName = "TrivialData";
	
	/**
	 * Constructor. 
	 * Un-comment the method you wish to test.
	 */
	public UnitTests() {

		fitEval();
//		System.out.println();
//		checkFeasible();
	}


	/**
	 */
	public void fitEval() { // XXX working
		System.out.println("Testing Fitness Evaluation.");
		test = new SimulatedAnnealing();
		System.out.println("Original Best: " + test.bestCost);
		int[] chromosomeTest = test.pop[3];
		System.out.println("Result of FitEval");
		System.out.println("New Best: " + test.bestCost);
		System.out.println("Fitness: " + test.fitEval(chromosomeTest));
	}

	
	/**
	 * returns an array marking infeasible solutions. 
	 * For trivial data set should return array: 0 0 1 0 1
	 */
	
//	public void checkFeasible(){
//		System.out.println("Testing checkFeasible.");
//		test = new SimulatedAnnealing(fileName, 1, 5);
//		int[] result = test.checkFeasible();
//		System.out.println("Result");
//		test.printFinal();
//		for(int i = 0; i < result.length; i++){
//			System.out.print(result[i] + ", ");
//		}
//	}

	

	public static void main(String[] args) {
		new UnitTests();
	}
}
