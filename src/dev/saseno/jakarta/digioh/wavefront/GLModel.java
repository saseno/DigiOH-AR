package dev.saseno.jakarta.digioh.wavefront;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

import de.javagl.obj.DefaultFloatTuple;
import de.javagl.obj.FloatTuple;
import de.javagl.obj.Mtl;
import de.javagl.obj.MtlReader;
import de.javagl.obj.Obj;
import de.javagl.obj.ObjData;
import de.javagl.obj.ObjReader;
import de.javagl.obj.ObjSplitting;
import de.javagl.obj.ObjUtils;

public abstract class GLModel {

	private List<Coords> vertices = new ArrayList<>();	
	private List<Coords> normals = new ArrayList<>();
	private List<Coords> texCoords = new ArrayList<>();
	
	protected List<String> texturesName = new ArrayList<>();
	protected List<Face> faces = new ArrayList<>();	
	private Texture texture = null;

	protected float[] defaultColor = { .8f, 0.0f, 0.0f, 1.0f };
	
	protected float[] no_mat 			= { 0.0f, 0.0f, 0.0f, 1.0f };
	protected float[] mat_ambient 		= { 0.7f, 0.7f, 0.7f, 1.0f };
	protected float[] mat_ambient_color = { 0.8f, 0.8f, 0.2f, 1.0f };
	protected float[] mat_diffuse 		= { 0.1f, 0.5f, 0.8f, 1.0f };
	protected float[] mat_specular 		= { 1.0f, 1.0f, 1.0f, 1.0f };
	
	protected float no_shininess 		= 0.0f;
	protected float low_shininess 		= 5.0f;
	protected float high_shininess 		= 100.0f;
	
	protected float[] mat_emission 		= { 0.3f, 0.2f, 0.2f, 0.0f };
	
	protected Obj renderableObj = null;
	protected Map<String, Obj> materialGroups = null;
	protected List<Mtl> allMtls = new ArrayList<>();
	
