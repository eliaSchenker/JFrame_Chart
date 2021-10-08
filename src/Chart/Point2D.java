package Chart;
/**
 * Custom Chart.Point2D Klasse aufgrund der Int-Limitation in dem normalen Point
 */
class Point2D {
    public long x;
    public long y;

    public Point2D(long x, long y) {
        this.x = x;
        this.y = y;
    }
}