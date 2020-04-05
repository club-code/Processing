package particles

import kotlin.math.max
import kotlin.math.min

interface System {
    fun setup(grid: Grid)
    fun update(grid: Grid, i: Int, j: Int, frameCount: Int)
    fun close(grid: Grid)
}

class GravitySystem : System {
    override fun setup(grid: Grid) {}

    override fun update(grid: Grid, i: Int, j: Int, frameCount: Int) {
        val cell = grid[i, j]
        if (cell != null && cell is GravityCell) {
            val bottom = grid[i - 1, j]
            if (bottom != null && (bottom is EmptyCell || bottom is LiquidCell)) {
                grid.swap(i, j, i - 1, j)
            }
        }
    }

    override fun close(grid: Grid) {}

}

class GranularSystem : System {
    override fun setup(grid: Grid) {}

    override fun update(grid: Grid, i: Int, j: Int, frameCount: Int) {
        val cell = grid[i, j]
        val evenFrame = frameCount % 2 == 0
        if (cell is GranularCell) {
            if (cell.evenFrame != evenFrame) {
                val bottom = grid[i - 1, j]
                val bottomLeft = grid[i - 1, j - 1]
                val bottomRight = grid[i - 1, j + 1]
                val left = grid[i, j - 1]
                val right = grid[i, j + 1]

                if (bottom != null && (bottom is EmptyCell || bottom is LiquidCell)) {
                    grid.swap(i, j, i - 1, j)
                } else if (evenFrame && left is EmptyCell && bottomLeft is EmptyCell) {
                    grid.swap(i, j, i - 1, j - 1)
                } else if (!evenFrame && right is EmptyCell && bottomRight is EmptyCell) {
                    grid.swap(i, j, i - 1, j + 1)
                }
            }
            cell.evenFrame = evenFrame
        }
    }

    override fun close(grid: Grid) {}

}


const val LIQUID_MAX = 1.0f
const val LIQUID_MIN = 0.005f

const val COMPRESSION_MAX = 0.25f

const val FLOW_MIN = 0.005f
const val FLOW_MAX = 4f
const val FLOW_SPEED = 1f

class LiquidSystem : System {

    private lateinit var diffs: Array<FloatArray>

    private fun initialize(grid: Grid) {
        diffs = Array(grid.height) { FloatArray(grid.width) }
    }

    private fun calculateVerticalFlowValue(remainingLiquid: Float, destination: LiquidCell): Float {
        val sum: Float = remainingLiquid + destination.liquid
        return when {
            sum <= LIQUID_MAX -> LIQUID_MAX
            sum < 2 * LIQUID_MAX + COMPRESSION_MAX -> {
                (LIQUID_MAX * LIQUID_MAX + sum * COMPRESSION_MAX) / (LIQUID_MAX + COMPRESSION_MAX)
            }
            else -> (sum + COMPRESSION_MAX) / 2f
        }
    }

    private fun unsettleNeighbors(grid: Grid, i: Int, j: Int) {
        val top = grid[i + 1, j]
        val right = grid[i, j + 1]
        val bottom = grid[i - 1, j]
        val left = grid[i, j - 1]
        if (top != null && top is LiquidCell) top.settled = false
        if (right != null && right is LiquidCell) right.settled = false
        if (bottom != null && bottom is LiquidCell) bottom.settled = false
        if (left != null && left is LiquidCell) left.settled = false
    }

    override fun setup(grid: Grid) {
        initialize(grid)
    }

