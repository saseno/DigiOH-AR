package dev.saseno.jakarta.digioh.io.utils;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.awt.TextRenderer;

import dev.saseno.jakarta.digioh.wavefront.GLModel;
import jp.nyatla.nyartoolkit.core.NyARRuntimeException;
import jp.nyatla.nyartoolkit.core.raster.INyARRaster;
import jp.nyatla.nyartoolkit.core.raster.rgb.INyARRgbRaster;
import jp.nyatla.nyartoolkit.core.types.NyARBufferType;
import jp.nyatla.nyartoolkit.core.types.NyARIntSize;

public class NyARGLDrawUtil {
	private static TextRenderer _tr = new TextRenderer(new Font("SansSerif", Font.PLAIN, 10));
	
	private static float[][] COLORCUBE_COLOR = new float[][] { { 1.0f, 1.0f, 1.0f }, { 1.0f, 1.0f, 0.0f },
			{ 0.0f, 1.0f, 0.0f }, { 0.0f, 1.0f, 1.0f }, { 1.0f, 0.0f, 1.0f }, { 1.0f, 0.0f, 0.0f },
			{ 0.0f, 0.0f, 0.0f }, { 0.0f, 0.0f, 1.0f } };
			
	private static float[][] CUBE_VERTICES = new float[][] { { 1.0f, 1.0f, 1.0f }, { 1.0f, -1.0f, 1.0f },
			{ -1.0f, -1.0f, 1.0f }, { -1.0f, 1.0f, 1.0f }, { 1.0f, 1.0f, -1.0f }, { 1.0f, -1.0f, -1.0f },
			{ -1.0f, -1.0f, -1.0f }, { -1.0f, 1.0f, -1.0f } };
			
	private static short[][] CUBE_FACE = new short[][] { { 3, 2, 1, 0 }, { 2, 3, 7, 6 }, { 0, 1, 5, 4 }, { 3, 0, 4, 7 },
			{ 1, 2, 6, 5 }, { 4, 5, 6, 7 } };

	public static void drawCube(GL i_gl, float i_size_per_mm, float i_r, float i_g, float i_b) {
		float v[] = { i_r, i_g, i_b };
		float vs[][] = { v, v, v, v, v, v, v, v };
		drawColorCube(i_gl, i_size_per_mm, vs);
	}

	public static void drawColorCube(GL i_gl, float i_size_per_mm, double rotate) {
		drawColorCube(i_gl, i_size_per_mm, COLORCUBE_COLOR, rotate);
	}
	
	public static void drawColorCube(GL i_gl, float i_size_per_mm) {
		drawColorCube(i_gl, i_size_per_mm, COLORCUBE_COLOR);
	}
	
	public static int gen3DObjectList(GL2 gl, float i_size_per_mm, double rotate, GLModel model) throws IOException {
		
		float fSize = i_size_per_mm * 0.7f;		
		gl.getGL2().glRotated(90, 1, 0, 0);
		
		if (rotate > 0) {
			gl.getGL2().glRotated(rotate, 0, 1, 0);			
		}
		
		int lid = gl.glGenLists(1);
		gl.glNewList(lid, GL2.GL_COMPILE);
		model.draw(gl, fSize);
        gl.glEndList();

		gl.glCallList(lid);	
		
		return lid;
	}
	
	public static void drawColorCustom(GL i_gl, float i_size_per_mm, double rotate) {
		float[][] i_color = COLORCUBE_COLOR;
		GL2 gl = i_gl.getGL2();
		
		if (rotate > 0) {
			gl.getGL2().glRotated(rotate, 0, 0, 1);			
		}
		
		// Colour cube data.
		int polyList = 0;
		float fSize = i_size_per_mm / 2f;
		int f, i;
		int cube_num_faces = 6; //meshArrays.getNumVertices();

		if (polyList == 0) {
			polyList = gl.glGenLists(1);
			gl.glNewList(polyList, GL2.GL_COMPILE);			
			gl.glBegin(GL2.GL_QUADS);
			
			for (f = 0; f < cube_num_faces; f++) {
				for (i = 0; i < 4; i++) {
					gl.glColor3f(i_color[CUBE_FACE[f][i]][0], i_color[CUBE_FACE[f][i]][1], i_color[CUBE_FACE[f][i]][2]);
					gl.glVertex3f(CUBE_VERTICES[CUBE_FACE[f][i]][0] * fSize, CUBE_VERTICES[CUBE_FACE[f][i]][1] * fSize,
							CUBE_VERTICES[CUBE_FACE[f][i]][2] * fSize);
					
				}
			}
			
			gl.glEnd();
			gl.glColor3f(1.0f, 0.0f, 0.0f);
			for (f = 0; f < cube_num_faces; f++) {
				gl.glBegin(GL.GL_LINE_LOOP);
				for (i = 0; i < 4; i++) {

					gl.glVertex3f(CUBE_VERTICES[CUBE_FACE[f][i]][0] * fSize, CUBE_VERTICES[CUBE_FACE[f][i]][1] * fSize,
							CUBE_VERTICES[CUBE_FACE[f][i]][2] * fSize);

				}
				
				gl.glEnd();
			}
			gl.glEndList();
		}		
		
		gl.glCallList(polyList); // Draw the cube.		
		
	}
	
