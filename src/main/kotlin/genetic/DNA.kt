package genetic

import kotlin.math.floor
import kotlin.random.Random


data class DNA<T>(val genes: MutableList<T> = ArrayList()) {
    fun crossover(partner: DNA<T>): DNA<T> {
        val mid = Random.nextInt(0, genes.size)
        return DNA(
            genes.mapIndexed { index, t ->
                if (index > mid) {
                    t
                } else {
                    partner.genes[index]
                }
            }.toMutableList()
        )
    }

    fun mutate(mutate: (T) -> T): DNA<T> {
        return DNA(
            genes.map {
                mutate(it)
            }.toMutableList()
        )

    }
}