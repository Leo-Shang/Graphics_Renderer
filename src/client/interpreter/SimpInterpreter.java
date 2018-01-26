package client.interpreter;

import java.util.Stack;

import geometry.*;
import line.LineRenderer;
import client.Clipper;
import client.RendererTrio;
import polygon.Polygon;
import polygon.PolygonRenderer;
import windowing.drawable.Drawable;
import windowing.drawable.PixelClippingDrawable;
import windowing.drawable.Z_buffering;
import windowing.graphics.Color;
import windowing.graphics.Dimensions;

public class SimpInterpreter {
	private static final int NUM_TOKENS_FOR_POINT = 3;
	private static final int NUM_TOKENS_FOR_COMMAND = 1;
	private static final int NUM_TOKENS_FOR_COLORED_VERTEX = 6;
	private static final int NUM_TOKENS_FOR_UNCOLORED_VERTEX = 3;
	private static final char COMMENT_CHAR = '#';
	private RenderStyle renderStyle;

	private static double[][] CTM;
//	private double[][] worldToScreen;
	private static double[][] CTM_inverse;
	private static double[][] perspective_matrix;
	private static double[][] viewToScreen;
	private static double[][] non_square_matrix;

//	private static int WORLD_LOW_X = -100;
//	private static int WORLD_HIGH_X = 100;
//	private static int WORLD_LOW_Y = -100;
//	private static int WORLD_HIGH_Y = 100;

	private static double VIEW_LOW_X = 0;
	private static double VIEW_HIGH_X = 0;
	private static double VIEW_LOW_Y = 0;
	private static double VIEW_HIGH_Y = 0;
	private static double VIEW_HITHER = 0;
	private static double VIEW_YON = 0;

	private static int cam_height;
	private static int cam_width;

	private double near;
	private double far;
	private Color depth_color = Color.BLACK;

	private LineBasedReader reader;
	private Stack<LineBasedReader> readerStack;
	private Stack<double[][]> xStack;

	private Color defaultColor = Color.WHITE;
	private Color ambientLight = Color.BLACK;
	private Color kd = Color.WHITE;

	private Drawable drawable;
	private Drawable depthCueingDrawable;
	private Drawable pixelClippingDrawable;

	public static LineRenderer lineRenderer;
	public static PolygonRenderer filledRenderer;
	public static PolygonRenderer wireframeRenderer;
	private Clipper clipper;
	private static boolean filled = true;
	public static Z_buffering z_buffer;

	public static boolean getFilled() {return filled;}

	public static double[][] getCTM_inverse() {return CTM_inverse;}

	public static double[][] getViewToScreen() {
		return viewToScreen;
	}

	public static double[][] getPerspectiveMatrix() {
		return perspective_matrix;
	}

	public static int getHeight(){
		return cam_height;
	}

	public static int getWidth(){
		return cam_width;
	}

	public static double getHither() {
		return VIEW_HITHER;
	}

	public static double getYon() {
		return VIEW_YON;
	}

	public static double[][] get_non_square_matrix() {
		return non_square_matrix;
	}

	public enum RenderStyle {
		FILLED,
		WIREFRAME
	}
	public SimpInterpreter(String filename,
			Drawable drawable,
			RendererTrio renderers) {
		this.drawable = drawable;
		this.depthCueingDrawable = drawable;
		this.lineRenderer = renderers.getLineRenderer();
		this.filledRenderer = renderers.getFilledRenderer();
		this.wireframeRenderer = renderers.getWireframeRenderer();
		this.defaultColor = Color.fromARGB(depthCueingDrawable.getPixel(0, 0));

		CTM = new double[4][4];
//		worldToScreen = new double[4][4];
		CTM_inverse = new double[4][4];
		viewToScreen = new double[4][4];
		perspective_matrix = new double[4][4];
		non_square_matrix = new double[4][4];
		CTM = identity();
//		worldToScreen = identity();
		CTM_inverse = identity();
		viewToScreen = identity();
		perspective_matrix = identity();
		non_square_matrix = identity();
		perspective_matrix[3][3] = 0;
		perspective_matrix[3][2] = -1;

		cam_height = drawable.getHeight();
		cam_width = drawable.getWidth();

		near = -Double.MAX_VALUE;
		far = -Double.MAX_VALUE;

//		makeWorldToScreenTransform(drawable.getDimensions());

		filled = true;
		reader = new LineBasedReader(filename);
		readerStack = new Stack<>();
		renderStyle = RenderStyle.FILLED;

		xStack = new Stack<>();
		renderStyle = RenderStyle.FILLED;
		z_buffer = new Z_buffering();
		for(int i = 0; i < 650; i++){
			for(int j = 0; j < 650; j++){
				z_buffer.changeValue(i, j, -200);
			}
		}
	}