	private static void drawColorCube(GL i_gl, float i_size_per_mm, float[][] i_color, double rotate) {
		
		GL2 gl = i_gl.getGL2();
		
		if (rotate > 0) {
			gl.getGL2().glRotated(rotate, 0, 0, 1);			
		}
		
		// Colour cube data.
		int polyList = 0;
		float fSize = i_size_per_mm / 2f;
		int f, i;
		int cube_num_faces = 6;

		if (polyList == 0) {
			polyList = gl.glGenLists(1);
			
			gl.glNewList(polyList, GL2.GL_COMPILE);			
			gl.glBegin(GL2.GL_QUADS);
			
			for (f = 0; f < cube_num_faces; f++) {
				for (i = 0; i < 4; i++) {
					gl.glColor3f(i_color[CUBE_FACE[f][i]][0], i_color[CUBE_FACE[f][i]][1], i_color[CUBE_FACE[f][i]][2]);
					gl.glVertex3f(CUBE_VERTICES[CUBE_FACE[f][i]][0] * fSize, CUBE_VERTICES[CUBE_FACE[f][i]][1] * fSize,
							CUBE_VERTICES[CUBE_FACE[f][i]][2] * fSize);
				}
			}
			
			gl.glEnd();
			gl.glColor3f(0.0f, 0.0f, 0.0f);
			
			for (f = 0; f < cube_num_faces; f++) {
				gl.glBegin(GL.GL_LINE_LOOP);
				for (i = 0; i < 4; i++) {
					gl.glVertex3f(CUBE_VERTICES[CUBE_FACE[f][i]][0] * fSize, CUBE_VERTICES[CUBE_FACE[f][i]][1] * fSize,
							CUBE_VERTICES[CUBE_FACE[f][i]][2] * fSize);
				}
				gl.glEnd();
			}
			
			gl.glEndList();
		}		
		
		gl.glCallList(polyList); // Draw the cube.		
	}

	private static void drawColorCube(GL i_gl, float i_size_per_mm, float[][] i_color) {
		drawColorCube(i_gl, i_size_per_mm, -1);
	}

	public static void setFontColor(Color i_c) {
		NyARGLDrawUtil._tr.setColor(i_c);
	}

	public static void setFontStyle(String i_font_name, int i_font_style, int i_size) {
		NyARGLDrawUtil._tr = new TextRenderer(new Font(i_font_name, i_font_style, i_size));
	}

	public static void drawText(String i_str, float i_scale) {
		NyARGLDrawUtil._tr.begin3DRendering();
		NyARGLDrawUtil._tr.draw3D(i_str, 1f, 0f, 0f, i_scale);
		NyARGLDrawUtil._tr.end3DRendering();
		return;
	}

	public static void drawBackGround(GL i_gl, INyARRaster i_raster, double i_zoom, boolean mirror, double width, double height) throws NyARRuntimeException {
		GL2 gl = i_gl.getGL2();
		
		IntBuffer texEnvModeSave = IntBuffer.allocate(1);
		boolean lightingSave;
		boolean depthTestSave;
		
		final NyARIntSize rsize = i_raster.getSize();
		
		// Prepare an orthographic projection, set camera position for 2D drawing, and save GL state.
		// Save GL texture environment mode.
		gl.glGetTexEnviv(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, texEnvModeSave); 
		if (texEnvModeSave.array()[0] != GL.GL_REPLACE) {
			gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL.GL_REPLACE);
		}
		
		// Save enabled state of lighting.
		lightingSave = gl.glIsEnabled(GL2.GL_LIGHTING); 
		if (lightingSave == true) {
			i_gl.glDisable(GL2.GL_LIGHTING);
		}
		
		// Save enabled state of depth test.
		depthTestSave = i_gl.glIsEnabled(GL.GL_DEPTH_TEST); 
		if (depthTestSave == true) {
			i_gl.glDisable(GL.GL_DEPTH_TEST);
		}
		
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glPushMatrix();
		gl.glLoadIdentity();
		
		gl.glOrtho(-100.0, rsize.w, 0.0, rsize.h, 0, 1);
		
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glPushMatrix();
		gl.glLoadIdentity();
		
