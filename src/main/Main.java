package main;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import GA.src.GA;
import SA.SA;

public class Main {
	static String path = "src/GA/input/";
	static String file = "kth_L";
	static String outputName = "output/";

	static String output;

	public final static boolean debug = false;

	public static void main(String[] args) {
		Main.path += Main.file;
		Main main = new Main();

		main.testGASA();
		// main.testSA();

		for (int i = 0; i < 0; i++) {
			Main.output = Main.outputName + Main.file + "_" + i + "_";
			System.out.println("Lap: " + i);
			main.GA();

			main.SA();

			main.GASA();

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
				for (int i = 0; i < 9; ++i) {
					
					// Time limit
					GA ga = new GA();
					ga.defaultSetup(path);
					Metaheuristic.TIME_LIMIT = time * 1000;

					SA sa = new SA();
					sa.defaultSetupGASA(ga.kth, ga.constraints);
					sa.setSameValueLimit(Integer.MAX_VALUE);
					sa.setDesiredFitness(-4);

					// Start time
					startTime = System.currentTimeMillis();

					TimeTable gatimeTable = ga.generateTimeTable(startTime);
					sa.setSolution(gatimeTable);
					Metaheuristic.TIME_LIMIT = 200 * 1000;
					sa.run(startTime);

					// End time
					endTime = System.currentTimeMillis();
					runtime = endTime - startTime;
					sa.getResult().time = sa.getResult().getCreatedTime() - startTime;

					outputStream.write("Time: " + runtime + " \tCreatedTime: " + (sa.getResult().getCreatedTime()  - startTime)
							+ " \tFitness " + sa.getResult().getFitness() + " \tGatime: " + (gatimeTable.getCreatedTime()  - startTime)
							+ " \tTimeLimit " + time + "\n");
					outputStreamM.write(runtime + " " + (sa.getResult().getCreatedTime()  - startTime) + " "
							+ sa.getResult().getFitness() + " " + (gatimeTable.getCreatedTime()  - startTime)+ " " + time + "\n");
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
				for (int i = 0; i < 9; ++i) {
					// Stuck limit
					GA ga2 = new GA();
					ga2.defaultSetup(path);
					Metaheuristic.TIME_LIMIT = 200 * 1000;
					ga2.setSamevalueLimit(stuck);

					SA sa2 = new SA();
					sa2.defaultSetupGASA(ga2.kth, ga2.constraints);
					sa2.setSameValueLimit(Integer.MAX_VALUE);
					sa2.setDesiredFitness(-4);

					// Start time
					startTime = System.currentTimeMillis();

					TimeTable gatimeTable = ga2.generateTimeTable(startTime);
					sa2.setSolution(gatimeTable);
					sa2.run(startTime);

					// End time
					endTime = System.currentTimeMillis();
					runtime = endTime - startTime;
					sa2.getResult().time = sa2.getResult().getCreatedTime() - startTime;

					outputStream.write("Time: " + runtime + " \tCreatedTime: " + (sa2.getResult().getCreatedTime() - startTime)
							+ " \tFitness: " + sa2.getResult().getFitness() + " \tGatime: "
							+ (gatimeTable.getCreatedTime() - startTime) + " \tStuckLimit: " + stuck + "\n");
					outputStreamM.write(runtime + " " + (sa2.getResult().getCreatedTime() - startTime) + " "
							+ sa2.getResult().getFitness() + " " + (gatimeTable.getCreatedTime() - startTime)+ " " + stuck + "\n");
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
				for (int i = 0; i < 9; ++i) {
					// Fitness goal
					GA ga3 = new GA();
					ga3.defaultSetup(path);
					Metaheuristic.TIME_LIMIT = 200 * 1000;
					ga3.setDesiredFitness(fit);

					SA sa3 = new SA();
					sa3.defaultSetupGASA(ga3.kth, ga3.constraints);
					sa3.setSameValueLimit(Integer.MAX_VALUE);
					sa3.setDesiredFitness(-4);

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

					outputStream.write("Time: " + runtime + " \tCreatedTime: " + (sa3.getResult().getCreatedTime() - startTime)
							+ " \tFitness: " + sa3.getResult().getFitness() + " \tGatime: "
							+ (gatimeTable.getCreatedTime() - startTime)+ " \tFitnessLimit: " + fit + "\n");
					outputStreamM.write(runtime + " " + (sa3.getResult().getCreatedTime() - startTime) + " "
							+ sa3.getResult().getFitness() + " " + (gatimeTable.getCreatedTime() - startTime) + " " + fit + "\n");
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
		Metaheuristic.TIME_LIMIT = 120000;
		double[] testMy = { -6.83E-5, -6.83E-4, -6.83E-3 };
		double[] testTemp = { 1, 4.3, 43, 100 };
		int[] testIter = { 10, 100, 1000 }; // GOOD

		BufferedWriter outputStream;
		BufferedWriter outputStreamM;
		try {
			outputStream = new BufferedWriter(new FileWriter("saTestLarge.txt"));
			outputStreamM = new BufferedWriter(new FileWriter("saTestLargeMatlab.txt"));
			for (int iter : testIter) {
				for (double temp : testTemp) {
					for (double my : testMy) {
						for (int i = 0; i < 9; ++i) {
							SA sa = new SA();
							sa.defaultSetup(path);
							sa.setSameValueLimit(Integer.MAX_VALUE);
							sa.setInitialIterations(iter);
							sa.setInitialTemperature(temp);
							sa.setMy(my);

							// Start time
							long startTime = System.currentTimeMillis();

							sa.run(startTime);

							// End time
							long endTime = System.currentTimeMillis();
							long time = endTime - startTime;
							sa.getResult().time = sa.getResult().getCreatedTime() - startTime;

							outputStream.write("Iter:" + iter + "\t Temp:" + temp + "\t My:" + my + "\t Time:" + time
									+ "\t ResultTime:" + sa.getResult().time + "\t Fitness:"
									+ sa.getResult().getFitness() + "\t GlobIt:" + sa.globalIterations + "\n");
							outputStreamM.write(iter + " " + temp + " " + my + " " + time + " " + sa.getResult().time
									+ " " + sa.getResult().getFitness() + " " + sa.globalIterations + "\n");
							System.out.println("Iter:" + iter + "\t Temp:" + temp + "\t My:" + my + "\t Time:" + time
									+ "\t ResultTime:" + sa.getResult().time + "\t Fitness:"
									+ sa.getResult().getFitness() + "\t GlobIt:" + sa.globalIterations);
							outputStream.flush();
							outputStreamM.flush();
						}
					}
				}
			}

			outputStream.close();
			outputStreamM.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void SA() {
		SA sa = new SA();
		sa.defaultSetup(path);
		sa.setSameValueLimit(Integer.MAX_VALUE);

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
	}

	public void GA() {
		GA ga = new GA();
		ga.defaultSetup(path);
		ga.setSamevalueLimit(Integer.MAX_VALUE);
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
	}

	public void GASA() {
		GA ga = new GA();
		ga.defaultSetup(path);

		// Set GA desiredFitness
		ga.setDesiredFitness(-9);
		ga.setSamevalueLimit(100);

		SA sa = new SA();
		sa.defaultSetupGASA(ga.kth, ga.constraints);
		sa.setSameValueLimit(Integer.MAX_VALUE);
		sa.setInitialTemperature(4.3);

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

			outputStream.write("Time limit: " + Metaheuristic.TIME_LIMIT + "\n");
			outputStream.write(config + "\n");

			outputStream.write(sb.toString());
			outputStream.flush();
			outputStream.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
