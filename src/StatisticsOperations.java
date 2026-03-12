import java.util.*;

public class StatisticsOperations {
    
    public double mean(double[] data) {
        double sum = 0;
        for (double val : data) {
            sum += val;
        }
        return sum / data.length;
    }
    
    public double median(double[] data) {
        double[] sorted = data.clone();
        Arrays.sort(sorted);
        
        int n = sorted.length;
        if (n % 2 == 0) {
            return (sorted[n / 2 - 1] + sorted[n / 2]) / 2;
        }
        return sorted[n / 2];
    }
    
    public double[] mode(double[] data) {
        Map<Double, Integer> frequencyMap = new HashMap<>();
        int maxFreq = 0;
        
        for (double val : data) {
            int freq = frequencyMap.getOrDefault(val, 0) + 1;
            frequencyMap.put(val, freq);
            maxFreq = Math.max(maxFreq, freq);
        }
        
        List<Double> modes = new ArrayList<>();
        for (Map.Entry<Double, Integer> entry : frequencyMap.entrySet()) {
            if (entry.getValue() == maxFreq) {
                modes.add(entry.getKey());
            }
        }
        
        return modes.stream().mapToDouble(Double::doubleValue).toArray();
    }
    
    public double variance(double[] data, boolean population) {
        double mean = mean(data);
        double sumSquaredDiff = 0;
        
        for (double val : data) {
            sumSquaredDiff += Math.pow(val - mean, 2);
        }
        
        return sumSquaredDiff / (population ? data.length : data.length - 1);
    }
    
    public double standardDeviation(double[] data, boolean population) {
        return Math.sqrt(variance(data, population));
    }
    
    public double sum(double[] data) {
        double total = 0;
        for (double val : data) {
            total += val;
        }
        return total;
    }
    
    public double sumOfSquares(double[] data) {
        double total = 0;
        for (double val : data) {
            total += val * val;
        }
        return total;
    }
    
    public double min(double[] data) {
        double min = Double.MAX_VALUE;
        for (double val : data) {
            if (val < min) min = val;
        }
        return min;
    }
    
    public double max(double[] data) {
        double max = Double.MIN_VALUE;
        for (double val : data) {
            if (val > max) max = val;
        }
        return max;
    }
    
    public double range(double[] data) {
        return max(data) - min(data);
    }
    
    public double[] fiveNumberSummary(double[] data) {
        double[] sorted = data.clone();
        Arrays.sort(sorted);
        
        double min = sorted[0];
        double max = sorted[sorted.length - 1];
        double median = median(sorted);
        
        int n = sorted.length;
        double[] lowerHalf = Arrays.copyOfRange(sorted, 0, n / 2);
        double[] upperHalf = n % 2 == 0 ? 
            Arrays.copyOfRange(sorted, n / 2, n) :
            Arrays.copyOfRange(sorted, n / 2 + 1, n);
        
        double q1 = median(lowerHalf);
        double q3 = median(upperHalf);
        
        return new double[]{min, q1, median, q3, max};
    }
    
    public double iqr(double[] data) {
        double[] summary = fiveNumberSummary(data);
        return summary[3] - summary[1];
    }
    
    public double[] linearRegression(double[] x, double[] y) {
        int n = x.length;
        double sumX = sum(x);
        double sumY = sum(y);
        double sumXY = 0;
        double sumX2 = sumOfSquares(x);
        
        for (int i = 0; i < n; i++) {
            sumXY += x[i] * y[i];
        }
        
        double slope = (n * sumXY - sumX * sumY) / (n * sumX2 - sumX * sumX);
        double intercept = (sumY - slope * sumX) / n;
        
        double ssRes = 0;
        double ssTot = 0;
        double meanY = sumY / n;
        
        for (int i = 0; i < n; i++) {
            double predicted = slope * x[i] + intercept;
            ssRes += Math.pow(y[i] - predicted, 2);
            ssTot += Math.pow(y[i] - meanY, 2);
        }
        
        double rSquared = 1 - ssRes / ssTot;
        double r = Math.sqrt(rSquared) * (slope >= 0 ? 1 : -1);
        
        return new double[]{slope, intercept, r, rSquared};
    }
    
    public double[] quadraticRegression(double[] x, double[] y) {
        int n = x.length;
        double sumX = 0, sumX2 = 0, sumX3 = 0, sumX4 = 0;
        double sumY = 0, sumXY = 0, sumX2Y = 0;
        
        for (int i = 0; i < n; i++) {
            double x2 = x[i] * x[i];
            double x3 = x2 * x[i];
            double x4 = x2 * x2;
            
            sumX += x[i];
            sumX2 += x2;
            sumX3 += x3;
            sumX4 += x4;
            sumY += y[i];
            sumXY += x[i] * y[i];
            sumX2Y += x2 * y[i];
        }
        
        double[][] matrix = {
            {n, sumX, sumX2},
            {sumX, sumX2, sumX3},
            {sumX2, sumX3, sumX4}
        };
        
        double[] constants = {sumY, sumXY, sumX2Y};
        
        MatrixOperations matOps = new MatrixOperations();
        double det = matOps.determinant(matrix);
        
        if (Math.abs(det) < 1e-10) return null;
        
        double[][] matrixA = {
            {sumY, sumX, sumX2},
            {sumXY, sumX2, sumX3},
            {sumX2Y, sumX3, sumX4}
        };
        double a = matOps.determinant(matrixA) / det;
        
        double[][] matrixB = {
            {n, sumY, sumX2},
            {sumX, sumXY, sumX3},
            {sumX2, sumX2Y, sumX4}
        };
        double b = matOps.determinant(matrixB) / det;
        
        double[][] matrixC = {
            {n, sumX, sumY},
            {sumX, sumX2, sumXY},
            {sumX2, sumX3, sumX2Y}
        };
        double c = matOps.determinant(matrixC) / det;
        
        double ssRes = 0;
        double ssTot = 0;
        double meanY = sumY / n;
        
        for (int i = 0; i < n; i++) {
            double predicted = a + b * x[i] + c * x[i] * x[i];
            ssRes += Math.pow(y[i] - predicted, 2);
            ssTot += Math.pow(y[i] - meanY, 2);
        }
        
        double rSquared = 1 - ssRes / ssTot;
        
        return new double[]{c, b, a, rSquared};
    }
    
