import { Syntax } from "./Syntax";
import {
  Accordion,
  AccordionContent,
  AccordionItem,
  AccordionTrigger,
} from "./ui/accordion";
import penguin from "@/assets/penguin.png";

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
          <AccordionTrigger>Introduction</AccordionTrigger>
          <AccordionContent className="flex flex-col gap-4 text-balance">
            <p>
              Lorem ipsum dolor sit amet consectetur adipisicing elit. Et minus,
              quos amet ipsum facere, cupiditate perferendis eveniet error a
              ducimus quis? Temporibus nesciunt repellendus labore quam quo,
              fuga iure corrupti.
            </p>
            <p>
              <img
                src={penguin}
                alt="A penguin with an umbrella."
                className="w-96"
              />
            </p>
            <p>
              Lorem, ipsum dolor sit amet consectetur adipisicing elit. Optio
              nulla nisi fugiat fuga, quaerat dolorem consectetur, earum quod
              excepturi nemo itaque libero omnis. Facilis expedita cupiditate,
              iste sint placeat laboriosam! Lorem ipsum dolor sit, amet
              consectetur adipisicing elit. Quaerat eius vero eos quisquam
              similique eaque odio, nemo qui facilis animi voluptatibus sunt
              dolor sequi rerum corporis nihil provident blanditiis architecto.
            </p>
          </AccordionContent>
        </AccordionItem>
        <AccordionItem value="item-2">
          <AccordionTrigger>Syntax</AccordionTrigger>
          <AccordionContent className="flex flex-col gap-4 text-balance">
            <Syntax />
          </AccordionContent>
        </AccordionItem>
        <AccordionItem value="item-3">
          <AccordionTrigger>Third</AccordionTrigger>
          <AccordionContent className="flex flex-col gap-4 text-balance">
            <p>
              Lorem ipsum dolor sit amet, consectetur adipisicing elit. Nihil
              quaerat non molestiae tempore sed nulla, distinctio consequatur
              excepturi veritatis rem nemo maxime maiores nobis dolor doloremque
              provident laborum. Ducimus, inventore.
            </p>
          </AccordionContent>
        </AccordionItem>
      </Accordion>
    </div>
  );
}
