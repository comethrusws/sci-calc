import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import java.util.List;

public class GraphPanel extends JPanel {
    private static final Color GRAPH_BG_COLOR = new Color(198, 208, 184);
    private static final Color AXIS_COLOR = new Color(40, 40, 40);
    private static final Color GRID_COLOR = new Color(150, 160, 140);
    private static final Color[] GRAPH_COLORS = {
        new Color(0, 0, 180),
        new Color(180, 0, 0),
        new Color(0, 140, 0),
        new Color(140, 0, 140),
        new Color(180, 100, 0),
        new Color(0, 140, 140)
    };
    
    private double xMin = -10;
    private double xMax = 10;
    private double yMin = -10;
    private double yMax = 10;
    private double xScale = 1;
    private double yScale = 1;
    
    private List<GraphFunction> functions;
    private Point tracePoint;
    private int traceFunctionIndex = 0;
    private double traceX = 0;
    private boolean traceMode = false;
    private CalculatorEngine engine;
    
    private JLabel coordinateLabel;
    
    public GraphPanel(CalculatorEngine engine) {
        this.engine = engine;
        this.functions = new ArrayList<>();
        initializePanel();
        setupMouseListeners();
    }
    
    private void initializePanel() {
        setBackground(GRAPH_BG_COLOR);
        setPreferredSize(new Dimension(300, 300));
        setLayout(new BorderLayout());
        
        coordinateLabel = new JLabel("X=0 Y=0");
        coordinateLabel.setFont(new Font("Monospaced", Font.PLAIN, 11));
        coordinateLabel.setForeground(Color.BLACK);
        coordinateLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(coordinateLabel, BorderLayout.SOUTH);
    }
    
