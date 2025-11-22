import { TexFormula } from "@/components/main-tabs/common/TexFormula";
import { Button } from "@/components/ui/button";
import { DataTable } from "@/components/ui/data-table";
import { Ranking, ConstantValues } from "@/lib/models";
import { ColumnDef } from "@tanstack/react-table";
import { ArrowUpDown } from "lucide-react";
import { Kb } from "../common/formulas";

// -------------------------------
// Helper: Convert rank to TeX
// -------------------------------
const rankToTex = (idx: number, symbol = "R") => {
  const base = `\\mathcal{${symbol}}`;
  return idx === ConstantValues.INFINITY_RANK_NUMBER
    ? `${base}_{\\infty}`
    : `${base}_{${idx.toString()}}`;
};

// -------------------------------
// Formulas column
// -------------------------------
const formulas = (label: string): ColumnDef<Ranking> => ({
  accessorKey: "formulas",
  header: () => {
    if (!label) return "Formulas or Statements";
    return (
      <span>
        Statements (<TexFormula>{label}</TexFormula>)
      </span>
    );
  },
  cell: ({ row }) => {
    const formulas = row.getValue<string[]>("formulas");
    return <Kb formulas={formulas} />;
  },
  meta: {
    headerClassName: "min-w-max w-full",
    cellClassName: "whitespace-nowrap",
  },
  filterFn: "includesString",
});

// -------------------------------
// Factory: Ranking columns with dynamic symbol
// -------------------------------
const createRankColumns = (symbol = "R"): ColumnDef<Ranking>[] => [
  {
    accessorKey: "rankNumber",
    header: ({ column }) => (
      <Button
        variant="ghost"
        onClick={() => column.toggleSorting(column.getIsSorted() === "asc")}
      >
        Rank
        <ArrowUpDown className="ml-2 h-4 w-4" />
      </Button>
    ),
    meta: {
      headerClassName: "max-w-[200px] text-center",
      cellClassName: "text-center",
    },
    cell: ({ row }) => {
      const idx = row.getValue("rankNumber") as number;
      return <TexFormula>{rankToTex(idx, symbol)}</TexFormula>;
    },
    filterFn: "weakEquals",
  },
  formulas(""),
];

// -------------------------------
// Factory: Sequence columns with dynamic symbol
// -------------------------------
const createSequenceColumns = (symbol = "R"): ColumnDef<Ranking>[] => [
  {
    accessorKey: "rankNumber",
    header: ({ column }) => (
      <Button
        variant="ghost"
        onClick={() => column.toggleSorting(column.getIsSorted() === "asc")}
      >
        Index (<TexFormula>i</TexFormula>)
        <ArrowUpDown className="ml-2 h-4 w-4" />
      </Button>
    ),
    meta: {
      headerClassName: "max-w-[200px] text-center",
      cellClassName: "text-center",
    },
    cell: ({ row }) => {
      const idx = row.getValue("rankNumber") as number;
      return <TexFormula>{rankToTex(idx, symbol)}</TexFormula>;
    },
    filterFn: "weakEquals",
  },
  formulas("*_i^\\mathcal{K}"),
];

// -------------------------------
// RankingTable
// -------------------------------
function RankingTable({
  ranking,
  caption = "",
  symbol = "R",
}: {
  ranking: Ranking[];
  caption?: string;
  symbol?: string;
}) {
  return (
    <DataTable
      columns={createRankColumns(symbol)}
      data={[...ranking].sort((a, b) => b.rankNumber - a.rankNumber)}
      filter={ranking.length !== 0}
      caption={caption}
    />
  );
}

// -------------------------------
// RankingTableWithout (with sorting + discard strikeout)
// -------------------------------
function RankingTableWithout({
  ranking,
  sortOrder = "desc",
  symbol = "R",
}: {
  ranking: Ranking[];
  sortOrder?: "asc" | "desc";
  symbol?: string;
}) {
  const sorted = [...ranking].sort((a, b) =>
    sortOrder === "asc"
      ? a.rankNumber - b.rankNumber
      : b.rankNumber - a.rankNumber
  );

  return (
    <DataTable<Ranking, unknown>
      columns={createRankColumns(symbol)}
      data={sorted}
      filter={ranking.length !== 0}
      rowClassName={(row) =>
        row.original.discarded
          ? "line-through decoration-red-500 decoration-2"
          : ""
      }
    />
  );
}

// -------------------------------
// RankingOfRanksTable
// -------------------------------
function RankingOfRanksTable({
  ranking,
  caption = "",
  symbol = "R",
}: {
  ranking: Ranking[];
  caption?: string;
  symbol?: string;
}) {
  const rankingData: Ranking[] = [];
  let index = 0;

  ranking.forEach((item) => {
    const existingRank = rankingData.find(
      (r) => r.rankNumber === item.rankNumber
    );

    if (existingRank) {
      index++;
      const rankFormulas = `A*${item.rankNumber}.${index}*A:* ${item.formulas} *:`;
      existingRank.formulas.push(rankFormulas);
    } else {
      index = 0;
      const rankFormulas = `A*${item.rankNumber}.${index}*A:* ${item.formulas} *:`;
      rankingData.push({
        rankNumber: item.rankNumber,
        formulas: [rankFormulas],
      });
    }
  });

  return (
    <DataTable
      columns={createRankColumns(symbol)}
      data={rankingData.sort((a, b) => b.rankNumber - a.rankNumber)}
      filter={rankingData.length !== 0}
      caption={caption}
    />
  );
}

// -------------------------------
// SequenceTable
// -------------------------------
function SequenceTable({
  ranking,
  caption = "",
  symbol = "R",
}: {
  ranking: Ranking[];
  caption?: string;
  symbol?: string;
}) {
  return (
    <DataTable
      columns={createSequenceColumns(symbol)}
      data={[...ranking].sort((a, b) => b.rankNumber - a.rankNumber)}
      filter={ranking.length !== 0}
      caption={caption}
      filters={[
        { id: "rankNumber", search: "filter index . . ." },
        { id: "formulas", search: "filter statements . . ." },
      ]}
    />
  );
}

export {
  RankingTable,
  RankingOfRanksTable,
  SequenceTable,
  RankingTableWithout,
};

