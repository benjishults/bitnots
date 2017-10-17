package com.benjishults.bitnots.dataIngest

import com.benjishults.bitnots.ingest.routes.EndpointConfig
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.WebTestClient.BodySpec

@RunWith(SpringJUnit4ClassRunner::class)
@ContextConfiguration(classes = arrayOf(EndpointConfig::class))
class WebFluxTest {

//    @Autowired
//    lateinit var apis: EndpointConfig

    @Autowired
    lateinit var context: ApplicationContext

    val webTestClient: WebTestClient = WebTestClient.bindToApplicationContext(context).build() //.bindToRouterFunction(apis.apis())

//    @Test
//    fun indexPage_WhenRequested_SaysHello() {
//        webTestClient.get().uri("/").exchange()
//                .expectStatus()
//                .is2xxSuccessful()
//                .expectBody(String::class.java)
//                .isEqualTo<String>("Hello")
//    }
//
//    @Test
//    fun jsonPage_WhenRequested_SaysHello() {
//        webTestClient.get().uri("/json").exchange()
//                .expectStatus().is2xxSuccessful()
//                .expectHeader().contentType(MediaType.APPLICATION_JSON)
//                .expectBody(String::class.java)
//                        .isEqualTo<BodySpec<String, *>>("world");
//    }
}
