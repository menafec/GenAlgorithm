//hands on 10 
// ximena carolina fernandez cardenas

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Maxones {
    // Definición de constantes para el tamaño de la población, longitud del cromosoma, tasa de mutación y número máximo de generaciones
    private static final int POPULATION_SIZE = 100;
    private static final int CHROMOSOME_LENGTH = 10;
    private static final double MUTATION_RATE = 0.1;
    private static final int MAX_GENERATIONS = 1000;

    public static void main(String[] args) {
        // Inicialización de la población
        List<String> population = initializePopulation(POPULATION_SIZE, CHROMOSOME_LENGTH);
        int generation = 1;

        // Bucle principal del algoritmo genético
        while (generation <= MAX_GENERATIONS && !population.contains("1111111111")) {
            // Paso 3: Calcular los valores de aptitud para cada cromosoma
            List<Integer> fitnessValues = calculateFitnessValues(population);

            // Paso 4: Selección de los padres basada en los valores de aptitud
            List<String> selectedParents = selectParents(population, fitnessValues);

            List<String> newPopulation = new ArrayList<>();

            // Paso 5: Cruce y mutación para generar la nueva población
            for (int i = 0; i < POPULATION_SIZE; i++) {
                // Selección aleatoria de los padres
                String parent1 = selectedParents.get(selectRandomIndex(selectedParents.size()));
                String parent2 = selectedParents.get(selectRandomIndex(selectedParents.size()));

                // Cruce de los padres para generar el descendiente
                String offspring = crossover(parent1, parent2);
                // Mutación del descendiente
                offspring = mutate(offspring);

                // Añadir el descendiente a la nueva población
                newPopulation.add(offspring);
            }

            // Reemplazo de la población antigua por la nueva
            population = newPopulation;
            // Imprimir la mejor aptitud de la generación actual
            System.out.println("Generation " + generation + ": Best fitness - " + calculateMaxFitness(population));
            // Incrementar el número de generación
            generation++;
        }

        int maxFitness = calculateMaxFitness(population);
        int index = population.indexOf("1111111111");

        if (index != -1) {
            System.out.println("Solution found in generation " + (generation - 1) + " - " + population.get(index));
        } else {
            System.out.println("Solution not found. Best fitness achieved: " + maxFitness);
        }
    }

    private static List<String> initializePopulation(int size, int length) {
        List<String> population = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < size; i++) {
            StringBuilder chromosome = new StringBuilder();
            for (int j = 0; j < length; j++) {
                chromosome.append(random.nextInt(2));
            }
            population.add(chromosome.toString());
        }

        return population;
    }

    private static List<Integer> calculateFitnessValues(List<String> population) {
        List<Integer> fitnessValues = new ArrayList<>();
        for (String chromosome : population) {
            int fitness = calculateFitness(chromosome);
            fitnessValues.add(fitness);
        }
        return fitnessValues;
    }

    private static int calculateFitness(String chromosome) {
        int countOnes = 0;
        for (char c : chromosome.toCharArray()) {
            if (c == '1') {
                countOnes++;
            }
        }
        return countOnes;
    }

    private static int calculateMaxFitness(List<String> population) {
        int maxFitness = 0;
        for (String chromosome : population) {
            int fitness = calculateFitness(chromosome);
            maxFitness = Math.max(maxFitness, fitness);
        }
        return maxFitness;
    }

    private static List<String> selectParents(List<String> population, List<Integer> fitnessValues) {
        List<String> selectedParents = new ArrayList<>();
        Random random = new Random();
        int totalFitness = fitnessValues.stream().mapToInt(Integer::intValue).sum();

        for (int i = 0; i < POPULATION_SIZE; i++) {
            int rand = random.nextInt(totalFitness);
            int runningSum = 0;

            for (int j = 0; j < population.size(); j++) {
                runningSum += fitnessValues.get(j);
                if (runningSum >= rand) {
                    selectedParents.add(population.get(j));
                    break;
                }
            }
        }
        return selectedParents;
    }

    private static int selectRandomIndex(int size) {
        Random random = new Random();
        return random.nextInt(size);
    }

    private static String crossover(String parent1, String parent2) {
        Random random = new Random();
        int crossoverPoint = random.nextInt(CHROMOSOME_LENGTH);

        return parent1.substring(0, crossoverPoint) + parent2.substring(crossoverPoint);
    }

    private static String mutate(String chromosome) {
        StringBuilder mutatedChromosome = new StringBuilder(chromosome);
        Random random = new Random();

        for (int i = 0; i < chromosome.length(); i++) {
            if (random.nextDouble() < MUTATION_RATE) {
                char gene = chromosome.charAt(i);
                if (gene == '0') {
                    mutatedChromosome.setCharAt(i, '1');
                } else {
                    mutatedChromosome.setCharAt(i, '0');
                }
            }
        }

        return mutatedChromosome.toString();
    }
}
