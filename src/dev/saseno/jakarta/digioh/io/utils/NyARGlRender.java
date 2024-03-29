package dev.saseno.jakarta.digioh.io.utils;

import java.awt.image.BufferedImage;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.awt.TextRenderer;

import dev.saseno.jakarta.digioh.wavefront.GLModel;
import jp.nyatla.nyartoolkit.core.NyARRuntimeException;
import jp.nyatla.nyartoolkit.core.param.NyARParam;
import jp.nyatla.nyartoolkit.core.raster.gs.INyARGrayscaleRaster;
import jp.nyatla.nyartoolkit.core.raster.rgb.INyARRgbRaster;
import jp.nyatla.nyartoolkit.core.types.NyARDoublePoint2d;
import jp.nyatla.nyartoolkit.core.types.NyARIntPoint2d;
import jp.nyatla.nyartoolkit.j2se.NyARBufferedImageRaster;
import jp.nyatla.nyartoolkit.markersystem.INyARSingleCameraSystemObserver;
import jp.nyatla.nyartoolkit.markersystem.NyARSingleCameraSystem;

public class NyARGlRender implements INyARSingleCameraSystemObserver {

	final protected double[] _projection_mat;

	public NyARGlRender(NyARSingleCameraSystem i_eventp) {
		this._projection_mat = new double[16];
		i_eventp.getSingleView().addObserver(this);
	}

	public void onUpdateCameraParametor(NyARParam i_param, double i_near, double i_far) {
		NyARGLUtil.toCameraFrustumRH(i_param, 1, i_near, i_far, this._projection_mat);
	}

	public final void loadARProjectionMatrix(GL i_gl) {
		loadARProjectionMatrix(i_gl, false);
	}
	
	public final void loadARProjectionMatrix(GL i_gl, boolean mirrored) {
		GL2 gl = i_gl.getGL2();
		int old_mode = NyARGLUtil.getGlMatrixMode(i_gl);
		
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadMatrixd(this._projection_mat, 0);
		if (mirrored) {
			gl.glScalef(-1f, 1f, 1f);
		}
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
		
		gl.glMatrixMode(old_mode);
	}

	public final void loadScreenProjectionMatrix(GL i_gl, int i_width, int i_height) {
		GL2 gl = i_gl.getGL2();
		int old_mode = NyARGLUtil.getGlMatrixMode(i_gl);
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrtho(0.0, i_width, i_height, 0, -1, 1);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glMatrixMode(old_mode);
		return;
	}

