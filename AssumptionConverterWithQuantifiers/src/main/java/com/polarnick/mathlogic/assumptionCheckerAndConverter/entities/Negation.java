package com.polarnick.mathlogic.assumptionCheckerAndConverter.entities;

import com.polarnick.mathlogic.assumptionCheckerAndConverter.utils.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Date: 10.01.14 at 19:41
 *
 * @author Nickolay Polyarniy aka PolarNick
 */
public class Negation extends Expression {

    public Expression expression;

    public Negation(Expression expression) {
        this.expression = expression;
    }

    @Override
    public String toString() {
        return "!" + expression;
    }

    @Override
    public boolean compareToPattern(Expression pattern, Map<String, Expression> patternValues) {
        if (pattern.getClass() == getClass()) {
            Negation negationPattern = (Negation) pattern;
            return expression.compareToPattern(negationPattern.expression, patternValues);
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
            Negation negation = (Negation) expression;
            diff.addAll(this.expression.diffToExpression(negation.expression));
        } else {
            diff.add(new Pair<>(this, expression));
        }
        return diff;
    }

    public List<Variable> getFreeVariables(List<Variable> busyVariables) {
        return expression.getFreeVariables(busyVariables);
    }

    public List<Variable> getBusyVariables() {
        return expression.getBusyVariables();
    }

    public List<Variable> getAllVariables() {
        return expression.getAllVariables();
    }

}
