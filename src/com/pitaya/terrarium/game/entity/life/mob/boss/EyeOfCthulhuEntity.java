package com.pitaya.terrarium.game.entity.life.mob.boss;

import com.pitaya.terrarium.game.entity.Action;
import com.pitaya.terrarium.game.entity.Box;
import com.pitaya.terrarium.game.entity.Entity;
import com.pitaya.terrarium.game.entity.MoveController;
import com.pitaya.terrarium.game.World;
import com.pitaya.terrarium.game.util.Util;
import com.pitaya.terrarium.game.util.Velocity;
import org.joml.Vector2f;

public class EyeOfCthulhuEntity extends BossEntity {

    public static EyeOfCthulhuEntity summon(Vector2f position) {
        return new EyeOfCthulhuEntity(position, null);
    }

    public EyeOfCthulhuEntity(Vector2f position, Entity target) {
        super("Eye of Cthulhu", new Box(100, 100, 45), new MoveController(true), position, 4641, 12, 5);
        action = new Action(this) {
            private Velocity velocity;
            private Vector2f targetPos;

            enum Action {
                FIRST_CHASING, FIRST_CRASHING, INVERTING, SECOND_CHASING, SECOND_CRASHING
            }

            private Action actionState = Action.FIRST_CHASING;

            private final Vector2f chasingTarget = new Vector2f();
            private int chasingCd;

            private int crashingCount;
            private int crashingCd;
            private int crashingTime;
            private final Velocity crashingVelocity = new Velocity();
            private final Vector2f crashingTarget = new Vector2f();

            @Override
            public void start(World world) {
                setTargetEntity(target);
                targetPos = getTargetEntity().position;
                velocity = new Velocity();
            }

            @Override
            public void act(World world) {
                chasingCd++;
                if (chasingCd == 150) {
                    crashingTime = 49;
                    actionState = Action.FIRST_CRASHING;
                }
                if (crashingCount >= 4) {
                    velocity.speed = 0;
                    chasingCd = 0;
                    crashingCd = 0;
                    crashingCount = 0;
                    actionState = Action.FIRST_CHASING;
                }

                switch (actionState) {
                    case FIRST_CHASING -> {
                        chasingTarget.set(getTargetPos().x, getTargetPos().y + 94);
                        velocity.radians = Util.Math.getRadians(entity.position, chasingTarget);
                        float distance = entity.position.distance(chasingTarget);
                        if (velocity.speed <= 2.78f) {
                            velocity.speed += 0.19f;
                        }
                        if (velocity.speed > distance) {
                            velocity.speed = distance;
                        }
                        Util.Math.movePos(entity.position, velocity);
                    }
                    case FIRST_CRASHING -> {
                        crashingTime++;
                        int maxTime = 50;
                        if (crashingTime == maxTime) {
                            crashingTarget.set(getTargetPos());
                            crashingVelocity.radians = Util.Math.getRadians(entity.position, crashingTarget);
                            crashingVelocity.speed = 6.6f;
                            crashingCount++;
                        } else if (crashingTime > maxTime) {
                            Util.Math.movePos(entity.position, crashingVelocity);
                            if (crashingTime > maxTime + 15) {
                                crashingVelocity.speed -= 0.8f;
                            }
                            if (crashingVelocity.speed <= 0) {
                                crashingTime = 0;
                            }
                        }
                    }
                }

            }

            @Override
            public void end(World world) {

            }
        };
    }
}
