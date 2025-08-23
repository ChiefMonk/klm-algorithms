package uct.cs.klm.algorithms.utils;

import org.slf4j.Logger;
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
    
    public static String toRankNumberString(int rankNumber)
    {
        if(rankNumber == Symbols.INFINITY_RANK_NUMBER)
        {
            return "\u221E";
        }
        
        return String.valueOf(rankNumber);
    }
    
    public static void LogDebug(Logger logger, String message)
    {
        if (logger.isDebugEnabled()) {
            logger.debug(message);
        }
    }
    
}
