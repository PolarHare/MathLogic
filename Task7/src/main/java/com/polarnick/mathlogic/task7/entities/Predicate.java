package com.polarnick.mathlogic.task7.entities;

/**
 * Date: 09.01.16.
 *
 * @author Nickolay Polyarniy
 */
public class Predicate extends Expression {

    public final Variable variable;
    public final Expression expression;
    public final String operator;

    public Predicate(Variable variable, Expression expression, String operator) {
        this.variable = variable;
        this.expression = expression;
        this.operator = operator;
    }

    @Override
    public String toString() {
        return operator + variable + "(" + expression + ")";
    }


}
