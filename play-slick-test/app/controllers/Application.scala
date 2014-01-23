package controllers

import db.Tables._
import slick.driver.MySQLDriver.simple._
import play.api.mvc.{Controller, Action}
import play.api.Play.current

object Application extends Controller {

  def index = Action {
    DB {
      implicit session => {

        val users = User.list

        Ok(views.html.index(users.toString))
      }
    }
  }

}

object DB {

  lazy private val default = Database.forDataSource(play.api.db.DB.getDataSource())
  def apply[T](f: Session => T): T = default.withSession(f)

}