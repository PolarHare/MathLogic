package com.polarnick.mathlogic.recursive.base;

/**
 * @author Polyarnyi Nickolay, PolarNick239
 */
public class Substitution extends AbstractRecursiveFunction {

    public Substitution(AbstractRecursiveFunction[] fAndGs) {
        super(fAndGs);
        assert fAndGs.length >= 1;
    }

    @Override
    public int execute(int[] args) {
        int[] results = new int[this.functions.length - 1];
        for (int i = 1; i < this.functions.length; i++) {
            results[i - 1] = this.functions[i].execute(args);
        }
        return this.functions[0].execute(results);
    }
}
