import java.util.Arrays;
import java.util.Random;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.File;
import java.util.Random;

// BEFORE RUNNING THE CODE, PLEASE MAKE SURE "dataset.CSV" IS IN THE SAME DIRECTORY AS THIS PROGRAM
public class ABC_Regression {
    static int rowTotal = 414, colTotal = 7, row = 0, 
    testTotal = rowTotal / 3, trainTotal = rowTotal - testTotal,
    testN = 0, trainN = 0;
    static double[][] trainSet = new double[trainTotal][colTotal]; 
    static double[][] testSet = new double[testTotal][colTotal];

    public static void main(String[] args) {
        dataSet();
        ABC();
    }

    public static void ABC() {
        // ## INITIALISATION
        // Related to ABC itself
        int maxIterations = 1000; // Maximum times the program will loop
        int maxSims = 3; // Amount of simulations the program will run.
        int varNo = 7; // Number of Variables
        int popSize = 50; // Population Size
        double lowerBound = -5, upperBound = 5; // Variable boundaries
        int limit = (popSize * varNo); // Limit for trial counters

        // Related to food sources
        double[][] foodSource = new double[popSize][varNo]; // Food Sources
        double[] fitness = new double[popSize]; // Storage of each fitness value
        int[] trialCounter = new int[popSize]; // Trial counter for each food source
        double[] probArray = new double[popSize]; // Storage of probability chances
        
        // Related to nearby/best sources
        double[] newSource = new double[varNo], bestSource = new double[varNo]; // New source and Current best food source
        double newSourceFit, bestSourceFit, trainBest=0, testBest=0, trainAvg=0, testAvg=0, 
               trainBestTotal=0, testBestTotal=0, maxFit, avgFit, r, bestMSE=0, avgMSE=0;
        int iterations, simulations, Xp, dimensions, m, n, maxTrial, k;

        Random rand = new Random();
        Object[][] table = new Object[maxSims][4];
 
        for (simulations = 0; simulations < maxSims; simulations++) { 
            System.out.println("Simulation " + (simulations + 1) + "...");
            // While i is lower than the population size and j is lower than the number of variables, generate food sources.
            for (int i = 0; i < popSize; i++) {
                for (int j = 0; j < varNo; j++) {
                    foodSource[i][j] = rand.nextDouble() * (upperBound - lowerBound) + lowerBound;
                }
                trialCounter[i] = 0;
                fitness[i] = CalcFit(foodSource[i], trainSet, trainTotal);
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
                    newSourceFit = CalcFit(newSource, trainSet, trainTotal);
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
                        newSourceFit = CalcFit(newSource, trainSet, trainTotal);
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

                bestMSE = 1 / bestSourceFit - 1;

                avgFit = fitness[0];
                for (int i = 1; i < popSize; i++) {
                    avgFit += fitness[i];
                }
                avgFit = avgFit / popSize;

                avgMSE = 1 / avgFit - 1;


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
                    fitness[k] = CalcFit(foodSource[k], trainSet, trainTotal);
                    trialCounter[k] = 0;
                }
                
                //System.out.println("Iteration Number: " + (iterations + 1) + " | Best Fitness: " + bestSourceFit + " | Average Fitness: " + avgFit);
                System.out.println("Iteration Number: " + (iterations + 1) + " | Best MSE: " + bestMSE + " | Average MSE: " + avgMSE);
                                    // + " | Best Food Sources: [0]: " + bestSource[0] + " | [1]: " + bestSource[1]
                                    // + " | [2]: " + bestSource[2] + " | [3]: " + bestSource[3] + " | [4]: " + bestSource[4]
                                    //  + " | [5]: " + bestSource[5] + " | [6]: " + bestSource[6]);
            }
            trainBest = (1 / CalcFit(bestSource, trainSet, trainTotal)) - 1;
            testBest = (1 / CalcFit(bestSource, testSet, testTotal)) - 1;

            table[simulations][0] = simulations + 1;
            table[simulations][1] = String.format("%.4f, %.4f, %.4f, %.4f, %.4f, %.4f, %.4f", bestSource[0], bestSource[1], bestSource[2], 
                                                    bestSource[3], bestSource[4], bestSource[5], bestSource[6]);
            table[simulations][2] = String.format("%.4f", trainBest);
            table[simulations][3] = String.format("%.4f", testBest);

            trainBestTotal += trainBest;
            testBestTotal += testBest;
            System.out.println("Simulation " + (simulations + 1) + " Done! Performed " + iterations + " iterations. ");
        }
        trainAvg = trainBestTotal / simulations;
        testAvg = testBestTotal / simulations;

