package uct.cs.klm.algorithms.rational;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import uct.cs.klm.algorithms.models.ModelEntailment;

/**
 * This class represents a model rational closure entailment for a given query.
 * 
 * @author Chipo Hamayobe (chipo@cs.uct.ac.za)
 * @version 1.0.1
 * @since 2024-01-01
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ModelRationalClosureEntailment extends ModelEntailment {
   
    public ModelRationalClosureEntailment() {
        super();
    }

    private ModelRationalClosureEntailment(RationalClosureEntailmentBuilder builder) {
        super(builder);
    }
   
    public static class RationalClosureEntailmentBuilder extends EntailmentBuilder<RationalClosureEntailmentBuilder> {
       
        @Override
        protected RationalClosureEntailmentBuilder self() {
            return this;
        }

        @Override
        public ModelRationalClosureEntailment build() {
            return new ModelRationalClosureEntailment(this);
        }
    }
}
