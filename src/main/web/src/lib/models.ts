type Ranking = {
  rankNumber: number;
  formulas: string[];
};

type BaseRanking = {
  knowledgeBase: string[];
  ranking: Ranking[];
  sequence: Ranking[];
  timeTaken: number;
};

type EntailmentModelBase = {
  queryFormula: string;
  negation: string;
  knowledgeBase: string[];
  entailed: boolean;
  baseRanking: Ranking[];
  timeTaken: number;
  removedRanking: Ranking[];
  type: EntailmentType;
};

type RationalEntailment = EntailmentModelBase;

type LexicalEntailment = EntailmentModelBase & {
  weakenedRanking: Ranking[];
};

type ErrorData = {
  code: number;
  description: string;
  message: string;
};

/**
 * A base model for managing rankings within a knowledge base.
 */
class BaseRankModel {
  // Private fields holding the data for the model.
  private _knowledgeBase: string[];
  private _ranking: Ranking[];
  private _sequence: Ranking[];
  private _timeTaken: number;

  /**
   * Constructs new base rank model.
   */
  constructor({ knowledgeBase, ranking, sequence, timeTaken }: BaseRanking) {
    this._knowledgeBase = knowledgeBase;
    this._ranking = ranking;
    this._sequence = sequence;
    this._timeTaken = timeTaken;
  }

  /**
   * Gets the knowledge base.
   *
   * @returns The knowledge base as an array of strings.
   */
  public get knowledgeBase(): string[] {
    return this._knowledgeBase;
  }

  /**
   * Gets the rankings.
   *
   * @returns An array of Ranking objects.
   */
  public get ranking(): Ranking[] {
    return this._ranking;
  }

  /**
   * Gets the exceptionality sequence.
   *
   * @returns An array of Ranking objects.
   */
  public get sequence(): Ranking[] {
    return this._sequence;
  }

  /**
   * Gets the time taken for the base rank.
   *
   * @returns The time taken in seconds.
   */
  public get timeTaken(): number {
    return this._timeTaken;
  }

  public toObject(): BaseRanking {
    return {
      knowledgeBase: this._knowledgeBase,
      ranking: this._ranking,
      sequence: this._sequence,
      timeTaken: this._timeTaken,
    };
  }

  public static create(obj: BaseRanking): BaseRankModel {
    return new BaseRankModel(obj);
  }
}

enum EntailmentType {
  RationlClosure,
  LexicographicClosure,
  NoTypeYet,
}

/**
 * An abstract class representing a model for entailment,
 * which determines whether a query is entailed by a knowledge base.
 */
abstract class EntailmentModel {
  private _type: EntailmentType = EntailmentType.NoTypeYet;
  private _queryFormula: string;
  private _negation: string;
  private _knowledgeBase: string[];
  private _entailed: boolean;
  private _baseRanking: Ranking[];
  private _timeTaken: number;
  private _removedRanking: Ranking[];

  constructor({
    queryFormula,
    negation,
    knowledgeBase,
    entailed,
    baseRanking,
    timeTaken,
    removedRanking,
    type,
  }: EntailmentModelBase) {
    this._queryFormula = queryFormula;
    this._negation = negation;
    this._knowledgeBase = knowledgeBase ?? [];
    this._entailed = entailed;
    this._baseRanking = baseRanking ?? [];
    this._timeTaken = timeTaken;
    this._removedRanking = removedRanking ?? [];
    this._type = type;
  }

  public get type(): EntailmentType {
    return this._type;
  }

  public get queryFormula(): string {
    return this._queryFormula;
  }

  public get negation(): string {
    return this._negation;
  }

  public get knowledgeBase(): string[] {
    return this._knowledgeBase;
  }

  public get entailed(): boolean {
    return this._entailed;
  }

  public get baseRanking(): Ranking[] {
    return this._baseRanking;
  }

  public get removedRanking(): Ranking[] {
    return this._removedRanking;
  }

  public get timeTaken(): number {
    return this._timeTaken;
  }
  public abstract get remainingRanks(): Ranking[];

  public toObject(): EntailmentModelBase {
    return {
      queryFormula: this._queryFormula,
      negation: this._negation,
      knowledgeBase: this._knowledgeBase,
      entailed: this._entailed,
      baseRanking: this._baseRanking,
      timeTaken: this._timeTaken,
      removedRanking: this._removedRanking,
      type: this._type,
    };
  }
}

/**
 * A specific type of entailment model based on rationality principles.
 */
class RationalEntailmentModel extends EntailmentModel {
  constructor(obj: RationalEntailment) {
    super({ ...obj, type: EntailmentType.RationlClosure });
  }

  public get remainingRanks(): Ranking[] {
    const ranks: Ranking[] = [];
    const n = this.removedRanking.length;
    const m = this.baseRanking.length;
    for (let i = n; i < m; i++) {
      ranks.push(this.baseRanking[i]);
    }
    return ranks;
  }

  public toObject(): RationalEntailment {
    return super.toObject();
  }

  public static create(obj: RationalEntailment): RationalEntailmentModel {
    return new RationalEntailmentModel(obj);
  }
}

/**
 * A specific type of entailment model based on lexical principles.
 */
class LexicalEntailmentModel extends EntailmentModel {
  private _weakenedRanking: Ranking[];

  constructor(obj: LexicalEntailment) {
    super({ ...obj, type: EntailmentType.LexicographicClosure });
    this._weakenedRanking = obj.weakenedRanking;
  }

  public get weakenedRanking(): Ranking[] {
    return this._weakenedRanking;
  }

  public get remainingRanks(): Ranking[] {
    const ranks: Ranking[] = [];
    const k = this.weakenedRanking.length;
    const n = this.removedRanking.length;
    const m = this.baseRanking.length;

    // Add Latest refined rank
    if (k != 0 && this.weakenedRanking[k - 1].rankNumber == n) {
      ranks.push(this.weakenedRanking[k - 1]);
    }

    for (let i = n + ranks.length; i < m; i++) {
      ranks.push(this.baseRanking[i]);
    }
    return ranks;
  }

  public toObject(): LexicalEntailment {
    const baseObj = super.toObject();
    return {
      ...baseObj,
      weakenedRanking: this._weakenedRanking,
    };
  }

  public static create(obj: LexicalEntailment): LexicalEntailmentModel {
    return new LexicalEntailmentModel(obj);
  }
}

/**
 * Represents an error response with a code, description, and message.
 */
class ErrorModel extends Error {
  private _code: number;
  private _description: string;
  private _message: string;

  constructor({ code, description, message }: ErrorData) {
    super(message);
    this._code = code;
    this._description = description;
    this._message = message;
    Error.captureStackTrace(this, this.constructor); // Capture stack trace
  }

  public get code(): number {
    return this._code;
  }

  public get description(): string {
    return this._description;
  }

  public get message(): string {
    return this._message;
  }

  public toObject(): ErrorData {
    return {
      code: this._code,
      description: this._description,
      message: this._message,
    };
  }

  public static create(obj: ErrorData): ErrorModel {
    return new ErrorModel(obj);
  }
}

// Exporting the models and the Ranking type for external use.
export {
  BaseRankModel,
  EntailmentModel,
  RationalEntailmentModel,
  LexicalEntailmentModel,
  ErrorModel,
  EntailmentType,
};
export type {
  Ranking,
  BaseRanking,
  EntailmentModelBase,
  RationalEntailment,
  LexicalEntailment,
  ErrorData,
};
