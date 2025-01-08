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
        // Ensure the genotype size matches the total demand for frequencies
        super(problem.totalDemand()); 
        this.problem = problem;

        // Print total demand (number of genes required for the problem)
        System.out.println("Total demand (genes needed): " + problem.totalDemand());

        // Adjust the possible frequency range for each gene if necessary
        for (int i = 0; i < problem.numEmitters(); i++) {
            // For each emitter, we can set an alphabet size for the frequency (if needed)
            // Set a default value range here; adjust as per the problem's frequency bounds
            setAlphabetSize(i, problem.maxFrequency()); // Example, adjust as necessary
        }
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
            return problem.maxFrequency(); // Penalize infeasible solutions
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