package main;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import GA.src.GA;
import SA.SA;

public class Main {
	static String path = "src/GA/input/";
	static String file = "kth_XS";
	static String outputName = "output/testOutput";

	static String output;
	
	public static void main(String[] args) {
		Main.path += Main.file;

		for (int i = 0; i < 10; i++) {
			Main.output = Main.outputName + i;
			Main.output += "_" + Main.file + "_";
			Main main = new Main();
			main.SA();

			main.GA();

			main.GASA();
		}
	}

	public void SA() {
		SA sa = new SA();
		sa.setup(path);
		sa.printConf();
		sa.run();

		printTimeTable(sa.getResult(), "SA", "Globalit: " + sa.globalIterations);
	}

	public void GA() {
		GA ga = new GA();
		ga.setup(path);
		ga.printConf();
		TimeTable bestTimeTable = ga.generateTimeTable();
		printTimeTable(bestTimeTable, "GA", "Generations: " + ga.numGenerations);
	}

	public void GASA() {
		GA ga = new GA();
		ga.setup(path);

		// TODO ga.setStopCondition();
		ga.printConf();

		TimeTable bestTimeTable = ga.generateTimeTable();

		SA sa = new SA();
		sa.setup(bestTimeTable, ga.kth, ga.constraints);
		sa.printConf();
		sa.run();

		printTimeTable(sa.getResult(), "GASA",
				"Globalit: " + sa.globalIterations + " Generations: " + ga.numGenerations);
	}

	public void printTimeTable(TimeTable tt, String name, String result) {
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
					/*
					 * if(eventId != 0) { //Event event = events.get(eventId);
					 * sb.append("[ " + event.getCourse().getId() + " " +
					 * event.getStudentGroup().getName() + " ");
					 * if(event.getType() == Event.Type.LECTURE) {
					 * sb.append(event.getLecturer().getName() + " ]"); } else {
					 * sb.append("    ]"); } } else { sb.append("[    -    ]");
					 * }
					 */
					sb.append("[\t" + eventId + "\t]");
				}
				sb.append("\n");
			}
		}

		BufferedWriter outputStream;
		try {
			outputStream = new BufferedWriter(new FileWriter(output + "_" + name + ".txt"));
			outputStream.write(sb.toString());
			outputStream.write("Number of slots: " + nrSlots + "\n");
			outputStream.write("Number of events: " + nrEvents + "\n");
			outputStream.write("Sparseness: " + ((double) nrEvents / (double) nrSlots) + "\n");
			outputStream.write("\n" + result + "\n");
			outputStream.flush();
			outputStream.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
