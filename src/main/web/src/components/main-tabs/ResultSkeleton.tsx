import { Skeleton } from "@/components/ui/skeleton";

function ResultSkeleton() {
  return (
    <div>
      <Skeleton className="h-6 w-full mb-4" />
      <Skeleton className="h-6 w-20 mb-6" />
      <Skeleton className="h-48 w-full" />
    </div>
  );
}

export { ResultSkeleton };
