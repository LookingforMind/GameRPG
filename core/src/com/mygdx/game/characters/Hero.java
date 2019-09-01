package com.mygdx.game.characters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.GameScreen;
import com.mygdx.game.Item;
import com.mygdx.game.Weapon;

public class Hero extends GameCharacter {
    private String name;
    private int coins;
    private int level;
    private int exp;
    private int[] expTo = {0, 0, 100, 300, 600, 1000, 5000};

    public Hero(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
        this.name = "Alexander";
        this.level = 1;
        this.texture = new Texture("Knight_anim.png");
        this.regions = new TextureRegion(texture).split(80, 80)[0];
        this.textureHp = new Texture("Bar.png");
        this.position = new Vector2(MathUtils.random(0, 1280), MathUtils.random(0, 720));
        while (!gameScreen.getMap().isCellPassable(position)) {
            this.position.set(MathUtils.random(0, 1280), MathUtils.random(0, 720));
        }
        this.direction = new Vector2(0, 0);
        this.hpMax = 100.0f;
        this.hp = this.hpMax;
        this.speed = 100.0f;
        this.weapon = new Weapon("Spear", 80.0f, 1.0f, 4.0f);
        this.secondsPerFrame =0.3f;
    }

    public void renderHUD(SpriteBatch batch, BitmapFont font24) {
        stringHelper.setLength(0);
        stringHelper.append("Knight: ").append(name).append('\n')
                .append("Level: ").append(level).append('\n')
                .append("Exp: ").append(exp).append(" / ").append(expTo[level + 1]).append('\n')
                .append("Coins: ").append(coins);
        font24.draw(batch, stringHelper, 20, 700);
    }

    @Override
    public void update(float dt) {
        damageEffectTimer -= dt;
        animationTimer += dt;

        if (damageEffectTimer < 0.0f) {
            damageEffectTimer = 0.0f;
        }

        Monster nearestMonster = null;
        float minDist = Float.MAX_VALUE;
        for (int i = 0; i < gameScreen.getAllMonsters().size(); i++) {
            float dst = gameScreen.getAllMonsters().get(i).getPosition().dst(this.position);
            if (dst < minDist) {
                minDist = dst;
                nearestMonster = gameScreen.getAllMonsters().get(i);
            }
        }

        if (nearestMonster != null && minDist < weapon.getAttackRadius()) {
            attackTimer += dt;
            if (attackTimer > weapon.getAttackPeriod()) {
                attackTimer = 0.0f;
                nearestMonster.takeDamage(weapon.getDamage());
            }
        }

        direction.set(0, 0);
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            direction.x = 1.0f;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            direction.x = -1.0f;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            direction.y = 1.0f;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            direction.y = -1.0f;
        }

        moveForward(dt);
        checkScreenBounds();
    }

    public void killMonster(Monster monster) {
        exp += monster.hpMax * 5;
        if (exp >= expTo[level + 1]) {
            level++;
            exp -= expTo[level];
            hpMax += 10;
            hp = hpMax;
        }
    }

    public void useItem(Item it) {
        switch (it.getType()) {
            case COINS:
                int amount = MathUtils.random(3, 5);
                coins += amount;
                stringHelper.setLength(0);
                stringHelper.append("coins ").append("+").append(amount);
                gameScreen.getTextEmitter().setup(it.getPosition().x, it.getPosition().y, stringHelper);
                break;
            case MEDKIT:
                hp += 5;
                stringHelper.setLength(0);
                stringHelper.append("hp ").append("+5");
                gameScreen.getTextEmitter().setup(it.getPosition().x, it.getPosition().y, stringHelper);
                if (hp > hpMax) {
                    hp = hpMax;
                }
                break;
        }
        it.deactivate();
    }
}
