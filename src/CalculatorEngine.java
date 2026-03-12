import java.util.*;
import java.util.regex.*;

public class CalculatorEngine {
    private StringBuilder expression;
    private String lastAnswer;
    private boolean useDegrees;
    
    public CalculatorEngine() {
        expression = new StringBuilder();
        lastAnswer = "0";
        useDegrees = true;
    }
    
    public void appendDigit(String digit) {
        expression.append(digit);
    }
    
    public void appendOperator(String operator) {
        if (expression.length() > 0) {
            expression.append(operator);
        } else if (operator.equals("−")) {
            expression.append("−");
        }
    }
    
    public void appendFunction(String function) {
        expression.append(function);
    }
    
    public void appendConstant(String constant) {
        expression.append(constant);
    }
    
    public void appendParenthesis(String paren) {
        expression.append(paren);
    }
    
    public void appendAns() {
        expression.append(lastAnswer);
    }
    
    public void negate() {
        if (expression.length() == 0) {
            expression.append("−");
        } else {
            String expr = expression.toString();
            if (expr.startsWith("−")) {
                expression = new StringBuilder(expr.substring(1));
            } else {
                expression = new StringBuilder("−" + expr);
            }
        }
    }
    
    public void delete() {
        if (expression.length() > 0) {
            String expr = expression.toString();
            String[] functions = {"sin(", "cos(", "tan(", "asin(", "acos(", "atan(", 
                                  "ln(", "log(", "√(", "exp(", "10^(", "cbrt(", "cube(", "abs("};
            
            for (String func : functions) {
                if (expr.endsWith(func)) {
                    expression = new StringBuilder(expr.substring(0, expr.length() - func.length()));
                    return;
                }
            }
            expression.deleteCharAt(expression.length() - 1);
        }
    }
    
    public void clear() {
        expression = new StringBuilder();
    }
    
    public void toggleAngleMode() {
        useDegrees = !useDegrees;
    }
    
    public String getExpression() {
        return expression.toString();
    }
    
    public String getCurrentValue() {
        if (expression.length() == 0) return "0";
        return expression.toString();
    }
    
    public String calculate() {
        try {
            String expr = expression.toString();
            if (expr.isEmpty()) return "0";
            
            double result = evaluate(expr);
            
            if (Double.isNaN(result)) {
                return "Error";
            }
            if (Double.isInfinite(result)) {
                return result > 0 ? "∞" : "−∞";
            }
            
            lastAnswer = formatResult(result);
            expression = new StringBuilder();
            return lastAnswer;
        } catch (Exception e) {
            return "Error";
        }
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
        expr = expr.replace("e", String.valueOf(Math.E));
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
            throw new RuntimeException("Unexpected character: " + expression.charAt(pos));
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
        
        String[] functions = {"asin", "acos", "atan", "sin", "cos", "tan", 
                              "ln", "log", "sqrt", "exp", "cbrt", "cube", "abs", "10^"};
        
        for (String func : functions) {
            if (expression.substring(pos).startsWith(func)) {
                pos += func.length();
                double arg = parseFactor();
                return applyFunction(func, arg);
            }
        }
        
        if (expression.charAt(pos) == '(') {
            pos++;
            double result = parseAddSubtract();
            if (pos >= expression.length() || expression.charAt(pos) != ')') {
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
            default:
                throw new RuntimeException("Unknown function: " + func);
        }
    }
}
