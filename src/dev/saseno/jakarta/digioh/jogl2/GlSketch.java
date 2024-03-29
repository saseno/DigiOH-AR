package dev.saseno.jakarta.digioh.jogl2;

import java.awt.Button;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.Animator;
import com.jogamp.opengl.util.gl2.GLUT;

import dev.saseno.jakarta.digioh.io.utils.ScreenResSelector;

public abstract class GlSketch implements GLEventListener, KeyListener, MouseListener {

	protected Frame frame;
	protected GLCanvas canvas;	
	protected Dimension monitorDimension = new Dimension(320, 240);

	protected GLUT glut;
	protected boolean showFps = false;
	private boolean _is_setup_done = false;
	private GraphicsDevice dev;
	
	private static final String TITLE = "DigiOH - Augmented Reality";
	private WindowAdapter exitWindowAdapter = new WindowAdapter() {
		public void windowClosing(WindowEvent e) {
			System.exit(0);
		}
	};

	public GlSketch(int i_width, int i_height) {		
		monitorDimension.setSize(i_width, i_height);
	}

	public void run2() {
		dev = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		DisplayMode newMode = null;

		if (dev.isFullScreenSupported()) {
			try {

				///////////////////////
				// automatic select
				///////////////////////
				
				int monitorWidth = 1920;
				int monitorHeght = 1080;
				int monitorRefreshRate = 29;
				
				java.util.List modes = ScreenResSelector.getAvailableDisplayModes();

				for (Object mode : modes) {
					if (mode != null && mode instanceof DisplayMode
							&& (((DisplayMode)mode).getWidth() == monitorWidth)
							&& (((DisplayMode)mode).getHeight() == monitorHeght)
							&& (((DisplayMode)mode).getRefreshRate() == monitorRefreshRate)) {
						
						newMode = (DisplayMode) mode;
						break;
					}
				}
				
				if (newMode == null) {
					newMode = ScreenResSelector.showSelectionDialog();
					//newMode = (DisplayMode) modes.get(modes.size() - 1);
				}
				
				//newMode = (DisplayMode) modes.get(modes.size() - 1);				
				monitorDimension.setSize(newMode.getWidth(), newMode.getHeight());
							
			} catch (Exception e) {
				e.printStackTrace();

				newMode = ScreenResSelector.showSelectionDialog();
				if (newMode != null) {										
					monitorDimension.setSize(newMode.getWidth(), newMode.getHeight());
				}
			}
		} else {
			System.err.println("NOTE: full-screen mode not supported; running in window instead");
		}
		
		monitorDimension = Toolkit.getDefaultToolkit().getScreenSize();
		
		frame = new Frame(TITLE);
		frame.setExtendedState(Frame.MAXIMIZED_BOTH); 
		frame.setUndecorated(true);
		frame.addWindowListener(exitWindowAdapter);
		frame.setSize(monitorDimension.width, monitorDimension.height);
		
		canvas = new GLCanvas();
		canvas.addGLEventListener(this);
		canvas.addKeyListener(this);
		canvas.addMouseListener(this);
		canvas.setBounds(0, 0, monitorDimension.width, monitorDimension.height);

		Button button = new Button("test button");
		button.setBounds(10, 10, 300, 40);
		
		frame.add(canvas);
		frame.add(button);
		
		frame.setResizable(false);
		
		if (dev.isFullScreenSupported() && (newMode != null)) {
			//dev.setFullScreenWindow(frame);
			
			if (dev.isDisplayChangeSupported()) {
				dev.setDisplayMode(newMode);
				
			} else {
				dev.setFullScreenWindow(null);
				System.err.println("---------------------------------------------------------------");
				System.err.println("NOTE: was not able to change display mode; full-screen disabled");
				System.err.println("---------------------------------------------------------------");
			}
		}
		
		frame.setVisible(true);		
		frame.toFront();
		
		System.err.println("-----------");
		System.err.println("INIT OK....");
		System.err.println("-----------");
	}

	public void run() {

		frame = new Frame(TITLE);
		frame.addWindowListener(exitWindowAdapter);

		canvas = new GLCanvas();
		canvas.addGLEventListener(this);
		canvas.addKeyListener(this);
		canvas.addMouseListener(this);

		frame.setSize(monitorDimension.width, monitorDimension.height);
		canvas.setBounds(0, 0, monitorDimension.width, monitorDimension.height);
		
		canvas.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
			}
			
			@Override
			public void mousePressed(MouseEvent e) {				
			}
			
			@Override
			public void mouseExited(MouseEvent e) {				
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {				
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				System.err.println("--> " + e.getXOnScreen() + ", " + e.getYOnScreen());
			}
		});
		
		frame.add(canvas);		
		frame.setVisible(true);
	}

//	public void size(int width, int height) {
//		
//		//
//		
//		if (frame.isUndecorated()) {			
//			System.err.println("--> change size...");			
////			frame.setSize(cameraDimension.width, cameraDimension.height);
////			canvas.setBounds(0, 0, cameraDimension.width, cameraDimension.height);
//			
//		} else {
//			
//			cameraDimension.setSize(width, height);
//			Insets ins = frame.getInsets();
//			frame.setSize(width + ins.left + ins.right, height + ins.top + ins.bottom);
//			canvas.setBounds(ins.left, ins.top, width, height);
//		}
//	}

	@Override
	public final void init(GLAutoDrawable drawable) {
		try {

			System.err.println("------------------");
			System.err.println("init...");
			System.err.println("------------------");

			glut = new GLUT();
			GL gl = drawable.getGL();
			setup(gl);
			Animator animator = new Animator(drawable);
			if (showFps) {
				animator.setUpdateFPSFrames(3, null);
			}
			animator.start();
			_is_setup_done = true;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return;
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {

		GL gl = drawable.getGL();
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		gl.glViewport(0, 0, width, height);
				
		monitorDimension.setSize(width, height);
		return;
	}

	@Override
	public final void display(GLAutoDrawable drawable) {
		try {
			if (_is_setup_done) {
				draw(drawable.getGL());
				if (showFps) {
					System.err.println("--> fps: " + drawable.getAnimator().getLastFPS());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void dispose(GLAutoDrawable arg0) {
	}

	public abstract void setup(GL i_gl) throws Exception;

	public abstract void draw(GL i_gl) throws Exception;
}