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

    public static class ProofWithAssumptions {
        public List<Expression> assumptions;
        public Expression alphaAssum;
        public Expression toBeProofed;
        public List<Expression> steps;

        public ProofWithAssumptions(List<Expression> assumptions, Expression alphaAssum, Expression toBeProofed, List<Expression> steps) {
            this.assumptions = assumptions;
            this.alphaAssum = alphaAssum;
            this.toBeProofed = toBeProofed;
            this.steps = steps;
        }
    }

    public List<Expression> totalyDeduct(List<Expression> assumptions, Expression toBeProofed, List<Expression> steps) {
        ProofWithAssumptions proof = new ProofWithAssumptions(
                assumptions.subList(0, assumptions.size() - 1), assumptions.get(assumptions.size() - 1), toBeProofed, steps);
        while (proof.alphaAssum != null) {
            proof = new Checker().useDeductionConvertion(proof);
        }
        return proof.steps;
    }

    public ProofWithAssumptions useDeductionConvertion(ProofWithAssumptions proof) {
        List<Expression> resAssumptions = new ArrayList<Expression>();
        Expression resAlpha = null;
        Expression resToBeProofed = null;
        List<Expression> resSteps = new ArrayList<Expression>();
        for (Expression ass : proof.assumptions) {
            assumptions.add(ass);
        }
        StringBuilder firstLine = new StringBuilder();
        for (int i = 0; i < assumptions.size() - 1; i++) {
            resAssumptions.add(assumptions.get(i));
        }
        if (assumptions.size() >= 1) {
            resAlpha = assumptions.get(assumptions.size() - 1);
        }
        resToBeProofed = new Consecution(proof.alphaAssum, proof.toBeProofed);
        int firstIncorrectLine = -1;
        int lineNumber = 0;
        for (Expression expI : proof.steps) {
            if (firstIncorrectLine == -1) {
                proofedExpressions.add(expI);
                if (isCorespondsToAxiom(expI) || containsIn(assumptions, expI)) {
                    Expression alphaConsSigma = new Consecution(proof.alphaAssum, expI);
                    resSteps.add(expI);
                    resSteps.add(new Consecution(expI, alphaConsSigma));
                    resSteps.add(alphaConsSigma);
                } else if (expI.compareToExpression(proof.alphaAssum)) {
                    Expression AA = new Consecution(proof.alphaAssum, proof.alphaAssum);
                    Expression A_AA = new Consecution(proof.alphaAssum, AA);
                    Expression AA_A = new Consecution(AA, proof.alphaAssum);
                    Expression A__AA_A = new Consecution(proof.alphaAssum, AA_A);
                    Expression lemma1 = A_AA;
                    Expression lemma2 = new Consecution(A_AA, new Consecution(A__AA_A, AA));
                    Expression lemma3 = new Consecution(A__AA_A, AA);
                    Expression lemma4 = A__AA_A;
                    Expression lemma5 = AA;
                    resSteps.add(lemma1);
                    resSteps.add(lemma2);
                    resSteps.add(lemma3);
                    resSteps.add(lemma4);
                    resSteps.add(lemma5);
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
                        Expression res2 = new Consecution(new Consecution(proof.alphaAssum, new Consecution(expJ, expI)),
                                new Consecution(proof.alphaAssum, expI));
                        Expression res1 = new Consecution(new Consecution(proof.alphaAssum, expJ), res2);
                        resSteps.add(res1);
                        resSteps.add(res2);
                        resSteps.add(new Consecution(proof.alphaAssum, expI));
                    }
                }
            }
            lineNumber++;
        }
        if (firstIncorrectLine != -1) {
            return null;
        } else {
            return new ProofWithAssumptions(resAssumptions, resAlpha, resToBeProofed, resSteps);
        }
    }

    public List<String> useDeductionConvertion(List<String> source) {
        ExpressionParser parser = new ExpressionParser();
        String[] lineParts = source.get(0).split("(,|\\|-)");
        List<Expression> exprAssums = new ArrayList<Expression>();
        for (int i = 0; i < lineParts.length - 2; i++) {
            exprAssums.add(parser.parseExpression(lineParts[i]));
        }
        Expression alphaAssum = parser.parseExpression(lineParts[lineParts.length - 2]);
        Expression toBeProofed = parser.parseExpression(lineParts[lineParts.length - 1]);
        List<Expression> steps = new ArrayList<Expression>();
        for (int i = 1; i < source.size(); i++) {
            steps.add(parser.parseExpression(source.get(i)));
        }
        ProofWithAssumptions newProof = useDeductionConvertion(new ProofWithAssumptions(exprAssums, alphaAssum, toBeProofed, steps));
        List<String> result = new ArrayList<String>();
        StringBuilder firstLine = new StringBuilder();
        for (Expression ass : newProof.assumptions) {
            firstLine.append(ass + ",");
        }
        if (newProof.alphaAssum != null) {
            firstLine.append(newProof.alphaAssum + "|-" + newProof.toBeProofed);
            result.add(firstLine.toString());
        }
        for (Expression step : newProof.steps) {
            result.add(step.toString());
        }
        result.add(END_OF_PROOF_PREFIX);
        result.add("");
        return result;
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
            List<String> input = new ArrayList<String>();
            String curLine = in.readLine();
            while (curLine != null && !curLine.equals(END_OF_PROOF_PREFIX)) {
                if (!curLine.isEmpty() && !curLine.startsWith("//")) {
                    input.add(curLine);
                }
                curLine = in.readLine();
            }
            List<String> result = checker.useDeductionConvertion(input);
            String verdict;
            if (result == null) {
                verdict = "proof is incorrect!";
            } else {
                verdict = "proof is correct. Covertation succeed!";
                for (String str : result) {
                    out.println(str);
                }
            }
            System.out.println("Proof #" + proofNumber + ": " + verdict);
            proofNumber++;
        }
        out.close();
    }

}
