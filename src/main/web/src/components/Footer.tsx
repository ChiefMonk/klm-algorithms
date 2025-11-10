import github from "@/assets/github-mark.svg";
import { cn } from "@/lib/utils";

interface FooterProps {
  className?: string;
}

function Footer({ className }: FooterProps) {
  return (
    <footer
      className={cn("text-center flex justify-center p-6 border-t", className)}
    >
      <a
        className="font-semibold flex items-center hover:underline"
        href="https://github.com/chiefmonk/klm-algorithms"
        target="_blank"
      >
        <img src={github} alt="GitHub logo" className="w-6 mr-2" />
        <span>
          KLM-Style Entailment and Explanation Algorithms
        </span>
      </a>
    </footer>
  );
}

export { Footer };
