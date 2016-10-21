package soy.frank.game.life

data class LivingCell(val column: Int, val row: Int) {
    companion object {
        @JvmStatic
        fun of(column: Int, row: Int): LivingCell = LivingCell(column, row)
    }
}