package neuralnetwork

import java.util.concurrent.ThreadLocalRandom
import kotlin.math.*
import space.kscience.kmath.linear.*
import space.kscience.kmath.nd.as2D
import space.kscience.kmath.nd.mapToBuffer

fun main() {
    val nn = createNeuralNetwork {
        inputLayer(3)
        hiddenLayer(3)
        outputLayer(3)
    }
    println(nn)
    nn.compute()
    println(nn)
}

fun createNeuralNetwork(op: NeuralNetworkBuilder.() -> Unit): NeuralNetwork {
    val nn = NeuralNetworkBuilder()
    nn.op()
    return nn.build().also { it.randomize() }
}

class NeuralNetwork(
    inputNodeCount: Int,
    hiddenLayerCounts: List<Int>,
    outputLayerCount: Int
) {


    val inputLayer = InputLayer(inputNodeCount)

    val hiddenLayers = hiddenLayerCounts
        .map {
            CalculatedLayer(it)
        }.also { layers ->
            layers.withIndex().forEach { (i, layer) ->
                layer.feedingLayer = (if (i == 0) inputLayer else layers[i - 1])
            }
        }

    val outputLayer = CalculatedLayer(outputLayerCount).also {
        it.feedingLayer = (if (hiddenLayers.isNotEmpty()) hiddenLayers.last() else inputLayer)
    }

    fun randomize() {
        hiddenLayers.forEach { it.randomizeWeights() }
        outputLayer.randomizeWeights()
    }

    fun compute() {
        hiddenLayers.forEach { it.compute() }
        outputLayer.compute()
    }

    val weightMatrices
        get() = hiddenLayers.asSequence().map { it.weightsMatrix }
            .plusElement(outputLayer.weightsMatrix)
            .toList()

    val calculatedLayers = hiddenLayers.plusElement(outputLayer)


    /**
     * Input a set of training values for each node
     */
    fun trainEntries(inputsAndTargets: Iterable<Pair<DoubleArray, DoubleArray>>) {

        // randomize if needed
        val entries = inputsAndTargets.toList()

        var lowestError = Int.MAX_VALUE
        var bestWeights = weightMatrices

        // calculate new hidden and output node values
        (0..10000).asSequence().takeWhile { lowestError > 0 }.forEach {
            randomize()

            val totalError = entries.asSequence().map { (input, target) ->

                inputLayer.withIndex().forEach { (i, layer) -> layer.value = input[i] }
                compute()


                outputLayer.asSequence().map { it.value }.zip(target.asSequence())
                    .filter { (calculated, desired) ->
                        desired == 1.0 && abs(calculated - desired) < .5 ||
                                desired == 0.0 && abs(calculated - desired) > .5
                    }.count()
            }.sum()

            if (entries.count() > 1 && totalError < lowestError) {
                println("$totalError < $lowestError")
                lowestError = totalError
                bestWeights = weightMatrices
            }
        }

        bestWeights.withIndex().forEach { (i, m) ->
            calculatedLayers[i].weightsMatrix = m
        }
    }

    fun predictEntry(vararg inputValues: Double): DoubleArray {


        // assign input values to input nodes
        inputValues.withIndex().forEach { (i, v) -> inputLayer.nodes[i].value = v }

        // calculate new hidden and output node values
        compute()
        return outputLayer.map { it.value }.toDoubleArray()
    }

    override fun toString(): String {
        val sb = StringBuilder()
        sb.append(inputLayer.toString() + "\n")
        sb.append(hiddenLayers.toString() + "\n")
        sb.append(outputLayer.toString() + "\n")
        return sb.toString()
    }
}


// LAYERS
sealed class Layer<N : Node> : Iterable<N> {
    abstract val nodes: List<N>
    override fun iterator() = nodes.iterator()

    override fun toString(): String {
        val sb = StringBuilder()
        for (node in nodes) {
            sb.append("$node ")
        }
        return sb.toString()
    }
}

/**
 * An `neuralnetwork.InputLayer` belongs to the first layer and accepts the input values for each `neuralnetwork.InputNode`
 */
class InputLayer(nodeCount: Int) : Layer<InputNode>() {

    override val nodes = (0 until nodeCount).asSequence()
        .map { InputNode(it) }
        .toList()
}

/**
 * A `neuralnetwork.CalculatedLayer` is used for the hidden and output layers, and is derived off weights and values off each previous layer
 */
class CalculatedLayer(nodeCount: Int) : Layer<CalculatedNode>() {

    lateinit var feedingLayer: Layer<out Node>

    override val nodes by lazy {
        (0 until nodeCount).asSequence()
            .map { CalculatedNode(it, this) }
            .toList()
    }

    var weightsMatrix = Matrix.real(nodeCount, 1) { _, _ -> 0.0 }
    var valuesMatrix: Matrix<Double> = Matrix.real(nodeCount, 1) { _, _ -> 0.0 }

    fun randomizeWeights() {
        weightsMatrix = Matrix.real(count(), feedingLayer.count()) { _, _ ->
            randomInitialValue()
        }
    }

    fun compute() {
        RealMatrixContext.apply {
            valuesMatrix = (weightsMatrix dot (feedingLayer.toMatrix({ it.value }))).mapToBuffer {
                sigmoid(it)
            }.buffer.asMatrix()
        }
    }
}


// NODES
sealed class Node(val index: Int) {
    abstract val value: Double

    override fun toString(): String {
        return value.toString()
    }
}


class InputNode(index: Int) : Node(index) {
    override var value = randomInitialValue()
}


class CalculatedNode(
    index: Int,
    val parentLayer: CalculatedLayer
) : Node(index) {

    override val value: Double
        get() = parentLayer.valuesMatrix[index, 0]

}

fun randomInitialValue() = ThreadLocalRandom.current().nextDouble(-1.0, 1.0)
fun sigmoid(x: Number) = 1.0 / (1.0 + exp(-x.toDouble()))

// BUILDERS
class NeuralNetworkBuilder {

    var input = 0
    var hidden = mutableListOf<Int>()
    var output = 0

    fun inputLayer(nodeCount: Int) {
        input = nodeCount
    }

    fun hiddenLayer(nodeCount: Int) {
        hidden.add(nodeCount)
    }

    fun outputLayer(nodeCount: Int) {
        output = nodeCount
    }

    fun build() = NeuralNetwork(input, hidden, output)
}

fun <T> Iterable<T>.toMatrix(vararg selectors: (T) -> Double): Matrix<Double> {
    val items = toList()

    return Matrix.real(items.count(), selectors.count()) { row, col ->
        selectors[col](items[row])
    }
}