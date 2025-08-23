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
  rationalEntailment: EntailmentModel | null;
  lexicalEntailment: EntailmentModel | null;
  basicRelevantEntailment: EntailmentModel | null;
  minimalRelevantEntailment: EntailmentModel | null;
}

function EntailmentTable({
  rationalEntailment,
  lexicalEntailment,
  basicRelevantEntailment,
  minimalRelevantEntailment,
}: EntailmentTableProps) {
  const getResult = ({ entailed, queryFormula }: EntailmentModel) => {
    return entailed
      ? toTex("\\mathcal{K} \\vapprox " + queryFormula)
      : toTex("\\mathcal{K} \\nvapprox " + queryFormula);
  };

  const getJustification = ({ entailed, justification }: EntailmentModel) => {
    console.log("Final Justification: " + justification);

    if (entailed && justification.length > 0) {
      const symbol = justification
        .map((item, index) => `\\mathcal{J_{${index + 1}}} = \\{ ${item} \\}`)
        .join(", ");

      return toTex(symbol);
    }

    return toTex("\\mathcal{J_{1}} = \\{ \\}");

    // return entailed && justification.length > 0
    //   ? toTex("\\mathcal{J_{1}} = \\{ " + justification + " \\}")
    //   : toTex("\\mathcal{J_{1}} = \\{ \\}");
  };
  const data = [
    rationalEntailment && {
      algorithm: "Rational Closure",
      result: getResult(rationalEntailment),
      justification: getJustification(rationalEntailment),
    },
    lexicalEntailment && {
      algorithm: "Lexicographic Closure",
      result: getResult(lexicalEntailment),
      justification: getJustification(lexicalEntailment),
    },
    basicRelevantEntailment && {
      algorithm: "Basic Relevant Closure",
      result: getResult(basicRelevantEntailment),
      justification: getJustification(basicRelevantEntailment),
    },
    minimalRelevantEntailment && {
      algorithm: "Minimal Relevant Closure",
      result: getResult(minimalRelevantEntailment),
      justification: getJustification(minimalRelevantEntailment),
    },
  ].filter(Boolean) as AlgorithmResult[];

  return <DataTable columns={entailmentColumns} data={data} />;
}

interface TimesTableProps {
  baseRank: BaseRankModel | null;
  rationalEntailment: EntailmentModel | null;
  lexicalEntailment: EntailmentModel | null;
  basicRelevantEntailment: EntailmentModel | null;
  minimalRelevantEntailment: EntailmentModel | null;
}

function TimesTable({
  baseRank,
  rationalEntailment,
  lexicalEntailment,
  basicRelevantEntailment,
  minimalRelevantEntailment,
}: TimesTableProps) {
  const roundOff = (value: number) => {
    return (Math.round(value * 10000) / 10000).toString();
  };

  const data: TimesResult[] = [
    baseRank && {
      algorithm: "Base Rank",
      timeTaken: roundOff(baseRank.timeTaken),
    },
    rationalEntailment && {
      algorithm: "Rational Closure",
      timeTaken: roundOff(rationalEntailment.timeTaken),
    },
    lexicalEntailment && {
      algorithm: "Lexicographic Closure",
      timeTaken: roundOff(lexicalEntailment.timeTaken),
    },
    basicRelevantEntailment && {
      algorithm: "Basic Relevant Closure",
      timeTaken: roundOff(basicRelevantEntailment.timeTaken),
    },
    minimalRelevantEntailment && {
      algorithm: "Minimal Relevant Closure",
      timeTaken: roundOff(minimalRelevantEntailment.timeTaken),
    },
  ].filter((item): item is TimesResult => Boolean(item));

  return <DataTable columns={timesColumns} data={data} />;
}
export { EntailmentTable, TimesTable };
