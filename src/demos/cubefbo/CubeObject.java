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

package demos.cubefbo;

import com.jogamp.common.nio.Buffers;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.Map;
import java.util.Map.Entry;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2ES1;

import de.javagl.obj.FloatTuple;
import de.javagl.obj.Mtl;
import de.javagl.obj.Obj;
import de.javagl.obj.ObjData;
import de.javagl.obj.ObjSplitting;
import dev.saseno.jakarta.digioh.obj.Love;

import com.jogamp.opengl.GL2;

public class CubeObject {
	Love modelLove;
	
	public CubeObject(boolean useTexCoords) {
//		// Initialize data Buffers
//		this.cubeVertices = Buffers.newDirectShortBuffer(s_cubeVertices.length);
//		cubeVertices.put(s_cubeVertices);
//		cubeVertices.rewind();
//
//		this.cubeColors = Buffers.newDirectByteBuffer(s_cubeColors.length);
//		cubeColors.put(s_cubeColors);
//		cubeColors.rewind();
//
//		this.cubeNormals = Buffers.newDirectByteBuffer(s_cubeNormals.length);
//		cubeNormals.put(s_cubeNormals);
//		cubeNormals.rewind();
//
//		this.cubeIndices = Buffers.newDirectByteBuffer(s_cubeIndices.length);
//		cubeIndices.put(s_cubeIndices);
//		cubeIndices.rewind();
//
//		if (useTexCoords) {
//			this.cubeTexCoords = Buffers.newDirectShortBuffer(s_cubeTexCoords.length);
//			cubeTexCoords.put(s_cubeTexCoords);
//			cubeTexCoords.rewind();
//		}
		
	}
	
	public void initObject() {
		modelLove = new Love();
	}

	private void perspective(GL2 gl, float fovy, float aspect, float zNear, float zFar) {
		float xmin;
		float xmax;
		float ymin;
		float ymax;

		ymax = zNear * (float) Math.tan((fovy * Math.PI) / 360.0);
		ymin = -ymax;
		xmin = ymin * aspect;
		xmax = ymax * aspect;

		gl.glFrustum(xmin, xmax, ymin, ymax, zNear, zFar);
	}

	static float[] light_position = { -50.f, 50.f, 50.f, 0.f };
	static float[] light_ambient = { 0.125f, 0.125f, 0.125f, 1.f };
	static float[] light_diffuse = { 1.0f, 1.0f, 1.0f, 1.f };
	static float[] material_spec = { 1.0f, 1.0f, 1.0f, 0.f };
	static float[] zero_vec4 = { 0.0f, 0.0f, 0.0f, 0.f };

	public void dispose(GL2 gl) {
		
		gl.glDisableClientState(GL2ES1.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL2ES1.GL_NORMAL_ARRAY);
		gl.glDisableClientState(GL2ES1.GL_COLOR_ARRAY);
		gl.glDisableClientState(GL2ES1.GL_TEXTURE_COORD_ARRAY);
		
//		this.cubeVertices.clear();
//		this.cubeVertices = null;
//		
//		this.cubeColors.clear();
//		this.cubeColors = null;
//		
//		this.cubeNormals.clear();
//		this.cubeNormals = null;
//		
//		this.cubeIndices.clear();
//		this.cubeIndices = null;
//		
//		if (null != this.cubeTexCoords) {
//			this.cubeTexCoords.clear();
//			this.cubeTexCoords = null;
//		}
	}

	public void reshape(GL2 gl, int x, int y, int width, int height) {
		float aspect = (height != 0) ? ((float) width / (float) height) : 1.0f;

		gl.glViewport(0, 0, width, height);
		gl.glScissor(0, 0, width, height);

		gl.glMatrixMode(GL2ES1.GL_MODELVIEW);
		gl.glLoadIdentity();

		gl.glLightfv(GL2ES1.GL_LIGHT0, GL2ES1.GL_POSITION, light_position, 0);
		gl.glLightfv(GL2ES1.GL_LIGHT0, GL2ES1.GL_AMBIENT, light_ambient, 0);
		gl.glLightfv(GL2ES1.GL_LIGHT0, GL2ES1.GL_DIFFUSE, light_diffuse, 0);
		gl.glLightfv(GL2ES1.GL_LIGHT0, GL2ES1.GL_SPECULAR, zero_vec4, 0);
		gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL2ES1.GL_SPECULAR, material_spec, 0);

		gl.glEnable(GL2ES1.GL_NORMALIZE);
		gl.glEnable(GL2ES1.GL_LIGHTING);
		gl.glEnable(GL2ES1.GL_LIGHT0);
		gl.glEnable(GL2ES1.GL_COLOR_MATERIAL);
		gl.glEnable(GL.GL_CULL_FACE);

		gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_FASTEST);

		gl.glShadeModel(GL2ES1.GL_SMOOTH);
		gl.glDisable(GL.GL_DITHER);

		gl.glClearColor(0.0f, 0.1f, 0.0f, 1.0f);

		gl.glEnableClientState(GL2ES1.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL2ES1.GL_NORMAL_ARRAY);
		gl.glEnableClientState(GL2ES1.GL_COLOR_ARRAY);
		
		//if (cubeTexCoords != null) {
			gl.glEnableClientState(GL2ES1.GL_TEXTURE_COORD_ARRAY);
		//} else {
		//	gl.glDisableClientState(GL2ES1.GL_TEXTURE_COORD_ARRAY);
		//}

		gl.glMatrixMode(GL2ES1.GL_PROJECTION);
		gl.glLoadIdentity();

		perspective(gl, 55.f, aspect, 0.1f, 100.f);
		gl.glCullFace(GL.GL_BACK);
	}

	public void display(GL2 gl, float xRot, float yRot) {
		
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

		gl.glMatrixMode(GL2ES1.GL_MODELVIEW);
		gl.glLoadIdentity();

		gl.glTranslatef(0.f, 0.f, -30.f);
		gl.glRotatef(yRot, 0, 1, 0);
		gl.glRotatef(xRot, 1, 0, 0);
		
		Map<String, Obj> materialGroups = ObjSplitting.splitByMaterialGroups(modelLove.getRenderableObj());

		for (Entry<String, Obj> entry : materialGroups.entrySet()) {
			String materialName = entry.getKey();
			Obj materialGroup = entry.getValue();

			Mtl mtl = modelLove.findMtlForName(modelLove.getAllMtls(), materialName);
			
			FloatTuple diffuseColor = null;
			FloatTuple specularColor = null;
			if (mtl != null) {
				diffuseColor = mtl.getKd();
				specularColor = mtl.getKs();
			}

			IntBuffer indices = ObjData.getFaceVertexIndices(materialGroup);
			FloatBuffer vertices = ObjData.getVertices(materialGroup);
			FloatBuffer texCoords = ObjData.getTexCoords(materialGroup, 2);
			FloatBuffer normals = ObjData.getNormals(materialGroup);
			
			String matName = materialName;
			if(mtl != null) {
				matName = mtl.getName();
			}
			
			gl.glScaled(20, 20, 20);
			gl.glVertexPointer(3, GL.GL_SHORT, 0, vertices);
			//gl.glColorPointer(4, GL.GL_UNSIGNED_BYTE, 0, cubeColors);
			gl.glNormalPointer(GL.GL_BYTE, 0, normals);

			//if (texCoords != null) {
			//	gl.glTexCoordPointer(2, GL.GL_SHORT, 0, texCoords);
			//	gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL.GL_REPLACE);
			//}

			gl.glDrawElements(GL.GL_TRIANGLES, 6 * 6, GL.GL_UNSIGNED_BYTE, indices);
		}
	}

	boolean initialized = false;

