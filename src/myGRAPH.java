import edu.princeton.cs.introcs.StdDraw;

public class myGRAPH {

    private int pointCount;

    public myGRAPH(String title, String yAxisLabel) {
        this.pointCount = 0;
        StdDraw.setCanvasSize(800, 600);
        StdDraw.setXscale(0, 100);
        StdDraw.setYscale(0, 110);
        StdDraw.setPenRadius(0.01);
        System.out.println("[GRAPH] Live graph ready");
    }

    public void addDataPoint(int moisturePercent) {
        pointCount++;
        StdDraw.setPenColor(StdDraw.BLUE);
        StdDraw.point(pointCount, moisturePercent);
        System.out.println("[GRAPH] Point " + pointCount + ": " + moisturePercent + "%");
    }

    public void loadHistoricalData(myDATA logger) {
        int[] data = logger.getMoistureValues();
        for (int i = 0; i < data.length; i++) {
            addDataPoint(data[i]);
        }
    }
}