        System.out.println("Results:");
        System.out.format("%-20s%-65s%-30s%-30s%n", "Simulations", "Best Source Found", "Training MSE", "Test MSE");        
        for (final Object[] tableRow : table) {
            System.out.format("%-20s%-65s%-30s%-30s%n", tableRow);
        }
        System.out.format("%-20s%65s%-30s%-30s%n", "", "Averages:        ", String.format("%.4f", trainAvg), String.format("%.4f", testAvg));
    }

    public static void dataSet() {
        File rawData = new File("dataSet.csv");
        String line = "";
        double[][] dataSet = new double[rowTotal][colTotal], normDataSet = new double[rowTotal][colTotal];
        double[] min = new double[colTotal], max = new double[colTotal];

        try {
            BufferedReader reader = new BufferedReader(new FileReader(rawData));
            while((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                for (int col = 0; col < colTotal; col++) {
                    dataSet[row][col] = Double.parseDouble(values[col].trim());
                }
                row++;
            }
            reader.close();
        }
        
        // FILE NOT FOUND ERROR
        catch (FileNotFoundException e) {
            e.printStackTrace();
            System.err.println("FILE NOT FOUND, CHECK PATH");
        }
        // READ LINE ERROR
        catch(IOException e) {
            e.printStackTrace();
            System.err.println("LINE READ ERROR");
        }

        for (int j = 0; j < colTotal; j++) {
            min[j] = dataSet[0][j];
            max[j] = dataSet[0][j];
            for (int i = 1; i < rowTotal; i++) {
                if (dataSet[i][j] < min[j]) {
                    min[j] = dataSet[i][j];
                }
                if (dataSet[i][j] > max[j]) {
                    max[j] = dataSet[i][j];
                }
            }
            for (int i = 0; i < rowTotal; i++) {
                normDataSet[i][j] = (dataSet[i][j] - min[j]) / (max[j] - min[j]);
            }
        }

        Random rand = new Random();
        for (int i = 0; i < rowTotal; i++) {
            int r = rand.nextInt(3);
            if ((r <= 1 && trainN < trainTotal) || (r > 1 && testN == testTotal)) {
                for (int j = 0; j < colTotal; j++) {
                    trainSet[(trainN)][j] = normDataSet[i][j];
                }
                trainN += 1;
            }
            else if ((r <= 1 && trainN == trainTotal) || (r > 1 && testN < testTotal)) {
                for (int j = 0; j < colTotal; j++) {
                    testSet[(testN)][j] = normDataSet[i][j];
                }
                testN += 1;
            }
        }
    }

    public static double CalcFit(double foodSource[], double providedSet[][], int rowNo) {
        // Function: y(x1,x2,x3,x4,x5,x6) = w0 + w1x1 + w2x2 + w3x3 + w4x4 + w5x5 + w6x6
        double mse, difference, squaredError = 0, foodSource_fitness;
        for (int i = 0; i < rowNo; i++) {
            double[] predictedSet = new double[colTotal];
            for (int j = 0; j < colTotal; j++) {
                predictedSet[j] = providedSet[i][j];
            }
            double actualValue = providedSet[i][6];
            double predictedValue = foodSource[0];
            for (int j = 1; j <= 6; j++) {
                predictedValue += foodSource[j] * predictedSet[j - 1];
            }
            difference = actualValue - predictedValue;
            squaredError += difference * difference;
        }
        mse = squaredError / rowNo;
        foodSource_fitness = 1.0 / (1.0 + mse);
        return foodSource_fitness;
    }
}
