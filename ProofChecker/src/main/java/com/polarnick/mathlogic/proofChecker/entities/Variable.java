package com.polarnick.mathlogic.proofChecker.entities;

import java.util.Map;

/**
 * Date: 10.01.14 at 20:23
 *
 * @author Nickolay Polyarniy aka PolarNick
 */
public class Variable extends Expression {

    private final String name;

    public Variable(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Variable variable = (Variable) o;

        if (!name.equals(variable.name)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    @Override
    public boolean compareToPattern(Expression pattern, Map<String, Expression> patternValues) {
        if (pattern.getClass() == getClass()) {
            return compareToExpression(pattern);
        }
        return super.compareToPattern(pattern, patternValues);
    }

    @Override
    public boolean compareToExpression(Expression expression) {
        if (expression.getClass() != getClass()) {
            return false;
        }
        Variable variable = (Variable) expression;
        return name.equals(variable.getName());
    }
}
