package uct.cs.klm.algorithms.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import uct.cs.klm.algorithms.ranking.ModelRankCollection;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LexicalEntailment extends Entailment {
  private final ModelRankCollection weakenedRanking;

  private LexicalEntailment(LexicalEntailmentBuilder builder) {
    super(builder);
    this.weakenedRanking = builder.weakenedRanking;
  }

  public ModelRankCollection getWeakenedRanking() {
    return weakenedRanking;
  }

  public static class LexicalEntailmentBuilder extends EntailmentBuilder<LexicalEntailmentBuilder> {
    private ModelRankCollection weakenedRanking;

    public LexicalEntailmentBuilder withWeakenedRanking(ModelRankCollection weakenedRanking) {
      this.weakenedRanking = weakenedRanking;
      return this;
    }

    @Override
    protected LexicalEntailmentBuilder self() {
      return this;
    }

    @Override
    public LexicalEntailment build() {
      return new LexicalEntailment(this);
    }
  }
}
