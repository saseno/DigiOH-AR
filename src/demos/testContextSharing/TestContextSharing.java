/*
 * Copyright (c) 2003 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * - Redistribution of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * - Redistribution in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * Neither the name of Sun Microsystems, Inc. or the names of
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES,
 * INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN
 * MICROSYSTEMS, INC. ("SUN") AND ITS LICENSORS SHALL NOT BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR
 * ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR
 * DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE
 * DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY,
 * ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF
 * SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 *
 * You acknowledge that this software is not designed or intended for use
 * in the design, construction, operation or maintenance of any nuclear
 * facility.
 *
 * Sun gratefully acknowledges that this software was originally authored
 * and developed by Kenneth Bradley Russell and Christopher John Kline.
 */

package demos.testContextSharing;

import java.awt.BorderLayout;
import java.awt.Frame;

import com.jogamp.opengl.DebugGL2;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLCanvas;


/** A simple demonstration of sharing of display lists between drawables. */

public class TestContextSharing {
  private int gearDisplayList;

  public static void main(String[] args) {
    new TestContextSharing().run(args);
  }

  public void run(String[] args) {
    GLCanvas canvas1 = new GLCanvas();
    canvas1.addGLEventListener(new Listener());
    canvas1.setSize(256, 256);
    Frame frame1 = new Frame("Canvas 1");
    frame1.setLayout(new BorderLayout());
    frame1.add(canvas1, BorderLayout.CENTER);
    System.err.println("Showing init frame1");
    frame1.setLocation(0, 0);
    frame1.pack();
    frame1.setVisible(true);

    GLCanvas canvas2 = new GLCanvas(null, null, null);
    canvas2.setSharedContext(canvas1.getContext());
    canvas2.addGLEventListener(new Listener());
    canvas2.setSize(256, 256);
    Frame frame2 = new Frame("Canvas 2");
    frame2.setLayout(new BorderLayout());
    frame2.add(canvas2, BorderLayout.CENTER);
    System.err.println("Showing init frame2");
    frame2.setLocation(frame1.getWidth(),0);
    frame2.pack();
    frame2.setVisible(true);

    try {
        Thread.sleep(10000);
    } catch (InterruptedException e) { }

  }

  class Listener implements GLEventListener {

    @Override
	public void init(GLAutoDrawable drawable) {

      GL2 gl = drawable.getGL().getGL2();

      drawable.setGL(new DebugGL2(gl));

      float pos[] = { 5.0f, 5.0f, 10.0f, 0.0f };
      gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, pos, 0);
      gl.glEnable(GL2.GL_CULL_FACE);
      gl.glEnable(GL2.GL_LIGHTING);
      gl.glEnable(GL2.GL_LIGHT0);
      gl.glEnable(GL2.GL_DEPTH_TEST);

      initializeDisplayList(gl);

      gl.glEnable(GL2.GL_NORMALIZE);
    }

    @Override
	public void dispose(GLAutoDrawable drawable) {
    }

    @Override
	public void display(GLAutoDrawable drawable) {
      GL2 gl = drawable.getGL().getGL2();

      gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

      System.err.println("Drawing display list " + gearDisplayList + ", context: 0x" + Integer.toHexString(gl.getContext().hashCode()));
      gl.glCallList(gearDisplayList);
    }

    @Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
      GL2 gl = drawable.getGL().getGL2();

      float h = (float)height / (float)width;

