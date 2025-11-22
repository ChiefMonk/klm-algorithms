package uct.cs.klm.algorithms.generators;

import java.util.ArrayList;
import java.util.Collections;

/**
 * The DefImplicationBuilder class provides methods for generating defeasible implications (DIs)
 * and their associated structures within a knowledge base.
 * 
 * @author Chipo Hamayobe (chipo@cs.uct.ac.za)
 * @version 1.0.1
 * @since 2024-01-01
 */
public class DefImplicationBuilder{

    private static Connective con = Connective.getInstance();

    /**
     * Generates the minimum DIs and structure needed for a rank.
     *
     * @param DIs          The list of defeasible implications in the KB.
     * @param rankBaseCons The consequent of the rank.
     * @param rankBaseAnt  The antecedent of the rank.
     */
    public static void rankZero(ArrayList<DefImplication> DIs, Atom rankBaseCons, Atom rankBaseAnt){
        DIs.add(new DefImplication(rankBaseAnt.toString(), new Atom(rankBaseCons).toString()));
    }

    /**
     * Generates baseline DIs and structure needed for a rank in a knowledge base.
     *
     * @param gen          An AtomBuilder instance for generating atoms.
     * @param DIs          The list of defeasible implications in the KB.
     * @param rankBaseCons The consequent of the rank.
     * @param rankBaseAnt  The antecedent of the rank.
     */
    public static void rankBuilderConstricted(AtomBuilder gen, ArrayList<DefImplication> DIs, Atom rankBaseCons, Atom rankBaseAnt){
        Atom atom = gen.generateAtom();
        DIs.add(new DefImplication(atom.toString(), new Atom(rankBaseCons).toString())); // 
        DIs.add(new DefImplication(atom.toString(), rankBaseAnt.toString())); 
        rankBaseAnt.setAtom(atom.toString());
    }

    /**
     * Generates baseline DIs and structure with a new consequent for the next rank in a knowledge base.
     *
     * @param gen          An AtomBuilder instance for generating atoms.
     * @param DIs          The list of defeasible implications in the KB.
     * @param rankBaseCons The consequent of the current rank.
     * @param rankBaseAnt  The antecedent of the current rank.
     */
    public static void rankBuilder(AtomBuilder gen, ArrayList<DefImplication> DIs, Atom rankBaseCons, Atom rankBaseAnt){
        Atom newRankBaseCons = gen.generateAtom(); // Atom acts as the rankBaseCons in the next rank.
        Atom atom = gen.generateAtom();
        DIs.add(new DefImplication(atom.toString(), new Atom(rankBaseCons).toString())); // 
        DIs.add(new DefImplication(atom.toString(), new Atom(newRankBaseCons).toString()));
        DIs.add(new DefImplication(atom.toString(), rankBaseAnt.toString()));
        rankBaseCons.setAtom(newRankBaseCons.toString());
        rankBaseAnt.setAtom(atom.toString());
    }

    /**
     * Determines the type of simple DI to generate and generates it.
     *
     * @param decision       The decision for DI generation type.
     * @param gen            An AtomBuilder instance for generating atoms.
     * @param DIs            The list of defeasible implications in the KB.
     * @param anyRankAtoms   The list of atoms usable in any rank.
     * @param curRankAtoms   The list of atoms usable in the current rank.
     * @param anyRankAtomsTemp The temporary list of atoms usable in any rank.
     */
    public static void simpleDI(int decision, AtomBuilder gen, ArrayList<DefImplication> DIs, ArrayList<Atom> anyRankAtoms, ArrayList<Atom> curRankAtoms, ArrayList<Atom> anyRankAtomsTemp){
        int i = (int)(Math.random() * curRankAtoms.size());
        switch(decision){
            case 0: // Adds defImplication with a new atom as antecedent and random curRankAtom as consequent.
                Atom[] temp = recycleAtom(gen, DIs, curRankAtoms.get(i));
                curRankAtoms.add(temp[0]);
                break;
            case 1: // Adds defImplication with negated atom as antecedent and random curRankAtom as consequent.
                temp = negateAntecedent(gen, DIs, curRankAtoms.get(i));
                anyRankAtomsTemp.add(temp[0]);
                break;
            case 2: // Reuses an antecedent from a previous rank as consequent in a new rank. 
                if(anyRankAtoms.size()==0){
                    temp = recycleAtom(gen, DIs, curRankAtoms.get(i));
                    curRankAtoms.add(temp[0]);
                }
                else{
                    int j = (int)(Math.random() * anyRankAtoms.size()); // Get random atom from atoms usable in any rank.
                    reuseConsequent(gen, DIs, anyRankAtoms.get(j), curRankAtoms.get(i));
                    anyRankAtomsTemp.add(anyRankAtoms.get(j));
                    anyRankAtoms.remove(j);
                }
                break;
        }
    }

