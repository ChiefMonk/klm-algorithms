package uct.cs.klm.algorithms.rational;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import uct.cs.klm.algorithms.models.ModelEntailment;

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
