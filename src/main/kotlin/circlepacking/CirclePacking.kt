package circlepacking

import processing.core.PApplet
import processing.core.PConstants

data class Circle(val radius: Float, val x: Float, val y: Float, var color: Int = 0)

class CirclePacking : PApplet() {
    companion object {
        fun run() {
            val art = CirclePacking()
            art.setSize(500, 500)
            art.runSketch()
        }
    }

    private val circleSizeCounts = listOf(
        65 to 19,
        37 to 38,
        20 to 75,
        7 to 150,
        3 to 300
    )

    private fun randomXY(xMin: Float, xMax: Float, yMin: Float, yMax: Float): Pair<Float, Float> {
        return Pair(random(xMin, xMax), random(yMin, yMax))
    }

    override fun setup() {
        colorMode(PConstants.HSB, 360f, 100f, 100f, 1.0f)
        noStroke()
        background(70)

        val allCircles = mutableListOf<Circle>()

        for (circleSizeCount in circleSizeCounts) {
            val circleSize = circleSizeCount.first
            val circleCount = circleSizeCount.second
            for (i in 1..circleCount) {
                // allow up to 100 collisions
                for (c in 0..1000) {
                    // generate random point
                    // do not allow circles to overlap canvas
                    // val (x, y) = randomXY(0f+circleSize, 500f-circleSize, 0f+circleSize, 500f-circleSize);
                    // allow circles overlapping canvas
                    val (x, y) = randomXY(0f, width.toFloat(), 0f, height.toFloat());
                    val testCircle = Circle(circleSize.toFloat(), x, y)
                    if (!circleOverlaps(allCircles, testCircle)) {
                        // get random color
                        val c = weightedChoice(
                            listOf(
                                floatArrayOf(0f, 0f, random(90f, 100f)) to 0.6f,
                                floatArrayOf(random(180f, 220f), 50f, 50f) to 0.3f,
                                floatArrayOf(random(0f, 20f), 80f, 80f) to 0.1f
                            )
                        )
                        testCircle.color = color(c[0], c[1], c[2])
                        allCircles.add(testCircle)
                        break
                    }
                }
            }
        }

        for (circle in allCircles) {
            fill(circle.color)
            ellipse(circle.x, circle.y, circle.radius * 2, circle.radius * 2)
        }

    }

    override fun draw() {
    }

    private fun circleOverlaps(allCircles: List<Circle>, testCircle: Circle): Boolean {
        return allCircles.asSequence().any {
            val distance = dist(it.x, it.y, testCircle.x, testCircle.y)
            distance <= (it.radius + testCircle.radius)
        }
    }

    private fun weightedChoice(colorsAndWeights: List<Pair<FloatArray, Float>>): FloatArray {
        val weightSum = colorsAndWeights.sumBy { (it.second * 100).toInt() }
        if (weightSum != 100) throw AssertionError("Weights should sum to 1")
        val random = random(0f, 1.0f)
        var weightTotal = 0f
        for (i in colorsAndWeights) {
            if (random >= weightTotal && random <= weightTotal + i.second) {
                return i.first
            }
            weightTotal += i.second
        }
        throw Exception("Should have returned a Weighted Choice...")
    }
}

fun main(args: Array<String>) {
    CirclePacking.run()
}