	private double[][] identity() {
		double[][] temp = new double[4][4];
		for(int i = 0 ; i < 4; i++){
			for(int j = 0 ; j < 4; j++){
				if(i == j)
					temp[i][j] = 1;
				else
					temp[i][j] = 0;
			}
		}
		return temp;
	}

//	private void makeWorldToScreenTransform(Dimensions dimensions) {
//
//		double width = (double)dimensions.getWidth()/(WORLD_HIGH_X - WORLD_LOW_X);
//		double height = (double)dimensions.getHeight()/(WORLD_HIGH_Y - WORLD_LOW_Y);
//		worldToScreen[0][0] = width;
//		worldToScreen[1][1] = height;
//		Transformation trans = Transformation.identity();
//		trans.change_value(0,3, 100);
//		trans.change_value(1, 3, 100);
//		worldToScreen = multiply(worldToScreen, trans);
//	}

	private void makeViewToScreenTransform(Dimensions dimensions) {
		double width;
		double height;
		double deltaY = VIEW_HIGH_Y - VIEW_LOW_Y;
		double deltaX = VIEW_HIGH_X - VIEW_LOW_X;
		double deno = deltaX >= deltaY ? deltaX : deltaY;
		double ratio = deltaX / deltaY;
		width = dimensions.getWidth() / deno;
		height = dimensions.getHeight() / deno;

		if(ratio >= 1){
			cam_height = ((int)Math.round(dimensions.getHeight() / ratio));
		} else{
			cam_width = ((int)Math.round(dimensions.getWidth() / ratio));
		}

		makeNonSquareScreenMatrix();

		viewToScreen[0][0] = width;
		viewToScreen[1][1] = height;
		Transformation trans = Transformation.identity();
		trans.change_value(0, 3, 1);
		trans.change_value(1, 3, 1);
		viewToScreen = multiply(viewToScreen, trans);
	}

	private static void makeNonSquareScreenMatrix(){
		double bottom;
		double right;
		if(cam_height >= cam_width){
			right = (650 - cam_width) / 2;
			non_square_matrix[0][3] = (int)right;
		}else{
			bottom = (650 - cam_height) / 2;
			non_square_matrix[1][3] = (int)bottom;
		}
	}

	public void interpret() {
		while(reader.hasNext() ) {
			String line = reader.next().trim();
			interpretLine(line);
			while(!reader.hasNext()) {
				if(readerStack.isEmpty()) {
					return;
				}
				else {
					reader = readerStack.pop();
				}
			}
		}
	}
	public void interpretLine(String line) {
		if(!line.isEmpty() && line.charAt(0) != COMMENT_CHAR) {
			String[] tokens = line.split("[ \t,()]+");
			if(tokens.length != 0) {
				interpretCommand(tokens);
			}
		}
	}
	private void interpretCommand(String[] tokens) {
		switch(tokens[0]) {
		case "{" :      push();   break;
		case "}" :      pop();    break;
		case "wire" :   wire();   break;
		case "filled" : filled(); break;

		case "file" :		interpretFile(tokens);		break;
		case "scale" :		interpretScale(tokens);		break;
		case "translate" :	interpretTranslate(tokens);	break;
		case "rotate" :		interpretRotate(tokens);	break;
		case "line" :		interpretLine(tokens);		break;
		case "polygon" :	interpretPolygon(tokens);	break;
		case "camera" :		interpretCamera(tokens);	break;
		case "surface" :	interpretSurface(tokens);	break;
		case "ambient" :	interpretAmbient(tokens);	break;
		case "depth" :		interpretDepth(tokens);		break;
		case "obj" :		interpretObj(tokens);		break;

		default :
			System.err.println("bad input line: " + tokens);
			break;
		}
	}

