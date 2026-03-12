import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MenuPanel extends JPanel {
    private static final Color BG_COLOR = new Color(30, 30, 30);
    private static final Color BUTTON_COLOR = new Color(45, 45, 50);
    private static final Color TEXT_COLOR = Color.WHITE;
    
    private CalculatorEngine engine;
    
    public MenuPanel(CalculatorEngine engine) {
        this.engine = engine;
        initializePanel();
        createMenuButtons();
    }
    
    private void initializePanel() {
        setBackground(BG_COLOR);
        setLayout(new FlowLayout(FlowLayout.CENTER, 3, 2));
        setPreferredSize(new Dimension(320, 32));
    }
    
    private void createMenuButtons() {
        String[] menuLabels = {"Y=", "WINDOW", "ZOOM", "TRACE", "GRAPH"};
        
        for (String label : menuLabels) {
            JButton btn = createMenuButton(label);
            add(btn);
        }
        
        add(Box.createHorizontalStrut(5));
        
        String[] extraLabels = {"MATH", "APPS", "PRGM", "STAT"};
        for (String label : extraLabels) {
            JButton btn = createMenuButton(label);
            add(btn);
        }
    }
    
    private JButton createMenuButton(String label) {
        JButton button = new JButton(label) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2d.setColor(BUTTON_COLOR.darker());
                } else if (getModel().isRollover()) {
                    g2d.setColor(BUTTON_COLOR.brighter());
                } else {
                    g2d.setColor(BUTTON_COLOR);
                }
                
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 4, 4);
                
                g2d.setColor(TEXT_COLOR);
                g2d.setFont(new Font("Monospaced", Font.BOLD, 9));
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(label)) / 2;
                int y = (getHeight() + fm.getAscent()) / 2 - 2;
                g2d.drawString(label, x, y);
                
                g2d.dispose();
            }
        };
        
        button.setPreferredSize(new Dimension(32, 24));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addActionListener(e -> handleMenuAction(label));
        
        return button;
    }
    
    private void handleMenuAction(String label) {
        switch (label) {
            case "Y=":
            case "GRAPH":
            case "WINDOW":
            case "ZOOM":
            case "TRACE":
                new GraphWindow(engine);
                break;
            case "MATH":
                showMathMenu();
                break;
            case "APPS":
                showAppsMenu();
                break;
            case "PRGM":
                new SolverWindow(engine);
                break;
            case "STAT":
                new StatisticsWindow();
                break;
        }
    }
    
    private void showMathMenu() {
        JPopupMenu menu = new JPopupMenu();
        menu.setBackground(BG_COLOR);
        
        String[] mathOptions = {"NUM", "CMPLX", "PROB", "FRAC", "MATRIX"};
        
        for (String option : mathOptions) {
            JMenuItem item = new JMenuItem(option);
            item.setBackground(BG_COLOR);
            item.setForeground(TEXT_COLOR);
            item.addActionListener(e -> handleMathOption(option));
            menu.add(item);
        }
        
        menu.show(this, 0, getHeight());
    }
    
    private void handleMathOption(String option) {
        switch (option) {
            case "MATRIX":
                new MatrixWindow();
                break;
            case "PROB":
                new StatisticsWindow();
                break;
            case "NUM":
            case "CMPLX":
            case "FRAC":
                new SolverWindow(engine);
                break;
        }
    }
    
    private void showAppsMenu() {
        JPopupMenu menu = new JPopupMenu();
        menu.setBackground(BG_COLOR);
        
        String[] appOptions = {"Solver", "Matrix", "Statistics", "Graph", "Table"};
        
        for (String option : appOptions) {
            JMenuItem item = new JMenuItem(option);
            item.setBackground(BG_COLOR);
            item.setForeground(TEXT_COLOR);
            item.addActionListener(e -> handleAppOption(option));
            menu.add(item);
        }
        
        menu.show(this, 100, getHeight());
    }
    
    private void handleAppOption(String option) {
        switch (option) {
            case "Solver":
                new SolverWindow(engine);
                break;
            case "Matrix":
                new MatrixWindow();
                break;
            case "Statistics":
                new StatisticsWindow();
                break;
            case "Graph":
            case "Table":
                new GraphWindow(engine);
                break;
        }
    }
}
