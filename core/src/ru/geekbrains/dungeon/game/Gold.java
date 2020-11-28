package ru.geekbrains.dungeon.game;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import lombok.Data;
import ru.geekbrains.dungeon.helpers.Assets;
import ru.geekbrains.dungeon.helpers.Poolable;

@Data
public class Gold implements Poolable {
    private GameController gc;
    TextureRegion textureGold;
    boolean active;
    int cellX;
    int cellY;

    public Gold(GameController gc) {
        this.textureGold = Assets.getInstance().getAtlas().findRegion("projectile");
        this.gc = gc;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    public Gold activate(int cellX, int cellY) {
        this.cellX = cellX;
        this.cellY = cellY;
        return this;
    }
    public void deactivate() {
        active = false;
    }

    public void update(float dt) {
        for (int i = 0; i < gc.getUnitController().getAllUnits().size(); i++) {
            if(cellX == gc.getUnitController().getAllUnits().get(i).cellX && cellY == gc.getUnitController().getAllUnits().get(i).cellY){
                deactivate();
            }
        }
    }

    public void render(SpriteBatch batch, BitmapFont font18) {
        batch.draw(textureGold, cellX * GameMap.CELL_SIZE, cellY * GameMap.CELL_SIZE);
    }
}
