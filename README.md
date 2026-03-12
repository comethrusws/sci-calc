# sci-calc

a scientific calculator built in java swing, inspired by the ti-84 plus ce graphing calculator.

this is a mini project for 7th semester.

## about

this calculator replicates the look and functionality of a ti-84 calculator. it supports basic arithmetic, scientific functions, graphing, equation solving, matrix operations, and statistical analysis.

## features

### calculator
- basic arithmetic operations
- trigonometric functions (sin, cos, tan and their inverses)
- logarithmic functions (ln, log)
- exponential and power functions
- constants (pi, e)
- degree/radian mode toggle
- answer recall (ans)
- expression history

### graphing
- plot up to 6 functions simultaneously
- zoom controls (standard, trig, zoom in/out)
- trace mode to follow graph points
- calculus operations:
  - find zeros
  - find minimum/maximum
  - calculate derivatives
  - evaluate integrals
  - find intersections

### equation solver
- quadratic and cubic equation solver
- 2x2 and 3x3 linear system solver
- trigonometric equation solver with general solutions
- numeric solver using newton-raphson method

### matrix operations
- matrix addition, subtraction, multiplication
- determinant and inverse
- row reduced echelon form (rref)
- transpose, trace, rank
- matrix powers
- eigenvalue approximation

### statistics
- 1-variable statistics (mean, median, std dev, etc.)
- regression analysis (linear, quadratic, exponential)
- probability calculations (permutations, combinations, factorial)
- distribution functions (normal pdf/cdf, binomial, poisson)

## how to run

compile:
```
javac -d bin src/*.java
```

run:
```
java -cp bin Main
```

## structure

```
src/
  Main.java                 - entry point
  CalculatorFrame.java      - main window
  DisplayPanel.java         - lcd display
  ButtonPanel.java          - calculator buttons
  MenuPanel.java            - top menu bar
  CalculatorEngine.java     - expression parser and evaluator
  GraphPanel.java           - graph rendering
  GraphWindow.java          - graphing interface
  EquationSolver.java       - equation solving algorithms
  SolverWindow.java         - solver interface
  MatrixOperations.java     - matrix math
  MatrixWindow.java         - matrix interface
  StatisticsOperations.java - statistics calculations
  StatisticsWindow.java     - statistics interface
```

## controls

- `2nd` - access secondary functions (shown in blue above buttons)
- `mode` - toggle between degree and radian mode
- `del` - delete last character
- `clear` - clear expression
- `y=` / `graph` - open graphing window
- `stat` - open statistics window
- `matrix` - open matrix operations
- `prgm` - open equation solver

## requirements

- java 8 or higher
- swing (included in jdk)

## notes

- the calculator uses recursive descent parsing for expression evaluation
- graphing uses numerical methods for calculus operations
- matrix operations support up to 10x10 matrices
- the ui mimics the dark body and green lcd of a real ti-84

---

mini project | 7th sem | java swing
