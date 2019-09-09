package dev.saseno.jakarta.digioh.obj;

import com.jogamp.opengl.GL;

import dev.saseno.jakarta.digioh.wavefront.Face;
import dev.saseno.jakarta.digioh.wavefront.GLModel;

public class Plane extends GLModel {
	
	private static final String objectName = "/obj/pesawat/TAL16OBJ.obj";
	private static final String textureName = "/obj/pesawat/TALTS.jpg";
	
	private float fSize = 80f;
	
	public Plane() {
		super(objectName, textureName);
	}

	@Override
	public void draw(GL i_gl, double rotate) {
		
		if (rotate > 0) {
			i_gl.getGL2().getGL2().glRotated(rotate, 0, 0, 1);
		}
		
		initTexture(i_gl.getGL2());
		
		for (Face face : faces) {
			face.draw(i_gl, fSize);
		}
		
		disabledTexture(i_gl.getGL2());		
	}
	
}
