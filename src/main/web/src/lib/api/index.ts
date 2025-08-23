export * from "./evaluation";
import axios, { AxiosError } from "axios";
import {
  BaseRankModel,
  ErrorModel,
  IBaseRankExplanation,
  IRationalClosureExplanation,
  KbGenerationInput,
  LexicalEntailmentModel,
  RationalEntailmentModel,
  BasicRelevantEntailmentModel,
  MinimalRelevantEntailmentModel,
} from "../models";

const URL_QUERY_GET_FORMULA = "/api/queries/get-formula";
const URL_QUERY_POST_CREATE_FORMULA = "/api/queries/create-formula";

const URL_KB_GET_DEFAULT = "/api/knowledge-base/get-default";
const URL_KB_POST_GENERATE = "/api/knowledge-base/generate";
const URL_KB_POST_SIGNATURE = "/api/knowledge-base/get-signature";
const URL_KB_POST_CREATE_INPUT = "/api/knowledge-base/create-from-input";
const URL_KB_POST_CREATE_FILE = "/api/knowledge-base/create-from-file";

const BASE_RANK_URL = "/api/base-rank";
const BASE_RANK_EXPLANATION_URL = "/api/base-rank-explanation";
const ENTAILMENT_URL = (reasoner: string, queryFormula: string) => {
  return `/api/entailment/${reasoner}/${queryFormula}`;
};
const ENTAILMENT_EXPLANATION_URL = (reasoner: string) => {
  return `/api/explanation/${reasoner}`;
};

// eslint-disable-next-line @typescript-eslint/no-explicit-any
const getError = (error: any) => {
  if (error instanceof AxiosError) {
    if (error.response && error.response.status < 500) {
      return ErrorModel.create(error.response.data);
    }
  }
  return ErrorModel.create({
    code: 500,
    description: "Internal Server Error",
    message:
      "We're currently experiencing issues with our server. Please try again later.",
  });
};

const fetchQueryFormula = async () => {
  try {
    console.log("GET: Query Formula Request");

    const response = await axios.get(URL_QUERY_GET_FORMULA);
    return response.data.queryFormula as string;
  } catch (error) {
    throw getError(error);
  }
};

const createQueryFormula = async (formula: string) => {
  try {
    console.log("POST: Create Query Formula Request: " + formula);

    const response = await axios.post(
      URL_QUERY_POST_CREATE_FORMULA + "/" + formula
    );
    return response.data.queryFormula as string;
  } catch (error) {
    throw getError(error);
  }
};

const getDefaultKnowledgeBase = async () => {
  try {
    console.log("GET: Default KnowledgeBase Request");

    const response = await axios.get(URL_KB_GET_DEFAULT);
    return response.data as string[];
  } catch (error) {
    throw getError(error);
  }
};

const getGeneratedKnowledgeBase = async (data: KbGenerationInput) => {
  try {
    console.log("GET: Generate KnowledgeBase Request");

    const response = await axios.post(URL_KB_POST_GENERATE, data);
    return response.data as string[];
  } catch (error) {
    throw getError(error);
  }
};

const getSignatureKnowledgeBase = async (data: string[]) => {
  try {
    console.log("POST: KnowledgeBase Signature Request: " + data.toString());

    const response = await axios.post(URL_KB_POST_SIGNATURE, data);
    return response.data as string[];
  } catch (error) {
    throw getError(error);
  }
};

const createInputKnowledgeBase = async (data: string[]) => {
  try {
    console.log("POST: Create Input KnowledgeBase Request: " + data.toString());

    const response = await axios.post(URL_KB_POST_CREATE_INPUT, data);
    return response.data as string[];
  } catch (error) {
    throw getError(error);
  }
};

const createFileKnowledgeBase = async (data: FormData) => {
  try {
    console.log("POST: Create File KnowledgeBase Request: " + data.toString());

    const response = await axios.post(URL_KB_POST_CREATE_FILE, data, {
      headers: { "Content-Type": "multipart/form-data" },
    });
    return response.data as string[];
  } catch (error) {
    throw getError(error);
  }
};

const fetchBaseRank = async (data: string[]) => {
  try {
    console.log("POST: BaseRank Request: " + data.toString());

    const response = await axios.post(BASE_RANK_URL, data);

    console.log("BaseRank Response: " + response.data.toString());

    return BaseRankModel.create(response.data);
  } catch (error) {
    throw getError(error);
  }
};

const fetchBaseRankExplanation = async (baseRank: BaseRankModel) => {
  try {
    console.log("POST: BaseRank Explanation Request: " + baseRank);

    const response = await axios.post(
      BASE_RANK_EXPLANATION_URL,
      baseRank.toObject()
    );

    console.log("BaseRank Explanation Response: " + response.data);

    return response.data as IBaseRankExplanation;
  } catch (error) {
    throw getError(error);
  }
};

