package genetic

import kotlin.random.Random

interface Evolutionable<M : Evolutionable<M, T>, T> {
    val dna: DNA<T>

    fun computeFitness(): Float
    fun changeDNA(dna: DNA<T>): M
    fun mutate(it: T): T
}

const val MAT_COUNT = 20

class Population<M, T : Evolutionable<T, M>>(var list: MutableList<T> = ArrayList()) {
    private val matingPool = mutableListOf<T>()

    fun evaluate() {
        val fitness = list.map { it.computeFitness() }
        val max = fitness.maxBy {
            it
        }
        if (max != null) {
            val normalizedFitness = fitness.map { it / max }
            for ((i, value) in normalizedFitness.withIndex()) {
                val n = (value * MAT_COUNT).toInt()
                for (x in 0 until n) {
                    matingPool.add(list[i])
                }
            }
        }
        selection()
        matingPool.clear()
    }

    private fun selection() {
        list = list.map {
            val parentA = matingPool[Random.nextInt(0, matingPool.size)].dna
            val parentB = matingPool[Random.nextInt(0, matingPool.size)].dna
            val child = parentA.crossover(parentB).mutate(it::mutate)

            it.changeDNA(child)
        }.toMutableList()
    }
}
