package application;



import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Ellipse2D;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.json.simple.JSONObject;

import utils.Helpful;

public class VizualizareTraseuGUI extends JFrame implements Runnable{
	private JSONObject traseu;
	private int numarOrasetraseu;
	private int flag;
	
	VizualizareTraseuGUI(JSONObject traseu, int nrOrase, int flag){
		super("Traveling Salesman Problem GA");
		graphicPanel = createGraphicPanel();
		add(graphicPanel, BorderLayout.CENTER);
		setSize(800, 600);
		setLocationRelativeTo(null);
		this.traseu = traseu;
		this.numarOrasetraseu = nrOrase;
		this.flag = flag;
	}
	
	public XYSeries series1 = new XYSeries("Object 1", false);
	public JPanel graphicPanel = null;
	public VizualizareTraseuGUI() {
		super("Traveling Salesman Problem GA");
		graphicPanel = createGraphicPanel();
		add(graphicPanel, BorderLayout.CENTER);
		setSize(800, 600);
		setLocationRelativeTo(null);
	}

	private JPanel createGraphicPanel() {
		String graphicTitle = "TSP";
		String xAxisLabel = "X";
		String yAxisLabel = "Y";

		XYDataset dataset = createDataset();
		// XYDataset dataset = null;

		boolean showLegend = false;
		boolean createURL = false;
		boolean createTooltip = false;

		JFreeChart graphic = ChartFactory.createXYLineChart(graphicTitle,
				xAxisLabel, yAxisLabel, dataset, PlotOrientation.VERTICAL,
				showLegend, createTooltip, createURL);
		
		XYPlot plot = graphic.getXYPlot();
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

		// sets paint color for each series
		renderer.setSeriesPaint(0, Color.GREEN);

		// sets thickness for series (using strokes)
		renderer.setSeriesStroke(0, new BasicStroke(1.0f));

		renderer.setSeriesShape(0, new Ellipse2D.Double(-3, -3, 6, 6));

		renderer.setSeriesLinesVisible(0, true);
		renderer.setSeriesShapesVisible(0, true);

		plot.setOutlinePaint(Color.BLUE);
		plot.setOutlineStroke(new BasicStroke(2.0f));

		plot.setBackgroundPaint(Color.DARK_GRAY);

		plot.setRangeGridlinesVisible(true);
		plot.setRangeGridlinePaint(Color.BLACK);

		plot.setDomainGridlinesVisible(true);
		plot.setDomainGridlinePaint(Color.BLACK);
		plot.setRenderer(renderer);

		return new ChartPanel(graphic);
	}

	//
	public XYDataset createDataset() {
		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(series1);
		return dataset;
	}

	@Override
	public void run() {
		this.setVisible(true);
		
		if(flag == 1)
			this.solutiaInitiala();
		if(flag == 2){
			afisareSolutii();
		}
	}
	
	public void addTraseu(){
		double x, y, xi = 0.0,yi = 0.0;
		series1.clear();
		for(int i = 0 ; i < this.numarOrasetraseu; i++){
			try{
				JSONObject oras = (JSONObject) this.traseu.get(i);
				x = Double.parseDouble(oras.get("cx").toString());
				y = Double.parseDouble(oras.get("cy").toString());
			}catch(NullPointerException e){
				JSONObject oras = (JSONObject) this.traseu.get(Integer.toString(i));
				x = Double.parseDouble(oras.get("cx").toString());
				y = Double.parseDouble(oras.get("cy").toString());
			}
			if(i == 0){
				xi = x;
				yi = y;
			}
			System.out.println(x + "-" + y);
			series1.add(x, y);
		}
		series1.add(xi, yi);
		graphicPanel.repaint();
	}
	
	public void solutiaInitiala() {
		addTraseu();

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.out.println("Closed");
				Thread.currentThread().interrupt();
			}
		});
	}
	
	public void afisareSolutii(){
		int code;
		addTraseu();
		long tStart = System.currentTimeMillis();
		do{
			String rezultat;
			try {
				rezultat = Helpful.read(ConversatieAlgoritm.in);
				JSONObject json = Helpful.stringToJsonObject(rezultat);
				this.numarOrasetraseu = Integer.parseInt(json.get("dimensiune").toString());
				this.traseu = json;
				float progres = (float) ((Integer.parseInt(json.get("aep").toString())*1.0) / Integer.parseInt(json.get("ep").toString()));
				RezolvareProblemaGUI.pb.setProgress(progres);
				RezolvareProblemaGUI.pin.setProgress(progres);
				addTraseu();
				code = Integer.parseInt(json.get("code").toString());
			} catch (IOException e) {
				System.out.println("[Eroare]Eroare la afisarea solutiilor");
				code = 100;
			}

		}while( code != 100 );
		
		RezolvareProblemaGUI.pb.setProgress(1.0);
		RezolvareProblemaGUI.pin.setProgress(1.0);
		
		long tEnd = System.currentTimeMillis();
		long tDelta = tEnd - tStart;
		double elapsedSeconds = tDelta / 1000.0;
		System.out.println("Timpul total in secunde:" + elapsedSeconds);
		System.out.println("Cel mai bun traseu(distanta):" + Integer.parseInt(traseu.get("distanta").toString()));
		System.out.println("Numar total de iteratii:" + 1500);
		RezolvareProblemaGUI.actiontarget.setText("Algoritmul a finalizat");
	}

}
