package com.polarnick.mathlogic.assumptionCheckerAndConverter;

import com.polarnick.mathlogic.proofChecker.entities.Consecution;
import com.polarnick.mathlogic.proofChecker.entities.Expression;
import com.polarnick.mathlogic.proofChecker.parsers.ExpressionParser;
import com.polarnick.mathlogic.proofChecker.parsers.ExpressionPatternParser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Date: 10.01.14 at 20:48
 *
 * @author Nickolay Polyarniy aka PolarNick
 */
public class Checker {

    private static final String END_OF_PROOF_PREFIX = "//END_OF_PROOF";
    private static final String COMMENT_PREFIX = "//";

    private static final String[] unparsedAxioms = new String[]{
            "C->(B->A)",
            "(A->B)->(A->B->C)->(A->C)",
            "A->B->A&B",
            "A&B->A",
            "A&B->B",
            "A->A|B",
            "B->A|B",
            "(A->C)->(B->C)->(A&B->C)",
            "(A->B)->(A->!B)->!A",
            "!!A->A"
    };

    private final List<Expression> axioms;
    private final List<Expression> proofedExpressions;
    private final List<Expression> assumptions;

    public Checker() {
        this.axioms = new ArrayList<Expression>(unparsedAxioms.length);
        this.proofedExpressions = new ArrayList<Expression>();
        this.assumptions = new ArrayList<Expression>();
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

    private boolean containsIn(List<Expression> expressions, Expression expression) {
        for (Expression exp : expressions) {
            if (expression.compareToExpression(exp)) {
                return true;
            }
        }
        return false;
    }

    private boolean wasProofed(Expression expression) {
        return containsIn(proofedExpressions, expression);
    }

    private boolean isProofedByModusPonons(Expression expression) {
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

    private String checkProof(BufferedReader in, PrintWriter out) throws IOException {
        ExpressionParser parser = new ExpressionParser();
        String curLine = in.readLine();
        while(curLine.isEmpty()) {
            curLine = in.readLine();
        }
        String[] lineParts = curLine.split("(,|\\|-)");
        for (int i = 0; i < lineParts.length - 2; i++) {
            assumptions.add(parser.parseExpression(lineParts[i]));
        }
        Expression alpha = parser.parseExpression(lineParts[lineParts.length - 2]);
        curLine = in.readLine();
        for (int i = 0; i < assumptions.size(); i++) {
            out.print(assumptions.get(i));
            if (i + 1 != assumptions.size()) {
                out.print(",");
            } else {
                out.println("|-" + new Consecution(alpha, parser.parseExpression(lineParts[lineParts.length - 1])));
            }
        }
        int lineNumber = 2;
        int firstIncorrectLine = -1;
        while (curLine != null && !curLine.startsWith(END_OF_PROOF_PREFIX)) {
            if (!curLine.startsWith(COMMENT_PREFIX) && !curLine.isEmpty() && firstIncorrectLine == -1) {
                int commentIndex = curLine.indexOf(COMMENT_PREFIX);
                if (commentIndex != -1) {
                    curLine = curLine.substring(0, commentIndex);
                }
                curLine = curLine.trim();
                Expression expI = parser.parseExpression(curLine);
                proofedExpressions.add(expI);
                if (isCorespondsToAxiom(expI) || containsIn(assumptions, expI)) {
                    Expression alphaConsSigma = new Consecution(alpha, expI);
                    out.println(expI);
                    out.println(new Consecution(expI, alphaConsSigma));
                    out.println(alphaConsSigma);
                } else if (expI.compareToExpression(alpha)) {
                    Expression AA = new Consecution(alpha, alpha);
                    Expression A_AA = new Consecution(alpha, AA);
                    Expression AA_A = new Consecution(AA, alpha);
                    Expression A__AA_A = new Consecution(alpha, AA_A);
                    Expression lemma1 = A_AA;
                    Expression lemma2 = new Consecution(A_AA, new Consecution(A__AA_A, AA));
                    Expression lemma3 = new Consecution(A__AA_A, AA);
                    Expression lemma4 = A__AA_A;
                    Expression lemma5 = AA;
                    out.println(lemma1);
                    out.println(lemma2);
                    out.println(lemma3);
                    out.println(lemma4);
                    out.println(lemma5);
                } else {
                    Expression expK = null;
                    Expression expJ = null;
                    for (Expression proofed : proofedExpressions) {
                        if (proofed.getClass() == Consecution.class) {
                            Consecution proofedConsecution = (Consecution) proofed;
                            if (expI.compareToExpression(proofedConsecution.getRight())
                                    && wasProofed(proofedConsecution.getLeft())) {
                                expK = proofedConsecution;
                                expJ = proofedConsecution.getLeft();
                                break;
                            }
                        }
                    }
                    if (expK == null || expJ == null) {
                        firstIncorrectLine = lineNumber;
                    } else {
                        Expression res2 = new Consecution(new Consecution(alpha, new Consecution(expJ, expI)),
                                new Consecution(alpha, expI));
                        Expression res1 = new Consecution(new Consecution(alpha, expJ), res2);
                        out.println(res1);
                        out.println(res2);
                        out.println(new Consecution(alpha, expI));
                    }
                }
            }
            curLine = in.readLine();
            lineNumber++;
        }
        out.println(END_OF_PROOF_PREFIX);
        out.println();
        if (firstIncorrectLine == -1) {
            return "Proof is correct.";
        } else {
            return "Proof is incorrect from the line number " + firstIncorrectLine + ".";
        }
    }

    private static final String DEFAULT_INPUT_FILE = "assumption.txt";

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
        PrintWriter out = new PrintWriter("result.txt");
        int proofNumber = 1;
        while (in.ready()) {
            Checker checker = new Checker();
            String verdict = checker.checkProof(in, out);
            System.out.println("Proof #" + proofNumber + ": " + verdict);
            proofNumber++;
        }
        out.close();
    }

}
