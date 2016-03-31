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

		for (int i = 0; i < 10; i++) {
			Main.output = Main.outputName + Main.file + "_" + i + "_";
			System.out.println("Lap: " + i);
			Main main = new Main();
			main.SA();

			main.GA();

			main.GASA();
		}
	}

	public void SA() {
		SA sa = new SA();
		sa.setup(path);
		sa.SAMEVALUE_LIMIT = Integer.MAX_VALUE;
		
		// Start time
		long startTime = System.currentTimeMillis();
		
		sa.run(startTime);
		
		// End time
		long endTime = System.currentTimeMillis();
		long time = endTime - startTime;
		
		sa.getResult().time = sa.getResult().getCreatedTime() - startTime;
		System.out.println("SA done");
		printTimeTable(sa.getResult(), time, "SA", "Globalit: " + sa.globalIterations, sa.kth, sa.getConf());
	}

	public void GA() {
		GA ga = new GA();
		ga.setup(path);
		ga.setSamevalueLimit(Integer.MAX_VALUE);
		// Start time
		long startTime = System.currentTimeMillis();
		
		TimeTable bestTimeTable = ga.generateTimeTable(startTime);
		
		// End time
		long endTime = System.currentTimeMillis();
		long time = endTime - startTime;
		
		bestTimeTable.time = bestTimeTable.getCreatedTime() - startTime;
		
		System.out.println("GA done");
		printTimeTable(bestTimeTable, time, "GA", "Generations: " + ga.numGenerations, ga.kth, ga.getConf());
	}

	public void GASA() {
		GA ga = new GA();
		ga.setup(path);
		
		// Set GA desiredFitness
		ga.setDesiredFitness(-9);
		ga.setSamevalueLimit(100);
		
		SA sa = new SA();
		sa.setup(ga.kth, ga.constraints);

		// Start time
		long startTime = System.currentTimeMillis();
		
		TimeTable bestTimeTable = ga.generateTimeTable(startTime);

		sa.setSolution(bestTimeTable);
		// TODO Set SA temperature?
		sa.setInitialTemperature(4.3);
		sa.run(startTime);
		
		// End time
		long endTime = System.currentTimeMillis();
		long time = endTime - startTime;
		sa.getResult().time = sa.getResult().getCreatedTime() - startTime;
		
		System.out.println("GA_SA done");
		printTimeTable(sa.getResult(), time, "GASA",
				"Globalit: " + sa.globalIterations + " Generations: " + ga.numGenerations, sa.kth, sa.getConf() + ga.getConf());
	}

	public void printTimeTable(TimeTable tt, long time, String name, String result, KTH kth, String config) {
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
						if(eventId != 0) {
						Event event = kth.getEvent(eventId);
						sb.append("[ " + event.getCourse().getId() + " " +
						event.getStudentGroup().getName() + " ");
						if(event.getType() == Event.Type.LECTURE) {
						sb.append(event.getLecturer().getName() + " ]"); } else {
						sb.append("    ]"); } } else { sb.append("[      -     ]");
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
			
			outputStream.write(Metaheuristic.TIME_LIMIT + "\n");
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
