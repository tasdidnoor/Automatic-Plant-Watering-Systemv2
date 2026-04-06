import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import javax.swing.JFrame;
import java.awt.Color;

public class myGRAPH {

    private XYSeries series;
    private XYSeriesCollection dataset;
    private JFreeChart chart;
    private JFrame frame;
    private int pointCount;

    // Constructor
    public myGRAPH(String title, String yAxisLabel) {
        // Create the data series
        series = new XYSeries("Soil Moisture");
        dataset = new XYSeriesCollection(series);

        // Create the chart
        chart = ChartFactory.createXYLineChart(
                title,           // Chart title
                "Time (cycles)", // X-axis label
                yAxisLabel,      // Y-axis label
                dataset          // Data
        );

        // Customize the chart
        XYPlot plot = chart.getXYPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesPaint(0, Color.BLUE);
        renderer.setSeriesShapesVisible(0, true);
        renderer.setSeriesLinesVisible(0, true);
        plot.setRenderer(renderer);

        // Create and setup the window
        ChartPanel panel = new ChartPanel(chart);
        frame = new JFrame("Live Moisture Graph");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(panel);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        pointCount = 0;
        System.out.println("[GRAPH] Live graph window opened");
    }

    // Add a single data point
    public void addDataPoint(int moisturePercent) {
        series.add(pointCount, moisturePercent);
        pointCount++;
        System.out.println("[GRAPH] Added point: Cycle " + pointCount + " = " + moisturePercent + "%");
    }

    // Add data point with custom x-value (for timestamps)
    public void addDataPoint(double xValue, int moisturePercent) {
        series.add(xValue, moisturePercent);
        pointCount++;
        System.out.println("[GRAPH] Added point: x=" + xValue + ", y=" + moisturePercent + "%");
    }

    // Add multiple points from DataLogger (for loading existing data)
    public void loadHistoricalData(myDATA logger) {
        int[] historicalData = logger.getMoistureValues();
        for (int i = 0; i < historicalData.length; i++) {
            series.add(i, historicalData[i]);
            pointCount++;
        }
        System.out.println("[GRAPH] Loaded " + historicalData.length + " historical points");
    }

    // Clear the graph
    public void clear() {
        series.clear();
        pointCount = 0;
        System.out.println("[GRAPH] Graph cleared");
    }

    // Close the graph window
    public void close() {
        if (frame != null) {
            frame.dispose();
            System.out.println("[GRAPH] Graph window closed");
        }
    }
}