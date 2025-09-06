package uct.cs.klm.algorithms.explanation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import org.tweetyproject.logics.pl.reasoner.SatReasoner;
import org.tweetyproject.logics.pl.sat.Sat4jSolver;
import org.tweetyproject.logics.pl.sat.SatSolver;
import org.tweetyproject.logics.pl.syntax.PlBeliefSet;
import org.tweetyproject.logics.pl.syntax.Proposition;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import uct.cs.klm.algorithms.enums.ReasonerType;

import uct.cs.klm.algorithms.models.KnowledgeBase;
import uct.cs.klm.algorithms.models.ModelHittingSetTree;
import uct.cs.klm.algorithms.models.ModelNode;
import uct.cs.klm.algorithms.ranking.ModelRank;
import uct.cs.klm.algorithms.utils.ReasonerUtils;

/**
 *
 * @author Chipo Hamayobe
 */
public abstract class JustificationServiceBase {

    protected final SatReasoner _reasoner;

    public JustificationServiceBase() {
        SatSolver.setDefaultSolver(new Sat4jSolver());
        _reasoner = new SatReasoner();
    }

    protected ArrayList<KnowledgeBase> computeAllJustifications(
            ModelRank infinityRank,
            ReasonerType reasonerType,
            KnowledgeBase remainingKnowledgeBase,
            PlFormula queryFormula,
            boolean convertDefeasible) {

        System.out.println();
        System.out.println(String.format("%s Justifications", reasonerType));
        System.out.println(String.format("Query: %s", queryFormula));

        if (remainingKnowledgeBase == null || remainingKnowledgeBase.isEmpty()) {
            System.out.println("KB = {}");
            System.out.println("J = {}");

            return new ArrayList<>();
        }

        System.out.println(String.format("KB = %s", remainingKnowledgeBase));

        remainingKnowledgeBase = ReasonerUtils.toMaterialisedKnowledgeBase(remainingKnowledgeBase);
        queryFormula = ReasonerUtils.toMaterialisedFormula(queryFormula);

        // Construct root node
        KnowledgeBase rootJustification = computeSingleJustification(remainingKnowledgeBase, queryFormula);

        ModelNode rootNode = new ModelNode(remainingKnowledgeBase, rootJustification);

        // Create a queue to keep track of nodes
        Queue<ModelNode> queue = new LinkedList<>();
        queue.add(rootNode);

        ModelHittingSetTree tree = new ModelHittingSetTree(rootNode);

        while (!queue.isEmpty()) {

            ModelNode node = queue.poll();

            for (PlFormula formula : node.getJustification()) {
                KnowledgeBase childKnowledgeBase = ReasonerUtils.removeFormula(node.getKnowledgeBase(), formula);
                KnowledgeBase childJustification = computeSingleJustification(childKnowledgeBase, queryFormula);
                ModelNode childNode = new ModelNode(childKnowledgeBase, childJustification);

                node.addChildNode(formula, childNode);
                tree.addNode(childNode);

                if (childJustification != null && childJustification.isEmpty()) {
                    queue.add(childNode);
                }
            }
        }

        ArrayList<KnowledgeBase> allJustifications = new ArrayList<>();
        var infinityRankKb = infinityRank.getFormulas();

        if (infinityRankKb.isEmpty()) {
            allJustifications = rootNode.getAllJustifications();
        } else {
            for (KnowledgeBase kb : rootNode.getAllJustifications()) {

                KnowledgeBase currentKb = new KnowledgeBase();
                for (PlFormula formula : kb) {
                    if (infinityRankKb.contains(formula)) {
                        currentKb.add(formula);
                    } else {
                        currentKb.add(ReasonerUtils.toDematerialisedFormula(formula));
                    }

                }

                allJustifications.add(currentKb);
            }
        }

        allJustifications.sort(Comparator.comparingInt(a -> a.size()));

        System.out.println(String.format("Number of Justifications = %s", allJustifications.size()));
        int counter = 1;
        for (KnowledgeBase kb : allJustifications) {
            System.out.println(String.format(" --> %s. J_%s = %s", counter, counter, kb));
            counter++;
        }

        return allJustifications;
    }

