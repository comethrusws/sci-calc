import javax.swing.*;
import java.awt.*;

public class DisplayPanel extends JPanel {
    private static final Color DISPLAY_BG_COLOR = new Color(198, 208, 184);
    private static final Color DISPLAY_BORDER_COLOR = new Color(50, 50, 50);
    private static final Color TEXT_COLOR = new Color(15, 15, 15);
    private static final Color STATUS_COLOR = new Color(60, 60, 60);
    
    private JLabel statusLabel;
    private JLabel expressionLabel;
    private JLabel resultLabel;
    private CalculatorEngine engine;
    
    public DisplayPanel(CalculatorEngine engine) {
        this.engine = engine;
        initializePanel();
        createLabels();
    }
    
    public DisplayPanel() {
        this(null);
    }
    
    private void initializePanel() {
        setPreferredSize(new Dimension(300, 110));
        setBackground(DISPLAY_BG_COLOR);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(DISPLAY_BORDER_COLOR, 3),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        setLayout(new BorderLayout(0, 2));
    }
    
    private void createLabels() {
        statusLabel = new JLabel("DEG  FUNC  ");
        statusLabel.setFont(new Font("Monospaced", Font.PLAIN, 10));
        statusLabel.setForeground(STATUS_COLOR);
        statusLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        
        expressionLabel = new JLabel("");
        expressionLabel.setFont(new Font("Monospaced", Font.PLAIN, 14));
        expressionLabel.setForeground(TEXT_COLOR);
        expressionLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        
        resultLabel = new JLabel("0");
        resultLabel.setFont(new Font("Monospaced", Font.BOLD, 26));
        resultLabel.setForeground(TEXT_COLOR);
        resultLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(DISPLAY_BG_COLOR);
        topPanel.add(statusLabel, BorderLayout.NORTH);
        topPanel.add(expressionLabel, BorderLayout.CENTER);
        
        add(topPanel, BorderLayout.NORTH);
        add(resultLabel, BorderLayout.SOUTH);
        
        updateStatus();
    }
    
    public void updateStatus() {
        if (engine != null) {
            String mode = engine.isUsingDegrees() ? "DEG" : "RAD";
            statusLabel.setText(mode + "  FUNC  ");
        }
    }
    
    public void setExpression(String expression) {
        expressionLabel.setText(expression);
    }
    
    public void setResult(String result) {
        resultLabel.setText(result);
    }
    
    public String getExpression() {
        return expressionLabel.getText();
    }
    
    public String getResult() {
        return resultLabel.getText();
    }
    
    public void clear() {
        expressionLabel.setText("");
        resultLabel.setText("0");
    }
}
