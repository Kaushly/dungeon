package ru.geekbrains.dungeon.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import ru.geekbrains.dungeon.game.units.Unit;
import ru.geekbrains.dungeon.helpers.Assets;
import ru.geekbrains.dungeon.helpers.Utils;

public class GameMap {
    public enum CellType {
        GRASS, WATER, TREE, SWAMP
    }

    public enum DropType {
        NONE, GOLD
    }

    private class Cell {
        CellType type;

        DropType dropType;
        int dropPower;
        boolean visible;
        int index;
        int cost;

        public Cell() {
            type = CellType.GRASS;
            cost = 1;
            dropType = DropType.NONE;
            index = 0;
        }

        public void changeType(CellType to) {
            type = to;
            if (type == CellType.TREE) {
                index = MathUtils.random(4);
            }
            if(type == CellType.SWAMP){
                cost = 2;
            }
        }
    }

    public static final int CELLS_X = 22;
    public static final int CELLS_Y = 12;
    public static final int CELL_SIZE = 60;
    public static final int FOREST_PERCENTAGE = 5;
    public static final int SWAMP_PERCENTAGE = 10;

    public int getCellsX() {
        return CELLS_X;
    }

    public int getCellsY() {
        return CELLS_Y;
    }

    private Cell[][] data;
    private TextureRegion grassTexture;
    private TextureRegion goldTexture;
    private TextureRegion[] treesTextures;



    public GameMap() {
        this.data = new Cell[CELLS_X][CELLS_Y];
        for (int i = 0; i < CELLS_X; i++) {
            for (int j = 0; j < CELLS_Y; j++) {
                this.data[i][j] = new Cell();
            }
        }
        int treesCount = (int) ((CELLS_X * CELLS_Y * FOREST_PERCENTAGE) / 100.0f);
        for (int i = 0; i < treesCount; i++) {
            this.data[MathUtils.random(0, CELLS_X - 1)][MathUtils.random(0, CELLS_Y - 1)].changeType(CellType.TREE);
        }
        int swampCount = (int)((CELLS_X * CELLS_Y * SWAMP_PERCENTAGE) / 100.0f);
        for (int i = 0; i < swampCount; i++) {
            this.data[MathUtils.random(0, CELLS_X - 1)][MathUtils.random(0, CELLS_Y - 1)].changeType(CellType.SWAMP);
        }

        this.grassTexture = Assets.getInstance().getAtlas().findRegion("grass");
        this.goldTexture = Assets.getInstance().getAtlas().findRegion("chest").split(60, 60)[0][0];
        this.treesTextures = Assets.getInstance().getAtlas().findRegion("trees").split(60, 90)[0];
    }

    public boolean isCellPassable(int cx, int cy) {
        if (cx < 0 || cx > getCellsX() - 1 || cy < 0 || cy > getCellsY() - 1) {
            return false;
        }
        if (data[cx][cy].type != CellType.GRASS && data[cx][cy].type != CellType.SWAMP) {
            return false;
        }
        return true;
    }

    public int costCell(int cx, int cy){
        return data[cx][cy].cost;
    }

    public void render(SpriteBatch batch) {
        for (int i = 0; i < CELLS_X; i++) {
            for (int j = CELLS_Y - 1; j >= 0; j--) {
                if(data[i][j].visible) {
                    batch.draw(grassTexture, i * CELL_SIZE, j * CELL_SIZE);
                    if (data[i][j].type == CellType.TREE) {
                        batch.draw(treesTextures[data[i][j].index], i * CELL_SIZE, j * CELL_SIZE);
                    }
                    if (data[i][j].type == CellType.SWAMP) {
                        batch.setColor(0, 0, 0, 0.5f);
                        batch.draw(grassTexture, i * CELL_SIZE, j * CELL_SIZE);
                        batch.setColor(1, 1, 1, 1);
                    }
                    if (data[i][j].dropType == DropType.GOLD) {
                        batch.draw(goldTexture, i * CELL_SIZE, j * CELL_SIZE);
                    }
                }
            }
        }
    }

    public void makeCellVisible(int cellX, int cellY){
        data[cellX][cellY].visible = true;
    }

    public boolean isCellVisible(int cellX, int cellY){
        return data[cellX][cellY].visible;
    }

    // todo: перенести в калькулятор
    public void generateDrop(int cellX, int cellY, int power) {
        if (MathUtils.random() < 0.5f) {
            DropType randomDropType = DropType.GOLD;

            if (randomDropType == DropType.GOLD) {
                int goldAmount = power + MathUtils.random(power, power * 3);
                data[cellX][cellY].dropType = randomDropType;
                data[cellX][cellY].dropPower = goldAmount;
            }
        }
    }

    public boolean hasDropInCell(int cellX, int cellY) {
        return data[cellX][cellY].dropType != DropType.NONE;
    }

    public void checkAndTakeDrop(Unit unit) {
        Cell currentCell = data[unit.getCellX()][unit.getCellY()];
        if (currentCell.dropType == DropType.NONE) {
            return;
        }
        if (currentCell.dropType == DropType.GOLD) {
            unit.addGold(currentCell.dropPower);
        }
        currentCell.dropType = DropType.NONE;
        currentCell.dropPower = 0;
    }
}
