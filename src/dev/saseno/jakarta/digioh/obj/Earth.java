package dev.saseno.jakarta.digioh.obj;

import com.jogamp.opengl.GL;

import dev.saseno.jakarta.digioh.wavefront.Face;
import dev.saseno.jakarta.digioh.wavefront.GLModel;

public class Earth extends GLModel {
	
	private static final String objectName = "/obj/earth/Earth_2K.obj";
	private static final String textureName = "/obj/earth/Diffuse_2K.png";
	
	private float fSize = 50f;
	
	public Earth() {
		super(objectName, textureName);
	}

	@Override
	public void draw(GL i_gl, double rotate) {
		
		if (rotate > 0) {
			i_gl.getGL2().getGL2().glRotated(rotate, 0, 1, 0);
		}
		
		initTexture(i_gl.getGL2());
		
		for (Face face : faces) {
			face.draw(i_gl, fSize);
		}
		
		disabledTexture(i_gl.getGL2());		
	}
	
}
