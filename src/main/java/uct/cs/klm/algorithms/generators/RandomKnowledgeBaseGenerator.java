package uct.cs.klm.algorithms.generators;

import org.tweetyproject.logics.pl.reasoner.SatReasoner;
import org.tweetyproject.logics.pl.sat.Sat4jSolver;
import org.tweetyproject.logics.pl.sat.SatSolver;
import org.tweetyproject.logics.pl.syntax.Conjunction;
import org.tweetyproject.logics.pl.syntax.Disjunction;
import org.tweetyproject.logics.pl.syntax.Implication;
import org.tweetyproject.logics.pl.syntax.Negation;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.logics.pl.syntax.Proposition;

import uct.cs.klm.algorithms.models.KnowledgeBase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import uct.cs.klm.algorithms.utils.ReasonerUtils;

/**
 * Utility class for generating random consistent defeasible knowledge bases for testing and benchmarking.
 */
public class RandomKnowledgeBaseGenerator {
    
    private final Random random;
    private final List<Proposition> propositions;
    private final SatReasoner reasoner;
    
    /**
     * Creates a new generator with a set of propositions using the given symbols.
     * 
     * @param propositionSymbols The symbols to use for propositions (e.g., "abcdef")
     */
    public RandomKnowledgeBaseGenerator(String propositionSymbols) {
        this.random = new Random();
        this.propositions = new ArrayList<>();
        
        for (char c : propositionSymbols.toCharArray()) {
            propositions.add(new Proposition(String.valueOf(c)));
        }
        
        // Initialize reasoner for consistency checking
        SatSolver.setDefaultSolver(new Sat4jSolver());
        this.reasoner = new SatReasoner();
    }
    
    /**
     * Creates a new generator with a set of propositions using the given symbols and a specific seed.
     * 
     * @param propositionSymbols The symbols to use for propositions (e.g., "abcdef")
     * @param seed The random seed for reproducible generation
     */
    public RandomKnowledgeBaseGenerator(String propositionSymbols, long seed) {
        this.random = new Random(seed);
        this.propositions = new ArrayList<>();
        
        for (char c : propositionSymbols.toCharArray()) {
            propositions.add(new Proposition(String.valueOf(c)));
        }
        
        // Initialize reasoner for consistency checking
        SatSolver.setDefaultSolver(new Sat4jSolver());
        this.reasoner = new SatReasoner();
    }
    
    /**
     * Generates a random knowledge base with the specified number of defeasible and classical formulas.
     * Ensures that the classical part is consistent.
     * 
     * @param defeasibleCount Number of defeasible formulas to generate
     * @param classicalCount Number of classical formulas to generate
     * @param maxFormulaDepth Maximum depth of generated formulas (complexity)
     * @param maxAttempts Maximum number of attempts to add a formula while maintaining consistency
     * @return A knowledge base with random consistent formulas
     */
    public KnowledgeBase generateKnowledgeBase(
            int defeasibleCount, 
            int classicalCount, 
            int maxFormulaDepth, 
            int maxAttempts) {
        
        KnowledgeBase kb = new KnowledgeBase();
        Set<PlFormula> classicalFormulas = new HashSet<>();
        
        // Generate classical formulas first, ensuring consistency
        int attempts = 0;
        while (classicalFormulas.size() < classicalCount && attempts < maxAttempts) {
            PlFormula formula = generateRandomFormula(1 + random.nextInt(maxFormulaDepth));
            
            // Check if adding this formula maintains consistency
            Set<PlFormula> testSet = new HashSet<>(classicalFormulas);
            testSet.add(formula);
            
            if (isConsistent(testSet)) {
                classicalFormulas.add(formula);
                kb.add(formula);
            }
            
            attempts++;
        }
        
        // Generate defeasible formulas (implications)
        for (int i = 0; i < defeasibleCount; i++) {
            PlFormula antecedent = generateRandomFormula(1 + random.nextInt(maxFormulaDepth));
            PlFormula consequent = generateRandomFormula(1 + random.nextInt(maxFormulaDepth));
            Implication implication = new Implication(antecedent, consequent);
            kb.add(ReasonerUtils.toDematerialisedFormula(implication));
        }
        
        return kb;
    }
    
    /**
     * Checks if a set of formulas is consistent.
     * 
     * @param formulas The set of formulas to check
     * @return true if the formulas are consistent, false otherwise
     */
    private boolean isConsistent(Set<PlFormula> formulas) {
        if (formulas.isEmpty()) {
            return true;
        }
        
        // Create a conjunction of all formulas
        Conjunction conjunction = new Conjunction(new ArrayList<>(formulas));
        
        // A set of formulas is consistent if and only if its negation is not a tautology
        Negation negation = new Negation(conjunction);
                
        // Use the reasoner to check if the negation is not a tautology
        return !reasoner.query(new KnowledgeBase(formulas), negation);
    }
    
    /**
     * Recursively generates a random formula with the specified maximum depth.
     * 
     * @param maxDepth Maximum depth of the formula tree
     * @return A randomly generated propositional formula
     */
    private PlFormula generateRandomFormula(int maxDepth) {
        if (maxDepth <= 1 || random.nextDouble() < 0.3) {
            // Generate a basic proposition or its negation
            Proposition prop = propositions.get(random.nextInt(propositions.size()));
            return random.nextBoolean() ? prop : new Negation(prop);
        }
        
        // Choose a random logical operator
        int operatorChoice = random.nextInt(3);
        
        switch (operatorChoice) {
            case 0: // Negation
                return new Negation(generateRandomFormula(maxDepth - 1));
                
            case 1: // Conjunction
                int numConjuncts = 2 + random.nextInt(2); // 2-3 conjuncts
                List<PlFormula> conjuncts = new ArrayList<>();
                for (int i = 0; i < numConjuncts; i++) {
                    conjuncts.add(generateRandomFormula(maxDepth - 1));
                }
                return new Conjunction(conjuncts);
                
            case 2: // Disjunction
                int numDisjuncts = 2 + random.nextInt(2); // 2-3 disjuncts
                List<PlFormula> disjuncts = new ArrayList<>();
                for (int i = 0; i < numDisjuncts; i++) {
                    disjuncts.add(generateRandomFormula(maxDepth - 1));
                }
                return new Disjunction(disjuncts);
                
            default:
                throw new IllegalStateException("Unexpected operator choice");
        }
    }
}