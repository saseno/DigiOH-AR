package dev.saseno.jakarta.digioh.io.utils;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;

import jp.nyatla.nyartoolkit.core.param.NyARParam;
import jp.nyatla.nyartoolkit.core.param.NyARPerspectiveProjectionMatrix;
import jp.nyatla.nyartoolkit.core.types.NyARIntSize;
import jp.nyatla.nyartoolkit.core.types.matrix.NyARDoubleMatrix44;

public class NyARGLUtil {

	public final static double SCALE_FACTOR_toCameraFrustumRH_NYAR2 = 1.0;
	public final static double SCALE_FACTOR_toCameraViewRH_NYAR2 = 1 / 0.025;

	private NyARGLUtil() {
	}

	private static int[] __wk = new int[1];

	public synchronized static final int getGlMatrixMode(GL i_gl) {
		GL gl = i_gl;
		gl.glGetIntegerv(GL2.GL_MATRIX_MODE, __wk, 0);
		return __wk[0];
	}

	public static void toCameraFrustumRH(NyARParam i_arparam, double i_scale, double i_near, double i_far,
			double[] o_gl_projection) {
		toCameraFrustumRH(i_arparam.getPerspectiveProjectionMatrix(), i_arparam.getScreenSize(), i_scale, i_near, i_far,
				o_gl_projection);
		return;
	}

	public static void toCameraFrustumRH(NyARPerspectiveProjectionMatrix i_promat, NyARIntSize i_size, double i_scale,
			double i_near, double i_far, double[] o_gl_projection) {
		NyARDoubleMatrix44 m = new NyARDoubleMatrix44();
		i_promat.makeCameraFrustumRH(i_size.w, i_size.h, i_near * i_scale, i_far * i_scale, m);
		m.getValueT(o_gl_projection);
		return;
	}

	public static void toCameraViewRH(NyARDoubleMatrix44 mat, double i_scale, double[] o_gl_result) {
		o_gl_result[0 + 0 * 4] = mat.m00;
		o_gl_result[1 + 0 * 4] = -mat.m10;
		o_gl_result[2 + 0 * 4] = -mat.m20;
		o_gl_result[3 + 0 * 4] = 0.0;
		o_gl_result[0 + 1 * 4] = mat.m01;
		o_gl_result[1 + 1 * 4] = -mat.m11;
		o_gl_result[2 + 1 * 4] = -mat.m21;
		o_gl_result[3 + 1 * 4] = 0.0;
		o_gl_result[0 + 2 * 4] = mat.m02;
		o_gl_result[1 + 2 * 4] = -mat.m12;
		o_gl_result[2 + 2 * 4] = -mat.m22;
		o_gl_result[3 + 2 * 4] = 0.0;

		double scale = 1 / i_scale;
		o_gl_result[0 + 3 * 4] = mat.m03 * scale;
		o_gl_result[1 + 3 * 4] = -mat.m13 * scale;
		o_gl_result[2 + 3 * 4] = -mat.m23 * scale;
		o_gl_result[3 + 3 * 4] = 1.0;
		return;
	}

	public static void toCameraViewRHFlipY(NyARDoubleMatrix44 mat, double i_scale, double[] o_gl_result) {
		o_gl_result[0 + 0 * 4] = mat.m00 ;
		o_gl_result[1 + 0 * 4] = -mat.m10;
		o_gl_result[2 + 0 * 4] = -mat.m20;
		o_gl_result[3 + 0 * 4] = 0.0;
		o_gl_result[0 + 1 * 4] = mat.m01;
		o_gl_result[1 + 1 * 4] = -mat.m11;
		o_gl_result[2 + 1 * 4] = -mat.m21;
		o_gl_result[3 + 1 * 4] = 0.0;
		o_gl_result[0 + 2 * 4] = mat.m02;
		o_gl_result[1 + 2 * 4] = -mat.m12;
		o_gl_result[2 + 2 * 4] = -mat.m22;
		o_gl_result[3 + 2 * 4] = 0.0;

		double scale = 1 / i_scale;
		o_gl_result[0 + 3 * 4] = mat.m03 * scale;
		o_gl_result[1 + 3 * 4] = -mat.m13 * scale;
		o_gl_result[2 + 3 * 4] = -mat.m23 * scale;
		o_gl_result[3 + 3 * 4] = 1.0;
		return;
	}

}
