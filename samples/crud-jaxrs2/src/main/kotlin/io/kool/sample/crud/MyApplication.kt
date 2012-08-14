package io.kool.sample.crud

import org.glassfish.jersey.server.ResourceConfig
//import org.glassfish.jersey.jackson.JacksonBinder

public class MyApplication:
    ResourceConfig(javaClass<ObjectMapperProvider>(), javaClass<Products>(), javaClass<JacksonProvider>()) {
/*
    {
        addBinders(JacksonBinder())
    }
*/
}