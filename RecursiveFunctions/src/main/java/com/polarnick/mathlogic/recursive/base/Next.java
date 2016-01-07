package com.polarnick.mathlogic.recursive.base;

/**
 * @author Polyarnyi Nickolay, PolarNick239
 */
public class Next extends AbstractRecursiveFunction {

    @Override
    public long execute(long[] args) {
        assert args.length == 1;
        return args[0] + 1;
    }
}
