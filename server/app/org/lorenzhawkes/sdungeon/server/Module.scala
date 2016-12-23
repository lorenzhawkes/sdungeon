package org.lorenzhawkes.sdungeon.server

import com.google.inject.AbstractModule
import org.lorenzhawkes.sdungeon.server.controllers.DisplayServerController
import javax.inject.Singleton
import org.lorenzhawkes.sdungeon.server.controllers.DisplayServerController
import org.lorenzhawkes.sdungeon.server.controllers.DisplayServerController

class Module extends AbstractModule {

  override def configure() = {   
    bind(classOf[DisplayServerController]).asEagerSingleton()
  }

}
