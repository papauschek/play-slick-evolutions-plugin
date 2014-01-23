import java.io.File
import play.api._
import play.api.db.BoneCPPlugin
import play.api.db.evolutions.Evolutions
import play.api.Application
import scala.slick.model.codegen.SourceCodeGenerator
import scala.slick.jdbc.meta.createModel
import scala.slick.driver.H2Driver.simple._
import scala.slick.driver.H2Driver

/**
 *  This code generator runs Play Framework Evolutions against an in-memory database
 *  and then generates code from this database using the default Slick code generator.
 *
 *  Parameters: Output directory for the generated code.
 *
 *  Other parameters are taken from this modules conf/application.conf
 */
object PlaySlickCodeGenerator{

  def generate(outputDir: String) = {
    try
    {
      run(outputDir)
    }
    catch {
      case ex: PlayException =>
        throw ex
      case ex: Throwable =>
        throw new PlayException("Could not generate code", ex.getMessage, ex)
    }
  }

  private def run(outputDir: String) = {

    // start fake application using in-memory database
    implicit val app = FakeApplication(
      path = new File("dbgen").getCanonicalFile,
      additionalConfiguration = Map(
        "db.default.url" -> "jdbc:h2:mem:test;MODE=MySQL",
        "db.default.driver" -> "org.h2.Driver"))

    // read database configuration
    val databaseName = "default" //databaseNames.headOption.getOrElse("")
    val outputPackage = "db" //app.configuration.getString(s"db.$databaseName.outputPackage").getOrElse("")
    val outputProfile = "scala.slick.driver.MySQLDriver" //app.configuration.getString(s"db.$databaseName.outputProfile").getOrElse("")

    // apply evolutions from main project
    val dbPlugin = new BoneCPPlugin(app)
    try
    {

      //Evolutions.applyFor(databaseName)
      val script = Evolutions.evolutionScript(dbPlugin.api, new File("."), dbPlugin.getClass.getClassLoader, databaseName)
      Evolutions.applyScript(dbPlugin.api, databaseName, script)

      // get list of tables for which code will be generated
      // also, we exclude the play evolutions table
      val db = Database.forDataSource(play.api.db.DB.getDataSource(databaseName))
      val excludedTables = Seq("play_evolutions")
      val model = db.withSession {
        implicit session =>
          val tables = H2Driver.getTables.list.filterNot(t => excludedTables contains t.name.name)
          createModel( tables, H2Driver )
      }

      // generate slick db code
      val codegen = new SourceCodeGenerator(model)
      codegen.writeToFile(
        profile = outputProfile,
        folder = outputDir,
        pkg = outputPackage,
        container = "Tables",
        fileName = "Tables.scala")

    }
    finally
    {
      dbPlugin.onStop()
    }

  }

}

/** Fake application needed for running evolutions outside normal Play app */
case class FakeApplication(
                            override val path: java.io.File = new java.io.File("."),
                            override val classloader : ClassLoader = classOf[FakeApplication].getClassLoader,
                            val additionalConfiguration: Map[String, _ <: Any]) extends {
  override val sources = None
  override val mode = play.api.Mode.Dev
} with Application with WithDefaultConfiguration with WithDefaultGlobal with WithDefaultPlugins {

  override def configuration =
    super.configuration ++ play.api.Configuration.from(additionalConfiguration)

}