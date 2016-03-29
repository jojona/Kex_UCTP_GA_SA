package main;

import GA.src.GUI;
import SA.SA;

public class Main {
	
	public static void main(String [] args) {
		
		Main main = new Main();
		
		//main.SA();
		
		main.GA();
	}
	
	public void SA() {
		SA sa = new SA();
		
		sa.run("src/GA/input/midkth");
	}
	
	public void GA() {
		GUI gui = new GUI();
	    gui.setVisible(true);
	}
	
	  
	
}

