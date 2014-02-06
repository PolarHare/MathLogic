package com.polarnick.mathlogic.proofChecker;

import com.polarnick.mathlogic.proofChecker.entities.Consecution;
import com.polarnick.mathlogic.proofChecker.entities.Expression;
import com.polarnick.mathlogic.proofChecker.parsers.ExpressionParser;
import com.polarnick.mathlogic.proofChecker.parsers.ExpressionPatternParser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Date: 10.01.14 at 20:48
 *
 * @author Nickolay Polyarniy aka PolarNick
 */
public class ProofChecker {

    private static final String END_OF_PROOF_PREFIX = "//END_OF_PROOF";
    private static final String COMMENT_PREFIX = "//";

    private static final String[] unparsedAxioms = new String[]{
            "A->(B->A)",
            "(A->B)->(A->B->C)->(A->C)",
            "A->B->A&B",
            "A&B->A",
            "A&B->B",
            "A->A|B",
            "B->A|B",
            "(A->C)->(B->C)->(A|B->C)",
            "(A->B)->(A->!B)->!A",
            "!!A->A"
    };

    private final List<Expression> axioms;
    private final List<Expression> proofedExpressions;

    public ProofChecker() {
        this.axioms = new ArrayList<Expression>(unparsedAxioms.length);
        this.proofedExpressions = new ArrayList<Expression>();
        ExpressionPatternParser parser = new ExpressionPatternParser();
        for (String axiom : unparsedAxioms) {
            axioms.add(parser.parseExpression(axiom));
        }
    }

    private boolean isCorespondsToAxiom(Expression expression) {
        for (Expression axiom : axioms) {
            if (expression.compareToPattern(axiom, new HashMap<String, Expression>())) {
                return true;
            }
        }
        return false;
    }

    private boolean wasProofed(Expression expression) {
        for (Expression proofed : proofedExpressions) {
            if (expression.compareToExpression(proofed)) {
                return true;
            }
        }
        return false;
    }

    private boolean proofedByModusPonons(Expression expression) {
        for (Expression proofed : proofedExpressions) {
            if (proofed.getClass() == Consecution.class) {
                Consecution proofedConsecution = (Consecution) proofed;
                if (expression.compareToExpression(proofedConsecution.getRight())
                        && wasProofed(proofedConsecution.getLeft())) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkExpression(Expression expression) {
        return isCorespondsToAxiom(expression) || proofedByModusPonons(expression);
    }

    private String checkProof(BufferedReader in) throws IOException {
        String curLine = in.readLine();
        int lineNumber = 1;
        ExpressionParser parser = new ExpressionParser();
        int firstIncorrectLine = -1;
        while (curLine != null && !curLine.startsWith(END_OF_PROOF_PREFIX)) {
            if (!curLine.startsWith(COMMENT_PREFIX) && !curLine.isEmpty() && firstIncorrectLine == -1) {
                int commentIndex = curLine.indexOf(COMMENT_PREFIX);
                if (commentIndex != -1) {
                    curLine = curLine.substring(0, commentIndex);
                }
                curLine = curLine.trim();
                Expression expression = parser.parseExpression(curLine);
                if (!checkExpression(expression)) {
                    firstIncorrectLine = lineNumber;
                }
                proofedExpressions.add(expression);
            }
            curLine = in.readLine();
            lineNumber++;
        }
        if (firstIncorrectLine == -1) {
            return "Proof is correct.";
        } else {
            return "Proof is incorrect from the line number " + firstIncorrectLine + ".";
        }
    }

    private static final String DEFAULT_INPUT_FILE = "proof.txt";

    public static void main(String[] args) throws IOException {
        String filename;
        if (args.length == 0) {
            filename = DEFAULT_INPUT_FILE;
        } else if (args.length == 1) {
            filename = args[0];
        } else {
            System.out.println("Usage: [inputFile], or no arguments - than default file will be readed - " + DEFAULT_INPUT_FILE);
            return;
        }
        BufferedReader in = new BufferedReader(new FileReader(filename));
        int proofNumber = 1;
        while (in.ready()) {
            ProofChecker checker = new ProofChecker();
            String verdict = checker.checkProof(in);
            System.out.println("Proof #" + proofNumber + ": " + verdict);
            proofNumber++;
        }
    }

}
