package com.polarnick.mathlogic.recursive.base;

/**
 * @author Polyarnyi Nickolay, PolarNick239
 */
public class UProjection extends AbstractRecursiveFunction {

    private final int index;

    public UProjection(int index) {
        this.index = index;
    }

    @Override
    public int execute(int[] args) {
        return args[this.index];
    }
}
