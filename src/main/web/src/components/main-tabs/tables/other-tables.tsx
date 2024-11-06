import { TexFormula } from "@/components/main-tabs/common/TexFormula";
import { DataTable } from "@/components/ui/data-table";
import { toTex } from "@/lib/formula";
import { BaseRankModel, EntailmentModel } from "@/lib/models";
import { ColumnDef } from "@tanstack/react-table";

interface AlgorithmResult {
  algorithm: string;
  result: string;
}

interface TimesResult {
  algorithm: string;
  timeTaken: string;
}

const entailmentColumns: ColumnDef<AlgorithmResult>[] = [
  {
    accessorKey: "algorithm",
    header: "Algorithm",
    cell: ({ row }) => row.getValue("algorithm"),
    meta: {
      cellClassName: "whitespace-nowrap",
    },
  },
  {
    accessorKey: "result",
    header: "Result",
    cell: ({ row }) => <TexFormula>{row.getValue("result")}</TexFormula>,
    meta: {
      headerClassName: "w-full",
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
}

function EntailmentTable({
  rationalEntailment,
  lexicalEntailment,
}: EntailmentTableProps) {
  const getResult = ({ entailed, queryFormula }: EntailmentModel) => {
    return entailed
      ? toTex("\\mathcal{K} \\vapprox " + queryFormula)
      : toTex("\\mathcal{K} \\nvapprox " + queryFormula);
  };
  return (
    <DataTable
      columns={entailmentColumns}
      data={[
        {
          algorithm: "Rational Closure",
          result: getResult(rationalEntailment),
        },
        {
          algorithm: "Lexicographic Closure",
          result: getResult(lexicalEntailment),
        },
      ]}
    />
  );
}

interface TimesTableProps {
  baseRank: BaseRankModel;
  rationalEntailment: EntailmentModel;
  lexicalEntailment: EntailmentModel;
}

function TimesTable({
  baseRank,
  rationalEntailment,
  lexicalEntailment,
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
      ]}
    />
  );
}
export { EntailmentTable, TimesTable };
