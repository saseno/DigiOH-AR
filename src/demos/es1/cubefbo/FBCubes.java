/*
 * Copyright (c) 2008 Sun Microsystems, Inc. All Rights Reserved.
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
 */

package demos.es1.cubefbo;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2ES1;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;

import com.jogamp.opengl.FBObject;
import com.jogamp.opengl.FBObject.Attachment;
import com.jogamp.opengl.FBObject.TextureAttachment;
import com.jogamp.opengl.util.glsl.fixedfunc.FixedFuncUtil;
import com.jogamp.opengl.util.glsl.fixedfunc.ShaderSelectionMode;

import demos.es1.cube.Cube;

public class FBCubes implements GLEventListener {
	private static final int FBO_SIZE = 256;

	public FBCubes() {
		cubeOuter = new Cube(true, false);

		fbo1 = new FBObject();
		cubeInner = new Cube(false, true);

		// JAU cubeMiddle = new Cube(true, false);
		// JAU fbo2 = new FBObject(FBO_SIZE, FBO_SIZE);
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		GL2ES1 gl = FixedFuncUtil.wrapFixedFuncEmul(drawable.getGL(), ShaderSelectionMode.AUTO, null);
		System.out.println(gl);

		fbo1.init(gl, FBO_SIZE, FBO_SIZE, 0);
		fbo1.attachTexture2D(gl, 0, true);
		fbo1.attachRenderbuffer(gl, Attachment.Type.DEPTH, 32);
		fbo1.unbind(gl);
		cubeInner.init(drawable);

		cubeOuter.init(drawable);
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		cubeOuter.reshape(drawable, x, y, width, height);
	}

	float xRot = 0f;
	float yRot = 0f;

	public void rotate(float xRot, float yRot) {
		this.xRot = xRot;
		this.yRot = yRot;
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
		GL2ES1 gl = drawable.getGL().getGL2ES1();
		fbo1.destroy(gl);
		fbo1 = null;
		cubeInner.dispose(drawable);
		cubeInner = null;
		cubeOuter.dispose(drawable);
		cubeOuter = null;
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		GL2ES1 gl = drawable.getGL().getGL2ES1();

		fbo1.bind(gl);
		cubeInner.reshape(drawable, 0, 0, FBO_SIZE, FBO_SIZE);
		cubeInner.display(drawable);
		gl.glFinish();
		fbo1.unbind(gl);

		gl.glEnable(GL.GL_TEXTURE_2D);
		fbo1.use(gl, (TextureAttachment) fbo1.getColorbuffer(0));
		cubeOuter.reshape(drawable, 0, 0, drawable.getSurfaceWidth(), drawable.getSurfaceHeight());
		cubeOuter.display(drawable);
		fbo1.unuse(gl);
		gl.glDisable(GL.GL_TEXTURE_2D);

		// JAUFBObject tex = fbo1;
		// JAU FBObject rend = fbo2;

		/*
		 * JAU int MAX_ITER = 1;
		 * 
		 * for (int i = 0; i < MAX_ITER; i++) { rend.bind(gl); gl.glEnable
		 * (gl.GL_TEXTURE_2D); gl.glBindTexture(gl.GL_TEXTURE_2D, tex.getTextureName());
		 * // to use it .. cubeMiddle.reshape(gl, 0, 0, FBO_SIZE, FBO_SIZE);
		 * cubeMiddle.display(gl, xRot, yRot); gl.glBindTexture(gl.GL_TEXTURE_2D, 0);
		 * gl.glDisable (gl.GL_TEXTURE_2D); rend.unbind(gl); FBObject tmp = tex; tex =
		 * rend; rend = tmp; }
		 * 
		 * // System.out.println("display .. p6"); gl.glClear(gl.GL_COLOR_BUFFER_BIT |
		 * gl.GL_DEPTH_BUFFER_BIT); gl.glClearColor(0, 0, 0, 1);
		 * 
		 * gl.glEnable (gl.GL_TEXTURE_2D); gl.glBindTexture(gl.GL_TEXTURE_2D,
		 * tex.getTextureName()); // to use it .. cubeOuter.display(gl, xRot, yRot); //
		 * System.out.println("display .. p7"); gl.glBindTexture(gl.GL_TEXTURE_2D, 0);
		 * gl.glDisable (gl.GL_TEXTURE_2D);
		 */
	}

	public void displayChanged(com.jogamp.opengl.GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
	}

	float time = 0.0f;
	Cube cubeInner = null;
	// JAU Cube cubeMiddle=null;
	Cube cubeOuter = null;
	FBObject fbo1;
	// JAU FBObject fbo2;
}
