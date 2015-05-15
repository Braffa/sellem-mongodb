package controllers

/**
 * Created by david.a.brayfield on 13/05/2015.
 */

import play.api.Logger
import play.api.mvc._

object Application extends Controller {

  def home = Action {
    Logger.info("Application home")
    Ok(views.html.index("Home"))
  }

}
