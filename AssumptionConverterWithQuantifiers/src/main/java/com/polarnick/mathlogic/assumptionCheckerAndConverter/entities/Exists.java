package com.polarnick.mathlogic.assumptionCheckerAndConverter.entities;

import com.polarnick.mathlogic.assumptionCheckerAndConverter.exceptions.LineNumberException;
import com.polarnick.mathlogic.assumptionCheckerAndConverter.utils.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Date: 08.01.16.
 *
 * @author Nickolay Polyarniy
 */
public class Exists extends Expression {

    public final Variable variable;
    public final Expression expression;

    public Exists(Variable variable, Expression expression) {
        this.variable = variable;
        this.expression = expression;
    }

    @Override
    public String toString() {
        return "?" + variable + expression;
    }

    @Override
    public boolean compareToPattern(Expression pattern, Map<String, Expression> patternValues) {
        if (pattern.getClass() == getClass()) {
            Exists existsPattern = (Exists) pattern;
            return variable.compareToPattern(existsPattern.variable, patternValues)
                    && expression.compareToPattern(existsPattern.expression, patternValues);
        }
        return super.compareToPattern(pattern, patternValues);
    }

    @Override
    public List<Pair<Expression, Expression>> diffToExpression(Expression expression) {
        List<Pair<Expression, Expression>> diff = new ArrayList<>(0);
        if (this == expression) {
            return diff;
        }
        if (getClass() == expression.getClass()) {
            Exists exists = (Exists) expression;
            diff.addAll(variable.diffToExpression(exists.variable));
            diff.addAll(this.expression.diffToExpression(exists.expression));
        } else {
            diff.add(new Pair<>(this, expression));
        }
        return diff;
    }

    @Override
    public List<Variable> getFreeVariables(List<Variable> busyVariables) {
        ArrayList<Variable> vars = new ArrayList<>();
        boolean isBusy = busyVariables.contains(variable);
        if (!isBusy) {
            busyVariables.add(variable);
        }
        vars.addAll(expression.getFreeVariables(busyVariables));
        if (!isBusy) {
            busyVariables.remove(variable);
        }
        return vars;
    }

    @Override
    public List<Variable> getBusyVariables() {
        ArrayList<Variable> vars = new ArrayList<Variable>();
        vars.add(variable);
        return vars;
    }

    @Override
    public List<Variable> getAllVariables() {
        List<Variable> vars = new ArrayList<>();
        vars.add(variable);
        vars.addAll(expression.getAllVariables());
        return vars;
    }
}
