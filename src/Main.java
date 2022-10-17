import com.sun.org.apache.xpath.internal.operations.Bool;
import org.jgap.*;
import org.jgap.impl.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;

/**
 * @author kbern
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    static String team1 = "";
    static String team2 = "";

    private static void Writerobocup(String team1, String team2, Boolean Restore) {
        // If restore is true, the file is restored to the original state (replace names of teams to "team1" and "team2")
        // If restore is false, the file is modified to the new state (replace names of "team1" and "team2" to team1 and team2)
        String path = "robocup.dsc";
        StringBuilder content = new StringBuilder();
        try {
            Scanner scanner = new Scanner(new File(path));
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (Restore) {
                    line = line.replace(team1, "team1");
                    line = line.replace(team2, "team2");
                } else {
                    line = line.replace("team1", team1);
                    line = line.replace("team2", team2);
                }
                content.append(line).append(System.getProperty("line.separator"));
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            java.io.FileWriter writer = new java.io.FileWriter(path);
            writer.write(content.toString());
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws InvalidConfigurationException {
        Configuration conf = new DefaultConfiguration();
        conf.setPreservFittestIndividual(true);
        conf.setKeepPopulationSizeConstant(false);

        // read Config.txt File
        File file = new File("Config.txt");
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split("=");
                if (parts[0].contains("Team1")) {
                    team1 = parts[1].trim();
                } else if (parts[0].contains("Team2")) {
                    team2 = parts[1].trim();
                }
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Writerobocup(team1, team2, true);
        Writerobocup(team1, team2, false);


        //Ejemplo de individuos:
        //Inicializa ejemplo de Genes con 15 elementos (15 alelos)
        //cada el alelo es un parametro del agente BasicTeamAG
        //cada agente tiene 3 parámetros (3x5 agentes = 15)
        //
        //InterGene permite definir los rangos máximos y mínimos que puede tomar un alelo
        //Para este ejemplo los valores pueden ser entre 1 y 9

        Gene[] sampleGenes = new Gene[15];
        for (int jj = 0; jj < 15; jj++) {
            sampleGenes[jj] = new IntegerGene(conf, 1, 9);
        }

        //Establece tamaño de la población (cantidad de genes)
        conf.setPopulationSize(10);

        //Establece la funcion de fitness como una Clase FuncionEvaluación 
        //dicha clase es la que se debe implementar con la función de fitness. 
        //Se puede parametrizar un objetivo o cualquier valor necesario
        //En este caso solo se inicializa el valor de MAXDIF
        conf.setFitnessFunction(new FuncionEvaluacion(50, team1, team2));

        //Se estable un cromosoma de muestra como parte de la configuración.
        IChromosome sampleChromosome = new Chromosome(conf, sampleGenes);
        conf.setSampleChromosome(sampleChromosome);

        //inicializa la población
        Genotype poblacion = Genotype.randomInitialGenotype(conf);

        System.out.println("Mejor individuo inicial");
        FuncionEvaluacion.println(poblacion.getFittestChromosome()); //muestra su conformación
        //iteramos una cantidad fija o bajo un criterio haciendo evoluacionar la población
        for (int i = 0; i < 100; i++) {
            //Hace evoluacionar a la poblacion 
            System.out.println("\nEvolucionando poblacion " + (i) + "...");
            poblacion.evolve();

            //obtiene el mejor cromosoma de la población actual
            IChromosome mejor = poblacion.getFittestChromosome();
            System.out.println("\nResultados Evolucion poblacion " + i);
            FuncionEvaluacion.println(mejor); //muestra su conformación
            System.out.println("\tFitness:" + mejor.getFitnessValue()); //muestra su evaluación
        }

        //Si finaliza evoluacion, obtiene el mejor y lo imprime
        IChromosome mejor = poblacion.getFittestChromosome();
        System.out.println("\n\nMEJOR INDIVIDUO:");
        FuncionEvaluacion.println(mejor); //muestra su conformación
        System.out.println("\tFitness:" + mejor.getFitnessValue()); //muestra su evaluación


    }
}
