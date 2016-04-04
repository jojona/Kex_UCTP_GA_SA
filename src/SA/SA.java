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

	private double INITIAL_TEMPERATURE;
	private double CONSTANT_MY;
	private int INITIAL_ITERATIONS;
	private int SAMEVALUE_LIMIT = 1000;
	private int DESIRED_FITNESS;

	private int iterations;
	private double temperature;
	public int globalIterations = 0;

	private TimeTable solution = null;
	private TimeTable bestResult;

	public SA() {
		super();
		random = new Random();

	}

	public void defaultSetupGASA(KTH kth, Constraints constraints) {
		this.kth = kth;
		this.constraints = constraints;

		setInitialTemperature(4.3);
		setInitialIterations(10);
		setMy(-6.83E-4);
		setDesiredFitness(0);
		setSameValueLimit(Integer.MAX_VALUE);
	}

	public void defaultSetup(String filename) {
		loadData(filename);
		constraints = new Constraints(kth);
		
		setDesiredFitness(0);
		setInitialTemperature(4.3);
		setInitialIterations(10);
		setMy(-6.83E-4);
		setSameValueLimit(Integer.MAX_VALUE);
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

				int delta = testSolution.getFitness() - solution.getFitness();

				// Interval of delta 0-30
				//System.out.println(delta + " " + temperature + " " + testSolution.getFitness() + " " + solution.getFitness());

				if (delta > 0 || (Math.exp(delta / temperature) > random.nextFloat())) {
					solution = testSolution;
					if (solution.getFitness() > bestResult.getFitness()) {
						bestResult = new TimeTable(solution);
					}
				}
			}

			// Stopping conditions
			int best = bestResult.getFitness();
			if (best == oldBestFitness) {
				if (globalIterations - newBestValueIteration > SAMEVALUE_LIMIT) {
					stop = true;
				} else {
					newBestValueIteration = globalIterations;
				}
			}
			oldBestFitness = best;
			if (bestResult.getFitness() >= DESIRED_FITNESS || System.currentTimeMillis() - startTime > Metaheuristic.TIME_LIMIT) {
				stop = true;
			}
			
			if (Main.debug)
				System.out.println(
						"#GlobalIteration: " + globalIterations + " CURRENT FITNESS: " + bestResult.getFitness());
			nextCoolingstep();

			globalIterations++;
		}
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

	public void setSolution(TimeTable tt) {
		solution = tt;
		constraints.fitness(solution);
	}

	/**
	 * Calculate initial iterations
	 * 
	 * @return
	 */
	public void setInitialIterations(int p) {
		INITIAL_ITERATIONS = p;
	}

	/**
	 * Calculate initial temperature
	 */
	public void setInitialTemperature(double p) {
		INITIAL_TEMPERATURE = p;
	}
	
	public void setMy(double p){
		CONSTANT_MY = p;
	}
	
	public void setDesiredFitness(int p) {
		DESIRED_FITNESS = p;
	}
	
	public void setSameValueLimit(int p) {
		SAMEVALUE_LIMIT = p;
	}

	/**
	 * Neighbourhood search
	 * 
	 * @return next neighbour
	 */
	private TimeTable neighbourSearch(TimeTable tt) {

		// Create a copy
		TimeTable copy = new TimeTable(tt);
		RoomTimeTable[] rttList = copy.getRoomTimeTables();

		// swap(rttList);
		// simpleSearch(rttList);
		simpleAndSwap(rttList);

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
		while (event2 == 0) {
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

	/**
	 * Cooling schedule
	 */
	private void nextCoolingstep() {
		temperature = INITIAL_TEMPERATURE * Math.exp(CONSTANT_MY * globalIterations);
		// iterations = iterations;
	}

	public TimeTable getResult() {
		return bestResult;
	}

	@Override
	public String getConf() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("Initialtemperature: " + INITIAL_TEMPERATURE + "\n");
		sb.append("InitialIterations: " + INITIAL_ITERATIONS + "\n");
		sb.append("Stuck limit: " + SAMEVALUE_LIMIT + "\n");
		sb.append("Desired fitness: " + DESIRED_FITNESS + "\n");
		
		return sb.toString();
	}

}
