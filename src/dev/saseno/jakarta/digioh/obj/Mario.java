package dev.saseno.jakarta.digioh.obj;

import com.jogamp.opengl.GL;

import dev.saseno.jakarta.digioh.wavefront.Face;
import dev.saseno.jakarta.digioh.wavefront.GLModel;

public class Mario extends GLModel {
	
	private static final String objectName = "/obj/mario/Obj/Mario.obj";
	private static final String textureName = "/obj/mario/Textures/mario_main.png";
	
	private float fSize = 240f;
	
	public Mario() {
		super(objectName, textureName);
	}

	@Override
	public void draw(GL i_gl, double rotate) {

		i_gl.getGL2().getGL2().glRotated(-90, 1, 0, 0);
		
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
