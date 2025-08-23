package uct.cs.klm.algorithms.serializers;

import java.io.IOException;

import org.tweetyproject.logics.pl.syntax.PlFormula;

import uct.cs.klm.algorithms.utils.DefeasibleParser;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class FormulaDeserializer extends JsonDeserializer<PlFormula> {

    @Override
    public PlFormula deserialize(JsonParser p, DeserializationContext context) throws IOException, JacksonException {
        String formulaString = p.getValueAsString();
        try {
            DefeasibleParser parser = new DefeasibleParser();
            return parser.parseFormula(formulaString);
        } catch (Exception e) {
            throw new IOException("The formula \"" + formulaString + "\" is invalid.");
        }
    }

}
