mvn clean install

for file in instances/*.fap; 
do 
    filepath="${file%.*}"
    mvn exec:java -Dexec.mainClass="es.uma.lcc.caesium.frequencyassignment.ea.RunEA4FAP" -Dexec.args="config $filepath"
done