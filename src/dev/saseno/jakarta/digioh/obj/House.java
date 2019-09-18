package dev.saseno.jakarta.digioh.obj;

import java.util.ArrayList;
import java.util.List;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

import dev.saseno.jakarta.digioh.wavefront.Face;
import dev.saseno.jakarta.digioh.wavefront.GLModel;

public class House extends GLModel {
	
	private static final String objectName = "/obj/house/Mill.obj";
	private static final String textureName = "/obj/house/MillCat_color.jpg";
	
	private List<Texture> textures = new ArrayList<>();
	
	private float fSize = 0.3f;
	
	public House() {
		super(objectName, textureName);

		try {
			textures.add(TextureIO.newTexture(getClass().getResourceAsStream("/obj/house/Ground_color.jpg"), true, "Ground"));
//			textures.add(TextureIO.newTexture(getClass().getResourceAsStream("/obj/house/ground_shadow.jpg"), true, "Ground_Shadow"));
//			textures.add(TextureIO.newTexture(getClass().getResourceAsStream("/obj/house/MillCat_color.jpg"), true, "Mill"));
//			
//			for (String textureName : texturesName) {
//				System.err.println("--> texture: " + textureName);
//			}
			
		} catch (Exception e) {
			System.err.println("--> init Texture: " + e.getMessage());
			e.printStackTrace();
		}
				
	}

	@Override
	public void draw(GL i_gl, double rotate) {

		i_gl.getGL2().getGL2().glRotated(90, 0, 1, 1);
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
		super.initTexture(gl);
		try {
			if (textures != null && textures.size() > 0) {
				
				//gl.glUseProgram(arg0);

		        gl.glEnable(GL2.GL_TEXTURE_2D);
		        gl.glEnable(GL2.GL_LIGHT0);
		        gl.glDisable(GL2.GL_LIGHTING); 
		        
		        int i = 0;
		        
				for (Texture texture : textures) {
					gl.glActiveTexture(GL2.GL_TEXTURE0 + i);
					//texture.enable(gl);
					texture.bind(gl);
					
					i++;
				}
				
				gl.glActiveTexture(GL2.GL_TEXTURE0);
				
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
				
			} else {
		        
		        gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL2.GL_AMBIENT, mat_ambient_color, 0);
		        gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL2.GL_DIFFUSE, mat_diffuse, 0);
		        gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL2.GL_SPECULAR, no_mat, 0);
		        gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL2.GL_EMISSION, no_mat, 0);
		        
		        gl.glMaterialf(GL.GL_FRONT_AND_BACK, GL2.GL_SHININESS, 2f);

		        gl.glPushAttrib(GL2.GL_LIGHTING_BIT);
		        gl.glDisable(GL2.GL_TEXTURE_2D);
		        		        
		        gl.glEnable(GL2.GL_LIGHT0);
		        gl.glEnable(GL2.GL_LIGHTING); 
			}
		} catch (Exception e) {
			//e.printStackTrace();
			System.err.println("--> initTexture: " + e.getMessage());
		}
	}
}
