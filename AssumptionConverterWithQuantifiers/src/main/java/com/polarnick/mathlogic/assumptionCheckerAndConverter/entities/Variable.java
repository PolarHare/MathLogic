package com.polarnick.mathlogic.assumptionCheckerAndConverter.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Date: 10.01.14 at 20:23
 *
 * @author Nickolay Polyarniy aka PolarNick
 */
public class Variable extends Expression {

    public final String name;
    public final List<Variable> variables;

    public Variable(String name, List<Variable> variables) {
        this.name = name;
        this.variables = variables;
    }

    public Variable(String name) {
        this(name, new ArrayList<>(0));
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

        if (!name.equals(variable.name) || variables.size() != variable.variables.size()) {
            return false;
        }
        for (int i = 0; i < variables.size(); i++) {
            if (!variables.get(i).equals(variable.variables.get(i))) {
                return false;
            }
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
