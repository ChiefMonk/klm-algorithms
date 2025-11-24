type Ranking = {
  rankNumber: number;
  formulas: string[];
  discarded?: boolean;
};

type RankingOfRank = {
  rankNumber: number;
  formulas: Ranking[];  
};

type BaseRanking = {
  knowledgeBase: string[];
  signature: string[];
  ranking: Ranking[];
  sequence: Ranking[];
  antecedents: Record<string, string>;
  timeTaken: number;
};

type EntailmentModelBase = {
  queryFormula: string;
  negation: string;
  knowledgeBase: string[];
  entailmentKnowledgeBase: string[];
  removedKnowledgeBase: string[];
  remainingKnowledgeBase: string[];
  relevantKnowledgeBase: string[];
  signature: string[];
  entailed: boolean;
  baseRanking: Ranking[];
  miniBaseRanking: Ranking[];
  timeTaken: number;
  justificationTime: number;
  removedRanking: Ranking[];
  remainingRanking: Ranking[];
  relevantRanking: Ranking[];
  irrelevantRanking: Ranking[];
  powersetRanking: Ranking[];
  consistentRank: number;
  justification: string[][];
  relevantJustification: string[][];
  type: EntailmentType;
};

type RationalEntailment = EntailmentModelBase;

type LexicalEntailment = EntailmentModelBase & {
  weakenedRanking: Ranking[];
};

type MinimalRelevantEntailment = EntailmentModelBase;

type BasicRelevantEntailment = EntailmentModelBase;

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
  private _signature: string[];
  private _ranking: Ranking[];
  private _sequence: Ranking[];
  private _antecedents: Record<string, string>;
  private _timeTaken: number;

  /**
   * Constructs new base rank model.
   */
  constructor({
    knowledgeBase,
    signature,
    ranking,
    sequence,
    antecedents,
    timeTaken,
  }: BaseRanking) {
    this._knowledgeBase = knowledgeBase;
    this._signature = signature;
    this._ranking = ranking;
    this._sequence = sequence;
    this._antecedents = antecedents;
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
   * Gets the Signature of the knowledge base.
   *
   * @returns The Signature of the knowledge base as an array of strings.
   */
  public get signature(): string[] {
    return this._signature;
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
   * Gets the exceptionality sequence.
   *
   * @returns An array of Ranking objects.
   */
  public get antecedents(): Record<string, string> {
    return this._antecedents;
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
      signature: this._signature,
      ranking: this._ranking,
      sequence: this._sequence,
      antecedents: this._antecedents,
      timeTaken: this._timeTaken,
    };
  }

  public static create(obj: BaseRanking): BaseRankModel {
    return new BaseRankModel(obj);
  }
}

enum EntailmentType {
  Unknown = 0,
  RationalClosure,
  LexicographicClosure,
  BasicRelevantClosure,
  MinimalRelevantClosure,
}

/**
 * An abstract class representing a model for entailment,
 * which determines whether a query is entailed by a knowledge base.
 */
abstract class EntailmentModel {
  private _type: EntailmentType = EntailmentType.Unknown;
  private _queryFormula: string;
  private _negation: string;
  private _knowledgeBase: string[];
  private _signature: string[];
  private _entailed: boolean;
  private _baseRanking: Ranking[];
  private _miniBaseRanking: Ranking[];
  private _timeTaken: number;
  private _removedRanking: Ranking[];
  private _remainingRanking: Ranking[];
  private _relevantRanking: Ranking[];
  private _irrelevantRanking: Ranking[];
  private _powersetRanking: Ranking[];
  private _consistentRank: number;
  private _justification: string[][];
  private _relevantJustification: string[][];
  private _entailmentKnowledgeBase: string[];
  private _removedKnowledgeBase: string[];
  private _remainingKnowledgeBase: string[];
  private _relevantKnowledgeBase: string[];
  private _justificationTime: number;

