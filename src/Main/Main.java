package Main;

import GA.src.GUI;

public class Main {
	
	public static void main(String [] args) {
		
		Main main = new Main();
		main.GA();
	}
	
	public void SA() {
		//SA sa = new SA();
	}
	
	public void GA() {
		GUI gui = new GUI();
	    gui.setVisible(true);
	}
	
}

