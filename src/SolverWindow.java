import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SolverWindow extends JFrame {
    private static final Color BG_COLOR = new Color(30, 30, 30);
    private static final Color TEXT_COLOR = Color.WHITE;
    private static final Color DISPLAY_BG = new Color(198, 208, 184);
    
    private CalculatorEngine engine;
    private EquationSolver solver;
    private JTextArea resultArea;
    private JTabbedPane tabbedPane;
    
    public SolverWindow(CalculatorEngine engine) {
        this.engine = engine;
        this.solver = new EquationSolver(engine);
        
        initializeFrame();
        createComponents();
        setVisible(true);
    }
    
    private void initializeFrame() {
        setTitle("Equation Solver");
        setSize(450, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_COLOR);
        setLayout(new BorderLayout(5, 5));
    }
    
    private void createComponents() {
        tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(BG_COLOR);
        tabbedPane.setForeground(TEXT_COLOR);
        
        tabbedPane.addTab("Polynomial", createPolynomialPanel());
        tabbedPane.addTab("Systems", createSystemsPanel());
        tabbedPane.addTab("Trig Solver", createTrigPanel());
        tabbedPane.addTab("Numeric", createNumericPanel());
        
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        resultArea.setBackground(DISPLAY_BG);
        resultArea.setForeground(Color.BLACK);
        resultArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setPreferredSize(new Dimension(0, 150));
        scrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 80)),
            "Results",
            0, 0, null, TEXT_COLOR
        ));
        scrollPane.getViewport().setBackground(DISPLAY_BG);
        
        add(tabbedPane, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.SOUTH);
    }
    
    private JPanel createPolynomialPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(BG_COLOR);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel quadPanel = createSubPanel("Quadratic: ax² + bx + c = 0");
        JTextField aField = createInputField();
        JTextField bField = createInputField();
        JTextField cField = createInputField();
        
        JPanel quadInputs = new JPanel(new FlowLayout(FlowLayout.LEFT));
        quadInputs.setBackground(BG_COLOR);
        quadInputs.add(createLabel("a:"));
        quadInputs.add(aField);
        quadInputs.add(createLabel("b:"));
        quadInputs.add(bField);
        quadInputs.add(createLabel("c:"));
        quadInputs.add(cField);
        quadPanel.add(quadInputs);
        
        JButton solveQuad = createStyledButton("Solve Quadratic");
        solveQuad.addActionListener(e -> {
            try {
                double a = Double.parseDouble(aField.getText());
                double b = Double.parseDouble(bField.getText());
                double c = Double.parseDouble(cField.getText());
                double[] roots = solver.solveQuadratic(a, b, c);
                displayQuadraticResult(roots, a, b, c);
            } catch (Exception ex) {
                resultArea.setText("Invalid input");
            }
        });
        quadPanel.add(solveQuad);
        panel.add(quadPanel);
        
        panel.add(Box.createVerticalStrut(10));
        
        JPanel cubicPanel = createSubPanel("Cubic: ax³ + bx² + cx + d = 0");
        JTextField aField3 = createInputField();
        JTextField bField3 = createInputField();
        JTextField cField3 = createInputField();
        JTextField dField3 = createInputField();
        
        JPanel cubicInputs = new JPanel(new FlowLayout(FlowLayout.LEFT));
        cubicInputs.setBackground(BG_COLOR);
        cubicInputs.add(createLabel("a:"));
        cubicInputs.add(aField3);
        cubicInputs.add(createLabel("b:"));
        cubicInputs.add(bField3);
        cubicInputs.add(createLabel("c:"));
        cubicInputs.add(cField3);
        cubicInputs.add(createLabel("d:"));
        cubicInputs.add(dField3);
        cubicPanel.add(cubicInputs);
        
        JButton solveCubic = createStyledButton("Solve Cubic");
        solveCubic.addActionListener(e -> {
            try {
                double a = Double.parseDouble(aField3.getText());
                double b = Double.parseDouble(bField3.getText());
                double c = Double.parseDouble(cField3.getText());
                double d = Double.parseDouble(dField3.getText());
                double[] roots = solver.solveCubic(a, b, c, d);
                displayCubicResult(roots);
            } catch (Exception ex) {
                resultArea.setText("Invalid input");
            }
        });
        cubicPanel.add(solveCubic);
        panel.add(cubicPanel);
        
        return panel;
    }
    
    private JPanel createSystemsPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(BG_COLOR);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel sys2Panel = createSubPanel("2×2 System: a₁x + b₁y = c₁, a₂x + b₂y = c₂");
        
        JTextField[] fields2x2 = new JTextField[6];
        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row1.setBackground(BG_COLOR);
        row1.add(createLabel("Eq1:"));
        for (int i = 0; i < 3; i++) {
            fields2x2[i] = createInputField();
            row1.add(fields2x2[i]);
            if (i == 0) row1.add(createLabel("x +"));
            else if (i == 1) row1.add(createLabel("y ="));
        }
        sys2Panel.add(row1);
        
        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row2.setBackground(BG_COLOR);
        row2.add(createLabel("Eq2:"));
        for (int i = 3; i < 6; i++) {
            fields2x2[i] = createInputField();
            row2.add(fields2x2[i]);
            if (i == 3) row2.add(createLabel("x +"));
            else if (i == 4) row2.add(createLabel("y ="));
        }
        sys2Panel.add(row2);
        
        JButton solve2x2 = createStyledButton("Solve 2×2");
        solve2x2.addActionListener(e -> {
            try {
                double a1 = Double.parseDouble(fields2x2[0].getText());
                double b1 = Double.parseDouble(fields2x2[1].getText());
                double c1 = Double.parseDouble(fields2x2[2].getText());
                double a2 = Double.parseDouble(fields2x2[3].getText());
                double b2 = Double.parseDouble(fields2x2[4].getText());
                double c2 = Double.parseDouble(fields2x2[5].getText());
                
                double[][] result = solver.solveLinearSystem2x2(a1, b1, c1, a2, b2, c2);
                if (result != null) {
                    resultArea.setText(String.format("Solution:\nx = %.6f\ny = %.6f", 
                        result[0][0], result[0][1]));
                } else {
                    resultArea.setText("No unique solution (parallel lines or coincident)");
                }
            } catch (Exception ex) {
                resultArea.setText("Invalid input");
            }
        });
        sys2Panel.add(solve2x2);
        panel.add(sys2Panel);
        
        panel.add(Box.createVerticalStrut(10));
        
        JPanel sys3Panel = createSubPanel("3×3 System");
        JTextField[][] fields3x3 = new JTextField[3][4];
        
        for (int i = 0; i < 3; i++) {
            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
            row.setBackground(BG_COLOR);
            row.add(createLabel("Eq" + (i + 1) + ":"));
            for (int j = 0; j < 4; j++) {
                fields3x3[i][j] = createInputField();
                row.add(fields3x3[i][j]);
                if (j == 0) row.add(createLabel("x +"));
                else if (j == 1) row.add(createLabel("y +"));
                else if (j == 2) row.add(createLabel("z ="));
            }
            sys3Panel.add(row);
        }
        
        JButton solve3x3 = createStyledButton("Solve 3×3");
        solve3x3.addActionListener(e -> {
            try {
                double[][] coeffs = new double[3][3];
                double[] consts = new double[3];
                
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        coeffs[i][j] = Double.parseDouble(fields3x3[i][j].getText());
                    }
                    consts[i] = Double.parseDouble(fields3x3[i][3].getText());
                }
                
                double[][] result = solver.solveLinearSystem3x3(coeffs, consts);
                if (result != null) {
                    resultArea.setText(String.format("Solution:\nx = %.6f\ny = %.6f\nz = %.6f",
                        result[0][0], result[0][1], result[0][2]));
                } else {
                    resultArea.setText("No unique solution");
                }
            } catch (Exception ex) {
                resultArea.setText("Invalid input");
            }
        });
        sys3Panel.add(solve3x3);
        panel.add(sys3Panel);
        
        return panel;
    }
    
    private JPanel createTrigPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(BG_COLOR);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel trigSolvePanel = createSubPanel("Solve: sin(x)=k, cos(x)=k, tan(x)=k");
        
        JPanel inputRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputRow.setBackground(BG_COLOR);
        
        String[] trigFuncs = {"sin", "cos", "tan"};
        JComboBox<String> funcCombo = new JComboBox<>(trigFuncs);
        funcCombo.setBackground(new Color(50, 50, 50));
        funcCombo.setForeground(TEXT_COLOR);
        
        inputRow.add(funcCombo);
        inputRow.add(createLabel("(x) ="));
        
        JTextField valueField = createInputField();
        inputRow.add(valueField);
        
        JCheckBox degreeCheck = new JCheckBox("Degrees", true);
        degreeCheck.setBackground(BG_COLOR);
        degreeCheck.setForeground(TEXT_COLOR);
        inputRow.add(degreeCheck);
        
        trigSolvePanel.add(inputRow);
        
        JPanel rangeRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        rangeRow.setBackground(BG_COLOR);
        rangeRow.add(createLabel("Range: from"));
        JTextField minField = createInputField();
        minField.setText("0");
        rangeRow.add(minField);
        rangeRow.add(createLabel("to"));
        JTextField maxField = createInputField();
        maxField.setText("360");
        rangeRow.add(maxField);
        trigSolvePanel.add(rangeRow);
        
        JButton solveTrig = createStyledButton("Solve Trig Equation");
        solveTrig.addActionListener(e -> {
            try {
                String func = (String) funcCombo.getSelectedItem();
                double value = Double.parseDouble(valueField.getText());
                boolean useDegrees = degreeCheck.isSelected();
                double min = Double.parseDouble(minField.getText());
                double max = Double.parseDouble(maxField.getText());
                
                TrigSolution general = solver.solveTrigEquation(func, value, useDegrees);
                double[] solutions = solver.solveTrigInRange(func, value, min, max, useDegrees);
                
                StringBuilder sb = new StringBuilder();
                sb.append("Equation: ").append(func).append("(x) = ").append(value).append("\n\n");
                
                if (!general.hasSolution) {
                    sb.append("No solution (value out of range)\n");
                } else {
                    sb.append("General solution:\n").append(general.generalForm).append("\n\n");
                    sb.append("Principal value: ").append(String.format("%.6f", general.principalValue));
                    sb.append(useDegrees ? "°" : " rad").append("\n\n");
                    
                    sb.append("Solutions in [").append(min).append(", ").append(max).append("]:\n");
                    if (solutions.length == 0) {
                        sb.append("No solutions in this range\n");
                    } else {
                        for (int i = 0; i < solutions.length; i++) {
                            sb.append("x").append(i + 1).append(" = ").append(String.format("%.6f", solutions[i]));
                            sb.append(useDegrees ? "°" : " rad").append("\n");
                        }
                    }
                }
                
                resultArea.setText(sb.toString());
            } catch (Exception ex) {
                resultArea.setText("Invalid input");
            }
        });
        trigSolvePanel.add(solveTrig);
        panel.add(trigSolvePanel);
        
        panel.add(Box.createVerticalStrut(10));
        
        JPanel identityPanel = createSubPanel("Trig Identities Reference");
        JTextArea identities = new JTextArea();
        identities.setEditable(false);
        identities.setBackground(new Color(50, 50, 50));
        identities.setForeground(TEXT_COLOR);
        identities.setFont(new Font("Monospaced", Font.PLAIN, 10));
        identities.setText(
            "Pythagorean:\n" +
            "  sin²(x) + cos²(x) = 1\n" +
            "  1 + tan²(x) = sec²(x)\n" +
            "  1 + cot²(x) = csc²(x)\n\n" +
            "Double Angle:\n" +
            "  sin(2x) = 2sin(x)cos(x)\n" +
            "  cos(2x) = cos²(x) - sin²(x)\n" +
            "  tan(2x) = 2tan(x)/(1-tan²(x))\n\n" +
            "Sum/Difference:\n" +
            "  sin(a±b) = sin(a)cos(b) ± cos(a)sin(b)\n" +
            "  cos(a±b) = cos(a)cos(b) ∓ sin(a)sin(b)"
        );
        identityPanel.add(new JScrollPane(identities));
        panel.add(identityPanel);
        
        return panel;
    }
    
    private JPanel createNumericPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(BG_COLOR);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel numericPanel = createSubPanel("Numeric Solver: f(x) = 0");
        
        JPanel exprRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        exprRow.setBackground(BG_COLOR);
        exprRow.add(createLabel("f(x) = "));
        JTextField exprField = new JTextField(20);
        exprField.setBackground(new Color(50, 50, 50));
        exprField.setForeground(TEXT_COLOR);
        exprField.setCaretColor(TEXT_COLOR);
        exprRow.add(exprField);
        numericPanel.add(exprRow);
        
        JPanel guessRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        guessRow.setBackground(BG_COLOR);
        guessRow.add(createLabel("Initial guess:"));
        JTextField guessField = createInputField();
        guessField.setText("0");
        guessRow.add(guessField);
        numericPanel.add(guessRow);
        
        JButton solveNumeric = createStyledButton("Find Root (Newton-Raphson)");
        solveNumeric.addActionListener(e -> {
            try {
                String expr = exprField.getText();
                double guess = Double.parseDouble(guessField.getText());
                double root = solver.solveNewtonRaphson(expr, guess);
                
                String exprAtRoot = expr.replaceAll("(?i)x", "(" + root + ")");
                double fRoot = engine.evaluateExpression(exprAtRoot);
                
                resultArea.setText(String.format(
                    "f(x) = %s\n\nRoot found:\nx = %.10f\n\nVerification:\nf(%.6f) = %.2e",
                    expr, root, root, fRoot
                ));
            } catch (Exception ex) {
                resultArea.setText("Error: Could not find root. Try a different initial guess.");
            }
        });
        numericPanel.add(solveNumeric);
        
        panel.add(numericPanel);
        
        panel.add(Box.createVerticalStrut(10));
        
        JPanel findAllPanel = createSubPanel("Find All Roots in Range");
        
        JPanel expr2Row = new JPanel(new FlowLayout(FlowLayout.LEFT));
        expr2Row.setBackground(BG_COLOR);
        expr2Row.add(createLabel("f(x) = "));
        JTextField expr2Field = new JTextField(20);
        expr2Field.setBackground(new Color(50, 50, 50));
        expr2Field.setForeground(TEXT_COLOR);
        expr2Field.setCaretColor(TEXT_COLOR);
        expr2Row.add(expr2Field);
        findAllPanel.add(expr2Row);
        
        JPanel rangeRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        rangeRow.setBackground(BG_COLOR);
        rangeRow.add(createLabel("From:"));
        JTextField minField = createInputField();
        minField.setText("-10");
        rangeRow.add(minField);
        rangeRow.add(createLabel("To:"));
        JTextField maxField = createInputField();
        maxField.setText("10");
        rangeRow.add(maxField);
        findAllPanel.add(rangeRow);
        
        JButton findAll = createStyledButton("Find All Roots");
        findAll.addActionListener(e -> {
            try {
                String expr = expr2Field.getText();
                double min = Double.parseDouble(minField.getText());
                double max = Double.parseDouble(maxField.getText());
                
                double[] roots = solver.findAllRoots(expr, min, max);
                
                StringBuilder sb = new StringBuilder();
                sb.append("f(x) = ").append(expr).append("\n\n");
                sb.append("Roots in [").append(min).append(", ").append(max).append("]:\n\n");
                
                if (roots.length == 0) {
                    sb.append("No roots found in this range\n");
                } else {
                    for (int i = 0; i < roots.length; i++) {
                        sb.append("x").append(i + 1).append(" = ").append(String.format("%.10f", roots[i])).append("\n");
                    }
                }
                
                resultArea.setText(sb.toString());
            } catch (Exception ex) {
                resultArea.setText("Error finding roots");
            }
        });
        findAllPanel.add(findAll);
        
        panel.add(findAllPanel);
        
        return panel;
    }
    
    private JPanel createSubPanel(String title) {
        JPanel panel = new JPanel();
        panel.setBackground(BG_COLOR);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 80)),
            title, 0, 0, null, TEXT_COLOR
        ));
        return panel;
    }
    
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(TEXT_COLOR);
        label.setFont(new Font("Monospaced", Font.PLAIN, 12));
        return label;
    }
    
    private JTextField createInputField() {
        JTextField field = new JTextField(5);
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
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        return button;
    }
    
    private void displayQuadraticResult(double[] roots, double a, double b, double c) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Equation: %.2fx² + %.2fx + %.2f = 0\n\n", a, b, c));
        
        double discriminant = b * b - 4 * a * c;
        sb.append(String.format("Discriminant: %.6f\n\n", discriminant));
        
        if (roots == null) {
            sb.append("No solution (a = 0 and b = 0)\n");
        } else if (roots.length == 1) {
            sb.append("One real root (double root):\n");
            sb.append(String.format("x = %.6f\n", roots[0]));
        } else if (roots.length == 2) {
            sb.append("Two real roots:\n");
            sb.append(String.format("x₁ = %.6f\n", roots[0]));
            sb.append(String.format("x₂ = %.6f\n", roots[1]));
        } else if (roots.length == 4) {
            sb.append("Two complex roots:\n");
            sb.append(String.format("x₁ = %.6f + %.6fi\n", roots[2], roots[3]));
            sb.append(String.format("x₂ = %.6f - %.6fi\n", roots[2], roots[3]));
        }
        
        resultArea.setText(sb.toString());
    }
    
    private void displayCubicResult(double[] roots) {
        StringBuilder sb = new StringBuilder();
        sb.append("Cubic equation roots:\n\n");
        
        if (roots == null || roots.length == 0) {
            sb.append("No solution found\n");
        } else {
            for (int i = 0; i < roots.length; i++) {
                sb.append(String.format("x%d = %.6f\n", i + 1, roots[i]));
            }
        }
        
        resultArea.setText(sb.toString());
    }
}
