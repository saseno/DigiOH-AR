package dev.saseno.jakarta.digioh.obj;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;

import dev.saseno.jakarta.digioh.wavefront.Face;
import dev.saseno.jakarta.digioh.wavefront.GLModel;

public class Love extends GLModel {
	
	private static final String objectName = "/obj/love/Love.obj";
	private static final String textureName = "/obj/love/Love.png";
	
	private float fSize = 4f;
	
	protected float[] mat_ambient 		= { 0.5f, 0.2f, 0.2f, 1.0f };
	protected float[] mat_ambient_color = { 0.3f, 0.0f, 0.0f, 1.0f };
	protected float[] mat_diffuse 		= { 0.5f, 0.2f, 0.2f, 1.0f };
	protected float[] mat_specular 		= { 0.1f, 0.1f, 0.1f, 1.0f };

	protected float shininess = 2.0f;
	
	public Love() {
		super(objectName, textureName);
	}

	@Override
	public void draw(GL i_gl, double rotate) {
		
		i_gl.getGL2().getGL2().glRotated(90, 0, 0, 1);

		if (rotate > 0) {
			i_gl.getGL2().getGL2().glRotated(rotate, 0, 1, 0);
		}
		
		initTexture(i_gl.getGL2());
		
		for (Face face : faces) {
			face.draw(i_gl, fSize);
		}
		
		disabledTexture(i_gl.getGL2());		
	}

	@Override
	protected void initTexture(GL2 gl) {
		try {
			
			super.initTexture(gl);
			
			gl.glEnable(GL2.GL_TEXTURE_2D);
			gl.glEnable(GL2.GL_LIGHT0);
			gl.glEnable(GL2.GL_LIGHTING);
			
			gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL2.GL_AMBIENT, mat_ambient, 0);
			gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL2.GL_DIFFUSE, mat_ambient_color, 0);
			gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL2.GL_SPECULAR, mat_diffuse, 0);
			gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL2.GL_EMISSION, mat_specular, 0);

			gl.glMaterialf(GL.GL_FRONT_AND_BACK, GL2.GL_SHININESS, 0.5f);

			gl.glEnable(GL2.GL_LIGHT0);
			gl.glEnable(GL2.GL_LIGHTING);

		} catch (Exception e) {
			// e.printStackTrace();
			System.err.println("--> initTexture: " + e.getMessage());
		}

	}
	
}
