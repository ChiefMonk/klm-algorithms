package uct.cs.klm.algorithms.generators;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * The AtomBuilder class provides functions for generating and keeping track of atoms in a knowledge base.
 */
public class AtomBuilder {
    private static AtomBuilder gen;
    private char startChar;
    private char endChar;
    private List<String> atomList = new CopyOnWriteArrayList<>();
    private Random random;
    private int length = 1;
    private int atomCount = 0;

    /**
     * Constructs an AtomBuilder with default settings.
     */
    public AtomBuilder() {
        atomList = new CopyOnWriteArrayList<>();
        random = new Random();
        startChar = '\u0041';
        endChar = '\u0058';
    }

    /**
     * Gets an instance of AtomBuilder.
     *
     * @return The AtomBuilder instance.
     */
    public static AtomBuilder getInstance() {
        if (gen == null) {
            gen = new AtomBuilder();
        }
        return gen;
    }

    /**
     * Resets the settings to default.
     * Clears the list of generated atoms, resets the atom length to 1, and resets the atom count.
     */
    public void reset() {
        atomList.clear();
        length = 1;
        atomCount = 0;
    }

    /**
     * Sets the character set to be used for atom generation.
     *
     * @param characterSet The character set to be used ("upperlatin", "lowerlatin", "greek", or "altlatin").
     */
    public void setCharacters(String characterSet) {
        switch (characterSet) {
            case "upperlatin":
                startChar = '\u0041';
                endChar = '\u0058';
                break;
            case "lowerlatin":
                startChar = '\u0061';
                endChar = '\u0078';
                break;
            case "greek":
                startChar = '\u03B1';
                endChar = '\u03C9';
                break;
            case "altlatin":
                startChar = '\u0250';
                endChar = '\u0267';
                break;
        }
    }

    /**
     * Generates a unique atom.
     *
     * @return An Atom object representing the generated atom.
     */
    public Atom generateAtom() {
        Atom atom = new Atom();
        StringBuilder sb = new StringBuilder();
        countChecker();

        do {
            sb.setLength(0);
            for (int i = 0; i < length; i++) {
                char randomChar = (char) (startChar + random.nextInt(endChar - startChar + 1));
                sb.append(randomChar);
            }
            atom.setAtom(sb.toString());
        } while (atomList.contains(atom.toString()));
        synchronized (atomList) {
            atomCount++;
            atomList.add(atom.toString());
        }

        return atom;
    }

    /**
     * Checks the atom count and updates the atom length if needed.
     */
    public void countChecker() {
        int len = length;
        int temp = 0;
        while (len != 0) {
            temp += Math.pow(24, len);
            len--;
        }
        if (atomCount == temp) {
            length++;
        }
    }
}
