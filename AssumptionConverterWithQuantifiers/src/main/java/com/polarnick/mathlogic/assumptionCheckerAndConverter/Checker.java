package com.polarnick.mathlogic.assumptionCheckerAndConverter;

import com.polarnick.mathlogic.assumptionCheckerAndConverter.entities.*;
import com.polarnick.mathlogic.assumptionCheckerAndConverter.exceptions.FreeAxiomUsageException;
import com.polarnick.mathlogic.assumptionCheckerAndConverter.exceptions.LineNumberException;
import com.polarnick.mathlogic.assumptionCheckerAndConverter.exceptions.NonFreeTermException;
import com.polarnick.mathlogic.assumptionCheckerAndConverter.parsers.ExpressionParser;
import com.polarnick.mathlogic.assumptionCheckerAndConverter.parsers.ExpressionPatternParser;
import com.polarnick.mathlogic.assumptionCheckerAndConverter.utils.Pair;
import org.testng.collections.Lists;

import java.io.*;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private static final String axiom11 = "@x(A)->B";  // ∀x(ψ) → (ψ[x := θ])
    private static final String axiom12 = "B->?x(A)";  // (ψ[x := θ]) → ∃x(ψ)
    private static Consecution axiom11Exp;
    private static Consecution axiom12Exp;

    private List<Expression> loadExpressions(String name) {
        String filepath = "/com/polarnick/mathlogic/assumptionCheckerAndConverter/" + name;
        java.net.URL url = Checker.class.getResource(filepath);
        java.nio.file.Path resPath = null;
        try {
            resPath = java.nio.file.Paths.get(url.toURI());
            String text = new String(java.nio.file.Files.readAllBytes(resPath), "UTF8");
            ExpressionParser parser = new ExpressionParser();
            List<Expression> expressions = new ArrayList<>();
            for (String line : text.split("\n")) {
                expressions.add(parser.parseExpression(line));
            }
            return expressions;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private final List<Expression> axioms;
    private final List<Expression> proofedExpressions;
    private final List<Expression> assumptions;
    private Expression resAlpha = null;
    private List<List<Variable>> assumptionsFreeVariables;
    private final List<Expression> forAllInferences;
    private final List<Expression> existsInferences;

    public Checker() {
        this.axioms = new ArrayList<Expression>(unparsedAxioms.length);
        this.proofedExpressions = new ArrayList<Expression>();
        this.assumptions = new ArrayList<Expression>();
        ExpressionPatternParser parser = new ExpressionPatternParser();
        for (String axiom : unparsedAxioms) {
            axioms.add(parser.parseExpression(axiom));
        }
        axiom11Exp = (Consecution) parser.parseExpression(axiom11);
        axiom12Exp = (Consecution) parser.parseExpression(axiom12);
        forAllInferences = loadExpressions("ExistsMP.in");
        existsInferences = loadExpressions("ForAllMP.in");
    }

    private boolean isCorespondsToAxiom(Expression expression) {
        for (Expression axiom : axioms) {
            if (expression.compareToPattern(axiom, new HashMap<String, Expression>())) {
                return true;
            }
        }
        if (isCorrespondsToForAllAxiom11(expression) || isCorrespondsToExistsAxiom12(expression)) {
            return true;
        }
        return false;
    }

    private boolean isCorrespondsToForAllAxiom11(Expression expression) {
        // ∀x(ψ) → (ψ[x := θ])
        // @x(A)->B
        Map<String, Expression> patternValues = new HashMap<>();
        if (!expression.compareToPattern(axiom11Exp, patternValues)) {
            return false;
        }
        Variable x = (Variable) patternValues.get("x");
        Expression a = patternValues.get("A");
        Expression b = patternValues.get("B");
        List<Pair<Expression, Expression>> diff = a.diffToExpression(b);

        if (diff.size() == 0) {
            return true;
        }

        Expression xInB = null;
        for (Pair<Expression, Expression> d : diff) {
            if (d.first.compareToExpression(x)) {
                if (xInB == null) {
                    xInB = d.second;
                } else if (!xInB.compareToExpression(d.second)) {
                    return false;
                }
            } else {
                return false;
            }
        }

        if (a.substitute(x, xInB).equals(b)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isCorrespondsToExistsAxiom12(Expression expression) {
        // (ψ[x := θ]) → ∃x(ψ)
        // B->?xA
        Map<String, Expression> patternValues = new HashMap<>();
        if (!expression.compareToPattern(axiom12Exp, patternValues)) {
            return false;
        }
        Variable x = (Variable) patternValues.get("x");
        Expression a = patternValues.get("A");
        Expression b = patternValues.get("B");
        Exists xa = (Exists) ((Consecution) expression).getRight();
        List<Pair<Expression, Expression>> diff = a.diffToExpression(b);

        if (diff.size() == 0) {
            return true;
        }

        Expression xInB = null;
        for (Pair<Expression, Expression> d : diff) {
            if (d.first.compareToExpression(x)) {
                if (xInB == null) {
                    xInB = d.second;
                } else if (!xInB.compareToExpression(d.second)) {
                    return false;
                }
            } else {
                return false;
            }
        }

        if (a.substitute(x, xInB).equals(b)) {
            return true;
        } else {
            return false;
        }
    }

    private void checkSubstitution(Variable x, Expression a, Expression newX) {
        if (newX != null && !x.equals(newX)) {
            for (Variable v : newX.getAllVariables()) {
                List<Variable> busyVariables = a.getBusyVariables();
                busyVariables.add(x);
                if (busyVariables.contains(v)) {
                    throw new NonFreeTermException(a, x, newX);
                }
            }
        }

        for (int i = 0; i < assumptionsFreeVariables.size(); ++i) {
            if (assumptionsFreeVariables.get(i).contains(x)) {
                Expression assumption;
                if (i == assumptions.size()) {
                    assumption = resAlpha;
                } else {
                    assumption = assumptions.get(i);
                }
                throw new FreeAxiomUsageException(x, assumption);
            }
        }
    }

    private List<Expression> stepsForAllRule(Expression expression) {
        if (expression instanceof Consecution) {
            Consecution consecution = (Consecution) expression;
            if (consecution.getRight() instanceof ForAll) {
                ForAll forAll = (ForAll) consecution.getRight();
                for (int j = proofedExpressions.size() - 1; j >= 0; --j) {
                    if (proofedExpressions.get(j) instanceof Consecution) {
                        Consecution inputConsecution = (Consecution) proofedExpressions.get(j);
                        if (consecution.getLeft().compareToExpression(inputConsecution.getLeft()) &&
                                forAll.expression.compareToExpression(inputConsecution.getRight())) {
                            if (consecution.getLeft().getAllVariables().contains(forAll.variable)
                                    && consecution.getLeft().getFreeVariables().contains(forAll.variable)) {
                                throw new FreeAxiomUsageException(forAll.variable, consecution.getLeft());
                            }
                            for (int i = 0; i < assumptionsFreeVariables.size(); ++i) {
                                if (assumptionsFreeVariables.get(i).contains(forAll.variable)) {
                                    Expression e;
                                    if (i < assumptions.size()) {
                                        e = assumptions.get(i);
                                    } else {
                                        e =  this.resAlpha;
                                    }
                                    throw new FreeAxiomUsageException(forAll.variable, e);
                                }
                            }

                            List<Expression> steps = new ArrayList<>(forAllInferences.size());
                            for (Expression e : forAllInferences) {
                                steps.add(substitude(e, resAlpha, forAll.expression, consecution.getLeft(), forAll.variable));
                            }
                            return steps;
                        }
                    }
                }
            }
        }
        return null;
    }

    private List<Expression> stepsExistsRule(Expression expression) {
        if (expression instanceof Consecution) {
            Consecution consecution = (Consecution) expression;
            if (consecution.getLeft() instanceof Exists) {
                Exists exists = (Exists) consecution.getLeft();
                for (int j = proofedExpressions.size() - 1; j >= 0; --j) {
                    if (proofedExpressions.get(j) instanceof Consecution) {
                        Consecution inputConsecution = (Consecution) proofedExpressions.get(j);
                        if (consecution.getRight().compareToExpression(inputConsecution.getRight()) &&
                                exists.expression.compareToExpression(inputConsecution.getLeft())) {
                            if (consecution.getRight().getAllVariables().contains(exists.variable)
                                    && consecution.getRight().getFreeVariables().contains(exists.variable)) {
                                throw new RuntimeException("Вывод некорректен начиная с формулы " + proofedExpressions.size() + ": "
                                        + "переменная " + exists.variable.toString() + " входит свободно в формулу " +
                                        consecution.getRight().toString());
                            }
                            for (int i = 0; i < assumptionsFreeVariables.size(); ++i) {
                                if (assumptionsFreeVariables.get(i).contains(exists.variable)) {
                                    Expression e;
                                    if (i < assumptions.size()) {
                                        e = assumptions.get(i);
                                    } else {
                                        e =  this.resAlpha;
                                    }
                                    throw new RuntimeException("Вывод некорректен начиная с формулы " + proofedExpressions.size() + ": " +
                                            "используется правило с квантором по переменной " + exists.variable.toString() +
                                            ", входящей свободно в допущение " + e.toString());
                                }
                            }

                            List<Expression> steps = new ArrayList<>(existsInferences.size());
                            for (Expression e : existsInferences) {
                                steps.add(substitude(e, resAlpha, exists.expression, consecution.getRight(), exists.variable));
                            }
                            return steps;
                        }
                    }
                }
            }
        }
        return null;
    }

    private static Expression substitude(Expression expression, Expression a, Expression b, Expression c, Variable v) {
        if (expression instanceof Variable) {
            if ("A".equals(expression.toString()))
                return a;
            if ("B".equals(expression.toString()))
                return b;
            if ("C".equals(expression.toString()))
                return c;
        }

        if (expression instanceof Exists) {
            Exists exists = (Exists) expression;
            if ("A".equals(exists.variable.getName()))
                return a;
            if ("B".equals(exists.variable.getName()))
                return b;
            if ("C".equals(exists.variable.getName()))
                return c;
        } else if (expression instanceof ForAll) {
            ForAll forAll = (ForAll) expression;
            if ("A".equals(forAll.variable.getName()))
                return a;
            if ("B".equals(forAll.variable.getName()))
                return b;
            if ("C".equals(forAll.variable.getName()))
                return c;
        }


        if (expression instanceof Conjunction) {
            Conjunction conj = (Conjunction) expression;
            return new Conjunction(substitude(conj.getLeft(), a, b, c, v), substitude(conj.getRight(), a, b, c, v));
        }

        if (expression instanceof Disjunction) {
            Disjunction disj = (Disjunction) expression;
            return new Disjunction(substitude(disj.getLeft(), a, b, c, v), substitude(disj.getRight(), a, b, c, v));
        }

        if (expression instanceof Consecution) {
            Consecution cons = (Consecution) expression;
            return new Consecution(substitude(cons.getLeft(), a, b, c, v), substitude(cons.getRight(), a, b, c, v));
        }

        if (expression instanceof Negation) {
            Negation neg = (Negation) expression;
            return new Negation(substitude(neg.expression, a, b, c, v));
        }

        if (expression instanceof Exists) {
            Exists exists = (Exists) expression;
            return new Exists(v, substitude(exists.expression, a, b, c, v));
        }

        if (expression instanceof ForAll) {
            ForAll forAll = (ForAll) expression;
            forAll = new ForAll(v, substitude(forAll.expression, a, b, c, v));
            return forAll;
        }
        return null;
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
        Expression resToBeProofed = null;
        List<Expression> resSteps = new ArrayList<Expression>();
        for (Expression ass : proof.assumptions) {
            assumptions.add(ass);
        }
        StringBuilder firstLine = new StringBuilder();
        for (int i = 0; i < assumptions.size(); i++) {
            resAssumptions.add(assumptions.get(i));
        }
        resAlpha = proof.alphaAssum;
        resToBeProofed = new Consecution(proof.alphaAssum, proof.toBeProofed);
        int lineNumber = 1;
        for (Expression expI : proof.steps) {
            try {
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
                    List<Expression> steps = stepsForAllRule(expI);
                    if (steps == null) {
                        steps = stepsExistsRule(expI);
                    }
                    if (steps != null) {
                        resSteps.addAll(steps);
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
                            throw new LineNumberException(lineNumber);
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
                proofedExpressions.add(expI);
            } catch (LineNumberException e) {
                e.lineNumber = lineNumber;
                throw e;
            }
            lineNumber++;
        }
        return new ProofWithAssumptions(resAssumptions, resAlpha, resToBeProofed, resSteps);
    }

    private static List<String> tokenizeFirstLine(String line) {
        int brackets = 0;
        int afterLastComma = 0;
        List<String> res = new ArrayList<>();
        for (int i = 0; i < line.length(); ++i) {
            if (line.charAt(i) == '(') {
                brackets += 1;
            }
            if (line.charAt(i) == ')') {
                brackets += 1;
            }
            if (line.charAt(i) == ',' && brackets == 0) {
                res.add(line.substring(afterLastComma, i));
                afterLastComma = i + 1;
            }
        }
        res.add(line.substring(afterLastComma, line.indexOf("|-")));
        res.add(line.substring(line.indexOf("|-") + 2, line.length()));
        return res;
    }

    public List<String> useDeductionConvertion(List<String> source) {
        ExpressionParser parser = new ExpressionParser();
        List<String> lineParts = tokenizeFirstLine(source.get(0));
        List<Expression> exprAssums = new ArrayList<Expression>();
        assumptionsFreeVariables = new ArrayList<>(lineParts.size() - 1);
        for (int i = 0; i < lineParts.size() - 2; i++) {
            Expression expr = parser.parseExpression(lineParts.get(i));
            assumptionsFreeVariables.add(expr.getFreeVariables());
            exprAssums.add(expr);
        }
        Expression alphaAssum = parser.parseExpression(lineParts.get(lineParts.size() - 2));
        assumptionsFreeVariables.add(alphaAssum.getFreeVariables());
        Expression toBeProofed = parser.parseExpression(lineParts.get(lineParts.size() - 1));
        List<Expression> steps = new ArrayList<Expression>();
        for (int i = 1; i < source.size(); i++) {
            steps.add(parser.parseExpression(source.get(i)));
        }
        ProofWithAssumptions newProof = useDeductionConvertion(new ProofWithAssumptions(exprAssums, alphaAssum, toBeProofed, steps));
        List<String> result = new ArrayList<String>();
        if (newProof.assumptions.size() > 0) {
            StringBuilder firstLine = new StringBuilder();
            for (Expression ass : newProof.assumptions) {
                firstLine.append(ass + ",");
            }
            result.add(firstLine.substring(0, firstLine.length() - 1) + "|-" + newProof.toBeProofed);
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
