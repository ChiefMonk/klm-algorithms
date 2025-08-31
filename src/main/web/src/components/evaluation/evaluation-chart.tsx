"use client";
import { useCurrentPng } from "recharts-to-png";
import { LineChart, Line, XAxis, YAxis, CartesianGrid } from "recharts";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import {
  ChartConfig,
  ChartContainer,
  ChartLegend,
  ChartLegendContent,
  ChartTooltip,
  ChartTooltipContent,
  CustomTooltipProps,
} from "@/components/ui/chart";

import { IEvaluationData, Algorithm } from "@/lib/models";
import { cn } from "@/lib/utils";
import { useCallback } from "react";
import { saveAs } from "file-saver";
import { Button } from "../ui/button";
import {
  Circle,
  Diamond,
  HardDriveDownload,
  LoaderCircle,
  Square,
  Triangle,
} from "lucide-react";
import { timestampFilename } from "@/lib/utils/file-name";

interface EvaluationChartProps {
  data: IEvaluationData[];
  className?: string;
  label?: string;
}

const ALL_ALGORITHMS: Algorithm[] = [
  Algorithm.Naive,
  Algorithm.Binary,
  Algorithm.Ternary,
  Algorithm.PowerSet,
];

export function EvaluationChart({
  data,
  className,
  label = "klm_algorithms_evaluation_chart",
}: EvaluationChartProps) {
  const [getPng, { ref, isLoading }] = useCurrentPng();

  const handleDownload = useCallback(async () => {
    const png = await getPng();
    if (png) {
      saveAs(png, timestampFilename(label, "png"));
    }
  }, [getPng, label]);

  if (data.length === 0) return null;

  // 1. Generate chart data
  const chartData = data.map((item) => ({
    name: item.querySetLabel,
    ...item.results,
  }));

  // 2. Determine which algorithms actually exist in the data
  const presentAlgos = new Set<Algorithm>();
  data.forEach((row) => {
    ALL_ALGORITHMS.forEach((algo) => {
      if (!isNaN(row.results[algo])) {
        presentAlgos.add(algo);
      }
    });
  });

  // 3. Build chartConfig for only present algorithms
  const chartConfig: ChartConfig = {};

  const algoColors = {
    [Algorithm.Naive]: "--chart-1",
    [Algorithm.Binary]: "--chart-2",
    [Algorithm.Ternary]: "--chart-3",
    [Algorithm.PowerSet]: "--chart-4",
  };

  Array.from(presentAlgos).forEach((algo) => {
    chartConfig[algo] = {
      label: algo,
      color: `hsl(var(${algoColors[algo]}))`,
    };
  });

  const algoIcons = {
    [Algorithm.Naive]: Square,
    [Algorithm.Binary]: Circle,
    [Algorithm.Ternary]: Diamond,
    [Algorithm.PowerSet]: Triangle,
  };

  return (
    <Card className={cn(className)}>
      <CardHeader className="relative">
        <CardTitle>{data[0].inferenceOperator}</CardTitle>
        <CardDescription>
          Chart of average execution times per algorithm.
        </CardDescription>
        <Button
          className="absolute right-4 top-4"
          variant="secondary"
          onClick={handleDownload}
        >
          {!isLoading && (
            <>
              <HardDriveDownload className="mr-2 w-6" />
              Download
            </>
          )}
          {isLoading && (
            <>
              <LoaderCircle className="mr-2 w-6 animate-spin" />
              Downloading...
            </>
          )}
        </Button>
      </CardHeader>

      <CardContent className="flex flex-col gap-8">
        <ChartContainer config={chartConfig}>
          <LineChart
            data={chartData}
            margin={{ top: 20, right: 20, left: 8, bottom: 5 }}
            ref={ref}
          >
            <CartesianGrid strokeDasharray="3 3" vertical={false} />
            <XAxis
              dataKey="name"
              tickLine={false}
              axisLine={false}
              tickMargin={8}
            />
            <YAxis
              label={{
                value: "Execution Time (ms)",
                angle: -90,
                position: "insideLeft",
              }}
              tickLine={false}
              axisLine={false}
              tickMargin={8}
            />

            <ChartTooltip
              cursor={false}
              content={(props: CustomTooltipProps) => (
                <ChartTooltipContent {...props} hideIndicator hideLabel />
              )}
            />

            {Object.keys(chartConfig).map((key) => (
              <Line
                key={key}
                type="linear"
                dataKey={key}
                stroke={chartConfig[key].color}
                strokeWidth={2}
                dot={({ cx, cy }) => {
                  const r = 12;
                  const AlgoIcon = algoIcons[key as Algorithm] ?? Circle;
                  return (
                    <AlgoIcon
                      key={key}
                      x={cx - r / 2}
                      y={cy - r / 2}
                      width={r}
                      height={r}
                      fill={`hsl(var(${algoColors[key as Algorithm]}))`}
                      stroke={`hsl(var(${algoColors[key as Algorithm]}))`}
                    />
                  );
                }}
                activeDot={({ cx, cy }) => {
                  const r = 16;
                  const AlgoIcon = algoIcons[key as Algorithm] ?? Circle;
                  return (
                    <AlgoIcon
                      key={key}
                      x={cx - r / 2}
                      y={cy - r / 2}
                      width={r}
                      height={r}
                      fill={`hsl(var(${algoColors[key as Algorithm]}))`}
                      stroke={`hsl(var(${algoColors[key as Algorithm]}))`}
                    />
                  );
                }}
              />
            ))}

            <ChartLegend content={<ChartLegendContent />} />
          </LineChart>
        </ChartContainer>
      </CardContent>
    </Card>
  );
}
