package com.test

import akka.NotUsed
import akka.event.Logging
import akka.stream.Attributes
import akka.stream.scaladsl.{Flow, Keep, Source}
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.server.ServerServiceCall
import play.api.Logger

import scala.concurrent.{ExecutionContext, Future}

class LagomTestClientServiceImpl(implicit ec: ExecutionContext) extends LagomTestApi {

  override def echo: ServiceCall[Source[String, NotUsed], Source[String, NotUsed]] =
    ServerServiceCall { in =>

      val source = in
        .watchTermination() { (_, done) ⇒
          done.onComplete {
            case scala.util.Success(_) ⇒ Logger.info("success")
            case scala.util.Failure(_) ⇒ Logger.info("error")
          }
        }
        .viaMat(
        Flow[String]
          .log("SENDING RESPONSE")
          .map(identity)
      )(Keep.right)

      Future.successful(source.withAttributes(
        Attributes.logLevels(
          onElement = Logging.WarningLevel,
          onFinish = Logging.InfoLevel,
          onFailure = Logging.DebugLevel
        )
      ))
    }
}
