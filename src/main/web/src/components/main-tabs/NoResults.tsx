import logo from "@/assets/logo2.svg";
import penguin from "@/assets/penguin.png";
import { Logo } from "@/components/ui/logo";

export function NoResults() {
  return (
    <div className="flex flex-col gap-4 items-center">
      <div className="relative">
        <Logo src={logo} className="w-16 absolute bottom-0 left-12" />
        <img src={penguin} alt="A penguin with an umbrella." className="w-96" />
      </div>
      <div className="text-center">
        <h4 className="scroll-m-20 font-bold text-lg tracking-tight">
          No Results Yet.
        </h4>
        <p className="font-semibold text-muted-foreground">
          Please submit a query first.
        </p>
      </div>
    </div>
  );
}
