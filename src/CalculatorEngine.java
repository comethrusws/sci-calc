import java.util.*;

public class CalculatorEngine {
    private StringBuilder expression;
    private String lastAnswer;
    private String lastExpression;
    private boolean useDegrees;
    private int cursorPosition;
    private boolean insertMode;
    private List<String> history;
    private int historyIndex;
    private Map<String, Double> variables;
    
    public CalculatorEngine() {
        expression = new StringBuilder();
        lastAnswer = "0";
        lastExpression = "";
        useDegrees = true;
        cursorPosition = 0;
        insertMode = false;
        history = new ArrayList<>();
        historyIndex = -1;
        variables = new HashMap<>();
        variables.put("X", 0.0);
        variables.put("Y", 0.0);
        variables.put("A", 0.0);
        variables.put("B", 0.0);
    }
    
    public void appendDigit(String digit) {
        expression.insert(cursorPosition, digit);
        cursorPosition++;
    }
    
    public void appendOperator(String operator) {
        expression.insert(cursorPosition, operator);
        cursorPosition += operator.length();
    }
    
    public void appendFunction(String function) {
        expression.insert(cursorPosition, function);
        cursorPosition += function.length();
    }
    
    public void appendConstant(String constant) {
        expression.insert(cursorPosition, constant);
        cursorPosition += constant.length();
    }
    
    public void appendParenthesis(String paren) {
        expression.insert(cursorPosition, paren);
        cursorPosition++;
    }
    
    public void appendVariable(String var) {
        expression.insert(cursorPosition, var);
        cursorPosition++;
    }
    
    public void appendAns() {
        String ans = lastAnswer;
        expression.insert(cursorPosition, ans);
        cursorPosition += ans.length();
    }
    
    public void negate() {
        if (expression.length() == 0) {
            expression.append("−");
            cursorPosition = 1;
        } else {
            String expr = expression.toString();
            if (expr.startsWith("−")) {
                expression = new StringBuilder(expr.substring(1));
                cursorPosition = Math.max(0, cursorPosition - 1);
            } else {
                expression = new StringBuilder("−" + expr);
                cursorPosition++;
            }
        }
    }
    
    public void delete() {
        if (cursorPosition > 0 && expression.length() > 0) {
            String expr = expression.toString();
            String beforeCursor = expr.substring(0, cursorPosition);
            
            String[] functions = {"sin(", "cos(", "tan(", "asin(", "acos(", "atan(", 
                                  "ln(", "log(", "√(", "exp(", "10^(", "cbrt(", "cube(", "abs("};
            
            for (String func : functions) {
                if (beforeCursor.endsWith(func)) {
                    expression.delete(cursorPosition - func.length(), cursorPosition);
                    cursorPosition -= func.length();
                    return;
                }
            }
            
            expression.deleteCharAt(cursorPosition - 1);
            cursorPosition--;
        }
    }
    
    public void clear() {
        expression = new StringBuilder();
        cursorPosition = 0;
    }
    
    public void toggleAngleMode() {
        useDegrees = !useDegrees;
    }
    
    public void toggleInsertMode() {
        insertMode = !insertMode;
    }
    
    public boolean isUsingDegrees() {
        return useDegrees;
    }
    
    public void moveCursorLeft() {
        if (cursorPosition > 0) cursorPosition--;
    }
    
    public void moveCursorRight() {
        if (cursorPosition < expression.length()) cursorPosition++;
    }
    
    public void historyUp() {
        if (!history.isEmpty() && historyIndex < history.size() - 1) {
            historyIndex++;
            expression = new StringBuilder(history.get(history.size() - 1 - historyIndex));
            cursorPosition = expression.length();
        }
    }
    
    public void historyDown() {
        if (historyIndex > 0) {
            historyIndex--;
            expression = new StringBuilder(history.get(history.size() - 1 - historyIndex));
            cursorPosition = expression.length();
        } else if (historyIndex == 0) {
            historyIndex = -1;
            expression = new StringBuilder();
            cursorPosition = 0;
        }
    }
    
    public String getExpression() {
        return expression.toString();
    }
    
    public String getLastExpression() {
        return lastExpression;
    }
    
