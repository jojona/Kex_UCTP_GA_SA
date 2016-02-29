
public class SA {
	
	private int INITIALTEMPERATURE = 10;
	
	private int iterations;
	private int temperature;
	
	
	public SA() {
		int s = initialSolution();
		temperature = 5;
		
		// TODO select cooling schedule
		boolean stop = false;
		while(!stop) {
			for(int i = 0; i < iterations; i++) {
				int currentSolution = neighbourSearch();
				int delta = 0;// TODO calculate fitness difference
				if (delta < 0 || (true)) { // (exp(-delta/t) < rand[0, 1])
					s = currentSolution;
					
				}
			}
			temperature = nextTemperature();	
		}
	}
	
	/**
	 * Calculate initial solution
	 * @return
	 */
	private int initialSolution() {
		
		return 0;
	}
	
	/**
	 * Calculate initial temperature
	 */
	private int temperature() {
		return 0;
	}
	
	/**
	 * Neighbourhood search
	 * @return next neighbour
	 */
	private int neighbourSearch() {
		
		return 0;
	}
	
	/**
	 * Cooling schedule
	 * @return next temperature
	 */
	private int nextTemperature() {
		int t = 10;
		return t;
	}

}
