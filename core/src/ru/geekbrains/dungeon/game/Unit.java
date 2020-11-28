package ru.geekbrains.dungeon.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import lombok.Data;
import ru.geekbrains.dungeon.helpers.Assets;
import ru.geekbrains.dungeon.helpers.Poolable;

@Data
public abstract class Unit implements Poolable {
    GameController gc;
    TextureRegion texture;
    TextureRegion textureHp;
    int damage;
    int defence;
    int hp;
    int hpMax;
    int cellX;
    int cellY;
    int gold;
    int attackRange;
    float movementTime;
    float movementMaxTime;
    int targetX, targetY;
    int turns, maxTurns;
    int attackTurns, maxAttackTurns;
    float innerTimer;
    StringBuilder stringHelper;
    StringBuilder step;
    StringBuilder attCount;

    public Unit(GameController gc, int cellX, int cellY, int hpMax) {
        this.gc = gc;
        this.hpMax = hpMax;
        this.hp = hpMax;
        this.cellX = cellX;
        this.cellY = cellY;
        this.targetX = cellX;
        this.targetY = cellY;
        this.damage = 2;
        this.defence = 0;
        this.movementMaxTime = 0.2f;
        this.attackRange = 2;
        this.innerTimer = MathUtils.random(1000.0f);
        this.stringHelper = new StringBuilder();
        this.attCount = new StringBuilder();
        this.step = new StringBuilder();
        this.gold = MathUtils.random(1, 5);
    }

    public void addGold(int amount) {
        gold += amount;
    }

    public void cure(int amount) {
        hp += amount;
        if (hp > hpMax) {
            hp = hpMax;
        }
    }

    public void startTurn() {
        turns = MathUtils.random(1, 6);
        attackTurns =  MathUtils.random(1, 6);;
    }

    public void startRound() {
        cure(1);
    }

    @Override
    public boolean isActive() {
        return hp > 0;
    }

    public boolean takeDamage(Unit source, int amount) {
        hp -= amount;
        if (hp <= 0) {
            gc.getUnitController().removeUnitAfterDeath(this);
            int x = gc.getUnitController().getCurrentUnit().cellX;
            int y = gc.getUnitController().getCurrentUnit().cellY;
            gc.getGoldController().activate(x, y);
//            source.addGold(this.gold);
        }
        return hp <= 0;
    }

    public boolean canIMakeAction() {
        return gc.getUnitController().isItMyTurn(this) && turns > 0 && isStayStill();
    }

    public boolean isStayStill() {
        return cellY == targetY && cellX == targetX;
    }

    public void goTo(int argCellX, int argCellY) {
        if (!gc.getGameMap().isCellPassable(argCellX, argCellY) || !gc.getUnitController().isCellFree(argCellX, argCellY)) {
            return;
        }
        if (Math.abs(argCellX - cellX) + Math.abs(argCellY - cellY) == 1) {
            targetX = argCellX;
            targetY = argCellY;
        }
    }

    public boolean canIAttackThisTarget(Unit target) {
        return cellX - target.getCellX() == 0 && Math.abs(cellY - target.getCellY()) <= attackRange ||
                cellY - target.getCellY() == 0 && Math.abs(cellX - target.getCellX()) <= attackRange;
    }

    public boolean endOfTurn(){
        if(turns == 0 && attackTurns > 0) {
            return !canIAttackThisTarget(gc.getUnitController().getHero());
        }
        return canIAttackThisTarget(gc.getUnitController().getHero());
    }

    public void attack(Unit target) {
        target.takeDamage(this, BattleCalc.attack(this, target));
        this.takeDamage(target, BattleCalc.checkCounterAttack(this, target));
        this.attackTurns--;
    }

    public void update(float dt) {
        innerTimer += dt;
        if (!isStayStill()) {
            movementTime += dt;
            if (movementTime > movementMaxTime) {
                movementTime = 0;
                cellX = targetX;
                cellY = targetY;
                turns--;
            }
        }
        if(Gdx.input.isKeyPressed(Input.Keys.E)){
            gc.getUnitController().getHero().setTurns(0);
            gc.getUnitController().getHero().setAttackTurns(0);
        }
    }

    public void render(SpriteBatch batch, BitmapFont font18) {
        float hpAlpha = hp == hpMax ? 0.4f : 1.0f;


        float px = cellX * GameMap.CELL_SIZE;
        float py = cellY * GameMap.CELL_SIZE;

        if (!isStayStill()) {
            px = cellX * GameMap.CELL_SIZE + (targetX - cellX) * (movementTime / movementMaxTime) * GameMap.CELL_SIZE;
            py = cellY * GameMap.CELL_SIZE + (targetY - cellY) * (movementTime / movementMaxTime) * GameMap.CELL_SIZE;
        }

        batch.draw(texture, px, py);
        batch.setColor(0.0f, 0.0f, 0.0f, hpAlpha);

        float barX = px, barY = py + MathUtils.sin(innerTimer * 5.0f) * 2;
        batch.draw(textureHp, barX + 1, barY + 51, 58, 10);
        batch.setColor(0.7f, 0.0f, 0.0f, hpAlpha);
        batch.draw(textureHp, barX + 2, barY + 52, 56, 8);
        batch.setColor(0.0f, 1.0f, 0.0f, hpAlpha);
        batch.draw(textureHp, barX + 2, barY + 52, (float) hp / hpMax * 56, 8);
        batch.setColor(1.0f, 1.0f, 1.0f, hpAlpha);
        stringHelper.setLength(0);
        attCount.setLength(0);
        step.setLength(0);
        stringHelper.append(hp);
        attCount.append(attackTurns);
        step.append(turns);
        font18.setColor(1.0f, 1.0f, 1.0f, hpAlpha);
        font18.draw(batch, stringHelper, barX, barY + 64, 60, 1, false);
        font18.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        if(this == gc.getUnitController().getCurrentUnit()) {
            font18.draw(batch, "S" + step, barX, barY + 80, 60, 0, false);
            font18.draw(batch, "A" + attCount, barX, barY + 80, 60, -1, false);
        }
        batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
    }


    public boolean amIBlocked() {
        return !(gc.isCellEmpty(cellX - 1, cellY) || gc.isCellEmpty(cellX + 1, cellY) || gc.isCellEmpty(cellX, cellY - 1) || gc.isCellEmpty(cellX, cellY + 1));
    }
}