	private void push() {
		xStack.push(CTM);
	}
	private void pop() {
		CTM = xStack.pop();
	}
	private void wire() {
		renderStyle = RenderStyle.WIREFRAME;
		filled = false;
	}
	private void filled() {
		renderStyle = RenderStyle.FILLED;
		filled = true;
	}

	// this one is complete.
	private void interpretFile(String[] tokens) {
		String quotedFilename = tokens[1];
		int length = quotedFilename.length();
		assert quotedFilename.charAt(0) == '"' && quotedFilename.charAt(length-1) == '"';
		String filename = quotedFilename.substring(1, length-1);
		file(filename + ".simp");
	}
	private void file(String filename) {
		readerStack.push(reader);
		reader = new LineBasedReader(filename);
	}

	private void interpretScale(String[] tokens) {
		double sx = cleanNumber(tokens[1]);
		double sy = cleanNumber(tokens[2]);
		double sz = cleanNumber(tokens[3]);
		Transformation sk = Transformation.identity();
		sk.change_value(0,0, sx);
		sk.change_value(1,1, sy);
		sk.change_value(2,2, sz);
		CTM = multiply(CTM, sk);
	}

	private void interpretTranslate(String[] tokens) {
		double tx = cleanNumber(tokens[1]);
		double ty = cleanNumber(tokens[2]);
		double tz = cleanNumber(tokens[3]);
		Transformation t = Transformation.identity();
		t.change_value(0, 3, tx);
		t.change_value(1, 3, ty);
		t.change_value(2, 3, tz);
		CTM = multiply(CTM, t);
	}
	private void interpretRotate(String[] tokens) {
		String axisString = tokens[1];
		double angleInDegrees = cleanNumber(tokens[2]);

		Transformation tran = Transformation.identity();
		if(axisString.equals("X")){
			tran.change_value(1, 1, Math.cos(Math.toRadians(angleInDegrees)));
			tran.change_value(1, 2, (-1) * Math.sin(Math.toRadians(angleInDegrees)));
			tran.change_value(2, 1, Math.sin(Math.toRadians(angleInDegrees)));
			tran.change_value(2, 2, Math.cos(Math.toRadians(angleInDegrees)));
		} else if(axisString.equals("Y")){
			tran.change_value(0, 0, Math.cos(Math.toRadians(angleInDegrees)));
			tran.change_value(0, 2, Math.sin(Math.toRadians(angleInDegrees)));
			tran.change_value(2, 0, (-1) * Math.sin(Math.toRadians(angleInDegrees)));
			tran.change_value(2, 2, Math.cos(Math.toRadians(angleInDegrees)));
		} else if(axisString.equals("Z")) {
			tran.change_value(0, 0, Math.cos(Math.toRadians(angleInDegrees)));
			tran.change_value(0, 1, (-1) * Math.sin(Math.toRadians(angleInDegrees)));
			tran.change_value(1, 0, Math.sin(Math.toRadians(angleInDegrees)));
			tran.change_value(1, 1, Math.cos(Math.toRadians(angleInDegrees)));
		}
		CTM = multiply(CTM, tran);
	}

	private void interpretCamera(String[] tokens){
		CTM_inverse = inverseMatrix(CTM);

		VIEW_LOW_X = cleanNumber(tokens[1]);
		VIEW_LOW_Y = cleanNumber(tokens[2]);
		VIEW_HIGH_X = cleanNumber(tokens[3]);
		VIEW_HIGH_Y = cleanNumber(tokens[4]);
		VIEW_HITHER = cleanNumber(tokens[5]);
		VIEW_YON = cleanNumber(tokens[6]);

		clipper = new Clipper(VIEW_LOW_X, VIEW_HIGH_X, VIEW_LOW_Y, VIEW_HIGH_Y, VIEW_HITHER, VIEW_YON);
		makeViewToScreenTransform(drawable.getDimensions());
		pixelClippingDrawable = new PixelClippingDrawable(drawable, 0, 650, 0, 650, VIEW_HITHER, VIEW_YON);
	}

