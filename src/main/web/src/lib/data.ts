import axios, { AxiosError } from "axios";
import {
  BaseRankModel,
  ErrorModel,
  LexicalEntailmentModel,
  RationalEntailmentModel,
} from "./models";

const FORMULA_URL = "/api/query-formula";
const KB_URL = "/api/knowledge-base";
const BASE_RANK_URL = "/api/base-rank";
const ENTAILMENT_URL = (reasoner: string, queryFormula: string) => {
  return `/api/entailment/${reasoner}/${queryFormula}`;
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
    const response = await axios.get(FORMULA_URL);
    return response.data.queryFormula as string;
  } catch (error) {
    throw getError(error);
  }
};

const createQueryFormula = async (formula: string) => {
  try {
    const response = await axios.post(FORMULA_URL + "/" + formula);
    return response.data.queryFormula as string;
  } catch (error) {
    throw getError(error);
  }
};

const fetchKnowledgeBase = async () => {
  try {
    const response = await axios.get(KB_URL);
    return response.data as string[];
  } catch (error) {
    throw getError(error);
  }
};

const createKnowledgeBase = async (data: string[]) => {
  try {
    const response = await axios.post(KB_URL, data);
    return response.data as string[];
  } catch (error) {
    throw getError(error);
  }
};

const uploadKnowledgeBase = async (data: FormData) => {
  try {
    const response = await axios.post(KB_URL + "/file", data, {
      headers: { "Content-Type": "multipart/form-data" },
    });
    return response.data as string[];
  } catch (error) {
    throw getError(error);
  }
};

const fetchBaseRank = async (data: string[]) => {
  try {
    const response = await axios.post(BASE_RANK_URL, data);
    return BaseRankModel.create(response.data);
  } catch (error) {
    throw getError(error);
  }
};

const fetchRationalEntailment = async (
  queryFormula: string,
  baseRank: BaseRankModel
) => {
  try {
    const response = await axios.post(
      ENTAILMENT_URL("rational", queryFormula),
      baseRank.toObject()
    );
    return RationalEntailmentModel.create(response.data);
  } catch (error) {
    throw getError(error);
  }
};

const fetchLexicalEntailment = async (
  queryFormula: string,
  baseRank: BaseRankModel
) => {
  try {
    const response = await axios.post(
      ENTAILMENT_URL("lexical", queryFormula),
      baseRank.toObject()
    );
    return LexicalEntailmentModel.create(response.data);
  } catch (error) {
    throw getError(error);
  }
};

export {
  fetchQueryFormula,
  createQueryFormula,
  fetchKnowledgeBase,
  createKnowledgeBase,
  uploadKnowledgeBase,
  fetchBaseRank,
  fetchRationalEntailment,
  fetchLexicalEntailment,
};
