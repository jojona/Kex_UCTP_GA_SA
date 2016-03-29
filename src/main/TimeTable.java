package main;

public class TimeTable implements Comparable<TimeTable> {
  private int fitness;

  // The timetables for each room
  private RoomTimeTable[] roomTimeTables;

  public TimeTable(int numRooms) {
    roomTimeTables = new RoomTimeTable[numRooms];
  }
  
  public TimeTable(TimeTable tt) {
	  roomTimeTables = new RoomTimeTable[tt.roomTimeTables.length];
	  for (int i = 0; i < tt.roomTimeTables.length; i++) {
		roomTimeTables[i] = new RoomTimeTable(tt.roomTimeTables[i]);
	  }
	  
  }
  
  public int getFitness() {
    return fitness;
  }

  public void setFitness(int fitness) {
    this.fitness = fitness;
  }

  public RoomTimeTable[] getRoomTimeTables() {
    return roomTimeTables;
  }

  public void putRoomTimeTable(int i, RoomTimeTable rtt) {
    roomTimeTables[i] = rtt;
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (RoomTimeTable rtt : roomTimeTables) {
      sb.append(rtt.toString());
      sb.append("\n");
    }

    return sb.toString();
  }
  
  // sorts descending
  @Override
  public int compareTo(TimeTable other) {
    int otherFitness = other.getFitness();

    if (fitness > otherFitness)
      return -1;
    else if (fitness == otherFitness)
      return 0;
    else
      return 1;
  }
}
