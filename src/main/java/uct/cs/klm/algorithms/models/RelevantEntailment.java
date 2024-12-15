package uct.cs.klm.algorithms.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RelevantEntailment extends Entailment {

  private RelevantEntailment(RelevantEntailmentBuilder builder) {
    super(builder);
  }

  public static class RelevantEntailmentBuilder extends EntailmentBuilder<RelevantEntailmentBuilder> {

    @Override
    protected RelevantEntailmentBuilder self() {
      return this;
    }

    @Override
    public RelevantEntailment build() {
      return new RelevantEntailment(this);
    }
  }
}
