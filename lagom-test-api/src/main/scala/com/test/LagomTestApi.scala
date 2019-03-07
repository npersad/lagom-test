package com.test

import akka.NotUsed
import akka.stream.scaladsl.Source
import com.lightbend.lagom.scaladsl.api.Service.{named, namedCall}
import com.lightbend.lagom.scaladsl.api.{CircuitBreaker, Descriptor, Service, ServiceCall}

trait LagomTestApi extends Service {


  def echo: ServiceCall[Source[String, NotUsed],Source[String, NotUsed]]


  override def descriptor: Descriptor = {

    import LagomTestApi._

    named(LagomTestApi.ServiceName)
      .withCalls(


        namedCall(apiPath("/stream/payload"), echo _)
          .withCircuitBreaker(circuitBreaker),

      )
      .withAutoAcl(true)
  }

  private def circuitBreaker =
    CircuitBreaker.identifiedBy("lagom-test-api-circuit-breaker")
}

object LagomTestApi {

  val ServiceName = "LagomTestService"

  def apiPath(path: String = ""): String = "/api/stream" + path


}
