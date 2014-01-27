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
        //val users = User.filter(_.text > now).list

        case class test(email: Option[String], number: Int)

        val query = User leftJoin User2
        val maybe = query.map { case (u, u2) => (u.id, u.email) }
        val grouped = maybe.groupBy { case (id, email) => (id, email) }
        val users = grouped.map { case ((id, email), rows) => (email, rows.length) }
        val output = users.map(_ <> (test.tupled, test.unapply _))
        val list = output.list

        Ok(views.html.index(list.toString, output.selectStatement))
      }
    }
  }
}

object DB {

  lazy private val default = Database.forDataSource(play.api.db.DB.getDataSource())
  def apply[T](f: Session => T): T = default.withSession(f)

}