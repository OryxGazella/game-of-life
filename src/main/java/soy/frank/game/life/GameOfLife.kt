package soy.frank.game.life

internal object GameOfLife {

    // DieHard   11111111
    // 012345678901234567
    //4
    //3       X
    //2 XX
    //1  X   XXX
    //0
//    @JvmField
//    val seed: List<LivingCell> = listOf(
//            LivingCell(2, 1),
//            LivingCell(6, 1),
//            LivingCell(7, 1),
//            LivingCell(8, 1),
//            LivingCell(1, 2),
//            LivingCell(2, 2),
//            LivingCell(7, 3))

    @JvmField
    val seed: List<LivingCell> = listOf(
            LivingCell(1, 1),
            LivingCell(2, 1),
            LivingCell(3, 1),
            LivingCell(4, 1),
            LivingCell(5, 1),
            LivingCell(6, 1),
            LivingCell(7, 1),
            LivingCell(8, 1),
            LivingCell(9, 1),
            LivingCell(10, 1))


    // Acorn   11111111
    // 012345678901234567
    //4
    //3   X
    //2     X
    //1  XX  XXX
    //0
//    @JvmField
//    val seed: List<LivingCell> = listOf(
//            LivingCell(2, 3),
//            LivingCell(4, 2),
//            LivingCell(5, 1),
//            LivingCell(6, 1),
//            LivingCell(7, 1),
//            LivingCell(1, 1),
//            LivingCell(2, 1)
//    )
//

    // Century
    // 0123
    //2  XX
    //1XXX
    //0 X
//    @JvmField
//    val seed: List<LivingCell> = listOf(
//            LivingCell(1, 0),
//            LivingCell(0, 1),
//            LivingCell(1, 1),
//            LivingCell(2, 1),
//            LivingCell(2, 2),
//            LivingCell(3, 2)
//    )

    // R-pentomino
    //   01234567
    //2// XX
    //1//XX
    //0// X

//    @JvmField
//    val seed: List<LivingCell> = listOf(
//            LivingCell(1, 2),
//            LivingCell(2, 2),
//            LivingCell(0, 1),
//            LivingCell(1, 1),
//            LivingCell(1, 0))

    @JvmStatic
    fun apply(frameToProcess: List<LivingCell>): List<LivingCell> {
        val board = GameBoard(frameToProcess)
        val coordinatesOfLivingCellsAndTheirNeighbours = board.coordinatesOfLivingCellsAndTheirNeighbours()
        return coordinatesOfLivingCellsAndTheirNeighbours.flatMap {
            if (board.isAlive(it.first, it.second)) rulesForLiveCell(it, board)
            else rulesForDeadCells(it, board)
        }
    }

    private fun rulesForLiveCell(liveCell: Pair<Int, Int>, board: GameBoard): List<LivingCell> {
        val column = liveCell.first
        val row = liveCell.second
        val numberOfLiveNeighbours = board.numberOfLiveNeighbours(column, row)
        if (numberOfLiveNeighbours > 3 || numberOfLiveNeighbours < 2) return emptyList()
        else return listOf(LivingCell(column, row))
    }

    private fun rulesForDeadCells(deadCell: Pair<Int, Int>, board: GameBoard): List<LivingCell> {
        if (board.numberOfLiveNeighbours(deadCell.first, deadCell.second) == 3) return listOf(LivingCell(deadCell.first, deadCell.second))
        else return emptyList()
    }
}

class GameBoard(cells: Collection<LivingCell>) {
    private val livingCellsByColumnAndRow: Map<Int, Map<Int, List<LivingCell>>>

    fun coordinatesOfLivingCellsAndTheirNeighbours(): Set<Pair<Int, Int>> {
        return livingCellsByColumnAndRow.flatMap {
            val column = it.key
            it.value.flatMap {
                val row = it.key
                listOf(listOf(Pair(column, row)), coordinatesOfNeighbours(column, row)).flatMap { it }
            }
        }.toSet()
    }

    fun numberOfLiveNeighbours(column: Int, row: Int): Int =
            coordinatesOfNeighbours(column, row).fold(0) { acc, pair ->
                if (isAlive(pair.first, pair.second)) {
                    acc + 1
                } else {
                    acc
                }
            }

    fun coordinatesOfNeighbours(column: Int, row: Int) =
            listOf(
                    Pair(column - 1, row),
                    Pair(column - 1, row - 1),
                    Pair(column - 1, row + 1),
                    Pair(column, row + 1),
                    Pair(column, row - 1),
                    Pair(column + 1, row - 1),
                    Pair(column + 1, row + 1),
                    Pair(column + 1, row))

    fun isAlive(column: Int, row: Int): Boolean {
        return this.livingCellsByColumnAndRow[column]?.get(row) != null
    }

    init {
        livingCellsByColumnAndRow = cells
                .groupBy { it.column }
                .mapValues { it.value.groupBy { it.row } }
    }
}
