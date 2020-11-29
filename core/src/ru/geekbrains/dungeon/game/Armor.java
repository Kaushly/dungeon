package ru.geekbrains.dungeon.game;

import lombok.Data;

@Data
public class Armor {

    public enum Type {
        ANTISPEAR(Weapon.Type.SPEAR),
        ANTISWORD(Weapon.Type.SWORD),
        ANTIMACE(Weapon.Type.MACE),
        ANTIAXE(Weapon.Type.AXE),
        ANTIBOW(Weapon.Type.BOW);

        private Weapon.Type armorType;
        Type(Weapon.Type type) {
            this.armorType = type;
        }

        public Weapon.Type getArmorType() {
            return armorType;
        }
    }

    Armor.Type type;
    int defenceSpecial;
    int defence;

    public Armor(Armor.Type type, int defenceSpecial, int defence) {
        this.type = type;
        this.defenceSpecial = defenceSpecial;
        this.defence = defence;
    }
}
