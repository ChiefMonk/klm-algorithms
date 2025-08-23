package uct.cs.klm.algorithms.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.tweetyproject.commons.ParserException;
import org.tweetyproject.logics.pl.parser.PlParser;
import org.tweetyproject.logics.pl.syntax.PlFormula;

import uct.cs.klm.algorithms.models.KnowledgeBase;

public class DefeasibleParser {

    private final PlParser parser;

    public DefeasibleParser() {
        this.parser = new PlParser();
    }

    public PlFormula parseFormula(String formula) throws Exception {
        PlFormula parsedFormula;
        try {
            
            formula = formula.replaceAll(" ", "");
            formula = formula.replaceAll("\\s", "");
            
            boolean isDI = formula.contains(Symbols.DEFEASIBLE_IMPLICATION);
            formula = isDI ? reformatDefeasibleImplication(formula) : formula;
            parsedFormula = parser.parseFormula(formula);
                       
            return isDI ? ReasonerUtils.toDematerialisedFormula(parsedFormula) : parsedFormula;
            
        } catch (IOException | ParserException e) {
            throw new Exception("Cannot parse formula: " + formula);
        }
    }

    public KnowledgeBase parseFormulas(String formulas) throws Exception {
        String[] formulaStrings = formulas.split(",");
        KnowledgeBase kb = new KnowledgeBase();

        for (String formula : formulaStrings) {
            if (!formula.trim().isEmpty()) {
                try {
                    PlFormula parsedFormula = this.parseFormula(formula.trim());
                    kb.add(parsedFormula);
                } catch (Exception e) {
                    throw e;
                }
            }
        }
        return kb;
    }

    public KnowledgeBase parseFormulasFromFile(String kbFilePath) throws Exception {
        KnowledgeBase kb = new KnowledgeBase();

        String error = "Please select a valid file with a predefined defeasible knowledge base, K.";

        Path path = Paths.get(kbFilePath);
        
        if (!Files.exists(path)) {
            throw new FileNotFoundException(error);
        }
        
        System.out.println(String.format("KB path : %s", path.toAbsolutePath()));

        List<String> kbStatements = Files.readAllLines(path, StandardCharsets.UTF_8);

        if (kbStatements == null || kbStatements.isEmpty()) {
            throw new Exception("Please define a valid defeasible knowledge base, K.");
        }

        for (String statement : kbStatements) {
            if (statement == null || statement.isEmpty()) {
                continue;
            }                       
            
            //System.out.println(String.format("Formula: %s", statement));

            PlFormula parsedFormula = this.parseFormula(statement.trim());
            kb.add(parsedFormula);
        }

        //System.out.println(String.format("KB: %s", kb));
        return kb;
    }

    public KnowledgeBase parseFormulasFromFile2(String filePath) throws Exception {
        KnowledgeBase kb = new KnowledgeBase();

        try (var reader = new BufferedReader(new FileReader(filePath))) {
            String line;

            while ((line = reader.readLine()) != null) {
                try {
                    PlFormula parsedFormula = this.parseFormula(line.trim());
                    kb.add(parsedFormula);
                } catch (Exception e) {
                    throw e;
                }
            }
        } catch (Exception e) {
            throw e;
        }

        return kb;
    }

    public KnowledgeBase parseInputStream(InputStream inputStream) throws Exception {
        KnowledgeBase kb = new KnowledgeBase();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    PlFormula parsedFormula = this.parseFormula(line.trim());
                    kb.add(parsedFormula);
                } catch (Exception e) {
                    throw e;
                }
            }
        } catch (Exception e) {
            throw e;
        }
        return kb;
    }

    private String reformatDefeasibleImplication(String formula) {
        formula = formula.replaceAll(" ", "");
        
        int index = formula.indexOf(Symbols.DEFEASIBLE_IMPLICATION);
        formula = "(" + formula.substring(0, index) + ")" + Symbols.IMPLICATION + "("
                + formula.substring(index + 2, formula.length()) + ")";
        return formula;
    }

}
