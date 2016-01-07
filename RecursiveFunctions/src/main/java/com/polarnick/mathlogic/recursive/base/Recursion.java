package com.polarnick.mathlogic.recursive.base;

import com.polarnick.mathlogic.recursive.utils.Common;

/**
 * @author Polyarnyi Nickolay, PolarNick239
 */
public class Recursion extends AbstractRecursiveFunction {

    public Recursion(AbstractRecursiveFunction[] fg) {
        super(fg);
        assert fg.length == 2;
    }

    @Override
    public long execute(long[] args) {
        AbstractRecursiveFunction f = this.functions[0];
        AbstractRecursiveFunction g = this.functions[1];

        long y = args[args.length - 1];
        long[] xs = Common.subArray(args, 0, args.length - 1);
        if (y == 0) {
            return f.execute(xs);
        } else {
            return g.execute(Common.merge(xs, y - 1, this.execute(Common.merge(xs, y - 1))));
        }
    }
}
