import {
  Table,
  TableBody,
  TableCaption,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import { Formula } from "./main-tabs/common/formulas";
import { toTex } from "@/lib/formula";

const symbols = [
  {
    name: "Negation",
    plainTextSymbol: "!",
    mathSymbol: toTex("!"),
    plainTextExample: "!p",
    mathExample: toTex("!p"),
  },
  {
    name: "Conjunction",
    plainTextSymbol: "&&",
    mathSymbol: toTex("&&"),
    plainTextExample: "p && q",
    mathExample: toTex("p && q"),
  },
  {
    name: "Disjunction",
    plainTextSymbol: "||",
    mathSymbol: toTex("||"),
    plainTextExample: "p || q",
    mathExample: toTex("p || q"),
  },
  {
    name: "Implication",
    plainTextSymbol: "=>",
    mathSymbol: toTex("=>"),
    plainTextExample: "p => q",
    mathExample: toTex("p => q"),
  },
  {
    name: "Defeasible Implication",
    plainTextSymbol: "~>",
    mathSymbol: toTex("~>"),
    plainTextExample: "p ~> q",
    mathExample: toTex("p ~> q"),
  },
  {
    name: "Equivalence",
    plainTextSymbol: "<=>",
    mathSymbol: toTex("<=>"),
    plainTextExample: "p <=> q",
    mathExample: toTex("p <=> q"),
  },
];

export function Syntax() {
  return (
    <Table className="overflow-x-auto border-collapse border">
      <TableCaption>Boolean connectives.</TableCaption>
      <TableHeader>
        <TableRow>
          <TableHead rowSpan={2} className="min-w-40 border">
            Name
          </TableHead>
          <TableHead colSpan={2} className="text-center border">
            Symbol
          </TableHead>
          <TableHead colSpan={2} className="text-center border">
            Example
          </TableHead>
        </TableRow>
        <TableRow>
          <TableHead className="text-center min-w-24 border">Text</TableHead>
          <TableHead className="text-center min-w-24 border">Math</TableHead>
          <TableHead className="text-center min-w-24 border">Text</TableHead>
          <TableHead className="text-center min-w-24 border">Math</TableHead>
        </TableRow>
      </TableHeader>
      <TableBody>
        {symbols.map((symbol) => (
          <TableRow key={symbol.name}>
            <TableCell className="border">{symbol.name}</TableCell>
            <TableCell className="text-center border">
              <code>{symbol.plainTextSymbol}</code>
            </TableCell>
            <TableCell className="text-center border">
              <Formula formula={symbol.mathSymbol} />
            </TableCell>
            <TableCell className="text-center border">
              <code>{symbol.plainTextExample}</code>
            </TableCell>
            <TableCell className="text-center border">
              <Formula formula={symbol.mathExample} />
            </TableCell>
          </TableRow>
        ))}
      </TableBody>
    </Table>
  );
}
