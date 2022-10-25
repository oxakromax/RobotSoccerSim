import org.jgap.*
import org.jgap.impl.*
import java.io.File
import java.io.FileNotFoundException
import java.io.FileWriter
import java.util.*


/**
 * @author kbern
 */
object Main {
    /**
     * @param args the command line arguments
     */
    var team1 = ""
    var team2 = ""
    private fun Writerobocup(team1: String, team2: String, Restore: Boolean) {
        // If restore is true, the file is restored to the original state (replace names of teams to "team1" and "team2")
        // If restore is false, the file is modified to the new state (replace names of "team1" and "team2" to team1 and team2)
        val path = "robocup.dsc"
        val content = StringBuilder()
        try {
            val scanner = Scanner(File(path))
            while (scanner.hasNextLine()) {
                var line = scanner.nextLine()
                if (Restore) {
                    line = line.replace(team1, "team1")
                    line = line.replace(team2, "team2")
                } else {
                    line = line.replace("team1", team1)
                    line = line.replace("team2", team2)
                }
                content.append(line).append(System.getProperty("line.separator"))
            }
            scanner.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        try {
            val writer = FileWriter(path)
            writer.write(content.toString())
            writer.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Throws(InvalidConfigurationException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val conf: Configuration = DefaultConfiguration()
        conf.setPreservFittestIndividual(true)
        conf.isKeepPopulationSizeConstant = false
        conf.randomGenerator = GaussianRandomGenerator()
        val best = BestChromosomesSelector(conf, 0.4)
        best.doubletteChromosomesAllowed = false
        conf.addNaturalSelector(best, false)
        conf.breeder = GABreeder()
        initSettings()


        //Ejemplo de individuos:
        //Inicializa ejemplo de Genes con 15 elementos (15 alelos)
        //cada el alelo es un parametro del agente BasicTeamAG
        //cada agente tiene 3 parámetros (3x5 agentes = 15)
        //
        //InterGene permite definir los rangos máximos y mínimos que puede tomar un alelo
        //Para este ejemplo los valores pueden ser entre 1 y 9
        val sampleGenes = arrayOfNulls<Gene>(15)
        for (jj in 0..14) {
            sampleGenes[jj] = IntegerGene(conf, 1, 11)
        }

        //Establece tamaño de la población (cantidad de genes)
        conf.populationSize = 20

        //Establece la funcion de fitness como una Clase FuncionEvaluación 
        //dicha clase es la que se debe implementar con la función de fitness. 
        //Se puede parametrizar un objetivo o cualquier valor necesario
        //En este caso solo se inicializa el valor de MAXDIF
        conf.fitnessFunction = FuncionEvaluacion(50, team1, team2)

        //Se estable un cromosoma de muestra como parte de la configuración.
        val sampleChromosome: IChromosome = Chromosome(conf, sampleGenes)
        conf.sampleChromosome = sampleChromosome

        //inicializa la población
        val poblacion = Genotype.randomInitialGenotype(conf)
        println("Mejor individuo inicial")
        FuncionEvaluacion.println(poblacion.fittestChromosome) //muestra su conformación
        //iteramos una cantidad fija o bajo un criterio haciendo evoluacionar la población
        // Semaphore of 10 threads
        for (i in 0..99) {
            try {
                //Hace evoluacionar a la poblacion
                println("\nEvolucionando poblacion $i...")
                poblacion.evolve()

                //obtiene el mejor cromosoma de la población actual
                val mejor = poblacion.fittestChromosome
                println("\nResultados Evolucion poblacion $i")
                FuncionEvaluacion.println(mejor) //muestra su conformación
                println("\tFitness:" + mejor.fitnessValue) //muestra su evaluación
            } catch (ex: InterruptedException) {
                ex.printStackTrace()
            }
        }

        //Si finaliza evoluacion, obtiene el mejor y lo imprime
        val mejor = poblacion.fittestChromosome
        println("\n\nMEJOR INDIVIDUO:")
        FuncionEvaluacion.println(mejor) //muestra su conformación
        println("\tFitness:" + mejor.fitnessValue) //muestra su evaluación
    }

    private fun initSettings() {
        // read Config.txt File
        val file = File("Config.txt")
        try {
            val scanner = Scanner(file)
            while (scanner.hasNextLine()) {
                val line = scanner.nextLine()
                val parts = line.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                if (parts[0].contains("Team1")) {
                    team1 = parts[1].trim { it <= ' ' }
                } else if (parts[0].contains("Team2")) {
                    team2 = parts[1].trim { it <= ' ' }
                }
            }
            scanner.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        Writerobocup(team1, team2, true)
        Writerobocup(team1, team2, false)

        // Define Exit routine
        Runtime.getRuntime().addShutdownHook(Thread {
            Writerobocup(team1, team2, true)
            println("Exiting...")
            // close every thread
            for (t in Thread.getAllStackTraces().keys) {
                if (t.state == Thread.State.RUNNABLE) t.stop()
            }
            System.exit(0)
        })
    } // Exit routine
}