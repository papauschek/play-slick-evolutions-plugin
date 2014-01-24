package controllers

import db.Tables._
import slick.driver.MySQLDriver.simple._
import play.api.mvc.{Controller, Action}
import play.api.Play.current
import org.joda.time.DateTime
import com.github.tototoshi.slick.MySQLJodaSupport._

object Application extends Controller {

  def index = Action {
    DB {
      implicit session => {

        val now = DateTime.now().minusWeeks(1)
        val users = User.filter(_.createdate > now).list

        Ok(views.html.index(users.toString))
      }
    }
  }

}

object DB {

  lazy private val default = Database.forDataSource(play.api.db.DB.getDataSource())
  def apply[T](f: Session => T): T = default.withSession(f)

}