		arglDispImageStateful(i_gl, rsize, i_raster, i_zoom, mirror, width, height);
		
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glPopMatrix();
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glPopMatrix();
		if (depthTestSave) {
			// Restore enabled state of depth test.
			i_gl.glEnable(GL.GL_DEPTH_TEST); 
		}
		if (lightingSave) {
			// Restore enabled state of lighting.
			gl.glEnable(GL2.GL_LIGHTING); 
		}
		if (texEnvModeSave.get(0) != GL.GL_REPLACE) {
			// Restore GL texture environment mode.
			gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, texEnvModeSave.get(0)); 
		}
		gl.glEnd();
	}

	public static void drawBackGround(GL i_gl, TextRenderer textInfo, String string, double i_zoom, double width, double height) throws NyARRuntimeException {
		GL2 gl = i_gl.getGL2();
		
		IntBuffer texEnvModeSave = IntBuffer.allocate(1);
		boolean lightingSave;
		boolean depthTestSave;
				
		// Prepare an orthographic projection, set camera position for 2D drawing, and save GL state.
		// Save GL texture environment mode.
		gl.glGetTexEnviv(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, texEnvModeSave); 
		if (texEnvModeSave.array()[0] != GL.GL_REPLACE) {
			gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL.GL_REPLACE);
		}
		
		// Save enabled state of lighting.
		lightingSave = gl.glIsEnabled(GL2.GL_LIGHTING); 
		if (lightingSave == true) {
			i_gl.glDisable(GL2.GL_LIGHTING);
		}
		
		// Save enabled state of depth test.
		depthTestSave = i_gl.glIsEnabled(GL.GL_DEPTH_TEST); 
		if (depthTestSave == true) {
			i_gl.glDisable(GL.GL_DEPTH_TEST);
		}
		
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glPushMatrix();
		gl.glLoadIdentity();
		
		gl.glOrtho(-100.0, width, 0.0, height, 0, 1);
		
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glPushMatrix();
		gl.glLoadIdentity();

		gl.glDisable(GL2.GL_TEXTURE_2D);
		textInfo.beginRendering((int) width, (int) height);
		textInfo.setColor(1.0f, 0.2f, 0.2f, 1f);
		textInfo.draw(string, 1, 1);
		textInfo.endRendering();	
		
		//arglDispImageStateful(i_gl, rsize, i_raster, i_zoom, mirror, width, height);
		
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glPopMatrix();
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glPopMatrix();
		if (depthTestSave) {
			// Restore enabled state of depth test.
			i_gl.glEnable(GL.GL_DEPTH_TEST); 
		}
		if (lightingSave) {
			// Restore enabled state of lighting.
			gl.glEnable(GL2.GL_LIGHTING); 
		}
		if (texEnvModeSave.get(0) != GL.GL_REPLACE) {
			// Restore GL texture environment mode.
			gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, texEnvModeSave.get(0)); 
		}
		gl.glEnd();
	}

	public static void drawRaster(GL i_gl, INyARRgbRaster i_raster) throws NyARRuntimeException {
		GL2 gl = i_gl.getGL2();

		NyARIntSize s = i_raster.getSize();
		int[] n = new int[1];
		float[] color = new float[3];
		boolean old_is_texture_2d = i_gl.glIsEnabled(GL.GL_TEXTURE_2D);
		gl.glGetFloatv(GL2.GL_CURRENT_COLOR, color, 0);
		gl.glColor3f(1, 1, 1);
		gl.glEnable(GL.GL_TEXTURE_2D);
		gl.glPixelStorei(GL.GL_UNPACK_ALIGNMENT, 1);
		gl.glGenTextures(1, n, 0);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
		
		switch (i_raster.getBufferType()) {
		
		case NyARBufferType.BYTE1D_R8G8B8_24:
			i_gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGB, s.w, s.h, 0, GL.GL_RGB, GL.GL_UNSIGNED_BYTE,
					ByteBuffer.wrap((byte[]) i_raster.getBuffer()));
			break;
			
		case NyARBufferType.BYTE1D_B8G8R8_24:
			i_gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGB, s.w, s.h, 0, GL.GL_BGR, GL.GL_UNSIGNED_BYTE,
					ByteBuffer.wrap((byte[]) i_raster.getBuffer()));
			break;
			
		case NyARBufferType.BYTE1D_B8G8R8X8_32:
			i_gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGB, s.w, s.h, 0, GL.GL_BGRA, GL.GL_UNSIGNED_BYTE,
					ByteBuffer.wrap((byte[]) i_raster.getBuffer()));
			break;
			
		case NyARBufferType.INT1D_X8R8G8B8_32:
			i_gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGB, s.w, s.h, 0, GL.GL_BGRA, GL.GL_UNSIGNED_BYTE,
					IntBuffer.wrap((int[]) i_raster.getBuffer()));
			break;
			
		default:
			throw new NyARRuntimeException();
			
		}
		
		gl.glBegin(GL2.GL_QUADS);
		gl.glBindTexture(GL.GL_TEXTURE_2D, n[0]);
		gl.glTexCoord2f(0, 0);
		gl.glVertex3f(0, 0, 0);
		gl.glTexCoord2f(1, 0);
		gl.glVertex3f(s.w, 0, 0);
		gl.glTexCoord2f(1, 1);
		gl.glVertex3f(s.w, s.h, 0);
		gl.glTexCoord2f(0, 1);
		gl.glVertex3f(0, s.h, 0);
		gl.glEnd();
		i_gl.glDeleteTextures(1, n, 0);
		
		if (!old_is_texture_2d) {
			i_gl.glDisable(GL.GL_TEXTURE_2D);
		}
		
		gl.glColor3fv(color, 0);
	}

