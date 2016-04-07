package GA.src;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;
import java.util.Random;

import SA.SA;
import main.Constraints;
import main.Event;
import main.KTH;
import main.Room;
import main.RoomTimeTable;
import main.TimeTable;

// keeps all the TimeTables for a generation
public class Population {

  // time slot class used when creating the random population
  public class TimeSlot {
    private int roomId;
    private int day;
    private int timeSlot;
    public TimeSlot(int roomId, int day, int timeSlot) {
      this.roomId = roomId;
      this.day = day;
      this.timeSlot = timeSlot;
    }
  }

  // should be ordered when selecting the best individuals
  private LinkedList<TimeTable> individuals;

  public Population() {
    individuals = new LinkedList<TimeTable>();
  }

  public void createRandomIndividuals(int numIndividuals, KTH kth) {
    Map<Integer, Room> rooms = kth.getRooms();
    int numRooms = kth.getRooms().size();
    for(int i = 0; i < numIndividuals; i++) {
    	TimeTable tt = generateRandomInvidual(kth, rooms, numRooms);
    	individuals.add(tt);
    }
  }
  
  public void createGoodInviduals(int numIndividuals, KTH kth) {
	  Map<Integer, Room> rooms = kth.getRooms();
	    int numRooms = kth.getRooms().size();
	    for(int i = 0; i < numIndividuals; i++) {
	    	TimeTable tt = createGoodInvidual(kth, rooms, numRooms);
	    	individuals.add(tt);
	    	System.out.println("Another one " + i);
	    }
  }
  
  public TimeTable createGoodInvidual(KTH kth, Map<Integer, Room> rooms, int numRooms) {
	  SA sa = new SA();
	  sa.defaultSetupGASA(kth, new Constraints(kth));
	  sa.setSolution(generateRandomInvidual(kth, rooms, numRooms));
	  sa.setDesiredFitness(-1000);
	  sa.setTimeLimit(3000);
	  sa.run(System.currentTimeMillis());
	  return sa.getResult();
  }

  public TimeTable generateRandomInvidual(KTH kth, Map<Integer, Room> rooms, int numRooms)  {
	// register all available timeslots
      ArrayList<TimeSlot> availableTimeSlots = new ArrayList<TimeSlot>();
      for(int roomId : rooms.keySet()) {
        for(int d = 0; d < RoomTimeTable.NUM_DAYS; d++) {
          for(int t = 0; t < RoomTimeTable.NUM_TIMESLOTS; t++) {
            availableTimeSlots.add(new TimeSlot(roomId, d, t));
          }
        }
      }

      TimeTable tt = new TimeTable(numRooms);
      for(int roomId : rooms.keySet()) {
        Room room = rooms.get(roomId);
        RoomTimeTable rtt = new RoomTimeTable(room);
        tt.putRoomTimeTable(roomId, rtt);
      }

      // assign all event to any randomly selected available timeslot
      Random rand = new Random(System.currentTimeMillis());
      for(Event e : kth.getEvents().values()) {
        TimeSlot availableTimeSlot = availableTimeSlots.get(rand.nextInt(availableTimeSlots.size()));
        RoomTimeTable rtt = tt.getRoomTimeTables()[availableTimeSlot.roomId];
        rtt.setEvent(availableTimeSlot.day, availableTimeSlot.timeSlot, e.getId());
        availableTimeSlots.remove(availableTimeSlot);
        /* DEBUG
        System.out.println("==============");
        System.out.println("ROOM TIME TABLE ID: " + rtt.getRoom().getName());
        System.out.println("Day: " + availableTimeSlot.day + " Timeslot: " + availableTimeSlot.timeSlot + " Event ID: " + e.getId());
        */
      }
      availableTimeSlots.clear();
	  return tt;
  }
  
  // assumes sorted
  public TimeTable getTopIndividual() {
    return individuals.get(0);
  }

  public TimeTable getWorstIndividual() {
    return individuals.getLast();
  }

  public void addIndividual(TimeTable tt) {
    individuals.add(tt);
  }

  public TimeTable getIndividual(int i) {
    return individuals.get(i);
  }

  public void addIndividualSorted(TimeTable tt) {
    ListIterator<TimeTable> it = individuals.listIterator();
    ListIterator<TimeTable> it2 = individuals.listIterator();

    while (it.hasNext()) {
      if (it.next().getFitness() < tt.getFitness()) {
        it2.add(tt);
        break;
      }

      it2.next();
    }
  }

  public ListIterator<TimeTable> listIterator() {
    return individuals.listIterator();
  }

  public void sortIndividuals() {
    Collections.sort(individuals);
  }

  public int size() {
    return individuals.size();
  }
}
