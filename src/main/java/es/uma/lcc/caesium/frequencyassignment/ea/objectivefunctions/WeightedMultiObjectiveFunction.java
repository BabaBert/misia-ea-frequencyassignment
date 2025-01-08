package es.uma.lcc.caesium.frequencyassignment.ea.objectivefunctions;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import es.uma.lcc.caesium.ea.base.Genotype;
import es.uma.lcc.caesium.ea.base.Individual;
import es.uma.lcc.caesium.ea.fitness.DiscreteObjectiveFunction;
import es.uma.lcc.caesium.ea.fitness.OptimizationSense;
import es.uma.lcc.caesium.frequencyassignment.FrequencyAssignmentProblem;
import es.uma.lcc.caesium.frequencyassignment.ea.fitness.FAPObjectiveFunction;

public class WeightedMultiObjectiveFunction extends DiscreteObjectiveFunction implements FAPObjectiveFunction {
    private final FrequencyAssignmentProblem problem;
    private final double weightSpan;
    private final double weightUniqueFrequencies;

    public WeightedMultiObjectiveFunction(FrequencyAssignmentProblem problem, double weightSpan, double weightUniqueFrequencies) {
        super(problem.numEmitters(), problem.maxFrequency());
        this.problem = problem;
        this.weightSpan = weightSpan;
        this.weightUniqueFrequencies = weightUniqueFrequencies;
    }

    @Override
    public Map<String, Set<Integer>> genotype2map(Genotype g) {
        Map<String, Set<Integer>> assignment = new HashMap<>();
        int index = 0;

        for (String emitter : problem.getEmitterNames()) {
            int demand = problem.getEmitter(emitter).demand();
            Set<Integer> frequencies = new HashSet<>();

            for (int d = 0; d < demand; d++) {
                frequencies.add((int) g.getGene(index++)); // Ensure the gene is an integer
            }
            assignment.put(emitter, frequencies);
        }
        return assignment;
    }

    @Override
    protected double _evaluate(Individual i) {
        Genotype g = i.getGenome(); // Updated to match the correct method
        Map<String, Set<Integer>> assignment = genotype2map(g);

        if (!problem.isFeasible(assignment)) {
            return Double.MAX_VALUE; // Assign a high cost to infeasible solutions
        }

        int span = FrequencyAssignmentProblem.frequencySpan(assignment);
        int uniqueFrequencies = FrequencyAssignmentProblem.numberOfFrequencies(assignment);

        return (weightSpan * span) + (weightUniqueFrequencies * uniqueFrequencies);
    }

    @Override
    public FrequencyAssignmentProblem getProblemData() {
        return this.problem;
    }

    @Override
    public OptimizationSense getOptimizationSense() {
        return OptimizationSense.MINIMIZATION;
    }
}