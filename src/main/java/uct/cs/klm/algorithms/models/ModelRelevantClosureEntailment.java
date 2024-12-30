package uct.cs.klm.algorithms.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ModelRelevantClosureEntailment extends ModelEntailment {

  private ModelRelevantClosureEntailment(RelevantEntailmentBuilder builder) {
    super(builder);
  }

  public static class RelevantEntailmentBuilder extends EntailmentBuilder<RelevantEntailmentBuilder> {

    @Override
    protected RelevantEntailmentBuilder self() {
      return this;
    }

    @Override
    public ModelRelevantClosureEntailment build() {
      return new ModelRelevantClosureEntailment(this);
    }
  }
}
