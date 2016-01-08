package com.polarnick.mathlogic.assumptionCheckerAndConverter.parsers;

import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * Date: 08.01.16.
 *
 * @author Nickolay Polyarniy
 */
public class ExpressionParserTest {

    @Test
    public void testGetMaxPrefixLength() throws Exception {
        assertEquals(4, ExpressionParser.getMaxPrefixLength("a123", "[a-z][0-9]*"));
        assertEquals(-1, ExpressionParser.getMaxPrefixLength("A123", "[a-z][0-9]*"));
        assertEquals(4, ExpressionParser.getMaxPrefixLength("a123asd4", "[a-z][0-9]*"));
    }

    @Test
    public void testParsingTests() throws Exception {
        ExpressionParser parser = new ExpressionParser();
        for (String name : new String[]{"axiom", "intro", "intro2", "must-fail", "must-fail2", "must-fail3"}) {
            String filepath = "/com/polarnick/mathlogic/assumptionCheckerAndConverter/" + name + ".in";
            java.net.URL url = ExpressionParserTest.class.getResource(filepath);
            java.nio.file.Path resPath = java.nio.file.Paths.get(url.toURI());
            String text = new String(java.nio.file.Files.readAllBytes(resPath), "UTF8");
            String[] lines = text.split("\r\n");
            for (int i = 1; i < lines.length; i++) {
                parser.parseExpression(lines[i]);
            }
            System.out.println("Lines from " + name + " parsed! (" + (lines.length - 1) + " lines)");
        }
    }
}