    public double[] exponentialRegression(double[] x, double[] y) {
        double[] lnY = new double[y.length];
        for (int i = 0; i < y.length; i++) {
            if (y[i] <= 0) return null;
            lnY[i] = Math.log(y[i]);
        }
        
        double[] linReg = linearRegression(x, lnY);
        double a = Math.exp(linReg[1]);
        double b = linReg[0];
        
        return new double[]{a, b, linReg[2], linReg[3]};
    }
    
    public double correlation(double[] x, double[] y) {
        double[] reg = linearRegression(x, y);
        return reg[2];
    }
    
    public long factorial(int n) {
        if (n < 0) return -1;
        if (n <= 1) return 1;
        
        long result = 1;
        for (int i = 2; i <= n; i++) {
            result *= i;
        }
        return result;
    }
    
    public long permutation(int n, int r) {
        if (n < 0 || r < 0 || r > n) return -1;
        return factorial(n) / factorial(n - r);
    }
    
    public long combination(int n, int r) {
        if (n < 0 || r < 0 || r > n) return -1;
        return factorial(n) / (factorial(r) * factorial(n - r));
    }
    
    public double binomialPdf(int n, int k, double p) {
        return combination(n, k) * Math.pow(p, k) * Math.pow(1 - p, n - k);
    }
    
    public double binomialCdf(int n, int k, double p) {
        double sum = 0;
        for (int i = 0; i <= k; i++) {
            sum += binomialPdf(n, i, p);
        }
        return sum;
    }
    
    public double normalPdf(double x, double mean, double stdDev) {
        double coefficient = 1 / (stdDev * Math.sqrt(2 * Math.PI));
        double exponent = -Math.pow(x - mean, 2) / (2 * stdDev * stdDev);
        return coefficient * Math.exp(exponent);
    }
    
    public double normalCdf(double x, double mean, double stdDev) {
        return 0.5 * (1 + erf((x - mean) / (stdDev * Math.sqrt(2))));
    }
    
    private double erf(double x) {
        double a1 =  0.254829592;
        double a2 = -0.284496736;
        double a3 =  1.421413741;
        double a4 = -1.453152027;
        double a5 =  1.061405429;
        double p  =  0.3275911;
        
        int sign = x < 0 ? -1 : 1;
        x = Math.abs(x);
        
        double t = 1.0 / (1.0 + p * x);
        double y = 1.0 - (((((a5 * t + a4) * t) + a3) * t + a2) * t + a1) * t * Math.exp(-x * x);
        
        return sign * y;
    }
    
    public double invNorm(double probability, double mean, double stdDev) {
        if (probability <= 0 || probability >= 1) return Double.NaN;
        
        double z = invStandardNormal(probability);
        return mean + z * stdDev;
    }
    
    private double invStandardNormal(double p) {
        double a1 = -39.6968302866538, a2 = 220.946098424521, a3 = -275.928510446969;
        double a4 = 138.357751867269, a5 = -30.6647980661472, a6 = 2.50662823884;
        double b1 = -54.4760987982241, b2 = 161.585836858041, b3 = -155.698979859887;
        double b4 = 66.8013118877197, b5 = -13.2806815528857;
        double c1 = -0.00778489400243029, c2 = -0.322396458041136, c3 = -2.40075827716184;
        double c4 = -2.54973253934373, c5 = 4.37466414146497, c6 = 2.93816398269878;
        double d1 = 0.00778469570904146, d2 = 0.32246712907004, d3 = 2.445134137143;
        double d4 = 3.75440866190742;
        
        double pLow = 0.02425, pHigh = 1 - pLow;
        double q, r;
        
        if (p < pLow) {
            q = Math.sqrt(-2 * Math.log(p));
            return (((((c1 * q + c2) * q + c3) * q + c4) * q + c5) * q + c6) /
                   ((((d1 * q + d2) * q + d3) * q + d4) * q + 1);
        } else if (p <= pHigh) {
            q = p - 0.5;
            r = q * q;
            return (((((a1 * r + a2) * r + a3) * r + a4) * r + a5) * r + a6) * q /
                   (((((b1 * r + b2) * r + b3) * r + b4) * r + b5) * r + 1);
        } else {
            q = Math.sqrt(-2 * Math.log(1 - p));
            return -(((((c1 * q + c2) * q + c3) * q + c4) * q + c5) * q + c6) /
                    ((((d1 * q + d2) * q + d3) * q + d4) * q + 1);
        }
    }
}
