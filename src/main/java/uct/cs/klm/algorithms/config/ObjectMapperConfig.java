package uct.cs.klm.algorithms.config;

import org.tweetyproject.logics.pl.syntax.PlFormula;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ObjectMapper;
import uct.cs.klm.algorithms.serializers.FormulaDeserializer;
import uct.cs.klm.algorithms.serializers.FormulaKeyDeserializer;
import uct.cs.klm.algorithms.serializers.FormulaSerializer;

/**
 * This class represents a object mapper config for a given query.
 * 
 * @author Chipo Hamayobe (chipo@cs.uct.ac.za)
 * @version 1.0.1
 * @since 2024-01-01
 */

public class ObjectMapperConfig {
  public static ObjectMapper createObjectMapper() {
    ObjectMapper mapper = new ObjectMapper();
    SimpleModule module = new SimpleModule();
    module.addSerializer(PlFormula.class, new FormulaSerializer());
    module.addDeserializer(PlFormula.class, new FormulaDeserializer());
    module.addKeyDeserializer(PlFormula.class, new FormulaKeyDeserializer());
    mapper.registerModule(module);
    return mapper;
  }
}