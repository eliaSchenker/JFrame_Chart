package Examples;

import javax.swing.*;
import java.awt.*;
import Chart.*;

public class Example extends JFrame {

    public Example() {
        super("Test");
        Chart chart = new Chart();
        chart.setSize(1000, 500);
        chart.xRand = 100;
        chart.yRand = 100;
        chart.yAxisDisplayStep = 10;
        DataAxis ax1 = new DataAxis(Color.red, "Line 1");
        DataAxis ax2 = new DataAxis(Color.blue, "Line 2");
        ax1.addEntry(new DataEntry(100000000, 10));
        ax1.addEntry(new DataEntry(200000000, 60));
        ax1.addEntry(new DataEntry(300000000, 30));
        ax1.addEntry(new DataEntry(400000000, 10));
        ax1.addEntry(new DataEntry(500000000, 15));
        ax1.addEntry(new DataEntry(600000000, 20));
        ax1.addEntry(new DataEntry(700000000, 100));
        chart.addAxis(ax1);
        chart.renderFrame();
        setLayout(new BorderLayout());
        setSize(500, 500);

        add(chart, BorderLayout.CENTER);
        setVisible(true);
    }

    public static void main(String[] args) {
        new Example();
    }
}
