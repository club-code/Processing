package cherrytree

import processing.core.PApplet
import processing.core.PVector


const val MOTION_BLUR_ALPHA_FACTOR = 150f
const val STOP_LENGTH = 25.0f
const val NO_SPLITTING_PROBA = 0.1
const val SYM_MAX_ORIENTATION_FACTOR = 10f
const val OSCILLATION_AMPLITUDE_MAX = 3.0f
const val FREQ_WEIGHT_COMPRESSION = 1f

const val LEAVES_RADIUS_MIN = 10f
const val LEAVES_RADIUS_MAX = 20f

const val FREQ_MIN = 0.02f
const val FREQ_MAX = 0.1f

const val PHASE_MIN = -1.0f
const val PHASE_MAX = 1.0f

const val TRUNC_WIDTH_DECREASE_MIN = 0.6f
const val TRUNC_WIDTH_DECREASE_MAX = 0.9f

const val TRUNC_LENGTH_DECREASE_MIN = 0.8f
const val TRUNC_LENGTH_DECREASE_MAX = 0.9f

const val TRUNC_STEP_LENGTH_MIN = 10f
const val TRUNC_STEP_LENGTH_MAX = 20f

const val TRUNC_STEP_ORIENTATION_DIFF_MIN = -15f
const val TRUNC_STEP_ORIENTATION_DIFF_MAX = 15f

const val ORIENTATION_DECREASE_MIN = 0.8f
const val ORIENTATION_DECREASE_MAX = 1.2f

const val INIT_ORIENTATION_DIFF_MIN = -20f
const val INIT_ORIENTATION_DIFF_MAX = 20f

const val INIT_TRUNC_LENGTH_MIN = 100f
const val INIT_TRUNC_LENGTH_MAX = 120f

const val INIT_TRUNC_WIDTH_MIN = 20f
const val INIT_TRUNC_WIDTH_MAX = 40f

const val INIT_MAX_ORIENTATION_MIN = 30f
const val INIT_MAX_ORIENTATION_MAX = 50f



class Program : PApplet() {
    lateinit var root: Tree

    inner class Tree(private val truncLength: Float,            private val truncWidth: Float,
                     private val orientationDifference: Float,  private val orientationMax: Float) {
        private var left: Tree? = null
        private var right: Tree? = null
        private val trunc = Trunc(this.truncLength, this.truncWidth, this.orientationDifference)
        private var leafRadius = 0f

        init {
            if (this.truncLength > STOP_LENGTH) {
                val noSplitting = random(1f) <= NO_SPLITTING_PROBA
                val coinToss = random(1f) <= 0.5f

                if (noSplitting && coinToss || !noSplitting) {
                    this.left = Tree(
                        this.truncLength * random(TRUNC_LENGTH_DECREASE_MIN, TRUNC_LENGTH_DECREASE_MAX),
                        this.truncWidth * random(TRUNC_WIDTH_DECREASE_MIN, TRUNC_WIDTH_DECREASE_MAX),
                        -random(-this.orientationMax / SYM_MAX_ORIENTATION_FACTOR, this.orientationMax),
                        this.orientationMax * random(ORIENTATION_DECREASE_MIN, ORIENTATION_DECREASE_MAX)
                    )
                }

                if (noSplitting && !coinToss || !noSplitting) {
                    this.right = Tree(
                        this.truncLength * random(TRUNC_LENGTH_DECREASE_MIN, TRUNC_LENGTH_DECREASE_MAX),
                        this.truncWidth * random(TRUNC_WIDTH_DECREASE_MIN, TRUNC_WIDTH_DECREASE_MAX),
                        random(-this.orientationMax / SYM_MAX_ORIENTATION_FACTOR, this.orientationMax),
                        this.orientationMax * random(ORIENTATION_DECREASE_MIN, ORIENTATION_DECREASE_MAX)
                    )
                }
            } else {
                this.leafRadius = random(LEAVES_RADIUS_MIN, LEAVES_RADIUS_MAX)
            }
        }

        fun noBaseOscillation() {
            this.trunc.noOscillation()
        }

        fun draw(t: Int, position: PVector, orientation: Float) {
            val (end, newOrientation) = this.trunc.draw(t, position, orientation)

            if (this.truncLength > STOP_LENGTH) {
                this.left?.draw(t, end, newOrientation)
                this.right?.draw(t, end, newOrientation)
            } else {
                strokeWeight(0f)
                fill(250f, 80f, 120f, 120f)
                ellipse(end.x, end.y, this.leafRadius, this.leafRadius)
            }
        }
    }