    /**
     * Method to generate simple DIs by recycling an atom as antecedent and reusing the current rank atom as consequent.
     *
     * @param gen       An AtomBuilder instance for generating atoms.
     * @param DIs       The list of defeasible implications in the KB.
     * @param rankBaseAnt The current rank's antecedent.
     * @return An array containing the generated atom.
     */
    public static Atom[] recycleAtom(AtomBuilder gen, ArrayList<DefImplication> DIs, Atom rankBaseAnt){
        Atom atom = gen.generateAtom();
        Atom[] atoms = {atom};
        DIs.add(new DefImplication(atom.toString(), rankBaseAnt.toString())); 
        return atoms;
    }

    /**
     * Method to generate simple DIs by using a new negated atom as antecedent and a current rank atom as consequent.
     *
     * @param gen          An AtomBuilder instance for generating atoms.
     * @param DIs          The list of defeasible implications in the KB.
     * @param currRankAtom The current rank's atom.
     * @return An array containing the generated atom.
     */
    public static Atom[] negateAntecedent(AtomBuilder gen, ArrayList<DefImplication> DIs, Atom currRankAtom){ 
        Atom atom = gen.generateAtom();
        atom.negateAtom();
        Atom[] atoms = {atom};
        DIs.add(new DefImplication(new Atom(atom).toString(), currRankAtom.toString())); 
        return atoms;
    }

    /**
     * Method to generate simple DIs by using a current rank atom as antecedent and a negated any rank atom as consequent.
     *
     * @param gen         An AtomBuilder instance for generating atoms.
     * @param DIs         The list of defeasible implications in the KB.
     * @param anyRankAtom The any rank atom to be negated.
     */
    public static void reuseConsequent(AtomBuilder gen, ArrayList<DefImplication> DIs, Atom anyRankAtom, Atom currRankAtom){
        anyRankAtom.negateAtom();
        DIs.add(new DefImplication(currRankAtom.toString(), new Atom(anyRankAtom).toString()));
    }
    
    /**
     * Determines the type of complex DI to generate and generates it based on the provided key.
     *
     * @param key          The key specifying the type and complexity of the complex DI.
     * @param gen          An AtomBuilder instance for generating atoms.
     * @param DIs          The list of defeasible implications in the KB.
     * @param curRankAtoms The list of atoms usable in the current rank.
     */
    public static void complexDI(String key, AtomBuilder gen, ArrayList<DefImplication> DIs, ArrayList<Atom> curRankAtoms){
        int s = Integer.parseInt(key.substring(0, 1));
        switch(s){
            case 1:
                disjunctionDI(key, gen, DIs, curRankAtoms);
                break;
            case 2:
                conjunctionDI(key, gen, DIs, curRankAtoms);
                break;
            case 3:
                implicationDI(key, gen, DIs, curRankAtoms);
                break;
            case 4:
                biImplicationDI(key, gen, DIs, curRankAtoms);
                break;
            case 5:
                mixedDI(key, gen, DIs, curRankAtoms);
                break;
        }
    }

