import javax.swing.*;
import java.awt.*;

public class DisplayPanel extends JPanel {
    private static final Color DISPLAY_BG_COLOR = new Color(198, 208, 184);
    private static final Color DISPLAY_BORDER_COLOR = new Color(60, 60, 60);
    private static final Color TEXT_COLOR = new Color(20, 20, 20);
    
    private JLabel expressionLabel;
    private JLabel resultLabel;
    
    public DisplayPanel() {
        initializePanel();
        createLabels();
    }
    
    private void initializePanel() {
        setPreferredSize(new Dimension(280, 100));
        setBackground(DISPLAY_BG_COLOR);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(DISPLAY_BORDER_COLOR, 3),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        setLayout(new BorderLayout());
    }
    
    private void createLabels() {
        expressionLabel = new JLabel("");
        expressionLabel.setFont(new Font("Monospaced", Font.PLAIN, 14));
        expressionLabel.setForeground(TEXT_COLOR);
        expressionLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        
        resultLabel = new JLabel("0");
        resultLabel.setFont(new Font("Monospaced", Font.BOLD, 28));
        resultLabel.setForeground(TEXT_COLOR);
        resultLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        
        add(expressionLabel, BorderLayout.NORTH);
        add(resultLabel, BorderLayout.SOUTH);
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
