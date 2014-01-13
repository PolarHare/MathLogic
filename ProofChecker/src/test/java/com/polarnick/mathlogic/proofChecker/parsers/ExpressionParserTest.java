package com.polarnick.mathlogic.proofChecker.parsers;

import junit.framework.Assert;
import org.junit.Test;

/**
 * Date: 10.01.14 at 20:32
 *
 * @author Nickolay Polyarniy aka PolarNick
 */
public class ExpressionParserTest {

    private static final String[] tests = new String[]{"A239", "Z1", "C", "!G777", "(A&B)", "(A|B)", "(A->B)", "((A&B)|(C->D))"};

    @Test
    public void testParseExpression() throws Exception {
        for (String test : tests) {
            Assert.assertEquals("Fail for test = " + test, test, new ExpressionParser().parseExpression(test).toString());
        }
    }

    private static final String[] tests2 = new String[]{"A->B->C", "A&B&C", "A|B|C"};
    private static final String[] toString2 = new String[]{"(A->(B->C))", "((A&B)&C)", "((A|B)|C)"};

    @Test
    public void testMultipleExpression() throws Exception {
        for (int i = 0; i < tests2.length; i++) {
            String test = tests2[i];
            String toString = toString2[i];
            Assert.assertEquals("Fail for test = " + test, toString, new ExpressionParser().parseExpression(test).toString());
        }
    }

    @Test
    public void testParseComplexExpression() throws Exception {
        Assert.assertEquals("(A->(B->((C&D)|(B|(((!!!D123&A)&C)&D123)))))", new ExpressionParser().parseExpression("A->B->(C&D|(B|(!!!D123&(A)&C&D123)))").toString());
    }

}
