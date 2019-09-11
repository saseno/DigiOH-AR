package dev.saseno.jakarta.digioh;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;

import com.github.sarxos.webcam.Webcam;
import com.jogamp.newt.event.KeyEvent;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.GLBuffers;
import com.jogamp.opengl.util.awt.TextRenderer;

import dev.saseno.jakarta.digioh.email.CaptureDialog;
import dev.saseno.jakarta.digioh.io.utils.NyARGlMarkerSystem;
import dev.saseno.jakarta.digioh.io.utils.NyARGlRender;
import dev.saseno.jakarta.digioh.jogl2.GlSketch;
import dev.saseno.jakarta.digioh.obj.Client;
import dev.saseno.jakarta.digioh.obj.Earth;
import dev.saseno.jakarta.digioh.obj.Love;
import dev.saseno.jakarta.digioh.obj.Mario;
import dev.saseno.jakarta.digioh.obj.Mars;
import dev.saseno.jakarta.digioh.obj.Moon;
import dev.saseno.jakarta.digioh.obj.Patung;
import dev.saseno.jakarta.digioh.obj.Plane;
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

	private final String ARCODE_FILE = "/patt.hiro";
	private final String patt_samsung = "/patt.samsung";
	private final String patt_digiOH = "/patt.digiOH";
	private final String patt_cloud = "/patt.cloud";
	private final String patt_insta = "/patt.insta";
	private final String patt_twitter = "/patt.twitter";

	private NyARBufferedImageRaster imgRaster = null;

	private Earth modelEarth 	= null;
	private Moon modelMoon 		= null;
	private Mars modelMars 		= null;
	private Love modelLove 		= null;
	private Patung modelPatung 	= null;
	private Client modelClient 	= null;
	private Plane modelPlane	= null;
	private Mario modelMario	= null;

	private NyARBufferedImageRaster testImg = null;
	private BufferedImage img = null;
	private double rquad = 0.0;

	private String tempTestImg = "/320x240ABGR.png";

	private TextRenderer textRenderer = null;
	private int startCaptureScreen = -1;
	private long startCaptureScreenTime = 0;

	private ByteBuffer snapShotBuffer = null;
	private BufferedImage screenshot = null;
	private Graphics graphics = null;

	private DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
	private CaptureDialog captureDialog;
	
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

		initInstagram();
	}

	private void initModel() {

		modelEarth 	= new Earth();
		modelMoon 	= new Moon();
		modelMars 	= new Mars();
		modelLove 	= new Love();
		modelPatung = new Patung();
		modelClient = new Client();
		modelPlane 	= new Plane();
		modelMario 	= new Mario();
	}

	private void initCameraDimension() {
		try {

			System.err.println("------------------");
			System.err.println("Use camera");
			System.err.println("------------------");

			for (Webcam webCam : Webcam.getWebcams()) {
				System.err.println("------------------");
				System.err.println("--> webCam: " + webCam);
				System.err.println("------------------");

				for (Dimension dim : webCam.getViewSizes()) {
					System.err.println("--> cameraDimension: " + dim);
					if (cameraDimension.height < dim.height && cameraDimension.width < dim.width) {
						cameraDimension = dim;
					}
				}
			}

			camera = Webcam.getDefault();
			cameraDimension = camera.getViewSize();

			for (Dimension dim : camera.getViewSizes()) {
				//System.err.println("--> cameraDimension: " + dim);
				if (cameraDimension.height < dim.height && cameraDimension.width < dim.width) {
					cameraDimension = dim;
				}
			}

			System.err.println("------------------");
			System.err.println("Use Dimension: " + cameraDimension);
			System.err.println("------------------");
			camera.setViewSize(cameraDimension);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void setup(GL gl) throws Exception {

		NyARMarkerSystemConfig config = new NyARMarkerSystemConfig(cameraDimension.width, cameraDimension.height);
		size(cameraDimension.width, cameraDimension.height);

		nyar = new NyARGlMarkerSystem(config);
		render = new NyARGlRender(nyar);
		sensor = new NyARSensor(config.getScreenSize());

		id = nyar.addARMarker(getClass().getResourceAsStream(ARCODE_FILE), 16, 25, 80);
		id_samsung 	= nyar.addARMarker(getClass().getResourceAsStream(patt_samsung), 16, 25, 80);
		id_digiOH 	= nyar.addARMarker(getClass().getResourceAsStream(patt_digiOH), 16, 25, 80);
		id_cloud 	= nyar.addARMarker(getClass().getResourceAsStream(patt_cloud), 16, 25, 80);
		id_insta 	= nyar.addARMarker(getClass().getResourceAsStream(patt_insta), 16, 25, 80);
		id_twitter 	= nyar.addARMarker(getClass().getResourceAsStream(patt_twitter), 16, 25, 80);

		textRenderer = new TextRenderer(new Font("SansSerif", Font.BOLD, cameraDimension.height));

		gl.glEnable(GL.GL_DEPTH_TEST);
		initModel();

		snapShotBuffer = GLBuffers.newDirectByteBuffer(cameraDimension.width * cameraDimension.height * 4);
		screenshot = new BufferedImage(cameraDimension.width, cameraDimension.height, BufferedImage.TYPE_INT_RGB);

		if (useCamera) {
			camera.open();
		}
		
//		captureDialog = new CaptureDialog(frame);
//		captureDialog.addWindowFocusListener(new WindowFocusListener() {
//			
//			@Override
//			public void windowLostFocus(WindowEvent e) {
//				//frame.setVisible(true);
//				//frame.toFront();
//			}
//			
//			@Override
//			public void windowGainedFocus(WindowEvent e) {
//				//frame.setVisible(true);
//				//frame.toFront();		
//			}
//		});
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
	
	private void updateImageRaster() {
		if (useCamera) {
			imgRaster = new NyARBufferedImageRaster(camera.getImage());
		} else {
			imgRaster = testImg;
		}
	}

	public void draw(GL gl) throws Exception {
		synchronized (camera) {
		try {

			updateRotation();
			updateImageRaster();
			sensor.update(imgRaster);

			if (sensor.getSourceImage() == null) {
				System.err.println("--> source null...");
				return;
			}

			render.drawBackground(gl, sensor.getSourceImage(), isMirrored(), cameraDimension.getWidth(), cameraDimension.getHeight());			
			render.loadARProjectionMatrix(gl, isMirrored());
			
			nyar.update(sensor);

			if (nyar.isExist(id)) {
				nyar.loadTransformMatrix(gl, id);
				//render.colorCube(gl, 50, 0, 0, 20, rquad);
				//render.renderModel(gl, 60, 0, 0, rquad, modelLove);
				render.renderModel(gl, 60, 0, 0, rquad, modelClient);

				//render.drawText(gl, 0, 0, 20, rquad, "test 123");
				//render.renderModel(gl, 0, 0, 0, rquad, modelPatung);
			}

			if (nyar.isExist(id_cloud)) {
				nyar.loadTransformMatrix(gl, id_cloud);
				render.renderModel(gl, 0, 0, 0, rquad, modelEarth);
			}

			if (nyar.isExist(id_digiOH)) {
				nyar.loadTransformMatrix(gl, id_digiOH);
				render.renderModel(gl, 60, 0, 0, rquad, modelLove);
			}

			if (nyar.isExist(id_insta)) {
				nyar.loadTransformMatrix(gl, id_insta);
				//render.renderModel(gl, 0, 0, 0, rquad, modelMars);
				//render.renderModel(gl, 0, 0, 0, rquad, modelPlane);
				render.renderModel(gl, 0, 0, 0, rquad, modelMario);
			}

			if (nyar.isExist(id_twitter)) {
				nyar.loadTransformMatrix(gl, id_twitter);
				render.renderModel(gl, 0, -230, 0, rquad, modelPatung);
			}

			if (nyar.isExist(id_samsung)) {
				nyar.loadTransformMatrix(gl, id_samsung);
		        render.renderModel(gl, 0, 0, 0, rquad, modelClient);
			}
						
			if ((startCaptureScreen >= 0)) {

				if (startCaptureScreen == 0) {
					//System.err.println("--> captured...");
					startCaptureScreen = -1;
					saveScreenShot(gl);

				} else if (startCaptureScreen > 0) {

			        gl.getGL2().glDisable(GL2.GL_TEXTURE_2D);
					textRenderer.beginRendering(cameraDimension.width, cameraDimension.height);
					textRenderer.setColor(1.0f, 0.2f, 0.2f, 0.8f);					
					textRenderer.draw("" + startCaptureScreen, (cameraDimension.width / 10) * 3,
							(cameraDimension.height / 7) * 1);
					textRenderer.endRendering();					
					
				}

				if ((System.currentTimeMillis() - startCaptureScreenTime >= 1200)) {
					startCaptureScreen--;
					startCaptureScreenTime = System.currentTimeMillis();
				}
			}

			Thread.sleep(1);

		} catch (Exception e) {
			e.printStackTrace();
		}
		}
	}
	
	private void saveScreenShot(GL gl) {

		try {		
			
			graphics = screenshot.getGraphics();			
			snapShotBuffer = GLBuffers.newDirectByteBuffer(cameraDimension.width * cameraDimension.height * 4);
			
			gl.getGL2().glReadBuffer(GL2.GL_FRONT_AND_BACK);
			gl.getGL2().glReadPixels(0, 0, cameraDimension.width, cameraDimension.height, GL2.GL_RGBA,
					GL2.GL_UNSIGNED_BYTE, snapShotBuffer);

			for (int h = 0; h < cameraDimension.height; h++) {
				for (int w = 0; w < cameraDimension.width; w++) {

					graphics.setColor(new Color(
							(snapShotBuffer.get() & 0xff), 
							(snapShotBuffer.get() & 0xff),
							(snapShotBuffer.get() & 0xff)));
					
					snapShotBuffer.get(); // consume alpha					
					graphics.drawRect(w, h, 1, 1);
				}
			}

			//snapShotBuffer.clear();

			String fileLocation = "photos/DigiOH-AR_" + dateFormat.format(new Date()) + ".png";
			File outputfile = new File(fileLocation);
			outputfile.mkdirs();
			
			try {
				AffineTransform tx = AffineTransform.getScaleInstance(-1, -1);
				tx.translate(-screenshot.getWidth(null), -screenshot.getHeight(null));
				AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
				screenshot = op.filter(screenshot, null);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			ImageIO.write(screenshot, "png", outputfile);
			uploadPhoto(fileLocation);			

		} catch (Exception ex) {
		}
	}

	private void initInstagram() {
		try {

//			String fileName = null;
//			byte[] data = null;
//			String accessToken = "";
//						
//			FacebookClient facebookClient = new DefaultFacebookClient(accessToken, Version.LATEST);
//			User user = facebookClient.fetchObject("me", User.class);
//			System.out.println("User name: " + user.getName());
//
//			URL url = new URL("img_url");
//			InputStream stream = new BufferedInputStream(url.openConnection().getInputStream());
//
//			FacebookType photo = facebookClient.publish("facebook_page_id/photos", FacebookType.class,
//					BinaryAttachment.with(fileName, data));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void uploadPhoto(String fileLocation) {
		try {
			captureDialog = new CaptureDialog(frame);
			//if (captureDialog != null) {
			captureDialog.sendEmail(cameraDimension, fileLocation);
			//}
		} catch (Exception e) {
			e.printStackTrace();
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
		
		if (e.getClickCount() >= 2 && startCaptureScreen < 0) {
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