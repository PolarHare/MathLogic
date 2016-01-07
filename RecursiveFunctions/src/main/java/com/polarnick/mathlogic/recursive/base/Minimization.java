package com.polarnick.mathlogic.recursive.base;

import com.polarnick.mathlogic.recursive.utils.Common;
import org.omg.CORBA.REBIND;

/**
 * @author Polyarnyi Nickolay, PolarNick239
 */
public class Minimization extends AbstractRecursiveFunction {

    public Minimization(AbstractRecursiveFunction[] f) {
        super(f);
        assert f.length == 1;
    }

    @Override
    public long execute(long[] args) {
        AbstractRecursiveFunction f = this.functions[0];
        long[] xsY = Common.merge(args, 0);
        for (int y = 0; y < 10000; y++) {
            xsY[xsY.length - 1] = y;
            if (f.execute(xsY) == 0) {
                return y;
            }
        }
        throw new RuntimeException("Minimization limit exceeded!");
    }
}
