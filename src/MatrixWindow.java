import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MatrixWindow extends JFrame {
    private static final Color BG_COLOR = new Color(30, 30, 30);
    private static final Color TEXT_COLOR = Color.WHITE;
    private static final Color DISPLAY_BG = new Color(198, 208, 184);
    
    private MatrixOperations matOps;
    private JTextField[][] matrixAFields;
    private JTextField[][] matrixBFields;
    private JTextArea resultArea;
    private JSpinner rowsSpinnerA, colsSpinnerA;
    private JSpinner rowsSpinnerB, colsSpinnerB;
    private JPanel matrixAPanel, matrixBPanel;
    
    public MatrixWindow() {
        this.matOps = new MatrixOperations();
        
        initializeFrame();
        createComponents();
        setVisible(true);
    }
    
    private void initializeFrame() {
        setTitle("Matrix Operations");
        setSize(700, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_COLOR);
        setLayout(new BorderLayout(5, 5));
    }
    
    private void createComponents() {
        JPanel topPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        topPanel.setBackground(BG_COLOR);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        topPanel.add(createMatrixInputPanel("Matrix [A]", true));
        topPanel.add(createMatrixInputPanel("Matrix [B]", false));
        
        JPanel buttonPanel = createButtonPanel();
        
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
            "Result", 0, 0, null, TEXT_COLOR
        ));
        
        add(topPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.SOUTH);
    }
    
    private JPanel createMatrixInputPanel(String title, boolean isMatrixA) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(BG_COLOR);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 80)),
            title, 0, 0, null, TEXT_COLOR
        ));
        
        JPanel sizePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        sizePanel.setBackground(BG_COLOR);
        
        JLabel rowsLabel = new JLabel("Rows:");
        rowsLabel.setForeground(TEXT_COLOR);
        JLabel colsLabel = new JLabel("Cols:");
        colsLabel.setForeground(TEXT_COLOR);
        
        JSpinner rowsSpinner = new JSpinner(new SpinnerNumberModel(3, 1, 5, 1));
        JSpinner colsSpinner = new JSpinner(new SpinnerNumberModel(3, 1, 5, 1));
        
        if (isMatrixA) {
            rowsSpinnerA = rowsSpinner;
            colsSpinnerA = colsSpinner;
        } else {
            rowsSpinnerB = rowsSpinner;
            colsSpinnerB = colsSpinner;
        }
        
        sizePanel.add(rowsLabel);
        sizePanel.add(rowsSpinner);
        sizePanel.add(colsLabel);
        sizePanel.add(colsSpinner);
        
        JButton updateBtn = createSmallButton("Update");
        updateBtn.addActionListener(e -> updateMatrixGrid(isMatrixA));
        sizePanel.add(updateBtn);
        
        JPanel matrixPanel = new JPanel(new GridLayout(3, 3, 2, 2));
        matrixPanel.setBackground(BG_COLOR);
        
        JTextField[][] fields = new JTextField[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                fields[i][j] = createMatrixField();
                matrixPanel.add(fields[i][j]);
            }
        }
        
        if (isMatrixA) {
            matrixAFields = fields;
            matrixAPanel = matrixPanel;
        } else {
            matrixBFields = fields;
            matrixBPanel = matrixPanel;
        }
        
        panel.add(sizePanel, BorderLayout.NORTH);
        panel.add(matrixPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void updateMatrixGrid(boolean isMatrixA) {
        JSpinner rowsSpinner = isMatrixA ? rowsSpinnerA : rowsSpinnerB;
        JSpinner colsSpinner = isMatrixA ? colsSpinnerA : colsSpinnerB;
        JPanel matrixPanel = isMatrixA ? matrixAPanel : matrixBPanel;
        
        int rows = (Integer) rowsSpinner.getValue();
        int cols = (Integer) colsSpinner.getValue();
        
        matrixPanel.removeAll();
        matrixPanel.setLayout(new GridLayout(rows, cols, 2, 2));
        
        JTextField[][] fields = new JTextField[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                fields[i][j] = createMatrixField();
                matrixPanel.add(fields[i][j]);
            }
        }
        
        if (isMatrixA) {
            matrixAFields = fields;
        } else {
            matrixBFields = fields;
        }
        
        matrixPanel.revalidate();
        matrixPanel.repaint();
    }
    
    private JTextField createMatrixField() {
        JTextField field = new JTextField("0", 4);
        field.setHorizontalAlignment(JTextField.CENTER);
        field.setBackground(new Color(50, 50, 50));
        field.setForeground(TEXT_COLOR);
        field.setCaretColor(TEXT_COLOR);
        field.setFont(new Font("Monospaced", Font.PLAIN, 12));
        return field;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 4, 5, 5));
        panel.setBackground(BG_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JButton addBtn = createStyledButton("[A]+[B]");
        addBtn.addActionListener(e -> performOperation("add"));
        
        JButton subBtn = createStyledButton("[A]-[B]");
        subBtn.addActionListener(e -> performOperation("subtract"));
        
        JButton mulBtn = createStyledButton("[A]×[B]");
        mulBtn.addActionListener(e -> performOperation("multiply"));
        
        JButton detABtn = createStyledButton("det([A])");
        detABtn.addActionListener(e -> performOperation("detA"));
        
        JButton detBBtn = createStyledButton("det([B])");
        detBBtn.addActionListener(e -> performOperation("detB"));
        
        JButton invABtn = createStyledButton("[A]⁻¹");
        invABtn.addActionListener(e -> performOperation("invA"));
        
        JButton invBBtn = createStyledButton("[B]⁻¹");
        invBBtn.addActionListener(e -> performOperation("invB"));
        
        JButton transABtn = createStyledButton("[A]ᵀ");
        transABtn.addActionListener(e -> performOperation("transA"));
        
        JButton transBBtn = createStyledButton("[B]ᵀ");
        transBBtn.addActionListener(e -> performOperation("transB"));
        
        JButton rrefABtn = createStyledButton("rref([A])");
        rrefABtn.addActionListener(e -> performOperation("rrefA"));
        
        JButton eigABtn = createStyledButton("eigen([A])");
        eigABtn.addActionListener(e -> performOperation("eigenA"));
        
        JButton powerBtn = createStyledButton("[A]ⁿ");
        powerBtn.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(this, "Enter power n:");
            if (input != null) {
                try {
                    int n = Integer.parseInt(input);
                    double[][] A = getMatrixA();
                    double[][] result = matOps.power(A, n);
                    if (result != null) {
                        displayMatrix(result, "[A]^" + n);
                    } else {
                        resultArea.setText("Error: Matrix not invertible (for negative powers)");
                    }
                } catch (Exception ex) {
                    resultArea.setText("Invalid input");
                }
            }
        });
        
        JButton traceABtn = createStyledButton("trace([A])");
        traceABtn.addActionListener(e -> {
            try {
                double[][] A = getMatrixA();
                double trace = matOps.trace(A);
                resultArea.setText("trace([A]) = " + trace);
            } catch (Exception ex) {
                resultArea.setText("Error");
            }
        });
        
        JButton rankABtn = createStyledButton("rank([A])");
        rankABtn.addActionListener(e -> {
            try {
                double[][] A = getMatrixA();
                int rank = matOps.rank(A);
                resultArea.setText("rank([A]) = " + rank);
            } catch (Exception ex) {
                resultArea.setText("Error");
            }
        });
        
        JButton scalarBtn = createStyledButton("k×[A]");
        scalarBtn.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(this, "Enter scalar k:");
            if (input != null) {
                try {
                    double k = Double.parseDouble(input);
                    double[][] A = getMatrixA();
                    double[][] result = matOps.scalarMultiply(A, k);
                    displayMatrix(result, k + " × [A]");
                } catch (Exception ex) {
                    resultArea.setText("Invalid input");
                }
            }
        });
        
        JButton identityBtn = createStyledButton("Identity");
        identityBtn.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(this, "Enter size n:");
            if (input != null) {
                try {
                    int n = Integer.parseInt(input);
                    double[][] result = matOps.identity(n);
                    displayMatrix(result, "I" + n);
                } catch (Exception ex) {
                    resultArea.setText("Invalid input");
                }
            }
        });
        
        panel.add(addBtn);
        panel.add(subBtn);
        panel.add(mulBtn);
        panel.add(detABtn);
        panel.add(detBBtn);
        panel.add(invABtn);
        panel.add(invBBtn);
        panel.add(transABtn);
        panel.add(transBBtn);
        panel.add(rrefABtn);
        panel.add(eigABtn);
        panel.add(powerBtn);
        panel.add(traceABtn);
        panel.add(rankABtn);
        panel.add(scalarBtn);
        panel.add(identityBtn);
        
        return panel;
    }
    
    private void performOperation(String operation) {
        try {
            double[][] A = getMatrixA();
            double[][] B = getMatrixB();
            double[][] result = null;
            String title = "";
            
            switch (operation) {
                case "add":
                    result = matOps.add(A, B);
                    title = "[A] + [B]";
                    break;
                case "subtract":
                    result = matOps.subtract(A, B);
                    title = "[A] - [B]";
                    break;
                case "multiply":
                    result = matOps.multiply(A, B);
                    title = "[A] × [B]";
                    break;
                case "detA":
                    double detA = matOps.determinant(A);
                    resultArea.setText("det([A]) = " + detA);
                    return;
                case "detB":
                    double detB = matOps.determinant(B);
                    resultArea.setText("det([B]) = " + detB);
                    return;
                case "invA":
                    result = matOps.inverse(A);
                    title = "[A]⁻¹";
                    if (result == null) {
                        resultArea.setText("Matrix [A] is singular (not invertible)");
                        return;
                    }
                    break;
                case "invB":
                    result = matOps.inverse(B);
                    title = "[B]⁻¹";
                    if (result == null) {
                        resultArea.setText("Matrix [B] is singular (not invertible)");
                        return;
                    }
                    break;
                case "transA":
                    result = matOps.transpose(A);
                    title = "[A]ᵀ";
                    break;
                case "transB":
                    result = matOps.transpose(B);
                    title = "[B]ᵀ";
                    break;
                case "rrefA":
                    result = matOps.rref(A);
                    title = "rref([A])";
                    break;
                case "eigenA":
                    if (A.length == 2 && A[0].length == 2) {
                        double[] eigenvalues = matOps.eigenvalues2x2(A);
                        resultArea.setText("Eigenvalues of [A]:\nλ₁ = " + eigenvalues[0] + 
                            "\nλ₂ = " + eigenvalues[1]);
                    } else {
                        resultArea.setText("Eigenvalue calculation only available for 2×2 matrices");
                    }
                    return;
            }
            
            if (result != null) {
                displayMatrix(result, title);
            } else {
                resultArea.setText("Operation not possible (dimension mismatch)");
            }
        } catch (Exception e) {
            resultArea.setText("Error: " + e.getMessage());
        }
    }
    
    private double[][] getMatrixA() {
        int rows = matrixAFields.length;
        int cols = matrixAFields[0].length;
        double[][] matrix = new double[rows][cols];
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                matrix[i][j] = Double.parseDouble(matrixAFields[i][j].getText());
            }
        }
        return matrix;
    }
    
    private double[][] getMatrixB() {
        int rows = matrixBFields.length;
        int cols = matrixBFields[0].length;
        double[][] matrix = new double[rows][cols];
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                matrix[i][j] = Double.parseDouble(matrixBFields[i][j].getText());
            }
        }
        return matrix;
    }
    
    private void displayMatrix(double[][] matrix, String title) {
        StringBuilder sb = new StringBuilder();
        sb.append(title).append(" =\n\n");
        
        for (double[] row : matrix) {
            sb.append("[ ");
            for (double val : row) {
                sb.append(String.format("%10.4f ", val));
            }
            sb.append("]\n");
        }
        
        resultArea.setText(sb.toString());
    }
    
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Monospaced", Font.BOLD, 11));
        button.setForeground(TEXT_COLOR);
        button.setBackground(new Color(60, 60, 60));
        button.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80)));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
    
    private JButton createSmallButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Monospaced", Font.PLAIN, 10));
        button.setForeground(TEXT_COLOR);
        button.setBackground(new Color(60, 60, 60));
        button.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
        button.setFocusPainted(false);
        return button;
    }
}