    public String getCurrentValue() {
        if (expression.length() == 0) return "0";
        return expression.toString();
    }
    
    public void setVariable(String name, double value) {
        variables.put(name.toUpperCase(), value);
    }
    
    public double getVariable(String name) {
        return variables.getOrDefault(name.toUpperCase(), 0.0);
    }
    
    public String calculate() {
        try {
            String expr = expression.toString();
            if (expr.isEmpty()) return "0";
            
            lastExpression = expr;
            history.add(expr);
            historyIndex = -1;
            
            double result = evaluate(expr);
            
            if (Double.isNaN(result)) {
                return "Error";
            }
            if (Double.isInfinite(result)) {
                return result > 0 ? "∞" : "−∞";
            }
            
            lastAnswer = formatResult(result);
            expression = new StringBuilder();
            cursorPosition = 0;
            return lastAnswer;
        } catch (Exception e) {
            return "Error";
        }
    }
    
    public double evaluateExpression(String expr) {
        try {
            return evaluate(expr);
        } catch (Exception e) {
            return Double.NaN;
        }
    }
    
    public double evaluateWithVariable(String expr, String varName, double value) {
        double oldValue = variables.getOrDefault(varName.toUpperCase(), 0.0);
        variables.put(varName.toUpperCase(), value);
        double result = evaluateExpression(expr);
        variables.put(varName.toUpperCase(), oldValue);
        return result;
    }
    
    private String formatResult(double result) {
        if (result == (long) result && Math.abs(result) < 1e10) {
            return String.valueOf((long) result);
        }
        
        if (Math.abs(result) < 1e-10 && result != 0) {
            return String.format("%.6e", result);
        }
        if (Math.abs(result) >= 1e10) {
            return String.format("%.6e", result);
        }
        
        String formatted = String.format("%.10f", result);
        formatted = formatted.replaceAll("0+$", "");
        formatted = formatted.replaceAll("\\.$", "");
        return formatted;
    }
    
    private double evaluate(String expr) {
        expr = preprocessExpression(expr);
        return parseExpression(expr);
    }
    
    private String preprocessExpression(String expr) {
        expr = expr.replace("π", String.valueOf(Math.PI));
        
        for (Map.Entry<String, Double> entry : variables.entrySet()) {
            expr = expr.replace(entry.getKey().toLowerCase(), String.valueOf(entry.getValue()));
            expr = expr.replace(entry.getKey().toUpperCase(), String.valueOf(entry.getValue()));
        }
        
        expr = expr.replace("−", "-");
        expr = expr.replace("×", "*");
        expr = expr.replace("÷", "/");
        expr = expr.replace("²", "^2");
        expr = expr.replace("⁻¹", "^(-1)");
        expr = expr.replace("√", "sqrt");
        
        expr = expr.replaceAll("(\\d)\\(", "$1*(");
        expr = expr.replaceAll("\\)(\\d)", ")*$1");
        expr = expr.replaceAll("\\)\\(", ")*(");
        expr = expr.replaceAll("(\\d)(sin|cos|tan|asin|acos|atan|ln|log|sqrt|exp|cbrt|cube|abs)", "$1*$2");
        
        return expr;
    }
    
    private double parseExpression(String expr) {
        return new ExpressionParser(expr, useDegrees).parse();
    }
}

class ExpressionParser {
    private String expression;
    private int pos;
    private boolean useDegrees;
    
    public ExpressionParser(String expression, boolean useDegrees) {
        this.expression = expression.replaceAll("\\s+", "");
        this.pos = 0;
        this.useDegrees = useDegrees;
    }
    
    public double parse() {
        double result = parseAddSubtract();
        if (pos < expression.length()) {
            char ch = expression.charAt(pos);
            if (ch != ')' && ch != '}' && ch != ']') {
                throw new RuntimeException("Unexpected character: " + ch);
            }
        }
        return result;
    }
    
    private double parseAddSubtract() {
        double left = parseMultiplyDivide();
        
        while (pos < expression.length()) {
            char op = expression.charAt(pos);
            if (op != '+' && op != '-') break;
            pos++;
            double right = parseMultiplyDivide();
            if (op == '+') left += right;
            else left -= right;
        }
        return left;
    }
    
