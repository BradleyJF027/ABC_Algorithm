import java.util.Arrays;
import java.util.Random;

public class ABC_FunctionOptimisation {
    static int example = 3; 
    static String functionName = ""; // testing purposes.

    public static void main(String[] args) {
        // ## INITIALISATION
        // Related to ABC itself
        int maxIterations = 100; // Maximum times the program will loop
        int maxSims = 3; // Amount of simulations the program will run.
        int varNo = 2; // Number of Variables
        int popSize = 50; // Population Size
        double lowerBound = -10, upperBound = 10; // Variable boundaries
        int limit = (popSize * varNo); // Limit for trial counters

        // Related to food sources
        double[][] foodSource = new double[popSize][varNo]; // Food Sources
        double[] fitness = new double[popSize]; // Storage of each fitness value
        int[] trialCounter = new int[popSize]; // Trial counter for each food source
        double[] probArray = new double[popSize]; // Storage of probability chances
        
        // Related to nearby/best sources
        double[] newSource = new double[varNo], bestSource = new double[varNo]; // New source and Current best food source
        double newSourceFit, bestSourceFit, computedfXY = 0, totalComputed = 0, avgComputed;
        double maxFit, r;
        int iterations, simulations, Xp, dimensions, m, n, maxTrial, k;

        Random rand = new Random();
        Object[][] table = new Object[maxSims][3];
 
        for (simulations = 0; simulations < maxSims; simulations++) { 
            System.out.println("Simulation " + (simulations + 1) + "...");
            // While i is lower than the population size and j is lower than the number of variables, generate food sources.
            for (int i = 0; i < popSize; i++) {
                for (int j = 0; j < varNo; j++) {
                    foodSource[i][j] = rand.nextDouble() * (upperBound - lowerBound) + lowerBound;
                }
                trialCounter[i] = 0;
                fitness[i] = CalcFit(foodSource[i]);
            }

            maxFit = fitness[0];
            k = 0;
            for (int i = 1; i < popSize; i++) {
                if (fitness[i] > maxFit) {
                    maxFit = fitness[i];
                    k = i;
                }
            }

            for (int j = 0; j < varNo; j++) {
                bestSource[j] = foodSource[k][j];
            }
            bestSourceFit = fitness[k];

            // Main Program, will keep iterating until it reaches the estabalished limit
            for (iterations = 0; iterations < maxIterations; iterations++) {
                // ## EMPLYOEE BEE
                for (int i = 0; i < popSize; i++) {
                    for (int j = 0; j < varNo; j++) {
                        newSource[j] = foodSource[i][j];
                    }
                    Xp = rand.nextInt(popSize);
                    while (Xp == i) {
                        Xp = rand.nextInt(popSize);
                    }

                    dimensions = rand.nextInt(varNo);

                    newSource[dimensions] += (2*rand.nextDouble() - 1) * (foodSource[i][dimensions] - foodSource[Xp][dimensions]);

                    // Check that the new solution is within bounds, and if its beyound, set it to the bounds.
                    if (newSource[dimensions] < lowerBound) newSource[dimensions] = lowerBound;
                    if (newSource[dimensions] > upperBound) newSource[dimensions] = upperBound;

                    // Calculate Fitness and replace if new is better.
                    newSourceFit = CalcFit(newSource);
                    if (newSourceFit > fitness[i]) {
                        foodSource[i][dimensions] = newSource[dimensions];
                        fitness[i] = newSourceFit;
                        trialCounter[i] = 0;
                    } else {
                        trialCounter[i] += 1;
                    }
                }

                // ## ONLOOKER BEE
                maxFit = fitness[0];
                for (int i = 1; i < popSize; i++) {
                    if (fitness[i] > maxFit) {
                        maxFit = fitness[i];
                    }
                }

                for (int i = 0; i < popSize; i++) {
                    probArray[i] = 0.9 * (fitness[i] / maxFit) + 0.1;
                }

                m = 0;
                n = 0;
                while (m < popSize) {
                    r = rand.nextDouble();
                    if (r < probArray[n]) {
                        for (int j = 0; j < varNo; j++) {
                            newSource[j] = foodSource[n][j];
                        }
                        Xp = rand.nextInt(popSize);
                        while (Xp == n) {
                            Xp = rand.nextInt(popSize);
                        }
                        dimensions = rand.nextInt(varNo);
                        newSource[dimensions] += (2*rand.nextDouble() - 1) * (foodSource[n][dimensions] - foodSource[Xp][dimensions]);
                        if (newSource[dimensions] < lowerBound) newSource[dimensions] = lowerBound;
                        if (newSource[dimensions] > upperBound) newSource[dimensions] = upperBound;
                        newSourceFit = CalcFit(newSource);
                        if (newSourceFit > fitness[n]) {
                            foodSource[n][dimensions] = newSource[dimensions];
                            fitness[n] = newSourceFit;
                            trialCounter[n] = 0;
                        }
                        else {
                            trialCounter[n] += 1;
                        }
                        m = m + 1;
                    }
                    n = n + 1;
                    if (n == popSize) {
                        n = 0;
                    }
                }

                maxFit = fitness[0];
                k = 0;
                for (int i = 1; i < popSize; i++) {
                    if (fitness[i] > maxFit) {
                        maxFit = fitness[i];
                        k = i;
                    }
                }

                if (maxFit > bestSourceFit) {
                    for (int j = 0; j < varNo; j++) {
                        bestSource[j] = foodSource[k][j];
                    }
                    bestSourceFit = fitness[k];
                }


                // ## SCOUT BEE
                maxTrial =  trialCounter[0];
                k = 0;
                for (int i = 1; i < popSize; i++) {
                    if (trialCounter[i] > maxTrial) {
                        maxTrial = trialCounter[i];
                        k = i;
                    }
                }
                if (maxTrial > limit) {
                    for (int j = 0; j < varNo; j++) {
                        foodSource[k][j] = rand.nextDouble() * (upperBound - lowerBound) + lowerBound;
                    }
                    fitness[k] = CalcFit(foodSource[k]);
                    trialCounter[k] = 0;
                }

                computedfXY = (1 / bestSourceFit) - 1;
                table[simulations][0] = simulations + 1;
                table[simulations][1] = String.format("(%.4f, %.4f)", bestSource[0], bestSource[1]);
                table[simulations][2] = String.format("%.4f", computedfXY);

                // Old Print example, for seeing every single iteration.
                // System.out.println("Function: " + example + " |  Iteration Number: " + (iterations + 1) + " | Best Fitness: " + bestSourceFit + 
                //                     " | Best Food Source [0]: " + bestSource[0] + " | Best Food Source [1]: " + bestSource[1]);
            }
            totalComputed += computedfXY;
            System.out.println("Simulation " + (simulations + 1) + " Done! Performed " + iterations + " iterations. ");
        }

        avgComputed = totalComputed / simulations;
        System.out.println("Results of " + functionName + ":");
        System.out.format("%-20s%-35s%-35s%n", "Simulations", "Best Solution (x,y) found", "Computed f(x,y) of best solution");        
        for (final Object[] row : table) {
            System.out.format("%-20s%-35s%-35s%n", row);
        }
        System.out.format("%-20s%-35s%-35s%n", "", "Average Performance", String.format("%.4f", avgComputed));
    }

