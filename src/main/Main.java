package main;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import GA.src.GA;
import SA.SA;

public class Main {
	static String path = "src/GA/input/";
	static String file = "kth_M";
	static String outputName = "output/";

	static String output;

	public static boolean debug = false;

	private int runs = 9;
	private int fitnessGoal = 0;
	private int timeGoal = 120 * 1000;

	// TODO list before running tests
	// Check input file
	// Check fitness goal
	// Check time limit goal
	// Check runs
	// Check number of days
	// Test run with small timegoal and check output
	// Dont run with debug
	// Check tests in main

	public static void main(String[] args) {
		Main.path += Main.file;
		Main main = new Main();

		// main.findSAdelta();

		// Need to run delta test before and fix delta value DONE
		main.testSA();
		main.testGA();

		// Need to run above tests before and fix parameters
		// main.testGASA();

		// Need to run above tests before and fix parameters
		// main.Runall();

	}

	public void findSAdelta() {

		for (int i = 0; i < runs; i++) {

			SA sa = new SA();
			sa.defaultSetup(path);
			sa.testDelta();
		}
	}

	public void Runall() {

		BufferedWriter outputStreamSA;
		BufferedWriter outputStreamGA;
		BufferedWriter outputSteamGASA;

		try {
			outputStreamSA = new BufferedWriter(new FileWriter("saMainRun.txt"));
			outputStreamGA = new BufferedWriter(new FileWriter("gaMainRun.txt"));
			outputSteamGASA = new BufferedWriter(new FileWriter("gasaMainRun.txt"));

			for (int i = 0; i < runs; i++) {
				Main.output = Main.outputName + Main.file + "_" + i;
				System.out.println("Lap: " + i);

				outputStreamGA.write(GA() + "\n");
				outputStreamGA.flush();

				outputStreamSA.write(SA() + "\n");
				outputStreamSA.flush();

				outputSteamGASA.write(GASA() + "\n");
				outputSteamGASA.flush();

			}
			outputStreamSA.close();
			outputStreamGA.close();
			outputSteamGASA.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void testGASA() {

		int[] testFitness = { -10, -25, -50, -100, -500 };
		int[] testStuck = { 10, 50, 100, 200, 1000 };
		int[] testTime = { 10, 20, 30, 40, 50, 60 };

		BufferedWriter outputStream;
		BufferedWriter outputStreamM;
		try {

			long startTime;
			long endTime;
			long runtime;

			outputStream = new BufferedWriter(new FileWriter("gasaTimeTestLarge.txt"));
			outputStreamM = new BufferedWriter(new FileWriter("gasaTimeTestLargeMatlab.txt"));

			for (int time : testTime) {
				System.out.print("Time: " + time);
				for (int i = 0; i < runs; ++i) {

					// Time limit
					GA ga = new GA();
					ga.defaultSetup(path);
					ga.setTimeLimit(time);

					SA sa = new SA();
					sa.defaultSetupGASA(ga.kth, ga.constraints);
					sa.setDesiredFitness(fitnessGoal);
					sa.setTimeLimit(timeGoal);

					// Start time
					startTime = System.currentTimeMillis();

					TimeTable gatimeTable = ga.generateTimeTable(startTime);
					sa.setSolution(gatimeTable);
					sa.run(startTime);

					// End time
					endTime = System.currentTimeMillis();
					runtime = endTime - startTime;
					sa.getResult().time = sa.getResult().getCreatedTime() - startTime;

					outputStream.write(
							"Time: " + runtime + " \tCreatedTime: " + (sa.getResult().getCreatedTime() - startTime)
									+ " \tFitness " + sa.getResult().getFitness() + " \tGatime: "
									+ (gatimeTable.getCreatedTime() - startTime) + " \tTimeLimit " + time + "\n");
					outputStreamM.write(runtime + " " + (sa.getResult().getCreatedTime() - startTime) + " "
							+ sa.getResult().getFitness() + " " + (gatimeTable.getCreatedTime() - startTime) + " "
							+ time + "\n");
					outputStream.flush();
					outputStreamM.flush();
					System.out.print(" " + i);
				}
				System.out.println();
			}
			outputStream.close();
			outputStreamM.close();

			outputStream = new BufferedWriter(new FileWriter("gasaStuckTestLarge.txt"));
			outputStreamM = new BufferedWriter(new FileWriter("gasaStuckTestLargeMatlab.txt"));

			for (int stuck : testStuck) {
				System.out.print("Stuck: " + stuck);
				for (int i = 0; i < runs; ++i) {
					// Stuck limit
					GA ga2 = new GA();
					ga2.defaultSetup(path);
					ga2.setTimeLimit(timeGoal);
					ga2.setSamevalueLimit(stuck);

					SA sa2 = new SA();
					sa2.defaultSetupGASA(ga2.kth, ga2.constraints);
					sa2.setDesiredFitness(fitnessGoal);
					sa2.setTimeLimit(timeGoal);

					// Start time
					startTime = System.currentTimeMillis();

					TimeTable gatimeTable = ga2.generateTimeTable(startTime);
					sa2.setSolution(gatimeTable);
					sa2.run(startTime);

					// End time
					endTime = System.currentTimeMillis();
					runtime = endTime - startTime;
					sa2.getResult().time = sa2.getResult().getCreatedTime() - startTime;

					outputStream.write(
							"Time: " + runtime + " \tCreatedTime: " + (sa2.getResult().getCreatedTime() - startTime)
									+ " \tFitness: " + sa2.getResult().getFitness() + " \tGatime: "
									+ (gatimeTable.getCreatedTime() - startTime) + " \tStuckLimit: " + stuck + "\n");
					outputStreamM.write(runtime + " " + (sa2.getResult().getCreatedTime() - startTime) + " "
							+ sa2.getResult().getFitness() + " " + (gatimeTable.getCreatedTime() - startTime) + " "
							+ stuck + "\n");
					outputStream.flush();
					outputStreamM.flush();
					System.out.print(" " + i);
				}
				System.out.println();
			}

			outputStream.close();
			outputStreamM.close();

			outputStream = new BufferedWriter(new FileWriter("gasaFitTestLarge.txt"));
			outputStreamM = new BufferedWriter(new FileWriter("gasaFitTestLargeMatlab.txt"));
			for (int fit : testFitness) {
				System.out.print("Fitness: " + fit);
				for (int i = 0; i < runs; ++i) {
					// Fitness goal
					GA ga3 = new GA();
					ga3.defaultSetup(path);
					ga3.setTimeLimit(timeGoal);
					ga3.setDesiredFitness(fit);

					SA sa3 = new SA();
					sa3.defaultSetupGASA(ga3.kth, ga3.constraints);
					sa3.setDesiredFitness(fitnessGoal);
					sa3.setTimeLimit(timeGoal);

					// Start time
					startTime = System.currentTimeMillis();

					TimeTable gatimeTable = ga3.generateTimeTable(startTime);
					gatimeTable.setCreatedTime();
					sa3.setSolution(gatimeTable);
					sa3.run(startTime);

					// End time
					endTime = System.currentTimeMillis();
					runtime = endTime - startTime;
					sa3.getResult().time = sa3.getResult().getCreatedTime() - startTime;

					outputStream.write(
							"Time: " + runtime + " \tCreatedTime: " + (sa3.getResult().getCreatedTime() - startTime)
									+ " \tFitness: " + sa3.getResult().getFitness() + " \tGatime: "
									+ (gatimeTable.getCreatedTime() - startTime) + " \tFitnessLimit: " + fit + "\n");
					outputStreamM.write(runtime + " " + (sa3.getResult().getCreatedTime() - startTime) + " "
							+ sa3.getResult().getFitness() + " " + (gatimeTable.getCreatedTime() - startTime) + " "
							+ fit + "\n");
					outputStream.flush();
					outputStreamM.flush();
					System.out.print(" " + i);

				}
				System.out.println();
			}
			outputStream.close();
			outputStreamM.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void testSA() {
		double[] startProb = { 0.001, 0.01, 0.05, 0.1 };
		double[] endProb = { 0.01, 0.001, 1E-4 };

		int iter = 10;

		BufferedWriter outputStream;
		BufferedWriter outputStreamM;
		try {
			outputStream = new BufferedWriter(new FileWriter("saTestLarge.txt"));
			outputStreamM = new BufferedWriter(new FileWriter("saTestLargeMatlab.txt"));
			for (double sProb : startProb) {
				for (double eProb : endProb) {
					if (eProb >= sProb) {
						continue;
					}
					for (int i = 0; i < runs; ++i) {
						SA sa = new SA();
						sa.defaultSetup(path);
						calcSAParam(sProb, eProb, sa);
						sa.setInitialIterations(iter);

						sa.setDesiredFitness(fitnessGoal);
						sa.setTimeLimit(timeGoal);

						// Start time
						long startTime = System.currentTimeMillis();

						sa.run(startTime);

						// End time
						long endTime = System.currentTimeMillis();
						long time = endTime - startTime;
						sa.getResult().time = sa.getResult().getCreatedTime() - startTime;

						double temp = sa.getT0();
						double my = sa.getMy();
						outputStream.write("Iter:" + iter + "\t startProb:" + sProb + "\t endProb:" + eProb + "\t Temp:"
								+ temp + "\t My:" + my + "\t Time:" + time + "\t ResultTime:" + sa.getResult().time
								+ "\t Fitness:" + sa.getResult().getFitness() + "\t GlobIt:" + sa.globalIterations
								+ "\n");
						outputStreamM.write(iter + " " + sProb + " " + eProb + " " + temp + " " + my + " " + time + " "
								+ sa.getResult().time + " " + sa.getResult().getFitness() + " " + sa.globalIterations
								+ "\n");
						System.out.println("Iter:" + iter + "\t startProb:" + sProb + "\t endProb:" + eProb + "\t Temp:"
								+ temp + "\t My:" + my + "\t Time:" + time + "\t ResultTime:" + sa.getResult().time
								+ "\t Fitness:" + sa.getResult().getFitness() + "\t GlobIt:" + sa.globalIterations);
						outputStream.flush();
						outputStreamM.flush();
					}
				}
			}

			outputStream.close();
			outputStreamM.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void calcSAParam(double startProb, double endProb, SA sa) {
		double delta = sa.avgDelta;
		int iterations = sa.iterationGoal;

		double T0 = delta / Math.log(startProb);
		double my = Math.log(delta / (Math.log(endProb) * T0)) / iterations;
		sa.setInitialTemperature(T0);
		sa.setMy(my);
	}

	public void testGA() {

		int[] mutationProb = { 40, 50, 60, 70, 80, 90, 100 }; // 7
		int[] crossoverProb = { 200, 300, 400, 450, 500, 550, 600, 700 }; // 8
		int[] popSize = { 10, 25, 50, 75, 100, 125, 150 }; // 7

		// 7*8*7 *2min /60min = 13h

		// int[] sel_Size= {20, 30, 40}; //3

		BufferedWriter outputStream;
		BufferedWriter outputStreamM;
		try {
			outputStream = new BufferedWriter(new FileWriter("gaTestLarge.txt"));
			outputStreamM = new BufferedWriter(new FileWriter("gaTestLargeMatlab.txt"));
			for (int pop : popSize) {
				for (int crossover : crossoverProb) {
					for (int mutation : mutationProb) {
						for (int i = 0; i < runs; ++i) {
							GA ga = new GA();
							ga.defaultSetup(path);

							ga.setSamevalueLimit(Integer.MAX_VALUE);
							ga.setMutationProbability(mutation);
							ga.setPopulationSize(pop);

							ga.setTimeLimit(timeGoal);
							ga.setDesiredFitness(fitnessGoal);

							// Start time
							long startTime = System.currentTimeMillis();

							TimeTable bestTimeTable = ga.generateTimeTable(startTime);

							// End time
							long endTime = System.currentTimeMillis();
							long time = endTime - startTime;

							bestTimeTable.time = bestTimeTable.getCreatedTime() - startTime;

							outputStream
									.write(/* "Selection_type:" + selection + */" Pop size:" + pop
											+ /* "\t Sel size:" + selSize + */
											"\t Crossover prob:" + crossover + "\t Mutation prob:" + mutation
											+ "\t Time:" + time + "\t ResultTime:" + bestTimeTable.time + "\t Fitness:"
											+ bestTimeTable.getFitness() + "\t Generations:" + ga.numGenerations
											+ "\n");

							outputStreamM.write(/* selection + " " + */ pop + " "
									+ /*
										 * selSize + " " +
										 */ crossover + " " + mutation + " " + time + " " + bestTimeTable.time + " "
									+ bestTimeTable.getFitness() + " " + ga.numGenerations + "\n");

							System.out.println(
									/* "Selection_type:" + selection + */ " Pop size:" + pop
											+ /* "\t Sel size:" + selSize + */
											"\t Crossover prob:" + crossover + "\t Mutation prob:" + mutation
											+ "\t Time:" + time + "\t ResultTime:" + bestTimeTable.time + "\t Fitness:"
											+ bestTimeTable.getFitness() + "\t Generations:" + ga.numGenerations);

							outputStream.flush();
							outputStreamM.flush();
						}
					}
				}
			}

			outputStream.close();
			outputStreamM.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public String SA() {
		SA sa = new SA();
		sa.defaultSetup(path);
		sa.setDesiredFitness(fitnessGoal);
		sa.setTimeLimit(timeGoal);
		// Start time
		long startTime = System.currentTimeMillis();

		sa.run(startTime);

		// End time
		long endTime = System.currentTimeMillis();
		long time = endTime - startTime;

		sa.getResult().time = sa.getResult().getCreatedTime() - startTime;
		System.out.println("SA done");
		printTimeTable(sa.getResult(), time, "SA", "Globalit: " + sa.globalIterations, sa.kth, sa.getConf(),
				sa.hardConstraints(sa.getResult()));

		return "" + time;
	}

	public String GA() {
		GA ga = new GA();
		ga.defaultSetup(path);
		ga.setDesiredFitness(fitnessGoal);
		ga.setTimeLimit(timeGoal);
		// Start time
		long startTime = System.currentTimeMillis();

		TimeTable bestTimeTable = ga.generateTimeTable(startTime);

		// End time
		long endTime = System.currentTimeMillis();
		long time = endTime - startTime;

		bestTimeTable.time = bestTimeTable.getCreatedTime() - startTime;

		System.out.println("GA done");
		printTimeTable(bestTimeTable, time, "GA", "Generations: " + ga.numGenerations, ga.kth, ga.getConf(),
				ga.hardConstraints(bestTimeTable));

		return "" + time;
	}

	public String GASA() {
		GA ga = new GA();
		ga.defaultSetup(path);

		// TODO set good stopping condition
		// TODO Do not remove below if not used
		ga.setTimeLimit(timeGoal);
		ga.setDesiredFitness(fitnessGoal);
		// TODO

		SA sa = new SA();
		sa.defaultSetupGASA(ga.kth, ga.constraints);
		sa.setDesiredFitness(fitnessGoal);
		sa.setTimeLimit(timeGoal);

		// Start time
		long startTime = System.currentTimeMillis();

		TimeTable bestTimeTable = ga.generateTimeTable(startTime);

		sa.setSolution(bestTimeTable);
		sa.run(startTime);

		// End time
		long endTime = System.currentTimeMillis();
		long time = endTime - startTime;
		sa.getResult().time = sa.getResult().getCreatedTime() - startTime;

		System.out.println("GA_SA done");
		printTimeTable(sa.getResult(), time, "GASA",
				"Globalit: " + sa.globalIterations + " Generations: " + ga.numGenerations, sa.kth,
				sa.getConf() + ga.getConf(), sa.hardConstraints(sa.getResult()));

		return "" + time;
	}

	public void printTimeTable(TimeTable tt, long time, String name, String result, KTH kth, String config,
			int hardconstraints) {
		StringBuilder sb = new StringBuilder();
		int nrSlots = 0;
		int nrEvents = 0;
		for (RoomTimeTable rtt : tt.getRoomTimeTables()) {
			sb.append("============ ");
			sb.append("Room: " + rtt.getRoom().getName() + " Capacity: " + rtt.getRoom().getCapacity());
			sb.append(" ============\n");
			for (int timeslot = 0; timeslot < RoomTimeTable.NUM_TIMESLOTS; timeslot++) {
				for (int day = 0; day < RoomTimeTable.NUM_DAYS; day++) {
					int eventId = rtt.getEvent(day, timeslot);
					if (eventId > nrEvents) {
						nrEvents = eventId;
					}
					nrSlots++;
					if (Main.debug) {
						if (eventId != 0) {
							Event event = kth.getEvent(eventId);
							sb.append("[ " + event.getCourse().getId() + " " + event.getStudentGroup().getName() + " ");
							if (event.getType() == Event.Type.LECTURE) {
								sb.append(event.getLecturer().getName() + " ]");
							} else {
								sb.append("    ]");
							}
						} else {
							sb.append("[      -     ]");
						}
					}

					sb.append("[\t" + eventId + "\t]");
				}
				sb.append("\n");
			}
		}

		BufferedWriter outputStream;
		try {
			outputStream = new BufferedWriter(new FileWriter(output + "_" + name + ".txt"));
			outputStream.write("Number of slots: " + nrSlots + "\n");
			outputStream.write("Number of events: " + nrEvents + "\n");
			outputStream.write("Sparseness: " + ((double) nrEvents / (double) nrSlots) + "\n");
			outputStream.write("\n" + result + "\n");
			outputStream.write("Fitness: " + tt.getFitness() + "\t Created: " + tt.time + "\n");
			outputStream.write("Time: " + time + "ms\n\n");
			outputStream.write("Hard broken: " + hardconstraints + "\n");

			outputStream.write(config + "\n");

			outputStream.write(sb.toString());
			outputStream.flush();
			outputStream.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
