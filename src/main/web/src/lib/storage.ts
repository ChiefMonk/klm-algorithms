import {
  BaseRankModel,
  LexicalEntailmentModel,
  RationalEntailmentModel,
} from "@/lib/models";

export type QueryInput = {
  queryFormula: string;
  knowledgeBase: string[];
};

export type QueryResult = {
  baseRank: BaseRankModel;
  rationalEntailment: RationalEntailmentModel;
  lexicalEntailment: LexicalEntailmentModel;
};

export const getQueryInput: () => QueryInput | null = () => {
  const storedValue = localStorage.getItem("queryInput");
  return storedValue ? JSON.parse(storedValue) : null;
};

export const saveQueryInput: (data: QueryInput) => void = (data) => {
  localStorage.setItem("queryInput", JSON.stringify(data));
};

export const getQueryResult: () => QueryResult | null = () => {
  const storedValue = localStorage.getItem("queryResult");
  const obj = storedValue ? JSON.parse(storedValue) : null;
  if (obj != null) {
    return {
      baseRank: BaseRankModel.create(obj.baseRank),
      rationalEntailment: RationalEntailmentModel.create(
        obj.rationalEntailment
      ),
      lexicalEntailment: LexicalEntailmentModel.create(obj.lexicalEntailment),
    };
  }
  return null;
};

export const deleteQueryInput: () => void = () => {
  localStorage.removeItem("queryInput");
};

export const saveQueryResult: (data: QueryResult) => void = (data) => {
  localStorage.setItem(
    "queryResult",
    JSON.stringify({
      baseRank: data.baseRank.toObject(),
      rationalEntailment: data.rationalEntailment.toObject(),
      lexicalEntailment: data.lexicalEntailment.toObject(),
    })
  );
};

export const deleteQueryResult: () => void = () => {
  localStorage.removeItem("queryResult");
};

export const deleteAllData: () => void = () => {
  deleteQueryInput();
  deleteQueryResult();
};
