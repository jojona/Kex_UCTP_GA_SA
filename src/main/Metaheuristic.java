package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class Metaheuristic {
	
	  protected KTH kth;
	  protected Constraints constraints;
	  
	  public Metaheuristic() {
		 
	  }

	//////////////////////////
	// SETUP
	//////////////////////////

	public void loadData(String dataFileUrl) {
		kth = new KTH();
		kth.clear(); // reset all previous data before loading

		try {
			File file = new File(dataFileUrl);
			BufferedReader in = new BufferedReader(new FileReader(file));
			String line = null;
			// input data sections are read in the following order separated by
			// #
			// #rooms <name> <capacity> <type>
			// #courses <id> <name> <numLectures> <numClasses> <numLabs>
			// #lecturers <name> <course>+
			// #studentgroups <name> <numStudents> <course>+
			String readingSection = null;
			String roomName = null;
			String courseName = null;
			String lecturerName = null;
			String studentGroupName = null;
			HashMap<String, Integer> courseNameToId = new HashMap<String, Integer>();
			while ((line = in.readLine()) != null) {
				String[] data = line.split(" ");
				if (data[0].charAt(0) == '#') {
					readingSection = data[1];
					data = in.readLine().split(" ");
				}
				if (readingSection.equals("ROOMS")) {
					roomName = data[0];
					int cap = Integer.parseInt(data[1]);
					Event.Type type = Event.generateType(Integer.parseInt(data[2]));
					Room room = new Room(roomName, cap, type);
					kth.addRoom(room);
				} else if (readingSection.equals("COURSES")) {
					courseName = data[0];
					int numLectures = Integer.parseInt(data[1]);
					int numLessons = Integer.parseInt(data[2]);
					int numLabs = Integer.parseInt(data[3]);
					Course course = new Course(courseName, numLectures, numLessons, numLabs);
					courseNameToId.put(courseName, course.getId());
					kth.addCourse(course);
				} else if (readingSection.equals("LECTURERS")) {
					lecturerName = data[0];
					Lecturer lecturer = new Lecturer(lecturerName);
					for (int i = 1; i < data.length; i++) {
						// register all courses that this lecturer may teach
						courseName = data[i];
						lecturer.addCourse(kth.getCourses().get(courseNameToId.get(courseName)));
					}
					kth.addLecturer(lecturer);
				} else if (readingSection.equals("STUDENTGROUPS")) {
					studentGroupName = data[0];
					int size = Integer.parseInt(data[1]);
					StudentGroup studentGroup = new StudentGroup(studentGroupName, size);
					for (int i = 2; i < data.length; i++) {
						courseName = data[i];
						studentGroup.addCourse(kth.getCourses().get(courseNameToId.get(courseName)));
					}
					kth.addStudentGroup(studentGroup);
				}
			}
			kth.createEvents(); // create all events
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		constraints = new Constraints(kth);
	}

}
