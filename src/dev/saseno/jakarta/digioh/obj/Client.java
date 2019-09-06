package dev.saseno.jakarta.digioh.obj;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;

import dev.saseno.jakarta.digioh.wavefront.Face;
import dev.saseno.jakarta.digioh.wavefront.GLModel;

public class Client extends GLModel {
	
	private static final String objectName = "/obj/samsung.obj";
	private static final String textureName = null;
	
	private float fSize = 50f;
		
	protected float[] mat_ambient_color = { 0.1f, 0.1f, 0.8f, 1.0f };
	protected float[] mat_diffuse 		= { 0.0f, 0.0f, 0.7f, 1.0f };
	protected float[] mat_specular 		= { 0.0f, 0.0f, 0.7f, 1.0f };
	protected float[] mat_emission 		= { 0.0f, 0.0f, 0.0f, 1.0f };

	protected float shininess = 5.0f;
	
	public Client() {
		super(objectName, textureName);
	}

	@Override
	public void draw(GL i_gl, double rotate) {
		
		//i_gl.getGL2().getGL2().glRotated(90, 1, 0, 0);

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
		        
			gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL2.GL_AMBIENT, mat_ambient_color, 0);
			gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL2.GL_DIFFUSE, mat_diffuse, 0);
			gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL2.GL_SPECULAR, mat_specular, 0);
			gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL2.GL_EMISSION, mat_emission, 0);

			gl.glMaterialf(GL.GL_FRONT_AND_BACK, GL2.GL_SHININESS, shininess);

			gl.glPushAttrib(GL2.GL_LIGHTING_BIT);
			gl.glDisable(GL2.GL_TEXTURE_2D);

			gl.glLightModeli(GL2.GL_LIGHT_MODEL_TWO_SIDE, 1);

			gl.glEnable(GL2.GL_LIGHT0);
			gl.glEnable(GL2.GL_LIGHTING);
		        
		} catch (Exception e) {
			//e.printStackTrace();
			System.err.println("--> initTexture: " + e.getMessage());
		}
	}
	
}
