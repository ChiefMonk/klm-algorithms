package uct.cs.klm.algorithms.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import uct.cs.klm.algorithms.ranking.ModelRankCollection;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ModelRelevantClosureEntailment extends ModelEntailment {
  private final ModelRankCollection weakenedRanking;

  private ModelRelevantClosureEntailment(ModelRelevantClosureEntailmentBuilder builder) {
    super(builder);
    this.weakenedRanking = builder.weakenedRanking;
  }

  public ModelRankCollection getWeakenedRanking() {
    return weakenedRanking;
  }

  public static class ModelRelevantClosureEntailmentBuilder extends ModelEntailment.EntailmentBuilder<ModelRelevantClosureEntailmentBuilder> {
    private ModelRankCollection weakenedRanking;

    public ModelRelevantClosureEntailmentBuilder withWeakenedRanking(ModelRankCollection weakenedRanking) {
      this.weakenedRanking = weakenedRanking;
      return this;
    }

    @Override
    protected ModelRelevantClosureEntailmentBuilder self() {
      return this;
    }

    @Override
    public ModelRelevantClosureEntailment build() {
      return new ModelRelevantClosureEntailment(this);
    }
  }
}

