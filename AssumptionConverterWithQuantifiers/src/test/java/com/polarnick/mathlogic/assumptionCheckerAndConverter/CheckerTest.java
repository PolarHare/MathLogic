package com.polarnick.mathlogic.assumptionCheckerAndConverter;

import com.polarnick.mathlogic.assumptionCheckerAndConverter.exceptions.FreeAxiomUsageException;
import com.polarnick.mathlogic.assumptionCheckerAndConverter.exceptions.LineNumberException;
import com.polarnick.mathlogic.assumptionCheckerAndConverter.parsers.ExpressionParser;
import org.testng.annotations.Test;
import org.testng.collections.Lists;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.*;

/**
 * Date: 08.01.16.
 *
 * @author Nickolay Polyarniy
 */
public class CheckerTest {

    private List<String> loadFile(String name) throws Exception {
        String filepath = "/com/polarnick/mathlogic/assumptionCheckerAndConverter/" + name;
        java.net.URL url = CheckerTest.class.getResource(filepath);
        java.nio.file.Path resPath = java.nio.file.Paths.get(url.toURI());
        String text = new String(java.nio.file.Files.readAllBytes(resPath), "UTF8");
        return Lists.newArrayList(text.split("\n"));
    }

    @Test
    public void testOkIntro1And2() throws Exception {
        for (String name : new String[]{"intro2"}) {//, "intro2"
            List<String> input = loadFile(name + ".in");
            System.out.println("Lines from " + name + " readed (" + input.size() + " lines)...");
            check(input);
            System.out.println("" + name + " ok!");
        }
    }

    private void check(List<String> input) {
        Checker checker = new Checker();
        List<String> result = checker.useDeductionConvertion(input);
    }

    @Test
    public void testFailAxiom() throws Exception {
        String name = "axiom";
        List<String> input = loadFile(name + ".in");
        System.out.println("Lines from " + name + " readed (" + input.size() + " lines)...");
        check(input);
        System.out.println("" + name + " ok!");
    }

    @Test(expectedExceptions = FreeAxiomUsageException.class)
    public void testFail1() throws Exception {
        String name = "must-fail";
        List<String> input = loadFile(name + ".in");
        System.out.println("Lines from " + name + " readed (" + input.size() + " lines)...");

        boolean success = false;
        try {
            check(input);
            success = true;
        } catch(FreeAxiomUsageException e) {

            assertEquals(e.lineNumber, 5);
            assertEquals(e.x.toString(), "a");
            assertEquals(e.assumption.toString(), "P(a)");

            System.out.println("Exception: " + e);
            throw e;
        } finally {
            assertFalse(success);
        }
    }

    @Test(expectedExceptions = LineNumberException.class)
    public void testFail2() throws Exception {
        String name = "must-fail2";
        List<String> input = loadFile(name + ".in");
        System.out.println("Lines from " + name + " readed (" + input.size() + " lines)...");

        boolean success = false;
        try {
            check(input);
            success = true;
        } catch(LineNumberException e) {

            assertEquals(e.lineNumber, 1);

            System.out.println("Exception: " + e);
            throw e;
        } finally {
            assertFalse(success);
        }
    }

    @Test(expectedExceptions = LineNumberException.class)
    public void testFail3() throws Exception {
        String name = "must-fail3";
        List<String> input = loadFile(name + ".in");
        System.out.println("Lines from " + name + " readed (" + input.size() + " lines)...");

        boolean success = false;
        try {
            check(input);
            success = true;
        } catch(LineNumberException e) {

            assertEquals(e.lineNumber, 2);

            System.out.println("Exception: " + e);
            throw e;
        } finally {
            assertFalse(success);
        }
    }

}