package com.test

import com.lightbend.lagom.scaladsl.api.ServiceLocator
import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import com.lightbend.lagom.scaladsl.server._
import play.api.libs.ws.ahc.AhcWSComponents


/**
  * The application server (an instance of LagomApplication). Any external runtime dependencies can be started
  * here (such as subscriptions to other service topics).
  *
  * @param context An execution context (usually provided implicitly).
  */
abstract class LagomTestClientApplication(context: LagomApplicationContext)
  extends LagomApplication(context)
    with AhcWSComponents {


  override lazy val lagomServer: LagomServer =
    serverFor[LagomTestApi](new LagomTestClientServiceImpl())
}


/**
  * Loads the application (service) and starts it, in either development or production mode.
  */
class LagomTestClientApplicationLoader extends LagomApplicationLoader {
  override def load(context: LagomApplicationContext): LagomApplication =
    new LagomTestClientApplication(context) {

      // TODO: change this for production
      override def serviceLocator: ServiceLocator = NoServiceLocator
    }

  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new LagomTestClientApplication(context) with LagomDevModeComponents

  override def describeService = Some(readDescriptor[LagomTestApi])
}
