package soy.frank.game.life.gfx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Disposable;
import kotlin.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class Renderer implements Disposable {

    private Map<Integer, Pair<Integer, Integer>> scales;

    void setScale(int scale) {
        int scaledPower = (int) Math.pow(2, scale);
        this.columns = 5 * scaledPower;
        this.rows = 3 * scaledPower;
        boolean hasGutter = (64f / scaledPower) >= 1.0f;
        this.gutterWidth = (hasGutter ? 64f / scaledPower : 0f);
        this.squareSideLength = (512f * (hasGutter ? 1.0f : 1.125f)) / scaledPower;
        columnOffset = scales.get(scale).component1();
        rowOffset = scales.get(scale).component2();
        leftFrameWidth = gutterWidth / 2;
        bottomFrameWidth = gutterWidth / 4;
    }

    private float gutterWidth;
    private float leftFrameWidth;
    private float bottomFrameWidth;
    private float squareSideLength;

    private int columns = 5;
    private int rows = 2;
    private int columnOffset = 0;
    private int rowOffset = 0;

    private final ShapeRenderer shapeRenderer;

    Renderer() {
        scales = new HashMap<>();
        scales.put(0, new Pair<>(0, 0));
        scales.put(1, new Pair<>(2, 1));
        scales.put(2, new Pair<>(7, 4));
        scales.put(3, new Pair<>(17, 10));
        scales.put(4, new Pair<>(36, 21));
        scales.put(5, new Pair<>(74, 44));
        shapeRenderer = new ShapeRenderer();
        OrthographicCamera camera = new OrthographicCamera(2880f, 1800f);
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.setAutoShapeType(true);
        setScale(0);
    }

    void renderScene(List<Cell> cellList) {
        cellList = cellList.stream()
                .map(c -> new Cell(c.getRed(), c.getGreen(), c.getBlue(), c.getOpacity(), c.getRow() + rowOffset, c.getColumn() + columnOffset))
                .collect(Collectors.toList());
        float GUTTER_RED = 7f / 255.0f;
        float GUTTER_GREEN = 54f / 255.0f;
        float GUTTER_BLUE = 66f / 255.0f;

        Gdx.gl.glClearColor(GUTTER_RED, GUTTER_GREEN, GUTTER_BLUE, 0xff / 255.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        createEmptyGrid();

        //Blinker
        cellList.stream().filter(c -> c.getRow() >= 0 && c.getRow() < rows && c.getColumn() >= 0 && c.getColumn() < columns).forEach(this::drawCell);

        shapeRenderer.end();
    }

    private void createEmptyGrid() {
        for (int column = 0; column < columns; column++) {
            for (int row = 0; row < rows; row++) {
                fillEmptyCell(column, row, 0.3f);
            }
        }
    }

    private void fillEmptyCell(int column, int row, float alpha) {
        float EMPTY_CELL_RED = 131f / 255.0f;
        float EMPTY_CELL_GREEN = 148f / 255.0f;
        float EMPTY_CELL_BLUE = 150f / 255.0f;
        fillCellAt(column, row, EMPTY_CELL_RED, EMPTY_CELL_GREEN, EMPTY_CELL_BLUE, alpha);
    }

    private void drawCell(Cell cell) {
        fillCellAt(
                cell.getColumn(),
                cell.getRow(),
                cell.getRed(),
                cell.getGreen(),
                cell.getBlue(),
                cell.getOpacity());
    }

    private void fillCellAt(int column,
                            int row,
                            float cellRed,
                            float cellGreen,
                            float cellBlue,
                            float cellAlpha) {
        shapeRenderer.setColor(cellRed, cellGreen, cellBlue, cellAlpha);
        shapeRenderer.rect(
                xOffset(column),
                yOffset(row),
                squareSideLength,
                squareSideLength);
    }

    private float yOffset(int row) {
        return -900f + bottomFrameWidth + (row * squareSideLength) + (row * gutterWidth);
    }

    private float xOffset(int column) {
        return -1440f + leftFrameWidth + (column * squareSideLength) + (column * gutterWidth);
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
    }
}
