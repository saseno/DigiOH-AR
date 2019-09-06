package mainclass;

import dev.saseno.jakarta.digioh.App;

public class RunnerClass extends App {

	protected boolean mirror = true;
	
	public RunnerClass(int i_width, int i_height, boolean useCamera) {
		super(i_width, i_height, useCamera);
	}
	
	@Override
	protected boolean isMirrored() {
		return this.mirror;
	}
	
	public static void main(String[] args) {
		
		int w0 = 320;
		int h0 = 240;
		
		boolean userInputCamera = true;
		
		for (String arg : args) {
			if ("camera".equals(arg.trim())) {
				userInputCamera = true;
			}
		}
			
		System.out.println("------------------");
		System.out.println("START APP");
		System.out.println("------------------");
		
		RunnerClass digiOhApp = new RunnerClass(w0, h0, userInputCamera);		
		if (userInputCamera) {
			digiOhApp.run2();
		} else {
			digiOhApp.run();
		}
		
		return;
	}
	
}
