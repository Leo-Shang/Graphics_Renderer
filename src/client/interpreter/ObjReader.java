package client.interpreter;

import java.util.ArrayList;
import java.util.List;

import geometry.Point3DH;
import geometry.Vertex3D;
import polygon.Polygon;
import polygon.WireframeRendererZBuff;
import windowing.drawable.Drawable;
import windowing.graphics.Color;
import client.interpreter.*;
import client.RendererTrio;

class ObjReader {
	private static final char COMMENT_CHAR = '#';
	private static final int NOT_SPECIFIED = -1;

	private class ObjVertex {
		private int vertex_index;

		private ObjVertex(int index) {
			this.vertex_index = index;
		}
		private int getVertex() {
			return this.vertex_index;
		}
	}
	private class ObjFace extends ArrayList<ObjVertex> {
		private static final long serialVersionUID = -4130668677651098160L;
	}
	private LineBasedReader reader;

	private List<Vertex3D> objVertices;
	private List<Vertex3D> transformedVertices;
	private List<Point3DH> objNormals;
	private List<ObjFace> objFaces;

	private Color defaultColor;

	ObjReader(String filename, Color defaultColor) {
		objVertices = new ArrayList<Vertex3D>();
		transformedVertices = new ArrayList<Vertex3D>();
		objNormals = new ArrayList<Point3DH>();
		objFaces = new ArrayList<ObjFace>();
		reader = new LineBasedReader(filename);
	}

	public void render(Color defaultColor, Drawable drawable) {
		for(int i = 0; i < objFaces.size(); i++) {
			ObjFace face = objFaces.get(i);
			if(face.size() > 3) {
				if(SimpInterpreter.getFilled()) {
					for(int j = 1; j < face.size() - 1; j++) {
						int first_index = face.get(0).getVertex();
						int second_index = face.get(j).getVertex();
						int third_index = face.get(j + 1).getVertex();

						Vertex3D vertex1 = new Vertex3D(transformedVertices.get(first_index).getX(),
								transformedVertices.get(first_index).getY(), transformedVertices.get(first_index).getZ(), defaultColor);

						Vertex3D vertex2 = new Vertex3D(transformedVertices.get(second_index).getX(),
								transformedVertices.get(second_index).getY(), transformedVertices.get(second_index).getZ(), defaultColor);

						Vertex3D vertex3 = new Vertex3D(transformedVertices.get(third_index).getX(),
								transformedVertices.get(third_index).getY(), transformedVertices.get(third_index).getZ(), defaultColor);

						Polygon dummy_face = Polygon.makeEnsuringClockwise(vertex1, vertex2, vertex3);
						SimpInterpreter.filledRenderer.drawPolygon(dummy_face, drawable);
					}
				}
				else {
					for(int j = 0; j < face.size() - 1; j ++) {
						int first_index = face.get(0).getVertex();
						int second_index = face.get(j).getVertex();
						int third_index = face.get(j + 1).getVertex();

						Vertex3D vertex1 = new Vertex3D(transformedVertices.get(first_index).getX(),
								transformedVertices.get(first_index).getY(), transformedVertices.get(first_index).getZ(), Color.WHITE);

						Vertex3D vertex2 = new Vertex3D(transformedVertices.get(second_index).getX(),
								transformedVertices.get(second_index).getY(), transformedVertices.get(second_index).getZ(), Color.WHITE);

						Vertex3D vertex3 = new Vertex3D(transformedVertices.get(third_index).getX(),
								transformedVertices.get(third_index).getY(), transformedVertices.get(third_index).getZ(), Color.WHITE);

						Polygon dummy_face = Polygon.make(vertex1, vertex2, vertex3);
						SimpInterpreter.wireframeRenderer.drawPolygon(dummy_face, drawable);
					}
				}
			}
			else{
				int first_index = face.get(0).getVertex();
				int second_index = face.get(1).getVertex();
				int third_index = face.get(2).getVertex();
				if(SimpInterpreter.getFilled()) {
					Vertex3D vertex1 = new Vertex3D(transformedVertices.get(first_index).getX(),
							transformedVertices.get(first_index).getY(), transformedVertices.get(first_index).getZ(), defaultColor);

					Vertex3D vertex2 = new Vertex3D(transformedVertices.get(second_index).getX(),
							transformedVertices.get(second_index).getY(), transformedVertices.get(second_index).getZ(), defaultColor);

					Vertex3D vertex3 = new Vertex3D(transformedVertices.get(third_index).getX(),
							transformedVertices.get(third_index).getY(), transformedVertices.get(third_index).getZ(), defaultColor);

					Polygon dummy_face = Polygon.make(vertex1, vertex2, vertex3);

					SimpInterpreter.filledRenderer.drawPolygon(dummy_face, drawable);
				}
				else {
					Vertex3D vertex1 = new Vertex3D(transformedVertices.get(first_index).getX(),
							transformedVertices.get(first_index).getY(), transformedVertices.get(first_index).getZ(), Color.WHITE);

					Vertex3D vertex2 = new Vertex3D(transformedVertices.get(second_index).getX(),
							transformedVertices.get(second_index).getY(), transformedVertices.get(second_index).getZ(), Color.WHITE);

					Vertex3D vertex3 = new Vertex3D(transformedVertices.get(third_index).getX(),
							transformedVertices.get(third_index).getY(), transformedVertices.get(third_index).getZ(), Color.WHITE);

					Polygon dummy_face = Polygon.make(vertex1, vertex2, vertex3);

					SimpInterpreter.wireframeRenderer.drawPolygon(dummy_face, drawable);
				}
			}
		}
	}


