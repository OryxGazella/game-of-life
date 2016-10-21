package soy.frank.game.life.gfx

data class Cell(
        val red: Float,
        val green: Float,
        val blue: Float,
        var opacity: Float,
        val row: Int,
        val column: Int) {

    companion object {

        @JvmStatic
        fun of(column: Int, row: Int, opacity: Float): Cell =
                Cell(
                        row = row,
                        column = column,
                        opacity = opacity,
                        red = 38f / 255.0f,
                        green = 139f / 255.0f,
                        blue = 210f / 255.0f)
    }
}
