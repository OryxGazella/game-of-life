package soy.frank.game.life.gfx;

import com.badlogic.gdx.utils.Disposable;
import io.reactivex.subjects.PublishSubject;
import kotlin.Pair;
import soy.frank.game.life.LivingCell;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Animator implements Disposable {

    private static final float TRANSITION_TIME = 1.0f;
    private final Renderer renderer;
    private final PublishSubject<List<LivingCell>> gameTicks;
    private float timeSinceLastTick = 0f;
    private int scale = 0;

    private Collection<Cell> dyingCells = Collections.emptyList();
    private Collection<Cell> cellsBeingBorn = Collections.emptyList();
    private Collection<Cell> stableCells = Collections.emptyList();

    private List<LivingCell> previousCells = Collections.emptyList();

    public Animator(PublishSubject<List<LivingCell>> gameTicks) {
        this.renderer = new Renderer();
        this.gameTicks = gameTicks;
        this.renderer.setScale(scale);
    }

    public void zoom(Zoom zoomLevel) {
        int scale = this.scale;
        switch (zoomLevel) {
            case Out: scale = scale + 1; break;
            case In: scale = scale - 1;
        }

        if(scale >= 0 && scale <= 5) {
            this.scale = scale;
            renderer.setScale(scale);
        }
    }

    public void animate(Pair<Float, List<LivingCell>> scene) {
        float deltaTime = scene.component1();
        List<LivingCell> cellList = scene.component2();

        if (cellList != this.previousCells) {
            determineTransition(cellList);
            this.previousCells = cellList;
        }

        timeSinceLastTick += deltaTime;
        if (timeSinceLastTick > TRANSITION_TIME) {
            gameTicks.onNext(cellList);
            timeSinceLastTick = 0f;
            return;
        }

        float fadeOut = (1f - (timeSinceLastTick * 3)) > 0f ? (float) (1f - (Math.sin(5.23598775598 * timeSinceLastTick))) : 0f;
        float fadeIn = (timeSinceLastTick * 3) <= 1f ? (float) (Math.sin(5.23598775598 * timeSinceLastTick)) : 1f;

        dyingCells.forEach(c -> c.setOpacity(fadeOut));
        cellsBeingBorn.forEach(c -> c.setOpacity(fadeIn));

        renderer.renderScene(
                Stream.of(dyingCells, stableCells, cellsBeingBorn)
                        .flatMap(Collection::stream)
                        .collect(Collectors.toList()));
    }

    private void determineTransition(List<LivingCell> cellsToGroup) {
        Map<Integer, Map<Integer, List<LivingCell>>> previousMap = groupByColumnAndRow(previousCells);
        Map<Integer, Map<Integer, List<LivingCell>>> transitionMap = groupByColumnAndRow(cellsToGroup);
        stableCells = cellsToGroup.stream()
                .filter(c -> previousMap.get(c.getColumn()) != null && previousMap.get(c.getColumn()).get(c.getRow()) != null)
                .map(lc -> Cell.of(lc.getColumn(), lc.getRow(), 1f))
                .collect(Collectors.toList());
        dyingCells = previousCells.stream()
                .filter(c -> (previousMap.get(c.getColumn()) != null && previousMap.get(c.getColumn()).get(c.getRow()) != null) && (transitionMap.get(c.getColumn()) == null || transitionMap.get(c.getColumn()).get(c.getRow()) == null))
                .map(lc -> Cell.of(lc.getColumn(), lc.getRow(), 1f))
                .collect(Collectors.toList());
        cellsBeingBorn = cellsToGroup.stream()
                .filter(c -> (previousMap.get(c.getColumn()) == null || previousMap.get(c.getColumn()).get(c.getRow()) == null))
                .map(lc -> Cell.of(lc.getColumn(), lc.getRow(), 0f))
                .collect(Collectors.toList());
    }

    private Map<Integer, Map<Integer, List<LivingCell>>> groupByColumnAndRow(List<LivingCell> transition) {
        if (transition == null) return Collections.emptyMap();
        return transition.stream()
                .collect(Collectors.groupingBy(LivingCell::getColumn))
                .entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, es -> es.getValue().stream().
                        collect(Collectors.groupingBy(LivingCell::getRow))));
    }

    @Override
    public void dispose() {
        renderer.dispose();
    }
}