//	private static void arglDispImageStateful(GL i_gl, NyARIntSize i_size, INyARRaster i_raster,
//			double zoom) throws NyARRuntimeException {
//		
//		arglDispImageStateful(i_gl, i_size, i_raster, zoom, false);
//	}

	private static void arglDispImageStateful(GL i_gl, NyARIntSize i_size, INyARRaster i_raster, double zoom,
			boolean mirror, double width, double height) throws NyARRuntimeException {

		Object i_buffer = i_raster.getBuffer();
		int i_buffer_type = i_raster.getBufferType();

		GL2 gl = i_gl.getGL2();

		float zoomf;
		IntBuffer params = IntBuffer.allocate(4);
		zoomf = (float) zoom;
		gl.glDisable(GL.GL_TEXTURE_2D);
		gl.glGetIntegerv(GL.GL_VIEWPORT, params);
		
		int mirrorZoom = 1;
		float startX = 0;
		float startY = (float)height; //i_raster.getHeight();

		//System.err.println("--> " + startY);
		
		if (mirror) {
			mirrorZoom = -1;
			startX = (float)width;
		}
		
		gl.glPixelZoom(mirrorZoom * zoomf * ((float) (params.get(2)) / (float) i_size.w),
				-zoomf * ((float) (params.get(3)) / (float) i_size.h));
		
		gl.glWindowPos2f(startX, startY);
		gl.glPixelStorei(GL.GL_UNPACK_ALIGNMENT, 1);

		switch (i_buffer_type) {
		case NyARBufferType.BYTE1D_B8G8R8_24:
			gl.glDrawPixels(i_size.w, i_size.h, GL.GL_BGR, GL.GL_UNSIGNED_BYTE, ByteBuffer.wrap((byte[]) i_buffer));
			break;
			
		case NyARBufferType.BYTE1D_R8G8B8_24:
			gl.glDrawPixels(i_size.w, i_size.h, GL.GL_RGB, GL.GL_UNSIGNED_BYTE, ByteBuffer.wrap((byte[]) i_buffer));
			break;
			
		case NyARBufferType.BYTE1D_B8G8R8X8_32:
			gl.glDrawPixels(i_size.w, i_size.h, GL.GL_BGRA, GL.GL_UNSIGNED_BYTE, ByteBuffer.wrap((byte[]) i_buffer));
			break;
			
		case NyARBufferType.INT1D_X8R8G8B8_32:
			gl.glDrawPixels(i_size.w, i_size.h, GL.GL_BGRA, GL.GL_UNSIGNED_BYTE, IntBuffer.wrap((int[]) i_buffer));
			break;
			
		case NyARBufferType.INT1D_GRAY_8:
			gl.glDrawPixels(i_size.w, i_size.h, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, IntBuffer.wrap((int[]) i_buffer));
			break;
			
		default:
			throw new NyARRuntimeException();
			
		}
	}

	public static void beginScreenCoordinateSystem(GL i_gl, int i_width, int i_height, boolean i_revers_y_direction) {
		GL2 gl = i_gl.getGL2();

		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glPushMatrix(); // Save world coordinate system.
		gl.glLoadIdentity();
		
		if (i_revers_y_direction) {
			gl.glOrtho(0.0, i_width, i_height, 0, -1, 1);
		} else {
			gl.glOrtho(0.0, i_width, 0, i_height, -1, 1);
		}
		
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glPushMatrix(); // Save world coordinate system.
		gl.glLoadIdentity();
		return;
	}

	public static void endScreenCoordinateSystem(GL i_gl) {
		GL2 gl = i_gl.getGL2();
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glPopMatrix();
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glPopMatrix();
		return;
	}
}