    private double parseMultiplyDivide() {
        double left = parsePower();
        
        while (pos < expression.length()) {
            char op = expression.charAt(pos);
            if (op != '*' && op != '/') break;
            pos++;
            double right = parsePower();
            if (op == '*') left *= right;
            else left /= right;
        }
        return left;
    }
    
    private double parsePower() {
        double left = parseUnary();
        
        while (pos < expression.length() && expression.charAt(pos) == '^') {
            pos++;
            double right = parseUnary();
            left = Math.pow(left, right);
        }
        return left;
    }
    
    private double parseUnary() {
        if (pos < expression.length()) {
            if (expression.charAt(pos) == '-') {
                pos++;
                return -parseFactor();
            }
            if (expression.charAt(pos) == '+') {
                pos++;
                return parseFactor();
            }
        }
        return parseFactor();
    }
    
    private double parseFactor() {
        if (pos >= expression.length()) {
            throw new RuntimeException("Unexpected end of expression");
        }
        
        String[] functions = {"asin", "acos", "atan", "sinh", "cosh", "tanh",
                              "sin", "cos", "tan", "ln", "log", "sqrt", "exp", 
                              "cbrt", "cube", "abs", "10^", "floor", "ceil", "round"};
        
        for (String func : functions) {
            if (expression.substring(pos).startsWith(func)) {
                pos += func.length();
                double arg = parseFactor();
                return applyFunction(func, arg);
            }
        }
        
        if (expression.charAt(pos) == '(' || expression.charAt(pos) == '{') {
            char openParen = expression.charAt(pos);
            char closeParen = openParen == '(' ? ')' : '}';
            pos++;
            double result = parseAddSubtract();
            if (pos >= expression.length() || expression.charAt(pos) != closeParen) {
                throw new RuntimeException("Missing closing parenthesis");
            }
            pos++;
            return result;
        }
        
        return parseNumber();
    }
    
    private double parseNumber() {
        int startPos = pos;
        
        while (pos < expression.length() && 
               (Character.isDigit(expression.charAt(pos)) || 
                expression.charAt(pos) == '.' ||
                expression.charAt(pos) == 'E' ||
                (expression.charAt(pos) == '-' && pos > startPos && 
                 expression.charAt(pos - 1) == 'E'))) {
            pos++;
        }
        
        if (startPos == pos) {
            throw new RuntimeException("Expected number at position " + pos);
        }
        
        return Double.parseDouble(expression.substring(startPos, pos));
    }
    
    private double applyFunction(String func, double arg) {
        switch (func) {
            case "sin":
                return Math.sin(useDegrees ? Math.toRadians(arg) : arg);
            case "cos":
                return Math.cos(useDegrees ? Math.toRadians(arg) : arg);
            case "tan":
                return Math.tan(useDegrees ? Math.toRadians(arg) : arg);
            case "asin":
                double asinResult = Math.asin(arg);
                return useDegrees ? Math.toDegrees(asinResult) : asinResult;
            case "acos":
                double acosResult = Math.acos(arg);
                return useDegrees ? Math.toDegrees(acosResult) : acosResult;
            case "atan":
                double atanResult = Math.atan(arg);
                return useDegrees ? Math.toDegrees(atanResult) : atanResult;
            case "sinh":
                return Math.sinh(arg);
            case "cosh":
                return Math.cosh(arg);
            case "tanh":
                return Math.tanh(arg);
            case "ln":
                return Math.log(arg);
            case "log":
                return Math.log10(arg);
            case "sqrt":
                return Math.sqrt(arg);
            case "exp":
                return Math.exp(arg);
            case "10^":
                return Math.pow(10, arg);
            case "cbrt":
                return Math.cbrt(arg);
            case "cube":
                return Math.pow(arg, 3);
            case "abs":
                return Math.abs(arg);
            case "floor":
                return Math.floor(arg);
            case "ceil":
                return Math.ceil(arg);
            case "round":
                return Math.round(arg);
            default:
                throw new RuntimeException("Unknown function: " + func);
        }
    }
}
