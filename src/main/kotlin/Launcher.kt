import boids.main as boidsMain
import cherrytree.main as cherrytreeMain
import circlepacking.main as circlepackingMain
import pi.main as piMain
import randomsierpinski.main as randomsierpinskiMain
import genetic.main as geneticMain
import textgenerator.main as textgeneratorMain
import fallingparticles.main as fallingparticlesMain
import sandbox.main as sandboxMain
import diffusion.main as diffusionMain
import metaball.main as metaballMain

val PROJECTS = listOf(
    "Boids" to ::boidsMain,
    "CherryTree" to ::cherrytreeMain,
    "CirclePacking" to ::circlepackingMain,
    "SquarePi" to ::piMain,
    "RandomSierpinski" to ::randomsierpinskiMain,
    "Rocket" to ::geneticMain,
    "TextGenerator" to ::textgeneratorMain,
    "FallingParticles" to ::fallingparticlesMain,
    "SandBox?" to ::sandboxMain,
    "Diffusion" to ::diffusionMain,
    "Metaball" to ::metaballMain
)


fun main(args: Array<String>) {
    println("Available projects:")

    for ((index, value) in PROJECTS.withIndex()) {
        println("\t${index + 1}: ${value.first}")
    }

    println("\nWhat project would you like to launch?")

    while (true) {
        print("\tChoice > ")
        val line = readLine()

        if (line != null) {
            var id: Int

            try {
                id = line.toInt()
            } catch(exc: NumberFormatException) {
                continue
            }

            if (id - 1 in PROJECTS.indices) {
                println("\nLaunching...")
                PROJECTS[id - 1].second.invoke(args)
                println("Done.")
                break
            }
        }
    }
}
