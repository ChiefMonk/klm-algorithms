package uct.cs.klm.algorithms.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import uct.cs.klm.algorithms.ranking.ModelRankCollection;

/**
 * This class represents a model relevant closure entailment for a given query.
 * 
 * @author Chipo Hamayobe (chipo@cs.uct.ac.za)
 * @version 1.0.1
 * @since 2024-01-01
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ModelRelevantClosureEntailment extends ModelEntailment {
      
    private ModelRankCollection _irrelevantRanking;
    
    public ModelRelevantClosureEntailment() {
        super();
    }

    private ModelRelevantClosureEntailment(ModelRelevantClosureEntailmentBuilder builder) {
      super(builder);
      
      this._relevantRanking = builder._relevantRanking;
      this._irrelevantRanking = builder._irrelevantRanking;
    }  
  
    public ModelRankCollection getIrrelevantRanking() {
        return _irrelevantRanking;
    }

    public static class ModelRelevantClosureEntailmentBuilder extends ModelEntailment.EntailmentBuilder<ModelRelevantClosureEntailmentBuilder> {      
        private ModelRankCollection _relevantRanking;
        private ModelRankCollection _irrelevantRanking;

        public ModelRelevantClosureEntailmentBuilder withRelevantRankCollection(ModelRankCollection relevantRanking) {
          _relevantRanking = relevantRanking;

          return this;
        }

        public ModelRelevantClosureEntailmentBuilder withIrrelevantRankCollection(ModelRankCollection irrelevantRanking) {
          _irrelevantRanking = irrelevantRanking;

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

