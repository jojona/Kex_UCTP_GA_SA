package main;
public class RoomTimeTable {

  public static final int NUM_DAYS = 4;
  public static final int NUM_TIMESLOTS = 4;

  // matrix of timeslots
  // with id of what event that is bookes in each slot
  // rows are timeslots, columns are days
  private int[][] timeSlots;

  private Room room;

  public RoomTimeTable(Room room) {
    this.room = room;
    timeSlots = new int[NUM_TIMESLOTS][NUM_DAYS];
  }
  
  public RoomTimeTable(RoomTimeTable rtt) {
	  room = rtt.room;
	  
	  timeSlots = new int[NUM_TIMESLOTS][NUM_DAYS];
	  for(int x = 0; x < NUM_TIMESLOTS; x++) {
		  for(int y = 0; y < NUM_DAYS; y++) {
			  timeSlots[x][y] = rtt.timeSlots[x][y];
		  }
	  }
  }

  public boolean hasEvent(int day, int timeslot) {
    if(timeSlots[timeslot][day] == 0) {
      return false;
    } else {
      return true;
    }
  }

  public int getEvent(int day, int timeslot) {
    return timeSlots[timeslot][day];
  }

  public void setEvent(int day, int timeslot, int eventId) {
    timeSlots[timeslot][day] = eventId;
  }

  public Room getRoom() {
    return room;
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("Room: " + room.getName() + "\n");
    for (int timeslot = 0; timeslot < NUM_TIMESLOTS; timeslot++) {
      for (int day = 0; day < NUM_DAYS; day++) {
        sb.append("[\t" + timeSlots[timeslot][day] + "\t]");
      }
      sb.append("\n");
    }

    return sb.toString();
  }
}
