package dev.saseno.jakarta.digioh.obj;

import java.io.InputStream;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

import de.javagl.obj.FloatTuple;
import de.javagl.obj.Mtl;
import de.javagl.obj.MtlReader;
import de.javagl.obj.Obj;
import de.javagl.obj.ObjData;
import de.javagl.obj.ObjReader;
import de.javagl.obj.ObjSplitting;
import de.javagl.obj.ObjUtils;
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
			textures.add(TextureIO.newTexture(getClass().getResourceAsStream("/obj/house/ground_shadow.jpg"), true, "Ground_Shadow"));
			textures.add(TextureIO.newTexture(getClass().getResourceAsStream("/obj/house/MillCat_color.jpg"), true, "Mill"));
			
			for (String textureName : texturesName) {
				System.err.println("--> texture: " + textureName);
			}
			
		} catch (Exception e) {
			System.err.println("--> init Texture: " + e.getMessage());
			e.printStackTrace();
		}
		
		try {

			// Read an OBJ file
			InputStream objInputStream = getClass().getResourceAsStream("/obj/house/Mill.obj");
			Obj originalObj = ObjReader.read(objInputStream);

			// Convert the OBJ into a "renderable" OBJ.
			// (See ObjUtils#convertToRenderable for details)
			Obj obj = ObjUtils.convertToRenderable(originalObj);

			// The OBJ may refer to multiple MTL files using the "mtllib"
			// directive. Each MTL file may contain multiple materials.
			// Here, all materials (in form of Mtl objects) are collected.
			List<Mtl> allMtls = new ArrayList<Mtl>();
			
			for (String mtlFileName : obj.getMtlFileNames()) {
				
				InputStream mtlInputStream = null;
				System.err.println("--> mtlFileName: " + mtlFileName);
				
				if (mtlFileName.equals("Ground")) {
					mtlInputStream = getClass().getResourceAsStream("/obj/house/Ground_color.jpg");
					
				} else if (mtlFileName.equals("Ground_Shadow")) {
					mtlInputStream = getClass().getResourceAsStream("/obj/house/ground_shadow.jpg");
					
				} else if (mtlFileName.equals("Mill")) {
					mtlInputStream = getClass().getResourceAsStream("/obj/house/MillCat_color.jpg");
					
				} else {
					System.err.println("--> mtlFileName: " + mtlFileName);
				}
				
				if (mtlInputStream != null) {
					List<Mtl> mtls = MtlReader.read(mtlInputStream);
					allMtls.addAll(mtls);
				}
			}

			// Split the OBJ into multiple parts. Each key of the resulting
			// map will be the name of one material. Each value will be
			// an OBJ that contains the OBJ data that has to be rendered
			// with this material.
			Map<String, Obj> materialGroups = ObjSplitting.splitByMaterialGroups(obj);

			for (Entry<String, Obj> entry : materialGroups.entrySet()) {
				String materialName = entry.getKey();
				Obj materialGroup = entry.getValue();

				// System.out.println("Material name : " + materialName);
				// System.out.println("Material group : " + materialGroup);

				// Find the MTL that defines the material with the current name
				Mtl mtl = findMtlForName(allMtls, materialName);

				// Render the current material group with this material:
				if (mtl != null) {
					sendToRenderer(mtl, materialGroup);
				}
			}
		} catch (Exception e) {
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
	
	@SuppressWarnings("unused")
	private static void sendToRenderer(Mtl mtl, Obj obj) {
		// Extract the relevant material properties. These properties can
		// be used to set up the renderer. For example, they may be passed
		// as uniform variables to a shader
		FloatTuple diffuseColor = mtl.getKd();
		FloatTuple specularColor = mtl.getKs();
		// ...

		// Extract the geometry data. This data can be used to create
		// the vertex buffer objects and vertex array objects for OpenGL
		IntBuffer indices = ObjData.getFaceVertexIndices(obj);
		FloatBuffer vertices = ObjData.getVertices(obj);
		FloatBuffer texCoords = ObjData.getTexCoords(obj, 2);
		FloatBuffer normals = ObjData.getNormals(obj);
		// ...

		// Print some information about the object that would be rendered
		System.out.println("Rendering an object with " + (indices.capacity() / 3) + " triangles with " + mtl.getName()
				+ ", having diffuse color " + diffuseColor);

	}
	
	private static Mtl findMtlForName(Iterable<? extends Mtl> mtls, String name) {
		for (Mtl mtl : mtls) {
			if (mtl.getName().equals(name)) {
				return mtl;
			}
		}
		return null;
	}
}
