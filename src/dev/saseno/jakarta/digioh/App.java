package dev.saseno.jakarta.digioh;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;

import com.github.sarxos.webcam.Webcam;
import com.jogamp.newt.event.KeyEvent;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.util.GLBuffers;
import com.jogamp.opengl.util.awt.AWTGLReadBufferUtil;

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

	protected boolean useCamera = false;
	protected boolean mirror = false;
	
	private Webcam camera;
	
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
	private DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HHmmss");
	
	private GL currentGL = null;
	private AWTGLReadBufferUtil util = new AWTGLReadBufferUtil(GLProfile.getGL2ES2(), true);
	
	private int startCaptureScreen = 0;
	private long startCaptureScreenTime = 0;
	
	public App(int i_width, int i_height, boolean useCamera) {
		super(i_width, i_height);
		this.useCamera = useCamera;
		
		initCamera();
	}
	
	public static void main(String[] args) {
		
		int w0 = 320; //640;
		int h0 = 240; //480;
		
		boolean userInputCamera = false;
		
		for (String arg : args) {
			if ("camera".equals(arg.trim())) {
				userInputCamera = true;
			}
		}
			
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
	
	private void initModel() {
		
		modelEarth 	= new Earth();
		modelMoon 	= new Moon();
		modelMars 	= new Mars();
		modelLove 	= new Love();
		modelPatung = new Patung();
		modelClient = new Client();
	}
	
	private void initCamera() {
		try {

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
			//size(cameraDimension.width, cameraDimension.height);	
			
			//config = new NyARMarkerSystemConfig(cameraDimension.width, cameraDimension.height);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setup(GL gl) throws Exception {
				
		try {
						
			img = ImageIO.read(getClass().getResourceAsStream(tempTestImg));
			testImg = new NyARBufferedImageRaster(img);
			
		} catch (Exception e) {
			e.printStackTrace();
		}	

		NyARMarkerSystemConfig config = new NyARMarkerSystemConfig(cameraDimension.width, cameraDimension.height);
		
		if (useCamera) {
			//initCamera(config);
			
		} else {
			size(cameraDimension.width, cameraDimension.height);	
		}		
		
		nyar 	= new NyARGlMarkerSystem(config);
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

		initModel();
		
		if (useCamera) {
			camera.open();
		}
		
		currentGL = gl;
	}
	
	private void updateRotation() {
		if (rquad == 0) {
			rquad = 360;
		} else {
			rquad -= 0.5;
		}
	}
	
	protected boolean isMirrored() {
		return mirror;
	}
		
	public void draw(GL gl) throws Exception {
		//synchronized (camera) {		
		try {

			updateRotation();
			
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

			render.drawBackground(gl, sensor.getSourceImage(), isMirrored(), cameraDimension.getWidth(),
					cameraDimension.getHeight());
			render.loadARProjectionMatrix(gl, isMirrored());
			
			nyar.update(sensor);

			if (nyar.isExist(id)) {
				nyar.loadTransformMatrix(gl, id);
				//render.colorCube(gl, 50, 0, 0, 20, rquad);
				//render.renderModel(gl, 60, 0, 0, rquad, modelLove);
				//render.renderModel(gl, 60, 0, 0, rquad, modelClient);
								
				//render.drawText(gl, 0, 0, 20, rquad, "test 123");
				//render.renderModel(gl, 0, 0, 20, rquad, modelMoon);
				render.renderModel(gl, 0, 0, 0, rquad, modelPatung);
			} 

			if (nyar.isExist(id_cloud)) {
				nyar.loadTransformMatrix(gl, id_cloud);
				render.renderModel(gl, 0, 0, 0, rquad, modelMars);
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
			
			
			if ((startCaptureScreen >= 0) && (startCaptureScreenTime + 1000000 >= System.currentTimeMillis() - startCaptureScreenTime)) {
				
				if (startCaptureScreen == 0) {
					System.err.println("--> captured...");
				} else {
					System.err.println("--> countDown: " + startCaptureScreen);
				}

				startCaptureScreen--;
				startCaptureScreenTime = System.currentTimeMillis();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}	
		//}
	}
	
	protected void saveImage() {

		try {
						
			Path location = Paths.get(App.class.getProtectionDomain().getCodeSource().getLocation()
				    .toURI());

			//File outputfile = new File(location + "/" + "DigiOH_photo-" + dateFormat.format(new Date()) + ".png");
			//System.err.println("--> savedImageTo: " + outputfile.getAbsolutePath());
			//System.err.println("--> savedImageTo: " + outputfile.exists());
			
			BufferedImage currImage = util.readPixelsToBufferedImage(currentGL, false);
			System.err.println("--> currImage: " + currImage);
			
			BufferedImage screenshot = new BufferedImage(cameraDimension.width, cameraDimension.height,
					BufferedImage.TYPE_INT_RGB);
			
			File outputfile2 = new File("d:/DigiOH_photo-" + dateFormat.format(new Date()) + ".png");
			ImageIO.write(currImage, "png", outputfile2);
			
//			BufferedImage tScreenshot = Screenshot.readToBufferedImage(0,0, i_width, i_height, false);
//			File tScreenCaptureImageFile = new File(tFileName);
//			ImageIO.write(tScreenshot, "png", tScreenCaptureImageFile);			
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		try {
			BufferedImage screenshot = new BufferedImage(cameraDimension.width, cameraDimension.height,
					BufferedImage.TYPE_INT_RGB);
			Graphics graphics = screenshot.getGraphics();

			ByteBuffer buffer = GLBuffers.newDirectByteBuffer(cameraDimension.width * cameraDimension.height * 4);
			// be sure you are reading from the right fbo (here is supposed to be the
			// default one)
			// bind the right buffer to read from
			currentGL.getGL2().glReadBuffer(GL2.GL_BACK);
			
			// if the width is not multiple of 4, set unpackPixel = 1
			currentGL.getGL2().glReadPixels(0, 0, cameraDimension.width, cameraDimension.height, GL2.GL_RGBA,
					GL2.GL_UNSIGNED_BYTE, buffer);

			for (int h = 0; h < cameraDimension.height; h++) {
				for (int w = 0; w < cameraDimension.width; w++) {
					// The color are the three consecutive bytes, it's like referencing
					// to the next consecutive array elements, so we got red, green, blue..
					// red, green, blue, and so on..+ ", "
					graphics.setColor(new Color((buffer.get() & 0xff), (buffer.get() & 0xff), (buffer.get() & 0xff)));
					buffer.get(); // consume alpha
					graphics.drawRect(w, cameraDimension.height - h, 1, 1); // height - h is for flipping the image
				}
			}
			// This is one util of mine, it make sure you clean the direct buffer
			//BufferUtils.destroyDirectBuffer(buffer);

			File outputfile = new File("D:\\Downloads\\texture" + dateFormat.format(new Date()) + ".png");
			ImageIO.write(screenshot, "png", outputfile);
		} catch (Exception ex) {
		}
	}

	@Override
	public void keyTyped(java.awt.event.KeyEvent e) {
		// TODO Auto-generated method stub		
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

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2) {
			System.err.println("--> double clicked");
			//saveImage();
			
			startCaptureScreen = 5;
			startCaptureScreenTime = System.currentTimeMillis();
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {		
	}

	@Override
	public void mousePressed(MouseEvent e) {		
	}

	@Override
	public void mouseReleased(MouseEvent e) {		
	}
}