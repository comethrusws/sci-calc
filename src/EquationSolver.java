import java.util.*;

public class EquationSolver {
    private CalculatorEngine engine;
    
    public EquationSolver(CalculatorEngine engine) {
        this.engine = engine;
    }
    
    public double[] solveLinear(double a, double b) {
        if (a == 0) {
            return null;
        }
        return new double[]{-b / a};
    }
    
    public double[] solveQuadratic(double a, double b, double c) {
        if (a == 0) {
            return solveLinear(b, c);
        }
        
        double discriminant = b * b - 4 * a * c;
        
        if (discriminant < 0) {
            return new double[]{Double.NaN, Double.NaN, 
                -b / (2 * a), Math.sqrt(-discriminant) / (2 * a)};
        } else if (discriminant == 0) {
            return new double[]{-b / (2 * a)};
        } else {
            double sqrtD = Math.sqrt(discriminant);
            return new double[]{(-b + sqrtD) / (2 * a), (-b - sqrtD) / (2 * a)};
        }
    }
    
    public double[] solveCubic(double a, double b, double c, double d) {
        if (a == 0) {
            return solveQuadratic(b, c, d);
        }
        
        b /= a;
        c /= a;
        d /= a;
        
        double p = c - b * b / 3;
        double q = 2 * b * b * b / 27 - b * c / 3 + d;
        double discriminant = q * q / 4 + p * p * p / 27;
        
        List<Double> roots = new ArrayList<>();
        
        if (discriminant > 0) {
            double sqrtD = Math.sqrt(discriminant);
            double u = Math.cbrt(-q / 2 + sqrtD);
            double v = Math.cbrt(-q / 2 - sqrtD);
            roots.add(u + v - b / 3);
        } else if (discriminant == 0) {
            double u = Math.cbrt(-q / 2);
            roots.add(2 * u - b / 3);
            roots.add(-u - b / 3);
        } else {
            double r = Math.sqrt(-p * p * p / 27);
            double theta = Math.acos(-q / (2 * r));
            double cbrtR = Math.cbrt(r);
            
            roots.add(2 * cbrtR * Math.cos(theta / 3) - b / 3);
            roots.add(2 * cbrtR * Math.cos((theta + 2 * Math.PI) / 3) - b / 3);
            roots.add(2 * cbrtR * Math.cos((theta + 4 * Math.PI) / 3) - b / 3);
        }
        
        return roots.stream().mapToDouble(Double::doubleValue).toArray();
    }
    
    public double[][] solveLinearSystem2x2(double a1, double b1, double c1,
                                           double a2, double b2, double c2) {
        double det = a1 * b2 - a2 * b1;
        
        if (Math.abs(det) < 1e-10) {
            return null;
        }
        
        double x = (c1 * b2 - c2 * b1) / det;
        double y = (a1 * c2 - a2 * c1) / det;
        
        return new double[][]{{x, y}};
    }
    