	public final void drawBackground(GL i_gl, INyARRgbRaster i_bg_image, boolean mirror, double width, double height) throws NyARRuntimeException {
		i_gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT); // Clear the buffers for new frame.
		NyARGLDrawUtil.drawBackGround(i_gl, i_bg_image, 1.0, mirror, width, height);
	}

	public final void drawStringInfo(GL i_gl, TextRenderer textInfo, String string, double width, double height) throws NyARRuntimeException {
		NyARGLDrawUtil.drawBackGround(i_gl, textInfo, string, 1.0, width, height);
	}

	public final void drawBackground(GL i_gl, INyARGrayscaleRaster i_bg_image, boolean mirror, int width, int height) throws NyARRuntimeException {
		i_gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT); // Clear the buffers for new frame.
		NyARGLDrawUtil.drawBackGround(i_gl, i_bg_image, 1.0, mirror, width, height);
	}

	public final void colorCube(GL i_gl, float i_size_per_mm, double i_x, double i_y, double i_z) {
		GL2 gl = i_gl.getGL2();
		int old_mode = NyARGLUtil.getGlMatrixMode(i_gl);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glPushMatrix();
		gl.glTranslated(i_x, i_y, i_z);
		NyARGLDrawUtil.drawColorCube(i_gl, i_size_per_mm);
		gl.glPopMatrix();
		gl.glMatrixMode(old_mode);
	}

	public final void drawText(GL i_gl, double i_x, double i_y, double i_z, double rotate, String text) {

		GL2 gl = i_gl.getGL2();
		int old_mode = NyARGLUtil.getGlMatrixMode(i_gl);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glPushMatrix();
		gl.glTranslated(i_x, i_y, i_z);
		
//		if (rotate > 0) {
//			i_gl.getGL2().getGL2().glRotated(rotate, 0, 1, 0);
//		}

		NyARGLDrawUtil.drawText(text, 1);

		gl.glPopMatrix();
		gl.glMatrixMode(old_mode);
	}

	public final void renderModel(GL i_gl, double i_x, double i_y, double i_z, double rotate, GLModel model) {

		GL2 gl = i_gl.getGL2();
		int old_mode = NyARGLUtil.getGlMatrixMode(i_gl);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glPushMatrix();
		gl.glTranslated(i_x, i_y, i_z);

		model.draw(i_gl, rotate);

		gl.glPopMatrix();
		gl.glMatrixMode(old_mode);
	}

	public final void colorCube(GL i_gl, float i_size_per_mm, double i_x, double i_y, double i_z, double rotate) {

		GL2 gl = i_gl.getGL2();
		int old_mode = NyARGLUtil.getGlMatrixMode(i_gl);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glPushMatrix();
		gl.glTranslated(i_x, i_y, i_z);
		
		NyARGLDrawUtil.drawColorCustom(i_gl, i_size_per_mm, rotate);
		
		gl.glPopMatrix();
		gl.glMatrixMode(old_mode);
	}

	public final void setColor(GL i_gl, float r, float g, float b) {
		GL2 gl = i_gl.getGL2();
		gl.glColor3f(r, g, b);
	}

	public final void setStrokeWeight(GL i_gl, float i_width) {
		i_gl.glLineWidth(i_width);
	}

	public final void line(GL i_gl, float i_x, double i_y, double i_x2, double i_y2) {
		GL2 gl = i_gl.getGL2();
		int old_mode = NyARGLUtil.getGlMatrixMode(i_gl);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glBegin(GL2.GL_LINE);

		gl.glVertex2d(i_x, i_y);
		gl.glEnd();
		gl.glMatrixMode(old_mode);
	}

	public final void polygon(GL i_gl, NyARDoublePoint2d[] i_vertex) {
		GL2 gl = i_gl.getGL2();
		int old_mode = NyARGLUtil.getGlMatrixMode(i_gl);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glBegin(GL.GL_LINE_LOOP);
		for (int i = 0; i < i_vertex.length; i++) {
			gl.glVertex2d(i_vertex[i].x, i_vertex[i].y);
		}
		gl.glEnd();
		gl.glMatrixMode(old_mode);
	}

	public final void polygon(GL i_gl, NyARIntPoint2d[] i_vertex) {
		GL2 gl = i_gl.getGL2();
		int old_mode = NyARGLUtil.getGlMatrixMode(i_gl);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glBegin(GL.GL_LINE_LOOP);
		for (int i = 0; i < i_vertex.length; i++) {
			gl.glVertex2d(i_vertex[i].x, i_vertex[i].y);
		}
		gl.glEnd();
		gl.glMatrixMode(old_mode);
	}

	public final void drawImage2d(GL i_gl, double i_x, double i_y, INyARRgbRaster i_raster) {
		GL2 gl = i_gl.getGL2();
		gl.glPushMatrix();
		try {
			gl.glMatrixMode(GL2.GL_MODELVIEW);
			gl.glLoadIdentity();
			gl.glTranslated(i_x, i_y, 0);
			NyARGLDrawUtil.drawRaster(i_gl, i_raster);
		} finally {
			gl.glPopMatrix();
		}
	}

	public final void drawImage2d(GL i_gl, double i_x, double i_y, BufferedImage i_bitmap) throws NyARRuntimeException {
		this.drawImage2d(i_gl, i_x, i_y, new NyARBufferedImageRaster(i_bitmap));
	}

}