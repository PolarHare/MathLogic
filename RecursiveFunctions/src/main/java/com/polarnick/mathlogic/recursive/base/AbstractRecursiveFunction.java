package com.polarnick.mathlogic.recursive.base;

/**
 * @author Polyarnyi Nickolay, PolarNick239
 */
public abstract class AbstractRecursiveFunction {

    protected final AbstractRecursiveFunction[] functions;

    public AbstractRecursiveFunction() {
        this.functions = new AbstractRecursiveFunction[0];
    }

    public AbstractRecursiveFunction(AbstractRecursiveFunction[] functions) {
        this.functions = functions;
    }

    public abstract long execute(long... args);

}
