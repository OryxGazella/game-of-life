package soy.frank.game.life;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import kotlin.Pair;
import soy.frank.game.life.gfx.Animator;
import soy.frank.game.life.gfx.Zoom;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = 2880;
        config.height = 1800;
        config.fullscreen = true;
        config.title = "Conway's Game of Life";
        new LwjglApplication(new Listener(), config);
    }

    private static class Listener extends ApplicationAdapter {

        private final PublishSubject<List<LivingCell>> gameTicks = PublishSubject.create();
        private final PublishSubject<Float> timeDelta = PublishSubject.create();
        private final PublishSubject<Zoom> zoomEvents = PublishSubject.create();
        private final Observable<List<LivingCell>> gameLogic;

        private Animator animator;

        Listener() {
            gameLogic = gameTicks
                    .map(GameOfLife::apply)
                    .startWith(GameOfLife.seed);
        }

        @Override
        public void create() {
            animator = new Animator(gameTicks);
            Observable
                    .combineLatest(
                            timeDelta.startWith(0f),
                            gameLogic,
                            Pair::new)
                    .subscribe(animator::animate);
            zoomEvents
                    .throttleWithTimeout(100, TimeUnit.MILLISECONDS)
                    .subscribe(animator::zoom);
        }

        @Override
        public void render() {
            if(Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
                animator.dispose();
                Gdx.app.exit();
                System.exit(0);
            }
            if(Gdx.input.isKeyPressed(Input.Keys.EQUALS)) {
                zoomEvents.onNext(Zoom.In);
            }

            if(Gdx.input.isKeyPressed(Input.Keys.MINUS)) {
                zoomEvents.onNext(Zoom.Out);
            }

            timeDelta.onNext(Gdx.graphics.getDeltaTime());
        }

        @Override
        public void dispose() {
            animator.dispose();
        }
    }
}
