package ru.geekbrains.dungeon.game.units;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import ru.geekbrains.dungeon.game.Armor;
import ru.geekbrains.dungeon.game.GameMap;
import ru.geekbrains.dungeon.game.Weapon;
import ru.geekbrains.dungeon.helpers.Assets;
import ru.geekbrains.dungeon.game.GameController;
import ru.geekbrains.dungeon.helpers.Utils;

public class Hero extends Unit {
    private String name;
    private int vision = 3;

    public Hero(GameController gc) {
        super(gc, 1, 1, 10, "Hero");
        this.name = "Sir Lancelot";
        this.textureHp = Assets.getInstance().getAtlas().findRegion("hp");
        this.weapon = new Weapon(Weapon.Type.SPEAR, 2, 2);
        this.armor = new Armor(Armor.Type.ANTISWORD, 2, 1);
        openWorld();
    }

    public void update(float dt) {
        super.update(dt);
        if (Gdx.input.justTouched() && canIMakeAction()) {
            Monster m = gc.getUnitController().getMonsterController().getMonsterInCell(gc.getCursorX(), gc.getCursorY());
            if (m != null && canIAttackThisTarget(m, 1)) {
                attack(m);
            } else {
                goTo(gc.getCursorX(), gc.getCursorY(), gc.getGameMap().costCell(gc.getCursorX(), gc.getCursorY()));
                openWorld();
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && isStayStill()) {
            stats.resetPoints();
        }

    }

    private void openWorld() {
        // доделать учитывая все эксепшены и чтобы во все стороны открывалось
        for (int i = this.cellX; i < this.cellX + 5; i++) {
            for (int j = this.cellY; j < this.cellY + 5; j++) {
                if(Utils.getCellsIntDistance(this.cellX,this.cellY, i, j) <= vision){
                    gc.getGameMap().makeCellVisible(i,j);
                }
            }
        }
    }

    public void renderHUD(SpriteBatch batch, BitmapFont font, int x, int y) {
        stringHelper.setLength(0);
        stringHelper
                .append("Player: ").append(name).append("\n")
                .append("Gold: ").append(gold).append("\n");
        font.draw(batch, stringHelper, x, y);
    }
}
