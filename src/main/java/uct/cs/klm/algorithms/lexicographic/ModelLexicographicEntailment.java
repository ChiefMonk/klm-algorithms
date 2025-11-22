package uct.cs.klm.algorithms.lexicographic;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import uct.cs.klm.algorithms.models.ModelEntailment;
import uct.cs.klm.algorithms.models.ModelRankResponse;
import uct.cs.klm.algorithms.ranking.ModelRankCollection;

/**
 * This class represents a model lexicographic entailment for a given query.
 * 
 * @author Chipo Hamayobe (chipo@cs.uct.ac.za)
 * @version 1.0.1
 * @since 2024-01-01
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ModelLexicographicEntailment extends ModelEntailment {
  private final ModelRankCollection weakenedRanking;  
  private final ArrayList<ModelRankResponse> _powersetRanking;

  private ModelLexicographicEntailment(ModelLexicographicEntailmentBuilder builder) {
    super(builder);
    this.weakenedRanking = builder.weakenedRanking;
     this._powersetRanking = builder._powersetRanking;
  }

  public ModelRankCollection getWeakenedRanking() {
    return weakenedRanking;
  }
  
   public ArrayList<ModelRankResponse> getPowersetRanking() {
    return _powersetRanking;
  }

  public static class ModelLexicographicEntailmentBuilder extends EntailmentBuilder<ModelLexicographicEntailmentBuilder> {
    private ModelRankCollection weakenedRanking;
    private ArrayList<ModelRankResponse> _powersetRanking;

    public ModelLexicographicEntailmentBuilder withWeakenedRanking(ModelRankCollection weakenedRanking) {
      this.weakenedRanking = weakenedRanking;
      return this;
    }
    
     public ModelLexicographicEntailmentBuilder withPowersetRanking(ArrayList<ModelRankResponse> powersetRanking) {
      this._powersetRanking = powersetRanking;
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
