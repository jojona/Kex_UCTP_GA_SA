package SA;

import java.util.Map;
import java.util.Random;

import GA.src.Population;
import main.Constraints;
import main.KTH;
import main.Main;
import main.Metaheuristic;
import main.Room;
import main.RoomTimeTable;
import main.TimeTable;

public class SA extends Metaheuristic {

	private Random random;

	public double avgDelta = -4.8;
	public int iterationGoal = 20000;

	private int SAMEVALUE_LIMIT = Integer.MAX_VALUE;
	
	// Always set desired fitness and time limit before running
	private int DESIRED_FITNESS = Integer.MAX_VALUE; 
	private int TIME_LIMIT = 0;

	private int INITIAL_ITERATIONS;
	private double INITIAL_TEMPERATURE;
	private double CONSTANT_MY;

	private double temperature;
	private int iterations;
	public int globalIterations = 0;
	private int neighbour_i = 0;

	private TimeTable solution = null;
	private TimeTable bestResult;

	public SA() {
		super();
		random = new Random();
	}

	//////////////////////////
	// Setup functions
	//////////////////////////

	public void defaultSetupGASA(KTH kth, Constraints constraints) {
		this.kth = kth;
		this.constraints = constraints;

		setInitialIterations(10);

		// TODO
		setInitialTemperature(0.69487);
		setMy(-1.4384E-5);
	}

	public void defaultSetup(String filename) {
		loadData(filename);
		constraints = new Constraints(kth);

		setInitialIterations(10);

		// TODO
		setInitialTemperature(0.69487);
		setMy(-1.4384E-5);
	}

	/**
	 * Calculate initial solution
	 * 
	 * @return
	 */
	private TimeTable initialSolution() {
		Population p = new Population();
		Map<Integer, Room> rooms = kth.getRooms();
		int numRooms = kth.getRooms().size();
		TimeTable invidual = p.generateRandomInvidual(kth, rooms, numRooms);
		constraints.fitness(invidual);
		return invidual;
	}

	//////////////////////////
	// Main algorithm functions
	//////////////////////////

	public void testDelta() {
		solution = initialSolution();
		int amount = 0;
		int sum = 0;
		for (int i = 0; i < 10000; i++) {
			TimeTable testSolution = neighbourSearch(solution);
			constraints.fitness(testSolution);

			int softdelta = constraints.softConstraints(testSolution) * -1 - constraints.softConstraints(solution) * -1;
			if (softdelta < 0) {
				sum += softdelta;
				amount++;
			}

			solution = testSolution;
		}

		System.out.println("Result: " + 1.0 * sum / amount);
		System.out.println("Amount: " + amount);
	}

	public void run(long startTime) {
		temperature = INITIAL_TEMPERATURE;
		iterations = INITIAL_ITERATIONS;

		if (solution == null) {
			solution = initialSolution();
		}
		bestResult = new TimeTable(solution);
		int oldBestFitness = solution.getFitness();
		int newBestValueIteration = 0;

		boolean stop = false;
		while (!stop) {
			for (int i = 0; i < iterations; i++) {
				TimeTable testSolution = neighbourSearch(solution);
				constraints.fitness(testSolution);

				// delta ~= -4 (soft constraints)
				int delta = testSolution.getFitness() - solution.getFitness();

				/*
				 * System.out.print(temperature + " \t" + solution.getFitness()
				 * + "  \t" + testSolution.getFitness() + "   \t" +
				 * globalIterations + " \t" + delta); if (delta > 0) {
				 * System.out.println(" \tPositive"); } else {
				 * System.out.println(); } //
				 */

				if (delta >= 0 || (Math.exp(delta / temperature) > random.nextFloat())) {

					// if (delta < 0) {
					// System.out.println(delta);
					// }

					solution = testSolution;
					if (solution.getFitness() > bestResult.getFitness()) {
						bestResult = new TimeTable(solution);
					}
				}
			}

			// System.out.println(bestResult.getFitness());

			// Stopping conditions
			int best = bestResult.getFitness();
			if (best == oldBestFitness) {
				if (globalIterations - newBestValueIteration > SAMEVALUE_LIMIT) {
					stop = true;
				}
			} else {
				newBestValueIteration = globalIterations;
				oldBestFitness = best;
			}

			if (bestResult.getFitness() >= DESIRED_FITNESS || System.currentTimeMillis() - startTime > TIME_LIMIT) {
				stop = true;
			}

			if (Main.debug && globalIterations%5==0)
				System.out.println(
						"#GlobalIteration: " + globalIterations + " CURRENT FITNESS: " + bestResult.getFitness());

			nextCoolingstep(globalIterations);
			globalIterations++;
		}
	}

