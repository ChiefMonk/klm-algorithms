import "katex/dist/katex.min.css";
import { Toaster } from "@/components/ui/toaster";
import { Footer } from "@/components/Footer";
import { Header } from "./components/Header";
import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import { MainContent } from "./components/MainContent";
import { Syntax } from "./components/Syntax";
import { KbData } from "./components/KbData";

function App() {
  return (
    <div className="bg-background flex flex-col items-center h-screen">
      <Router>
        <Header className="w-full" />
        <div className="flex-grow space-y-4 px-2 sm:px-8 py-4 max-w-screen-xl w-full">
          <Routes>
            <Route path="/" element={<MainContent />} />
            <Route path="/syntax" element={<Syntax />} />
            <Route path="/knowledge-base" element={<KbData />} />
            <Route path="*" element={<MainContent />} />
          </Routes>
        </div>
        <Footer className="w-full" />
      </Router>
      <Toaster />
    </div>
  );
}

export default App;
