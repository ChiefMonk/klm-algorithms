import { type ClassValue, clsx } from "clsx";
import { twMerge } from "tailwind-merge";
import { Ranking } from "./models";

/**
 * An function to merge class values.
 * 
 * @author Chipo Hamayobe (chipo@cs.uct.ac.za)
 * @version 1.0.1
 * @since 2025-01-26
 */
export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs));
}

/**
 * An function to check if two arrays are equal.
 * 
 * @author Chipo Hamayobe (chipo@cs.uct.ac.za)
 * @version 1.0.1
 * @since 2025-01-26
 */
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

/**
 * An function to get the remaining ranks.
 * 
 * @author Chipo Hamayobe (chipo@cs.uct.ac.za)
 * @version 1.0.1
 * @since 2025-01-26
 */
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
