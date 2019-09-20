package demos.es1;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.jogamp.nativewindow.NativeWindowFactory;
import com.jogamp.opengl.FPSCounter;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2ES1;
import com.jogamp.opengl.GL2ES2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLPipelineFactory;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.glu.GLU;

import com.jogamp.common.nio.Buffers;
import com.jogamp.newt.Display;
import com.jogamp.newt.NewtFactory;
import com.jogamp.newt.Screen;
import com.jogamp.newt.Window;
import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.newt.event.MouseEvent;
import com.jogamp.newt.event.MouseListener;
import com.jogamp.newt.event.WindowEvent;
import com.jogamp.newt.event.WindowListener;
import com.jogamp.newt.event.WindowUpdateEvent;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.util.Animator;
import com.jogamp.opengl.util.glsl.fixedfunc.FixedFuncUtil;
import com.jogamp.opengl.util.glsl.fixedfunc.ShaderSelectionMode;

public class RedSquare extends Thread implements WindowListener, KeyListener, MouseListener, GLEventListener {

    public static boolean glDebugEmu = false;
    public static boolean glDebug = false ;
    public static boolean glTrace = false ;
    public Window nWindow = null;
    public GLWindow window;
    private GLProfile glp;
    private GLU glu;
    private boolean quit = false;
    private final String glprofile;
    private final int type;
    Animator glAnimator=null;

    public RedSquare() {
        this(null, USE_NEWT);
    }

    public RedSquare(String glprofile, int type) {
        super();
        this.glprofile=glprofile;
        this.type=type;
    }

    @Override
	public void windowRepaint(WindowUpdateEvent e) {
    }
    @Override
	public void windowResized(WindowEvent e) {
    }
    @Override
	public void windowMoved(WindowEvent e) {
    }
    @Override
	public void windowDestroyNotify(WindowEvent e) {
        System.out.println("WINDOW-DESTROY NOTIFY "+Thread.currentThread()+" QUIT "+e);
        quit=true;
        if(null!=glAnimator) {
            glAnimator.stop();
        }
    }
    @Override
	public void windowDestroyed(WindowEvent e) {
        System.out.println("WINDOW-DESTROYED "+Thread.currentThread());
    }
    @Override
	public void windowGainedFocus(WindowEvent e) { }
    @Override
	public void windowLostFocus(WindowEvent e) { }

    @Override
	public void keyPressed(KeyEvent e) {
        System.out.println("KEY-PRESSED "+Thread.currentThread()+" UNHANDLED "+e);
    }
    @Override
	public void keyReleased(KeyEvent e) {
        System.out.println("KEY-RELEASED "+Thread.currentThread()+" UNHANDLED "+e);
        if( !e.isPrintableKey() || e.isAutoRepeat() ) {
            return;
        }
        if(e.getKeyChar()=='f') {
            System.out.println("KEY-TYPED "+Thread.currentThread()+" FULLSCREEN "+e);
            window.setFullscreen(!window.isFullscreen());
        } else if(e.getKeyChar()=='q') {
            System.out.println("KEY-TYPED "+Thread.currentThread()+" QUIT "+e);
            quit = true;
            if(null!=glAnimator) {
                glAnimator.stop();
            }
        } else {
            System.out.println("KEY-TYPED "+Thread.currentThread()+" UNHANDLED "+e);
        }
    }