	/**
	 * Cooling schedule
	 */
	private void nextCoolingstep(int iter) {
		temperature = INITIAL_TEMPERATURE * Math.exp(CONSTANT_MY * iter);
		// temperature *= 0.97;
	}

	//////////////////////////
	// Neighbourhood search methods
	//////////////////////////

	/**
	 * Neighbourhood search
	 * 
	 * @return next neighbour
	 */
	private TimeTable neighbourSearch(TimeTable tt) {

		// Create a copy
		TimeTable copy = new TimeTable(tt);
		RoomTimeTable[] rttList = copy.getRoomTimeTables();

		if (neighbour_i == 0) {
			simpleSearch(rttList);
			neighbour_i++;
		} else if (neighbour_i == 1) {
			swap(rttList);
			neighbour_i++;
		} else {
			simpleAndSwap(rttList);
			neighbour_i = 1;
		}

		return copy;
	}

	private void swap(RoomTimeTable[] rttList) {
		int timeslot1 = random.nextInt(RoomTimeTable.NUM_TIMESLOTS);
		int day1 = random.nextInt(RoomTimeTable.NUM_DAYS);
		int room1 = random.nextInt(rttList.length);
		int event1 = rttList[room1].getEvent(day1, timeslot1);
		while (event1 == 0) {
			timeslot1 = random.nextInt(RoomTimeTable.NUM_TIMESLOTS);
			day1 = random.nextInt(RoomTimeTable.NUM_DAYS);
			room1 = random.nextInt(rttList.length);
			event1 = rttList[room1].getEvent(day1, timeslot1);
		}

		int timeslot2 = random.nextInt(RoomTimeTable.NUM_TIMESLOTS);
		int day2 = random.nextInt(RoomTimeTable.NUM_DAYS);
		int room2 = random.nextInt(rttList.length);
		int event2 = rttList[room2].getEvent(day2, timeslot2);
		while (event2 == 0 || event2 == event1) {
			timeslot2 = random.nextInt(RoomTimeTable.NUM_TIMESLOTS);
			day2 = random.nextInt(RoomTimeTable.NUM_DAYS);
			room2 = random.nextInt(rttList.length);
			event2 = rttList[room2].getEvent(day2, timeslot2);
		}

		// Swap events
		rttList[room1].setEvent(day1, timeslot1, event2);
		rttList[room2].setEvent(day2, timeslot2, event1);
	}

	private void simpleSearch(RoomTimeTable[] rttList) {
		int timeslot1 = random.nextInt(RoomTimeTable.NUM_TIMESLOTS);
		int day1 = random.nextInt(RoomTimeTable.NUM_DAYS);
		int room1 = random.nextInt(rttList.length);
		int event1 = rttList[room1].getEvent(day1, timeslot1);
		while (event1 == 0) {
			timeslot1 = random.nextInt(RoomTimeTable.NUM_TIMESLOTS);
			day1 = random.nextInt(RoomTimeTable.NUM_DAYS);
			room1 = random.nextInt(rttList.length);
			event1 = rttList[room1].getEvent(day1, timeslot1);
		}

		int timeslot2 = random.nextInt(RoomTimeTable.NUM_TIMESLOTS);
		int day2 = random.nextInt(RoomTimeTable.NUM_DAYS);
		int room2 = random.nextInt(rttList.length);
		int event2 = rttList[room2].getEvent(day2, timeslot2);
		while (event2 != 0) {
			timeslot2 = random.nextInt(RoomTimeTable.NUM_TIMESLOTS);
			day2 = random.nextInt(RoomTimeTable.NUM_DAYS);
			room2 = random.nextInt(rttList.length);
			event2 = rttList[room2].getEvent(day2, timeslot2);
		}

		// Remove event1 from time1
		rttList[room1].setEvent(day1, timeslot1, 0);
		// Set event1 to time2
		rttList[room2].setEvent(day2, timeslot2, event1);
	}

