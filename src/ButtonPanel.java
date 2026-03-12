import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ButtonPanel extends JPanel {
    private static final Color PANEL_BG_COLOR = new Color(30, 30, 30);
    private static final Color NUMBER_BTN_COLOR = new Color(55, 55, 55);
    private static final Color OPERATOR_BTN_COLOR = new Color(75, 75, 80);
    private static final Color FUNCTION_BTN_COLOR = new Color(50, 50, 60);
    private static final Color SPECIAL_BTN_COLOR = new Color(85, 85, 95);
    private static final Color ENTER_BTN_COLOR = new Color(35, 105, 55);
    private static final Color CLEAR_BTN_COLOR = new Color(110, 55, 55);
    private static final Color ARROW_BTN_COLOR = new Color(65, 65, 70);
    private static final Color TEXT_COLOR = Color.WHITE;
    private static final Color SECOND_TEXT_COLOR = new Color(100, 180, 255);
    
    private CalculatorEngine engine;
    private DisplayPanel displayPanel;
    private boolean secondMode = false;
    
    public ButtonPanel(CalculatorEngine engine, DisplayPanel displayPanel) {
        this.engine = engine;
        this.displayPanel = displayPanel;
        initializePanel();
        createButtons();
    }
    
    private void initializePanel() {
        setBackground(PANEL_BG_COLOR);
        setLayout(new GridBagLayout());
    }
    
    private void createButtons() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(2, 2, 2, 2);
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        
        String[][] buttonLabels = {
            {"Y=", "WINDOW", "ZOOM", "TRACE", "GRAPH"},
            {"2nd", "MODE", "DEL", "CLEAR", ""},
            {"x⁻¹", "sin", "cos", "tan", "^"},
            {"x²", "ln", "log", "(", ")"},
            {"√", "7", "8", "9", "÷"},
            {"MATRIX", "4", "5", "6", "×"},
            {"PRGM", "1", "2", "3", "−"},
            {"STAT", "0", ".", "(−)", "+"},
            {"X", "ANS", "ENTER", "", ""}
        };
        
        String[][] secondLabels = {
            {"", "", "", "", ""},
            {"", "QUIT", "INS", "", ""},
            {"x³", "sin⁻¹", "cos⁻¹", "tan⁻¹", "π"},
            {"∛", "eˣ", "10ˣ", "{", "}"},
            {"ABS", "", "", "", "e"},
            {"", "", "", "", ""},
            {"SOLVE", "", "", "", ""},
            {"", "", "", "ANS", ""},
            {"", "", "", "", ""}
        };
        
        int row = 0;
        for (int i = 0; i < buttonLabels.length; i++) {
            for (int j = 0; j < buttonLabels[i].length; j++) {
                String label = buttonLabels[i][j];
                if (label.isEmpty()) continue;
                
                String secondLabel = (i < secondLabels.length && j < secondLabels[i].length)
                    ? secondLabels[i][j] : "";
                
                JButton button = createStyledButton(label, secondLabel, getButtonColor(label));
                
                gbc.gridx = j;
                gbc.gridy = row;
                
                if (label.equals("ENTER")) {
                    gbc.gridwidth = 3;
                } else {
                    gbc.gridwidth = 1;
                }
                
                add(button, gbc);
            }
            row++;
        }
    }
    
    private JButton createStyledButton(String label, String secondLabel, Color bgColor) {
        JButton button = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2d.setColor(bgColor.darker());
                } else if (getModel().isRollover()) {
                    g2d.setColor(bgColor.brighter());
                } else {
                    g2d.setColor(bgColor);
                }
                
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                
                g2d.setColor(bgColor.darker().darker());
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);
                
                if (!secondLabel.isEmpty()) {
                    g2d.setColor(SECOND_TEXT_COLOR);
                    g2d.setFont(new Font("Monospaced", Font.PLAIN, 8));
                    FontMetrics fm = g2d.getFontMetrics();
                    int x = (getWidth() - fm.stringWidth(secondLabel)) / 2;
                    g2d.drawString(secondLabel, x, 10);
                }
                
                g2d.setColor(TEXT_COLOR);
                int fontSize = label.length() > 4 ? 9 : (label.length() > 2 ? 11 : 13);
                g2d.setFont(new Font("Monospaced", Font.BOLD, fontSize));
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(label)) / 2;
                int y = (getHeight() + fm.getAscent()) / 2 + (secondLabel.isEmpty() ? 0 : 2);
                g2d.drawString(label, x, y);
                
                g2d.dispose();
            }
        };
        
        button.setPreferredSize(new Dimension(58, 38));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addActionListener(e -> handleButtonPress(label, secondLabel));
        
        return button;
    }
    
    private Color getButtonColor(String label) {
        if (label.equals("ENTER")) return ENTER_BTN_COLOR;
        if (label.equals("CLEAR") || label.equals("DEL")) return CLEAR_BTN_COLOR;
        if (label.matches("[0-9]") || label.equals(".")) return NUMBER_BTN_COLOR;
        if (label.matches("[+−×÷^]")) return OPERATOR_BTN_COLOR;
        if (label.equals("2nd")) return SPECIAL_BTN_COLOR;
        if (label.matches("Y=|WINDOW|ZOOM|TRACE|GRAPH")) return new Color(40, 40, 45);
        return FUNCTION_BTN_COLOR;
    }
    
    private void handleButtonPress(String label, String secondLabel) {
        if (secondMode && !secondLabel.isEmpty()) {
            handleSecondFunction(secondLabel);
            secondMode = false;
            return;
        }
        
        switch (label) {
            case "CLEAR":
                engine.clear();
                displayPanel.clear();
                break;
            case "DEL":
                engine.delete();
                updateDisplay();
                break;
            case "ENTER":
                String result = engine.calculate();
                displayPanel.setExpression(engine.getLastExpression());
                displayPanel.setResult(result);
                break;
            case "2nd":
                secondMode = !secondMode;
                break;
            case "(−)":
                engine.negate();
                updateDisplay();
                break;
            case "MODE":
                engine.toggleAngleMode();
                displayPanel.updateStatus();
                break;
            case "Y=":
            case "GRAPH":
            case "WINDOW":
            case "ZOOM":
            case "TRACE":
                new GraphWindow(engine);
                break;
            case "STAT":
                new StatisticsWindow();
                break;
            case "MATRIX":
                new MatrixWindow();
                break;
            case "PRGM":
                new SolverWindow(engine);
                break;
            case "X":
                engine.appendVariable("x");
                updateDisplay();
                break;
            case "ANS":
                engine.appendAns();
                updateDisplay();
                break;
            case "sin":
                engine.appendFunction("sin(");
                updateDisplay();
                break;
            case "cos":
                engine.appendFunction("cos(");
                updateDisplay();
                break;
            case "tan":
                engine.appendFunction("tan(");
                updateDisplay();
                break;
            case "ln":
                engine.appendFunction("ln(");
                updateDisplay();
                break;
            case "log":
                engine.appendFunction("log(");
                updateDisplay();
                break;
            case "√":
                engine.appendFunction("√(");
                updateDisplay();
                break;
            case "x²":
                engine.appendOperator("²");
                updateDisplay();
                break;
            case "x⁻¹":
                engine.appendOperator("⁻¹");
                updateDisplay();
                break;
            case "^":
                engine.appendOperator("^");
                updateDisplay();
                break;
            case "÷":
                engine.appendOperator("÷");
                updateDisplay();
                break;
            case "×":
                engine.appendOperator("×");
                updateDisplay();
                break;
            case "−":
                engine.appendOperator("−");
                updateDisplay();
                break;
            case "+":
                engine.appendOperator("+");
                updateDisplay();
                break;
            case "(":
            case ")":
            case "{":
            case "}":
                engine.appendParenthesis(label);
                updateDisplay();
                break;
            default:
                if (label.matches("[0-9.]")) {
                    engine.appendDigit(label);
                    updateDisplay();
                }
                break;
        }
    }
    
    private void handleSecondFunction(String label) {
        switch (label) {
            case "sin⁻¹":
                engine.appendFunction("asin(");
                break;
            case "cos⁻¹":
                engine.appendFunction("acos(");
                break;
            case "tan⁻¹":
                engine.appendFunction("atan(");
                break;
            case "eˣ":
                engine.appendFunction("exp(");
                break;
            case "10ˣ":
                engine.appendFunction("10^(");
                break;
            case "∛":
                engine.appendFunction("cbrt(");
                break;
            case "x³":
                engine.appendFunction("cube(");
                break;
            case "ABS":
                engine.appendFunction("abs(");
                break;
            case "π":
                engine.appendConstant("π");
                break;
            case "e":
                engine.appendConstant("e");
                break;
            case "ANS":
                engine.appendAns();
                break;
            case "QUIT":
                System.exit(0);
                break;
            case "INS":
                engine.toggleInsertMode();
                break;
            case "SOLVE":
                new SolverWindow(engine);
                break;
            default:
                break;
        }
        updateDisplay();
    }
    
    private void updateDisplay() {
        displayPanel.setExpression(engine.getExpression());
        displayPanel.setResult(engine.getCurrentValue());
    }
}