//	ShortBuffer cubeVertices;
//	ShortBuffer cubeTexCoords;
//
//	ByteBuffer cubeColors;
//	ByteBuffer cubeNormals;
//	ByteBuffer cubeIndices;
//
//	private static final short[] s_cubeVertices = { 
//			-10, 10, 10, 10, -10, 10, 10, 10, 10, -10, -10, 10,
//			-10, 10, -10, 10, -10, -10, 10, 10, -10, -10, -10, -10,
//			-10, -10, 10, 10, -10, -10, 10, -10, 10, -10, -10, -10,
//			-10, 10, 10, 10, 10, -10, 10, 10, 10, -10, 10, -10,
//			10, -10, 10, 10, 10, -10, 10, 10, 10, 10, -10, -10,
//			-10, -10, 10, -10, 10, -10, -10, 10, 10, -10, -10, -10 };
//
//	private static final short[] s_cubeTexCoords = { 
//			0, (short) 0xffff, (short) 0xffff, 0, (short) 0xffff,
//			(short) 0xffff, 0, 0,
//			0, (short) 0xffff, (short) 0xffff, 0, (short) 0xffff, (short) 0xffff, 0, 0,
//			0, (short) 0xffff, (short) 0xffff, 0, (short) 0xffff, (short) 0xffff, 0, 0,
//			0, (short) 0xffff, (short) 0xffff, 0, (short) 0xffff, (short) 0xffff, 0, 0,
//			0, (short) 0xffff, (short) 0xffff, 0, (short) 0xffff, (short) 0xffff, 0, 0,
//			0, (short) 0xffff, (short) 0xffff, 0, (short) 0xffff, (short) 0xffff, 0, 0, };
//
////	private static final byte[] s_cubeColors = { (byte) 0, (byte) 128, (byte) 0, (byte) 255, (byte) 0, (byte) 128,
////			(byte) 0, (byte) 255, (byte) 0, (byte) 128, (byte) 0, (byte) 255, (byte) 0, (byte) 128, (byte) 0,
////			(byte) 255,
////
////			(byte) 0, (byte) 128, (byte) 0, (byte) 255, (byte) 0, (byte) 128, (byte) 0, (byte) 255, (byte) 0,
////			(byte) 128, (byte) 0, (byte) 255, (byte) 0, (byte) 128, (byte) 0, (byte) 255,
////
////			(byte) 0, (byte) 128, (byte) 0, (byte) 255, (byte) 0, (byte) 128, (byte) 0, (byte) 255, (byte) 0,
////			(byte) 128, (byte) 0, (byte) 255, (byte) 0, (byte) 128, (byte) 0, (byte) 255,
////
////			(byte) 0, (byte) 128, (byte) 0, (byte) 255, (byte) 0, (byte) 128, (byte) 0, (byte) 255, (byte) 0,
////			(byte) 128, (byte) 0, (byte) 255, (byte) 0, (byte) 128, (byte) 0, (byte) 255,
////
////			(byte) 0, (byte) 128, (byte) 0, (byte) 255, (byte) 0, (byte) 128, (byte) 0, (byte) 255, (byte) 0,
////			(byte) 128, (byte) 0, (byte) 255, (byte) 0, (byte) 128, (byte) 0, (byte) 255,
////
////			(byte) 0, (byte) 128, (byte) 0, (byte) 255, (byte) 0, (byte) 128, (byte) 0, (byte) 255, (byte) 0,
////			(byte) 128, (byte) 0, (byte) 255, (byte) 0, (byte) 128, (byte) 0, (byte) 255 };
//
//	
//	private static final byte[] s_cubeColors = { (byte) 40, (byte) 80, (byte) 160, (byte) 255, (byte) 40, (byte) 80,
//			(byte) 160, (byte) 255, (byte) 40, (byte) 80, (byte) 160, (byte) 255, (byte) 40, (byte) 80, (byte) 160,
//			(byte) 255,
//
//			(byte) 40, (byte) 80, (byte) 160, (byte) 255, (byte) 40, (byte) 80, (byte) 160, (byte) 255, (byte) 40,
//			(byte) 80, (byte) 160, (byte) 255, (byte) 40, (byte) 80, (byte) 160, (byte) 255,
//
//			(byte) 128, (byte) 128, (byte) 128, (byte) 255, (byte) 128, (byte) 128, (byte) 128, (byte) 255, (byte) 128,
//			(byte) 128, (byte) 128, (byte) 255, (byte) 128, (byte) 128, (byte) 128, (byte) 255,
//
//			(byte) 128, (byte) 128, (byte) 128, (byte) 255, (byte) 128, (byte) 128, (byte) 128, (byte) 255, (byte) 128,
//			(byte) 128, (byte) 128, (byte) 255, (byte) 128, (byte) 128, (byte) 128, (byte) 255,
//
//			(byte) 255, (byte) 110, (byte) 10, (byte) 255, (byte) 255, (byte) 110, (byte) 10, (byte) 255, (byte) 255,
//			(byte) 110, (byte) 10, (byte) 255, (byte) 255, (byte) 110, (byte) 10, (byte) 255,
//
//			(byte) 255, (byte) 70, (byte) 60, (byte) 255, (byte) 255, (byte) 70, (byte) 60, (byte) 255, (byte) 255,
//			(byte) 70, (byte) 60, (byte) 255, (byte) 255, (byte) 70, (byte) 60, (byte) 255 };
//	 
//
//	private static final byte[] s_cubeIndices = { 
//			0, 3, 1, 2, 0, 1, /* front */
//			6, 5, 4, 5, 7, 4, /* back */
//			8, 11, 9, 10, 8, 9, /* top */
//			15, 12, 13, 12, 14, 13, /* bottom */
//			16, 19, 17, 18, 16, 17, /* right */
//			23, 20, 21, 20, 22, 21 /* left */
//	};
//	
//	private static final byte[] s_cubeNormals = { 
//			0, 0, 127, 0, 0, 127, 0, 0, 127, 0, 0, 127,
//			0, 0, -128, 0, 0, -128, 0, 0, -128, 0, 0, -128,
//			0, -128, 0, 0, -128, 0, 0, -128, 0, 0, -128, 0,
//			0, 127, 0, 0, 127, 0, 0, 127, 0, 0, 127, 0,
//			127, 0, 0, 127, 0, 0, 127, 0, 0, 127, 0, 0,
//			-128, 0, 0, -128, 0, 0, -128, 0, 0, -128, 0, 0 };
}
