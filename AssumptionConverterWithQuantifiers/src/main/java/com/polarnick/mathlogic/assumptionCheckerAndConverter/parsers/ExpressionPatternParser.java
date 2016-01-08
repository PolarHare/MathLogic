package com.polarnick.mathlogic.assumptionCheckerAndConverter.parsers;

import com.google.common.base.Preconditions;
import com.polarnick.mathlogic.assumptionCheckerAndConverter.entities.Expression;
import com.polarnick.mathlogic.assumptionCheckerAndConverter.entities.NamedAnyExpression;
import com.polarnick.mathlogic.assumptionCheckerAndConverter.entities.Variable;

import java.util.List;

/**
 * Date: 13.01.14 at 17:36
 *
 * @author Nickolay Polyarniy aka PolarNick
 */
public class ExpressionPatternParser extends ExpressionParser {

    protected Variable createNamed(String name, List<Variable> terms) {
        Preconditions.checkState(name.matches(VARIABLE_REGEXP) || name.matches(PREDICATE_REGEXP),
                "Incorrect variable/predicate NamedAnyExpression name: " + name);
        return new NamedAnyExpression(name);
    }

}