    private KnowledgeBase computeSingleJustification(
            KnowledgeBase entailmentKb,
            PlFormula query) {
        KnowledgeBase result = new KnowledgeBase();

        if (entailmentKb.contains(query)) {
            result.add(query);
            return result;
        }

        result = expandFormulas(entailmentKb, query, _reasoner);

        if (result.isEmpty()) {
            return result;
        }

        result = contractFormuls(result, query, _reasoner);

        return result;
    }

    private KnowledgeBase expandFormulas(
            KnowledgeBase knowledgeBase,
            PlFormula query,
            SatReasoner reasoner) {
        KnowledgeBase result = new KnowledgeBase();

        if (!reasoner.query(knowledgeBase, query)) {
            return new KnowledgeBase();
        }

        KnowledgeBase sPrime = new KnowledgeBase();
        List<Proposition> sigma = getSignature(query);

        while (result != sPrime) {
            sPrime = result;
            result = ReasonerUtils.toCombinedKnowledgeBases(result, findRelatedFormulas(sigma, knowledgeBase));
            PlBeliefSet resultKownledgeBase = new PlBeliefSet(result);

            if (reasoner.query(resultKownledgeBase, query)) {
                return result;
            }

            sigma = getSignature(result);
        }

        return result;
    }

    private KnowledgeBase findRelatedFormulas(
            List<Proposition> signatures,
            KnowledgeBase knowledgeBase) {
        KnowledgeBase result = new KnowledgeBase();

        for (PlFormula formula : knowledgeBase) {
            if (!Collections.disjoint(getSignature(formula), signatures)) {
                result.add(formula);
            }
        }

        return result;
    }

    private List<Proposition> getSignature(
            KnowledgeBase formulas) {
        List<Proposition> result = new ArrayList<>();

        for (PlFormula formula : formulas) {
            List<Proposition> signature = getSignature(formula);
            for (Proposition atom : signature) {
                if (!result.contains(atom)) {
                    result.add(atom);
                }
            }
        }
        return result;
    }

    private List<Proposition> getSignature(
            PlFormula query) {
        List<Proposition> result = new ArrayList<>();
        Set<Proposition> atoms = query.getAtoms();
        result.addAll(atoms);
        return result;
    }

    private KnowledgeBase contractFormuls(
            KnowledgeBase result,
            PlFormula query,
            SatReasoner reasoner) {
        return contractRecursive(new KnowledgeBase(), result, query, reasoner);
    }

    private KnowledgeBase contractRecursive(
            KnowledgeBase support,
            KnowledgeBase whole,
            PlFormula query,
            SatReasoner reasoner) {
        if (whole.size() == 1) {
            return whole;
        }

        List<KnowledgeBase> splitList = split(whole);
        KnowledgeBase left = splitList.get(0);
        KnowledgeBase right = splitList.get(1);

        KnowledgeBase leftUnion = ReasonerUtils.toCombinedKnowledgeBases(support, left);

        PlBeliefSet leftKB = new PlBeliefSet(leftUnion);

        KnowledgeBase rightUnion = ReasonerUtils.toCombinedKnowledgeBases(support, right);

        PlBeliefSet rightKB = new PlBeliefSet(rightUnion);

        if (reasoner.query(leftKB, query)) {
            return contractRecursive(support, left, query, reasoner);
        }
        if (reasoner.query(rightKB, query)) {
            return contractRecursive(support, right, query, reasoner);
        }

        KnowledgeBase leftPrime = contractRecursive(rightUnion, left, query, reasoner);
        KnowledgeBase leftPrimeUnion = ReasonerUtils.toCombinedKnowledgeBases(support, leftPrime);
        KnowledgeBase rightPrime = contractRecursive(leftPrimeUnion, right, query, reasoner);

        return ReasonerUtils.toCombinedKnowledgeBases(leftPrime, rightPrime);
    }

    private List<KnowledgeBase> split(KnowledgeBase whole) {
        List<PlFormula> kbList = new ArrayList<>();

        for (PlFormula formula : whole) {
            kbList.add(formula);
        }

        int length = kbList.size();
        int halfLength = length / 2;

        KnowledgeBase left = new KnowledgeBase(kbList.subList(0, halfLength));
        KnowledgeBase right = new KnowledgeBase(kbList.subList(halfLength, length));

        List<KnowledgeBase> result = new ArrayList<>();

        result.add(left);
        result.add(right);

        return result;
    }
}
