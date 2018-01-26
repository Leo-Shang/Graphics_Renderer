package client;

//import client.interpreter.SimpInterpreter;
import client.interpreter.SimpInterpreter;
import client.testPages.*;
import geometry.Point2D;
import line.AlternatingLineRenderer;
import line.ExpensiveLineRenderer;
import line.LineRenderer;
import client.ColoredDrawable;
import line.AntialiasingLineRenderer;
import line.BresenhamLineRenderer;
import line.DDALineRenderer;
import polygon.*;
import windowing.PageTurner;
import windowing.drawable.Drawable;
import windowing.drawable.GhostWritingDrawable;
import windowing.drawable.InvertedYDrawable;
import windowing.drawable.TranslatingDrawable;
import windowing.graphics.Color;
import windowing.graphics.Dimensions;

public class Client implements PageTurner {
	private static final int ARGB_WHITE = 0xff_ff_ff_ff;
	private static final int ARGB_GREEN = 0xff_00_ff_40;

	private static final int NUM_PAGES = 10;
	protected static final double GHOST_COVERAGE = 0.14;

	private static final int NUM_PANELS = 1;
	private static final Dimensions PANEL_SIZE = new Dimensions(650, 650);

	private final Drawable drawable;
	private int pageNumber = 0;

	private Drawable image; // 750
	private Drawable[] panels;
	private Drawable[] ghostPanels;					// use transparency and write only white
	private Drawable largePanel; // 650

	private LineRenderer lineRenderers[];
	private PolygonRenderer polygonRenderer;

	private boolean hasArgument = false;


	public Client(Drawable drawable) {
		this.drawable = drawable;
		createDrawables();
		createRenderers();
	}

	public void createDrawables() {
		image = new InvertedYDrawable(drawable);
		image = new TranslatingDrawable(image, point(0, 0), dimensions(750, 750));
		image = new ColoredDrawable(image, ARGB_WHITE);

		largePanel = new TranslatingDrawable(image, point(  50, 50),  dimensions(650, 650));

		createPanels();
		createGhostPanels();
	}

	public void createPanels() {
		panels = new Drawable[NUM_PANELS];

		for(int index = 0; index < NUM_PANELS; index++) {
			panels[index] = new TranslatingDrawable(image, point(50, 50), PANEL_SIZE);
		}
	}

	private void createGhostPanels() {
		ghostPanels = new Drawable[NUM_PANELS];

		for(int index = 0; index < NUM_PANELS; index++) {
			Drawable drawable = panels[index];
			ghostPanels[index] = new GhostWritingDrawable(drawable, GHOST_COVERAGE);
		}
	}
	private Point2D point(int x, int y) {
		return new Point2D(x, y);
	}
	private Dimensions dimensions(int x, int y) {
		return new Dimensions(x, y);
	}
	private void createRenderers() {

		lineRenderers = new LineRenderer[4];
		lineRenderers[0] = BresenhamLineRenderer.make();
		//lineRenderers[0] = ExpensiveLineRenderer.make();
		lineRenderers[1] = DDALineRenderer.make();
		lineRenderers[2] = AlternatingLineRenderer.make();
		lineRenderers[3] = AntialiasingLineRenderer.make();

		polygonRenderer = FilledPolygonRenderer.make();
	}

//	@Override
//	public void nextPage() {
//		Drawable depthCueingDrawable;
//		System.out.println("PageNumber " + (pageNumber + 1));
//		pageNumber = (pageNumber + 1) % NUM_PAGES;
//		SimpInterpreter interpreter;
//		RendererTrio renderers = new RendererTrio();
//
//		image.clear();
//		largePanel.clear();
//
//		switch(pageNumber) {
//			case 1:  new MeshPolygonTest(largePanel, WireframeRenderer.make(), MeshPolygonTest.USE_PERTURBATION);
//				break;
//			case 2:  new MeshPolygonTest(largePanel, ColoredpolygonRendererNoZBuff.make(), MeshPolygonTest.USE_PERTURBATION);
//				break;
//			case 3:	 new CenteredTriangleTest(largePanel, polygonRenderer);
//				break;
//
//			case 4:  depthCueingDrawable = new DepthCueingDrawable(largePanel, 0, -200, Color.GREEN);
//				interpreter = new SimpInterpreter("myPage4.simp", depthCueingDrawable, renderers);
//				interpreter.interpret();
//				break;
//
//			case 5:  depthCueingDrawable = new DepthCueingDrawable(largePanel, 0, -200, Color.RED);
//				interpreter = new SimpInterpreter("myPage5.simp", depthCueingDrawable, renderers);
//				interpreter.interpret();
//				break;
//
//			case 6:  depthCueingDrawable = new DepthCueingDrawable(largePanel, 0, -200, Color.WHITE);
//				interpreter = new SimpInterpreter("page6.simp", depthCueingDrawable, renderers);
//				interpreter.interpret();
//				break;
//
//			case 7:  depthCueingDrawable = new DepthCueingDrawable(largePanel, 0, -200, Color.WHITE);
//				interpreter = new SimpInterpreter("page7.simp", depthCueingDrawable, renderers);
//				interpreter.interpret();
//				break;
//
//			case 0:  depthCueingDrawable = new DepthCueingDrawable(largePanel, 0, -200, Color.WHITE);
//				interpreter = new SimpInterpreter("page8.simp", depthCueingDrawable, renderers);
//				interpreter.interpret();
//				break;
//
//			default: defaultPage();
//				break;
//		}
//	}

