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
import { ALGORITHMS } from "@/lib/constants";

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
  const algorithms = Object.values(Algorithm);

  if (data.length > 0) {
    labels.push("Query Set");
    const results: Record<Algorithm, number> = data[0].results;

    algorithms.forEach((algo) => {
      if (!isNaN(results[algo])) {
        labels.push(ALGORITHMS.get(algo) || algo);
      }
    });
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
                  {algorithms.map(
                    (algo) =>
                      !isNaN(item.results[algo]) && (
                        <TableCell key={algo} className="text-right">
                          {item.results[algo]}
                        </TableCell>
                      )
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