    override fun update(grid: Grid, i: Int, j: Int, frameCount: Int) {
        val cell = grid[i, j]
        if (cell != null && cell is LiquidCell) {

            cell.resetFlowDirections()
            if (cell.liquid < LIQUID_MIN) {
                cell.liquid = 0.0f
            } else if (!cell.settled) {
                val startValue: Float = cell.liquid
                var remainingValue: Float = cell.liquid
                var flow: Float

                val bottom = grid[i - 1, j]
                if (bottom != null && (bottom is LiquidCell || bottom is EmptyCell)) {
                    val bottom = if (bottom is EmptyCell) {
                        val newBottom = cell.blankCopy()
                        grid[i - 1, j] = newBottom as Cell
                        newBottom
                    } else bottom as LiquidCell
                    flow = calculateVerticalFlowValue(cell.liquid, bottom) - bottom.liquid
                    if (bottom.liquid > 0 && flow > FLOW_MIN) flow *= FLOW_SPEED

                    flow = max(flow, 0f)

                    flow = min(min(FLOW_MAX, cell.liquid), flow)

                    if (flow != 0f) {
                        remainingValue -= flow
                        diffs[i][j] -= flow
                        diffs[i - 1][j] += flow
                        cell.flowDirections[BOTTOM] = true
                        bottom.settled = false
                    }
                }

                if (remainingValue < LIQUID_MIN) {
                    diffs[i][j] -= remainingValue
                } else {
                    val left = grid[i, j - 1]
                    if (left != null && (left is LiquidCell || left is EmptyCell)) {
                        val left = if (left is EmptyCell) {
                            val newBottom = cell.blankCopy()
                            grid[i, j - 1] = newBottom as Cell
                            newBottom
                        } else left as LiquidCell
                        // Calculate flow rate
                        flow = (remainingValue - left.liquid) / 4f
                        if (flow > LIQUID_MIN) flow *= FLOW_SPEED

                        // constrain flow
                        flow = max(flow, 0f)

                        flow = min(min(FLOW_MAX, remainingValue), flow)

                        // Adjust temp values
                        if (flow != 0f) {
                            remainingValue -= flow
                            diffs[i][j] -= flow
                            diffs[i][j - 1] += flow
                            cell.flowDirections[LEFT] = true
                            left.settled = false
                        }
                    }

                    if (remainingValue < LIQUID_MIN) {
                        diffs[i][j] -= remainingValue
                    } else {

                        val right = grid[i, j + 1]
                        if (right != null && (right is LiquidCell || right is EmptyCell)) {
                            val right = if (right is EmptyCell) {
                                val newBottom = cell.blankCopy()
                                grid[i, j + 1] = newBottom as Cell
                                newBottom
                            } else right as LiquidCell
                            // Calculate flow rate
                            flow = (remainingValue - right.liquid) / 3f
                            if (flow > LIQUID_MIN) flow *= FLOW_SPEED

                            // constrain flow
                            flow = max(flow, 0f)

                            flow = min(min(FLOW_MAX, remainingValue), flow)

                            // Adjust temp values
                            if (flow != 0f) {
                                remainingValue -= flow
                                diffs[i][j] -= flow
                                diffs[i][j + 1] += flow
                                cell.flowDirections[RIGHT] = true
                                right.settled = false
                            }
                        }

                        if (remainingValue < LIQUID_MIN) {
                            diffs[i][j] -= remainingValue
                        } else {
                            val top = grid[i + 1, j]
                            if (top != null && (top is LiquidCell || top is EmptyCell)) {
                                val top = if (top is EmptyCell) {
                                    val newBottom = cell.blankCopy()
                                    grid[i + 1, j] = newBottom as Cell
                                    newBottom
                                } else top as LiquidCell
                                // Calculate flow rate
                                flow = remainingValue - calculateVerticalFlowValue(remainingValue, top)
                                if (flow > LIQUID_MIN) flow *= FLOW_SPEED

                                // constrain flow
                                flow = max(flow, 0f)

                                flow = min(min(FLOW_MAX, remainingValue), flow)

                                // Adjust temp values
                                if (flow != 0f) {
                                    remainingValue -= flow
                                    diffs[i][j] -= flow
                                    diffs[i + 1][j] += flow
                                    cell.flowDirections[TOP] = true
                                    top.settled = false
                                }
                            }
                        }

                        if (remainingValue < LIQUID_MIN) {
                            diffs[i][j] -= remainingValue
                        } else {
                            if (startValue == remainingValue) {
                                cell.settleCount++
                                if (cell.settleCount >= 10) {
                                    cell.resetFlowDirections()
                                    cell.settled = true
                                }
                            } else {
                                unsettleNeighbors(grid, i, j)
                            }
                        }

                    }

                }
            }
        }
    }

    override fun close(grid: Grid) {
        for (i in grid.heightIndices) {
            for (j in grid.widthIndices) {
                val cell = grid[i, j]
                if (cell is LiquidCell) {
                    cell.liquid += diffs[i][j]
                    if (cell.liquid < LIQUID_MIN) {
                        cell.liquid = 0f
                        cell.settled = false // default empty cell to
                        // unsettled
                    }
                    cell.size = min(1f, cell.liquid)
                    val bottom = grid[i-1, j]
                    if (bottom != null && bottom is LiquidCell && bottom.liquid <= 0.99f) {
                        cell.size = 0f
                    }
                    val top = grid[i+1, j]
                    if (top != null && top is LiquidCell && (top.liquid > 0.05f || top.flowDirections[BOTTOM])) {
                        cell.size = 1f
                    }
                }
            }
        }
    }
}


