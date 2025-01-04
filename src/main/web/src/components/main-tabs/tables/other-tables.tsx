import { TexFormula } from "@/components/main-tabs/common/TexFormula";
import { DataTable } from "@/components/ui/data-table";
import { toTex } from "@/lib/formula";
import { BaseRankModel, EntailmentModel } from "@/lib/models";
import { ColumnDef } from "@tanstack/react-table";

interface AlgorithmResult {
  algorithm: string;
  result: string;
  justification: string;
}

interface TimesResult {
  algorithm: string;
  timeTaken: string;
}

const entailmentColumns: ColumnDef<AlgorithmResult>[] = [
  {
    accessorKey: "algorithm",
    header: "Inference Operator",
    cell: ({ row }) => row.getValue("algorithm"),
    meta: {
      headerClassName: "min-w-[180px]",
      cellClassName: "whitespace-nowrap",
    },
  },
  {
    accessorKey: "result",
    header: "Entailment",
    cell: ({ row }) => <TexFormula>{row.getValue("result")}</TexFormula>,
    meta: {     
      headerClassName: "min-w-[180px]",
      cellClassName: "whitespace-nowrap",
    },
  },
  {
    accessorKey: "justification",
    header: "Justification",
    cell: ({ row }) => <TexFormula>{row.getValue("justification")}</TexFormula>,
    meta: {
      headerClassName: "w-full min-w-[180px]",
      cellClassName: "whitespace-nowrap",
    },
  },
];

const timesColumns: ColumnDef<TimesResult>[] = [
  {
    accessorKey: "algorithm",
    header: "Algorithm",
    cell: ({ row }) => row.getValue("algorithm"),
    meta: {
      headerClassName: "min-w-[180px]",
      cellClassName: "whitespace-nowrap",
    },
  },
  {
    accessorKey: "timeTaken",
    header: "Time Taken (in seconds)",
    cell: ({ row }) => {
      const value = row.getValue("timeTaken") as string;
      return <TexFormula>{value}</TexFormula>;
    },
    meta: {
      headerClassName: "w-full min-w-[180px]",
      cellClassName: "whitespace-nowrap",
    },
  },
];

interface EntailmentTableProps {
  rationalEntailment: EntailmentModel;
  lexicalEntailment: EntailmentModel;
  relevantEntailment: EntailmentModel;
}

function EntailmentTable({
  rationalEntailment,
  lexicalEntailment,
  relevantEntailment,
}: EntailmentTableProps) {
  const getResult = ({entailed, queryFormula }: EntailmentModel) => {
      
    return entailed
      ? toTex("\\mathcal{K} \\vapprox " + queryFormula)
      : toTex("\\mathcal{K} \\nvapprox " + queryFormula)
  };

  const getJustification = ({ entailed, justification }: EntailmentModel) => {
    console.log("Final Justification: " + justification);

    if (entailed && justification.length > 0) {
      const symbol = justification
          .map((item, index) => `\\mathcal{J_{${index + 1}}} =: \\{ ${item} \\}`)
          .join(', ');
      
      return toTex(symbol);
  }

  return toTex("\\mathcal{J_{1}} =: \\{ \\}");


   // return entailed && justification.length > 0
   //   ? toTex("\\mathcal{J_{1}} =: \\{ " + justification + " \\}")
   //   : toTex("\\mathcal{J_{1}} =: \\{ \\}");
  };
  return (
    <DataTable
      columns={entailmentColumns}
      data={[
        {
          algorithm: "Rational Closure",
          result: getResult(rationalEntailment),
          justification:  getJustification(rationalEntailment),
        },
        {
          algorithm: "Lexicographic Closure",
          result: getResult(lexicalEntailment),
          justification: getJustification(lexicalEntailment),
        },
        {
          algorithm: "Relevant Closure",
          result: getResult(relevantEntailment),
          justification: getJustification(relevantEntailment),
        },
      ]}
    />
  );
}

interface TimesTableProps {
  baseRank: BaseRankModel;
  rationalEntailment: EntailmentModel;
  lexicalEntailment: EntailmentModel;
  relevantEntailment: EntailmentModel;
}

function TimesTable({
  baseRank,
  rationalEntailment,
  lexicalEntailment,
  relevantEntailment,
}: TimesTableProps) {
  const roundOff = (value: number) => {
    return (Math.round(value * 10000) / 10000).toString();
  };
  return (
    <DataTable
      columns={timesColumns}
      data={[
        { algorithm: "Base Rank", timeTaken: roundOff(baseRank.timeTaken) },
        {
          algorithm: "Rational Closure",
          timeTaken: roundOff(rationalEntailment.timeTaken),
        },
        {
          algorithm: "Lexicographic Closure",
          timeTaken: roundOff(lexicalEntailment.timeTaken),
        },
        {
          algorithm: "Relevant Closure",
          timeTaken: roundOff(relevantEntailment.timeTaken),
        },
      ]}
    />
  );
}
export { EntailmentTable, TimesTable };