	private void interpretAmbient(String[] tokens){
		double color_R = cleanNumber(tokens[1])*255;
		double color_G = cleanNumber(tokens[2])*255;
		double color_B = cleanNumber(tokens[3])*255;
		ambientLight = Color.fromARGB(Color.makeARGB((int)Math.round(color_R),(int)Math.round(color_G), (int)Math.round(color_B)));
		defaultColor = ambientLight;
	}

	private void interpretSurface(String[] tokens){
		double color_R = cleanNumber(tokens[1])*255;
		double color_G = cleanNumber(tokens[2])*255;
		double color_B = cleanNumber(tokens[3])*255;
		kd = Color.fromARGB(Color.makeARGB((int)Math.round(color_R),(int)Math.round(color_G), (int)Math.round(color_B)));
	}

	private void interpretDepth(String[] tokens){
		near = cleanNumber(tokens[1]);
		far = cleanNumber(tokens[2]);
		double depth_R = cleanNumber(tokens[3])*255;
		double depth_G = cleanNumber(tokens[4])*255;
		double depth_B = cleanNumber(tokens[5])*255;
		depth_color = Color.fromARGB(Color.makeARGB((int)Math.round(depth_R),(int)Math.round(depth_G), (int)Math.round(depth_B)));
	}

	private void interpretObj(String[] tokens) {
		String quotedFilename = tokens[1];
		int length = quotedFilename.length();
		assert quotedFilename.charAt(0) == '"' && quotedFilename.charAt(length-1) == '"';
		String filename = quotedFilename.substring(1, length-1);
		filename = filename + ".obj";
		objFile(filename);
	}

	private void objFile(String filename) {
		ObjReader objReader = new ObjReader(filename, defaultColor);
		// store object vertex, texture vertex, normal vertex, and faces into lists
		objReader.read();
		// start rendering
		objReader.render(defaultColor, drawable);
	}

	private static double cleanNumber(String string) {
		return Double.parseDouble(string);
	}

	private enum VertexColors {
		COLORED(NUM_TOKENS_FOR_COLORED_VERTEX),
		UNCOLORED(NUM_TOKENS_FOR_UNCOLORED_VERTEX);

		private int numTokensPerVertex;

		private VertexColors(int numTokensPerVertex) {
			this.numTokensPerVertex = numTokensPerVertex;
		}
		public int numTokensPerVertex() {
			return numTokensPerVertex;
		}
	}
	private void interpretLine(String[] tokens) {
		Vertex3D[] vertices = interpretVertices(tokens, 2, 1);
		Clipper.Clipped<Line> clipped = clipper.clipLine(new Line(vertices[0], vertices[1]));
		if(clipped.objectIsValid()){
			Line line = clipped.getClippedPolygon();

			Point3D temp1 = line.p1.getPoint3D();
			Point3D temp2 = line.p2.getPoint3D();
			Color c1 = line.p1.getColor();
			Color c2 = line.p2.getColor();
			double cam_Z1 = temp1.getZ();
			double cam_Z2 = temp2.getZ();
			double[] current_point1 = new double[4];
			double[] multiply_result1 = new double[4];
			double[] current_point2 = new double[4];
			double[] multiply_result2 = new double[4];
			current_point1[0] = temp1.getX();
			current_point1[1] = temp1.getY();
			current_point1[2] = temp1.getZ();
			current_point1[3] = 1.0;
			current_point2[0] = temp2.getX();
			current_point2[1] = temp2.getY();
			current_point2[2] = temp2.getZ();
			current_point2[3] = 1.0;
			for(int j = 0; j < 4; j++){
				double lineTotal1 = 0;
				double lineTotal2 = 0;
				for(int k = 0; k < 4; k++){
					lineTotal1 += perspective_matrix[j][k] * current_point1[k];
					lineTotal2 += perspective_matrix[j][k] * current_point2[k];
				}
				multiply_result1[j] = lineTotal1;
				multiply_result2[j] = lineTotal2;
			}
			current_point1 = multiply_result1; // in view space
			current_point2 = multiply_result2; // in view space
			for(int j = 0; j < 4; j++){
				double lineTotal1 = 0;
				double lineTotal2 = 0;
				for(int k = 0; k < 4; k++){
					lineTotal1 += viewToScreen[j][k] * current_point1[k];
					lineTotal2 += viewToScreen[j][k] * current_point2[k];
				}
				multiply_result1[j] = lineTotal1;
				multiply_result2[j] = lineTotal2;
			}
			current_point1 = multiply_result1; // in view space
			current_point2 = multiply_result2; // in view space
			Point3DH temp_point1 = new Point3DH(current_point1).euclidean();
			Point3DH temp_point2 = new Point3DH(current_point2).euclidean();
			Vertex3D p1 = new Vertex3D(temp_point1.getX(), temp_point1.getY(), temp_point1.getZ(), c1);
			Vertex3D p2 = new Vertex3D(temp_point2.getX(), temp_point2.getY(), temp_point2.getZ(), c2);
			p1.setZ(cam_Z1);
			p2.setZ(cam_Z2);
			lineRenderer.drawLine(p1, p2, drawable);
		}
	}

