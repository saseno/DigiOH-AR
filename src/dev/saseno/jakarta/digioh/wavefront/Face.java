package dev.saseno.jakarta.digioh.wavefront;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;

public class Face {
	
    private List<Coords> vertices = new ArrayList<>();
    private List<Coords> normals = new ArrayList<>();
    private List<Coords> texCoords = new ArrayList<>();

    private boolean isFlat = false;

    public Face() {
    }

    public void addVertex(Coords c) {
        vertices.add(c);
    }

    public void addNormal(Coords c) {
    	//System.err.println("--> addNormal...");
        isFlat = false;
        normals.add(c);
    }

    public void addGlobalNormal(Coords c) {
        isFlat = true;
        normals.add(c);
    }

    public void addTexCoord(Coords c) {
        texCoords.add(c);
    }

    public void draw(GL i_gl, float fSize) {    	

		GL2 gl = i_gl.getGL2();
        int size = vertices.size();
                
        boolean pushNormals = normals.size() == size;
        boolean pushTexCoords = texCoords.size() == size;
        Coords v;

        if (size < 3) {
        	//System.err.println("--> out..");
            return;
        }
               
		gl.glBegin(GL2.GL_POLYGON);
		
		for (int i = 0; i < size; i++) {
			
			if (isFlat) {
				if (i == 0) {
					v = normals.get(0);
					gl.glNormal3d(v.getX() * fSize, v.getY() * fSize, v.getZ() * fSize);
				}
			} else if (pushNormals) {
				v = normals.get(i);
				gl.glNormal3d(v.getX() * fSize, v.getY() * fSize, v.getZ() * fSize);
			}

			if (pushTexCoords) {
				v = texCoords.get(i);
				gl.glTexCoord2d(v.getU(), v.getV());
				
			} 

			v = vertices.get(i);
			gl.glVertex3d(v.getX() * fSize, v.getY() * fSize, v.getZ() * fSize);
		}

        gl.glEnd();
    }
    
    @Override
    public String toString() {
        return String.format(
                "[V: %s, N:%s, T:%s]",
                Arrays.toString(vertices.toArray()),
                Arrays.toString(normals.toArray()),
                Arrays.toString(texCoords.toArray())
        );
    }
}