	public GLModel(String objectName, String textureName) {
		try {

			InputStream inputStream = getClass().getResourceAsStream(objectName);
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			
			List<String[]> lines = new ArrayList<>();
			
			while (reader.ready()) {
				String line = reader.readLine();
				String[] tokens = line.trim().split("\\s+");
				lines.add(tokens);

				switch (tokens[0]) {
				case "v":
					vertices.add(parseVertex(tokens));
					break;

				case "vn":
					normals.add(parseNormal(tokens));
					break;

				case "vt":
					texCoords.add(parseTexCoord(tokens));
					break;

				case "usemtl":
					//System.err.println("--> material: " + tokens[1]);
					if (tokens.length > 1) {
						texturesName.add(tokens[1]);
					}
					break;

				}
			}
			
			// scan the faces
			for (String[] tokens : lines) {
				if (tokens[0].equals("f")) {
					faces.add(parseFace(tokens));
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			if (textureName != null) {
				texture = TextureIO.newTexture(getClass().getResourceAsStream(textureName), true, "default");
			}
		} catch (Exception e) {
			System.err.println("--> init Texture: " + e.getMessage());
			e.printStackTrace();
		}
		
		readObj(objectName);
	}

	public abstract void draw(com.jogamp.opengl.GL i_gl, double rotate);
	
	protected void initTexture(GL2 gl) {
		try {
			if (texture != null) {
				
				//gl.glUseProgram(arg0);

		        gl.glEnable(GL2.GL_TEXTURE_2D);
		        gl.glEnable(GL2.GL_LIGHT0);
		        gl.glDisable(GL2.GL_LIGHTING); 
		        
				texture.enable(gl);
				texture.bind(gl);
								
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
	
	protected boolean hasTexture() {
		return texture == null;
	}

	protected void disabledTexture(GL2 gl) {

		try {
			if (texture != null) {
				texture.disable(gl);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		int faceCount = faces.size();

		for (int i = 0; i < faceCount - 1; i++) {
			builder.append(faces.get(i)).append("\n");
		}

		if (faceCount > 0) {
			builder.append(faces.get(faceCount - 1));
		}

		return builder.toString();
	}

	private Coords parseVertex(String[] tokens) {
		double x = Double.parseDouble(tokens[1]);
		double y = Double.parseDouble(tokens[2]);
		double z = Double.parseDouble(tokens[3]);

		return new Coords(x, y, z);
	}

	private Coords parseNormal(String[] tokens) {
		double x = Double.parseDouble(tokens[1]);
		double y = Double.parseDouble(tokens[2]);
		double z = Double.parseDouble(tokens[3]);

		return new Coords(x, y, z).normalize();
	}

	private Coords parseTexCoord(String[] tokens) {
		double u = Double.parseDouble(tokens[1]);
		double v = Double.parseDouble(tokens[2]);

		return new Coords(u, v);
	}

	private Face parseFace(String[] tokens) {
		Face face = new Face();
		boolean hasNormal = false;

		for (int i = 1; i < tokens.length; i++) {
			String[] indices = tokens[i].split("/");
			int vIndex, tIndex, nIndex;

			switch (indices.length) {
			// vertices only
			case 1:
				vIndex = Integer.parseInt(indices[0]) - 1;
				face.addVertex(vertices.get(vIndex));
				break;

			// vertices / texture coordinates
			case 2:
				vIndex = Integer.parseInt(indices[0]) - 1;
				tIndex = Integer.parseInt(indices[1]) - 1;
				face.addVertex(vertices.get(vIndex));
				face.addTexCoord(texCoords.get(tIndex));
				break;

			// vertices / normals
			case 3:
				vIndex = Integer.parseInt(indices[0]) - 1;
				nIndex = Integer.parseInt(indices[2]) - 1;
				face.addVertex(vertices.get(vIndex));
				face.addNormal(normals.get(nIndex));

				hasNormal = true;

				// and texture coordinates
				if (!indices[1].isEmpty()) {
					tIndex = Integer.parseInt(indices[1]) - 1;
					face.addTexCoord(texCoords.get(tIndex));
				}

				break;
			}
		}

		// flat face -> generate a 'global' normal
		if (!hasNormal) {
			face.addGlobalNormal(faceNormal(vertices.get(0), vertices.get(1), vertices.get(2)));
		}

		return face;
	}

	private Coords faceNormal(Coords v1, Coords v2, Coords v3) {
		// a = v2 - v1
		Coords a = Coords.add(v2, Coords.scale(v1, -1));

		// b = v3 - v1
		Coords b = Coords.add(v3, Coords.scale(v1, -1));

		// result = (v2 - v1) X (v3 - v1)
		return Coords.cross(a, b).normalize();
	}
	
	protected void readObj(String objectName) {

		try {
			
			// Read an OBJ file
			InputStream objInputStream = getClass().getResourceAsStream(objectName);
			Obj originalObj = ObjReader.read(objInputStream);

			// Convert the OBJ into a "renderable" OBJ.
			// (See ObjUtils#convertToRenderable for details)
			renderableObj = ObjUtils.convertToRenderable(originalObj);

			// The OBJ may refer to multiple MTL files using the "mtllib"
			// directive. Each MTL file may contain multiple materials.
			// Here, all materials (in form of Mtl objects) are collected.
			allMtls = new ArrayList<Mtl>();
			
			for (String mtlFileName : renderableObj.getMtlFileNames()) {
				
				URL objUrl = getClass().getResource(objectName);
				Path objPath = Paths.get(objUrl.toURI());				
				File file = Paths.get("" + objPath.getParent(), mtlFileName).toFile();
				
				InputStream mtlInputStream = null;
				//System.err.println("--> mtlFileName: " + file.getPath());
				
				if (file.exists()) {
					mtlInputStream = new FileInputStream(file);
				}
				
				if (mtlInputStream != null) {
					List<Mtl> mtls = MtlReader.read(mtlInputStream);
					allMtls.addAll(mtls);
				}
			}

			System.err.println("---------------------");
			System.err.println(objectName);
			System.err.println("---------------------");

			// Split the OBJ into multiple parts. Each key of the resulting
			// map will be the name of one material. Each value will be
			// an OBJ that contains the OBJ data that has to be rendered
			// with this material.
			materialGroups = ObjSplitting.splitByMaterialGroups(renderableObj);

			for (Entry<String, Obj> entry : materialGroups.entrySet()) {
				String materialName = entry.getKey();
				Obj materialGroup = entry.getValue();

				// System.out.println("Material name : " + materialName);
				// System.out.println("Material group : " + materialGroup);

				// Find the MTL that defines the material with the current name
				Mtl mtl = findMtlForName(allMtls, materialName);

				// Render the current material group with this material:
				sendToRenderer(mtl, materialGroup, materialName);				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unused")
	protected void sendToRenderer(Mtl mtl, Obj obj, String materialName) {
		
		// Extract the relevant material properties. These properties can
		// be used to set up the renderer. For example, they may be passed
		// as uniform variables to a shader
		FloatTuple diffuseColor = null;
		FloatTuple specularColor = null;
		if (mtl != null) {
			diffuseColor = mtl.getKd();
			specularColor = mtl.getKs();
		}
		// ...

		// Extract the geometry data. This data can be used to create
		// the vertex buffer objects and vertex array objects for OpenGL
		IntBuffer indices = ObjData.getFaceVertexIndices(obj);
		FloatBuffer vertices = ObjData.getVertices(obj);
		FloatBuffer texCoords = ObjData.getTexCoords(obj, 2);
		FloatBuffer normals = ObjData.getNormals(obj);
		// ...
		
		String matName = materialName;
		if(mtl != null) {
			matName = mtl.getName();
		}

		// Print some information about the object that would be rendered
		System.err.println("Object with " + (indices.capacity() / 3) 
				+ " triangles " + matName + ", diffuse color "
				+ diffuseColor + ", specular color " + specularColor);

	}
	
	protected void drawVer02(com.jogamp.opengl.GL i_gl) {
		
		GL2 gl = i_gl.getGL2();	        
		materialGroups = ObjSplitting.splitByMaterialGroups(renderableObj);		
		
		for (Entry<String, Obj> entry : materialGroups.entrySet()) {

			String materialName = entry.getKey();
			Obj materialGroup = entry.getValue();
			Mtl mtl = findMtlForName(allMtls, materialName);
			
			FloatTuple diffuseColor = null;
			FloatTuple specularColor = null;
			
			if (mtl != null) {
				diffuseColor = mtl.getKd();
				specularColor = mtl.getKs();

				gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL2.GL_SPECULAR, DefaultFloatTuple.getValues(specularColor), 0);
				gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL2.GL_DIFFUSE, DefaultFloatTuple.getValues(diffuseColor), 0);
			}

			IntBuffer indices = ObjData.getFaceVertexIndices(materialGroup);
			FloatBuffer vertices = ObjData.getVertices(materialGroup);
			FloatBuffer texCoords = ObjData.getTexCoords(materialGroup, 2);
			FloatBuffer normals = ObjData.getNormals(materialGroup);

			gl.glBegin(GL2.GL_QUADS);
			
			gl.glIndexiv(indices);
			gl.glNormal3fv(normals);
			gl.glTexCoord2fv(texCoords);
			gl.glVertex3fv(vertices);
			
			gl.glEnd();

			/*
			String matName = materialName;
			if(mtl != null) {
				matName = mtl.getName();
			}

			System.err.println("Object with " + (indices.capacity() / 3) 
					+ " triangles " + matName + ", diffuse color "
					+ diffuseColor + ", specular color " + specularColor);
			*/			
		}		

	}
	
	protected Mtl findMtlForName(Iterable<? extends Mtl> mtls, String name) {
		for (Mtl mtl : mtls) {
			if (mtl.getName().equals(name)) {
				return mtl;
			}
		}
		return null;
	}
}
