package main;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class Constraints {

	KTH kth;

	public Constraints(KTH kth) {
		this.kth = kth;
	}

	//////////////////////////
	// FITNESS
	//////////////////////////

	public void fitness(TimeTable tt) {
		// set the fitness to this time table

		int studentGroupDoubleBookings = studentGroupDoubleBookings(tt);
		int lecturerDoubleBookings = lecturerDoubleBookings(tt);
		int roomTypeBreaches = roomTypeBreaches(tt);
		
		// Hard;
		int numBreaches = studentGroupDoubleBookings * 2 + lecturerDoubleBookings + roomTypeBreaches * 4;
		numBreaches *= 1;

		//numBreaches += softConstraints(tt);

		int fitness = -1 * numBreaches;
		tt.setFitness(fitness);
	}

	public int hardConstraints(TimeTable tt) {
		int studentGroupDoubleBookings = studentGroupDoubleBookings(tt);
		int lecturerDoubleBookings = lecturerDoubleBookings(tt);
		int roomTypeBreaches = roomTypeBreaches(tt);

		// Hard;
		int numBreaches = studentGroupDoubleBookings + lecturerDoubleBookings + roomTypeBreaches;
		return numBreaches;
	}

	public int softConstraints(TimeTable tt) {
		return 4 * minimumWorkingDays(tt) + 2 * sameRoom(tt) + lastTimeslot(tt) + 4 * roomCapacityBreaches(tt) + 2 * studentOneEventDay(tt);
	}

	//////////////////////////
	// CONSTRAINTS
	//////////////////////////

	///////////////////
	// Hard constraints, each function returns the number of constraint breaches
	///////////////////

	// NOTE: Two of the hard constraints are solved by the chosen datastructure
	// Invalid timeslots may not be used
	// A room can not be double booked at a certain timeslot

	private int studentGroupDoubleBookings(TimeTable tt) {
		int numBreaches = 0;

		RoomTimeTable[] rtts = tt.getRoomTimeTables();

		for (int timeslot = 0; timeslot < RoomTimeTable.NUM_TIMESLOTS; timeslot++) {
			for (int day = 0; day < RoomTimeTable.NUM_DAYS; day++) {
				for (StudentGroup sg : kth.getStudentGroups().values()) {

					HashMap<Integer, Integer> eventGroupCounts = new HashMap<Integer, Integer>();

					for (RoomTimeTable rtt : rtts) {
						int eventID = rtt.getEvent(day, timeslot);

						// only look at booked timeslots
						if (eventID != 0) {
							Event event = kth.getEvent(eventID);
							int sgID = event.getStudentGroup().getId();

							// if this bookings is for the current studentgroup
							if (sgID == sg.getId()) {
								int eventGroupID = event.getEventGroupId();

								// increment the count for this event group id
								if (!eventGroupCounts.containsKey(eventGroupID)) {
									eventGroupCounts.put(eventGroupID, 1);

								} else {
									int oldCount = eventGroupCounts.get(eventGroupID);
									eventGroupCounts.put(eventGroupID, oldCount + 1);
								}
							}
						}
					}

					// find the biggest event group
					int biggestGroupSize = 0;
					int sumGroupSize = 0;
					for (Map.Entry<Integer, Integer> entry : eventGroupCounts.entrySet()) {

						sumGroupSize += entry.getValue();

						if (entry.getValue() > biggestGroupSize) {
							biggestGroupSize = entry.getValue();
						}
					}

					numBreaches += sumGroupSize - biggestGroupSize;
				}
			}
		}
		return numBreaches;
	}

	// num times a lecturer is double booked
	// NOTE: lecturers are only booked to lectures
	// for the labs and classes, TAs are used and they are assumed to always
	// be available
	private int lecturerDoubleBookings(TimeTable tt) {
		int numBreaches = 0;

		RoomTimeTable[] rtts = tt.getRoomTimeTables();

		for (Lecturer lecturer : kth.getLecturers().values()) {

			// for each time
			for (int timeslot = 0; timeslot < RoomTimeTable.NUM_TIMESLOTS; timeslot++) {

				for (int day = 0; day < RoomTimeTable.NUM_DAYS; day++) {
					int numBookings = 0;

					for (RoomTimeTable rtt : rtts) {
						int eventID = rtt.getEvent(day, timeslot);

						// 0 is unbooked
						if (eventID != 0) {
							Event event = kth.getEvent(eventID);
							// only check lectures since lecturers are only
							// attached to lecture events
							if (event.getType() == Event.Type.LECTURE) {
								if (event.getLecturer().getId() == lecturer.getId()) {
									numBookings++;
								}
							}
						}
					}

					// only one booking per time is allowed
					if (numBookings > 1) {

						// add all extra bookings to the number of constraint
						// breaches
						numBreaches += numBookings - 1;
					}
				}
			}
		}

		return numBreaches;
	}

	// num times an event is booked to the wrong room type
	private int roomTypeBreaches(TimeTable tt) {
		int numBreaches = 0;

		RoomTimeTable[] rtts = tt.getRoomTimeTables();

		for (RoomTimeTable rtt : rtts) {
			Event.Type roomType = rtt.getRoom().getType();

			// for each time
			for (int timeslot = 0; timeslot < RoomTimeTable.NUM_TIMESLOTS; timeslot++) {

				for (int day = 0; day < RoomTimeTable.NUM_DAYS; day++) {
					int eventID = rtt.getEvent(day, timeslot);

					// only look at booked timeslots
					if (eventID != 0) {
						Event.Type type = kth.getEvent(eventID).getType();
						if (roomType != type) {
							numBreaches++;
						}
					}
				}
			}
		}

		return numBreaches;
	}

	///////////////////
	// Soft constraints, each function returns the number of constraint breaches
	///////////////////

	private int minimumWorkingDays(TimeTable tt) {
		int numBreaches = 0;

		RoomTimeTable[] rtts = tt.getRoomTimeTables();

		for (int day = 0; day < RoomTimeTable.NUM_DAYS; day++) {

			for (Course course : kth.getCourses().values()) {
				int lecturesThisDay = 0;
				for (int timeslot = 0; timeslot < RoomTimeTable.NUM_TIMESLOTS; timeslot++) {

					for (RoomTimeTable rtt : rtts) {
						int eventID = rtt.getEvent(day, timeslot);

						if (eventID != 0) {
							Event event = kth.getEvent(eventID);

							if (event.getCourse() == course && event.getType() == Event.Type.LECTURE) {
								lecturesThisDay++;
							}
						}

					}
				}
				if (lecturesThisDay > 0) {
					numBreaches += lecturesThisDay - 1;
				}
			}
		}

		return numBreaches;
	}

	private int sameRoom(TimeTable tt) {
		int numBreaches = 0;

		RoomTimeTable[] rtts = tt.getRoomTimeTables();

		for (Course course : kth.getCourses().values()) {
			LinkedList<Room> rooms = new LinkedList<Room>();
			for (int day = 0; day < RoomTimeTable.NUM_DAYS; day++) {
				for (int timeslot = 0; timeslot < RoomTimeTable.NUM_TIMESLOTS; timeslot++) {

					for (RoomTimeTable rtt : rtts) {
						int eventID = rtt.getEvent(day, timeslot);

						if (eventID != 0) {
							Event event = kth.getEvent(eventID);

							if (event.getCourse() == course && event.getType() == Event.Type.LECTURE) {
								if (!rooms.contains(rtt.getRoom())) {
									rooms.add(rtt.getRoom());
								}
							}
						}
					}
				}
			}
			if (rooms.size() > 0) {
				numBreaches += rooms.size() - 1;
			}
		}

		return numBreaches;
	}

	// A student has a class in the last timeslot of a day
	private int lastTimeslot(TimeTable tt) {
		RoomTimeTable[] rtts = tt.getRoomTimeTables();
		int numBreaches = 0;

		for (RoomTimeTable rtt : rtts) {
			for (int day = 0; day < RoomTimeTable.NUM_DAYS; day++) {
				int timeslot = RoomTimeTable.NUM_TIMESLOTS - 1;
				if (rtt.getEvent(day, timeslot) != 0) {
					numBreaches++;
				}
			}
		}
		return numBreaches;
	}

	// num times a room is too small for the event booked
	private int roomCapacityBreaches(TimeTable tt) {
		int numBreaches = 0;

		RoomTimeTable[] rtts = tt.getRoomTimeTables();

		for (RoomTimeTable rtt : rtts) {
			int roomSize = rtt.getRoom().getCapacity();

			// for each time
			for (int timeslot = 0; timeslot < RoomTimeTable.NUM_TIMESLOTS; timeslot++) {

				for (int day = 0; day < RoomTimeTable.NUM_DAYS; day++) {
					int eventID = rtt.getEvent(day, timeslot);

					// only look at booked timeslots
					if (eventID != 0) {
						int eventSize = kth.getEvent(eventID).getSize();
						if (roomSize < eventSize) {
							numBreaches++;
						}
					}
				}
			}
		}
		return numBreaches;
	}

	private int studentOneEventDay(TimeTable tt) {
		int numBreaches = 0;

		RoomTimeTable[] rtts = tt.getRoomTimeTables();

		for (StudentGroup sg : kth.getStudentGroups().values()) {
			for (int day = 0; day < RoomTimeTable.NUM_DAYS; day++) {
				int dailyEvents = 0;
				for (int timeslot = 0; timeslot < RoomTimeTable.NUM_TIMESLOTS; timeslot++) {
					for (RoomTimeTable rtt : rtts) {
						int eventID = rtt.getEvent(day, timeslot);

						// only look at booked timeslots
						if (eventID != 0) {
							Event event = kth.getEvent(eventID);
							int sgID = event.getStudentGroup().getId();

							// if this bookings is for the current studentgroup
							if (sgID == sg.getId()) {
								dailyEvents++;
							}
						}
					}
					if (dailyEvents == 1) {
						numBreaches++;
					}
				}
			}
		}

		return numBreaches;
	}
}
