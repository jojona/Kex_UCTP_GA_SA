package main;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import GA.src.GA;
import SA.SA;

public class Main {
	static String path = "src/input/";
	static String outputName = "output/";
	static String output;

	static String file = "kth_L";
	public static boolean debug = false;

	private int runs = 9;
	private int fitnessGoal = -50;
	private int timeGoal = 200 * 1000;

	// TODO list before running tests
	// Check input file
	// Check output file name
	// Check fitness goal
	// Check time limit goal
	// Check runs
	// Check number of days
	// Test run with small time limit goal and check output
	// Don't run with debug
	// Check tests in main

	public static void main(String[] args) {
		Main.path += Main.file;
		Main main = new Main();

		long startTime = System.currentTimeMillis();
		// main.findSAdelta();

		// Need to run delta test before and fix delta value DONE
		// main.testSA();
		//main.testGA();

		// Need to run above tests before and fix parameters DONE
		main.testGASA();
		main.fitnessGoal = 0;
		// Need to run above tests before and fix parameters
		main.Runall();
		main.file = "kth_M";
		Main.path = "src/input/" + Main.file;
		main.Runall();
		
		long endTime = System.currentTimeMillis();
		System.out.println("Total runtime: " + Math.floor((endTime - startTime) / 1000) + " seconds");
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
		BufferedWriter outputStreamSAMatlab;
		BufferedWriter outputStreamGAMatlab;
		OutputStrings result;
		int[] timeTests = {2*1000, 10*1000, 60*1000, 150*1000};
		int runAllRuns = 50;
		try {
			outputStreamSA = new BufferedWriter(new FileWriter("saMainRun" + Main.file + ".txt"));
			outputStreamSAMatlab = new BufferedWriter(new FileWriter("saMainRun" + Main.file + "Matlab.txt"));
			outputStreamGA = new BufferedWriter(new FileWriter("gaMainRun" + Main.file + ".txt"));
			outputStreamGAMatlab = new BufferedWriter(new FileWriter("gaMainRun" + Main.file + "Matlab.txt"));
			//outputSteamGASA = new BufferedWriter(new FileWriter("gasaMainRun.txt"));
			
			for (int time : timeTests){
				System.out.print("Time:" + time + " Lap:");
				for (int i = 0; i < runAllRuns; i++) {
					Main.output = Main.outputName + Main.file + "_" + i;
					System.out.print(" " + i);
					result = GA(time);
					
					outputStreamGA.write(result.regOutput);
					outputStreamGA.flush();
					outputStreamGAMatlab.write(result.matlabOutput);
					outputStreamGAMatlab.flush();
					
					result = SA(time);
					outputStreamSA.write(result.regOutput);
					outputStreamSA.flush();
					outputStreamSAMatlab.write(result.matlabOutput);
					outputStreamSAMatlab.flush();
	
					//outputSteamGASA.write(GASA() + "\n");
					//outputSteamGASA.flush();
	
				}
				System.out.println();
			}
			outputStreamGAMatlab.close();
			outputStreamSAMatlab.close();
			outputStreamSA.close();
			outputStreamGA.close();
			//outputSteamGASA.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void testGASA() {
		
		int[] testFitness = { -500, -1000, -10000, -50000, -100000, -300000 };
		int[] testStuck = { 10, 50, 100, 200, 1000 };
		int[] testTime1 = { 10*1000, 20*1000, 50*1000, 80*1000, 100*1000, 150*1000, 190*1000}; // timegoal= 200
		int[] testTime2 = {1*100, 5*100, 1*1000, 2*1000, 5*1000, 10*1000, 20*1000}; // timegoal= 200
		

		BufferedWriter outputStream;
		BufferedWriter outputStreamM;
		try {

			long startTime;
			long endTime;
			long runtime;

			outputStream = new BufferedWriter(new FileWriter("gasaTime1TestLarge.txt")); // TODO
																						// filename
			outputStreamM = new BufferedWriter(new FileWriter("gasaTime1TestLargeMatlab.txt"));

			for (int time : testTime1) {
				System.out.print("Time: " + time);
				for (int i = 0; i < runs; ++i) {

					// Time limit
					GA ga = new GA();
					ga.defaultSetup(path);
					ga.setDesiredFitness(fitnessGoal);
					ga.setTimeLimit(time);

					SA sa = new SA();
					sa.defaultSetupGASA(ga.kth, ga.constraints);
					sa.setDesiredFitness(fitnessGoal);
					sa.setTimeLimit(timeGoal); //TODO Correct?

					// Start time
					startTime = System.currentTimeMillis();

					TimeTable gatimeTable = ga.generateTimeTable(startTime);
					sa.setSolution(gatimeTable);
					sa.run(startTime);

					// End time
					endTime = System.currentTimeMillis();
					runtime = endTime - startTime;
					sa.getResult().time = sa.getResult().getCreatedTime() - startTime;

					outputStream.write("Time: " + runtime + " \tCreatedTime: "
							+ (sa.getResult().getCreatedTime() - startTime) + " \tFitness "
							+ sa.getResult().getFitness() + " \tGatime: " + (gatimeTable.getCreatedTime() - startTime)
							+ "\tTimeLimit " + time + "\t Hard broken:" + sa.hardConstraints(sa.getResult()) + "\n");
					outputStreamM.write(runtime + " " + (sa.getResult().getCreatedTime() - startTime) + " "
							+ sa.getResult().getFitness() + " " + (gatimeTable.getCreatedTime() - startTime) + " "
							+ time + " " + sa.hardConstraints(sa.getResult()) + "\n");
					outputStream.flush();
					outputStreamM.flush();
					System.out.print(" " + i);
				}
				System.out.println();
			}
			outputStream.close();
			outputStreamM.close();
			
			
		

//				long startTime;
//				long endTime;
//				long runtime;

			outputStream = new BufferedWriter(new FileWriter("gasaTime2TestLarge.txt")); // TODO
																						// filename
			outputStreamM = new BufferedWriter(new FileWriter("gasaTime2TestLargeMatlab.txt"));

			for (int time : testTime2) {
				System.out.print("Time: " + time);
				for (int i = 0; i < runs; ++i) {

					// Time limit
					GA ga = new GA();
					ga.defaultSetup(path);
					ga.setDesiredFitness(fitnessGoal);
					ga.setTimeLimit(time);

					SA sa = new SA();
					sa.defaultSetupGASA(ga.kth, ga.constraints);
					sa.setDesiredFitness(fitnessGoal);
					sa.setTimeLimit(timeGoal); //TODO Correct?

					// Start time
					startTime = System.currentTimeMillis();

					TimeTable gatimeTable = ga.generateTimeTable(startTime);
					sa.setSolution(gatimeTable);
					sa.run(startTime);

					// End time
					endTime = System.currentTimeMillis();
					runtime = endTime - startTime;
					sa.getResult().time = sa.getResult().getCreatedTime() - startTime;

					outputStream.write("Time: " + runtime + " \tCreatedTime: "
							+ (sa.getResult().getCreatedTime() - startTime) + " \tFitness "
							+ sa.getResult().getFitness() + " \tGatime: " + (gatimeTable.getCreatedTime() - startTime)
							+ "\tTimeLimit " + time + "\t Hard broken:" + sa.hardConstraints(sa.getResult()) + "\n");
					outputStreamM.write(runtime + " " + (sa.getResult().getCreatedTime() - startTime) + " "
							+ sa.getResult().getFitness() + " " + (gatimeTable.getCreatedTime() - startTime) + " "
							+ time + " " + sa.hardConstraints(sa.getResult()) + "\n");
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
					ga2.setDesiredFitness(fitnessGoal);
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
									+ (gatimeTable.getCreatedTime() - startTime) + " \tStuckLimit: " + stuck
									+ " \tHard broken:" + sa2.hardConstraints(sa2.getResult()) + "\n");
					outputStreamM.write(runtime + " " + (sa2.getResult().getCreatedTime() - startTime) + " "
							+ sa2.getResult().getFitness() + " " + (gatimeTable.getCreatedTime() - startTime) + " "
							+ stuck + " " + sa2.hardConstraints(sa2.getResult()) + "\n");
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
									+ (gatimeTable.getCreatedTime() - startTime) + " \tFitnessLimit: " + fit
									+ "\t Hard broken:" + sa3.hardConstraints(sa3.getResult()) + "\n");
					outputStreamM.write(runtime + " " + (sa3.getResult().getCreatedTime() - startTime) + " "
							+ sa3.getResult().getFitness() + " " + (gatimeTable.getCreatedTime() - startTime) + " "
							+ fit + " " + sa3.hardConstraints(sa3.getResult()) + "\n");
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

		BufferedWriter outputStream;
		BufferedWriter outputStreamM;
		try {
			outputStream = new BufferedWriter(new FileWriter("saTestLarge120s50fit.txt"));
			outputStreamM = new BufferedWriter(new FileWriter("saTestLargeMatlab120s50fit.txt"));
			for (double sProb : startProb) {
				for (double eProb : endProb) {
					if (eProb >= sProb) {
						continue;
					}
					for (int i = 0; i < runs; ++i) {
						SA sa = new SA();
						sa.defaultSetup(path);
						calcSAParam(sProb, eProb, sa);

						sa.setDesiredFitness(fitnessGoal);
						sa.setTimeLimit(timeGoal);

						// Start time
						long startTime = System.currentTimeMillis();

						sa.run(startTime);

						// End time
						long endTime = System.currentTimeMillis();
						long time = endTime - startTime;
						sa.getResult().time = sa.getResult().getCreatedTime() - startTime;

						double temp = sa.getInitialTemp();
						double my = sa.getMy();
						int iter = sa.getInitialIterations();
						outputStream.write("Iter:" + iter + "\t startProb:" + sProb + "\t endProb:" + eProb + "\t Temp:"
								+ temp + "\t My:" + my + "\t Time:" + time + "\t ResultTime:" + sa.getResult().time
								+ "\t Fitness:" + sa.getResult().getFitness() + "\t GlobIt:" + sa.globalIterations
								+ "\t Hard broken:" + sa.hardConstraints(sa.getResult()) + "\n");
						outputStreamM.write(iter + " " + sProb + " " + eProb + " " + temp + " " + my + " " + time + " "
								+ sa.getResult().time + " " + sa.getResult().getFitness() + " " + sa.globalIterations
								+ " " + sa.hardConstraints(sa.getResult()) + "\n");
						System.out.println("Iter:" + iter + "\t startProb:" + sProb + "\t endProb:" + eProb + "\t Temp:"
								+ temp + "\t My:" + my + "\t Time:" + time + "\t ResultTime:" + sa.getResult().time
								+ "\t Fitness:" + sa.getResult().getFitness() + "\t GlobIt:" + sa.globalIterations
								+ "\t Hard broken:" + sa.hardConstraints(sa.getResult()));
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

		int[] mutationProb = { 40, 60, 80, 100 }; // 4
		int[] popSize = { 50, 100, 150, 200 }; // 4
		double[] selSize = { 0.25, 0.5, 0.75, 1 }; // 4
		// 4*4*4 *9 *2min /60min = 19.2h

		// int[] sel_Size= {20, 30, 40}; //3

		BufferedWriter outputStream;
		BufferedWriter outputStreamM;
		try {
			outputStream = new BufferedWriter(new FileWriter("gaTestLarge120s50fit.txt"));
			outputStreamM = new BufferedWriter(new FileWriter("gaTestLargeMatlab120s50fit.txt"));
			for (int pop : popSize) {
				for (int mutation : mutationProb) {
					for (double sel : selSize) {

						int selectionSize = (int) (pop * sel);

						for (int i = 0; i < runs; ++i) {
							GA ga = new GA();
							ga.defaultSetup(path);

							ga.setSamevalueLimit(Integer.MAX_VALUE);
							ga.setMutationProbability(mutation);
							ga.setPopulationSize(pop);
							ga.setSelectionSize(selectionSize);

							ga.setTimeLimit(timeGoal);
							ga.setDesiredFitness(fitnessGoal);

							// Start time
							long startTime = System.currentTimeMillis();

							TimeTable bestTimeTable = ga.generateTimeTable(startTime);

							// End time
							long endTime = System.currentTimeMillis();
							long time = endTime - startTime;

							bestTimeTable.time = bestTimeTable.getCreatedTime() - startTime;

							outputStream.write(" Pop size:" + pop + "\t Sel size:" + selectionSize + "\t Sel quota:"
									+ sel + "\t Mutation prob:" + mutation + "\t Time:" + time + "\t ResultTime:"
									+ bestTimeTable.time + "\t Fitness:" + bestTimeTable.getFitness()
									+ "\t Generations:" + ga.numGenerations + "\t Hard broken:"
									+ ga.hardConstraints(bestTimeTable) + "\n");

							outputStreamM.write(pop + " " + selectionSize + " " + sel + " " + mutation + " " + time
									+ " " + bestTimeTable.time + " " + bestTimeTable.getFitness() + " "
									+ ga.numGenerations + " " + ga.hardConstraints(bestTimeTable) + "\n");

							System.out.println(" Pop size:" + pop + "\t Sel size:" + selectionSize + "\t Sel quota:"
									+ sel + "\t Mutation prob:" + mutation + "\t Time:" + time + "\t ResultTime:"
									+ bestTimeTable.time + "\t Fitness:" + bestTimeTable.getFitness()
									+ "\t Generations:" + ga.numGenerations + "\t Hard broken:"
									+ ga.hardConstraints(bestTimeTable));

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

	public OutputStrings SA(int thisTimeGoal) {
		SA sa = new SA();
		sa.defaultSetup(path);
		sa.setDesiredFitness(fitnessGoal);
		sa.setTimeLimit(thisTimeGoal);
		// Start time
		long startTime = System.currentTimeMillis();

		sa.run(startTime);

		// End time
		long endTime = System.currentTimeMillis();
		long time = endTime - startTime;

		sa.getResult().time = sa.getResult().getCreatedTime() - startTime;
		if(Main.debug){
			System.out.println("SA done");			
		}
		printTimeTable(sa.getResult(), time, "SA", "Globalit: " + sa.globalIterations, sa.kth, sa.getConf(),
				sa.hardConstraints(sa.getResult()));

		String reg = "Time:" + time + "\t ResultTime:" + sa.getResult().time + "\t Breaktime:" + thisTimeGoal
				+ "\t Fitness:" + sa.getResult().getFitness() + "\t GlobIt:" + sa.globalIterations
				+ "\t Hard broken:" + sa.hardConstraints(sa.getResult()) + "\n";;
		
		String matlab = time + " " + sa.getResult().time + " " + thisTimeGoal + " " + sa.getResult().getFitness() + " " + sa.globalIterations
				+ " " + sa.hardConstraints(sa.getResult()) + "\n";;
		
		return new OutputStrings(reg, matlab);
	}

	public OutputStrings GA(int thisTimeGoal) {
		GA ga = new GA();
		ga.defaultSetup(path);
		ga.setDesiredFitness(fitnessGoal);
		ga.setTimeLimit(thisTimeGoal);
		// Start time
		long startTime = System.currentTimeMillis();

		TimeTable bestTimeTable = ga.generateTimeTable(startTime);

		// End time
		long endTime = System.currentTimeMillis();
		long time = endTime - startTime;

		bestTimeTable.time = bestTimeTable.getCreatedTime() - startTime;

		if(Main.debug){
			System.out.println("GA done");			
		}
		
		printTimeTable(bestTimeTable, time, "GA", "Generations: " + ga.numGenerations, ga.kth, ga.getConf(),
				ga.hardConstraints(bestTimeTable));

		//return "" + time;
		String reg =  "Time:" + time + "\t ResultTime:" + bestTimeTable.time + "\t Breaktime:" + thisTimeGoal 
				+ "\t Fitness:" + bestTimeTable.getFitness()
		+ "\t Generations:" + ga.numGenerations + "\t Hard broken:"
		+ ga.hardConstraints(bestTimeTable) + "\n";
		
		String matlab = time + " " + bestTimeTable.time + " " + thisTimeGoal + " " + bestTimeTable.getFitness() + " "
				+ ga.numGenerations + " " + ga.hardConstraints(bestTimeTable) + "\n";
		
		return new OutputStrings(reg, matlab);
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
	
	private class OutputStrings {
		public String regOutput;
		public String matlabOutput;
		public OutputStrings(String reg, String matlab){
			regOutput = reg;
			matlabOutput = matlab;
		}
	}

}
