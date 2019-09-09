package dev.saseno.jakarta.digioh.obj;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;

import dev.saseno.jakarta.digioh.wavefront.Face;
import dev.saseno.jakarta.digioh.wavefront.GLModel;

public class Client extends GLModel {
	
	private static final String objectName = "/obj/samsung.obj";
	private static final String textureName = "/obj/samsung.png";
	
	private float fSize = 100f;
		
	public Client() {
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

		} catch (Exception e) {
			// e.printStackTrace();
			System.err.println("--> initTexture: " + e.getMessage());
		}

	}
	
}
