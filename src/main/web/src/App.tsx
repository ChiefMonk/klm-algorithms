import "katex/dist/katex.min.css";
import { Toaster } from "@/components/ui/toaster";
import { Footer } from "@/components/Footer";
import { Header } from "./components/Header";
import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import { MainContent } from "./components/MainContent";
// import { Syntax } from "./components/Syntax";
// import { KbData } from "./components/KbData";
import { SCREEN_WIDTH_SIZE } from "@/lib/constants";
import { cn } from "./lib/utils";
import { EvaluationContent } from "./components/evaluation/evaluation-content";
import { InfoPage } from "./components/info-page";
import { ReasonerProvider } from "./state/reasoner.context";

function App() {
  return (
    <Router>
      <ReasonerProvider>
        <div className="bg-secondary/40 flex flex-col items-center min-h-screen">
          <Header className="w-full" />
          <div
            className={cn(
              "flex-grow space-y-4 px-2 sm:px-8 py-4 max-w-screen-xl w-full",
              `max-w-[${SCREEN_WIDTH_SIZE}vw]`
            )}
          >
            <Routes>
              <Route path="/" element={<MainContent />} />
              <Route path="/entailment" element={<MainContent />} />
              <Route path="/justification" element={<MainContent />} />
              <Route path="/evaluation" element={<EvaluationContent />} />
              {/* <Route path="/syntax" element={<Syntax />} /> */}
              {/* <Route path="/knowledge-base" element={<KbData />} /> */}
              <Route path="/info" element={<InfoPage />} />
              <Route path="*" element={<MainContent />} />
            </Routes>
          </div>
          <Footer className="w-full" />
          <Toaster />
        </div>
      </ReasonerProvider>
    </Router>
  );
}

export default App;
