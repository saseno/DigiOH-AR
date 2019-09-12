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
import dev.saseno.jakarta.digioh.wavefront.GLModel;
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
	private TextRenderer textInfo = null;
	
	private int startCaptureScreen = -1;
	private long startCaptureScreenTime = 0;

	private ByteBuffer snapShotBuffer = null;
	private BufferedImage screenshot = null;
	private Graphics graphics = null;

	private DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
	private CaptureDialog captureDialog;
	
	private boolean isFilterActive = false;
	
	public App(int i_width, int i_height, boolean useCamera) {
		super(i_width, i_height);
		this.useCamera = useCamera;

		try {
			if (this.useCamera) {
				initCameraDimension();

			} else {

				img = ImageIO.read(getClass().getResourceAsStream(tempTestImg));
				testImg = new NyARBufferedImageRaster(img);

				monitorDimension.setSize(testImg.getWidth(), testImg.getHeight());

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
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

			//Dimension cameraDimension = new Dimension(320, 240);
			
			System.err.println("------------------");
			System.err.println("Use camera");
			System.err.println("------------------");

			for (Webcam webCam : Webcam.getWebcams()) {
				System.err.println("------------------");
				System.err.println("--> webCam: " + webCam);
				System.err.println("------------------");

				for (Dimension dim : webCam.getViewSizes()) {
					System.err.println("--> cameraDimension: " + dim);
					if (monitorDimension.height < dim.height && monitorDimension.width < dim.width) {
						monitorDimension = dim;
					}
				}
			}

			camera = Webcam.getDefault();
			monitorDimension = camera.getViewSize();

			for (Dimension dim : camera.getViewSizes()) {
				//System.err.println("--> cameraDimension: " + dim);
				if (monitorDimension.height < dim.height && monitorDimension.width < dim.width) {
					monitorDimension = dim;
				}
			}

			System.err.println("------------------");
			System.err.println("Camera Dimension: " + monitorDimension);
			System.err.println("------------------");
			
			camera.setViewSize(monitorDimension);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void setup(GL gl) throws Exception {
		
		NyARMarkerSystemConfig config = new NyARMarkerSystemConfig(monitorDimension.width, monitorDimension.height);
		
		nyar 	= new NyARGlMarkerSystem(config);
		render 	= new NyARGlRender(nyar);
		sensor 	= new NyARSensor(config.getScreenSize());

		id = nyar.addARMarker(getClass().getResourceAsStream(ARCODE_FILE), 16, 25, 80);
		id_samsung 	= nyar.addARMarker(getClass().getResourceAsStream(patt_samsung), 16, 25, 80);
		id_digiOH 	= nyar.addARMarker(getClass().getResourceAsStream(patt_digiOH), 16, 25, 80);
		id_cloud 	= nyar.addARMarker(getClass().getResourceAsStream(patt_cloud), 16, 25, 80);
		id_insta 	= nyar.addARMarker(getClass().getResourceAsStream(patt_insta), 16, 25, 80);
		id_twitter 	= nyar.addARMarker(getClass().getResourceAsStream(patt_twitter), 16, 25, 80);

		textRenderer = new TextRenderer(new Font("SansSerif", Font.BOLD, monitorDimension.height));
		textInfo = new TextRenderer(new Font("SansSerif", Font.BOLD, 22));
		
		gl.glEnable(GL.GL_DEPTH_TEST);
		initModel();

		snapShotBuffer = GLBuffers.newDirectByteBuffer(monitorDimension.width * monitorDimension.height * 4);
		screenshot = new BufferedImage(monitorDimension.width, monitorDimension.height, BufferedImage.TYPE_INT_RGB);

		if (useCamera) {
			//System.err.println("--> camera open");
			camera.open();
		}
		
		captureDialog = new CaptureDialog(frame);
		captureDialog.addWindowFocusListener(new WindowFocusListener() {
			
			@Override
			public void windowLostFocus(WindowEvent e) {
				try {
					isFilterActive = false;
					captureDialog.setVisible(false);
				} catch (Exception ex) {
				}
			}
			
			@Override
			public void windowGainedFocus(WindowEvent e) {
				isFilterActive = true;
			}
		});
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

			render.drawBackground(gl, sensor.getSourceImage(), isMirrored(), monitorDimension.getWidth(), monitorDimension.getHeight());			
			
			if (isFilterActive) {

				gl.glEnable(GL2.GL_BLEND);
				gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
				
		        gl.glEnable(GL2.GL_TEXTURE_2D);
		        gl.glEnable(GL2.GL_LIGHT0);
		        gl.glDisable(GL2.GL_LIGHTING); 
				
				gl.getGL2().glMatrixMode(GL2.GL_PROJECTION);
				gl.getGL2().glPushMatrix();
				gl.getGL2().glLoadIdentity();
				
				gl.getGL2().glOrtho(-100.0, monitorDimension.getWidth(), 0.0, monitorDimension.getHeight(), 0, 1);
				
				gl.getGL2().glMatrixMode(GL2.GL_MODELVIEW);
				gl.getGL2().glPushMatrix();
				gl.getGL2().glLoadIdentity();
				
				gl.getGL2().glBegin(GL2.GL_POLYGON);

				gl.getGL2().glColor4f(0.3f, 0.3f, 0.3f, 0.4f);
				gl.getGL2().glVertex3d(-100, -100, 0);
				gl.getGL2().glVertex3d(monitorDimension.getWidth(), -100, 0);
				gl.getGL2().glVertex3d(monitorDimension.getWidth(), monitorDimension.getHeight(), 0);
				gl.getGL2().glVertex3d(-100, monitorDimension.getHeight(), 0);				
				
				gl.getGL2().glEnd();
								 
			} else {
				
				render.loadARProjectionMatrix(gl, isMirrored());

				try {
					nyar.update(sensor);
				} catch (Exception e) {
					e.printStackTrace();
				}

				renderModelIfExist(gl, id, modelClient, 60, 0, 0);
				renderModelIfExist(gl, id_cloud, modelEarth, 0, 0, 0);
				renderModelIfExist(gl, id_digiOH, modelLove, 60, 0, 0);
				renderModelIfExist(gl, id_insta, modelMario, 0, 0, 0);
				renderModelIfExist(gl, id_twitter, modelPatung, 0, -230, 0);
				renderModelIfExist(gl, id_samsung, modelClient, 0, 0, 0);

				if ((startCaptureScreen >= 0)) {

					if (startCaptureScreen == 0) {
						// System.err.println("--> captured...");
						startCaptureScreen = -1;
						saveScreenShot(gl);

					} else if (startCaptureScreen > 0) {

						gl.getGL2().glDisable(GL2.GL_TEXTURE_2D);
						textRenderer.beginRendering(monitorDimension.width, monitorDimension.height);
						textRenderer.setColor(1.0f, 0.2f, 0.2f, 0.8f);
						textRenderer.draw("" + startCaptureScreen, (monitorDimension.width / 10) * 3,
								(monitorDimension.height / 7) * 1);
						textRenderer.endRendering();

					}

					if ((System.currentTimeMillis() - startCaptureScreenTime >= 1200)) {
						startCaptureScreen--;
						startCaptureScreenTime = System.currentTimeMillis();
					}
				}

				// render.drawStringInfo(gl, textInfo, "DigiOH - 2019",
				// cameraDimension.getWidth(), cameraDimension.getHeight());
			}
			
			Thread.sleep(1);

		} catch (Exception e) {
			e.printStackTrace();
		}
		}
	}
	
	private void renderModelIfExist(GL gl, int markerId, GLModel model, double i_x, double i_y, double i_z) {

		if (nyar.isExist(markerId)) {
			nyar.loadTransformMatrix(gl, markerId);
			render.renderModel(gl, i_x, i_y, i_z, rquad, model);
		}
	}
	
	private void saveScreenShot(GL gl) {

		try {		
			
			graphics = screenshot.getGraphics();			
			snapShotBuffer = GLBuffers.newDirectByteBuffer(monitorDimension.width * monitorDimension.height * 4);
			
			gl.getGL2().glReadBuffer(GL2.GL_FRONT_AND_BACK);
			gl.getGL2().glReadPixels(0, 0, monitorDimension.width, monitorDimension.height, GL2.GL_RGBA,
					GL2.GL_UNSIGNED_BYTE, snapShotBuffer);

			for (int h = 0; h < monitorDimension.height; h++) {
				for (int w = 0; w < monitorDimension.width; w++) {

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
				
				//AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
				AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
				
				screenshot = op.filter(screenshot, null);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			ImageIO.write(screenshot, "png", outputfile);
			uploadPhoto(fileLocation);			

		} catch (Exception ex) {
		}
	}

	private void uploadPhoto(String fileLocation) {
		try {
			//captureDialog = new CaptureDialog(frame);
			if (captureDialog != null) {
				captureDialog.sendEmail(monitorDimension, fileLocation);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {		
		if (e.getClickCount() >= 2 && startCaptureScreen < 0) {
			startCaptureScreen = 5;
			startCaptureScreenTime = System.currentTimeMillis();
		}
	}

	@Override
	public void keyPressed(java.awt.event.KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			System.exit(0);
		}
	}

	@Override
	public void keyTyped(java.awt.event.KeyEvent e) {
	}

	@Override
	public void keyReleased(java.awt.event.KeyEvent e) {
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