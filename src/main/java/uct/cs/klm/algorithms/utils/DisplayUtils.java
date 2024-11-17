package uct.cs.klm.algorithms.utils;

import org.tweetyproject.logics.pl.syntax.PlFormula;
import uct.cs.klm.algorithms.models.KnowledgeBase;

public class DisplayUtils {
    
    public static String printJustificationAsCSV(KnowledgeBase justification)
    {
        StringBuilder stringBuilder = new StringBuilder();
        
        for (PlFormula formula : justification)
        {
            stringBuilder.append(formula).append(", ");
        }
        String result = stringBuilder.toString();
        
        if (result != null && result != "" && result.length() > 2)
            return result.substring(0, result.length()-2);
        else
            return "NULL";
    }
    
}
