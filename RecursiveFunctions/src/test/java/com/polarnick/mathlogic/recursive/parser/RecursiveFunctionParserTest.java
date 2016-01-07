package com.polarnick.mathlogic.recursive.parser;

import com.polarnick.mathlogic.recursive.base.AbstractRecursiveFunction;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class RecursiveFunctionParserTest {

    @Test
    public void testParsingNext() throws Exception {
        AbstractRecursiveFunction n = new RecursiveFunctionParser("N").parse();
        assertEquals(n.execute(10), 11);
        assertEquals(n.execute(0), 1);
    }

    @Test
    public void testParsingAddition() throws Exception {
        AbstractRecursiveFunction foo = new RecursiveFunctionParser("R<U(1), S<N, U(3)>>").parse();
        assertEquals(foo.execute(0, 0), 0 + 0);
        assertEquals(foo.execute(239, 0), 239 + 0);
        assertEquals(foo.execute(0, 239), 0 + 239);
        assertEquals(foo.execute(239, 30), 239 + 30);
    }

    @Test
    public void testParsingAdditionPredefined() throws Exception {
        AbstractRecursiveFunction foo = new RecursiveFunctionParser("Add").parse();
        assertEquals(foo.execute(0, 0), 0 + 0);
        assertEquals(foo.execute(239, 0), 239 + 0);
        assertEquals(foo.execute(0, 239), 0 + 239);
        assertEquals(foo.execute(239, 30), 239 + 30);
    }

    @Test
    public void testParsingSubtractionPredefined() throws Exception {
        AbstractRecursiveFunction foo = new RecursiveFunctionParser("Sub").parse();
        assertEquals(foo.execute(100, 20), 100 - 20);
        assertEquals(foo.execute(10, 20), 0);
        assertEquals(foo.execute(10, 10), 0);
        assertEquals(foo.execute(10, 9), 1);
        assertEquals(foo.execute(8, 9), 0);
        assertEquals(foo.execute(10, 0), 10);
        assertEquals(foo.execute(0, 10), 0);
    }

    @Test
    public void testParsingMultiplicationPredefined() throws Exception {
        AbstractRecursiveFunction foo = new RecursiveFunctionParser("Mul").parse();
        assertEquals(foo.execute(0, 0), 0 * 0);
        assertEquals(foo.execute(0, 2), 0 * 2);
        assertEquals(foo.execute(2, 0), 2 * 0);
        assertEquals(foo.execute(4, 3), 4 * 3);
    }

    @Test
    public void testParsingIfPredefined() throws Exception {
        AbstractRecursiveFunction foo = new RecursiveFunctionParser("If<U(1), U(2), U(3)>").parse();
        assertEquals(foo.execute(0, 20, 30), 30);
        assertEquals(foo.execute(1, 20, 30), 20);
        assertEquals(foo.execute(2, 20, 30), 20);
    }

    @Test
    public void testParsingEqualsPredefined() throws Exception {
        AbstractRecursiveFunction foo = new RecursiveFunctionParser("Equal<U(1), U(2)>").parse();
        assertEquals(foo.execute(0, 0), 1);
        assertEquals(foo.execute(1, 1), 1);
        assertEquals(foo.execute(2, 2), 1);

        assertEquals(foo.execute(0, 1), 0);
        assertEquals(foo.execute(1, 0), 0);
        assertEquals(foo.execute(0, 2), 0);
        assertEquals(foo.execute(2, 0), 0);
        assertEquals(foo.execute(2, 5), 0);
    }

    @Test
    public void testParsingDivisionPredefined() throws Exception {
        AbstractRecursiveFunction foo = new RecursiveFunctionParser("Div").parse();
        assertEquals(foo.execute(1, 2), 0);
        assertEquals(foo.execute(2, 2), 1);
        assertEquals(foo.execute(3, 2), 1);
        assertEquals(foo.execute(4, 2), 2);
        assertEquals(foo.execute(11, 3), 3);
    }

    @Test
    public void testParsingModPredefined() throws Exception {
        AbstractRecursiveFunction foo = new RecursiveFunctionParser("Mod").parse();
        assertEquals(foo.execute(1, 2), 1);
        assertEquals(foo.execute(2, 2), 0);
        assertEquals(foo.execute(3, 2), 1);
        assertEquals(foo.execute(4, 2), 0);
        assertEquals(foo.execute(11, 3), 2);
    }

    @Test
    public void testParsingIsPrimePredefined() throws Exception {
        AbstractRecursiveFunction foo = new RecursiveFunctionParser("IsPrime").parse();
        assertEquals(foo.execute(2), 1);
        assertEquals(foo.execute(3), 1);
        assertEquals(foo.execute(4), 0);
        assertEquals(foo.execute(5), 1);
        assertEquals(foo.execute(6), 0);
        assertEquals(foo.execute(7), 1);
        assertEquals(foo.execute(8), 0);
    }

    @Test
    public void testParsingNthPrimePredefined() throws Exception {
        AbstractRecursiveFunction foo = new RecursiveFunctionParser("NthPrime").parse();
        assertEquals(foo.execute(0), 2);
        assertEquals(foo.execute(1), 3);
        assertEquals(foo.execute(2), 5);
        assertEquals(foo.execute(3), 7);
        assertEquals(foo.execute(4), 11);
    }

    @Test
    public void testParsingPartialLogPredefined() throws Exception {
        AbstractRecursiveFunction foo = new RecursiveFunctionParser("PLog").parse();
        assertEquals(foo.execute(3, 27), 3);
        assertEquals(foo.execute(3, 12), 1);
        assertEquals(foo.execute(3, 36), 2);
        assertEquals(foo.execute(5, 7), 0);
    }
}