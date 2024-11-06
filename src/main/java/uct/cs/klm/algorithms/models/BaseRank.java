package uct.cs.klm.algorithms.models;

public class BaseRank {
  private final KnowledgeBase knowledgeBase;
  private final Ranking ranking;
  private final Ranking sequence;
  private final double timeTaken;

  public BaseRank() {
    this(new KnowledgeBase(), new Ranking(), new Ranking(), 0);
  }

  public BaseRank(KnowledgeBase knowledgeBase, Ranking sequence, Ranking ranking, double timeTaken) {
    this.sequence = sequence;
    this.ranking = ranking;
    this.timeTaken = timeTaken;
    this.knowledgeBase = knowledgeBase;
  }

  public BaseRank(BaseRank baseRank) {
    this(baseRank.getKnowledgeBase(), baseRank.getSequence(), baseRank.getRanking(), baseRank.getTimeTaken());
  }

  public Ranking getRanking() {
    return new Ranking(ranking);
  }

  public Ranking getSequence() {
    return new Ranking(sequence);
  }

  public double getTimeTaken() {
    return timeTaken;
  }

  public KnowledgeBase getKnowledgeBase() {
    return new KnowledgeBase(knowledgeBase);
  }
}
