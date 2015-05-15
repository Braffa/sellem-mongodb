package controllers

/**
 * Created by david.a.brayfield on 13/05/2015.
 */
import play.api.mvc._

object Application extends Controller {

  def home = Action {
    Ok(views.html.index("Home"))
  }

}
