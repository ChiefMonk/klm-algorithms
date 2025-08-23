/**
 * Pads a number or string with a leading zero if needed.
 *
 * @param {string | number} n - The number or string to pad.
 * @returns {string} A zero-padded string of length 2.
 *
 * @example
 * pad(5); // "05"
 * pad("9"); // "09"
 */
function pad(n: string | number): string {
  return String(n).padStart(2, "0");
}

/**
 * Sanitizes a filename so it is safe for most filesystems.
 * - Trims whitespace
 * - Replaces spaces with underscores
 * - Removes all characters except letters, numbers, underscores, and hyphens
 *
 * @param {string} name - The filename to sanitize.
 * @returns {string} The sanitized filename.
 *
 * @example
 * sanitizeFilename("My File!.txt"); // "My_Filetxt"
 */
function sanitizeFilename(name: string): string {
  return name
    .trim()
    .replace(/\s+/g, "_") // Replace spaces with underscores
    .replace(/[^a-zA-Z0-9_-]/g, ""); // Remove anything not alphanumeric, _ or -
}

/**
 * Generates a filesystem-safe filename with a timestamp in the format: YYYYMMDD_HHMMSS
 *
 * @param {string} [base="file"] - The base name of the file (unsanitized).
 * @param {string} [ext="txt"] - The file extension without the dot.
 * @returns {string} A sanitized filename with timestamp.
 *
 * @example
 * timestampFilename("Report", "log");
 * // "Report_20250812_203015.log"
 *
 * @example
 * timestampFilename("My Report!");
 * // "My_Report_20250812_203015.txt"
 */
function timestampFilename(base: string = "file", ext: string = "txt"): string {
  const safeBase = sanitizeFilename(base);
  const d = new Date();
  const datePart = [
    d.getFullYear(),
    pad(d.getMonth() + 1),
    pad(d.getDate()),
  ].join("");

  const timePart = [
    pad(d.getHours()),
    pad(d.getMinutes()),
    pad(d.getSeconds()),
  ].join("");

  return `${safeBase}_${datePart}_${timePart}.${ext}`;
}

export { sanitizeFilename, timestampFilename };
