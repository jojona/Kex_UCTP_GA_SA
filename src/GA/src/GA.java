package GA.src;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

import SA.SA;
import main.Constraints;
import main.Main;
import main.Metaheuristic;
import main.RoomTimeTable;
import main.TimeTable;

/**
 * Performs the Genetic Algorithm(GA) on the KTH data set.
 */
public class GA extends Metaheuristic {

	public int numGenerations = 0;

	// algorithm parameters
	private int SAMEVALUE_LIMIT = Integer.MAX_VALUE;
	
	// Always set desired fitness and time limit before running
	private int DESIRED_FITNESS = Integer.MAX_VALUE; 
	private int TIME_LIMIT = 0;

	private int MAX_POPULATION_SIZE;
	private int MUTATION_PROBABILITY; // compared with 1000
	private int SELECTION_SIZE;
	
	private Population population;
	Random rand;

	public GA() {
		super();
		rand = new Random();
	}

	public void defaultSetup(String filename) {
		loadData(filename);
		constraints = new Constraints(kth);

		// setup the genetic algorithm
		// TODO
		setMutationProbability(40); // 60
		setPopulationSize(100); // 100
		setSelectionSize(50); // XX TODO
	}

	/*
	 * Returns a schedule based on the given constraints
	 */
	public TimeTable generateTimeTable(long startTime) {
		// create the initial random population
		createRandomPopulation();
		ListIterator<TimeTable> it = population.listIterator();
		while (it.hasNext()) {
			TimeTable tt = it.next();
			constraints.fitness(tt);
		}

		population.sortIndividuals();

		boolean stop = false;
		int oldBestFitness = Integer.MIN_VALUE;
		int newBestValueGeneration = 0;

		numGenerations = 1;
		while (!stop) {
			Population children = breed(population, SELECTION_SIZE);
			population = selection(population, children);

			// sort the population by their fitness (not needed?)
			population.sortIndividuals();
			numGenerations++;

			// Stopping conditions
			if ((System.currentTimeMillis() - startTime > TIME_LIMIT)
					|| (population.getTopIndividual().getFitness() >= DESIRED_FITNESS)) {
				stop = true;
			}
			int bestFitness = population.getTopIndividual().getFitness();
			if (bestFitness == oldBestFitness) {
				if (numGenerations - newBestValueGeneration > SAMEVALUE_LIMIT) {
					stop = true;
				}
			} else {
				if (bestFitness > oldBestFitness) {
					population.getTopIndividual().setCreatedTime();
					newBestValueGeneration = numGenerations;
					oldBestFitness = bestFitness;
				}
			}

			if (Main.debug && numGenerations%5==0)
				System.out.println("#GENERATIONS: " + numGenerations + " BEST FITNESS: "
						+ population.getTopIndividual().getFitness());
		}

		return population.getTopIndividual();
	}

	//////////////////////////
	// GENETIC ALGORITHMS
	//////////////////////////

	private Population createRandomPopulation() {
		population = new Population();
		population.createRandomIndividuals(MAX_POPULATION_SIZE, kth);
		// population.createGoodInviduals(MAX_POPULATION_SIZE, kth);
		return population;
	}

	private TimeTable next(ListIterator<TimeTable> it) {
		return it.hasNext() ? it.next() : null;
	}

	/////////////////////////////

