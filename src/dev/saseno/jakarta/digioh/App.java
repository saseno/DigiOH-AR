package dev.saseno.jakarta.digioh;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
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
import com.jogamp.opengl.util.awt.ImageUtil;
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

public class App extends GlSketch {

	protected boolean useCamera = false;
	protected boolean mirror = true;
	
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
	
	private TextRenderer textRenderer = null;
	private TextRenderer waterMarkTextRenderer = null;
	
	private int startCaptureScreen = 0;
	private long startCaptureScreenTime = 0;
	
	private ByteBuffer snapShotBuffer = null;
	private BufferedImage screenshot = null;
	private Graphics graphics = null;
	
	private DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
	
	public App(int i_width, int i_height, boolean useCamera) {
		super(i_width, i_height);
		this.useCamera = useCamera;

		try {
			if (this.useCamera) {
				initCameraDimension();
				
			} else {

				img = ImageIO.read(getClass().getResourceAsStream(tempTestImg));
				testImg = new NyARBufferedImageRaster(img);
				
				cameraDimension.setSize(testImg.getWidth(), testImg.getHeight());
				
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
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
			
		System.err.println("------------------");
		System.err.println("START APP");
		System.err.println("------------------");
		
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
	
	private void initCameraDimension() {
		try {
			
			System.err.println("------------------");
			System.err.println("Use camera");
			System.err.println("------------------");
			
			for (Webcam webCam : Webcam.getWebcams()) {
				System.err.println("--> webCam: " + webCam);
			}

			camera = Webcam.getDefault();
			cameraDimension = camera.getViewSize();

			for (Dimension dim : camera.getViewSizes()) {
				System.err.println("--> cameraDimension: " + dim);
				if (cameraDimension.height < dim.height && cameraDimension.width < dim.width) {
					cameraDimension = dim;
				}
			}
			
			System.err.println("------------------");
			camera.setViewSize(cameraDimension);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public void setup(GL gl) throws Exception {

		NyARMarkerSystemConfig config = new NyARMarkerSystemConfig(cameraDimension.width, cameraDimension.height);
		size(cameraDimension.width, cameraDimension.height);
			
		nyar 	= new NyARGlMarkerSystem(config);
		render 	= new NyARGlRender(nyar);
		sensor 	= new NyARSensor(config.getScreenSize());
		
		id 			= nyar.addARMarker(getClass().getResourceAsStream(ARCODE_FILE), 16, 25, 80);
		id_samsung 	= nyar.addARMarker(getClass().getResourceAsStream(patt_samsung), 16, 25, 80);
		id_digiOH 	= nyar.addARMarker(getClass().getResourceAsStream(patt_digiOH), 16, 25, 80);
		id_cloud 	= nyar.addARMarker(getClass().getResourceAsStream(patt_cloud), 16, 25, 80);
		id_insta 	= nyar.addARMarker(getClass().getResourceAsStream(patt_insta), 16, 25, 80);
		id_twitter 	= nyar.addARMarker(getClass().getResourceAsStream(patt_twitter), 16, 25, 80);		
		
		textRenderer = new TextRenderer(new Font("SansSerif", Font.BOLD, cameraDimension.height));
		waterMarkTextRenderer = new TextRenderer(new Font("SansSerif", Font.BOLD, 12));
		
		gl.glEnable(GL.GL_DEPTH_TEST);
		initModel();

		snapShotBuffer = GLBuffers.newDirectByteBuffer(cameraDimension.width * cameraDimension.height * 4);
		screenshot = new BufferedImage(cameraDimension.width, cameraDimension.height, BufferedImage.TYPE_INT_RGB);
		
		if (useCamera) {
			camera.open();
		}
		
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
			
			
			if ((startCaptureScreen >= 0)) {
				
				if (startCaptureScreen == 0) {
					System.err.println("--> captured...");
					startCaptureScreen = -1;

					//waterMarkTextRenderer.beginRendering(cameraDimension.width, cameraDimension.height);
					//waterMarkTextRenderer.draw("datetime: " + dateFormat.format(new Date()), 0, 0);
					//waterMarkTextRenderer.endRendering();
					
					saveScreenShot(gl);
					
				} else {
				
					textRenderer.beginRendering(cameraDimension.width, cameraDimension.height);
					//textRenderer.setColor(1.0f, 0.2f, 0.2f, 0.8f);
					textRenderer.draw("" + startCaptureScreen, (cameraDimension.width/10) * 3, (cameraDimension.height/7) * 1);
					textRenderer.endRendering();
					
				}
				
				if ((System.currentTimeMillis() - startCaptureScreenTime >= 1200)) {
					startCaptureScreen--;
					startCaptureScreenTime = System.currentTimeMillis();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}	
		//}
	}

	private void saveScreenShot(GL gl) {

		try {
			//BufferedImage screenshot = new BufferedImage(cameraDimension.width, cameraDimension.height, BufferedImage.TYPE_INT_RGB);
			//Graphics graphics = screenshot.getGraphics();

			graphics = screenshot.getGraphics();
			snapShotBuffer = GLBuffers.newDirectByteBuffer(cameraDimension.width * cameraDimension.height * 4);
			gl.getGL2().glReadBuffer(GL2.GL_BACK);
			gl.getGL2().glReadPixels(0, 0, cameraDimension.width, cameraDimension.height, GL2.GL_RGBA, GL2.GL_UNSIGNED_BYTE, snapShotBuffer);

			//snapShotBuffer = (ByteBuffer) snapShotBuffer.flip();
			
			for (int h = 0; h < cameraDimension.height; h++) {
				for (int w = 0; w < cameraDimension.width; w++) {
					graphics.setColor(new Color((snapShotBuffer.get() & 0xff), (snapShotBuffer.get() & 0xff), (snapShotBuffer.get() & 0xff)));
					snapShotBuffer.get(); // consume alpha

					//graphics.translate(w >> 1, h >> 1);
					graphics.drawRect(w, h, 1, 1);
					//graphics.drawRect(w, cameraDimension.height - h, 1, 1);
				}
			}
			// This is one util of mine, it make sure you clean the direct buffer
			//BufferUtils.destroyDirectBuffer(buffer);
			snapShotBuffer.clear();
						
			//ImageUtil.flipImageVertically(screenshot);
			File outputfile = new File("photos/DigiOH-AR_" + dateFormat.format(new Date()) + ".png");
			outputfile.mkdirs();
						
			ImageIO.write(screenshot, "png", outputfile);
			
		} catch (Exception ex) {
		}
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

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2) {
			System.err.println("--> double clicked");
			
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