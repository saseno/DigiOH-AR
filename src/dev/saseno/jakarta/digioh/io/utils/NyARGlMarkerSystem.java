package dev.saseno.jakarta.digioh.io.utils;

import java.awt.image.BufferedImage;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;

import jp.nyatla.nyartoolkit.core.NyARRuntimeException;
import jp.nyatla.nyartoolkit.core.marker.artk.NyARCode;
import jp.nyatla.nyartoolkit.core.raster.rgb.INyARRgbRaster;
import jp.nyatla.nyartoolkit.core.raster.rgb.NyARRgbRaster;
import jp.nyatla.nyartoolkit.core.rasterdriver.perspectivecopy.INyARPerspectiveCopy;
import jp.nyatla.nyartoolkit.j2se.NyARBufferedImageRaster;
import jp.nyatla.nyartoolkit.markersystem.INyARMarkerSystemConfig;
import jp.nyatla.nyartoolkit.markersystem.NyARMarkerSystem;
import jp.nyatla.nyartoolkit.markersystem.NyARSensor;

public class NyARGlMarkerSystem extends NyARMarkerSystem {

	public NyARGlMarkerSystem(INyARMarkerSystemConfig i_config) throws NyARRuntimeException {
		super(i_config);
	}
	
	public double[] getGlTransformMatrix(int i_id, double[] o_buf) {
		NyARGLUtil.toCameraViewRH(this.getTransformMatrix(i_id), 1, o_buf);
		return o_buf;
	}

	public double[] getGlTransformMatrix(int i_id) {
		return this.getGlTransformMatrix(i_id, new double[16]);
	}

	public int addARMarker(BufferedImage i_img, int i_patt_resolution, int i_patt_edge_percentage,
			double i_marker_size) {
		
		int w = i_img.getWidth();
		int h = i_img.getHeight();
		NyARBufferedImageRaster bmr = new NyARBufferedImageRaster(i_img);
		NyARCode c = new NyARCode(i_patt_resolution, i_patt_resolution);
		
		INyARPerspectiveCopy pc = (INyARPerspectiveCopy) bmr.createInterface(INyARPerspectiveCopy.class);
		INyARRgbRaster tr = NyARRgbRaster.createInstance(i_patt_resolution, i_patt_resolution);
		pc.copyPatt(0, 0, w, 0, w, h, 0, h, i_patt_edge_percentage, i_patt_edge_percentage, 4, tr);
		
		c.setRaster(tr);
		return super.addARMarker(c, i_patt_edge_percentage, i_marker_size);
	}

	public void getPlaneImage(int i_id, NyARSensor i_sensor, int i_x1, int i_y1, int i_x2, int i_y2, int i_x3, int i_y3,
			int i_x4, int i_y4, BufferedImage i_img) {
		NyARBufferedImageRaster bmr = new NyARBufferedImageRaster(i_img);
		super.getPlaneImage(i_id, i_sensor, i_x1, i_y1, i_x2, i_y2, i_x3, i_y3, i_x4, i_y4, bmr);
		return;
	}

	public void getMarkerPlaneImage(int i_id, NyARSensor i_sensor, int i_x1, int i_y1, int i_x2, int i_y2, int i_x3,
			int i_y3, int i_x4, int i_y4, BufferedImage i_img) {
		this.getPlaneImage(i_id, i_sensor, i_x1, i_y1, i_x2, i_y2, i_x3, i_y3, i_x4, i_y4, i_img);
	}

	public void getPlaneImage(int i_id, NyARSensor i_sensor, int i_l, int i_t, int i_w, int i_h, BufferedImage i_img) {
		NyARBufferedImageRaster bmr = new NyARBufferedImageRaster(i_img);
		super.getPlaneImage(i_id, i_sensor, i_l, i_t, i_w, i_h, bmr);
		this.getPlaneImage(i_id, i_sensor, i_l + i_w - 1, i_t + i_h - 1, i_l, i_t + i_h - 1, i_l, i_t, i_l + i_w - 1,
				i_t, bmr);
		return;
	}

	public void getMarkerPlaneImage(int i_id, NyARSensor i_sensor, int i_l, int i_t, int i_w, int i_h,
			BufferedImage i_img) {
		this.getPlaneImage(i_id, i_sensor, i_l, i_t, i_w, i_h, i_img);
	}

	final private double[] _mv_mat = new double[16];

	public void loadTransformMatrix(GL i_gl, int i_id) {
		GL2 gl = i_gl.getGL2();
		int old_mode = NyARGLUtil.getGlMatrixMode(i_gl);
		if (old_mode != GL2.GL_MODELVIEW) {
			gl.glMatrixMode(GL2.GL_MODELVIEW);
			
			NyARGLUtil.toCameraViewRH(this.getTransformMatrix(i_id), 1, this._mv_mat);
			
			gl.glLoadMatrixd(this._mv_mat, 0);
			gl.glMatrixMode(old_mode);
		} else {
			NyARGLUtil.toCameraViewRH(this.getTransformMatrix(i_id), 1, this._mv_mat);
			gl.glLoadMatrixd(this._mv_mat, 0);
		}
	}

	public void loadTransformMatrix(GL i_gl, int i_id, double rotate) {
		GL2 gl = i_gl.getGL2();
		int old_mode = NyARGLUtil.getGlMatrixMode(i_gl);
		if (old_mode != GL2.GL_MODELVIEW) {
			gl.glMatrixMode(GL2.GL_MODELVIEW);
			NyARGLUtil.toCameraViewRH(this.getTransformMatrix(i_id), 1, this._mv_mat);
			gl.glLoadMatrixd(this._mv_mat, 0);
			gl.glMatrixMode(old_mode);
		} else {
			NyARGLUtil.toCameraViewRH(this.getTransformMatrix(i_id), 1, this._mv_mat);
			gl.glLoadMatrixd(this._mv_mat, 0);
		}
	}

}