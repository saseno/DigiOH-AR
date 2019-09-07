package dev.saseno.jakarta.digioh.jogl2;

import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.Animator;

import dev.saseno.jakarta.digioh.io.utils.ScreenResSelector;

public abstract class GlSketch implements GLEventListener, KeyListener, MouseListener {

	private Frame frame;
	protected GLCanvas canvas;
	protected Dimension cameraDimension = new Dimension(320, 240);

	private boolean _is_setup_done = false;
	private GraphicsDevice dev;
	
	private static final String TITLE = "DigiOH - Augmented Reality";
	private WindowAdapter exitWindowAdapter = new WindowAdapter() {
		public void windowClosing(WindowEvent e) {
			System.exit(0);
		}
	};

	public GlSketch(int i_width, int i_height) {		
		cameraDimension.setSize(i_width, i_height);
	}

	public void run2() {
		dev = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		DisplayMode newMode = null;

		if (dev.isFullScreenSupported()) {
			try {
				java.util.List modes = ScreenResSelector.getAvailableDisplayModes();
				newMode = (DisplayMode) modes.get(modes.size() - 1);
				cameraDimension.setSize(newMode.getWidth(), newMode.getHeight());
				
			} catch (Exception e) {
				e.printStackTrace();

				newMode = ScreenResSelector.showSelectionDialog();
				if (newMode != null) {
										
					cameraDimension.setSize(newMode.getWidth(), newMode.getHeight());
				}
			}
		} else {
			System.err.println("NOTE: full-screen mode not supported; running in window instead");
		}

		frame = new Frame(TITLE);
		frame.setUndecorated(newMode != null);
		frame.addWindowListener(exitWindowAdapter);
		frame.setSize(cameraDimension.width, cameraDimension.height);
		
		canvas = new GLCanvas();
		canvas.addGLEventListener(this);
		canvas.addKeyListener(this);
		canvas.addMouseListener(this);		
		canvas.setBounds(0, 0, cameraDimension.width, cameraDimension.height);

		frame.add(canvas);
		
		frame.setVisible(true);		
		frame.setResizable(false);
		
		if (dev.isFullScreenSupported() && (newMode != null)) {
			dev.setFullScreenWindow(frame);
			if (dev.isDisplayChangeSupported()) {
				dev.setDisplayMode(newMode);
				
			} else {
				// Not much point in having a full-screen window in this case
				dev.setFullScreenWindow(null);
				final Frame f2 = frame;
				try {
					EventQueue.invokeAndWait(() -> {
						
						f2.setVisible(false);
						f2.setUndecorated(false);
						f2.setVisible(true);
						f2.setSize(cameraDimension.width, cameraDimension.height);
						f2.toFront();
						
					});
				} catch (Exception e) {
					e.printStackTrace();
				}
				System.err.println("NOTE: was not able to change display mode; full-screen disabled");
			}
		}
		
	}

	public void run() {

		frame = new Frame(TITLE);
		frame.addWindowListener(exitWindowAdapter);

		canvas = new GLCanvas();
		canvas.addGLEventListener(this);
		canvas.addKeyListener(this);
		canvas.addMouseListener(this);

		frame.setSize(cameraDimension.width, cameraDimension.height);
		canvas.setBounds(0, 0, cameraDimension.width, cameraDimension.height);

		frame.add(canvas);
		frame.setVisible(true);
	}

	public void size(int width, int height) {
		
		cameraDimension.setSize(width, height);
		
		if (frame.isUndecorated()) {

			frame.setSize(cameraDimension.width, cameraDimension.height);
			canvas.setBounds(0, 0, cameraDimension.width, cameraDimension.height);
			
		} else {

			 Insets ins = frame.getInsets();
			 frame.setSize(width + ins.left + ins.right, height + ins.top + ins.bottom);
			 canvas.setBounds(ins.left, ins.top, width, height);
		}
	}

	@Override
	public final void init(GLAutoDrawable drawable) {
		try {

			System.err.println("------------------");
			System.err.println("init...");
			System.err.println("------------------");

			GL gl = drawable.getGL();
			setup(gl);
			Animator animator = new Animator(drawable);
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
				
		cameraDimension.setSize(width, height);

		return;
	}

	@Override
	public final void display(GLAutoDrawable drawable) {
		try {
			if (_is_setup_done) {
				draw(drawable.getGL());
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