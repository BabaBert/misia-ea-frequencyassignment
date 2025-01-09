package es.uma.lcc.caesium.frequencyassignment.ea;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import es.uma.lcc.caesium.ea.base.Genotype;
import es.uma.lcc.caesium.ea.base.Individual;
import es.uma.lcc.caesium.ea.fitness.OptimizationSense;
import es.uma.lcc.caesium.ea.fitness.PermutationalObjectiveFunction;
import es.uma.lcc.caesium.frequencyassignment.FrequencyAssignmentProblem;
import es.uma.lcc.caesium.frequencyassignment.ea.fitness.FAPObjectiveFunction;

public class ObjectiveFunction extends PermutationalObjectiveFunction implements FAPObjectiveFunction {

    private final FrequencyAssignmentProblem problem;

    public ObjectiveFunction(FrequencyAssignmentProblem problem) {
        super(problem.totalDemand());
        this.problem = problem;
    }

    @Override
    public FrequencyAssignmentProblem getProblemData() {
        return this.problem;
    }

    @Override
    public Map<String, Set<Integer>> genotype2map(Genotype g) {
        Map<String, Set<Integer>> frequencyMap = new HashMap<>();
        Set<String> emitterIDs = problem.getEmitterNames();
        int geneIndex = 0;

        for (String emitterID : emitterIDs) {
            int demand = problem.getEmitter(emitterID).demand();
            Set<Integer> frequencies = new HashSet<>();
            for (int i = 0; i < demand; i++) {
                int frequency = (int) g.getGene(geneIndex);
                frequencies.add(frequency);
                geneIndex++; 
            }
            frequencyMap.put(emitterID, frequencies);
        }
        return frequencyMap;

    }

    @Override
    public OptimizationSense getOptimizationSense() {
        return OptimizationSense.MINIMIZATION;
    }

    @Override
    protected double _evaluate(Individual individual) {
        Genotype genome = individual.getGenome();
        Map<String, Set<Integer>> frequencyMap = genotype2map(genome);
        double fitness = calculateObjective(frequencyMap);
        individual.setFitness(fitness);
        return fitness;
    }

    private double calculateObjective(Map<String, Set<Integer>> frequencyMap) {
        double penalty = 0.0;

        for (String emitter1 : problem.getEmitterNames()) {
            Set<Integer> frequencies1 = frequencyMap.get(emitter1);
            penalty += calculateMaxFrequencyPenalty(frequencies1);
            for (String emitter2 : problem.getEmitterNames()) {
                if (emitter1.equals(emitter2)) {
                    continue;
                }
                double distance = problem.getDistance(emitter1, emitter2);
                Set<Integer> frequencies2 = frequencyMap.get(emitter2);
                for (int f1 : frequencies1) {
                    for (int f2 : frequencies2) {
                        if (!problem.checkSeparation(f1, f2, distance)) {
                            penalty += 1.0; 
                        }
                    }
                }
            }
        }
        // lower is better
        return penalty;
    }

    private double calculateMaxFrequencyPenalty(Set<Integer> frequencies) {
        double penalty = 0.0;

        for (int frequency : frequencies) {
            if (frequency > problem.maxFrequency()) {
                penalty += 1.0; 
            }
        }

        return penalty;
    }
}
