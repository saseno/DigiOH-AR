package mainclass;

import dev.saseno.jakarta.digioh.App;

public class RunnerClass {
	
	public static void main(String[] args) {
		
		int w0 = 320;
		int h0 = 240;
		
		boolean userInputCamera = true; //isUseCamera(args);
					
		System.out.println("------------------");
		System.out.println("START APP");
		System.out.println("------------------");
		
		App digiOhApp = new App(w0, h0, userInputCamera);		
		if (userInputCamera) {
			digiOhApp.run2();
		} else {
			digiOhApp.run();
		}
		
		return;
	}
	
	@SuppressWarnings("unused")
	private static boolean isUseCamera(String[] args) {
		boolean result = false;
		
		for (String arg : args) {
			if ("camera".equals(arg.trim())) {
				result = true;
				break;
			}
		}
		
		return result;
	}
	
}