    public static double CalcFit(double foodSource[]) {
        double a, b, c, d, e, fxy, foodSource_fitness;
        switch (example) {
            case 1:
                // f(x,y) = (1.5 - x + xy)^2 + (2.25 - x + xy^2)^2 + (2.625 - x + xy^3)^2 (Beale Function)
                functionName = ("Beale Function");
                a = (1.5 - foodSource[0] + (foodSource[0] * foodSource[1]));
                b = (2.25 - foodSource[0] + (foodSource[0] * foodSource[1] * foodSource[1]));
                c = (2.625 - foodSource[0] + (foodSource[0] * foodSource[1] * foodSource[1] * foodSource[1]));
                fxy = a * a + b * b + c * c;
                foodSource_fitness = 1.0 / (1.0 + fxy);
                return foodSource_fitness;
            case 2:
                // f(x,y) = (x + 2y - 7)^2 + (2x + y - 5)^2 (Booth Function)
                functionName = ("Booth Function");
                a = (foodSource[0] + (foodSource[1] * 2)- 7);
                b = ((foodSource[0] * 2) + foodSource[1] - 5);
                fxy = a * a + b * b;
                foodSource_fitness = 1.0 / (1.0 + fxy);
                return foodSource_fitness;
            case 3:
                // f(x, y) = 0.26(x^2 + y^2) - 0.48xy (Matyas Function)
                functionName = ("Matyas Function");
                a = 0.26 * ((foodSource[0] * foodSource[0]) + (foodSource[1] * foodSource[1]));
                b = 0.48 * foodSource[0] * foodSource[1];
                fxy = a - b;
                foodSource_fitness = 1.0 / (1.0 + fxy);
                return foodSource_fitness;
            case 4: 
                // f(x, y) = 2x^2 - 1.05x^4 + x^6 / 6 + xy + y^2 (Three Hump Camel Function)
                functionName = ("Three Hump Camel Function");
                a = 2 * (foodSource[0] * foodSource[0]);
                b = 1.05 * (foodSource[0] * foodSource[0] * foodSource[0] * foodSource[0]);
                c = (foodSource[0] * foodSource[0] * foodSource[0] * foodSource[0] * foodSource[0] * foodSource[0]) / 6;
                d = foodSource[0] * foodSource[1];
                e = foodSource[1] * foodSource[1];
                fxy = a - b + c + d + e;
                foodSource_fitness = 1.0 / (1.0 + fxy);
                return foodSource_fitness;
            default:
                // f(x,y) = (1.5 - x + xy)^2 + (2.25 - x + xy^2)^2 + (2.625 - x + xy^3)^2 (Beale Function)
                functionName = ("Beale Function");
                a = (1.5 - foodSource[0] + (foodSource[0] * foodSource[1]));    
                b = (2.25 - foodSource[0] + (foodSource[0] * foodSource[1] * foodSource[1]));
                c = (2.625 - foodSource[0] + (foodSource[0] * foodSource[1] * foodSource[1] * foodSource[1]));
                fxy = a * a + b * b + c * c;
                foodSource_fitness = 1.0 / (1.0 + fxy);
                return foodSource_fitness;
        }
    }
}
