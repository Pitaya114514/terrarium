package com.pitaya.terrarium.game.world;

import com.pitaya.terrarium.game.entity.Box;
import com.pitaya.terrarium.game.entity.Entity;
import com.pitaya.terrarium.game.tool.Quadtree;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

public class Chunk {
    public final Quadtree quadtree;
    private final Box loadingArea;
    private final List<Entity> entityList = new ArrayList<>();
    private final World world;

    public Chunk(World world, Vector2f center, float range) {
        this.world = world;
        loadingArea = new Box(range, range, 0, false);
        loadingArea.center = center;
        quadtree = new Quadtree(0, loadingArea);
    }

    public boolean add(Entity entity) {
        if (loadingArea.isIntersected(entity.position)) {
            return entityList.add(entity);
        } else {
            return false;
        }
    }

    public List<Entity> getEntityList() {
        return entityList;
    }
}
