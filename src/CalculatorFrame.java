import javax.swing.*;
import java.awt.*;

public class CalculatorFrame extends JFrame {
    private static final int FRAME_WIDTH = 340;
    private static final int FRAME_HEIGHT = 620;
    private static final Color CALCULATOR_BODY_COLOR = new Color(30, 30, 30);
    
    private DisplayPanel displayPanel;
    private ButtonPanel buttonPanel;
    private MenuPanel menuPanel;
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
        setLayout(new BorderLayout(0, 0));
    }
    
    private void createComponents() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(0, 5));
        mainPanel.setBackground(CALCULATOR_BODY_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        
        JPanel topSection = new JPanel(new BorderLayout(0, 3));
        topSection.setBackground(CALCULATOR_BODY_COLOR);
        
        menuPanel = new MenuPanel(engine);
        displayPanel = new DisplayPanel(engine);
        
        topSection.add(menuPanel, BorderLayout.NORTH);
        topSection.add(displayPanel, BorderLayout.CENTER);
        
        buttonPanel = new ButtonPanel(engine, displayPanel);
        
        mainPanel.add(topSection, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        
        add(mainPanel, BorderLayout.CENTER);
    }
    
    public CalculatorEngine getEngine() {
        return engine;
    }
}
