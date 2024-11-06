import { type ClassValue, clsx } from "clsx";
import { twMerge } from "tailwind-merge";
import { Ranking } from "./models";

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs));
}

export function arrayEquals(array1: unknown[], array2: unknown[]) {
  if (array1.length == array2.length) {
    const n = array1.length;
    for (let i = 0; i < n; i++) {
      if (array1[i] !== array2[i]) {
        return false;
      }
    }
    return true;
  }
  return false;
}

export function remainingRanks(baseRanks: Ranking[], removedRanks: Ranking[]) {
  if (removedRanks.length == 0) return baseRanks;
  const n = removedRanks.length;
  const m = baseRanks.length;
  const ranks: Ranking[] = [];
  for (let i = n - 1; i < m; i++) {
    if (i < n) {
      if (baseRanks[i].rankNumber == removedRanks[i].rankNumber) {
        if (!arrayEquals(baseRanks[i].formulas, removedRanks[i].formulas)) {
          const formulas = baseRanks[i].formulas.filter(
            (formula) => !removedRanks[i].formulas.includes(formula)
          );
          ranks.push({ ...baseRanks[i], formulas });
        }
      }
    } else {
      ranks.push(baseRanks[i]);
    }
  }

  return ranks;
}
