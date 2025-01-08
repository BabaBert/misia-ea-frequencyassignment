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


public class ConstraintPenalizingObjectiveFunction extends DiscreteObjectiveFunction implements FAPObjectiveFunction {
    private final FrequencyAssignmentProblem problem;

    public ConstraintPenalizingObjectiveFunction(FrequencyAssignmentProblem problem) {
        super(problem.numEmitters(), problem.maxFrequency());
        this.problem = problem;
    }

    @Override
    public Map<String, Set<Integer>> genotype2map(Genotype g) {
        Map<String, Set<Integer>> assignment = new HashMap<>();
        int index = 0;

        for (String emitter : problem.getEmitterNames()) {
            int demand = problem.getEmitter(emitter).demand();
            Set<Integer> frequencies = new HashSet<>();

            for (int d = 0; d < demand; d++) {
                frequencies.add((int) g.getGene(index++));
            }
            assignment.put(emitter, frequencies);
        }
        return assignment;
    }

    @Override
    protected double _evaluate(Individual i) {
        Genotype g = i.getGenome();
        Map<String, Set<Integer>> assignment = genotype2map(g);

        if (!problem.isFeasible(assignment)) {
            return Double.MAX_VALUE; // Infeasible solutions have a high cost
        }

        return FrequencyAssignmentProblem.frequencySpan(assignment);
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