    public double[][] solveLinearSystem3x3(double[][] coefficients, double[] constants) {
        int n = 3;
        double[][] augmented = new double[n][n + 1];
        
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                augmented[i][j] = coefficients[i][j];
            }
            augmented[i][n] = constants[i];
        }
        
        for (int col = 0; col < n; col++) {
            int maxRow = col;
            for (int row = col + 1; row < n; row++) {
                if (Math.abs(augmented[row][col]) > Math.abs(augmented[maxRow][col])) {
                    maxRow = row;
                }
            }
            
            double[] temp = augmented[col];
            augmented[col] = augmented[maxRow];
            augmented[maxRow] = temp;
            
            if (Math.abs(augmented[col][col]) < 1e-10) {
                return null;
            }
            
            for (int row = col + 1; row < n; row++) {
                double factor = augmented[row][col] / augmented[col][col];
                for (int j = col; j <= n; j++) {
                    augmented[row][j] -= factor * augmented[col][j];
                }
            }
        }
        
        double[] solution = new double[n];
        for (int i = n - 1; i >= 0; i--) {
            solution[i] = augmented[i][n];
            for (int j = i + 1; j < n; j++) {
                solution[i] -= augmented[i][j] * solution[j];
            }
            solution[i] /= augmented[i][i];
        }
        
        return new double[][]{solution};
    }
    
    public double solveNewtonRaphson(String expression, double initialGuess) {
        double x = initialGuess;
        double tolerance = 1e-10;
        int maxIterations = 100;
        
        for (int i = 0; i < maxIterations; i++) {
            double fx = evaluateAt(expression, x);
            double fpx = derivativeAt(expression, x);
            
            if (Math.abs(fpx) < 1e-15) {
                break;
            }
            
            double newX = x - fx / fpx;
            
            if (Math.abs(newX - x) < tolerance) {
                return newX;
            }
            
            x = newX;
        }
        
        return x;
    }
    
    public double[] findAllRoots(String expression, double min, double max) {
        List<Double> roots = new ArrayList<>();
        double step = (max - min) / 1000;
        
        for (double x = min; x < max; x += step) {
            double y1 = evaluateAt(expression, x);
            double y2 = evaluateAt(expression, x + step);
            
            if (!Double.isNaN(y1) && !Double.isNaN(y2) && y1 * y2 <= 0) {
                double root = solveNewtonRaphson(expression, (x + x + step) / 2);
                
                boolean isDuplicate = false;
                for (double existingRoot : roots) {
                    if (Math.abs(root - existingRoot) < 1e-6) {
                        isDuplicate = true;
                        break;
                    }
                }
                
                if (!isDuplicate && root >= min && root <= max) {
                    roots.add(root);
                }
            }
        }
        
        return roots.stream().mapToDouble(Double::doubleValue).toArray();
    }
    
    private double evaluateAt(String expression, double x) {
        String expr = expression.replaceAll("(?i)x", "(" + x + ")");
        return engine.evaluateExpression(expr);
    }
    
    private double derivativeAt(String expression, double x) {
        double h = 1e-8;
        return (evaluateAt(expression, x + h) - evaluateAt(expression, x - h)) / (2 * h);
    }
    
    public TrigSolution solveTrigEquation(String type, double value, boolean useDegrees) {
        TrigSolution solution = new TrigSolution();
        
        switch (type.toLowerCase()) {
            case "sin":
                if (Math.abs(value) > 1) {
                    solution.hasSolution = false;
                    return solution;
                }
                double asinVal = Math.asin(value);
                solution.principalValue = useDegrees ? Math.toDegrees(asinVal) : asinVal;
                solution.generalForm = useDegrees ? 
                    String.format("x = %.4f° + 360°n or x = %.4f° + 360°n", 
                        solution.principalValue, 180 - solution.principalValue) :
                    String.format("x = %.4f + 2πn or x = %.4f + 2πn", 
                        solution.principalValue, Math.PI - solution.principalValue);
                break;
                
            case "cos":
                if (Math.abs(value) > 1) {
                    solution.hasSolution = false;
                    return solution;
                }
                double acosVal = Math.acos(value);
                solution.principalValue = useDegrees ? Math.toDegrees(acosVal) : acosVal;
                solution.generalForm = useDegrees ?
                    String.format("x = ±%.4f° + 360°n", solution.principalValue) :
                    String.format("x = ±%.4f + 2πn", solution.principalValue);
                break;
                
            case "tan":
                double atanVal = Math.atan(value);
                solution.principalValue = useDegrees ? Math.toDegrees(atanVal) : atanVal;
                solution.generalForm = useDegrees ?
                    String.format("x = %.4f° + 180°n", solution.principalValue) :
                    String.format("x = %.4f + πn", solution.principalValue);
                break;
        }
        
        solution.hasSolution = true;
        return solution;
    }
    
    public double[] solveTrigInRange(String type, double value, double min, double max, boolean useDegrees) {
        List<Double> solutions = new ArrayList<>();
        TrigSolution general = solveTrigEquation(type, value, useDegrees);
        
        if (!general.hasSolution) {
            return new double[0];
        }
        
        double period = useDegrees ? 360 : 2 * Math.PI;
        double halfPeriod = useDegrees ? 180 : Math.PI;
        
        switch (type.toLowerCase()) {
            case "sin":
                for (int n = -100; n <= 100; n++) {
                    double x1 = general.principalValue + period * n;
                    double x2 = (useDegrees ? 180 : Math.PI) - general.principalValue + period * n;
                    
                    if (x1 >= min && x1 <= max) solutions.add(x1);
                    if (x2 >= min && x2 <= max && Math.abs(x1 - x2) > 1e-10) solutions.add(x2);
                }
                break;
                
            case "cos":
                for (int n = -100; n <= 100; n++) {
                    double x1 = general.principalValue + period * n;
                    double x2 = -general.principalValue + period * n;
                    
                    if (x1 >= min && x1 <= max) solutions.add(x1);
                    if (x2 >= min && x2 <= max && Math.abs(x1 - x2) > 1e-10) solutions.add(x2);
                }
                break;
                
            case "tan":
                for (int n = -100; n <= 100; n++) {
                    double x = general.principalValue + halfPeriod * n;
                    if (x >= min && x <= max) solutions.add(x);
                }
                break;
        }
        
        Collections.sort(solutions);
        return solutions.stream().mapToDouble(Double::doubleValue).toArray();
    }
}

class TrigSolution {
    boolean hasSolution = true;
    double principalValue;
    String generalForm;
}
