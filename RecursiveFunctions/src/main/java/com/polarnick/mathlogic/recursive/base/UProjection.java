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
    public long execute(long[] args) {
        return args[this.index];
    }
}