	@Override
	public void nextPage() {
		if(hasArgument) {
			argumentNextPage();
		}
		else {
			noArgumentNextPage();
		}
	}

	private void argumentNextPage() {
		SimpInterpreter interpreter;
		String filename = null;
		RendererTrio renderers = new RendererTrio();
		image.clear();
		largePanel.clear();

		interpreter = new SimpInterpreter(filename + ".simp", largePanel, renderers);
		interpreter.interpret();
	}

	public void noArgumentNextPage() {
		SimpInterpreter interpreter;
		RendererTrio renderers = new RendererTrio();
		System.out.println("PageNumber " + (pageNumber + 1));
		pageNumber = (pageNumber + 1) % NUM_PAGES;

		image.clear();
		largePanel.clear();
		String filename;

		switch(pageNumber) {
			case 1:  filename = "pageA";	 break;
			case 2:  filename = "pageB";	 break;
			case 3:	 filename = "pageC";	 break;
			case 4:  filename = "pageD";	 break;
			case 5:  filename = "pageE";	 break;
			case 6:  filename = "pageF";	 break;
			case 7:  filename = "pageG";	 break;
			case 8:  filename = "pageH";	 break;
			case 9:  filename = "pageI";	 break;
			case 0:  filename = "PageJ";	 break;

			default: defaultPage();
				return;
		}
		interpreter = new SimpInterpreter(filename + ".simp", largePanel, renderers);
		interpreter.interpret();
	}



	@FunctionalInterface
	private interface TestPerformer {
		public void perform(Drawable drawable, LineRenderer renderer);
	}
	private void lineDrawerPage(TestPerformer test) {
		image.clear();

		for(int panelNumber = 0; panelNumber < panels.length; panelNumber++) {
			panels[panelNumber].clear();
			test.perform(panels[panelNumber], lineRenderers[panelNumber]);
		}
	}
	public void polygonDrawerPage(Drawable[] panelArray) {
		image.clear();
		for(Drawable panel: panels) {		// 'panels' necessary here.  Not panelArray, because clear() uses setPixel.
			panel.clear();
		}
		new StarburstPolygonTest(panelArray[0], polygonRenderer);
		new MeshPolygonTest(panelArray[1], polygonRenderer, MeshPolygonTest.NO_PERTURBATION);
		new MeshPolygonTest(panelArray[2], polygonRenderer, MeshPolygonTest.USE_PERTURBATION);
		new RandomPolygonTest(panelArray[3], polygonRenderer);
	}

	private void defaultPage() {
		image.clear();
		largePanel.fill(ARGB_GREEN, Double.MAX_VALUE);
	}
}
