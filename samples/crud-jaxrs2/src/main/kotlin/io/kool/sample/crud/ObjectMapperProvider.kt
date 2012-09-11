package io.kool.sample.crud

import javax.ws.rs.ext.ContextResolver
import org.codehaus.jackson.map.DeserializationConfig
import org.codehaus.jackson.map.ObjectMapper
import org.codehaus.jackson.map.SerializationConfig.Feature

/**
* Configures the Jackson JSON marshalling rules
*/
public class ObjectMapperProvider: ContextResolver<ObjectMapper> {
    val mapper: ObjectMapper? = ObjectMapper().
    configure(Feature.INDENT_OUTPUT, true)?.
    configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    public override fun getContext(genericType: Class<out Any?>?): ObjectMapper? {
        return mapper
    }

}