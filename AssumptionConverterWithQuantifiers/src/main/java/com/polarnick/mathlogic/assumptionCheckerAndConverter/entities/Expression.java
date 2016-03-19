package com.polarnick.mathlogic.assumptionCheckerAndConverter.entities;

import com.polarnick.mathlogic.assumptionCheckerAndConverter.utils.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Date: 10.01.14 at 19:39
 *
 * @author Nickolay Polyarniy aka PolarNick
 */
public abstract class Expression {

    public boolean compareToPattern(Expression pattern, Map<String, Expression> patternValues) {
        if (pattern.getClass() == NamedAnyExpression.class) {
            NamedAnyExpression namedExpression = (NamedAnyExpression) pattern;
            Expression expression = patternValues.get(namedExpression.getName());
            if (expression != null) {
                return compareToExpression(expression);
            } else {
                patternValues.put(namedExpression.getName(), this);
                return true;
            }
        }
        return false;
    }

    public boolean compareToExpression(Expression expression) {
        return diffToExpression(expression).size() == 0;
    }

    public abstract List<Pair<Expression, Expression>> diffToExpression(Expression expression);

    public abstract List<Variable> getFreeVariables(List<Variable> busyVariables);

    public List<Variable> getFreeVariables() {
        return getFreeVariables(new ArrayList<>());
    }

    public abstract List<Variable> getBusyVariables();

    public abstract List<Variable> getAllVariables();

    public abstract Expression substitute(Variable x, Expression expression);
}
