package com.polarnick.mathlogic.assumptionCheckerAndConverter.entities;

import com.polarnick.mathlogic.assumptionCheckerAndConverter.parsers.ExpressionPatternParser;

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

    public abstract boolean compareToExpression(Expression expression);

    public abstract boolean evaluate(Map<String, Boolean> values);

    public abstract void replace(Map<String, Expression> expForNamedAnyExpression);

    public abstract void addSteps(Map<String, Boolean> varValues, List<Expression> steps);

    protected static void addSteps(String[] solution, Expression A, Expression B, List<Expression> steps) {
        ExpressionPatternParser parser = new ExpressionPatternParser();
        for (String str : solution) {
            Expression exp = parser.parseExpression(str);
            Map<String, Expression> map = new HashMap<String, Expression>();
            map.put("A", A);
            map.put("B", B);
            exp.replace(map);
            if (exp.getClass() == NamedAnyExpression.class) {
                exp = map.get(((NamedAnyExpression) exp).name);
            }
            steps.add(exp);
        }
    }
}
