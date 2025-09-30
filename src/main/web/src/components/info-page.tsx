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
          <AccordionContent className="flex flex-col gap-4 text-balance text-lg md:text-xl leading-relaxed">      
      <h1>Knowledge Representation &amp; Reasoning: Overview</h1>

      <p>
        Artificial Intelligence (AI) systems must frequently perform reasoning tasks across diverse problem
        domains, wherein they derive new conclusions from pre-established beliefs. This process, central to AI, is
        rigorously studied within the subfield of Knowledge Representation and Reasoning (KRR) <sup>[8]</sup>, which
        employs formal logic to encode domain knowledge symbolically. McCarthy <sup>[48]</sup> first proposed this
        declarative paradigm in the 1950s, offering a clear alternative to the dominant statistical and
        data-driven methodologies of today. KRR adopts a formal, often philosophical, approach to simulating
        intelligence, enabling precise inference and systematic analysis of knowledge derived from explicit
        representations <sup>[4]</sup>. These representations are organised within a structured repository known as a
        knowledge base <sup>[28]</sup>. At the core of KRR lies the knowledge-based system, where symbolic information
        populates the knowledge base, and formally defined reasoning services operate over it to infer new insights
        <sup>[8]</sup>.
      </p>

      <p>
        Classical Propositional Logic <sup>[4]</sup> offers a simple, yet expressive foundational framework for reasoning
        in KRR. This reasoning paradigm, known as classical reasoning, is grounded in Tarskian <sup>[65]</sup> notions of
        consequence, which impose strict formal criteria ensuring that every inference derives maximal support
        from the explicitly represented knowledge <sup>[53]</sup>. The principles and insights gained from this framework
        can serve as a starting point for studying more expressive and complex languages such as Description Logics
        <sup>[15]</sup> and First-Order Logic <sup>[28]</sup>. However, despite its strengths, a critical shortcoming of Propositional
        Logic is its inability to accommodate exceptions within the domain knowledge. Classical consequence
        relations operate uniformly and do not distinguish between general rules and their exceptions <sup>[15]</sup>.
      </p>

      <p>
        For instance, the general rule that animals have legs and hunt admits the exceptional case of domesticated
        cats, animals that do not hunt. In Propositional Logic, we can formalise such knowledge as follows:
      </p>

      <ol>
        <li><code>animals → legs</code></li>
        <li><code>animals → hunt</code></li>
        <li><code>cats → animals</code></li>
        <li><code>cats → legs</code></li>
        <li><code>cats → ¬hunt</code></li>
      </ol>

      <p>
        Using classical inference rules, this information explicitly says <code>cats → ¬hunt</code>, yet also implies
        <code> cats → hunt</code>, since <code>cats → animals</code> and <code>animals → hunt</code>. While this may seem
        contradictory, it is not a strict logical contradiction. Rather, it reflects an inconsistency in the knowledge
        base: either animals are assigned incompatible traits, or the system effectively rules out the existence of such
        animals. Although modifying the knowledge base to handle exceptions like cats is possible, each new exception,
        such as dogs, demands further adjustments. Consequently, the more exceptions there are, the larger and more
        difficult the knowledge base becomes to manage. With extensive knowledge bases containing hundreds or
        thousands of statements, systematically handling these exceptions by explicit adjustments quickly becomes
        impractical. Under Propositional Logic, such exceptions cannot be seamlessly integrated without undermining
        consistency, highlighting the inadequacy of classical logic for modelling nuanced, human-like understanding of
        the world.
      </p>

      <p>
        The problem of exceptions arises directly because classical reasoning follows the principle of monotonicity.
        Monotonicity requires that any inference valid within a subset of statements must remain valid when more
        statements are added. In other words, expanding a knowledge base can only produce additional inferences; it
        cannot eliminate any inferences already made, even if they are potentially contradictory. Thus, monotonicity
        ensures that from statements like <code>animals → hunt</code> and <code>cats → animals</code>, the inference
        <code> cats → hunt</code> always follows, irrespective of other information. Yet, the ability to revise beliefs in light
        of new information remains fundamental to any realistic model of human-like reasoning. To address this issue,
        non-monotonic reasoning is proposed as a solution.
      </p>

      <h2>Non-Monotonic Reasoning and the KLM Approach</h2>

      <p>
        Non-monotonic reasoning explores formalising “common-sense” reasoning by discarding monotonicity and
        investigating alternative, non-Tarskian notions of consequence. Humans typically reason by making assumptions
        based on their current knowledge, adjusting these assumptions as new information becomes available <sup>[63]</sup>.
        Various frameworks developed primarily during the 1980s and 1990s emulate this approach to defeasible
        reasoning. This dissertation focuses specifically on the KLM Framework or Approach, introduced by Kraus,
        Lehmann, and Magidor (KLM) <sup>[44, 46, 47]</sup>. While initially formulated in Propositional Logic, the framework
        has since been extended to more expressive logics like Description Logics <sup>[15, 10, 11]</sup> and Modal Logics
        <sup>[50]</sup>. The KLM Approach defines a set of properties intended to characterise a rational notion of defeasible
        entailment.
      </p>

      <p>
        One notable strength of this approach is that it provides two complementary perspectives: a series of intuitive
        postulates describing expected reasoning behaviours, and a model-theoretic semantics more suitable for
        computational reasoning algorithms. This dissertation emphasises the model-theoretic semantic viewpoint,
        although these two approaches are connected through established results <sup>[47, 29, 16]</sup>. Key defeasible
        entailment formalisms within the KLM Framework discussed in the literature include Rational Closure (RatC)
        <sup>[47]</sup>, Lexicographic Closure (LexC) <sup>[46]</sup> and Relevant Closure (RelC) <sup>[14]</sup>. Rational Closure and
        Lexicographic Closure are considered rational because both can be fully described syntactically; Rational
        Closure is the most conservative (with respect to subset inclusion) while Lexicographic Closure allows greater
        flexibility <sup>[16]</sup>. Relevant Closure, first introduced for Description Logics, appears intuitively rational but
        does not satisfy all of the original KLM postulates.
      </p>

      <h2>Explaining Defeasible Reasoning</h2>

      <p>
        Technologies can now integrate reasoning services or function as independent reasoners. However, the
        complexity and volume of knowledge involved in defeasible reasoning often make the resulting inferences
        hard to interpret <sup>[5]</sup>. While classical logic has a strong foundation in explanation, work on explaining
        defeasible reasoning remains limited. Regardless of the logic type, systems increasingly need to justify
        their conclusions, driven by growing demands for transparency and interoperability, and there are several
        reasons, as explained by Horridge <sup>[33]</sup>:
      </p>

      <ol>
        <li>
          <strong>Knowledge base interpretation across user expertise:</strong> Users of knowledge-based systems differ
          in their familiarity with the domain. For instance, a system that generates recommendations or
          classifications based on structured inputs might produce results clear to a domain expert, who can
          easily align the output with their reasoning. However, non-expert users may find these conclusions
          difficult to interpret or trust without explicit justification.
        </li>
        <li>
          <strong>Knowledge bases are dynamic:</strong> Knowledge bases continually grow or change, becoming
          increasingly complex. Even domain experts may struggle to understand how conclusions are drawn
          from large, intricate ontologies that contain thousands of logical rules. Consequently, transparency
          and trust require systems not only to provide accurate answers but also clear justifications. This
          reasoning process can be defined algorithmically and implemented computationally.
        </li>
      </ol>

      <p>
        One proposed solution is to provide logical explanations in the form of a justification, as defined by
        Schlobach and Cornet <sup>[62]</sup>. A justification identifies the minimal set of statements required to support
        a specific conclusion, helping users understand how that conclusion was reached <sup>[40]</sup>. For example,
        given the knowledge base about cats above, if we ask whether <code>cats → legs</code> follows, we find it is
        explicitly stated (i.e., statement 4), forming one justification: <code>{'{cats \u2192 legs}'}</code>. Alternatively, we can
        infer it implicitly through statements 1 and 3, <code>{'{animals \u2192 legs, cats \u2192 animals}'}</code>, which also
        constitutes a justification set for the assertion. Each set constitutes the minimal collection of statements
        that, if reduced in any way, would no longer suffice to support the conclusion and thus would fail to justify
        <code>cats → legs</code>.
      </p>

      <p>
        This dissertation provides an overview of justifications, starting from their origins in Reiter’s theory
        of model-based diagnosis <sup>[60]</sup>, examining their computation in classical logic, and explaining why they
        cannot directly apply to defeasible reasoning. Although most research on justifications, such as the works
        of Horridge <sup>[33]</sup> and Chama <sup>[17]</sup>, focuses on Description Logics, this dissertation primarily addresses
        Propositional Logic. Specifically, we define and investigate defeasible justifications within the KLM Framework,
        covering all the inference operators: Rational Closure, Lexicographic Closure, and Relevant Closure.
        Additionally, we review the adaptation by Wang et&nbsp;al. <sup>[68]</sup> of Chama’s justification algorithm for Rational
        Closure, and the subsequent extensions by Everett et&nbsp;al. <sup>[23]</sup> to Lexicographic Closure and Relevant Closure.
      </p>

      <h2>System Design &amp; Implementation</h2>

      <p>
        We also present the design and implementation of a comprehensive software system for computing defeasible
        entailment and justification within the context of the KLM Approach, building upon foundational research and
        algorithms. The system aims to operationalise the computation of minimal justification sets necessary to
        elucidate defeasible entailments clearly and effectively. To achieve this, the software architecture incorporates
        modular reasoning components, each tailored to the unique logical characteristics and computational
        complexity associated with the respective inference operators. Particular attention is devoted to algorithms
        ensuring optimal extraction of justifications, handling the inherent non-monotonicity of defeasible reasoning.
        The implemented system further emphasises usability and transparency by providing explicit traceability of
        inference processes, thereby facilitating deeper insights into the logical dependencies underlying conclusions.
        Consequently, the resulting framework not only bridges theoretical developments with practical applicability
        but also contributes significantly to the growing demand for interpretability in Artificial Intelligence reasoning
        systems <sup>[9]</sup>.
      </p>

      <h2>Empirical Evaluation</h2>

      <p>
        Our work also undertakes a rigorous evaluation of the entailment and justification algorithms implemented
        for Rational Closure, Lexicographic Closure, and Relevant Closure, focusing on their performance across
        knowledge bases of varying sizes and structural distributions. The evaluation framework systematically tests
        the algorithms against synthetic and semi-structured datasets designed to reflect a range of real-world and
        theoretical conditions, including uniform, clustered, and skewed distributions of defeasible and strict
        statements. Key performance metrics, such as computation time, memory usage, and justification set
        minimality, are analysed to assess scalability, efficiency, and robustness <sup>[19]</sup>. Special emphasis is placed
        on understanding how the combinatorial complexity introduced by different inference operators impacts
        algorithmic behaviour under increasing knowledge base density. By comparing the outputs across operators
        and configurations, the study reveals trade-offs between reasoning expressiveness and computational overhead.
        This empirical investigation not only validates the correctness and practicality of the implemented algorithms
        but also provides critical insights into the algorithmic sensitivity to knowledge base characteristics, offering
        guidance for their deployment in real-world intelligent systems <sup>[5]</sup>.
      </p>
          </AccordionContent>
        </AccordionItem>
        <AccordionItem value="item-2">
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
        <AccordionItem value="item-3">
          <AccordionTrigger>Syntax</AccordionTrigger>
          <AccordionContent className="flex flex-col gap-4 text-balance">
            <Syntax />
          </AccordionContent>
        </AccordionItem>
        <AccordionItem value="item-4">
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
