package com.pitaya.terrarium.game.entity.life.mob.boss;

import com.pitaya.terrarium.game.entity.Action;
import com.pitaya.terrarium.game.entity.Box;
import com.pitaya.terrarium.game.entity.Entity;
import com.pitaya.terrarium.game.entity.MoveController;
import com.pitaya.terrarium.game.entity.life.mob.ServantOfCthulhuEntity;
import com.pitaya.terrarium.game.util.PosUtil;
import com.pitaya.terrarium.game.World;
import org.joml.Vector2f;

public class EyeOfCthulhuEntity extends BossEntity {

    public EyeOfCthulhuEntity(Vector2f position, Entity target) {
        super("Eye of Cthulhu", new Box(100, 100, 45, true), new MoveController(true), position, 4641, 12, 5);
        action = new Action(this.position) {
            private Vector2f targetPos;

            enum Action {
                FIRST_CHASING, FIRST_CRASHING, INVERTING, SECOND_CHASING, SECOND_CRASHING
            }

            private Action actionState;
            private int cd;
            private int invertCd;
            private int servantCd;
            private int crashCd;
            private int crashCount;
            private float cSlope;
            private boolean cDirection;
            private float slope;
            private boolean direction;
            private boolean isEnraged;
            private boolean si;

            @Override
            public void start(World world) {
                setTargetEntity(target);
                targetPos = getTargetEntity().position;
                slope = PosUtil.getSlope(position, targetPos);
                direction = PosUtil.getDirection(position, targetPos);
            }

            @Override
            public void act(World world) {
                if (!isEnraged && getHealth() / defaultHealth <= 0.64) {
                    isEnraged = true;
                    actionState = Action.INVERTING;
                    cd = 0;
                    servantCd = 0;
                    crashCount = 0;
                    crashCd = 0;
                }
                if (!isEnraged) {
                    actionState = Action.FIRST_CHASING;
                    cd++;
                    if (cd >= 130) {
                        actionState = Action.FIRST_CRASHING;
                    }
                    if (crashCount >= 3) {
                        crashCount = 0;
                        cd = 0;
                        actionState = Action.FIRST_CHASING;
                    }
                } else {
                    if (actionState == Action.INVERTING) {
                        invertCd++;
                        if (invertCd >= 240) {
                            invertCd = 0;
                            box.damage = 54;
                            setDefense(-3);
                            actionState = Action.SECOND_CHASING;
                        }
                    }
                    cd++;
                    if (cd >= 130) {
                        actionState = Action.SECOND_CRASHING;
                    }
                    if (crashCount >= 6) {
                        crashCount = 0;
                        cd = 0;
                        actionState = Action.SECOND_CHASING;
                    }
                }

                float maxSpeed;
                final Vector2f crashPos = new Vector2f();
                switch (actionState) {
                    case FIRST_CHASING -> {
                        servantCd++;
                        if (servantCd == 21) {
                            servantCd = 0;
                            world.addEntity(new ServantOfCthulhuEntity(this.position, target));
                        }
                        slope = PosUtil.getSlope(this.position, targetPos);
                        direction = PosUtil.getDirection(this.position, targetPos);
                        maxSpeed = 1;
                        PosUtil.movePos(this.position, direction, slope, maxSpeed);
                    }
                    case FIRST_CRASHING -> {
                        maxSpeed = 3;
                        crashCd++;
                        if (crashCd == 1) {
                            crashPos.set(targetPos);
                            cSlope = PosUtil.getSlope(this.position, crashPos);
                            cDirection = PosUtil.getDirection(this.position, crashPos);
                        }
                        if (crashCd > 1) {
                            PosUtil.movePos(this.position, cDirection, cSlope, maxSpeed);
                        }
                        if (crashCd > 100) {
                            crashCd = 0;
                            crashCount++;
                        }
                    }
                    case INVERTING -> {
                        if (!si) {
                            Vector2f v = new Vector2f();
                            world.addEntity(new ServantOfCthulhuEntity(v.set(this.position).add(0, -60), target));
                            world.addEntity(new ServantOfCthulhuEntity(v.set(this.position).add(0, 60), target));
                            world.addEntity(new ServantOfCthulhuEntity(v.set(this.position).add(60, 0), target));
                            world.addEntity(new ServantOfCthulhuEntity(v.set(this.position).add(-60, 0), target));
                            si = true;
                        }
                    }
                    case SECOND_CHASING -> {
                        slope = PosUtil.getSlope(this.position, targetPos);
                        direction = PosUtil.getDirection(this.position, targetPos);
                        maxSpeed = 3.5f;
                        PosUtil.movePos(this.position, direction, slope, maxSpeed);
                    }
                    case SECOND_CRASHING -> {
                        maxSpeed = 10;
                        crashCd++;
                        if (crashCd == 1) {
                            crashPos.set(targetPos);
                            cSlope = PosUtil.getSlope(this.position, crashPos);
                            cDirection = PosUtil.getDirection(this.position, crashPos);
                        }
                        if (crashCd > 1) {
                            PosUtil.movePos(this.position, cDirection, cSlope, maxSpeed);
                        }
                        if (crashCd > 10) {
                            crashCd = 0;
                            crashCount++;
                        }
                    }
                }
            }

            @Override
            public void end(World world) {

            }
        };
    }

    @Override
    public boolean damage(Entity source, double value) {
        if (source instanceof ServantOfCthulhuEntity) {
            return false;
        }
        super.damage(source, value);
        return true;
    }
}
