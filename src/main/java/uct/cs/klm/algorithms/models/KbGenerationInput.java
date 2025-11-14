package uct.cs.klm.algorithms.models;

import uct.cs.klm.algorithms.enums.Complexity;
import uct.cs.klm.algorithms.enums.Distribution;

/**
 * This class models an knowledge base generation input for a given query set.
 * 
 * @author Chipo Hamayobe (chipo@cs.uct.ac.za)
 * @version 1.0.1
 * @since 2024-01-01
 */
public class KbGenerationInput {
    private int numberOfRanks;
    private Distribution distributionType;
    private Complexity complexity;
    private int numberOfDefeasibleImplications;

    public KbGenerationInput() {}

    public KbGenerationInput(int numberOfRanks, Distribution distributionType, Complexity complexity, int numberOfDefeasibleImplications) {
        this.numberOfRanks = numberOfRanks;
        this.distributionType = distributionType;
        this.complexity = complexity;
        this.numberOfDefeasibleImplications = numberOfDefeasibleImplications;
    }

    public int getNumberOfRanks() {
        return numberOfRanks;
    }

    public void setNumberOfRanks(int numberOfRanks) {
        this.numberOfRanks = numberOfRanks;
    }

    public Distribution getDistributionType() {
        return distributionType;
    }

    public void setDistributionType(Distribution distributionType) {
        this.distributionType = distributionType;
    }

    public Complexity getComplexity() {
        return complexity;
    }

    public void setComplexity(Complexity complexity) {
        this.complexity = complexity;
    }

    public int getNumberOfDefeasibleImplications() {
        return numberOfDefeasibleImplications;
    }

    public void setNumberOfDefeasibleImplications(int numberOfDefeasibleImplications) {
        this.numberOfDefeasibleImplications = numberOfDefeasibleImplications;
    }
}

