import javax.swing.*;
import java.awt.*;

public class CalculatorFrame extends JFrame {
    private static final int FRAME_WIDTH = 320;
    private static final int FRAME_HEIGHT = 580;
    private static final Color CALCULATOR_BODY_COLOR = new Color(30, 30, 30);
    
    private DisplayPanel displayPanel;
    private ButtonPanel buttonPanel;
    private CalculatorEngine engine;
    
    public CalculatorFrame() {
        engine = new CalculatorEngine();
        initializeFrame();
        createComponents();
        setVisible(true);
    }
    
    private void initializeFrame() {
        setTitle("Scientific Calculator");
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
        getContentPane().setBackground(CALCULATOR_BODY_COLOR);
        setLayout(new BorderLayout(5, 5));
    }
    
    private void createComponents() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(5, 10));
        mainPanel.setBackground(CALCULATOR_BODY_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        displayPanel = new DisplayPanel();
        buttonPanel = new ButtonPanel(engine, displayPanel);
        
        mainPanel.add(displayPanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        
        add(mainPanel, BorderLayout.CENTER);
    }
}
