import { TexFormula } from "@/components/main-tabs/common/TexFormula";
import { Button } from "@/components/ui/button";
import { DataTable } from "@/components/ui/data-table";
import { Ranking } from "@/lib/models";
import { ColumnDef } from "@tanstack/react-table";
import { ArrowUpDown } from "lucide-react";
import { Kb } from "../common/formulas";

const MAX_VALUE = 2147483647;

interface RefinedRanking extends Ranking {
  subsetSize: number;
}

const rankColumns: ColumnDef<RefinedRanking>[] = [
  {
    accessorKey: "rankNumber",
    header: ({ column }) => (
      <Button
        variant="ghost"
        onClick={() => column.toggleSorting(column.getIsSorted() === "asc")}
      >
        Rank Number
        <ArrowUpDown className="ml-2 h-4 w-4" />
      </Button>
    ),
    meta: {
      headerClassName: "max-w-[200px] text-center",
      cellClassName: "text-center",
    },
    cell: ({ row }) => {
      const idx = row.getValue("rankNumber") as number;
      return (
        <TexFormula>{idx == MAX_VALUE ? "\\infty" : idx.toString()}</TexFormula>
      );
    },
    filterFn: "weakEquals",
  },
  {
    accessorKey: "subsetSize",
    header: ({ column }) => (
      <Button
        variant="ghost"
        onClick={() => column.toggleSorting(column.getIsSorted() === "asc")}
      >
        Subset Size
        <ArrowUpDown className="ml-2 h-4 w-4" />
      </Button>
    ),
    meta: {
      headerClassName: "max-w-[200px] text-center",
      cellClassName: "text-center",
    },
    cell: ({ row }) => {
      const idx = row.getValue("subsetSize") as number;
      return (
        <TexFormula>{idx == MAX_VALUE ? "\\infty" : idx.toString()}</TexFormula>
      );
    },
    filterFn: "weakEquals",
  },
  {
    accessorKey: "formulas",
    header: "Fomulas",
    cell: ({ row }) => {
      const formulas = row.getValue<string[]>("formulas");
      return <Kb formulas={formulas} />;
    },
    meta: {
      headerClassName: "min-w-[200px] w-full", // min-width, taking
      cellClassName: "whitespace-nowrap",
    },
    filterFn: "includesString",
  },
];

export function RefinedRankingTable({
  ranks,
  caption = "",
}: {
  ranks: Ranking[];
  caption?: string;
}) {
  const calcSubsetSize = (formula: string) => {
    if (formula.includes("||")) {
      if (formula.includes("&&")) {
        return formula.split("||")[0].split("&&").length;
      }
      return 1;
    }
    return 0;
  };
  const data: RefinedRanking[] = ranks.map((value) => ({
    rankNumber: value.rankNumber,
    formulas: value.formulas,
    subsetSize: calcSubsetSize(value.formulas[0]),
  }));
  return (
    <DataTable
      columns={rankColumns}
      data={data}
      filter={data.length != 0}
      filters={[
        { id: "subsetSize", search: "Search subset size..." },
        { id: "formulas", search: "Search formulas..." },
      ]}
      caption={caption}
    />
  );
}
