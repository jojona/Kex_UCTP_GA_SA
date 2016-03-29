package SA;

import java.util.Map;
import java.util.Random;

import GA.src.Population;
import main.Constraints;
import main.KTH;
import main.Metaheuristic;
import main.Room;
import main.RoomTimeTable;
import main.TimeTable;

public class SA extends Metaheuristic {
	
	private Random random;
	
	public double INITIAL_TEMPERATURE = 4.3;
	public double CONSTANT_MY = -6.83E-4; // TODO find correct values
	public int INITIAL_ITERATIONS = 100;
	
	private int iterations;
	private double temperature;
	private int GLOBAL_ITERATIONS_MAX = 10000;
	private int globalIterations = 0;
	
	private TimeTable solution;
	private Constraints constraints;
	private KTH kth;
	
	public SA() {
		super();
		random = new Random(1565161515l);
	
		// TODO select cooling schedule
	}
	
	public void run(String filename) {
		
		loadData(filename);
		solution = initialSolution();
		temperature = initialTemperature();
		iterations = initialIterations();
		
		constraints.fitness(solution);
		
		boolean stop = false;
		while(!stop) {
			for(int i = 0; i < iterations; i++) {
				TimeTable currentSolution = neighbourSearch(solution);
				constraints.fitness(currentSolution);
				
				int delta = currentSolution.getFitness() - solution.getFitness();
				
				// TODO see interval of delta ANSWER 0-30
				System.out.println(delta + " " + temperature + " " + currentSolution.getFitness() + " " + solution.getFitness());
				
				if (delta > 0 || (Math.exp(delta/temperature) > random.nextFloat())) {
					solution = currentSolution;
				}
			}
			nextCoolingstep();	
			
			if (globalIterations == GLOBAL_ITERATIONS_MAX || solution.getFitness() == 0) {
				stop = true;
			}
			globalIterations++;
		}
		
		System.out.println("End");
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
	private double initialTemperature() {
		return INITIAL_TEMPERATURE;
	}
	
	/**
	 * Neighbourhood search
	 * @return next neighbour
	 */
	private TimeTable neighbourSearch(TimeTable tt) {
		
		// Create a copy
		TimeTable copy= new TimeTable(tt);
		RoomTimeTable[] rttList = copy.getRoomTimeTables();
		
		
		//swap(rttList);
		//simpleSearch(rttList);
		simpleAndSwap(rttList);
		
		return copy;
	}
	
	private void swap(RoomTimeTable[] rttList) {
		int timeslot1 = random.nextInt(RoomTimeTable.NUM_TIMESLOTS);
		int day1 = random.nextInt(RoomTimeTable.NUM_DAYS);
		int room1 = random.nextInt(rttList.length);
		int event1 = rttList[room1].getEvent(day1, timeslot1);
		while(event1 == 0) {
			timeslot1 = random.nextInt(RoomTimeTable.NUM_TIMESLOTS);
			day1 = random.nextInt(RoomTimeTable.NUM_DAYS);
			room1 = random.nextInt(rttList.length);
			event1 = rttList[room1].getEvent(day1, timeslot1);
		}
		
		int timeslot2 = random.nextInt(RoomTimeTable.NUM_TIMESLOTS);
		int day2 = random.nextInt(RoomTimeTable.NUM_DAYS);
		int room2 = random.nextInt(rttList.length);
		int event2 = rttList[room2].getEvent(day2, timeslot2);
		while(event2 == 0) {
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
		while(event1 == 0) { 
			timeslot1 = random.nextInt(RoomTimeTable.NUM_TIMESLOTS);
			day1 = random.nextInt(RoomTimeTable.NUM_DAYS);
			room1 = random.nextInt(rttList.length);
			event1 = rttList[room1].getEvent(day1, timeslot1);
		}
		
		int timeslot2 = random.nextInt(RoomTimeTable.NUM_TIMESLOTS);
		int day2 = random.nextInt(RoomTimeTable.NUM_DAYS);
		int room2 = random.nextInt(rttList.length);
		int event2 = rttList[room2].getEvent(day2, timeslot2);
		while(event2 != 0) {
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
		while(event1 == 0) {
			timeslot1 = random.nextInt(RoomTimeTable.NUM_TIMESLOTS);
			day1 = random.nextInt(RoomTimeTable.NUM_DAYS);
			room1 = random.nextInt(rttList.length);
			event1 = rttList[room1].getEvent(day1, timeslot1);
		}
		int timeslot2 = random.nextInt(RoomTimeTable.NUM_TIMESLOTS);
		int day2 = random.nextInt(RoomTimeTable.NUM_DAYS);
		int room2 = random.nextInt(rttList.length);
		int event2 = rttList[room2].getEvent(day2, timeslot2);
		while(event2 == 0) {
			timeslot2 = random.nextInt(RoomTimeTable.NUM_TIMESLOTS);
			day2 = random.nextInt(RoomTimeTable.NUM_DAYS);
			room2 = random.nextInt(rttList.length);
			event2 = rttList[room2].getEvent(day2, timeslot2);
		}
		
		int timeslot3 = random.nextInt(RoomTimeTable.NUM_TIMESLOTS);
		int day3 = random.nextInt(RoomTimeTable.NUM_DAYS);
		int room3 = random.nextInt(rttList.length);
		int event3 = rttList[room3].getEvent(day3, timeslot3);
		while(event3 != 0) {
			timeslot3 = random.nextInt(RoomTimeTable.NUM_TIMESLOTS);
			day3 = random.nextInt(RoomTimeTable.NUM_DAYS);
			room3 = random.nextInt(rttList.length);
			event3 = rttList[room3].getEvent(day3, timeslot3);
		}
		int timeslot4 = random.nextInt(RoomTimeTable.NUM_TIMESLOTS);
		int day4 = random.nextInt(RoomTimeTable.NUM_DAYS);
		int room4 = random.nextInt(rttList.length);
		int event4 = rttList[room4].getEvent(day4, timeslot4);
		while(event4 != 0) {
			timeslot4 = random.nextInt(RoomTimeTable.NUM_TIMESLOTS);
			day4 = random.nextInt(RoomTimeTable.NUM_DAYS);
			room4 = random.nextInt(rttList.length);
			event4 = rttList[room4].getEvent(day4, timeslot4);
		}
		// Remove events
		rttList[room1].setEvent(day1, timeslot1, 0);
		rttList[room2].setEvent(day2, timeslot2, 0);
		
		// Put them back at new timeslots
		rttList[room3].setEvent(day3, timeslot3, 1);
		rttList[room4].setEvent(day4, timeslot4, 2);
		
	}
	
	/**
	 * Cooling schedule
	 */
	private void nextCoolingstep() {
		temperature = INITIAL_TEMPERATURE * Math.exp(CONSTANT_MY * globalIterations);
		//iterations = iterations;
	}
	

}