    /**
     * Method to generate complex DIs using the disjunction connective.
     *
     * @param key          The key specifying the type and complexity of the complex DI.
     * @param gen          An AtomBuilder instance for generating atoms.
     * @param DIs          The list of defeasible implications in the KB.
     * @param curRankAtoms The list of atoms usable in the current rank.
     */
    public static void disjunctionDI(String key, AtomBuilder gen, ArrayList<DefImplication> DIs, ArrayList<Atom> curRankAtoms){
        Collections.shuffle(curRankAtoms);
        String disjunction = con.getDisjunctionSymbol();
        String antecedent = "";
        String consequent = "";
        int complexityAnt = Integer.parseInt(key.substring(2, 3));
        int complexityCon = Integer.parseInt(key.substring(4, 5));
        Atom a = gen.generateAtom();
        curRankAtoms.add(a); 
        switch(complexityAnt){
            case 0:
                antecedent = a.toString();
                break;
            case 1:
                Atom b = gen.generateAtom();
                curRankAtoms.add(b); 
                antecedent = a.toString() + disjunction + b.toString();
                break;
            case 2:
                b = gen.generateAtom();
                Atom c = gen.generateAtom();
                curRankAtoms.add(b);
                curRankAtoms.add(c);
                antecedent = a.toString() + disjunction + b.toString() + disjunction + c.toString();
                break;
        }
        switch(complexityCon){
            case 0:
                consequent = curRankAtoms.get(0).toString();
                break;
            case 1:
                consequent = curRankAtoms.get(0).toString() + disjunction + curRankAtoms.get(1).toString();
                break;
            case 2:
                consequent = curRankAtoms.get(0).toString() + disjunction + curRankAtoms.get(1).toString() + disjunction + curRankAtoms.get(2).toString();
                break;
        }
        DIs.add(new DefImplication(antecedent, consequent));
    }

    /**
     * Method to generate complex DIs using the conjunction connective.
     *
     * @param key          The key specifying the type and complexity of the complex DI.
     * @param gen          An AtomBuilder instance for generating atoms.
     * @param DIs          The list of defeasible implications in the KB.
     * @param curRankAtoms The list of atoms usable in the current rank.
     */
    public static void conjunctionDI(String key, AtomBuilder gen, ArrayList<DefImplication> DIs, ArrayList<Atom> curRankAtoms){
        Collections.shuffle(curRankAtoms);
        String conjunction = con.getConjunctionSymbol();
        String antecedent = "";
        String consequent = "";
        int complexityAnt = Integer.parseInt(key.substring(2, 3));
        int complexityCon = Integer.parseInt(key.substring(4, 5));
        switch(complexityAnt){
            case 0:
                Atom a = gen.generateAtom();
                antecedent = a.toString();
                curRankAtoms.add(a);
                break;
            case 1:
                antecedent = gen.generateAtom().toString() + conjunction + gen.generateAtom().toString();
                break;
            case 2:
                antecedent = gen.generateAtom().toString() + conjunction + gen.generateAtom().toString() + conjunction + gen.generateAtom().toString();
                break;
        }
        switch(complexityCon){
            case 0:
                consequent = curRankAtoms.get(0).toString();
                break;
            case 1:
                consequent = curRankAtoms.get(0).toString() + conjunction + gen.generateAtom().toString();
                break;
            case 2:
                consequent = gen.generateAtom().toString() + conjunction + curRankAtoms.get(0).toString() + conjunction + gen.generateAtom().toString();
                break;
        }
        DIs.add(new DefImplication(antecedent, consequent));
    }

    /**
     * Method to generate complex DIs using the implication connective.
     *
     * @param key          The key specifying the type and complexity of the complex DI.
     * @param gen          An AtomBuilder instance for generating atoms.
     * @param DIs          The list of defeasible implications in the KB.
     * @param curRankAtoms The list of atoms usable in the current rank.
     */
    public static void implicationDI(String key, AtomBuilder gen, ArrayList<DefImplication> DIs, ArrayList<Atom> curRankAtoms){
        Collections.shuffle(curRankAtoms);
        String implication = con.getImplicationSymbol();
        String antecedent = "";
        String consequent = "";
        int complexityAnt = Integer.parseInt(key.substring(2, 3));
        int complexityCon = Integer.parseInt(key.substring(4, 5));
        Atom a = gen.generateAtom();
        Atom b = gen.generateAtom();
        switch(complexityAnt){
            case 0:
                antecedent = curRankAtoms.get(0).toString();
                break;
            case 1:
                antecedent = a.toString() + implication + b.toString();
                break;
            case 2:
                Atom c = gen.generateAtom();
                antecedent = a.toString() + implication + b.toString() + implication + c.toString();
                break;
        }
        switch(complexityCon){
            case 0:
                consequent = curRankAtoms.get(0).toString();
                break;
            case 1:
                consequent = a.toString() + implication + b.toString();
                break;
            case 2:
                Atom c = gen.generateAtom();
                consequent = a.toString() + implication + b.toString() + implication + c.toString();
                break;
        }
        DIs.add(new DefImplication(antecedent, consequent));
    }

