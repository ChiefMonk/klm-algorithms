package uct.cs.klm.algorithms.generators;

/**
 * The DefImplication class represents a defeasible implication and provides functions to manipulate and access its components
 * 
 * @author Chipo Hamayobe (chipo@cs.uct.ac.za)
 * @version 1.0.1
 * @since 2024-01-01
 */
public class DefImplication {

    private static Connective con = Connective.getInstance();
    private String antecedent;
    private String consequent;

    /**
     * Constructs a DefImplication object with the given antecedent and consequent.
     *
     * @param ant The antecedent of the defeasible implication.
     * @param cons The consequent of the defeasible implication.
     */
    public DefImplication(String ant, String cons) {
        this.antecedent = ant;
        this.consequent = cons;
    }

    /**
     * Checks if an atom is included in the defeasible implication's consequent.
     *
     * @param defImp The defeasible implication to check.
     * @param atom   The atom to check for inclusion.
     * @return true if the atom is included in the consequent, false otherwise.
     */
    public static boolean checkConsequent(DefImplication defImp, Atom atom) {
        boolean val;
        String[] defImpSplit = defImp.toString().split(con.getDISymbol(), 2);
        if (defImpSplit[1].contains(atom.toString())) {
            val = true;
        } else {
            val = false;
        }
        return val;
    }

    /**
     * Checks if an atom is included in the defeasible implication's antecedent.
     *
     * @param defImp The defeasible implication to check.
     * @param atom   The atom to check for inclusion.
     * @return true if the atom is included in the antecedent, false otherwise.
     */
    public static boolean checkAntecedent(DefImplication defImp, Atom atom) {
        boolean val;
        String[] defImpSplit = defImp.toString().split(con.getDISymbol(), 2);
        if (defImpSplit[0].contains(atom.toString())) {
            val = true;
        } else {
            val = false;
        }
        return val;
    }

    /**
     * Sets the antecedent of a defeasible implication.
     *
     * @param ant An array of objects representing the antecedent.
     */
    public void setAntecedent(Object[] ant) {
        StringBuilder antecedentBuilder = new StringBuilder();
        for (int i = 0; i < ant.length; i++) {
            if (ant[i] != null) {
                antecedentBuilder.append(ant[i].toString());
            }
        }
        antecedent = antecedentBuilder.toString();
    }

    /**
     * Sets the consequent of a defeasible implication.
     *
     * @param con An array of objects representing the consequent.
     */
    public void setConsequent(Object[] con) {
        StringBuilder consequentBuilder = new StringBuilder();
        for (int i = 0; i < con.length; i++) {
            if (con[i] != null) {
                consequentBuilder.append(con[i].toString());
            }
        }
        consequent = consequentBuilder.toString();
    }

    /**
     * Gets the antecedent of the defeasible implication.
     *
     * @return The antecedent string.
     */
    public String getAntecedent() {
        return antecedent;
    }

    /**
     * Gets the consequent of the defeasible implication.
     *
     * @return The consequent string.
     */
    public String getConsequent() {
        return consequent;
    }

    /**
     * Returns a string representation of the defeasible implication.
     *
     * @return The string representation of the defeasible implication.
     */
    @Override
    public String toString() {
        return antecedent + con.getDISymbol() + consequent;
    }
}