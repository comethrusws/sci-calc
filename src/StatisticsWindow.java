import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class StatisticsWindow extends JFrame {
    private static final Color BG_COLOR = new Color(30, 30, 30);
    private static final Color TEXT_COLOR = Color.WHITE;
    private static final Color DISPLAY_BG = new Color(198, 208, 184);
    
    private StatisticsOperations statOps;
    private JTextArea dataInputArea;
    private JTextArea dataInputArea2;
    private JTextArea resultArea;
    
    public StatisticsWindow() {
        this.statOps = new StatisticsOperations();
        
        initializeFrame();
        createComponents();
        setVisible(true);
    }
    
    private void initializeFrame() {
        setTitle("Statistics");
        setSize(650, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_COLOR);
        setLayout(new BorderLayout(5, 5));
    }
    
    private void createComponents() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(BG_COLOR);
        tabbedPane.setForeground(TEXT_COLOR);
        
        tabbedPane.addTab("1-Var Stats", createOneVarPanel());
        tabbedPane.addTab("2-Var Stats", createTwoVarPanel());
        tabbedPane.addTab("Regression", createRegressionPanel());
        tabbedPane.addTab("Probability", createProbabilityPanel());
        tabbedPane.addTab("Distributions", createDistributionsPanel());
        
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        resultArea.setBackground(DISPLAY_BG);
        resultArea.setForeground(Color.BLACK);
        resultArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setPreferredSize(new Dimension(0, 180));
        scrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 80)),
            "Results", 0, 0, null, TEXT_COLOR
        ));
        
        add(tabbedPane, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.SOUTH);
    }
    
    private JPanel createOneVarPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BG_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBackground(BG_COLOR);
        inputPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 80)),
            "Data (comma or newline separated)", 0, 0, null, TEXT_COLOR
        ));
        
        dataInputArea = new JTextArea(8, 30);
        dataInputArea.setBackground(new Color(50, 50, 50));
        dataInputArea.setForeground(TEXT_COLOR);
        dataInputArea.setCaretColor(TEXT_COLOR);
        dataInputArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        inputPanel.add(new JScrollPane(dataInputArea), BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        buttonPanel.setBackground(BG_COLOR);
        
        JButton calcStats = createStyledButton("1-Var Stats");
        calcStats.addActionListener(e -> calculateOneVarStats());
        
        JButton calcSummary = createStyledButton("5-Num Summary");
        calcSummary.addActionListener(e -> calculateFiveNumSummary());
        
        JButton sortAsc = createStyledButton("Sort Asc");
        sortAsc.addActionListener(e -> sortData(true));
        
        JButton sortDesc = createStyledButton("Sort Desc");
        sortDesc.addActionListener(e -> sortData(false));
        
        buttonPanel.add(calcStats);
        buttonPanel.add(calcSummary);
        buttonPanel.add(sortAsc);
        buttonPanel.add(sortDesc);
        
        panel.add(inputPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createTwoVarPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BG_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel inputPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        inputPanel.setBackground(BG_COLOR);
        
        JPanel xPanel = new JPanel(new BorderLayout());
        xPanel.setBackground(BG_COLOR);
        xPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 80)),
            "X Data", 0, 0, null, TEXT_COLOR
        ));
        
        dataInputArea = new JTextArea(8, 15);
        dataInputArea.setBackground(new Color(50, 50, 50));
        dataInputArea.setForeground(TEXT_COLOR);
        dataInputArea.setCaretColor(TEXT_COLOR);
        dataInputArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        xPanel.add(new JScrollPane(dataInputArea), BorderLayout.CENTER);
        
        JPanel yPanel = new JPanel(new BorderLayout());
        yPanel.setBackground(BG_COLOR);
        yPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 80)),
            "Y Data", 0, 0, null, TEXT_COLOR
        ));
        
        dataInputArea2 = new JTextArea(8, 15);
        dataInputArea2.setBackground(new Color(50, 50, 50));
        dataInputArea2.setForeground(TEXT_COLOR);
        dataInputArea2.setCaretColor(TEXT_COLOR);
        dataInputArea2.setFont(new Font("Monospaced", Font.PLAIN, 12));
        yPanel.add(new JScrollPane(dataInputArea2), BorderLayout.CENTER);
        
        inputPanel.add(xPanel);
        inputPanel.add(yPanel);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        buttonPanel.setBackground(BG_COLOR);
        
        JButton calcStats = createStyledButton("2-Var Stats");
        calcStats.addActionListener(e -> calculateTwoVarStats());
        
        JButton calcCorr = createStyledButton("Correlation");
        calcCorr.addActionListener(e -> calculateCorrelation());
        
        buttonPanel.add(calcStats);
        buttonPanel.add(calcCorr);
        
        panel.add(inputPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createRegressionPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BG_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel inputPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        inputPanel.setBackground(BG_COLOR);
        
        JPanel xPanel = new JPanel(new BorderLayout());
        xPanel.setBackground(BG_COLOR);
        xPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 80)),
            "X Data", 0, 0, null, TEXT_COLOR
        ));
        
        JTextArea xArea = new JTextArea(6, 15);
        xArea.setBackground(new Color(50, 50, 50));
        xArea.setForeground(TEXT_COLOR);
        xArea.setCaretColor(TEXT_COLOR);
        xArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        xPanel.add(new JScrollPane(xArea), BorderLayout.CENTER);
        
        JPanel yPanel = new JPanel(new BorderLayout());
        yPanel.setBackground(BG_COLOR);
        yPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 80)),
            "Y Data", 0, 0, null, TEXT_COLOR
        ));
        
        JTextArea yArea = new JTextArea(6, 15);
        yArea.setBackground(new Color(50, 50, 50));
        yArea.setForeground(TEXT_COLOR);
        yArea.setCaretColor(TEXT_COLOR);
        yArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        yPanel.add(new JScrollPane(yArea), BorderLayout.CENTER);
        
        inputPanel.add(xPanel);
        inputPanel.add(yPanel);
        
        JPanel buttonPanel = new JPanel(new GridLayout(2, 3, 5, 5));
        buttonPanel.setBackground(BG_COLOR);
        
        JButton linReg = createStyledButton("LinReg(ax+b)");
        linReg.addActionListener(e -> {
            double[] x = parseData(xArea.getText());
            double[] y = parseData(yArea.getText());
            if (x.length == y.length && x.length > 1) {
                double[] result = statOps.linearRegression(x, y);
                resultArea.setText(String.format(
                    "Linear Regression: y = ax + b\n\n" +
                    "a (slope) = %.6f\n" +
                    "b (intercept) = %.6f\n" +
                    "r (correlation) = %.6f\n" +
                    "r² = %.6f\n\n" +
                    "Equation: y = %.4fx + %.4f",
                    result[0], result[1], result[2], result[3],
                    result[0], result[1]
                ));
            } else {
                resultArea.setText("Error: X and Y must have same length (>1)");
            }
        });
        
        JButton quadReg = createStyledButton("QuadReg");
        quadReg.addActionListener(e -> {
            double[] x = parseData(xArea.getText());
            double[] y = parseData(yArea.getText());
            if (x.length == y.length && x.length > 2) {
                double[] result = statOps.quadraticRegression(x, y);
                if (result != null) {
                    resultArea.setText(String.format(
                        "Quadratic Regression: y = ax² + bx + c\n\n" +
                        "a = %.6f\n" +
                        "b = %.6f\n" +
                        "c = %.6f\n" +
                        "R² = %.6f\n\n" +
                        "Equation: y = %.4fx² + %.4fx + %.4f",
                        result[0], result[1], result[2], result[3],
                        result[0], result[1], result[2]
                    ));
                } else {
                    resultArea.setText("Error calculating regression");
                }
            } else {
                resultArea.setText("Error: X and Y must have same length (>2)");
            }
        });
        
        JButton expReg = createStyledButton("ExpReg");
        expReg.addActionListener(e -> {
            double[] x = parseData(xArea.getText());
            double[] y = parseData(yArea.getText());
            if (x.length == y.length && x.length > 1) {
                double[] result = statOps.exponentialRegression(x, y);
                if (result != null) {
                    resultArea.setText(String.format(
                        "Exponential Regression: y = a·eᵇˣ\n\n" +
                        "a = %.6f\n" +
                        "b = %.6f\n" +
                        "r = %.6f\n" +
                        "r² = %.6f\n\n" +
                        "Equation: y = %.4f·e^(%.4fx)",
                        result[0], result[1], result[2], result[3],
                        result[0], result[1]
                    ));
                } else {
                    resultArea.setText("Error: Y values must be positive for ExpReg");
                }
            } else {
                resultArea.setText("Error: X and Y must have same length (>1)");
            }
        });
        
        buttonPanel.add(linReg);
        buttonPanel.add(quadReg);
        buttonPanel.add(expReg);
        
        panel.add(inputPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createProbabilityPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(BG_COLOR);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel factPanel = createSubPanel("Factorial & Permutations");
        JPanel factRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        factRow.setBackground(BG_COLOR);
        
        JTextField nField = createInputField();
        JTextField rField = createInputField();
        
        factRow.add(createLabel("n:"));
        factRow.add(nField);
        factRow.add(createLabel("r:"));
        factRow.add(rField);
        factPanel.add(factRow);
        
        JPanel factBtnRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        factBtnRow.setBackground(BG_COLOR);
        
        JButton factBtn = createStyledButton("n!");
        factBtn.addActionListener(e -> {
            try {
                int n = Integer.parseInt(nField.getText());
                long result = statOps.factorial(n);
                resultArea.setText(n + "! = " + result);
            } catch (Exception ex) {
                resultArea.setText("Invalid input");
            }
        });
        
        JButton permBtn = createStyledButton("nPr");
        permBtn.addActionListener(e -> {
            try {
                int n = Integer.parseInt(nField.getText());
                int r = Integer.parseInt(rField.getText());
                long result = statOps.permutation(n, r);
                resultArea.setText(n + "P" + r + " = " + result);
            } catch (Exception ex) {
                resultArea.setText("Invalid input");
            }
        });
        
        JButton combBtn = createStyledButton("nCr");
        combBtn.addActionListener(e -> {
            try {
                int n = Integer.parseInt(nField.getText());
                int r = Integer.parseInt(rField.getText());
                long result = statOps.combination(n, r);
                resultArea.setText(n + "C" + r + " = " + result);
            } catch (Exception ex) {
                resultArea.setText("Invalid input");
            }
        });
        
        factBtnRow.add(factBtn);
        factBtnRow.add(permBtn);
        factBtnRow.add(combBtn);
        factPanel.add(factBtnRow);
        panel.add(factPanel);
        
        panel.add(Box.createVerticalStrut(10));
        
        JPanel binomPanel = createSubPanel("Binomial Distribution");
        JPanel binomRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        binomRow.setBackground(BG_COLOR);
        
        JTextField binomN = createInputField();
        JTextField binomK = createInputField();
        JTextField binomP = createInputField();
        
        binomRow.add(createLabel("n:"));
        binomRow.add(binomN);
        binomRow.add(createLabel("k:"));
        binomRow.add(binomK);
        binomRow.add(createLabel("p:"));
        binomRow.add(binomP);
        binomPanel.add(binomRow);
        
        JPanel binomBtnRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        binomBtnRow.setBackground(BG_COLOR);
        
        JButton binomPdfBtn = createStyledButton("binompdf");
        binomPdfBtn.addActionListener(e -> {
            try {
                int n = Integer.parseInt(binomN.getText());
                int k = Integer.parseInt(binomK.getText());
                double p = Double.parseDouble(binomP.getText());
                double result = statOps.binomialPdf(n, k, p);
                resultArea.setText(String.format("P(X = %d) = %.10f", k, result));
            } catch (Exception ex) {
                resultArea.setText("Invalid input");
            }
        });
        
        JButton binomCdfBtn = createStyledButton("binomcdf");
        binomCdfBtn.addActionListener(e -> {
            try {
                int n = Integer.parseInt(binomN.getText());
                int k = Integer.parseInt(binomK.getText());
                double p = Double.parseDouble(binomP.getText());
                double result = statOps.binomialCdf(n, k, p);
                resultArea.setText(String.format("P(X ≤ %d) = %.10f", k, result));
            } catch (Exception ex) {
                resultArea.setText("Invalid input");
            }
        });
        
        binomBtnRow.add(binomPdfBtn);
        binomBtnRow.add(binomCdfBtn);
        binomPanel.add(binomBtnRow);
        panel.add(binomPanel);
        
        return panel;
    }
    
    private JPanel createDistributionsPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(BG_COLOR);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel normalPanel = createSubPanel("Normal Distribution");
        JPanel normalRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        normalRow.setBackground(BG_COLOR);
        
        JTextField meanField = createInputField();
        meanField.setText("0");
        JTextField stdField = createInputField();
        stdField.setText("1");
        JTextField xField = createInputField();
        
        normalRow.add(createLabel("μ:"));
        normalRow.add(meanField);
        normalRow.add(createLabel("σ:"));
        normalRow.add(stdField);
        normalRow.add(createLabel("x:"));
        normalRow.add(xField);
        normalPanel.add(normalRow);
        
        JPanel normalBtnRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        normalBtnRow.setBackground(BG_COLOR);
        
        JButton normPdfBtn = createStyledButton("normalpdf");
        normPdfBtn.addActionListener(e -> {
            try {
                double mean = Double.parseDouble(meanField.getText());
                double std = Double.parseDouble(stdField.getText());
                double x = Double.parseDouble(xField.getText());
                double result = statOps.normalPdf(x, mean, std);
                resultArea.setText(String.format("f(%.4f) = %.10f", x, result));
            } catch (Exception ex) {
                resultArea.setText("Invalid input");
            }
        });
        
        JButton normCdfBtn = createStyledButton("normalcdf");
        normCdfBtn.addActionListener(e -> {
            try {
                double mean = Double.parseDouble(meanField.getText());
                double std = Double.parseDouble(stdField.getText());
                double x = Double.parseDouble(xField.getText());
                double result = statOps.normalCdf(x, mean, std);
                resultArea.setText(String.format("P(X ≤ %.4f) = %.10f", x, result));
            } catch (Exception ex) {
                resultArea.setText("Invalid input");
            }
        });
        
        normalBtnRow.add(normPdfBtn);
        normalBtnRow.add(normCdfBtn);
        normalPanel.add(normalBtnRow);
        panel.add(normalPanel);
        
        panel.add(Box.createVerticalStrut(10));
        
        JPanel invNormPanel = createSubPanel("Inverse Normal");
        JPanel invNormRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        invNormRow.setBackground(BG_COLOR);
        
        JTextField invMeanField = createInputField();
        invMeanField.setText("0");
        JTextField invStdField = createInputField();
        invStdField.setText("1");
        JTextField probField = createInputField();
        
        invNormRow.add(createLabel("μ:"));
        invNormRow.add(invMeanField);
        invNormRow.add(createLabel("σ:"));
        invNormRow.add(invStdField);
        invNormRow.add(createLabel("Area:"));
        invNormRow.add(probField);
        invNormPanel.add(invNormRow);
        
        JButton invNormBtn = createStyledButton("invNorm");
        invNormBtn.addActionListener(e -> {
            try {
                double mean = Double.parseDouble(invMeanField.getText());
                double std = Double.parseDouble(invStdField.getText());
                double prob = Double.parseDouble(probField.getText());
                double result = statOps.invNorm(prob, mean, std);
                resultArea.setText(String.format("x such that P(X ≤ x) = %.4f:\nx = %.10f", prob, result));
            } catch (Exception ex) {
                resultArea.setText("Invalid input");
            }
        });
        invNormPanel.add(invNormBtn);
        panel.add(invNormPanel);
        
        return panel;
    }
    
    private void calculateOneVarStats() {
        double[] data = parseData(dataInputArea.getText());
        if (data.length == 0) {
            resultArea.setText("No valid data");
            return;
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("1-Variable Statistics\n");
        sb.append("═══════════════════════\n\n");
        sb.append(String.format("n = %d\n", data.length));
        sb.append(String.format("x̄ (mean) = %.6f\n", statOps.mean(data)));
        sb.append(String.format("Σx = %.6f\n", statOps.sum(data)));
        sb.append(String.format("Σx² = %.6f\n", statOps.sumOfSquares(data)));
        sb.append(String.format("Sx (sample std) = %.6f\n", statOps.standardDeviation(data, false)));
        sb.append(String.format("σx (pop std) = %.6f\n", statOps.standardDeviation(data, true)));
        sb.append(String.format("minX = %.6f\n", statOps.min(data)));
        sb.append(String.format("maxX = %.6f\n", statOps.max(data)));
        sb.append(String.format("range = %.6f\n", statOps.range(data)));
        sb.append(String.format("median = %.6f\n", statOps.median(data)));
        
        double[] modes = statOps.mode(data);
        sb.append("mode = ");
        for (int i = 0; i < modes.length && i < 5; i++) {
            sb.append(modes[i]);
            if (i < modes.length - 1 && i < 4) sb.append(", ");
        }
        if (modes.length > 5) sb.append("...");
        sb.append("\n");
        
        resultArea.setText(sb.toString());
    }
    
    private void calculateFiveNumSummary() {
        double[] data = parseData(dataInputArea.getText());
        if (data.length == 0) {
            resultArea.setText("No valid data");
            return;
        }
        
        double[] summary = statOps.fiveNumberSummary(data);
        
        StringBuilder sb = new StringBuilder();
        sb.append("5-Number Summary\n");
        sb.append("═══════════════════════\n\n");
        sb.append(String.format("Min = %.6f\n", summary[0]));
        sb.append(String.format("Q1 = %.6f\n", summary[1]));
        sb.append(String.format("Median = %.6f\n", summary[2]));
        sb.append(String.format("Q3 = %.6f\n", summary[3]));
        sb.append(String.format("Max = %.6f\n", summary[4]));
        sb.append(String.format("\nIQR = %.6f\n", statOps.iqr(data)));
        
        resultArea.setText(sb.toString());
    }
    
    private void calculateTwoVarStats() {
        double[] x = parseData(dataInputArea.getText());
        double[] y = parseData(dataInputArea2.getText());
        
        if (x.length != y.length || x.length == 0) {
            resultArea.setText("Error: X and Y must have same non-zero length");
            return;
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("2-Variable Statistics\n");
        sb.append("═══════════════════════\n\n");
        sb.append(String.format("n = %d\n\n", x.length));
        sb.append("X Statistics:\n");
        sb.append(String.format("  x̄ = %.6f\n", statOps.mean(x)));
        sb.append(String.format("  Σx = %.6f\n", statOps.sum(x)));
        sb.append(String.format("  Σx² = %.6f\n", statOps.sumOfSquares(x)));
        sb.append(String.format("  Sx = %.6f\n\n", statOps.standardDeviation(x, false)));
        sb.append("Y Statistics:\n");
        sb.append(String.format("  ȳ = %.6f\n", statOps.mean(y)));
        sb.append(String.format("  Σy = %.6f\n", statOps.sum(y)));
        sb.append(String.format("  Σy² = %.6f\n", statOps.sumOfSquares(y)));
        sb.append(String.format("  Sy = %.6f\n", statOps.standardDeviation(y, false)));
        
        resultArea.setText(sb.toString());
    }
    
    private void calculateCorrelation() {
        double[] x = parseData(dataInputArea.getText());
        double[] y = parseData(dataInputArea2.getText());
        
        if (x.length != y.length || x.length < 2) {
            resultArea.setText("Error: X and Y must have same length (≥2)");
            return;
        }
        
        double r = statOps.correlation(x, y);
        resultArea.setText(String.format("Correlation coefficient:\nr = %.10f\nr² = %.10f", r, r * r));
    }
    
    private void sortData(boolean ascending) {
        double[] data = parseData(dataInputArea.getText());
        if (data.length == 0) {
            resultArea.setText("No valid data");
            return;
        }
        
        Arrays.sort(data);
        if (!ascending) {
            for (int i = 0; i < data.length / 2; i++) {
                double temp = data[i];
                data[i] = data[data.length - 1 - i];
                data[data.length - 1 - i] = temp;
            }
        }
        
        StringBuilder sb = new StringBuilder();
        for (double val : data) {
            sb.append(val).append("\n");
        }
        dataInputArea.setText(sb.toString().trim());
        resultArea.setText("Data sorted " + (ascending ? "ascending" : "descending"));
    }
    
    private double[] parseData(String text) {
        String[] parts = text.split("[,\\s\\n]+");
        java.util.List<Double> values = new java.util.ArrayList<>();
        
        for (String part : parts) {
            part = part.trim();
            if (!part.isEmpty()) {
                try {
                    values.add(Double.parseDouble(part));
                } catch (NumberFormatException ignored) {}
            }
        }
        
        return values.stream().mapToDouble(Double::doubleValue).toArray();
    }
    
    private JPanel createSubPanel(String title) {
        JPanel panel = new JPanel();
        panel.setBackground(BG_COLOR);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 80)),
            title, 0, 0, null, TEXT_COLOR
        ));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        return panel;
    }
    
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(TEXT_COLOR);
        label.setFont(new Font("Monospaced", Font.PLAIN, 12));
        return label;
    }
    
    private JTextField createInputField() {
        JTextField field = new JTextField(6);
        field.setBackground(new Color(50, 50, 50));
        field.setForeground(TEXT_COLOR);
        field.setCaretColor(TEXT_COLOR);
        field.setFont(new Font("Monospaced", Font.PLAIN, 12));
        return field;
    }
    
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Monospaced", Font.BOLD, 11));
        button.setForeground(TEXT_COLOR);
        button.setBackground(new Color(60, 60, 60));
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 80)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
}