    private void setupMouseListeners() {
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (!traceMode) {
                    double x = screenToGraphX(e.getX());
                    double y = screenToGraphY(e.getY());
                    coordinateLabel.setText(String.format("X=%.4f Y=%.4f", x, y));
                }
            }
        });
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (traceMode && !functions.isEmpty()) {
                    traceX = screenToGraphX(e.getX());
                    updateTrace();
                }
            }
        });
    }
    
    public void addFunction(String expression, String name) {
        int colorIndex = functions.size() % GRAPH_COLORS.length;
        functions.add(new GraphFunction(expression, name, GRAPH_COLORS[colorIndex]));
        repaint();
    }
    
    public void clearFunctions() {
        functions.clear();
        repaint();
    }
    
    public void removeFunction(int index) {
        if (index >= 0 && index < functions.size()) {
            functions.remove(index);
            repaint();
        }
    }
    
    public void setWindow(double xMin, double xMax, double yMin, double yMax) {
        this.xMin = xMin;
        this.xMax = xMax;
        this.yMin = yMin;
        this.yMax = yMax;
        repaint();
    }
    
    public void setScale(double xScale, double yScale) {
        this.xScale = xScale;
        this.yScale = yScale;
        repaint();
    }
    
    public void zoomIn() {
        double xCenter = (xMin + xMax) / 2;
        double yCenter = (yMin + yMax) / 2;
        double xRange = (xMax - xMin) / 4;
        double yRange = (yMax - yMin) / 4;
        xMin = xCenter - xRange;
        xMax = xCenter + xRange;
        yMin = yCenter - yRange;
        yMax = yCenter + yRange;
        repaint();
    }
    
    public void zoomOut() {
        double xCenter = (xMin + xMax) / 2;
        double yCenter = (yMin + yMax) / 2;
        double xRange = (xMax - xMin);
        double yRange = (yMax - yMin);
        xMin = xCenter - xRange;
        xMax = xCenter + xRange;
        yMin = yCenter - yRange;
        yMax = yCenter + yRange;
        repaint();
    }
    
    public void zoomStandard() {
        xMin = -10;
        xMax = 10;
        yMin = -10;
        yMax = 10;
        xScale = 1;
        yScale = 1;
        repaint();
    }
    
    public void zoomTrig() {
        xMin = -2 * Math.PI;
        xMax = 2 * Math.PI;
        yMin = -4;
        yMax = 4;
        xScale = Math.PI / 2;
        yScale = 1;
        repaint();
    }
    
    public void toggleTrace() {
        traceMode = !traceMode;
        if (traceMode && !functions.isEmpty()) {
            traceX = 0;
            updateTrace();
        }
        repaint();
    }
    
    public void traceLeft() {
        if (traceMode) {
            traceX -= (xMax - xMin) / 100;
            updateTrace();
        }
    }
    
    public void traceRight() {
        if (traceMode) {
            traceX += (xMax - xMin) / 100;
            updateTrace();
        }
    }
    
    public void nextFunction() {
        if (traceMode && !functions.isEmpty()) {
            traceFunctionIndex = (traceFunctionIndex + 1) % functions.size();
            updateTrace();
        }
    }
    
    private void updateTrace() {
        if (!functions.isEmpty()) {
            GraphFunction func = functions.get(traceFunctionIndex);
            double y = evaluateFunction(func.expression, traceX);
            coordinateLabel.setText(String.format("%s: X=%.4f Y=%.4f", 
                func.name, traceX, y));
            repaint();
        }
    }
    
    public double[] findZero(int functionIndex) {
        if (functionIndex < 0 || functionIndex >= functions.size()) {
            return null;
        }
        
        GraphFunction func = functions.get(functionIndex);
        double left = xMin;
        double right = xMax;
        double tolerance = 1e-10;
        int maxIterations = 100;
        
        double step = (right - left) / 1000;
        for (double x = left; x < right; x += step) {
            double y1 = evaluateFunction(func.expression, x);
            double y2 = evaluateFunction(func.expression, x + step);
            
            if (!Double.isNaN(y1) && !Double.isNaN(y2) && y1 * y2 <= 0) {
                double a = x;
                double b = x + step;
                
                for (int i = 0; i < maxIterations; i++) {
                    double mid = (a + b) / 2;
                    double fMid = evaluateFunction(func.expression, mid);
                    
                    if (Math.abs(fMid) < tolerance || (b - a) / 2 < tolerance) {
                        return new double[]{mid, fMid};
                    }
                    
                    if (evaluateFunction(func.expression, a) * fMid < 0) {
                        b = mid;
                    } else {
                        a = mid;
                    }
                }
                return new double[]{(a + b) / 2, evaluateFunction(func.expression, (a + b) / 2)};
            }
        }
        return null;
    }
    
    public double[] findMinimum(int functionIndex, double left, double right) {
        if (functionIndex < 0 || functionIndex >= functions.size()) {
            return null;
        }
        
        GraphFunction func = functions.get(functionIndex);
        double golden = (Math.sqrt(5) - 1) / 2;
        double tolerance = 1e-10;
        
        double x1 = right - golden * (right - left);
        double x2 = left + golden * (right - left);
        double f1 = evaluateFunction(func.expression, x1);
        double f2 = evaluateFunction(func.expression, x2);
        
        while (Math.abs(right - left) > tolerance) {
            if (f1 < f2) {
                right = x2;
                x2 = x1;
                f2 = f1;
                x1 = right - golden * (right - left);
                f1 = evaluateFunction(func.expression, x1);
            } else {
                left = x1;
                x1 = x2;
                f1 = f2;
                x2 = left + golden * (right - left);
                f2 = evaluateFunction(func.expression, x2);
            }
        }
        
        double minX = (left + right) / 2;
        return new double[]{minX, evaluateFunction(func.expression, minX)};
    }
    
    public double[] findMaximum(int functionIndex, double left, double right) {
        if (functionIndex < 0 || functionIndex >= functions.size()) {
            return null;
        }
        
        GraphFunction func = functions.get(functionIndex);
        double golden = (Math.sqrt(5) - 1) / 2;
        double tolerance = 1e-10;
        
        double x1 = right - golden * (right - left);
        double x2 = left + golden * (right - left);
        double f1 = evaluateFunction(func.expression, x1);
        double f2 = evaluateFunction(func.expression, x2);
        
        while (Math.abs(right - left) > tolerance) {
            if (f1 > f2) {
                right = x2;
                x2 = x1;
                f2 = f1;
                x1 = right - golden * (right - left);
                f1 = evaluateFunction(func.expression, x1);
            } else {
                left = x1;
                x1 = x2;
                f1 = f2;
                x2 = left + golden * (right - left);
                f2 = evaluateFunction(func.expression, x2);
            }
        }
        
        double maxX = (left + right) / 2;
        return new double[]{maxX, evaluateFunction(func.expression, maxX)};
    }
    
    public double calculateIntegral(int functionIndex, double a, double b) {
        if (functionIndex < 0 || functionIndex >= functions.size()) {
            return Double.NaN;
        }
        
        GraphFunction func = functions.get(functionIndex);
        int n = 1000;
        double h = (b - a) / n;
        double sum = 0;
        
        sum += evaluateFunction(func.expression, a);
        sum += evaluateFunction(func.expression, b);
        
        for (int i = 1; i < n; i++) {
            double x = a + i * h;
            double coefficient = (i % 2 == 0) ? 2 : 4;
            sum += coefficient * evaluateFunction(func.expression, x);
        }
        
        return sum * h / 3;
    }
    
    public double calculateDerivative(int functionIndex, double x) {
        if (functionIndex < 0 || functionIndex >= functions.size()) {
            return Double.NaN;
        }
        
        GraphFunction func = functions.get(functionIndex);
        double h = 1e-8;
        double f1 = evaluateFunction(func.expression, x - h);
        double f2 = evaluateFunction(func.expression, x + h);
        
        return (f2 - f1) / (2 * h);
    }
    
    public double[] findIntersection(int func1Index, int func2Index) {
        if (func1Index < 0 || func1Index >= functions.size() ||
            func2Index < 0 || func2Index >= functions.size()) {
            return null;
        }
        
        GraphFunction func1 = functions.get(func1Index);
        GraphFunction func2 = functions.get(func2Index);
        
        double step = (xMax - xMin) / 1000;
        for (double x = xMin; x < xMax; x += step) {
            double diff1 = evaluateFunction(func1.expression, x) - evaluateFunction(func2.expression, x);
            double diff2 = evaluateFunction(func1.expression, x + step) - evaluateFunction(func2.expression, x + step);
            
            if (!Double.isNaN(diff1) && !Double.isNaN(diff2) && diff1 * diff2 <= 0) {
                double a = x;
                double b = x + step;
                double tolerance = 1e-10;
                
                for (int i = 0; i < 100; i++) {
                    double mid = (a + b) / 2;
                    double fMid = evaluateFunction(func1.expression, mid) - evaluateFunction(func2.expression, mid);
                    
                    if (Math.abs(fMid) < tolerance) {
                        double y = evaluateFunction(func1.expression, mid);
                        return new double[]{mid, y};
                    }
                    
                    double fA = evaluateFunction(func1.expression, a) - evaluateFunction(func2.expression, a);
                    if (fA * fMid < 0) {
                        b = mid;
                    } else {
                        a = mid;
                    }
                }
            }
        }
        return null;
    }
    
    private double evaluateFunction(String expression, double x) {
        String expr = expression.replaceAll("(?i)x", "(" + x + ")");
        return engine.evaluateExpression(expr);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        drawGrid(g2d);
        drawAxes(g2d);
        drawFunctions(g2d);
        
        if (traceMode && !functions.isEmpty()) {
            drawTracePoint(g2d);
        }
        
        g2d.dispose();
    }
    
    private void drawGrid(Graphics2D g2d) {
        g2d.setColor(GRID_COLOR);
        g2d.setStroke(new BasicStroke(0.5f));
        
        for (double x = Math.ceil(xMin / xScale) * xScale; x <= xMax; x += xScale) {
            int screenX = graphToScreenX(x);
            g2d.drawLine(screenX, 0, screenX, getHeight() - 20);
        }
        
        for (double y = Math.ceil(yMin / yScale) * yScale; y <= yMax; y += yScale) {
            int screenY = graphToScreenY(y);
            g2d.drawLine(0, screenY, getWidth(), screenY);
        }
    }
    
    private void drawAxes(Graphics2D g2d) {
        g2d.setColor(AXIS_COLOR);
        g2d.setStroke(new BasicStroke(1.5f));
        
        int originX = graphToScreenX(0);
        int originY = graphToScreenY(0);
        
        if (originX >= 0 && originX <= getWidth()) {
            g2d.drawLine(originX, 0, originX, getHeight() - 20);
        }
        
        if (originY >= 0 && originY <= getHeight() - 20) {
            g2d.drawLine(0, originY, getWidth(), originY);
        }
        
        g2d.setFont(new Font("Monospaced", Font.PLAIN, 9));
        for (double x = Math.ceil(xMin / xScale) * xScale; x <= xMax; x += xScale) {
            if (Math.abs(x) > 0.001) {
                int screenX = graphToScreenX(x);
                g2d.drawLine(screenX, originY - 3, screenX, originY + 3);
                String label = formatAxisLabel(x);
                g2d.drawString(label, screenX - 10, originY + 12);
            }
        }
        
        for (double y = Math.ceil(yMin / yScale) * yScale; y <= yMax; y += yScale) {
            if (Math.abs(y) > 0.001) {
                int screenY = graphToScreenY(y);
                g2d.drawLine(originX - 3, screenY, originX + 3, screenY);
                String label = formatAxisLabel(y);
                g2d.drawString(label, originX + 5, screenY + 4);
            }
        }
    }
    
    private String formatAxisLabel(double value) {
        if (Math.abs(value - Math.round(value)) < 0.001) {
            return String.valueOf((int) Math.round(value));
        }
        return String.format("%.1f", value);
    }
    
    private void drawFunctions(Graphics2D g2d) {
        g2d.setStroke(new BasicStroke(2f));
        
        for (GraphFunction func : functions) {
            g2d.setColor(func.color);
            
            GeneralPath path = new GeneralPath();
            boolean started = false;
            double prevY = Double.NaN;
            
            for (int screenX = 0; screenX < getWidth(); screenX++) {
                double x = screenToGraphX(screenX);
                double y = evaluateFunction(func.expression, x);
                
                if (!Double.isNaN(y) && !Double.isInfinite(y) && y >= yMin - 10 && y <= yMax + 10) {
                    int screenY = graphToScreenY(y);
                    
                    if (!started) {
                        path.moveTo(screenX, screenY);
                        started = true;
                    } else {
                        if (!Double.isNaN(prevY) && Math.abs(y - prevY) < (yMax - yMin)) {
                            path.lineTo(screenX, screenY);
                        } else {
                            path.moveTo(screenX, screenY);
                        }
                    }
                    prevY = y;
                } else {
                    started = false;
                    prevY = Double.NaN;
                }
            }
            
            g2d.draw(path);
        }
    }
    
    private void drawTracePoint(Graphics2D g2d) {
        if (traceFunctionIndex < functions.size()) {
            GraphFunction func = functions.get(traceFunctionIndex);
            double y = evaluateFunction(func.expression, traceX);
            
            if (!Double.isNaN(y) && !Double.isInfinite(y)) {
                int screenX = graphToScreenX(traceX);
                int screenY = graphToScreenY(y);
                
                g2d.setColor(func.color);
                g2d.fillOval(screenX - 5, screenY - 5, 10, 10);
                g2d.setColor(Color.WHITE);
                g2d.fillOval(screenX - 3, screenY - 3, 6, 6);
            }
        }
    }
    
    private int graphToScreenX(double x) {
        return (int) ((x - xMin) / (xMax - xMin) * getWidth());
    }
    
    private int graphToScreenY(double y) {
        return (int) ((yMax - y) / (yMax - yMin) * (getHeight() - 20));
    }
    
    private double screenToGraphX(int screenX) {
        return xMin + (double) screenX / getWidth() * (xMax - xMin);
    }
    
    private double screenToGraphY(int screenY) {
        return yMax - (double) screenY / (getHeight() - 20) * (yMax - yMin);
    }
    
    public List<GraphFunction> getFunctions() {
        return functions;
    }
    
    public double getXMin() { return xMin; }
    public double getXMax() { return xMax; }
    public double getYMin() { return yMin; }
    public double getYMax() { return yMax; }
}

class GraphFunction {
    String expression;
    String name;
    Color color;
    
    public GraphFunction(String expression, String name, Color color) {
        this.expression = expression;
        this.name = name;
        this.color = color;
    }
}
