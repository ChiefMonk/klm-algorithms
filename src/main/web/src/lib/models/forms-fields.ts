/**
 * An interface representing a form field option.
 * 
 * @author Chipo Hamayobe (chipo@cs.uct.ac.za)
 * @version 1.0.1
 * @since 2025-01-26
 */
export interface IFormFieldOption<T> {
  label: string;
  value: T;
}
