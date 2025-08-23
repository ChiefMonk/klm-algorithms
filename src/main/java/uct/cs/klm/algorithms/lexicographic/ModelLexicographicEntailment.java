package uct.cs.klm.algorithms.lexicographic;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import uct.cs.klm.algorithms.models.ModelEntailment;
import uct.cs.klm.algorithms.ranking.ModelRankCollection;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ModelLexicographicEntailment extends ModelEntailment {
  private final ModelRankCollection weakenedRanking;

  private ModelLexicographicEntailment(ModelLexicographicEntailmentBuilder builder) {
    super(builder);
    this.weakenedRanking = builder.weakenedRanking;
  }

  public ModelRankCollection getWeakenedRanking() {
    return weakenedRanking;
  }

  public static class ModelLexicographicEntailmentBuilder extends EntailmentBuilder<ModelLexicographicEntailmentBuilder> {
    private ModelRankCollection weakenedRanking;

    public ModelLexicographicEntailmentBuilder withWeakenedRanking(ModelRankCollection weakenedRanking) {
      this.weakenedRanking = weakenedRanking;
      return this;
    }

    @Override
    protected ModelLexicographicEntailmentBuilder self() {
      return this;
    }

    @Override
    public ModelLexicographicEntailment build() {
      return new ModelLexicographicEntailment(this);
    }
  }
}
