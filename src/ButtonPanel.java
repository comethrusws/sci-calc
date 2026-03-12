import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ButtonPanel extends JPanel {
    private static final Color PANEL_BG_COLOR = new Color(30, 30, 30);
    private static final Color NUMBER_BTN_COLOR = new Color(50, 50, 50);
    private static final Color OPERATOR_BTN_COLOR = new Color(70, 70, 70);
    private static final Color FUNCTION_BTN_COLOR = new Color(45, 45, 55);
    private static final Color SPECIAL_BTN_COLOR = new Color(80, 80, 90);
    private static final Color ENTER_BTN_COLOR = new Color(30, 100, 50);
    private static final Color CLEAR_BTN_COLOR = new Color(100, 50, 50);
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
            {"2nd", "MODE", "DEL", "CLEAR"},
            {"x⁻¹", "sin", "cos", "tan", "^"},
            {"x²", "ln", "log", "(", ")"},
            {"√", "7", "8", "9", "÷"},
            {"π", "4", "5", "6", "×"},
            {"e", "1", "2", "3", "−"},
            {"±", "0", ".", "EXP", "+"},
            {"ANS", "ENTER"}
        };
        
        String[][] secondLabels = {
            {"", "QUIT", "INS", ""},
            {"x³", "sin⁻¹", "cos⁻¹", "tan⁻¹", "ⁿ√"},
            {"∛", "eˣ", "10ˣ", "{", "}"},
            {"ABS", "", "", "", ""},
            {"", "", "", "", ""},
            {"", "", "", "", ""},
            {"", "", "", "", ""},
            {"", ""}
        };
        
        int row = 0;
        for (int i = 0; i < buttonLabels.length; i++) {
            for (int j = 0; j < buttonLabels[i].length; j++) {
                String label = buttonLabels[i][j];
                String secondLabel = (i < secondLabels.length && j < secondLabels[i].length) 
                    ? secondLabels[i][j] : "";
                
                JButton button = createStyledButton(label, secondLabel, getButtonColor(label));
                
                gbc.gridx = j;
                gbc.gridy = row;
                
                if (label.equals("ENTER")) {
                    gbc.gridwidth = 3;
                    gbc.gridx = 1;
                } else if (label.equals("ANS") && i == 7) {
                    gbc.gridwidth = 1;
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
                
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                
                g2d.setColor(bgColor.darker().darker());
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                
                if (!secondLabel.isEmpty()) {
                    g2d.setColor(SECOND_TEXT_COLOR);
                    g2d.setFont(new Font("Monospaced", Font.PLAIN, 9));
                    FontMetrics fm = g2d.getFontMetrics();
                    int x = (getWidth() - fm.stringWidth(secondLabel)) / 2;
                    g2d.drawString(secondLabel, x, 12);
                }
                
                g2d.setColor(TEXT_COLOR);
                g2d.setFont(new Font("Monospaced", Font.BOLD, 14));
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(label)) / 2;
                int y = (getHeight() + fm.getAscent()) / 2 + (secondLabel.isEmpty() ? 0 : 3);
                g2d.drawString(label, x, y);
                
                g2d.dispose();
            }
        };
        
        button.setPreferredSize(new Dimension(55, 42));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addActionListener(e -> handleButtonPress(label));
        
        return button;
    }
    
    private Color getButtonColor(String label) {
        if (label.equals("ENTER")) return ENTER_BTN_COLOR;
        if (label.equals("CLEAR") || label.equals("DEL")) return CLEAR_BTN_COLOR;
        if (label.matches("[0-9]") || label.equals(".")) return NUMBER_BTN_COLOR;
        if (label.matches("[+−×÷^]")) return OPERATOR_BTN_COLOR;
        if (label.equals("2nd")) return SPECIAL_BTN_COLOR;
        return FUNCTION_BTN_COLOR;
    }
    
    private void handleButtonPress(String label) {
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
                displayPanel.setExpression(engine.getExpression());
                displayPanel.setResult(result);
                break;
            case "2nd":
                secondMode = !secondMode;
                break;
            case "±":
                engine.negate();
                updateDisplay();
                break;
            case "ANS":
                engine.appendAns();
                updateDisplay();
                break;
            default:
                if (secondMode) {
                    handleSecondFunction(label);
                    secondMode = false;
                } else {
                    handleRegularInput(label);
                }
                updateDisplay();
                break;
        }
    }
    
    private void handleSecondFunction(String label) {
        switch (label) {
            case "sin":
                engine.appendFunction("asin(");
                break;
            case "cos":
                engine.appendFunction("acos(");
                break;
            case "tan":
                engine.appendFunction("atan(");
                break;
            case "ln":
                engine.appendFunction("exp(");
                break;
            case "log":
                engine.appendFunction("10^(");
                break;
            case "x²":
                engine.appendFunction("cbrt(");
                break;
            case "x⁻¹":
                engine.appendFunction("cube(");
                break;
            case "√":
                engine.appendFunction("abs(");
                break;
            default:
                handleRegularInput(label);
                break;
        }
    }
    
    private void handleRegularInput(String label) {
        switch (label) {
            case "sin":
                engine.appendFunction("sin(");
                break;
            case "cos":
                engine.appendFunction("cos(");
                break;
            case "tan":
                engine.appendFunction("tan(");
                break;
            case "ln":
                engine.appendFunction("ln(");
                break;
            case "log":
                engine.appendFunction("log(");
                break;
            case "√":
                engine.appendFunction("√(");
                break;
            case "x²":
                engine.appendOperator("²");
                break;
            case "x⁻¹":
                engine.appendOperator("⁻¹");
                break;
            case "π":
                engine.appendConstant("π");
                break;
            case "e":
                engine.appendConstant("e");
                break;
            case "^":
                engine.appendOperator("^");
                break;
            case "÷":
                engine.appendOperator("÷");
                break;
            case "×":
                engine.appendOperator("×");
                break;
            case "−":
                engine.appendOperator("−");
                break;
            case "+":
                engine.appendOperator("+");
                break;
            case "(":
            case ")":
                engine.appendParenthesis(label);
                break;
            case "EXP":
                engine.appendOperator("E");
                break;
            case "MODE":
                engine.toggleAngleMode();
                break;
            default:
                if (label.matches("[0-9.]")) {
                    engine.appendDigit(label);
                }
                break;
        }
    }
    
    private void updateDisplay() {
        displayPanel.setExpression(engine.getExpression());
        displayPanel.setResult(engine.getCurrentValue());
    }
}
