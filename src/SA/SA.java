package SA;

import java.util.Map;
import java.util.Random;

import GA.src.Population;
import main.Constraints;
import main.KTH;
import main.Metaheuristic;
import main.Room;
import main.TimeTable;

public class SA extends Metaheuristic {
	
	private Random random;
	
	public int INITIAL_TEMPERATURE = 90;
	public int INITIAL_ITERATIONS = 10;
	
	private int iterations;
	private int temperature;
	
	private TimeTable solution;
	private Constraints constraints;
	private KTH kth;
	
	public SA() {
		super();
		random = new Random(1565161515l);
		
		solution = initialSolution();
		temperature = initialTemperature();
		iterations = initialIterations();
		
		// TODO select cooling schedule
		
		
	}
	
	public void run(String filename) {
		
		loadData(filename);
		
		boolean stop = false;
		while(!stop) {
			for(int i = 0; i < iterations; i++) {
				TimeTable currentSolution = neighbourSearch(solution);
				constraints.fitness(currentSolution);
				
				int delta = currentSolution.getFitness() - solution.getFitness();
				
				// TODO see interval of delta
				
				if (delta < 0 || (Math.exp(-delta/temperature) > random.nextFloat())) {
					solution = currentSolution;
				}
			}
			nextCoolingstep();	
		}
	}
	
	/**
	 * Calculate initial solution
	 * @return
	 */
	private TimeTable initialSolution() {
		Population p = new Population();
		Map<Integer, Room> rooms = kth.getRooms();
	    int numRooms = kth.getRooms().size();
	    return p.generateRandomInvidual(kth, rooms, numRooms);
	}
	
	/**
	 * Calculate initial iterations
	 * @return
	 */
	private int initialIterations() {
		return INITIAL_ITERATIONS;
	}
	
	/**
	 * Calculate initial temperature
	 */
	private int initialTemperature() {
		return INITIAL_TEMPERATURE;
	}
	
	/**
	 * Neighbourhood search
	 * @return next neighbour
	 */
	private TimeTable neighbourSearch(TimeTable tt) {
		
		// Create a copy
		
		// Get random timeslots
		
		// Get Random events
		
		// Swap
		
		return tt;
	}
	
	/**
	 * Cooling schedule
	 */
	private void nextCoolingstep() {
		temperature = temperature - 1;
		iterations = iterations;
	}
	

}