    inner class Trunc(private val length: Float, private val width: Float, private val orientationDifference: Float) {
        private val truncSteps: MutableList<TruncStep> = mutableListOf()
        private val phase: Float = random(PHASE_MIN, PHASE_MAX)
        private var oscillationAmplitude = random(radians(OSCILLATION_AMPLITUDE_MAX))
        private val frequency = random(FREQ_MIN, FREQ_MAX) / log(1 + this.width * FREQ_WEIGHT_COMPRESSION)

        init {
            var sum = 0f

            while (sum < this.length) {
                val l = random(TRUNC_STEP_LENGTH_MIN, TRUNC_STEP_LENGTH_MAX)
                this.truncSteps.add(TruncStep(l, this.width, radians(random(TRUNC_STEP_ORIENTATION_DIFF_MIN,
                                                                            TRUNC_STEP_ORIENTATION_DIFF_MAX))))
                sum += l
            }
        }

        fun noOscillation() {
            this.oscillationAmplitude = 0f
        }

        fun draw(t: Int, position: PVector, orientation: Float): Pair<PVector, Float> {
            var end: PVector = position
            var stepOrientation = (orientation + this.orientationDifference
                                   + this.oscillationAmplitude * sin(this.frequency * t + this.phase))

            for (step in this.truncSteps) {
                val pair = step.draw(end, stepOrientation)
                end = pair.first
                stepOrientation = pair.second
            }

            return end to stepOrientation
        }
    }


    inner class TruncStep(private val length: Float, private val width: Float, private val orientationDifference: Float) {
        fun draw(position: PVector, orientation: Float): Pair<PVector, Float> {
            val newOrientation = orientation + this.orientationDifference
            val end = PVector.add(position, PVector.fromAngle(newOrientation).mult(this.length))
 
            strokeWeight(this.width)
            line(position.x, position.y, end.x, end.y)

            return end to newOrientation
        }
    }


    override fun settings() {
        size(1500, 800)
    }

    override fun setup() {
        frameRate(30f)

        ellipseMode(CENTER)
        strokeCap(ROUND)

        background(230f, 250f, 220f)
        stroke(80f, 0f, 50f, 200f)

        this.mouseClicked()
    }

    override fun mouseClicked() {
        this.root = Tree(random(INIT_TRUNC_LENGTH_MIN,
                                INIT_TRUNC_LENGTH_MAX),
                         random(INIT_TRUNC_WIDTH_MIN,
                                INIT_TRUNC_WIDTH_MAX),
                         radians(random(INIT_ORIENTATION_DIFF_MIN,
                                        INIT_ORIENTATION_DIFF_MAX)),
                         radians(random(INIT_MAX_ORIENTATION_MIN,
                                        INIT_MAX_ORIENTATION_MAX)))
        this.root.noBaseOscillation()
    }

    override fun draw() {
        fill(230f, 250f, 220f, MOTION_BLUR_ALPHA_FACTOR)
        rect(0f, 0f, width.toFloat(), height.toFloat())
        this.root.draw(frameCount, PVector(width.toFloat() / 2, height.toFloat()), radians(-90f))
    }

    fun run() {
        runSketch()
    }
}


fun main(args: Array<String>) {
    Program().run()
}

