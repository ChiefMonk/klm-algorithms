package uct.cs.klm.algorithms.serializers;

import com.fasterxml.jackson.databind.KeyDeserializer;
import java.io.IOException;
import uct.cs.klm.algorithms.utils.DefeasibleParser;

/**
 * This class represents a formula key deserializer for a given query.
 * 
 * @author Chipo Hamayobe (chipo@cs.uct.ac.za)
 * @version 1.0.1
 * @since 2024-01-01
 */

public class FormulaKeyDeserializer extends KeyDeserializer {
    @Override
    public Object deserializeKey(String key, com.fasterxml.jackson.databind.DeserializationContext ctxt) throws IOException {
        try {
            return new DefeasibleParser().parseFormula(key);
        } catch (Exception e) {
            throw new IOException("Invalid formula key: " + key, e);
        }
    }
}
