import boids.main as boidsMain
import cherrytree.main as cherrytreeMain
import circlepacking.main as circlepackingMain
import pi.main as piMain
import randomsierpinski.main as randomsierpinskiMain
import genetic.main as geneticMain
import textgenerator.main as textgeneratorMain
import fallingparticles.main as fallingparticlesMain


fun main(args: Array<String>) {
    val projects = listOf(
        "Boids" to ::boidsMain,
        "CherryTree" to ::cherrytreeMain,
        "CirclePacking" to ::circlepackingMain,
        "SquarePi" to ::piMain,
        "RandomSierpinski" to ::randomsierpinskiMain,
        "Rocket" to ::geneticMain,
        "TextGenerator" to ::textgeneratorMain,
        "FallingParticles" to ::fallingparticlesMain
    )
    var found = false

    while (!found) {
        for ((index, value) in projects.withIndex()) {
            println("${value.first}: ${index + 1}")
        }

        val a = readLine()

        if (a != null) {
            found = true
            val x = a.toInt()

            if (x - 1 in projects.indices) {
                projects[x - 1].second.invoke(args)
            }
        }
    }
}