    @Override
	public void mouseClicked(MouseEvent e) {
        System.out.println("MOUSE-CLICKED "+Thread.currentThread()+" UNHANDLED "+e);
        switch(e.getClickCount()) {
            case 1:
                if(e.getButton()>MouseEvent.BUTTON1) {
                    window.setFullscreen(!window.isFullscreen());
                }
                break;
            default:
                quit=true;
                break;
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
    @Override
	public void mouseMoved(MouseEvent e) {
    }
    @Override
	public void mouseDragged(MouseEvent e) {
    }
    @Override
	public void mouseWheelMoved(MouseEvent e) {
    }

    public boolean shouldQuit() { return quit; }

    @Override
	public void run() {
        int width = 800;
        int height = 480;
        glp = GLProfile.get(glprofile);
        System.out.println("RUN "+Thread.currentThread()+" "+glp);
        try {
            GLCapabilities caps = new GLCapabilities(glp);

            if(0!=(type&USE_AWT)) {
                Display nDisplay = NewtFactory.createDisplay(NativeWindowFactory.TYPE_AWT, null); // local display
                Screen nScreen  = NewtFactory.createScreen(nDisplay, 0); // screen 0
                nWindow = NewtFactory.createWindow(nScreen, caps);
                nWindow.setUndecorated(false);
                window = GLWindow.create(nWindow);
            } else {
                window = GLWindow.create(caps);
            }

            window.addWindowListener(this);
            window.addMouseListener(this);
            window.addKeyListener(this);
            window.addGLEventListener(this);

            window.setUpdateFPSFrames(FPSCounter.DEFAULT_FRAMES_PER_INTERVAL, System.err);
            // Size OpenGL to Video Surface
            window.setSize(width, height);
            // window.setFullscreen(true);
            window.setVisible(true);
            window.setUpdateFPSFrames(FPSCounter.DEFAULT_FRAMES_PER_INTERVAL, System.err);

            if(!oneThread) {
                if(useAnimator) {
                    System.out.println("Using Animator .. "+Thread.currentThread());
                    glAnimator = new Animator(Thread.currentThread().getThreadGroup(), window);
                    glAnimator.start();
                    while (glAnimator.isAnimating()) {
                        try {
                            Thread.sleep(100);
                        } catch (Exception e) {}
                    }
                    shutdown();
                } else {
                    do {
                        display();
                    } while (!quit && window.getTotalFPSDuration() < 11000) ;
                    shutdown();
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public void display() {
        try {
            window.display();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public void shutdown() {
        try {
            System.out.println("SHUTDOWN "+Thread.currentThread()+" START");
            // Shut things down cooperatively
            window.destroy();
            window = null;
            if(null!=nWindow) {
                nWindow.destroy();
                nWindow=null;
            }
            System.out.println("SHUTDOWN "+Thread.currentThread()+" FIN");
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }


    // FIXME: we must add storage of the pointers in the GL state to
    // the GLImpl classes. The need for this can be seen by making
    // these variables method local instead of instance members. The
    // square will disappear after a second or so due to garbage
    // collection. On desktop OpenGL this implies a stack of
    // references due to the existence of glPush/PopClientAttrib. On
    // OpenGL ES 1/2 it can simply be one set of references.
    private FloatBuffer colors;
    private FloatBuffer vertices;

    @Override
	public void init(GLAutoDrawable drawable) {
        GL _gl = drawable.getGL();

        if(glDebugEmu) {
            try {
                // Debug ..
                _gl = _gl.getContext().setGL( GLPipelineFactory.create("com.jogamp.opengl.Debug", GL2ES2.class, _gl, null) );

                if(glTrace) {
                    // Trace ..
                    _gl = _gl.getContext().setGL( GLPipelineFactory.create("com.jogamp.opengl.Trace", GL2ES2.class, _gl, new Object[] { System.err } ) );
                }
            } catch (Exception e) {e.printStackTrace();}
            glDebug = false;
            glTrace = false;
        }

        GL2ES1 gl = FixedFuncUtil.wrapFixedFuncEmul(_gl, ShaderSelectionMode.AUTO, null);
        if(swapInterval>=0) {
            gl.setSwapInterval(swapInterval);
        }

        if(glDebug) {
            try {
                // Debug ..
                gl = (GL2ES1) gl.getContext().setGL( GLPipelineFactory.create("com.jogamp.opengl.Debug", GL2ES1.class, gl, null) );
            } catch (Exception e) {e.printStackTrace();}
        }

        if(glTrace) {
            try {
                // Trace ..
                gl = (GL2ES1) gl.getContext().setGL( GLPipelineFactory.create("com.jogamp.opengl.Trace", GL2ES1.class, gl, new Object[] { System.err } ) );
            } catch (Exception e) {e.printStackTrace();}
        }

        glu = GLU.createGLU(gl);

        System.err.println(Thread.currentThread()+" Entering initialization");
        System.err.println(Thread.currentThread()+" GL Profile: "+gl.getGLProfile());
        System.err.println(Thread.currentThread()+" GL:" + gl);
        System.err.println(Thread.currentThread()+" GL_VERSION=" + gl.glGetString(gl.GL_VERSION));
        System.err.println(Thread.currentThread()+" GL_EXTENSIONS:");
        System.err.println(Thread.currentThread()+"   " + gl.glGetString(gl.GL_EXTENSIONS));
        System.err.println(Thread.currentThread()+" swapInterval: " + swapInterval + " (GL: "+gl.getSwapInterval()+")");
        System.err.println(Thread.currentThread()+" GLU: " + glu);

        // Allocate vertex arrays
        colors   = Buffers.newDirectFloatBuffer(16);
        vertices = Buffers.newDirectFloatBuffer(12);
        // Fill them up
        colors.put( 0, 1);    colors.put( 1, 0);     colors.put( 2, 0);    colors.put( 3, 1);
        colors.put( 4, 0);    colors.put( 5, 0);     colors.put( 6, 1);    colors.put( 7, 1);
        colors.put( 8, 1);    colors.put( 9, 0);     colors.put(10, 0);    colors.put(11, 1);
        colors.put(12, 1);    colors.put(13, 0);     colors.put(14, 0);    colors.put(15, 1);
        vertices.put(0, -2);  vertices.put( 1,  2);  vertices.put( 2,  0);
        vertices.put(3,  2);  vertices.put( 4,  2);  vertices.put( 5,  0);
        vertices.put(6, -2);  vertices.put( 7, -2);  vertices.put( 8,  0);
        vertices.put(9,  2);  vertices.put(10, -2);  vertices.put(11,  0);

        gl.glEnableClientState(gl.GL_VERTEX_ARRAY);
        gl.glEnableClientState(gl.GL_COLOR_ARRAY);
        gl.glVertexPointer(3, GL.GL_FLOAT, 0, vertices);
        gl.glColorPointer(4, GL.GL_FLOAT, 0, colors);

        // OpenGL Render Settings
        gl.glClearColor(0, 0, 0, 1);
        gl.glEnable(GL.GL_DEPTH_TEST);
    }

    @Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL2ES1 gl = drawable.getGL().getGL2ES1();
        // Set location in front of camera
        gl.glMatrixMode(gl.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(45.0f, (float)width / (float)height, 1.0f, 100.0f);
        //gl.glOrthof(-4.0f, 4.0f, -4.0f, 4.0f, 1.0f, 100.0f);
        //glu.gluLookAt(0, 0, -20, 0, 0, 0, 0, 1, 0);
    }

    @Override
	public void display(GLAutoDrawable drawable) {
        GL2ES1 gl = drawable.getGL().getGL2ES1();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

        // One rotation every four seconds
        gl.glMatrixMode(gl.GL_MODELVIEW);
        gl.glLoadIdentity();
        gl.glTranslatef(0, 0, -10);
        float ang = (window.getTotalFPSDuration() * 360.0f) / 4000.0f;
        gl.glRotatef(ang, 0, 0, 1);
        gl.glRotatef(ang, 0, 1, 0);


        // Draw a square
        gl.glDrawArrays(GL.GL_TRIANGLE_STRIP, 0, 4);
    }

    @Override
	public void dispose(GLAutoDrawable drawable) {
        GL2ES1 gl = drawable.getGL().getGL2ES1();
        System.out.println(Thread.currentThread()+" RedSquare.dispose: "+gl.getContext());
        gl.glDisableClientState(gl.GL_VERTEX_ARRAY);
        gl.glDisableClientState(gl.GL_COLOR_ARRAY);
        glu = null;
        colors.clear();
        colors   = null;
        vertices.clear();
        vertices = null;
        System.out.println(Thread.currentThread()+" RedSquare.dispose: FIN");
    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
    }

    public static int USE_NEWT      = 0;
    public static int USE_AWT       = 1 << 0;

    public static boolean oneThread = false;
    public static boolean useAnimator = false;
    public static int swapInterval = -1;

    public static void main(String[] args) {
        int type = USE_NEWT ;
        boolean useEDT = true;
        List threads = new ArrayList();
        for(int i=0; i<args.length; i++) {
            if(args[i].equals("-swapi")) {
                i++;
                try {
                    swapInterval = Integer.parseInt(args[i]);
                } catch (Exception ex) { ex.printStackTrace(); }
            } else if(args[i].equals("-trace")) {
                glTrace=true;
            } else if(args[i].equals("-debug")) {
                glDebug=true;
            } else if(args[i].equals("-debugff")) {
                glDebugEmu=true;
            } else if(args[i].equals("-1thread")) {
                oneThread=true;
            } else if(args[i].equals("-awt")) {
                type |= USE_AWT;
            } else if(args[i].equals("-noedt")) {
                useEDT = false;
            } else if(args[i].equals("-animator")) {
                useAnimator = true;
            } else if(args[i].startsWith("-GL")) {
                threads.add(new RedSquare(args[i].substring(1), type));
            }
        }
        if(threads.size()==0) {
            threads.add(new RedSquare(GLProfile.GL2ES1, type));
        }

        NewtFactory.setUseEDT(useEDT); // true is the default

        System.out.println(Thread.currentThread()+" RedSquare.main: Start - oneThread: "+oneThread);
        if(!oneThread) {
            for(Iterator i = threads.iterator(); i.hasNext(); ) {
                ((Thread)i.next()).start();
            }

            boolean done = false;

            int lastAliveCount = 0;
            while(!done) {
                int aliveCount = 0;
                for(Iterator i = threads.iterator(); i.hasNext(); ) {
                    if ( ((Thread)i.next()).isAlive() ) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException ie) {}
                        aliveCount++;
                    }
                }
                if(lastAliveCount != aliveCount) {
                    System.out.println(Thread.currentThread()+" RedSquare.main: alive changed: "+lastAliveCount+" -> "+aliveCount);
                }
                lastAliveCount = aliveCount;
                done = 0==aliveCount ;
            }
        } else {
            // init all ..
            for(Iterator i = threads.iterator(); i.hasNext(); ) {
                ((Thread)i.next()).run();
            }
            while (threads.size()>0) {
                for(Iterator i = threads.iterator(); i.hasNext(); ) {
                    RedSquare app = (RedSquare) i.next();
                    if(app.shouldQuit()) {
                        app.shutdown();
                        i.remove();
                    } else {
                        app.display();
                    }
                }
            }
        }
        System.out.println(Thread.currentThread()+" RedSquare.main: FIN");
    }
}
