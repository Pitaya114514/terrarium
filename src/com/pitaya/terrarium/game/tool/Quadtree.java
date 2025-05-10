package com.pitaya.terrarium.game.tool;

import com.pitaya.terrarium.game.entity.Box;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

public class Quadtree {

    private int level;      // 当前深度
    private List<Vector2f> objects; // 存储的点对象
    private Box bounds;    // 边界矩形
    private Quadtree[] nodes;    // 四个子节点

    public Quadtree(int level, Box bounds) {
        this.level = level;
        this.bounds = bounds;
        this.objects = new ArrayList<>();
        this.nodes = new Quadtree[4];
    }

    // 清除四叉树
    public void clear() {
        objects.clear();

        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i] != null) {
                nodes[i].clear();
                nodes[i] = null;
            }
        }
    }

    // 分割为四个子节点
    private void split() {
        int subWidth = (int) (bounds.getWidth() / 2);
        int subCenterWidth = subWidth / 2;
        int subHeight = (int) (bounds.getHeight() / 2);
        int subCenterHeight = subHeight / 2;
        int x = (int) bounds.center.x;
        int y = (int) bounds.center.y;

        Box b1 = new Box(subWidth, subHeight, 0 ,false);
        b1.center.set(x - subCenterWidth, y - subCenterHeight);
        nodes[0] = new Quadtree(level + 1, b1);
        Box b2 = new Box(subWidth, subHeight, 0 ,false);
        b2.center.set(x - subCenterWidth, y + subCenterHeight);
        nodes[1] = new Quadtree(level + 1, b2);
        Box b3 = new Box(subWidth, subHeight, 0 ,false);
        b3.center.set(x + subCenterWidth, y + subCenterHeight);
        nodes[2] = new Quadtree(level + 1, b3);
        Box b4 = new Box(subWidth, subHeight, 0 ,false);
        b4.center.set(x + subCenterWidth, y - subCenterHeight);
        nodes[3] = new Quadtree(level + 1, b4);
    }

    // 确定对象属于哪个象限
    private int getIndex(Vector2f p) {
        int index = -1;
        Vector2f bottomLeft = bounds.getBottomLeft();
        double verticalMidpoint = bottomLeft.x + (bounds.getWidth() / 2);
        double horizontalMidpoint = bottomLeft.y + (bounds.getHeight() / 2);

        // 对象可以完全放入哪个象限？
        boolean topQuadrant = (p.y < horizontalMidpoint);
        boolean bottomQuadrant = (p.y >= horizontalMidpoint);

        if (p.x < verticalMidpoint) {
            if (topQuadrant) {
                index = 1;
            } else if (bottomQuadrant) {
                index = 2;
            }
        } else if (p.x >= verticalMidpoint) {
            if (topQuadrant) {
                index = 0;
            } else if (bottomQuadrant) {
                index = 3;
            }
        }

        return index;
    }

    // 插入对象
    public void insert(Vector2f p) {
        if (nodes[0] != null) {
            int index = getIndex(p);

            if (index != -1) {
                nodes[index].insert(p);
                return;
            }
        }

        objects.add(p);

        // 最大深度
        int MAX_LEVELS = 5;
        // 节点最大对象数
        int MAX_OBJECTS = 4;
        if (objects.size() > MAX_OBJECTS && level < MAX_LEVELS) {
            if (nodes[0] == null) {
                split();
            }

            int i = 0;
            while (i < objects.size()) {
                int index = getIndex(objects.get(i));
                if (index != -1) {
                    nodes[index].insert(objects.remove(i));
                } else {
                    i++;
                }
            }
        }
    }

    // 检索可能碰撞的对象
    public List<Vector2f> retrieve(List<Vector2f> returnObjects, Vector2f p) {
        int index = getIndex(p);
        if (index != -1 && nodes[0] != null) {
            nodes[index].retrieve(returnObjects, p);
        }

        returnObjects.addAll(objects);

        return returnObjects;
    }
}
