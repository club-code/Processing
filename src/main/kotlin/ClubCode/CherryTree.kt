package ClubCode

import processing.core.PApplet
import processing.core.PConstants
import processing.core.PVector


const val MOTION_BLUR_ALPHA_FACTOR = 30f
const val STOP_LENGTH = 25.0f
const val OSCILLATION_AMPLITUDE_MAX = 6.0f
const val FREQ_WEIGHT_COMPRESSION = 1f

const val FREQ_MIN = 0.02f
const val FREQ_MAX = 0.1f

const val PHASE_MIN = -1.0f
const val PHASE_MAX = 1.0f

const val TRUNC_WIDTH_DECREASE_MIN = 0.6f
const val TRUNC_WIDTH_DECREASE_MAX = 0.9f

const val TRUNC_LENGTH_DECREASE_MIN = 0.8f
const val TRUNC_LENGTH_DECREASE_MAX = 0.9f

const val INIT_ORIENTATION_DIFF_MIN = 20f
const val INIT_ORIENTATION_DIFF_MAX = 40f

const val ORIENTATION_DECREASE_MIN = 0.8f
const val ORIENTATION_DECREASE_MAX = 1.2f

const val NO_SPLITTING_PROBA = 0.1


class Program : PApplet() {
    lateinit var root: Tree

    inner class Tree(private val truncLength: Float, private val truncWidth: Float, private val orientationDifference: Float, orientationMax: Float){
        private val phase: Float = random(PHASE_MIN, PHASE_MAX)
        private var oscillationAmplitude = random(radians(OSCILLATION_AMPLITUDE_MAX))
        private val frequency = random(FREQ_MIN, FREQ_MAX) / log(1 + this.truncWidth * FREQ_WEIGHT_COMPRESSION)
        private var left: Tree? = null
        private var right: Tree? = null

        init {
            if(truncLength > STOP_LENGTH) {
                val noSplitting = random(1f) <= NO_SPLITTING_PROBA
                val coinToss = random(1f) <= 0.5f

                if (noSplitting && coinToss || !noSplitting) {
                    left = Tree(
                        truncLength * random(TRUNC_LENGTH_DECREASE_MIN, TRUNC_LENGTH_DECREASE_MAX),
                        truncWidth * random(TRUNC_WIDTH_DECREASE_MIN, TRUNC_WIDTH_DECREASE_MAX),
                        -random(-orientationMax/10, orientationMax),
                        orientationMax * random(ORIENTATION_DECREASE_MIN, ORIENTATION_DECREASE_MAX)
                    )
                }

                if (noSplitting && !coinToss || !noSplitting) {
                    right = Tree(
                        truncLength * random(TRUNC_LENGTH_DECREASE_MIN, TRUNC_LENGTH_DECREASE_MAX),
                        truncWidth * random(TRUNC_WIDTH_DECREASE_MIN, TRUNC_WIDTH_DECREASE_MAX),
                        random(-orientationMax/10, orientationMax),
                        orientationMax * random(ORIENTATION_DECREASE_MIN, ORIENTATION_DECREASE_MAX)
                    )
                }
            }
        }

        fun noOscillation() {
            this.oscillationAmplitude = 0f
        }

        fun draw(t: Int, position: PVector, orientation: Float){
            strokeWeight(truncWidth)
            val end = PVector.add(position, PVector
                .fromAngle(orientation + oscillationAmplitude * sin(frequency*t + phase))
                .mult(truncLength))
            line(position.x, position.y, end.x, end.y)

            if(truncLength > STOP_LENGTH) {
                left?.let { it.draw(t, end, orientation + it.orientationDifference) }
                right?.let { it.draw(t, end, orientation + it.orientationDifference) }
            } else {
                strokeWeight(0f)
                fill(250f, 80f, 120f, 120f)
                ellipse(end.x, end.y, 20f,20f)
            }
        }
    }

    override fun settings() {
        size(1500, 800)
    }

    override fun setup() {
        frameRate(30f)

        strokeCap(ROUND)
        background(230f, 250f, 220f)
        stroke(80f, 0f, 50f, 200f)
        ellipseMode(CENTER)

        this.mouseClicked()
    }

    override fun mouseClicked() {
        root = Tree(random(100f, 120f), 10f, radians(random(INIT_ORIENTATION_DIFF_MIN, INIT_ORIENTATION_DIFF_MAX)), radians(45f))
        root.noOscillation()
    }

    override fun draw() {
        fill(230f, 250f, 220f, MOTION_BLUR_ALPHA_FACTOR)
        rect(0f,0f, width.toFloat(), height.toFloat())
        root.draw(frameCount, PVector(width.toFloat() / 2, height.toFloat()), radians(-90f))
    }

    fun run(){
        runSketch()
    }
}


fun main(){
    val program = Program()
    program.run()
}