	private Vertex3D applyColor(Vertex3D new_point) {
		Color new_color = defaultColor;
		if (new_point.getZ() >= near) {
			new_color = ambientLight.multiply(kd);
		} else if (new_point.getZ() <= far) {
			new_color = depth_color;
		} else if (near >= new_point.getZ() && new_point.getZ() >= far) {
			Color lighting_calc_color = ambientLight.multiply(kd);
			//			System.out.println("The depth color is: " + depth_color);
			//			System.out.println("The kd is: "+ kd);
			//			System.out.println("The ambientLight is: "+ ambientLight);
			//			System.out.println("The lighting color is: " + lighting_calc_color);
			double r_slope = (depth_color.getR() - lighting_calc_color.getR()) / Math.abs((far - near));
			double g_slope = (depth_color.getG() - lighting_calc_color.getG()) / Math.abs((far - near));
			double b_slope = (depth_color.getB() - lighting_calc_color.getB()) / Math.abs((far - near));
			double deltaZ = Math.abs(new_point.getZ()) - Math.abs(near);
			//			System.out.println("r_slope is: " + r_slope);
			//			System.out.println("g_slope is: " + g_slope);
			//			System.out.println("b_slope is: " + b_slope);
			//			System.out.println("delta z is: " + deltaZ);
			double new_R = (lighting_calc_color.getR() + r_slope * deltaZ) * 255;
			double new_G = (lighting_calc_color.getG() + g_slope * deltaZ) * 255;
			double new_B = (lighting_calc_color.getB() + b_slope * deltaZ) * 255;
			new_color = Color.fromARGB(Color.makeARGB((int) Math.round(new_R), (int) Math.round(new_G),
					(int) Math.round(new_B)));
		}
		return new Vertex3D(new_point.getX(),new_point.getY(),new_point.getZ(), new_color);
	}

	private void interpretPolygon(String[] tokens) {
		Vertex3D[] vertices = interpretVertices(tokens, 3, 1);
		Polygon polygon = Polygon.make(vertices[0],vertices[1], vertices[2]);
		Clipper.Clipped<Polygon> clipped = clipper.clipPolygon(polygon);
		if (clipped.objectIsValid()){
			polygon = clipped.getClippedPolygon();
			Vertex3D[] list = new Vertex3D[polygon.length()];
			for(int i = 0 ; i < polygon.length(); i++){
				Vertex3D temp = polygon.get(i);
				list[i] = applyColor(temp);
			}
			polygon = Polygon.makeEnsuringClockwise(list);

			polygon = ApplyPerspective.multiplyPerspectiveMatrix(polygon);
			polygon = ApplyViewToScreen.multiplyViewToScreenMatrix(polygon);
			polygon = ApplyNonSquareScreen.multiplyNonSquareScreenMatrix(polygon);
			int current = 1;
			int size_of_polygon = polygon.length() - 2;
			while(current <= size_of_polygon){
				if(filled) {
					Polygon triangle = Polygon.makeEnsuringClockwise(polygon.get(current + 1), polygon.get(0), polygon.get(current));
					Polygon triangle1 = Polygon.makeEnsuringClockwise(polygon.get(0), polygon.get(current), polygon.get(current + 1));
					Polygon triangle2 = Polygon.makeEnsuringClockwise(polygon.get(0), polygon.get(current), polygon.get(current + 1));
//					System.out.println(triangle);
					filledRenderer.drawPolygon(triangle, pixelClippingDrawable);
					filledRenderer.drawPolygon(triangle1, pixelClippingDrawable);
					filledRenderer.drawPolygon(triangle2, pixelClippingDrawable);
				} else {
					Polygon triangle = Polygon.make(polygon.get(current + 1), polygon.get(0), polygon.get(current));
					wireframeRenderer.drawPolygon(triangle, pixelClippingDrawable);
				}
				current++;
			}
		}
	}


