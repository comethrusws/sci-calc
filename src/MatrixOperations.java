public class MatrixOperations {
    
    public double[][] add(double[][] a, double[][] b) {
        if (a.length != b.length || a[0].length != b[0].length) {
            return null;
        }
        
        int rows = a.length;
        int cols = a[0].length;
        double[][] result = new double[rows][cols];
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[i][j] = a[i][j] + b[i][j];
            }
        }
        return result;
    }
    
    public double[][] subtract(double[][] a, double[][] b) {
        if (a.length != b.length || a[0].length != b[0].length) {
            return null;
        }
        
        int rows = a.length;
        int cols = a[0].length;
        double[][] result = new double[rows][cols];
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[i][j] = a[i][j] - b[i][j];
            }
        }
        return result;
    }
    
    public double[][] multiply(double[][] a, double[][] b) {
        if (a[0].length != b.length) {
            return null;
        }
        
        int rowsA = a.length;
        int colsA = a[0].length;
        int colsB = b[0].length;
        double[][] result = new double[rowsA][colsB];
        
        for (int i = 0; i < rowsA; i++) {
            for (int j = 0; j < colsB; j++) {
                for (int k = 0; k < colsA; k++) {
                    result[i][j] += a[i][k] * b[k][j];
                }
            }
        }
        return result;
    }
    
    public double[][] scalarMultiply(double[][] matrix, double scalar) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        double[][] result = new double[rows][cols];
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[i][j] = matrix[i][j] * scalar;
            }
        }
        return result;
    }
    
    public double[][] transpose(double[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        double[][] result = new double[cols][rows];
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[j][i] = matrix[i][j];
            }
        }
        return result;
    }
    
    public double determinant(double[][] matrix) {
        int n = matrix.length;
        
        if (n == 1) {
            return matrix[0][0];
        }
        
        if (n == 2) {
            return matrix[0][0] * matrix[1][1] - matrix[0][1] * matrix[1][0];
        }
        
        double det = 0;
        for (int j = 0; j < n; j++) {
            det += Math.pow(-1, j) * matrix[0][j] * determinant(minor(matrix, 0, j));
        }
        return det;
    }
    
    private double[][] minor(double[][] matrix, int row, int col) {
        int n = matrix.length;
        double[][] result = new double[n - 1][n - 1];
        int r = 0;
        
        for (int i = 0; i < n; i++) {
            if (i == row) continue;
            int c = 0;
            for (int j = 0; j < n; j++) {
                if (j == col) continue;
                result[r][c] = matrix[i][j];
                c++;
            }
            r++;
        }
        return result;
    }
    
    public double[][] inverse(double[][] matrix) {
        int n = matrix.length;
        double det = determinant(matrix);
        
        if (Math.abs(det) < 1e-10) {
            return null;
        }
        
        double[][] adjugate = new double[n][n];
        
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                adjugate[j][i] = Math.pow(-1, i + j) * determinant(minor(matrix, i, j));
            }
        }
        
        return scalarMultiply(adjugate, 1.0 / det);
    }
    
    public double[][] rref(double[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        double[][] result = new double[rows][cols];
        
        for (int i = 0; i < rows; i++) {
            result[i] = matrix[i].clone();
        }
        
        int lead = 0;
        for (int r = 0; r < rows; r++) {
            if (lead >= cols) break;
            
            int i = r;
            while (Math.abs(result[i][lead]) < 1e-10) {
                i++;
                if (i == rows) {
                    i = r;
                    lead++;
                    if (lead == cols) return result;
                }
            }
            
            double[] temp = result[r];
            result[r] = result[i];
            result[i] = temp;
            
            double div = result[r][lead];
            for (int j = 0; j < cols; j++) {
                result[r][j] /= div;
            }
            
            for (i = 0; i < rows; i++) {
                if (i != r) {
                    double mult = result[i][lead];
                    for (int j = 0; j < cols; j++) {
                        result[i][j] -= mult * result[r][j];
                    }
                }
            }
            lead++;
        }
        return result;
    }
    
    public double[] eigenvalues2x2(double[][] matrix) {
        double a = matrix[0][0];
        double b = matrix[0][1];
        double c = matrix[1][0];
        double d = matrix[1][1];
        
        double trace = a + d;
        double det = a * d - b * c;
        double discriminant = trace * trace - 4 * det;
        
        if (discriminant < 0) {
            return new double[]{trace / 2, Math.sqrt(-discriminant) / 2};
        }
        
        double sqrtD = Math.sqrt(discriminant);
        return new double[]{(trace + sqrtD) / 2, (trace - sqrtD) / 2};
    }
    
    public double[][] power(double[][] matrix, int n) {
        if (n == 0) {
            return identity(matrix.length);
        }
        
        if (n == 1) {
            return matrix;
        }
        
        if (n < 0) {
            double[][] inv = inverse(matrix);
            if (inv == null) return null;
            return power(inv, -n);
        }
        
        double[][] result = identity(matrix.length);
        double[][] base = matrix;
        
        while (n > 0) {
            if (n % 2 == 1) {
                result = multiply(result, base);
            }
            base = multiply(base, base);
            n /= 2;
        }
        return result;
    }
    
    public double[][] identity(int n) {
        double[][] result = new double[n][n];
        for (int i = 0; i < n; i++) {
            result[i][i] = 1;
        }
        return result;
    }
    
    public double trace(double[][] matrix) {
        double sum = 0;
        for (int i = 0; i < matrix.length; i++) {
            sum += matrix[i][i];
        }
        return sum;
    }
    
    public int rank(double[][] matrix) {
        double[][] rrefMatrix = rref(matrix);
        int rank = 0;
        
        for (double[] row : rrefMatrix) {
            boolean isZeroRow = true;
            for (double val : row) {
                if (Math.abs(val) > 1e-10) {
                    isZeroRow = false;
                    break;
                }
            }
            if (!isZeroRow) rank++;
        }
        return rank;
    }
}
