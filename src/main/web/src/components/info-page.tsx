import { Syntax } from "./Syntax";
import {
  Accordion,
  AccordionContent,
  AccordionItem,
  AccordionTrigger,
} from "./ui/accordion";

export function InfoPage() {
  return (
    <div>
      <Accordion
        type="single"
        collapsible
        className="w-full"
        defaultValue="item-1"
      >              
        <AccordionItem value="item-1">
          <AccordionTrigger>Syntax</AccordionTrigger>
          <AccordionContent className="flex flex-col gap-4 text-balance">
            <Syntax />
          </AccordionContent>
        </AccordionItem>        
      </Accordion>
    </div>
  );
}
