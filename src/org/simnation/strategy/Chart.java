/*
 * SimNation is a multi-agent model to simulate economic systems. It is scalable 
 * and used JSimpleSim as technical backbone for concurrent discrete event simulation.
 * 
 * This software is published as open source and licensed under GNU GPLv3.
 * 
 * Contributors:
 * 	- Rene Kuhlemann - development and initial implementation
 * 
 */
package org.simnation.strategy;

import java.awt.Color;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import org.knowm.xchart.AnnotationTextPanel;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.XYSeries.XYSeriesRenderStyle;
import org.knowm.xchart.style.Styler.ChartTheme;
import org.knowm.xchart.style.markers.SeriesMarkers;

/**
 */
public class Chart {
	
	private final int PARETO_SIZE=50;
	
	private final XYChart chart;
	private final double[] f1pareto=new double[PARETO_SIZE];
	private final double[] f2pareto=new double[PARETO_SIZE];
	private final Deque<Double> f1=new ArrayDeque<>();
	private final Deque<Double> f2=new ArrayDeque<>();

	private final SwingWrapper sw;
    
  public Chart() {
    chart=new XYChartBuilder()
            .width(800)
            .height(600)
            .title("Fonseca-Fleming-Problem")
            .xAxisTitle("X")
            .yAxisTitle("Y")
            .theme(ChartTheme.Matlab)
            .build();

    // Customize Chart
    chart.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Scatter);
    chart.getStyler().setChartTitleVisible(false);
    chart.getStyler().setLegendVisible(false);
    chart.getStyler().setAxisTitlesVisible(false);
    chart.getStyler().setXAxisDecimalPattern("0.0");
    
    for (int i=0; i<PARETO_SIZE; i++) { // draw the pareto-optimal front
    	double f1=Math.exp(-4)+i*((1-2*Math.exp(-4))/PARETO_SIZE);
    	f1pareto[i]=f1;
    	f2pareto[i]=Math.exp(-Math.pow(2-Math.sqrt(-Math.log(f1)),2));
    	
    	// AAT to minimze optimum for comparison
        
    }
    XYSeries series=chart.addSeries("Pareto front", f1pareto, f2pareto);
    series.setMarkerColor(Color.RED);
    series.setMarker(SeriesMarkers.DIAMOND);
    
    series=chart.addSeries("Algorithm", new double[1], new double[1]);
    series.setMarkerColor(Color.BLUE);
    series.setMarker(SeriesMarkers.CIRCLE);
   
    sw=new SwingWrapper<>(chart);
    sw.displayChart();
  }
  
  private int BUFFER_SIZE=20;
  
  public void update(double x1, double x2) {
		f1.addFirst(x1);
		while (f1.size()>BUFFER_SIZE) f1.removeLast();
		f2.addFirst(x2);
		while (f2.size()>BUFFER_SIZE) f2.removeLast();
		chart.updateXYSeries("Algorithm",new ArrayList<Double>(f1),new ArrayList<Double>(f2),null);
		sw.repaintChart();
  }

}