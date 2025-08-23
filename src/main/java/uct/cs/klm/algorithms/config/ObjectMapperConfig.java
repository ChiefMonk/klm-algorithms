package uct.cs.klm.algorithms.config;

import org.tweetyproject.logics.pl.syntax.PlFormula;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ObjectMapper;
import uct.cs.klm.algorithms.serializers.FormulaDeserializer;
import uct.cs.klm.algorithms.serializers.FormulaKeyDeserializer;
import uct.cs.klm.algorithms.serializers.FormulaSerializer;

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