	public Vertex3D[] interpretVertices(String[] tokens, int numVertices, int startingIndex) {
		VertexColors vertexColors = verticesAreColored(tokens, numVertices);
		Vertex3D vertices[] = new Vertex3D[numVertices];

		for(int index = 0; index < numVertices; index++) {
			vertices[index] = interpretVertex(tokens, startingIndex + index * vertexColors.numTokensPerVertex(), vertexColors);
		}
		return vertices;
	}
	public VertexColors verticesAreColored(String[] tokens, int numVertices) {
		return hasColoredVertices(tokens, numVertices) ? VertexColors.COLORED :
														 VertexColors.UNCOLORED;
	}
	public boolean hasColoredVertices(String[] tokens, int numVertices) {
		return tokens.length == numTokensForCommandWithNVertices(numVertices);
	}
	public int numTokensForCommandWithNVertices(int numVertices) {
		return NUM_TOKENS_FOR_COMMAND + numVertices*(NUM_TOKENS_FOR_COLORED_VERTEX);
	}

	private Vertex3D interpretVertex(String[] tokens, int startingIndex, VertexColors colored) {
		Point3DH point = interpretPoint(tokens, startingIndex);
		Color color = defaultColor;
		if(colored == VertexColors.COLORED) {
			color = interpretColor(tokens, startingIndex + NUM_TOKENS_FOR_POINT);
		}
		Color new_color = color;

		double current_point[] = new double[4];
		double temp[] = new double[4];
		current_point[0] = point.getX();
		current_point[1] = point.getY();
		current_point[2] = point.getZ();
		current_point[3] = point.getW();
		for(int i = 0; i < 4; i++) {
			double lineTotal = 0;
			for(int j = 0; j < 4; j++) {
				lineTotal += CTM_inverse[i][j] * current_point[j];
			}
			temp[i] = lineTotal;
		}
		current_point = temp; // in cam space

		Point3DH new_point = new Point3DH(current_point[0], current_point[1], current_point[2], current_point[3]).euclidean();
//		double z_cam = new_point.getZ();
//		System.out.println("z_cam is: " + z_cam);
//		System.out.println("point in cam space is: " + new_point);


//		for(int i = 0; i < 4; i++){
//			double lineTotal = 0;
//			for(int j = 0; j < 4; j++){
//				lineTotal += perspective_matrix[i][j] * current_point[j];
//			}
//			temp[i] = lineTotal;
//		}
//		current_point = temp; // in view space
//		new_point = new Point3DH(current_point[0], current_point[1], current_point[2], current_point[3]).euclidean();
////		System.out.println("point in view space is: " + new_point);
//		for(int i = 0; i < 4; i++){
//			double lineTotal = 0;
//			for(int j = 0; j < 4; j++){
//				lineTotal += viewToScreen[i][j] * current_point[j];
//			}
//			temp[i] = lineTotal;
//		}
//		current_point = temp; // in window space
//		new_point = new Point3DH(current_point[0], current_point[1], current_point[2], current_point[3]).euclidean();
//		System.out.println("point in window space is: " + new_point);

//
//		System.out.println("CTM MATRIX====================");
//		for(int i = 0; i < 4; i ++){
//			for(int j = 0 ; j < 4; j ++){
//				System.out.print(CTM[i][j] + " ");
//			}
//			System.out.println();
//		}
//		System.out.println("CTM INVERSE MATRIX====================");
//		for(int i = 0; i < 4; i ++){
//			for(int j = 0 ; j < 4; j ++){
//				System.out.print(CTM_inverse[i][j] + " ");
//			}
//			System.out.println();
//		}
//		System.out.println("PERSPECTIVE MATRIX====================");
//		for(int i = 0; i < 4; i ++){
//			for(int j = 0 ; j < 4; j ++){
//				System.out.print(perspective_matrix[i][j] + " ");
//			}
//			System.out.println();
//		}
//		System.out.println("VIEW TO SCREEN MATRIX====================");
//		for(int i = 0; i < 4; i ++){
//			for(int j = 0 ; j < 4; j ++){
//				System.out.print(viewToScreen[i][j] + " ");
//			}
//			System.out.println();
//		}

		new_point = new Point3DH(current_point[0], current_point[1], current_point[2], current_point[3]).euclidean();
//		double fraction = (200 - Math.abs(new_point.getZ())) / 200;
//		r = r * fraction;
//		g = g * fraction;
//		b = b * fraction;
//		System.out.println("THE POINT IS: " + new_point);
//		boolean all_empty = false;
//		for(int i = 0; i < 650; i++){
//			for(int j = 0; j < 650; j++){
//				if(z_buffer.getValue(i,j) != -200)
//					all_empty = true;
//			}
//		}
//		if(all_empty == true){
//			System.out.println("Z BUFFER HAS BEEN CHANGED");
//		} else{
//			System.out.println("z buffer is in INITIAL STATE ");
//		}

//		System.out.println("The point is: " + new_point.getX() + " " + new_point.getY() + " " + new_point.getZ() + " and the color is: " + new_color);
//		if(new_point.getX() >= 0 && new_point.getX() < 650 && new_point.getY() >= 0 && new_point.getY() < 650)
//			System.out.println("z buffer at this point is: "+z_buffer.getValue(new_point.getIntX(), new_point.getIntY()));
//		System.out.println("====================================================================");
		return new Vertex3D(new_point.getX(), new_point.getY(), new_point.getZ(), new_color);
	}

