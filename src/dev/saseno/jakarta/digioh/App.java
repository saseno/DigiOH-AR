package dev.saseno.jakarta.digioh;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import com.github.sarxos.webcam.Webcam;
import com.jogamp.newt.event.KeyEvent;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.util.awt.TextRenderer;

import dev.saseno.jakarta.digioh.io.utils.NyARGlMarkerSystem;
import dev.saseno.jakarta.digioh.io.utils.NyARGlRender;
import dev.saseno.jakarta.digioh.jogl2.GlSketch;
import dev.saseno.jakarta.digioh.obj.Client;
import dev.saseno.jakarta.digioh.obj.Earth;
import dev.saseno.jakarta.digioh.obj.Love;
import dev.saseno.jakarta.digioh.obj.Mars;
import dev.saseno.jakarta.digioh.obj.Moon;
import dev.saseno.jakarta.digioh.obj.Patung;
import jp.nyatla.nyartoolkit.j2se.NyARBufferedImageRaster;
import jp.nyatla.nyartoolkit.markersystem.NyARMarkerSystemConfig;
import jp.nyatla.nyartoolkit.markersystem.NyARSensor;

@SuppressWarnings("unused")
public class App extends GlSketch {

	private Webcam camera;
	private boolean useCamera = true;
	private Dimension cameraDimension = null;
	
	private NyARGlMarkerSystem nyar;
	private NyARGlRender render;
	private NyARSensor sensor;
	
	private int id;	
	private int id_samsung;
	private int id_digiOH;
	private int id_cloud;
	private int id_insta;
	private int id_twitter;

	private final String ARCODE_FILE 	= "/patt.hiro";
	private final String patt_samsung 	= "/patt.samsung";
	private final String patt_digiOH 	= "/patt.digiOH";
	private final String patt_cloud 	= "/patt.cloud";
	private final String patt_insta 	= "/patt.insta";
	private final String patt_twitter 	= "/patt.twitter";

	private NyARBufferedImageRaster imgRaster = null;
	
	private Earth modelEarth = null;
	private Moon modelMoon = null;
	private Mars modelMars = null;
	private Love modelLove = null;
	private Patung modelPatung = null;
	private Client modelClient = null;

	private NyARBufferedImageRaster testImg = null;
	private BufferedImage img = null;
	private double rquad = 0.0;
	
	private String tempTestImg = "/320x240ABGR.png";	
	
	public App(int i_width, int i_height, boolean useCamera) {
		super(i_width, i_height);
		this.useCamera = useCamera;
	}
	
	public static void main(String[] args) {
		
		int w0 = 320; //640;
		int h0 = 240; //480;
		
		boolean userInputCamera = true;
		
		for (String arg : args) {
			if ("camera".equals(arg.trim())) {
				userInputCamera = true;
			}
		}
			
		System.out.println("------------------");
		System.out.println("START APP");
		System.out.println("------------------");
		
		App digiOhApp = new App(w0, h0, userInputCamera);		
		digiOhApp.run2();		
		
		return;
	}
	
