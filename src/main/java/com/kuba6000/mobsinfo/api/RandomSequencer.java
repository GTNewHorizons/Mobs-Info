package com.kuba6000.mobsinfo.api;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.enchantment.Enchantment;

public class RandomSequencer extends Random {

    private static final long serialVersionUID = 109358312784613473L;

    private static class nexter {

        private final int bound;
        private int next;

        public nexter(int type, int bound) {
            this.next = 0;
            this.bound = bound;
        }

        private boolean getBoolean() {
            return next == 1;
        }

        private int getInt() {
            return next;
        }

        private float getFloat() {
            return next * 0.1f;
        }

        private boolean next() {
            next++;
            return next >= bound;
        }
    }

    private final ArrayList<nexter> nexts = new ArrayList<>();
    public int walkCounter = 0;
    public double chance;
    public boolean exceptionOnEnchantTry = false;
    public int maxWalkCount = -1;
    public float forceFloatValue = -1.f;

    @Override
    public int nextInt(int bound) {
        if (exceptionOnEnchantTry && bound == Enchantment.enchantmentsBookList.length) return -1;
        if (nexts.size() <= walkCounter) { // new call
            if (maxWalkCount == walkCounter) {
                return 0;
            }
            nexts.add(new nexter(0, bound));
            walkCounter++;
            chance /= bound;
            return 0;
        }
        chance /= bound;
        return nexts.get(walkCounter++)
            .getInt();
    }

    @Override
    public float nextFloat() {
        if (forceFloatValue != -1f) return forceFloatValue;
        if (nexts.size() <= walkCounter) { // new call
            if (maxWalkCount == walkCounter) {
                return 0f;
            }
            nexts.add(new nexter(2, 10));
            walkCounter++;
            chance /= 10;
            return 0f;
        }
        chance /= 10;
        return nexts.get(walkCounter++)
            .getFloat();
    }

    @Override
    public boolean nextBoolean() {
        if (nexts.size() <= walkCounter) { // new call
            if (maxWalkCount == walkCounter) {
                return false;
            }
            nexts.add(new nexter(1, 2));
            walkCounter++;
            chance /= 2;
            return false;
        }
        chance /= 2;
        return nexts.get(walkCounter++)
            .getBoolean();
    }

    public void newRound() {
        walkCounter = 0;
        nexts.clear();
        chance = 1d;
        maxWalkCount = -1;
        exceptionOnEnchantTry = false;
        forceFloatValue = -1f;
    }

    public boolean nextRound() {
        walkCounter = 0;
        chance = 1d;
        while (!nexts.isEmpty() && nexts.get(nexts.size() - 1)
            .next()) nexts.remove(nexts.size() - 1);
        return !nexts.isEmpty();
    }
}
