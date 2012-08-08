package io.kool.sample.chat

import org.codehaus.jackson.map.ObjectMapper
import javax.ws.rs.ext.ContextResolver
import org.codehaus.jackson.jaxrs.JacksonJsonProvider
import org.codehaus.jackson.map.DeserializationConfig
import javax.ws.rs.ext.Provider
import javax.ws.rs.core.MediaType
import javax.ws.rs.Produces
import javax.ws.rs.Consumes
import java.lang.reflect.Type
import java.lang.annotation.Annotation
import javax.ws.rs.core.MultivaluedMap
import java.io.InputStream

/**
 * Configure Jackson as the JSON provider
 */
/*
TODO can't compile due to Kotlin compiler thinking there's an abstract method missing!

public class JacksonProvider: JacksonJsonProvider(ObjectMapper().configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false)) {
}

*/
