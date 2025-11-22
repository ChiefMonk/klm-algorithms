package uct.cs.klm.algorithms.generators;

/**
 *  The Atom class represents an atomic proposition and encapsulates the properties and behavior associated with individual atoms.
 * 
 * @author Chipo Hamayobe (chipo@cs.uct.ac.za)
 * @version 1.0.1
 * @since 2024-01-01
 */
public class Atom {
    private static Connective con = Connective.getInstance();
    private String atom;

    /**
     * Constructs a new Atom object with no initial string value.
     */
    public Atom() {
        this.atom = null;
    }

    /**
     * Constructs a new Atom object that is a copy of the provided Atom object.
     *
     * @param x The Atom object to copy.
     */
    public Atom(Atom x) {
        this.atom = new String(x.atom);
    }

    /**
     * Sets the atom to the specified string.
     *
     * @param string The string to set as the atom.
     */
    public void setAtom(String string) {
        atom = string;
    }

    /**
     * Negates the atom by adding a negation symbol if it doesn't already have one,
     * or removing it if it does.
     */
    public void negateAtom() {
        if (atom.startsWith(con.getNegationSymbol())) {
            atom = atom.substring(1);
        } else {
            atom = con.getNegationSymbol() + atom;
        }
    }

    /**
     * Returns a string representation of the atom.
     *
     * @return The string representation of the atom.
     */
    @Override
    public String toString() {
        return atom;
    }
}
