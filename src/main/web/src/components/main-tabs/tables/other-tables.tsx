import { TexFormula } from "@/components/main-tabs/common/TexFormula";
import { DataTable } from "@/components/ui/data-table";
import { toTex } from "@/lib/formula";
import { BaseRankModel, EntailmentModel, QueryType } from "@/lib/models";
import { cn } from "@/lib/utils";
import { useReasonerContext } from "@/state/reasoner.context";
import { ColumnDef } from "@tanstack/react-table";

interface AlgorithmResult {
  algorithm: string;
  result: string;
  justification: string;
}

interface TimesResult {
  algorithm: string;
  timeTaken: string;
  justifcationTime?: string;
}

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
  const { queryType } = useReasonerContext();

  const getResult = ({ entailed, queryFormula }: EntailmentModel) => {
    return entailed
      ? toTex("\\mathcal{K} \\vapprox " + queryFormula)
      : toTex("\\mathcal{K} \\nvapprox " + queryFormula);
  };

  const getJustification = ({ entailed, justification }: EntailmentModel) => {
    if (entailed && justification.length > 0) {
      const symbol = justification
        .map((item, index) => `\\mathcal{J_{${index + 1}}} = \\{ ${item} \\}`)
        .join(", ");
      return toTex(symbol);
    }
    return toTex("\\mathcal{J_{1}} = \\{ \\}");
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

  const baseColumns: ColumnDef<AlgorithmResult>[] = [
    {
      accessorKey: "algorithm",
      header: "Entailment Algorithm",
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
        headerClassName: cn("min-w-[180px]", {
          "w-full": queryType !== QueryType.Justification,
        }),
        cellClassName: "whitespace-nowrap",
      },
    },
  ];

  const justificationColumn: ColumnDef<AlgorithmResult> = {
    accessorKey: "justification",
    header: "Justification",
    cell: ({ row }) => <TexFormula>{row.getValue("justification")}</TexFormula>,
    meta: {
      headerClassName: "w-full min-w-[180px]",
      cellClassName: "whitespace-nowrap",
    },
  };

  const columns =
    queryType === QueryType.Justification
      ? [...baseColumns, justificationColumn]
      : baseColumns;

  return <DataTable columns={columns} data={data} />;
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
  const { queryType } = useReasonerContext();

  const roundOff = (value: number) =>
    (Math.round(value * 10000) / 10000).toString();

  const data: TimesResult[] = [
    baseRank && {
      algorithm: "Base Rank",
      timeTaken: roundOff(baseRank.timeTaken),
      justificationTimeTaken: undefined,
    },
    rationalEntailment && {
      algorithm: "Rational Closure",
      timeTaken: roundOff(rationalEntailment.timeTaken),
      justificationTimeTaken:
        queryType === QueryType.Justification
          ? roundOff(rationalEntailment.justifcationTime)
          : undefined,
    },
    lexicalEntailment && {
      algorithm: "Lexicographic Closure",
      timeTaken: roundOff(lexicalEntailment.timeTaken),
      justificationTimeTaken:
        queryType === QueryType.Justification
          ? roundOff(lexicalEntailment.justifcationTime)
          : undefined,
    },
    basicRelevantEntailment && {
      algorithm: "Basic Relevant Closure",
      timeTaken: roundOff(basicRelevantEntailment.timeTaken),
      justificationTimeTaken:
        queryType === QueryType.Justification
          ? roundOff(basicRelevantEntailment.justifcationTime)
          : undefined,
    },
    minimalRelevantEntailment && {
      algorithm: "Minimal Relevant Closure",
      timeTaken: roundOff(minimalRelevantEntailment.timeTaken),
      justificationTimeTaken:
        queryType === QueryType.Justification
          ? roundOff(minimalRelevantEntailment.justifcationTime)
          : undefined,
    },
  ].filter(Boolean) as TimesResult[];

  const baseColumns: ColumnDef<TimesResult>[] = [
    {
      accessorKey: "algorithm",
      header: "Entailment Algorithm",
      cell: ({ row }) => row.getValue("algorithm"),
      meta: {
        headerClassName: "min-w-[180px]",
        cellClassName: "whitespace-nowrap",
      },
    },
    {
      accessorKey: "timeTaken",
      header: "Entailment Determination",
      cell: ({ row }) => {
        const value = row.getValue("timeTaken") as string;
        return <TexFormula>{value}</TexFormula>;
      },
      meta: {
        headerClassName: cn("min-w-[180px]", {
          "w-full": queryType !== QueryType.Justification,
        }),
        cellClassName: "whitespace-nowrap",
      },
    },
  ];

  const justificationColumn: ColumnDef<TimesResult> = {
    accessorKey: "justificationTimeTaken",
    header: "Justification Determination",
    cell: ({ row }) => {
      const value = row.getValue("justificationTimeTaken") as
        | string
        | undefined;
      return value ? <TexFormula>{value}</TexFormula> : null;
    },
    meta: {
      headerClassName: "w-full min-w-[180px]",
      cellClassName: "whitespace-nowrap",
    },
  };

  const columns =
    queryType === QueryType.Justification
      ? [...baseColumns, justificationColumn]
      : baseColumns;

  return <DataTable columns={columns} data={data} />;
}

export { EntailmentTable, TimesTable };