	public static Point3DH interpretPoint(String[] tokens, int startingIndex) {
		double x = cleanNumber(tokens[startingIndex]);
		double y = cleanNumber(tokens[startingIndex + 1]);
		double z = cleanNumber(tokens[startingIndex + 2]);

		double current_point[] = new double[4];
		double temp[] = new double[4];
		current_point[0] = x;
		current_point[1] = y;
		current_point[2] = z;
		current_point[3] = 1.0;
		for(int i = 0; i < 4; i++) {
			double lineTotal = 0;
			for(int j = 0; j < 4; j++) {
				lineTotal += CTM[i][j] * current_point[j];
			}
			temp[i] = lineTotal;
		}
		current_point = temp; // in world space
		return new Point3DH(current_point[0], current_point[1], current_point[2], current_point[3]).euclidean();
	}
	public static Color interpretColor(String[] tokens, int startingIndex) {
		double r = cleanNumber(tokens[startingIndex]);
		double g = cleanNumber(tokens[startingIndex + 1]);
		double b = cleanNumber(tokens[startingIndex + 2]);
		return Color.fromARGB(Color.makeARGB((int)Math.round(r), (int)Math.round(g), (int)Math.round(b)));
	}

	private void line(Vertex3D p1, Vertex3D p2) {
		Vertex3D screenP1 = transformToCamera(p1);
		Vertex3D screenP2 = transformToCamera(p2);

		lineRenderer.drawLine(screenP1, screenP2, drawable);
	}
	private void polygon(Vertex3D p1, Vertex3D p2, Vertex3D p3) {
		Vertex3D screenP1 = transformToCamera(p1);
		Vertex3D screenP2 = transformToCamera(p2);
		Vertex3D screenP3 = transformToCamera(p3);

		lineRenderer.drawLine(screenP1, screenP2, drawable);
		lineRenderer.drawLine(screenP1, screenP3, drawable);
		lineRenderer.drawLine(screenP2, screenP3, drawable);
	}

