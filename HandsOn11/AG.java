import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class AG{
    // Definición de constantes para el algoritmo genético
    private static final int DIMENSION = 2;
    private static final int POPULATION_SIZE = 50;
    private static final int NUM_GENERATIONS = 100;
    private static final double ELITISM_RATE = 0.1;
    private static final double MUTATION_RATE = 0.05;
    private static final double[] dataset = {108, 115, 106, 97, 95, 91, 97, 83, 83, 78, 54, 67, 56, 53, 61, 115, 81, 78, 30, 45, 99, 32, 25, 28, 90, 89};
    private static final double[] intercept = {95, 96, 95, 97, 93, 94, 95, 93, 92, 86, 83, 80, 65, 69, 77, 96, 87, 89, 60, 63, 95, 61, 55, 56, 94, 93};

    // Inicialización del generador de números aleatorios
    private static Random random = new Random();

    public static void main(String[] args) {
        // Inicialización de la población
        List<RealChromosome> population = initializePopulation();

        // Bucle principal del algoritmo genético
        for (int i = 0; i < NUM_GENERATIONS; i++) {
            List<RealChromosome> newPopulation = new ArrayList<>();

            // Selección de los mejores individuos para la próxima generación
            int eliteCount = (int) (POPULATION_SIZE * ELITISM_RATE);
            newPopulation.addAll(getElite(population, eliteCount));

            // Creación de la nueva población mediante cruce y mutación
            while (newPopulation.size() < POPULATION_SIZE) {
                RealChromosome parent1 = selectParent(population);
                RealChromosome parent2 = selectParent(population);

                RealChromosome offspring = crossover(parent1, parent2);
                newPopulation.add(offspring);
            }

            // Mutación de los individuos no elitistas
            for (int j = eliteCount; j < newPopulation.size(); j++) {
                mutate(newPopulation.get(j));
            }

            // Reemplazo de la población antigua por la nueva
            population = newPopulation;
        }

        // Obtención y visualización de la mejor solución encontrada
        RealChromosome bestSolution = getBestSolution(population);
        double bestMSE = bestSolution.calculateMSE(bestSolution.getGenes(), dataset, intercept);
        System.out.println("Best solution: " + bestSolution);
        System.out.println("Best solution genes: " + java.util.Arrays.toString(bestSolution.getGenes()));
        System.out.println("Best solution fitness: " + bestSolution.getFitness());
        System.out.println("Best solution MSE: " + bestMSE);
    }

    // Método para inicializar la población con individuos aleatorios
    private static List<RealChromosome> initializePopulation() {
        List<RealChromosome> initialPopulation = new ArrayList<>();
        for (int i = 0; i < POPULATION_SIZE; i++) {
            double[] genes = getRandomVector(DIMENSION);
            RealChromosome chromosome = new RealChromosome(genes);
            initialPopulation.add(chromosome);
        }
        return initialPopulation;
    }

    // Método para generar un vector de números aleatorios
    private static double[] getRandomVector(int dimension) {
        double[] vector = new double[dimension];
        for (int i = 0; i < dimension; i++) {
            vector[i] = random.nextDouble();
        }
        return vector;
    }

    // Método para seleccionar los mejores individuos de la población
    private static List<RealChromosome> getElite(List<RealChromosome> population, int eliteCount) {
        population.sort(Comparator.comparingDouble(RealChromosome::getFitness).reversed());
        return new ArrayList<>(population.subList(0, eliteCount));
    }

    // Método para seleccionar un padre para el cruce
    private static RealChromosome selectParent(List<RealChromosome> population) {
        return population.get(random.nextInt(population.size()));
    }

    // Método para realizar el cruce entre dos padres
    private static RealChromosome crossover(RealChromosome parent1, RealChromosome parent2) {
        double[] genesParent1 = parent1.getGenes();
        double[] genesParent2 = parent2.getGenes();

        int crossoverPoint = random.nextInt(genesParent1.length);

        double[] newGenes = new double[genesParent1.length];
        for (int i = 0; i < genesParent1.length; i++) {
            newGenes[i] = (i < crossoverPoint) ? genesParent1[i] : genesParent2[i];
        }

        return new RealChromosome(newGenes);
    }

    // Método para mutar un individuo
    private static void mutate(RealChromosome chromosome) {
        for (int i = 0; i < chromosome.getGenes().length; i++) {
            if (random.nextDouble() < MUTATION_RATE) {
                chromosome.getGenes()[i] = random.nextDouble();
            }
        }
    }

    // Método para obtener la mejor solución de la población
    private static RealChromosome getBestSolution(List<RealChromosome> population) {
        return population.stream()
                .max(Comparator.comparingDouble(RealChromosome::getFitness))
                .orElseThrow();
    }

    // Clase para representar a los individuos del algoritmo genético
    static class RealChromosome {
        private double[] genes;

        public RealChromosome(double[] genes) {
            this.genes = genes;
        }

        public double[] getGenes() {
            return genes;
        }

        // Método para calcular el error cuadrático medio
        public double calculateMSE(double[] parameters, double[] x, double[] y) {
            int n = x.length;
            double mse = 0.0;

            for (int i = 0; i < n; i++) {
                double prediction = 0.0;
                for (int j = 0; j < parameters.length; j++) {
                    prediction += parameters[j] * Math.pow(x[i], j);
                }

                mse += Math.pow(prediction - y[i], 2);
            }

            return mse / n;
        }

        // Método para calcular la aptitud del individuo
        public double getFitness() {
            double mse = calculateMSE(genes, dataset, intercept);
            return 1.0 / mse;
        }
    }
}
