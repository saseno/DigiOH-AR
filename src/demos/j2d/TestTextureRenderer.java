/*
 * Copyright (c) 2006 Sun Microsystems, Inc. All Rights Reserved.
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

package demos.j2d;

import gleem.linalg.Vec2f;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.text.DecimalFormat;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;

import com.jogamp.opengl.util.Animator;
import com.jogamp.opengl.util.awt.TextureRenderer;

import demos.gears.Gears;
import demos.util.SystemTime;
import demos.util.Time;



/** A simple test of the TextureRenderer utility class. Draws gears
    underneath with moving Java 2D-rendered text on top. */

public class TestTextureRenderer implements GLEventListener {
  public static void main(String[] args) {
    Frame frame = new Frame("Java 2D Renderer Test");
    GLCapabilities caps = new GLCapabilities(GLProfile.get(GLProfile.GL2));
    caps.setAlphaBits(8);

    GLCanvas canvas = new GLCanvas(caps);
    canvas.addGLEventListener(new Gears());
    canvas.addGLEventListener(new TestTextureRenderer());
    frame.add(canvas);
    frame.setSize(512, 512);
    final Animator animator = new Animator(canvas);
    frame.addWindowListener(new WindowAdapter() {
        @Override
		public void windowClosing(WindowEvent e) {
          // Run this on another thread than the AWT event queue to
          // make sure the call to Animator.stop() completes before
          // exiting
          new Thread(new Runnable() {
              @Override
			public void run() {
                animator.stop();
                System.exit(0);
              }
            }).start();
        }
      });
    frame.setVisible(true);
    animator.start();
  }

  private TextureRenderer renderer;
  private Time time;
  private Font font;
  private final Color TRANSPARENT_BLACK = new Color(0.0f, 0.0f, 0.0f, 0.0f);
  private final Vec2f velocity = new Vec2f(100.0f, 150.0f);
  private Vec2f position;
  private Rectangle textBounds;
  private Rectangle fpsBounds;
  private final String TEST_STRING = "Java 2D Text";
  private long startTime;
  private int frameCount;
  private final DecimalFormat format = new DecimalFormat("####.00");

  @Override
public void init(GLAutoDrawable drawable) {
    GL gl = drawable.getGL();
    gl.setSwapInterval(0);

    renderer = new TextureRenderer(256, 256, true);
    time = new SystemTime();
    ((SystemTime) time).rebase();

    // Start the text half way up the left side
    position = new Vec2f(0.0f, drawable.getSurfaceHeight() / 2);

    // Create the font, render context, and glyph vector
    font = new Font("SansSerif", Font.BOLD, 36);
    Graphics2D g2d = renderer.createGraphics();
    g2d.setFont(font);
    g2d.setComposite(AlphaComposite.Src);
    FontRenderContext frc = g2d.getFontRenderContext();
    GlyphVector gv = font.createGlyphVector(frc, TEST_STRING);
    g2d.setColor(TRANSPARENT_BLACK);
    g2d.fillRect(0, 0, renderer.getWidth(), renderer.getHeight());
    g2d.setColor(Color.WHITE);
    g2d.drawString(TEST_STRING, 10, 50);
    textBounds = gv.getPixelBounds(frc, 10, 50);
    renderer.markDirty(textBounds.x, textBounds.y, textBounds.width, textBounds.height);
  }

  @Override
public void dispose(GLAutoDrawable drawable) {
    renderer = null;
    textBounds = null;
    position = null;
    time = null;
  }

  @Override
public void display(GLAutoDrawable drawable) {
    if (startTime == 0) {
      startTime = System.currentTimeMillis();
    }

    if (++frameCount == 100) {
      long endTime = System.currentTimeMillis();
      float fps = 100.0f / (endTime - startTime) * 1000;
      frameCount = 0;
      startTime = System.currentTimeMillis();

      Graphics2D g2d = renderer.createGraphics();
      g2d.setFont(font);
      g2d.setComposite(AlphaComposite.Src);
      FontRenderContext frc = g2d.getFontRenderContext();
      String fpsString = "FPS: " + format.format(fps);
      GlyphVector gv = font.createGlyphVector(frc, TEST_STRING);
      fpsBounds = gv.getPixelBounds(frc, 10, 100);
      g2d.setColor(TRANSPARENT_BLACK);
      g2d.fillRect(fpsBounds.x, fpsBounds.y, fpsBounds.width, fpsBounds.height);
      g2d.setColor(Color.WHITE);
      g2d.drawString(fpsString, 10, 100);
      renderer.markDirty(fpsBounds.x, fpsBounds.y, fpsBounds.width, fpsBounds.height);
    }

    time.update();

    // Compute the next position of the text
    position = position.plus(velocity.times((float) time.deltaT()));
    // Figure out whether we have to switch directions
    Rectangle tmpBounds = new Rectangle((int) position.x(), (int) position.y(),
                                        textBounds.width, textBounds.height);
    if (tmpBounds.getMinX() < 0) {
      velocity.setX(Math.abs(velocity.x()));
    } else if (tmpBounds.getMaxX() > drawable.getSurfaceWidth()) {
      velocity.setX(-1.0f * Math.abs(velocity.x()));
    }
    if (tmpBounds.getMinY() < 0) {
      velocity.setY(Math.abs(velocity.y()));
    } else if (tmpBounds.getMaxY() > drawable.getSurfaceHeight()) {
      velocity.setY(-1.0f * Math.abs(velocity.y()));
    }

    // Prepare to draw from the renderer's texture
    renderer.beginOrthoRendering(drawable.getSurfaceWidth(), drawable.getSurfaceHeight());

    // Draw from the renderer's texture
    renderer.drawOrthoRect((int) position.x(), (int) position.y(),
                           textBounds.x,
                           renderer.getHeight() - textBounds.y - textBounds.height,
                           textBounds.width,
                           textBounds.height);

    // If we have the FPS, draw it
    if (fpsBounds != null) {
      renderer.drawOrthoRect(drawable.getSurfaceWidth() - fpsBounds.width,
                             20,
                             fpsBounds.x,
                             renderer.getHeight() - fpsBounds.y - fpsBounds.height,
                             fpsBounds.width,
                             fpsBounds.height);
    }

    // Clean up rendering
    renderer.endOrthoRendering();
  }

  // Unused methods
  @Override
public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {}
  public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {}
}
