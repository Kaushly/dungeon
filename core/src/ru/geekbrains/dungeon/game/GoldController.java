package ru.geekbrains.dungeon.game;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import ru.geekbrains.dungeon.helpers.ObjectPool;

public class GoldController extends ObjectPool<Gold> {
    private GameController gc;

    public GoldController(GameController gc) {
        this.gc = gc;
    }

    @Override
    protected Gold newObject() {
        return new Gold(gc);
    }

    public Gold activate(int cellX, int cellY) {
        return getActiveElement().activate(cellX, cellY);
    }

    public void update(float dt) {
        for (int i = 0; i < getActiveList().size(); i++) {
            getActiveList().get(i).update(dt);
        }
        checkPool();
    }

    public void render(SpriteBatch batch, BitmapFont font18) {
        for (int i = 0; i < getActiveList().size(); i++) {
            getActiveList().get(i).render(batch, font18);
        }
    }


}
