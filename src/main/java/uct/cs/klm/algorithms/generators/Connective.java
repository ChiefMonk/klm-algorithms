package uct.cs.klm.algorithms.generators;

/**
 *
 * @author Chipo Hamayobe
 */
/**
 * The Connective class is responsible for managing the logical connective symbols.
 * It provides default symbols and allows customization of the symbols.
 */
public class Connective {
    private static Connective instance;
    private String defeasibleImplicationSymbol;
    private String conjunctionSymbol;
    private String disjunctionSymbol;
    private String implicationSymbol;
    private String biImplicationSymbol;
    private String negationSymbol;

    /**
     * Constructs a Connective object with default logical symbols.
     * The default symbols are:
     * - Defeasible Implication: "~>"
     * - Conjunction: "&" (logical AND)
     * - Disjunction: "||" (logical OR)
     * - Implication: "=>" (logical implication)
     * - Bi-Implication: "<=>" (logical bi-implication)
     * - Negation: "!" (logical NOT)
     */
    public Connective() {
        // Default symbols
        defeasibleImplicationSymbol = "~>";
        conjunctionSymbol = "&"; 
        disjunctionSymbol = "||";
        implicationSymbol = "=>";
        biImplicationSymbol = "<=>";
        negationSymbol = "!"; 
    }

    /**
     * Gets an instance of the Connective class.
     *
     * @return The Connective instance.
     */
    public static Connective getInstance() {
        if (instance == null) {
            instance = new Connective();
        }
        return instance;
    }

    /**
     * Resets all logical symbols to their default values.
     */
    public void reset() {
        defeasibleImplicationSymbol = "~>";
        conjunctionSymbol = "&";
        disjunctionSymbol = "||";
        implicationSymbol = "=>";
        biImplicationSymbol = "<=>";
        negationSymbol = "!";
    }

    /**
     * Gets a random logical connective symbol from the provided options.
     *
     * @param conArr An array representing available connective types.
     * @param con    The Connective object to retrieve symbols from.
     * @return A randomly selected connective symbol.
     */
    public static String getRandom(int[] conArr, Connective con) {
        String connective = "";
        int type = (int) (Math.random() * conArr.length);
        switch (type) {
            case 0:
                connective = con.getConjunctionSymbol();
                break;
            case 1:
                connective = con.getDisjunctionSymbol();
                break;
            case 2:
                connective = con.getImplicationSymbol();
                break;
            case 3:
                connective = con.getBiImplicationSymbol();
                break;
        }
        return connective;
    }

    /**
     * Allows the user to set a custom symbol for defeasible implication.
     *
     * @param symbol The custom symbol to set.
     */
    public void setDISymbol(String symbol) {
        defeasibleImplicationSymbol = symbol;
    }

    /**
     * Allows the user to set a custom symbol for the conjunction connective.
     *
     * @param symbol The custom symbol to set.
     */
    public void setConjunctionSymbol(String symbol) {
        conjunctionSymbol = symbol;
    }

    /**
     * Allows the user to set a custom symbol for the disjunction connective.
     *
     * @param symbol The custom symbol to set.
     */
    public void setDisjunctionSymbol(String symbol) {
        disjunctionSymbol = symbol;
    }

    /**
     * Allows the user to set a custom symbol for the implication connective.
     *
     * @param symbol The custom symbol to set.
     */
    public void setImplicationSymbol(String symbol) {
        implicationSymbol = symbol;
    }

    /**
     * Allows the user to set a custom symbol for the bi-implication connective.
     *
     * @param symbol The custom symbol to set.
     */
    public void setBiImplicationSymbol(String symbol) {
        biImplicationSymbol = symbol;
    }

    /**
     * Allows the user to set a custom symbol for the negation connective.
     *
     * @param symbol The custom symbol to set.
     */
    public void setNegationSymbol(String symbol) {
        negationSymbol = symbol;
    }

    /**
     * Gets the current symbol for defeasible implication.
     *
     * @return The defeasible implication symbol.
     */
    public String getDISymbol() {
        return defeasibleImplicationSymbol;
    }

    /**
     * Gets the current symbol for conjunction.
     *
     * @return The conjunction symbol.
     */
    public String getConjunctionSymbol() {
        return conjunctionSymbol;
    }

    /**
     * Gets the current symbol for disjunction.
     *
     * @return The disjunction symbol.
     */
    public String getDisjunctionSymbol() {
        return disjunctionSymbol;
    }

    /**
     * Gets the current symbol for implication.
     *
     * @return The implication symbol.
     */
    public String getImplicationSymbol() {
        return implicationSymbol;
    }

    /**
     * Gets the current symbol for bi-implication.
     *
     * @return The bi-implication symbol.
     */
    public String getBiImplicationSymbol() {
        return biImplicationSymbol;
    }

    /**
     * Gets the current symbol for negation.
     *
     * @return The negation symbol.
     */
    public String getNegationSymbol() {
        return negationSymbol;
    }
}