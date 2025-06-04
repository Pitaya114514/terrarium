package com.pitaya.terrarium.game.entity;

import com.pitaya.terrarium.game.World;
import com.pitaya.terrarium.game.entity.life.LivingEntity;
import com.pitaya.terrarium.game.world.IDLoader;
import org.joml.Vector2f;

import java.util.Map;

public abstract class Entity {

    public enum EntityGroups {
        DEFAULT(0), ANIMAL(1), PLAYER(2), MOB(2);

        public final int level;

        EntityGroups(int level) {
            this.level = Math.max(level, 0);
        }

        public static boolean isHostile(Entity yourEntity, Entity targetEntity) {
            return yourEntity.group != targetEntity.group && yourEntity.group.level >= targetEntity.group.level;
        }
    }

    public final Box box;
    public final MoveController moveController;
    public final Vector2f position = new Vector2f();
    protected Action action = new Action(this) {
        @Override
        public void start(World world) {

        }

        @Override
        public void act(World world) {

        }

        @Override
        public void end(World world) {

        }
    };
    private EntityGroups group;
    private int time;
    private boolean isAlive;
    private Entity attackTarget;
    private short id;
    private String name;

    public Entity(String name, Box box, MoveController moveController, Vector2f position, EntityGroups group) {
        this.name = name;
        this.box = box;
        this.moveController = moveController;
        this.position.set(position);
        this.box.center = this.position;
        moveController.pos = this.position;
        setGroup(group);
    }



    public float getX() {
        return position.x;
    }

    public float getY() {
        return position.y;
    }

    public void attack(LivingEntity target, double value) {
        if (value < 0) {
            value = 0;
        }
        setAttackTarget(target);
        target.damage(this, value);
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name != null) {
            this.name = name;
        }
    }

    public Entity getAttackTarget() {
        return attackTarget;
    }

    public void setAttackTarget(Entity attackTarget) {
        this.attackTarget = attackTarget;
    }

    public Action getAction() {
        return action;
    }

    public void addTime() {
        time++;
    }

    public int getTime() {
        return time;
    }

    public EntityGroups getGroup() {
        return group;
    }

    public void setGroup(EntityGroups group) {
        this.group = group != null ? group : EntityGroups.DEFAULT;
    }

    public short getId() {
        if (id == 0) {
            for (Map.Entry<Class<? extends Entity>, Short> classShortEntry : IDLoader.getLoader().getEntityIDMap().entrySet()) {
                Class<? extends Entity> key = classShortEntry.getKey();
                Short value = classShortEntry.getValue();
                if (this.getClass() == key) {
                    id = value;
                }
            }
        }
        return id;
    }
}