	public void setup(GL gl) throws Exception {
		
		try {
						
			img = ImageIO.read(getClass().getResourceAsStream(tempTestImg));
			testImg = new NyARBufferedImageRaster(img);
			
		} catch (Exception e) {
			e.printStackTrace();
		}	
		
		modelEarth = new Earth();
		modelMoon = new Moon();
		modelMars = new Mars();
		modelLove = new Love();
		modelPatung = new Patung();
		modelClient = new Client();

		cameraDimension = new Dimension(i_width, i_height);
		
		if (useCamera) {

			for (Webcam webCam : Webcam.getWebcams()) {
				System.err.println("--> webCam: " + webCam);
			}
			
			camera = Webcam.getDefault();
			cameraDimension = camera.getViewSize();

			for (Dimension dim : camera.getViewSizes()) {
				System.err.println("--> dim: " + dim);
				if (cameraDimension.height < dim.height && cameraDimension.width < dim.width) {
					cameraDimension = dim;
				}
			}

			camera.setViewSize(cameraDimension);
		}
		
		NyARMarkerSystemConfig config = new NyARMarkerSystemConfig(i_width, i_height);
		
		nyar 	= new NyARGlMarkerSystem(config); // create MarkerSystem
		render 	= new NyARGlRender(nyar);
		sensor 	= new NyARSensor(config.getScreenSize());
		
		id 			= nyar.addARMarker(getClass().getResourceAsStream(ARCODE_FILE), 16, 25, 80);
		id_samsung 	= nyar.addARMarker(getClass().getResourceAsStream(patt_samsung), 16, 25, 80);
		id_digiOH 	= nyar.addARMarker(getClass().getResourceAsStream(patt_digiOH), 16, 25, 80);
		id_cloud 	= nyar.addARMarker(getClass().getResourceAsStream(patt_cloud), 16, 25, 80);
		id_insta 	= nyar.addARMarker(getClass().getResourceAsStream(patt_insta), 16, 25, 80);
		id_twitter 	= nyar.addARMarker(getClass().getResourceAsStream(patt_twitter), 16, 25, 80);		
		
		//textRenderer = new TextRenderer(new Font("SansSerif", Font.BOLD, 25));
		
		gl.glEnable(GL.GL_DEPTH_TEST);

		if (useCamera) {
			camera.open();
		}
	}
		
	public void draw(GL gl) throws Exception {
		//synchronized (imgRaster) {
			try {

				if (rquad >= 360) {
					rquad = 0;
				} else {
					rquad += 0.5;
				}

				if (useCamera) {
					imgRaster = new NyARBufferedImageRaster(camera.getImage());
				} else {
					imgRaster = testImg;
				}

				sensor.update(imgRaster);

				if (sensor.getSourceImage() == null) {
					System.err.println("--> source null...");
					return;
				}

				render.drawBackground(gl, sensor.getSourceImage(), false, i_width, i_height);
				render.loadARProjectionMatrix(gl);

				nyar.update(sensor);

				if (nyar.isExist(id)) {
					nyar.loadTransformMatrix(gl, id);
					render.colorCube(gl, 50, 0, 0, 20, rquad);

					// render.drawText(gl, 0, 0, 20, rquad, "test 123");
					// render.renderModel(gl, 0, 0, 20, rquad, modelEarth);
					// render.renderModel(gl, 0, 0, 0, rquad, modelPatung);
				}

				if (nyar.isExist(id_cloud)) {
					nyar.loadTransformMatrix(gl, id_cloud);
					render.renderModel(gl, 0, 0, 0, rquad, modelClient);
				}

				if (nyar.isExist(id_digiOH)) {
					nyar.loadTransformMatrix(gl, id_digiOH);
					render.renderModel(gl, 60, 0, 0, rquad, modelLove);
				}

				if (nyar.isExist(id_insta)) {
					nyar.loadTransformMatrix(gl, id_insta);
					render.renderModel(gl, 0, 0, 0, rquad, modelEarth);

				}

				if (nyar.isExist(id_twitter)) {
					nyar.loadTransformMatrix(gl, id_twitter);
					render.renderModel(gl, 0, -80, 0, rquad, modelPatung);

//				textRenderer.beginRendering(i_width, i_height);
//				textRenderer.setColor(1.0f, 0.2f, 0.2f, 0.8f);
//				textRenderer.draw("Twitter", 3, 3);
//				textRenderer.endRendering();
				}

				if (nyar.isExist(id_samsung)) {
					nyar.loadTransformMatrix(gl, id_samsung);
					render.renderModel(gl, 0, 0, 0, rquad, modelClient);
				}

				Thread.sleep(1);

			} catch (Exception e) {
				e.printStackTrace();
			}
		//}
	}

	@Override
	public void keyTyped(java.awt.event.KeyEvent e) {
	}

	@Override
	public void keyPressed(java.awt.event.KeyEvent e) {		
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			System.exit(0);
		}
	}

	@Override
	public void keyReleased(java.awt.event.KeyEvent e) {
	}
}