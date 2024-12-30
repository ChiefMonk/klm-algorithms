package uct.cs.klm.algorithms.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ModelRationalClosureEntailment extends ModelEntailment {

    private ModelRationalClosureEntailment(RationalEntailmentBuilder builder) {
        super(builder);
    }

    public static class RationalEntailmentBuilder extends EntailmentBuilder<RationalEntailmentBuilder> {

        @Override
        protected RationalEntailmentBuilder self() {
            return this;
        }

        @Override
        public ModelRationalClosureEntailment build() {
            return new ModelRationalClosureEntailment(this);
        }
    }
}
