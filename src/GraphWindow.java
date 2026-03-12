import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class GraphWindow extends JFrame {
    private static final Color BG_COLOR = new Color(30, 30, 30);
    private static final Color TEXT_COLOR = Color.WHITE;
    
    private GraphPanel graphPanel;
    private CalculatorEngine engine;
    private JPanel functionListPanel;
    private JTextField[] functionFields;
    private int functionCount = 6;
    
    public GraphWindow(CalculatorEngine engine) {
        this.engine = engine;
        this.graphPanel = new GraphPanel(engine);
        this.functionFields = new JTextField[functionCount];
        
        initializeFrame();
        createComponents();
        setVisible(true);
    }
    
    private void initializeFrame() {
        setTitle("Graph - Y= Editor");
        setSize(700, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_COLOR);
        setLayout(new BorderLayout(5, 5));
    }
    
    private void createComponents() {
        JPanel leftPanel = createFunctionInputPanel();
        JPanel rightPanel = createGraphDisplayPanel();
        JPanel bottomPanel = createControlPanel();
        
        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createFunctionInputPanel() {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(200, 0));
        panel.setBackground(BG_COLOR);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel title = new JLabel("Y= Functions");
        title.setForeground(TEXT_COLOR);
        title.setFont(new Font("Monospaced", Font.BOLD, 14));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(title);
        panel.add(Box.createVerticalStrut(10));
        
        String[] colors = {"Blue", "Red", "Green", "Purple", "Orange", "Cyan"};
        
        for (int i = 0; i < functionCount; i++) {
            JPanel row = new JPanel(new BorderLayout(5, 0));
            row.setBackground(BG_COLOR);
            row.setMaximumSize(new Dimension(180, 30));
            row.setAlignmentX(Component.LEFT_ALIGNMENT);
            
            JLabel label = new JLabel("Y" + (i + 1) + "=");
            label.setForeground(getColorForIndex(i));
            label.setFont(new Font("Monospaced", Font.BOLD, 12));
            
            functionFields[i] = new JTextField();
            functionFields[i].setFont(new Font("Monospaced", Font.PLAIN, 11));
            functionFields[i].setBackground(new Color(50, 50, 50));
            functionFields[i].setForeground(TEXT_COLOR);
            functionFields[i].setCaretColor(TEXT_COLOR);
            functionFields[i].setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80)));
            
            row.add(label, BorderLayout.WEST);
            row.add(functionFields[i], BorderLayout.CENTER);
            
            panel.add(row);
            panel.add(Box.createVerticalStrut(5));
        }
        
        panel.add(Box.createVerticalStrut(10));
        
        JButton graphButton = createStyledButton("GRAPH");
        graphButton.addActionListener(e -> plotFunctions());
        panel.add(graphButton);
        
        panel.add(Box.createVerticalStrut(5));
        
        JButton clearButton = createStyledButton("CLEAR ALL");
        clearButton.addActionListener(e -> {
            for (JTextField field : functionFields) {
                field.setText("");
            }
            graphPanel.clearFunctions();
        });
        panel.add(clearButton);
        
        return panel;
    }
    
    private Color getColorForIndex(int index) {
        Color[] colors = {
            new Color(100, 150, 255),
            new Color(255, 100, 100),
            new Color(100, 255, 100),
            new Color(200, 100, 255),
            new Color(255, 180, 100),
            new Color(100, 255, 255)
        };
        return colors[index % colors.length];
    }
    
    private JPanel createGraphDisplayPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 10));
        
        panel.add(graphPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        panel.setBackground(BG_COLOR);
        
        JButton zoomIn = createStyledButton("ZOOM IN");
        zoomIn.addActionListener(e -> graphPanel.zoomIn());
        
        JButton zoomOut = createStyledButton("ZOOM OUT");
        zoomOut.addActionListener(e -> graphPanel.zoomOut());
        
        JButton zoomStd = createStyledButton("STANDARD");
        zoomStd.addActionListener(e -> graphPanel.zoomStandard());
        
        JButton zoomTrig = createStyledButton("ZTRIG");
        zoomTrig.addActionListener(e -> graphPanel.zoomTrig());
        
        JButton trace = createStyledButton("TRACE");
        trace.addActionListener(e -> graphPanel.toggleTrace());
        
        JButton window = createStyledButton("WINDOW");
        window.addActionListener(e -> showWindowDialog());
        
        JButton calc = createStyledButton("CALC");
        calc.addActionListener(e -> showCalcMenu());
        
        panel.add(zoomIn);
        panel.add(zoomOut);
        panel.add(zoomStd);
        panel.add(zoomTrig);
        panel.add(trace);
        panel.add(window);
        panel.add(calc);
        
        return panel;
    }
    
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Monospaced", Font.BOLD, 10));
        button.setForeground(TEXT_COLOR);
        button.setBackground(new Color(60, 60, 60));
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 80)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(80, 80, 80));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(60, 60, 60));
            }
        });
        
        return button;
    }
    
    private void plotFunctions() {
        graphPanel.clearFunctions();
        
        for (int i = 0; i < functionCount; i++) {
            String expr = functionFields[i].getText().trim();
            if (!expr.isEmpty()) {
                graphPanel.addFunction(expr, "Y" + (i + 1));
            }
        }
    }
    
    private void showWindowDialog() {
        JDialog dialog = new JDialog(this, "Window Settings", true);
        dialog.setSize(250, 300);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(BG_COLOR);
        dialog.setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        String[] labels = {"Xmin:", "Xmax:", "Xscl:", "Ymin:", "Ymax:", "Yscl:"};
        double[] values = {graphPanel.getXMin(), graphPanel.getXMax(), 1, 
                          graphPanel.getYMin(), graphPanel.getYMax(), 1};
        JTextField[] fields = new JTextField[6];
        
        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i;
            JLabel label = new JLabel(labels[i]);
            label.setForeground(TEXT_COLOR);
            dialog.add(label, gbc);
            
            gbc.gridx = 1;
            fields[i] = new JTextField(String.valueOf(values[i]), 10);
            fields[i].setBackground(new Color(50, 50, 50));
            fields[i].setForeground(TEXT_COLOR);
            fields[i].setCaretColor(TEXT_COLOR);
            dialog.add(fields[i], gbc);
        }
        
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        JButton applyBtn = createStyledButton("APPLY");
        applyBtn.addActionListener(e -> {
            try {
                double xMin = Double.parseDouble(fields[0].getText());
                double xMax = Double.parseDouble(fields[1].getText());
                double xScl = Double.parseDouble(fields[2].getText());
                double yMin = Double.parseDouble(fields[3].getText());
                double yMax = Double.parseDouble(fields[4].getText());
                double yScl = Double.parseDouble(fields[5].getText());
                
                graphPanel.setWindow(xMin, xMax, yMin, yMax);
                graphPanel.setScale(xScl, yScl);
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid input", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        dialog.add(applyBtn, gbc);
        
        dialog.setVisible(true);
    }
    
    private void showCalcMenu() {
        JPopupMenu menu = new JPopupMenu();
        menu.setBackground(BG_COLOR);
        
        String[] options = {"1: value", "2: zero", "3: minimum", "4: maximum", 
                           "5: intersect", "6: dy/dx", "7: ∫f(x)dx"};
        
        for (int i = 0; i < options.length; i++) {
            final int index = i;
            JMenuItem item = new JMenuItem(options[i]);
            item.setBackground(BG_COLOR);
            item.setForeground(TEXT_COLOR);
            item.addActionListener(e -> handleCalcOption(index));
            menu.add(item);
        }
        
        menu.show(this, getWidth() / 2, getHeight() - 100);
    }
    
    private void handleCalcOption(int option) {
        List<GraphFunction> functions = graphPanel.getFunctions();
        if (functions.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No functions graphed", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        switch (option) {
            case 0:
                String xValStr = JOptionPane.showInputDialog(this, "X value:");
                if (xValStr != null) {
                    try {
                        double x = Double.parseDouble(xValStr);
                        StringBuilder result = new StringBuilder();
                        for (int i = 0; i < functions.size(); i++) {
                            String expr = functions.get(i).expression.replaceAll("(?i)x", "(" + x + ")");
                            double y = engine.evaluateExpression(expr);
                            result.append(functions.get(i).name).append(": ").append(y).append("\n");
                        }
                        JOptionPane.showMessageDialog(this, result.toString(), "Values at X=" + x, JOptionPane.INFORMATION_MESSAGE);
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(this, "Invalid input", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
                break;
                
            case 1:
                double[] zero = graphPanel.findZero(0);
                if (zero != null) {
                    JOptionPane.showMessageDialog(this, 
                        String.format("Zero found at X=%.6f", zero[0]),
                        "Zero", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "No zero found in window", "Zero", JOptionPane.WARNING_MESSAGE);
                }
                break;
                
            case 2:
                double[] min = graphPanel.findMinimum(0, graphPanel.getXMin(), graphPanel.getXMax());
                if (min != null) {
                    JOptionPane.showMessageDialog(this,
                        String.format("Minimum at X=%.6f, Y=%.6f", min[0], min[1]),
                        "Minimum", JOptionPane.INFORMATION_MESSAGE);
                }
                break;
                
            case 3:
                double[] max = graphPanel.findMaximum(0, graphPanel.getXMin(), graphPanel.getXMax());
                if (max != null) {
                    JOptionPane.showMessageDialog(this,
                        String.format("Maximum at X=%.6f, Y=%.6f", max[0], max[1]),
                        "Maximum", JOptionPane.INFORMATION_MESSAGE);
                }
                break;
                
            case 4:
                if (functions.size() >= 2) {
                    double[] intersect = graphPanel.findIntersection(0, 1);
                    if (intersect != null) {
                        JOptionPane.showMessageDialog(this,
                            String.format("Intersection at X=%.6f, Y=%.6f", intersect[0], intersect[1]),
                            "Intersect", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "No intersection found", "Intersect", JOptionPane.WARNING_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Need at least 2 functions", "Intersect", JOptionPane.WARNING_MESSAGE);
                }
                break;
                
            case 5:
                String dxStr = JOptionPane.showInputDialog(this, "X value for derivative:");
                if (dxStr != null) {
                    try {
                        double x = Double.parseDouble(dxStr);
                        double deriv = graphPanel.calculateDerivative(0, x);
                        JOptionPane.showMessageDialog(this,
                            String.format("dy/dx at X=%.6f is %.6f", x, deriv),
                            "Derivative", JOptionPane.INFORMATION_MESSAGE);
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(this, "Invalid input", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
                break;
                
            case 6:
                JPanel integralPanel = new JPanel(new GridLayout(2, 2, 5, 5));
                JTextField lowerField = new JTextField();
                JTextField upperField = new JTextField();
                integralPanel.add(new JLabel("Lower bound:"));
                integralPanel.add(lowerField);
                integralPanel.add(new JLabel("Upper bound:"));
                integralPanel.add(upperField);
                
                int result = JOptionPane.showConfirmDialog(this, integralPanel, 
                    "Integration Bounds", JOptionPane.OK_CANCEL_OPTION);
                
                if (result == JOptionPane.OK_OPTION) {
                    try {
                        double lower = Double.parseDouble(lowerField.getText());
                        double upper = Double.parseDouble(upperField.getText());
                        double integral = graphPanel.calculateIntegral(0, lower, upper);
                        JOptionPane.showMessageDialog(this,
                            String.format("∫f(x)dx from %.2f to %.2f = %.6f", lower, upper, integral),
                            "Integral", JOptionPane.INFORMATION_MESSAGE);
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(this, "Invalid input", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
                break;
        }
    }
    
    public GraphPanel getGraphPanel() {
        return graphPanel;
    }
}
