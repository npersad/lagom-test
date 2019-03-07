import akka.event.Logging
import akka.stream.scaladsl.Source
import akka.stream.testkit.scaladsl.TestSink
import com.lightbend.lagom.scaladsl.server.LocalServiceLocator
import com.lightbend.lagom.scaladsl.testkit.ServiceTest
import com.test.{LagomTestApi, LagomTestClientApplication}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Seconds, Span}
import org.scalatest.{AsyncWordSpec, Matchers}
import play.api.Logger


class TestClientServiceTest extends AsyncWordSpec with Matchers with ScalaFutures  {

  implicit val defaultPatience: PatienceConfig =
    PatienceConfig(timeout = Span(10, Seconds), interval = Span(5, Seconds))


  var server: ServiceTest.TestServer[LagomTestClientApplication] =  ServiceTest.startServer(
    ServiceTest.defaultSetup
  ) { ctx =>
    new LagomTestClientApplication(ctx) with LocalServiceLocator
  }


  "Test service" should {

    "non test stream test" in {
      import akka.actor._
      import akka.stream._
      import akka.stream.scaladsl._
      import akka.stream.testkit.scaladsl._


      implicit val sys = ActorSystem()
      implicit val mat = ActorMaterializer()
      implicit val ec = sys.dispatcher

      val probe = Source(1 to 10)
        .log("Regular stream element")
        .watchTermination() { (_, done) ⇒
          done.onComplete {
            case scala.util.Success(_) ⇒ println("REGULAR STREAM SUCCESS")
            case scala.util.Failure(_) ⇒ println("ERROR!!")
          }
        }
        .addAttributes(Attributes.logLevels(
          onElement = Logging.WarningLevel,
          onFinish = Logging.InfoLevel,
          onFailure = Logging.DebugLevel
        ))
        .runWith(TestSink.probe(sys))


      probe.request(10)
      probe.expectNextUnorderedN(1 to 10)
      sys.terminate()

      succeed
    }

    "feed test with a stream" in {

      val _server = server

      import _server.materializer

      val testClient = server.serviceClient.implement[LagomTestApi]

      val documents = List("one", "two", "three")

      val input = Source(documents).concat(Source.maybe)

      testClient.echo.invoke(input).map { output =>

        val probe = output.runWith(TestSink.probe(server.actorSystem))
        probe.request(documents.length)

        val paired = List("one", "two", "three")

        probe.expectNextUnorderedN(paired)

        succeed
      }


    }
  }


}
