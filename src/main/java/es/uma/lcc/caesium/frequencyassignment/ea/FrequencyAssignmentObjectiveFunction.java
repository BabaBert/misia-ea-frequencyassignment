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

public class FrequencyAssignmentObjectiveFunction extends PermutationalObjectiveFunction implements FAPObjectiveFunction {
    private final FrequencyAssignmentProblem problem;

    public FrequencyAssignmentObjectiveFunction(FrequencyAssignmentProblem problem) {
        super(problem.totalDemand()); // Ensure the genotype size matches the total demand
        this.problem = problem;

        System.out.println("Total demand (genes needed): " + problem.totalDemand());
    }

    @Override
    public Map<String, Set<Integer>> genotype2map(Genotype g) {
        Map<String, Set<Integer>> assignment = new HashMap<>();
        int index = 0;

        System.out.println("Genotype size: " + g.length());

        for (String emitter : problem.getEmitterNames()) {
            int demand = problem.getEmitter(emitter).demand(); // Number of frequencies required
            Set<Integer> frequencies = new HashSet<>();

            for (int d = 0; d < demand; d++) {
                if (index < g.length()) { // Ensure index is within bounds
                    frequencies.add((int) g.getGene(index++));
                } else {
                    throw new IllegalArgumentException("Insufficient genes in genotype for emitter " + emitter);
                }
            }
            assignment.put(emitter, frequencies);
        }
        return assignment;
    }

    @Override
    protected double _evaluate(Individual i) {
        Genotype g = i.getGenome();
        System.out.println("Evaluating genotype: " + g);

        Map<String, Set<Integer>> assignment = genotype2map(g);

        if (!problem.isFeasible(assignment)) {
            return 10 * problem.getMaxFrequencySpan();
        }

        double span = FrequencyAssignmentProblem.frequencySpan(assignment);
        System.out.println("Frequency span: " + span);
        return span;
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