    /**
     * Method to generate complex DIs using the bi-implication connective.
     *
     * @param key          The key specifying the type and complexity of the complex DI.
     * @param gen          An AtomBuilder instance for generating atoms.
     * @param DIs          The list of defeasible implications in the KB.
     * @param curRankAtoms The list of atoms usable in the current rank.
     */
    public static void biImplicationDI(String key, AtomBuilder gen, ArrayList<DefImplication> DIs, ArrayList<Atom> curRankAtoms){
        Collections.shuffle(curRankAtoms);
        String biimplication = con.getBiImplicationSymbol();
        String antecedent = "";
        String consequent = "";
        int complexityAnt = Integer.parseInt(key.substring(2, 3));
        int complexityCon = Integer.parseInt(key.substring(4, 5));
        Atom a = gen.generateAtom();
        Atom b = gen.generateAtom();
        switch(complexityAnt){
            case 0:
                antecedent = curRankAtoms.get(0).toString();
                break;
            case 1:
                antecedent = a.toString() + biimplication + b.toString();
                break;
            case 2:
                Atom c = gen.generateAtom();
                antecedent = a.toString() + biimplication + b.toString() + biimplication + c.toString();
                break;
        }
        switch(complexityCon){
            case 0:
                consequent = curRankAtoms.get(0).toString();
                break;
            case 1:
                consequent = a.toString() + biimplication + b.toString();
                break;
            case 2:
                Atom c = gen.generateAtom();
                consequent = a.toString() + biimplication + b.toString() + biimplication + c.toString();
                break;
        }
        DIs.add(new DefImplication(antecedent, consequent));
    }

    /**
     * Method to generate complex DIs using a mixture of connectives.
     *
     * @param key          The key specifying the type and complexity of the complex DI.
     * @param gen          An AtomBuilder instance for generating atoms.
     * @param DIs          The list of defeasible implications in the KB.
     * @param curRankAtoms The list of atoms usable in the current rank.
     */
    public static void mixedDI(String key, AtomBuilder gen, ArrayList<DefImplication> DIs, ArrayList<Atom> curRankAtoms){
        Collections.shuffle(curRankAtoms);
        int[] connective = {0,1,2,3};
        String antecedent = "";
        String consequent = "";
        int complexityAnt = Integer.parseInt(key.substring(2, 3));
        int complexityCon = Integer.parseInt(key.substring(4, 5));
        switch(complexityAnt){
            case 0:
                antecedent = curRankAtoms.get(0).toString();
                break;
            case 1:
                int[] connective1 = {0,1};
                antecedent = curRankAtoms.get(0).toString() + Connective.getRandom(connective1, con) + curRankAtoms.get(1).toString();
                break;
            case 2:
                antecedent = curRankAtoms.get(0).toString() + con.getConjunctionSymbol() + "(" + curRankAtoms.get(1).toString() + Connective.getRandom(connective, con) + gen.generateAtom().toString() + ")";
                break;
        }
        switch(complexityCon){
            case 0:
                consequent = gen.generateAtom().toString();
                break;
            case 1:
                consequent = gen.generateAtom().toString() + Connective.getRandom(connective, con) + gen.generateAtom().toString();
                break;
            case 2:
                consequent = gen.generateAtom().toString() + Connective.getRandom(connective, con) + gen.generateAtom().toString() + Connective.getRandom(connective, con) + gen.generateAtom().toString();
                break;
        }
        DIs.add(new DefImplication(antecedent, consequent));
    }
}