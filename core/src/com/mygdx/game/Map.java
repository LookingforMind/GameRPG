package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class Map {
    public static final int CELLS_X = 16;
    public static final int CELLS_Y = 9;
    public static final int CELL_SIZE = 80;

    private Texture textureGrass;
    private Texture textureWalls;
    private TextureRegion[] regions;
    private byte[][] dataLayer;
    private byte[][] typeLayer;

    public Map() {
        dataLayer = new byte[CELLS_X][CELLS_Y];
        typeLayer = new byte[CELLS_X][CELLS_Y];
        for (int i = 0; i < 15; i++) {
            int cellX = MathUtils.random(0, CELLS_X - 1);
            int cellY = MathUtils.random(0, CELLS_Y - 1);
            dataLayer[cellX][cellY] = 1;
            typeLayer[cellX][cellY] = (byte)MathUtils.random(0, 3);
        }
        textureGrass = new Texture("Grass.png");
        textureWalls = new Texture("Walls.png");
        regions = new TextureRegion(textureWalls).split(100, 100)[0];
    }

    public boolean isCellPassable(Vector2 position) {
        if (position.x < 0.0f || position.x > 1280.0f || position.y < 0.0f || position.y > 720.0f) {
            return false;
        }
        return dataLayer[(int) (position.x / CELL_SIZE)][(int) (position.y / CELL_SIZE)] == 0;
    }

    public void render(SpriteBatch batch) {
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 9; j++) {
                batch.draw(textureGrass, i * 80, j * 80);
            }
        }
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 9; j++) {
                if (dataLayer[i][j] == 1) {
                    batch.draw(regions[typeLayer[i][j]], i * 80 - 10, j * 80 - 10);
                }
            }
        }
    }
}
