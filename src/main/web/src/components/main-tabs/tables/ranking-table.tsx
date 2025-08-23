import { TexFormula } from "@/components/main-tabs/common/TexFormula";
import { Button } from "@/components/ui/button";
import { DataTable } from "@/components/ui/data-table";
import { Ranking, ConstantValues } from "@/lib/models";
import { ColumnDef } from "@tanstack/react-table";
import { ArrowUpDown } from "lucide-react";
import { Kb } from "../common/formulas";
//import { toTex } from "@/lib/formula";

const formulas = (label: string): ColumnDef<Ranking> => ({
  accessorKey: "formulas",
  header: () => {
    if (!label) return "Fomulas";
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
    headerClassName: "min-w-max w-full", // min-width, taking
    cellClassName: "whitespace-nowrap",
  },
  filterFn: "includesString",
});

const rankColumns: ColumnDef<Ranking>[] = [
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
        <TexFormula>{idx == ConstantValues.INFINITY_RANK_NUMBER ? "\\infty" : idx.toString()}</TexFormula>
      );
    },
    filterFn: "weakEquals",
  },
  formulas(""),
];

const sequenceColumns: ColumnDef<Ranking>[] = [
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
      return (
        <TexFormula>{idx == ConstantValues.INFINITY_RANK_NUMBER ? "\\infty" : idx.toString()}</TexFormula>
      );
    },
    filterFn: "weakEquals",
  },
  formulas("*_i^\\mathcal{K}"),
];

function RankingTable({
  ranking,
  caption = "",
}: {
  ranking: Ranking[];
  caption?: string;
}) {
  
  return (
    <DataTable
      columns={rankColumns}
      data={ranking.sort((a, b) => b.rankNumber - a.rankNumber)}
      filter={ranking.length != 0}
      caption={caption}
    />
  );
}

function RankingOfRanksTable({
  ranking,
  caption = "",
}: {
  ranking: Ranking[];
  caption?: string;
}) {
  const rankingData: Ranking[] = [];

  let index = 0;

  ranking.forEach((item) => {
    const existingRank = rankingData.find((r) => r.rankNumber === item.rankNumber);

    if (existingRank) {
      index++;
      const rankFormulas = `A*${item.rankNumber}.${index}*A:* ${item.formulas} *:`;
      existingRank.formulas.push(rankFormulas);
    } else {
      index = 0;
      const rankFormulas = `A*${item.rankNumber}.${index}*A:* ${item.formulas} *:`;
      rankingData.push({ rankNumber: item.rankNumber, formulas: [rankFormulas] });
    }
  });
 
  return (
    <DataTable
      columns={rankColumns}
      data={rankingData.sort((a, b) => b.rankNumber - a.rankNumber)}
      filter={rankingData.length !== 0}
      caption={caption}
    />
  );
}


function SequenceTable({
  ranking,
  caption = "",
}: {
  ranking: Ranking[];
  caption?: string;
}) {
  return (
    <DataTable
      columns={sequenceColumns}
      data={ranking.sort((a, b) => b.rankNumber - a.rankNumber)}
      filter={ranking.length != 0}
      caption={caption}
      filters={[
        { id: "rankNumber", search: "filter index . . ." },
        { id: "formulas", search: "filter statements . . ." },
      ]}
    />
  );
}

export { RankingTable, RankingOfRanksTable, SequenceTable };
