import { Algorithm, IEvaluationData } from "@/lib/models";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "../ui/table";
import { cn } from "@/lib/utils";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "../ui/card";

interface EvaluationTableProps {
  caption: string;
  data: IEvaluationData[];
  className?: string;
}
export function EvaluationTable({
  data,
  caption,
  className,
}: EvaluationTableProps) {
  const labels: string[] = [];
  if (data.length > 0) {
    labels.push("Query Set");

    const results: Record<Algorithm, number> = data[0].results;

    if (!isNaN(results.Naive)) {
      labels.push("Naive");
    }

    if (!isNaN(results.Binary)) {
      labels.push("Binary");
    }

    if (!isNaN(results.Ternary)) {
      labels.push("Ternary");
    }

    if (!isNaN(results.PowerSet)) {
      labels.push("Power Set");
    }
  }
  return (
    <Card className={cn(className)}>
      <CardHeader>
        <CardTitle>{caption}</CardTitle>
        <CardDescription>
          Table of average execution times per algorithm.
        </CardDescription>
      </CardHeader>
      <CardContent>
        <div className="flex-grow border rounded-lg pb-4">
          <Table>
            <TableHeader className="[&_tr]:border-b-2">
              <TableRow>
                {labels.map((label: string, index: number) => (
                  <TableHead
                    key={index}
                    className={cn({ "text-right": index !== 0 }, "px-4")}
                  >
                    {label}
                  </TableHead>
                ))}
              </TableRow>
            </TableHeader>
            <TableBody>
              {data.map((item, index) => (
                <TableRow key={index} className="[&_td]:px-4">
                  <TableCell>{item.querySetLabel}</TableCell>
                  {!isNaN(item.results.Naive) && (
                    <TableCell className="text-right">
                      {item.results.Naive}
                    </TableCell>
                  )}
                  {!isNaN(item.results.Binary) && (
                    <TableCell className="text-right">
                      {item.results.Binary}
                    </TableCell>
                  )}
                  {!isNaN(item.results.Ternary) && (
                    <TableCell className="text-right">
                      {item.results.Ternary}
                    </TableCell>
                  )}
                  {!isNaN(item.results.PowerSet) && (
                    <TableCell className="text-right">
                      {item.results.PowerSet}
                    </TableCell>
                  )}
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </div>
      </CardContent>
    </Card>
  );
}
