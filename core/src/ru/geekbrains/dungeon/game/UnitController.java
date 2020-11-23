package ru.geekbrains.dungeon.game;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import ru.geekbrains.dungeon.helpers.Assets;

import java.util.ArrayList;
import java.util.List;

public class UnitController {
    private GameController gc;
    private MonsterController monsterController;
    private Hero hero;
    private Unit currentUnit;
    private int index;
    private int round;
    private List<Unit> allUnits;

    public MonsterController getMonsterController() {
        return monsterController;
    }

    public Hero getHero() {
        return hero;
    }

    public boolean isItMyTurn(Unit unit) {
        return currentUnit == unit;
    }

    public boolean isCellFree(int cellX, int cellY) {
        for (Unit u : allUnits) {
            if (u.getCellX() == cellX && u.getCellY() == cellY) {
                return false;
            }
        }
        return true;
    }

    public UnitController(GameController gc) {
        this.gc = gc;
        this.hero = new Hero(gc);
        this.monsterController = new MonsterController(gc);
    }

    public void init() {
        this.monsterController.activate(5, 5);
        this.monsterController.activate(9, 5);
        this.index = -1;
        this.round = 1;
        this.allUnits = new ArrayList<>();
        this.allUnits.add(hero);
        this.allUnits.addAll(monsterController.getActiveList());
        this.nextTurn();
    }

    public void nextTurn() {
        index++;
        if (index >= allUnits.size()) {
            index = 0;
            newRound();
        }
        currentUnit = allUnits.get(index);
        currentUnit.startTurn();
    }

    private void newRound() {
        round++;
        if(round % 3 == 0){
            this.monsterController.activate(MathUtils.random(1, gc.getGameMap().getCellsX()), MathUtils.random(1, gc.getGameMap().getCellsY()));
        }
        hero.hp++;
        for (Monster m : gc.getUnitController().monsterController.getActiveList()) {
            if (m.hp != m.hpMax) {
                m.hp++;
            }
        }
    }

    public void render(SpriteBatch batch, BitmapFont font18) {
        hero.render(batch, font18);
        monsterController.render(batch, font18);
    }

    public void update(float dt) {
        hero.update(dt);
        monsterController.update(dt);


        if (!currentUnit.isActive() || currentUnit.getTurns() == 0) {
            nextTurn();
        }
    }

    public void removeUnitAfterDeath(Unit unit) {
        int unitIndex = allUnits.indexOf(unit);
        allUnits.remove(unit);
        if (unitIndex <= index) {
            index--;
        }
    }

    public int getRound() {
        return round;
    }
}
