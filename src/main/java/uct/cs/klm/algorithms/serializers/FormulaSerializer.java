package uct.cs.klm.algorithms.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import org.tweetyproject.logics.pl.syntax.PlFormula;

/**
 * This class represents a formula serializer for a given query.
 * 
 * @author Chipo Hamayobe (chipo@cs.uct.ac.za)
 * @version 1.0.1
 * @since 2024-01-01
 */

public class FormulaSerializer extends JsonSerializer<PlFormula> {
  @Override
  public void serialize(PlFormula formula, JsonGenerator gen, SerializerProvider serializers) throws IOException {
    gen.writeObject(formula.toString());
  }
}
