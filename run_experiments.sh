#!/bin/bash

# Create result directory if it doesn't exist
mkdir -p results

# Define objective functions
objective_functions=("constraint-repair" "constraint-penalizing" "weighted-multi-objective")

# Run each objective function 30 times
for obj_func in "${objective_functions[@]}"; do
    # Create subdirectory for the objective function
    mkdir -p results/$obj_func
    
    for i in {1..30}; do
        # Run the Java program and measure execution time
        start_time=$(date +%s)
        java -cp /path/to/your/classes es.uma.lcc.caesium.frequencyassignment.ea.RunEA4FAP config problem-data $obj_func > results/$obj_func/run_$i.txt
        end_time=$(date +%s)
        
        # Calculate and save execution time
        execution_time=$((end_time - start_time))
        echo "Execution time: $execution_time seconds" >> results/$obj_func/run_$i.txt
    done
done