	public void read() {
		while(reader.hasNext() ) {
			String line = reader.next().trim();
			interpretObjLine(line);
		}
	}
	private void interpretObjLine(String line) {
		if(!line.isEmpty() && line.charAt(0) != COMMENT_CHAR) {
			String[] tokens = line.split("[ \t,()]+");
			if(tokens.length != 0) {
				interpretObjCommand(tokens);
			}
		}
	}

	private void interpretObjCommand(String[] tokens) {
		switch(tokens[0]) {
		case "v" :
		case "V" :
			interpretObjVertex(tokens);
			break;
		case "vn":
		case "VN":
			interpretObjNormal(tokens);
			break;
		case "f":
		case "F":
			interpretObjFace(tokens);
			break;
		default:
			break;
		}
	}
	private void interpretObjFace(String[] tokens) {
		ObjFace face = new ObjFace();
		for(int i = 1; i<tokens.length; i++) {
			String token = tokens[i];
			String[] subtokens = token.split("/");
			int vertexIndex  = objIndex(subtokens, 0, objVertices.size());
			int textureIndex = objIndex(subtokens, 1, 0);
			int normalIndex  = objIndex(subtokens, 2, objNormals.size());

			ObjVertex face_vertex = new ObjVertex(vertexIndex);
			face.add(face_vertex);
		}
		objFaces.add(face);
	}

	private int objIndex(String[] subtokens, int tokenIndex, int baseForNegativeIndices) {
		int index = 0;
		if(!subtokens[tokenIndex].equals(null)) {
			index = Integer.parseInt(subtokens[tokenIndex]) - 1;
			if(index < 0) {
				index = baseForNegativeIndices - 1 - (Math.abs(index) - 1);
			}
		}
		return index;
	}

	private void interpretObjNormal(String[] tokens) {
		int numArgs = tokens.length - 1;
		if(numArgs != 3) {
			throw new BadObjFileException("vertex normal with wrong number of arguments : " + numArgs + ": " + tokens);
		}
		Point3DH normal = SimpInterpreter.interpretPoint(tokens, 1);
		objNormals.add(normal);
	}
	private void interpretObjVertex(String[] tokens) {
		int numArgs = tokens.length - 1;
		Point3DH point = objVertexPoint(tokens, numArgs);
		Color color = objVertexColor(tokens, numArgs);

		Vertex3D obj_vertex = new Vertex3D(point, color);
		objVertices.add(obj_vertex);

		Point3DH trans_vertex = new Point3DH(obj_vertex.getPoint3D().getX(), obj_vertex.getPoint3D().getY(), obj_vertex.getPoint3D().getZ());

		double[][] CTM_inverse = SimpInterpreter.getCTM_inverse();
		double current_point[] = new double[4];
		double temp[] = new double[4];
		current_point[0] = trans_vertex.getX();
		current_point[1] = trans_vertex.getY();
		current_point[2] = trans_vertex.getZ();
		current_point[3] = trans_vertex.getW();
		for(int i = 0; i < 4; i++) {
			double lineTotal = 0;
			for(int j = 0; j < 4; j++) {
				lineTotal += CTM_inverse[i][j] * current_point[j];
			}
			temp[i] = lineTotal;
		}
		current_point = temp;
		trans_vertex = new Point3DH(current_point[0], current_point[1], current_point[2], current_point[3]).euclidean();

		double[][] perspective_matrix = SimpInterpreter.getPerspectiveMatrix();
		current_point[0] = trans_vertex.getX();
		current_point[1] = trans_vertex.getY();
		current_point[2] = trans_vertex.getZ();
		current_point[3] = trans_vertex.getW();
		for(int i = 0; i < 4; i++) {
			double lineTotal = 0;
			for(int j = 0; j < 4; j++) {
				lineTotal += perspective_matrix[i][j] * current_point[j];
			}
			temp[i] = lineTotal;
		}
		current_point = temp;
		trans_vertex = new Point3DH(current_point[0], current_point[1], current_point[2], current_point[3]).euclidean();

		double[][] viewToScreen = SimpInterpreter.getViewToScreen();
		current_point[0] = trans_vertex.getX();
		current_point[1] = trans_vertex.getY();
		current_point[2] = trans_vertex.getZ();
		current_point[3] = trans_vertex.getW();
		for(int i = 0; i < 4; i++) {
			double lineTotal = 0;
			for(int j = 0; j < 4; j++) {
				lineTotal += viewToScreen[i][j] * current_point[j];
			}
			temp[i] = lineTotal;
		}
		current_point = temp;
		trans_vertex = new Point3DH(current_point[0], current_point[1], current_point[2], current_point[3]).euclidean();


		Vertex3D transformed_vertex = new Vertex3D(trans_vertex, color);

		transformedVertices.add(transformed_vertex);
	}

	private Color objVertexColor(String[] tokens, int numArgs) {
		if(numArgs == 6) {
			return SimpInterpreter.interpretColor(tokens, 4);
		}
		if(numArgs == 7) {
			return SimpInterpreter.interpretColor(tokens, 5);
		}
		return defaultColor;
	}

	private Point3DH objVertexPoint(String[] tokens, int numArgs) {
		if(numArgs == 3 || numArgs == 6) {
			return SimpInterpreter.interpretPoint(tokens, 1);
		}
		else if(numArgs == 4 || numArgs == 7) {
			return SimpInterpreter.interpretPointWithW(tokens, 1);
		}
		throw new BadObjFileException("vertex with wrong number of arguments : " + numArgs + ": " + tokens);
	}
}