	// Uses another implementation of roulette selection of parents
	private Population breed(Population population, int N) {
		Population children = new Population();

		// calculate the pseudofitness of each individual
		// used in the roulette selection
		int[] pseudoFitness = new int[population.size()];
		int smallestFitness = population.getWorstIndividual().getFitness();
		smallestFitness = smallestFitness >= 0 ? 0 : smallestFitness;

		int i = 0;
		ListIterator<TimeTable> it = population.listIterator();
		int fitnessSum = 0;
		while (it.hasNext()) {
			// the smallest possible is 1, this saves us from weird behaviour in
			// cases where all individuals have the same fitness
			pseudoFitness[i] = it.next().getFitness() + -1 * smallestFitness + 1;
			fitnessSum += pseudoFitness[i];
			i++;
		}

		// create alias index
		int[] alias = new int[fitnessSum];

		// add the individual indexes a proportionate amount of times
		int aliasIndex = 0;
		it = population.listIterator();
		for (int individual = 0; individual < population.size(); individual++) {
			for (int j = 0; j < pseudoFitness[individual]; j++) {
				alias[aliasIndex] = individual;
				aliasIndex++;
			}
		}

		while (children.size() < N) {
			if (alias.length == 0) {
				break;
			}

			int pi1 = alias[rand.nextInt(alias.length)];
			int numPi1 = pseudoFitness[pi1];
			int aIndex = rand.nextInt(alias.length - numPi1);

			int ai = 0;
			int j = 0;
			for (; j < alias.length && ai < aIndex; j++) {
				// skip ahead if we are at the span of the first parent's index
				while (j < (alias.length - 1) && alias[j] == pi1) {
					j++;
				}

				ai++;
			}

			int pi2 = alias[j];

			TimeTable t1 = population.getIndividual(pi1);
			TimeTable t2 = population.getIndividual(pi2);

			TimeTable child = crossoverWithPoint(t1, t2);
			mutate(child);
			repairTimeTable(child);
			// improveTimeTable(child); // Ineffective
			constraints.fitness(child);

			children.addIndividual(child);
		}

		return children;
	}

	private Population selection(Population population, Population children) {
		// population is already sorted
		children.sortIndividuals();

		Population nextPopulation = new Population();

		ListIterator<TimeTable> itParents = population.listIterator();
		ListIterator<TimeTable> itChildren = children.listIterator();
		TimeTable nextParent = next(itParents);
		TimeTable nextChild = next(itChildren);

		while (nextPopulation.size() < MAX_POPULATION_SIZE) {
			if (nextChild != null) {
				if (nextChild.getFitness() > nextParent.getFitness()) {
					nextPopulation.addIndividual(nextChild);

					nextChild = next(itChildren);

				} else {
					nextPopulation.addIndividual(nextParent);

					nextParent = next(itParents);
				}

			} else {
				if (nextParent != null) {
					// add the rest from population
					nextPopulation.addIndividual(nextParent);
					nextParent = next(itParents);
				}
			}
		}

		return nextPopulation;
	}

	/////////////////////////////

	private TimeTable crossoverWithPoint(TimeTable t1, TimeTable t2) {
		TimeTable child = new TimeTable(kth.getNumRooms());

		int interval = kth.getNumRooms() * RoomTimeTable.NUM_TIMESLOTS * RoomTimeTable.NUM_DAYS;

		int point = rand.nextInt(interval);

		RoomTimeTable[] rtts1 = t1.getRoomTimeTables();
		RoomTimeTable[] rtts2 = t2.getRoomTimeTables();

		int gene = 0;

		// iterate over the genes
		for (int i = 0; i < kth.getNumRooms(); i++) {
			RoomTimeTable rtt = new RoomTimeTable(rtts1[i].getRoom());

			// for each available time
			for (int timeslot = 0; timeslot < RoomTimeTable.NUM_TIMESLOTS; timeslot++) {
				for (int day = 0; day < RoomTimeTable.NUM_DAYS; day++) {
					int allele;

					if (gene < point) {
						allele = rtts1[i].getEvent(day, timeslot);
					} else {
						allele = rtts2[i].getEvent(day, timeslot);
					}

					rtt.setEvent(day, timeslot, allele);
					gene++;
				}
			}

			child.putRoomTimeTable(i, rtt);
		}

		return child;
	}

