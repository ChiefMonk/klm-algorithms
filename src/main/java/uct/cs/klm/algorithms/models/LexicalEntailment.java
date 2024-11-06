package uct.cs.klm.algorithms.models;

public class LexicalEntailment extends Entailment {
  private final Ranking weakenedRanking;

  private LexicalEntailment(LexicalEntailmentBuilder builder) {
    super(builder);
    this.weakenedRanking = builder.weakenedRanking;
  }

  public Ranking getWeakenedRanking() {
    return weakenedRanking;
  }

  public static class LexicalEntailmentBuilder extends EntailmentBuilder<LexicalEntailmentBuilder> {
    private Ranking weakenedRanking;

    public LexicalEntailmentBuilder withWeakenedRanking(Ranking weakenedRanking) {
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