  constructor({
    queryFormula,
    negation,
    knowledgeBase,
    signature,
    entailed,
    baseRanking,
    miniBaseRanking,
    timeTaken,
    removedRanking,
    remainingRanking,
    relevantRanking,
    irrelevantRanking,
    powersetRanking,
    consistentRank,
    justification,
    relevantJustification,
    entailmentKnowledgeBase,
    removedKnowledgeBase,
    remainingKnowledgeBase,
    relevantKnowledgeBase,
    justificationTime,
    type,
  }: EntailmentModelBase) {
    this._queryFormula = queryFormula;
    this._negation = negation;
    this._knowledgeBase = knowledgeBase ?? [];
    this._entailmentKnowledgeBase = (entailmentKnowledgeBase as string[]) ?? [];
    this._removedKnowledgeBase = (removedKnowledgeBase as string[]) ?? [];
    this._remainingKnowledgeBase = (remainingKnowledgeBase as string[]) ?? [];
    this._relevantKnowledgeBase = (relevantKnowledgeBase as string[]) ?? [];
    this._signature = signature ?? [];
    this._entailed = entailed;
    this._baseRanking = baseRanking ?? [];
    this._miniBaseRanking = miniBaseRanking ?? [];
    this._timeTaken = timeTaken;
    this._justificationTime = justificationTime ?? 0;
    this._removedRanking = removedRanking ?? [];
    this._remainingRanking = remainingRanking ?? [];
    this._relevantRanking = relevantRanking ?? [];
    this._irrelevantRanking = irrelevantRanking ?? [];
    this._justification = justification ?? [];
    this._relevantJustification = relevantJustification ?? [];
    this._powersetRanking = powersetRanking ?? [];
    this._consistentRank = consistentRank ?? 0;
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

  public get signature(): string[] {
    return this._signature;
  }

  public get entailed(): boolean {
    return this._entailed;
  }

  public get baseRanking(): Ranking[] {
    return this._baseRanking;
  }

  public get miniBaseRanking(): Ranking[] {
    return this._miniBaseRanking;
  }

  public get removedRanking(): Ranking[] {
    return this._removedRanking;
  }

  public get remainingRanking(): Ranking[] {
    return this._remainingRanking;
  }

  public get relevantRanking(): Ranking[] {
    return this._relevantRanking;
  }

  public get irrelevantRanking(): Ranking[] {
    return this._irrelevantRanking;
  }

  public get powersetRanking(): Ranking[] {
    return this._powersetRanking;
  }

  public get consistentRank(): number {
    return this._consistentRank;
  }

  public get timeTaken(): number {
    return this._timeTaken;
  }

  public get justifcationTime(): number {
    return this._justificationTime;
  }

  public get justification(): string[][] {
    return this._justification;
  }
  public get relevantJustification(): string[][] {
    return this._relevantJustification;
  }

  public get entailmentKnowledgeBase(): string[] {
    return this._entailmentKnowledgeBase;
  }

  public get remainingKnowledgeBase(): string[] {
    return this._remainingKnowledgeBase;
  }

  public get removedKnowledgeBase(): string[] {
    return this._removedKnowledgeBase;
  }
  public get relevantKnowledgeBase(): string[] {
    return this._relevantKnowledgeBase;
  }

  public abstract get remainingRanks(): Ranking[];

  public toObject(): EntailmentModelBase {
    return {
      queryFormula: this._queryFormula,
      negation: this._negation,
      knowledgeBase: this._knowledgeBase,
      entailmentKnowledgeBase: this._entailmentKnowledgeBase,
      removedKnowledgeBase: this._removedKnowledgeBase,
      remainingKnowledgeBase: this._remainingKnowledgeBase,
      relevantKnowledgeBase: this._relevantKnowledgeBase,
      signature: this._signature,
      entailed: this._entailed,
      baseRanking: this._baseRanking,
      miniBaseRanking: this._miniBaseRanking,
      timeTaken: this._timeTaken,
      removedRanking: this._removedRanking,
      remainingRanking: this._remainingRanking,
      relevantRanking: this._relevantRanking,
      irrelevantRanking: this._irrelevantRanking,
      powersetRanking: this._powersetRanking,
      consistentRank: this._consistentRank,
      justification: this._justification,
      relevantJustification: this._relevantJustification,
      justificationTime: this._justificationTime,
      type: this._type,
    };
  }
}

/**
 * A specific type of entailment model based on rationality principles.
 */
class RationalEntailmentModel extends EntailmentModel {
  constructor(obj: RationalEntailment) {
    super({ ...obj, type: EntailmentType.RationalClosure });
  }

  public get remainingRanks(): Ranking[] {
    const ranks: Ranking[] = [];
    //const n =  this.removedRanking != null ? this.removedRanking.length: 0;
    //const m =  this.baseRanking != null ? this.baseRanking.length: 0;
    const r = this.remainingRanking != null ? this.remainingRanking.length : 0;

    console.log("RC Remaining: " + r.toString());

    for (let i = 0; i < r; i++) {
      ranks.push(this.remainingRanking[i]);
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

    const r = this.remainingRanking != null ? this.remainingRanking.length : 0;
    console.log("LC Remaining: " + r.toString());

    for (let i = 0; i < r; i++) {
      ranks.push(this.remainingRanking[i]);
    }
    return ranks;

    // const k = this.weakenedRanking != null ? this.weakenedRanking.length: 0;
    //const n =  this.removedRanking != null ? this.removedRanking.length: 0;
    // const m =  this.baseRanking != null ? this.baseRanking.length: 0;

    // Add Latest refined rank
    // if (k != 0 && this.weakenedRanking[k - 1].rankNumber == n) {
    //   ranks.push(this.weakenedRanking[k - 1]);
    // }

    //for (let i = n + ranks.length; i < m; i++) {
    //   ranks.push(this.baseRanking[i]);
    //  }
    // return ranks;
  }

  public toObject(): LexicalEntailment {
    const baseObj = super.toObject();
    return {
      ...baseObj,
      weakenedRanking: this._weakenedRanking,
    };
  }

  public static create(obj: LexicalEntailment): LexicalEntailmentModel {
    console.log("1-Lexical Justification: " + obj.justification);

    return new LexicalEntailmentModel(obj);
  }
}

/**
 * A specific type of entailment model based on rationality principles.
 */
class MinimalRelevantEntailmentModel extends EntailmentModel {
  constructor(obj: MinimalRelevantEntailment) {
    super({ ...obj, type: EntailmentType.MinimalRelevantClosure });
  }

