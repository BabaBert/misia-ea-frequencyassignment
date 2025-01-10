mvn clean install

for config in configs/*.json; 
do 
    configpath="${config%.*}"
    for file in instances/*.fap; 
    do 
        filepath="${file%.*}"
        mvn exec:java -Dexec.mainClass="es.uma.lcc.caesium.frequencyassignment.ea.RunEA4FAP" -Dexec.args="$configpath $filepath"
    done
done