	private void repairTimeTable(TimeTable tt) {
		HashMap<Integer, LinkedList<RoomDayTime>> locations = new HashMap<Integer, LinkedList<RoomDayTime>>();

		LinkedList<RoomDayTime> unusedSlots = new LinkedList<RoomDayTime>();

		// initiate number of bookings to 0
		for (int eventID : kth.getEvents().keySet()) {
			locations.put(eventID, new LinkedList<RoomDayTime>());
		}

		RoomTimeTable[] rtts = tt.getRoomTimeTables();

		for (int i = 0; i < kth.getNumRooms(); i++) {
			RoomTimeTable rtt = rtts[i];
			// for each available time
			for (int timeslot = 0; timeslot < RoomTimeTable.NUM_TIMESLOTS; timeslot++) {
				for (int day = 0; day < RoomTimeTable.NUM_DAYS; day++) {
					int bookedEvent = rtt.getEvent(day, timeslot);
					if (bookedEvent == 0) {
						// add to usable slots
						unusedSlots.add(new RoomDayTime(i, day, timeslot));

					} else {
						// save the location
						locations.get(bookedEvent).add(new RoomDayTime(i, day, timeslot));
					}
				}
			}
		}

		List<Integer> unbookedEvents = new LinkedList<Integer>();

		for (int eventID : kth.getEvents().keySet()) {
			if (locations.get(eventID).size() == 0) {
				// this event is unbooked
				unbookedEvents.add(eventID);

			} else if (locations.get(eventID).size() > 1) {
				// this is event is booked more than once
				// randomly make those slots unused until only one remains
				LinkedList<RoomDayTime> slots = locations.get(eventID);
				Collections.shuffle(slots);

				while (slots.size() > 1) {
					RoomDayTime rdt = slots.removeFirst();

					// mark this slot as unused
					unusedSlots.add(rdt);
					rtts[rdt.room].setEvent(rdt.day, rdt.time, 0);
				}
			}
		}

		// now put each unbooked event in an unused slot
		Collections.shuffle(unusedSlots);
		for (int eventID : unbookedEvents) {
			RoomDayTime rdt = unusedSlots.removeFirst();
			rtts[rdt.room].setEvent(rdt.day, rdt.time, eventID);
		}
	}

	// Wrapper class only used in repair function
	private class RoomDayTime {
		int room;
		int day;
		int time;

		RoomDayTime(int room, int day, int time) {
			this.room = room;
			this.day = day;
			this.time = time;
		}
	}

	//////////////////////////
	// MUTATION
	//////////////////////////

	private void mutate(TimeTable tt) {
		RoomTimeTable[] rtts = tt.getRoomTimeTables();

		for (int i = 0; i < kth.getNumRooms(); i++) {
			RoomTimeTable rtt = rtts[i];
			// for each available time
			for (int timeslot = 0; timeslot < RoomTimeTable.NUM_TIMESLOTS; timeslot++) {
				for (int day = 0; day < RoomTimeTable.NUM_DAYS; day++) {
					if (rand.nextInt(1000) < MUTATION_PROBABILITY) {
						// mutate this gene
						int swapTargetDay = rand.nextInt(RoomTimeTable.NUM_DAYS);
						int swapTargetTimeslot = rand.nextInt(RoomTimeTable.NUM_TIMESLOTS);
						int swapTargetEventId = rtt.getEvent(swapTargetDay, swapTargetTimeslot);
						int swapSrcEventId = rtt.getEvent(day, timeslot);
						rtt.setEvent(swapTargetDay, swapTargetTimeslot, swapSrcEventId);
						rtt.setEvent(day, timeslot, swapTargetEventId);
					}
				}
			}
		}
	}

	//////////////////////////
	// Get and set methods
	//////////////////////////

	public void setSamevalueLimit(int p) {
		SAMEVALUE_LIMIT = p;
	}

	public void setDesiredFitness(int p) {
		DESIRED_FITNESS = p;
	}

	public void setTimeLimit(int t) {
		TIME_LIMIT = t;
	}

	public void setMutationProbability(int p) {
		MUTATION_PROBABILITY = p;
	}

	public void setPopulationSize(int size) {
		MAX_POPULATION_SIZE = size;
	}

	public void setSelectionSize(int size) {
		SELECTION_SIZE = size;
	}
	
	@Override
	public String getConf() {
		StringBuilder sb = new StringBuilder();
		sb.append("Desired fitness: " + DESIRED_FITNESS + "\n");
		sb.append("Stuck limit: " + SAMEVALUE_LIMIT + "\n");

		sb.append("Population size: " + MAX_POPULATION_SIZE + "\n");
		sb.append("P(Mutation) = " + ((double) MUTATION_PROBABILITY / 1000.0d * 100) + "%" + "\n");
		sb.append("Time limit: " + TIME_LIMIT + "\n");

		return sb.toString();
	}

	private void improveTimeTable(TimeTable tt) {
		SA sa = new SA();
		sa.defaultSetupGASA(kth, constraints);
		sa.setSolution(tt);
		sa.setDesiredFitness(-1000);

		sa.run(System.currentTimeMillis());
		System.out.println("Improved");
		tt = sa.getResult();

	}
}