  public get remainingRanks(): Ranking[] {
    const ranks: Ranking[] = [];
    //const n =  this.removedRanking != null ? this.removedRanking.length: 0;
    // const m =  this.baseRanking != null ? this.baseRanking.length: 0;
    // for (let i = n; i < m; i++) {
    //  ranks.push(this.baseRanking[i]);
    // }

    const r = this.remainingRanking != null ? this.remainingRanking.length : 0;

    console.log("RelC Remaining: " + r.toString());

    for (let i = 0; i < r; i++) {
      ranks.push(this.remainingRanking[i]);
    }
    return ranks;
  }

  public toObject(): MinimalRelevantEntailment {
    return super.toObject();
  }

  public static create(
    obj: MinimalRelevantEntailment
  ): MinimalRelevantEntailmentModel {
    return new MinimalRelevantEntailmentModel(obj);
  }
}

/**
 * A specific type of entailment model based on rationality principles.
 */
class BasicRelevantEntailmentModel extends EntailmentModel {
  constructor(obj: BasicRelevantEntailment) {
    super({ ...obj, type: EntailmentType.BasicRelevantClosure });
  }

  public get remainingRanks(): Ranking[] {
    const ranks: Ranking[] = [];
    //const n =  this.removedRanking != null ? this.removedRanking.length: 0;
    // const m =  this.baseRanking != null ? this.baseRanking.length: 0;
    // for (let i = n; i < m; i++) {
    //  ranks.push(this.baseRanking[i]);
    // }

    const r = this.remainingRanking != null ? this.remainingRanking.length : 0;

    console.log("Basic RelC Remaining: " + r.toString());

    for (let i = 0; i < r; i++) {
      ranks.push(this.remainingRanking[i]);
    }
    return ranks;
  }

  public toObject(): BasicRelevantEntailment {
    return super.toObject();
  }

  public static create(
    obj: BasicRelevantEntailment
  ): BasicRelevantEntailmentModel {
    return new BasicRelevantEntailmentModel(obj);
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

enum DistributionType {
  Uniform = "Uniform",
  Linear = "Linear",
  ReversedLinear = "ReversedLinear",
  Exponential = "Exponential",
  ReversedExponential = "ReversedExponential",
}

enum Complexity {
  Low = "Low",
  Medium = "Medium",
  High = "High",
}

interface KbGenerationInput {
  numberOfRanks: number;
  distributionType: DistributionType;
  complexity: Complexity;
  numberOfDefeasibleImplications: number;
}

export interface IRank {
  rankNumber: number;
  formulas: string[];
}

export enum SequenceTerminationReason {
  DoesNotTerminateYet,
  EqualsLastElement,
  EmptySet,
}

export interface ISequenceElementCheck {
  elementNumber: number;
  antecedentNegation: string;
  formula: string;
  isExceptional: boolean;
}

export interface ISequenceElement {
  elementNumber: number;
  formulas: string[];
  checks: ISequenceElementCheck[];
  isLastElement: boolean;
  isEmpty: boolean;
}

export interface IBaseRankExplanation {
  knowledgeBase: string[];
  sequence: ISequenceElement[];
  ranks: IRank[];
}

export interface IRCRankEntailmentCheck {
  ranks: IRank[];
  knowledgeBase: string[];
  removedRank: IRank;
  isConsistent: boolean;
  antecedentNegation: string;
}

export interface IRationalClosureExplanation {
  queryFormula: string;
  knowledgeBase: string[];
  isEntailed: boolean;
  checks: IRCRankEntailmentCheck[];
  baseRanking: IRank[];
  removedRanking: IRank[];
  remainingRanking: IRank[];
  justification: string[][];
}

// Exporting the models and the Ranking type for external use.
export {
  BaseRankModel,
  EntailmentModel,
  RationalEntailmentModel,
  LexicalEntailmentModel,
  MinimalRelevantEntailmentModel,
  BasicRelevantEntailmentModel,
  ErrorModel,
  EntailmentType,
  DistributionType,
  Complexity,
};

export type {
  Ranking,
  RankingOfRank,
  BaseRanking,
  EntailmentModelBase,
  RationalEntailment,
  LexicalEntailment,
  MinimalRelevantEntailment,
  BasicRelevantEntailment,
  ErrorData,
  KbGenerationInput,
};

export const ConstantValues = {
  INFINITY_RANK_NUMBER: 999999999,
};

export * from "./common";
export * from "./evaluation";
export * from "./forms-fields";
