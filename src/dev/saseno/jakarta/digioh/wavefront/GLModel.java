package dev.saseno.jakarta.digioh.wavefront;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

public abstract class GLModel {

	private List<Coords> vertices = new ArrayList<>();	
	private List<Coords> normals = new ArrayList<>();
	private List<Coords> texCoords = new ArrayList<>();
	
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
				
			} else {
		        
		        gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL2.GL_AMBIENT, mat_ambient_color, 0);
		        gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL2.GL_DIFFUSE, mat_diffuse, 0);
		        gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL2.GL_SPECULAR, no_mat, 0);
		        gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL2.GL_EMISSION, no_mat, 0);
		        
		        gl.glMaterialf(GL.GL_FRONT_AND_BACK, GL2.GL_SHININESS, 2f);

		        gl.glPushAttrib(GL2.GL_LIGHTING_BIT);
		        gl.glDisable(GL2.GL_TEXTURE_2D);
		        
//		        gl.glLightModeli(GL2.GL_LIGHT_MODEL_TWO_SIDE, 1);
		        
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
}
