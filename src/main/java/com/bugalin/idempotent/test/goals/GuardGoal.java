package com.bugalin.idempotent.test.goals;

import com.destroystokyo.paper.entity.ai.Goal;
import com.destroystokyo.paper.entity.ai.GoalKey;
import com.destroystokyo.paper.entity.ai.GoalType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;

import java.util.EnumSet;


public class GuardGoal implements Goal<Mob> {
    private final GoalKey<Mob> guardGoalKey = GoalKey.of(Mob.class, new NamespacedKey("idempotent", "guard_goal"));
    private final Mob mob;
    private final Location restLocation;
    private LivingEntity target;
    private final double moveDistance = 20D;
    private final double warnDistance = 10D;
    private final double forgetDistance = 20D;

    public GuardGoal(Mob mob, Location restLocation) {
        this.mob = mob;
        this.restLocation = restLocation;
    }

    @Override
    public boolean shouldActivate() {return true;}

    @Override
    public boolean shouldStayActive() {return shouldActivate();}

    @Override
    public void tick() {
        if (target == null) {
            double distance = mob.getLocation().distance(restLocation);
            if (mob.getTicksLived() % 5 == 0 && distance < moveDistance) {
                System.out.println("Searching");
                LivingEntity newTarget = findTarget();
                if (newTarget != null) {setTarget(newTarget);
                    System.out.println("Target found");}
                return;
            }

            if (distance > moveDistance) {
                mob.getPathfinder().moveTo(restLocation);
            }
            return;
        }
        if (!isValidTarget(target)) {
            target = null;
            return;
        }
        double distance = target.getLocation().distance(mob.getLocation());
        if (distance > forgetDistance) {
            target = null;
            return;
        }
        mob.setTarget(target);
    }

    private boolean isValidTarget(LivingEntity target) {
        if (!target.isValid()) return false;
        else if (target instanceof Mob m) {
            Goal<Mob> goal = Bukkit.getMobGoals().getGoal(m,guardGoalKey);
            if (goal instanceof GuardGoal) return false;
        }
        return true;
    }

    private LivingEntity findTarget() {
        for (Entity e : mob.getNearbyEntities(warnDistance,warnDistance,warnDistance)) {
            System.out.println("Found possible target "+e.getType().name());
            if (e instanceof LivingEntity le && isValidTarget(le)) {
                return le;
            }
        }
        return null;
    }

    private void setTarget(LivingEntity target) {
        if (isValidTarget(target)) this.target = target;
    }

    @Override
    public GoalKey<Mob> getKey() {
        return guardGoalKey;
    }

    @Override
    public EnumSet<GoalType> getTypes() {
        return EnumSet.of(GoalType.TARGET);
    }
}