const fetchRationalEntailment = async (
  queryFormula: string,
  baseRank: BaseRankModel
) => {
  try {
    console.log(
      "POST: Rational Entailment Request: " +
        queryFormula +
        " " +
        baseRank.toString()
    );

    const response = await axios.post(
      ENTAILMENT_URL("rational", queryFormula),
      baseRank.toObject()
    );

    console.log("RC Response: " + response.data.toString());

    return RationalEntailmentModel.create(response.data);
  } catch (error) {
    throw getError(error);
  }
};

const fetchRationalExplanation = async (
  entailment: RationalEntailmentModel
) => {
  try {
    console.log("POST: Rational Explanation Request:", entailment);

    const response = await axios.post(
      ENTAILMENT_EXPLANATION_URL("rational"),
      entailment.toObject()
    );

    console.log("Rational Explanation Response: " + response.data);

    return response.data as IRationalClosureExplanation;
  } catch (error) {
    throw getError(error);
  }
};

const fetchLexicalEntailment = async (
  queryFormula: string,
  baseRank: BaseRankModel
) => {
  try {
    console.log(
      "POST: Lexical Entailment Request: " +
        queryFormula +
        " " +
        baseRank.toString()
    );

    const response = await axios.post(
      ENTAILMENT_URL("lexical", queryFormula),
      baseRank.toObject()
    );

    console.log("LC Response: " + response.data.toString());

    return LexicalEntailmentModel.create(response.data);
  } catch (error) {
    throw getError(error);
  }
};

const fetchLexicalExplanation = async (entailment: RationalEntailmentModel) => {
  try {
    console.log("POST: Lexical Explanation Request:", entailment);

    const response = await axios.post(
      ENTAILMENT_EXPLANATION_URL("lexical"),
      entailment.toObject()
    );

    console.log("Lexical Explanation Response: " + response.data);

    return response.data as IRationalClosureExplanation;
  } catch (error) {
    throw getError(error);
  }
};

const fetchBasicRelevantEntailment = async (
  queryFormula: string,
  baseRank: BaseRankModel
) => {
  try {
    console.log(
      "POST: Basic Relevant Entailment Request: " +
        queryFormula +
        " " +
        baseRank.toString()
    );

    const response = await axios.post(
      ENTAILMENT_URL("brelc", queryFormula),
      baseRank.toObject()
    );

    console.log("Basic RelC Response: " + response.data.toString());

    return BasicRelevantEntailmentModel.create(response.data);
  } catch (error) {
    throw getError(error);
  }
};

const fetchBasicRelevantExplanation = async (
  entailment: RationalEntailmentModel
) => {
  try {
    console.log("POST: Basic Relevant Explanation Request:", entailment);

    const response = await axios.post(
      ENTAILMENT_EXPLANATION_URL("brelc"),
      entailment.toObject()
    );

    console.log("Basic Relevant Explanation Response: " + response.data);

    return response.data as IRationalClosureExplanation;
  } catch (error) {
    throw getError(error);
  }
};

const fetchMinimalRelevantEntailment = async (
  queryFormula: string,
  baseRank: BaseRankModel
) => {
  try {
    console.log(
      "POST: Minimal Relevant Entailment Request: " +
        queryFormula +
        " " +
        baseRank.toString()
    );

    const response = await axios.post(
      ENTAILMENT_URL("mrelc", queryFormula),
      baseRank.toObject()
    );

    console.log("Minimal RelC Response: " + response.data.toString());

    return MinimalRelevantEntailmentModel.create(response.data);
  } catch (error) {
    throw getError(error);
  }
};

const fetchMinimalRelevantExplanation = async (
  entailment: RationalEntailmentModel
) => {
  try {
    console.log("POST: Minimal Relevant Explanation Request:", entailment);

    const response = await axios.post(
      ENTAILMENT_EXPLANATION_URL("mrelc"),
      entailment.toObject()
    );

    console.log("Minimal Relevant Explanation Response: " + response.data);

    return response.data as IRationalClosureExplanation;
  } catch (error) {
    throw getError(error);
  }
};


export {
  fetchQueryFormula,
  createQueryFormula,
  getDefaultKnowledgeBase,
  getGeneratedKnowledgeBase,
  getSignatureKnowledgeBase,
  createInputKnowledgeBase,
  createFileKnowledgeBase,
  fetchBaseRank,
  fetchBaseRankExplanation,
  fetchRationalEntailment,
  fetchLexicalEntailment,
  fetchBasicRelevantEntailment,
  fetchMinimalRelevantEntailment,
  fetchRationalExplanation,
  fetchLexicalExplanation,
  fetchBasicRelevantExplanation,
  fetchMinimalRelevantExplanation,
};