	void simpleAndSwap(RoomTimeTable[] rttList) {
		int timeslot1 = random.nextInt(RoomTimeTable.NUM_TIMESLOTS);
		int day1 = random.nextInt(RoomTimeTable.NUM_DAYS);
		int room1 = random.nextInt(rttList.length);
		int event1 = rttList[room1].getEvent(day1, timeslot1);
		while (event1 == 0) {
			timeslot1 = random.nextInt(RoomTimeTable.NUM_TIMESLOTS);
			day1 = random.nextInt(RoomTimeTable.NUM_DAYS);
			room1 = random.nextInt(rttList.length);
			event1 = rttList[room1].getEvent(day1, timeslot1);
		}
		int timeslot2 = random.nextInt(RoomTimeTable.NUM_TIMESLOTS);
		int day2 = random.nextInt(RoomTimeTable.NUM_DAYS);
		int room2 = random.nextInt(rttList.length);
		int event2 = rttList[room2].getEvent(day2, timeslot2);
		while (event2 == 0 || event1 == event2) {
			timeslot2 = random.nextInt(RoomTimeTable.NUM_TIMESLOTS);
			day2 = random.nextInt(RoomTimeTable.NUM_DAYS);
			room2 = random.nextInt(rttList.length);
			event2 = rttList[room2].getEvent(day2, timeslot2);
		}

		int timeslot3 = random.nextInt(RoomTimeTable.NUM_TIMESLOTS);
		int day3 = random.nextInt(RoomTimeTable.NUM_DAYS);
		int room3 = random.nextInt(rttList.length);
		int event3 = rttList[room3].getEvent(day3, timeslot3);
		while (event3 != 0) {
			timeslot3 = random.nextInt(RoomTimeTable.NUM_TIMESLOTS);
			day3 = random.nextInt(RoomTimeTable.NUM_DAYS);
			room3 = random.nextInt(rttList.length);
			event3 = rttList[room3].getEvent(day3, timeslot3);
		}
		int timeslot4 = random.nextInt(RoomTimeTable.NUM_TIMESLOTS);
		int day4 = random.nextInt(RoomTimeTable.NUM_DAYS);
		int room4 = random.nextInt(rttList.length);
		int event4 = rttList[room4].getEvent(day4, timeslot4);
		int i = 0;
		while (event4 != 0 || (timeslot3 == timeslot4 && day3 == day4 && room3 == room4)) {
			i++;
			timeslot4 = random.nextInt(RoomTimeTable.NUM_TIMESLOTS);
			day4 = random.nextInt(RoomTimeTable.NUM_DAYS);
			room4 = random.nextInt(rttList.length);
			event4 = rttList[room4].getEvent(day4, timeslot4);
			if (i > 10) {
				return; // TODO add to all similar while loops or find better
						// solution
			}
		}
		// Remove events
		rttList[room1].setEvent(day1, timeslot1, 0);
		rttList[room2].setEvent(day2, timeslot2, 0);

		// Put them back at new timeslots
		rttList[room3].setEvent(day3, timeslot3, event1);
		rttList[room4].setEvent(day4, timeslot4, event2);

	}

	//////////////////////////
	// Get and set methods
	//////////////////////////

	public TimeTable getResult() {
		return bestResult;
	}

	@Override
	public String getConf() {
		StringBuilder sb = new StringBuilder();

		sb.append("Initialtemperature: " + INITIAL_TEMPERATURE + "\n");
		sb.append("InitialIterations: " + INITIAL_ITERATIONS + "\n");
		sb.append("My value: " + CONSTANT_MY + "\n");
		sb.append("Stuck limit: " + SAMEVALUE_LIMIT + "\n");
		sb.append("Desired fitness: " + DESIRED_FITNESS + "\n");
		sb.append("Time limit: " + TIME_LIMIT + "\n");
		return sb.toString();
	}

	public double getInitialTemp() {
		return INITIAL_TEMPERATURE;
	}

	public double getMy() {
		return CONSTANT_MY;
	}
	
	public int getInitialIterations() {
		return INITIAL_ITERATIONS;
	}

	/**
	 * Set initial solution, fed from previous algorithm
	 * 
	 * @param tt
	 */
	public void setSolution(TimeTable tt) {
		solution = tt;
		constraints.fitness(solution);
	}

	/**
	 * Set initial iterations
	 */
	public void setInitialIterations(int p) {
		INITIAL_ITERATIONS = p;
	}

	/**
	 * Set initial temperature
	 */
	public void setInitialTemperature(double p) {
		INITIAL_TEMPERATURE = p;
	}

	/**
	 * Set my value
	 */
	public void setMy(double p) {
		CONSTANT_MY = p;
	}

	/**
	 * Set fitness stopping condition
	 */
	public void setDesiredFitness(int p) {
		DESIRED_FITNESS = p;
	}

	/**
	 * Set stuck stopping condition
	 */
	public void setSameValueLimit(int p) {
		SAMEVALUE_LIMIT = p;
	}

	public void setTimeLimit(int t) {
		TIME_LIMIT = t;
	}

}