      gl.glMatrixMode(GL2.GL_PROJECTION);
      gl.glLoadIdentity();
      gl.glFrustum(-1.0f, 1.0f, -h, h, 5.0f, 60.0f);
      gl.glMatrixMode(GL2.GL_MODELVIEW);
      gl.glLoadIdentity();
      gl.glTranslatef(0.0f, 0.0f, -40.0f);
    }

    // Unused routines
    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {}
  }

  private synchronized void initializeDisplayList(GL2 gl) {
    if (gearDisplayList != 0) {
      return;
    }

    gearDisplayList = gl.glGenLists(1);
    gl.glNewList(gearDisplayList, GL2.GL_COMPILE);
    float red[] = { 0.8f, 0.1f, 0.0f, 1.0f };
    gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE, red, 0);
    gear(gl, 1.0f, 4.0f, 1.0f, 20, 0.7f);
    gl.glEndList();
    System.err.println(Thread.currentThread() + " Initialized display list " + gearDisplayList + ", context: 0x" + Integer.toHexString(gl.getContext().hashCode()));
  }

  private void gear(GL2 gl,
                    float inner_radius,
                    float outer_radius,
                    float width,
                    int teeth,
                    float tooth_depth)
  {
    int i;
    float r0, r1, r2;
    float angle, da;
    float u, v, len;

    r0 = inner_radius;
    r1 = outer_radius - tooth_depth / 2.0f;
    r2 = outer_radius + tooth_depth / 2.0f;

    da = 2.0f * (float) Math.PI / teeth / 4.0f;

    gl.glShadeModel(GL2.GL_FLAT);

    gl.glNormal3f(0.0f, 0.0f, 1.0f);

    /* draw front face */
    gl.glBegin(GL2.GL_QUAD_STRIP);
    for (i = 0; i <= teeth; i++)
      {
        angle = i * 2.0f * (float) Math.PI / teeth;
        gl.glVertex3f(r0 * (float)Math.cos(angle), r0 * (float)Math.sin(angle), width * 0.5f);
        gl.glVertex3f(r1 * (float)Math.cos(angle), r1 * (float)Math.sin(angle), width * 0.5f);
        if(i < teeth)
          {
            gl.glVertex3f(r0 * (float)Math.cos(angle), r0 * (float)Math.sin(angle), width * 0.5f);
            gl.glVertex3f(r1 * (float)Math.cos(angle + 3.0f * da), r1 * (float)Math.sin(angle + 3.0f * da), width * 0.5f);
          }
      }
    gl.glEnd();

    /* draw front sides of teeth */
    gl.glBegin(GL2.GL_QUADS);
    for (i = 0; i < teeth; i++)
      {
        angle = i * 2.0f * (float) Math.PI / teeth;
        gl.glVertex3f(r1 * (float)Math.cos(angle), r1 * (float)Math.sin(angle), width * 0.5f);
        gl.glVertex3f(r2 * (float)Math.cos(angle + da), r2 * (float)Math.sin(angle + da), width * 0.5f);
        gl.glVertex3f(r2 * (float)Math.cos(angle + 2.0f * da), r2 * (float)Math.sin(angle + 2.0f * da), width * 0.5f);
        gl.glVertex3f(r1 * (float)Math.cos(angle + 3.0f * da), r1 * (float)Math.sin(angle + 3.0f * da), width * 0.5f);
      }
    gl.glEnd();

    /* draw back face */
    gl.glBegin(GL2.GL_QUAD_STRIP);
    for (i = 0; i <= teeth; i++)
      {
        angle = i * 2.0f * (float) Math.PI / teeth;
        gl.glVertex3f(r1 * (float)Math.cos(angle), r1 * (float)Math.sin(angle), -width * 0.5f);
        gl.glVertex3f(r0 * (float)Math.cos(angle), r0 * (float)Math.sin(angle), -width * 0.5f);
        gl.glVertex3f(r1 * (float)Math.cos(angle + 3 * da), r1 * (float)Math.sin(angle + 3 * da), -width * 0.5f);
        gl.glVertex3f(r0 * (float)Math.cos(angle), r0 * (float)Math.sin(angle), -width * 0.5f);
      }
    gl.glEnd();

    /* draw back sides of teeth */
    gl.glBegin(GL2.GL_QUADS);
    for (i = 0; i < teeth; i++)
      {
        angle = i * 2.0f * (float) Math.PI / teeth;
        gl.glVertex3f(r1 * (float)Math.cos(angle + 3 * da), r1 * (float)Math.sin(angle + 3 * da), -width * 0.5f);
        gl.glVertex3f(r2 * (float)Math.cos(angle + 2 * da), r2 * (float)Math.sin(angle + 2 * da), -width * 0.5f);
        gl.glVertex3f(r2 * (float)Math.cos(angle + da), r2 * (float)Math.sin(angle + da), -width * 0.5f);
        gl.glVertex3f(r1 * (float)Math.cos(angle), r1 * (float)Math.sin(angle), -width * 0.5f);
      }
    gl.glEnd();

    /* draw outward faces of teeth */
    gl.glBegin(GL2.GL_QUAD_STRIP);
    for (i = 0; i < teeth; i++)
      {
        angle = i * 2.0f * (float) Math.PI / teeth;
        gl.glVertex3f(r1 * (float)Math.cos(angle), r1 * (float)Math.sin(angle), width * 0.5f);
        gl.glVertex3f(r1 * (float)Math.cos(angle), r1 * (float)Math.sin(angle), -width * 0.5f);
        u = r2 * (float)Math.cos(angle + da) - r1 * (float)Math.cos(angle);
        v = r2 * (float)Math.sin(angle + da) - r1 * (float)Math.sin(angle);
        len = (float)Math.sqrt(u * u + v * v);
        u /= len;
        v /= len;
        gl.glNormal3f(v, -u, 0.0f);
        gl.glVertex3f(r2 * (float)Math.cos(angle + da), r2 * (float)Math.sin(angle + da), width * 0.5f);
        gl.glVertex3f(r2 * (float)Math.cos(angle + da), r2 * (float)Math.sin(angle + da), -width * 0.5f);
        gl.glNormal3f((float)Math.cos(angle), (float)Math.sin(angle), 0.0f);
        gl.glVertex3f(r2 * (float)Math.cos(angle + 2 * da), r2 * (float)Math.sin(angle + 2 * da), width * 0.5f);
        gl.glVertex3f(r2 * (float)Math.cos(angle + 2 * da), r2 * (float)Math.sin(angle + 2 * da), -width * 0.5f);
        u = r1 * (float)Math.cos(angle + 3 * da) - r2 * (float)Math.cos(angle + 2 * da);
        v = r1 * (float)Math.sin(angle + 3 * da) - r2 * (float)Math.sin(angle + 2 * da);
        gl.glNormal3f(v, -u, 0.0f);
        gl.glVertex3f(r1 * (float)Math.cos(angle + 3 * da), r1 * (float)Math.sin(angle + 3 * da), width * 0.5f);
        gl.glVertex3f(r1 * (float)Math.cos(angle + 3 * da), r1 * (float)Math.sin(angle + 3 * da), -width * 0.5f);
        gl.glNormal3f((float)Math.cos(angle), (float)Math.sin(angle), 0.0f);
      }
    gl.glVertex3f(r1 * (float)Math.cos(0), r1 * (float)Math.sin(0), width * 0.5f);
    gl.glVertex3f(r1 * (float)Math.cos(0), r1 * (float)Math.sin(0), -width * 0.5f);
    gl.glEnd();

    gl.glShadeModel(GL2.GL_SMOOTH);

    /* draw inside radius cylinder */
    gl.glBegin(GL2.GL_QUAD_STRIP);
    for (i = 0; i <= teeth; i++)
      {
        angle = i * 2.0f * (float) Math.PI / teeth;
        gl.glNormal3f(-(float)Math.cos(angle), -(float)Math.sin(angle), 0.0f);
        gl.glVertex3f(r0 * (float)Math.cos(angle), r0 * (float)Math.sin(angle), -width * 0.5f);
        gl.glVertex3f(r0 * (float)Math.cos(angle), r0 * (float)Math.sin(angle), width * 0.5f);
      }
    gl.glEnd();
  }
}
