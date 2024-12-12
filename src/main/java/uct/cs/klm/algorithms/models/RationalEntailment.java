package uct.cs.klm.algorithms.models;

public class RationalEntailment extends Entailment {

    private RationalEntailment(RationalEntailmentBuilder builder) {
        super(builder);
    }

    public static class RationalEntailmentBuilder extends EntailmentBuilder<RationalEntailmentBuilder> {

        @Override
        protected RationalEntailmentBuilder self() {
            return this;
        }

        @Override
        public RationalEntailment build() {
            return new RationalEntailment(this);
        }
    }
}