	private Vertex3D transformToCamera(Vertex3D vertex) {
		double[] ans = new double[4];
		double[] temp = new double[4];
		temp[0] = vertex.getX();
		temp[1] = vertex.getY();
		temp[2] = vertex.getZ();
		temp[3] = vertex.getPoint3D().getW();

		for(int i = 0; i < 4; i++) {
			double lineTotal = 0;
			for(int j = 0; j < 4; j++) {
				lineTotal += perspective_matrix[i][j] * temp[j];
			}
			ans[i] = lineTotal;
		}
		return new Vertex3D(ans[0]/ans[3], ans[1]/ans[3], ans[2]/ans[3], vertex.getColor());
	}

	private double[][] multiply(double[][] ctm, Transformation sk) {
		double[][] temp = new double[4][4];
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < sk.getWidth(); j++) {
				double lineTotal = 0;
				for (int index = 0; index < 4; index++) {
					lineTotal += ctm[i][index] * sk.getValue(index, j);
				}
				temp[i][j] = lineTotal;
			}
		}
		return temp;
	}

	public static Point3DH interpretPointWithW(String[] tokens, int startingIndex) {
		double x = cleanNumber(tokens[startingIndex]);
		double y = cleanNumber(tokens[startingIndex + 1]);
		double z = cleanNumber(tokens[startingIndex + 2]);
		double w = cleanNumber(tokens[startingIndex + 3]);
		Point3DH point = new Point3DH(x, y, z, w);
		return point;
	}
//
//	private void objFile(String filename) {
//		ObjReader objReader = new ObjReader(filename, defaultColor);
//		objReader.read();
//		objReader.render();
//	}

	private double[][] inverseMatrix(double[][] matrix) {
		double[][] cofactor = new double[4][4];

		for(int row = 0; row < 4; row++){
			for(int col = 0; col < 4; col++){
				double[][] temp = new double[3][3];
				double[] arrayMat = new double[9];
				int counter = 0;
				for(int i = 0; i < 4; i++){
					for(int j = 0; j < 4; j++){
						if(i != row && j != col){
							arrayMat[counter] = matrix[i][j];
							counter++;
						}
					}
				}
				counter = 0;
				for(int i = 0; i < 3; i++){
					for(int j = 0; j < 3; j++){
						temp[i][j] = arrayMat[counter];
						counter++;
					}
				}
				cofactor[row][col] = calculate3by3Determinant(temp);
			}
		}

		int n = -1;
		int exp = 2;
		for(int i = 0; i < 4; i++){
			for(int j = 0; j < 4; j++){
				cofactor[i][j] *= Math.pow(n, exp);
				exp++;
			}
			exp += 1;
		}

		double[][] cofactor_inverse = new double[4][4];
		for(int i = 0; i < 4; i++){
			for(int j = 0; j < 4; j++) {
				cofactor_inverse[i][j] = cofactor[j][i];
			}
		}

		double determinant = 0;
		for(int row = 0; row < 4; row++){
			int col = 0;
			double[][] temp = new double[3][3];
			double[] arrayMat = new double[9];
			int counter = 0;
			for(int i = 0; i < 4; i++){
				for(int j = 0; j < 4; j++){
					if(i != row && j != col){
						arrayMat[counter] = matrix[i][j];
						counter++;
					}
				}
			}
			counter = 0;
			for(int i = 0; i < 3; i++){
				for(int j = 0; j < 3; j++){
					temp[i][j] = arrayMat[counter];
					counter++;
				}
			}
			determinant += matrix[row][col] * calculate3by3Determinant(temp);
		}

		for(int row = 0; row < 4; row++){
			for(int col = 0; col < 4; col++) {
				cofactor_inverse[row][col] /= determinant;
			}
		}
		return cofactor_inverse;
	}

	private double calculate3by3Determinant(double[][] mat){
		double num1 = mat[0][0] * mat[1][1] * mat[2][2];
		double num2 = mat[0][1] * mat[1][2] * mat[2][0];
		double num3 = mat[0][2] * mat[1][0] * mat[2][1];
		double num4 = mat[2][0] * mat[1][1] * mat[0][2];
		double num5 = mat[2][1] * mat[1][2] * mat[0][0];
		double num6 = mat[2][2] * mat[1][0] * mat[0][1];
		return num1 + num2 + num3 - num4 - num5 - num6;
	}
}