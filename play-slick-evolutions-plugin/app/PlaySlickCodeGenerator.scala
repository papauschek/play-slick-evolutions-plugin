import com.typesafe.config.{ConfigFactory, Config}
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
 *  Database configuration is taken from conf/application.conf
 */
object PlaySlickCodeGenerator{

  /** runs the code generator for the given configuration in confDir and writes files to the given outputDir
    * confDir is assumed to be the conf folder of a Play app
    * returns set of generated code files */
  def generate(outputDir: File, confDir: File) : Set[File] = {
    try
    {
      println("Database evolutions have changed.")
      generateAllDatabases(outputDir, confDir)
    }
    catch {
      case ex: PlayException =>
        throw ex
      case ex: Throwable =>
        throw new PlayException("Could not generate code", ex.getMessage, ex)
    }
  }

  /** generates source code for all databases in the config */
  private def generateAllDatabases(outputDir: File, confDir: File) : Set[File] = {

    val appConfigFile = new File(confDir.getPath + "/application.conf")
    val appConfig = Configuration(ConfigFactory.parseFileAnySyntax(appConfigFile))
    val dbConfig = appConfig.getConfig("db").getOrElse(Configuration.empty)
    val databases = dbConfig.subKeys

    println(dbConfig.entrySet.toString())
    println(appConfigFile.getAbsolutePath)

    // generate source files for each database
    val generatedFiles = databases.flatMap(database =>
      generateDatabase(outputDir, database, dbConfig.getConfig(database + ".generator").getOrElse(Configuration.empty)))

    generatedFiles.toSet
  }

  /** generates source code for a single database with the given configuration
    * Configuration parameters used:
    * - package
    * - profile
    * - container
    *
    * Returns none if no evolutions are configured for this database */
  private def generateDatabase(outputDir: File, databaseName: String, config: Configuration) : Option[File] = {

    println("Generating slick code for '" + databaseName + "' ...")

    // read generator configuration
    val defaultContainer = if (databaseName == "default") "Tables" else s"${databaseName}Tables"
    val outputPackage = config.getString("package").getOrElse("db")
    val outputContainer = config.getString("container").getOrElse(defaultContainer)

    // expect output profile
    val outputProfile = config.getString("profile") match {
      case Some(profile) => profile
      case None => throw new PlayException("Please specify an output profile for the Slick Code Generator",
        s"For example, if you're using MySQL, add\ndb.${databaseName}.generator.profile=scala.slick.driver.MySQLDriver\nto your application.conf")
    }

    // config for generator database
    val driver = config.getString("driver").getOrElse("org.h2.Driver")
    val maybeMode = config.getString("mode")
    val baseUrl = config.getString("url").getOrElse("jdbc:h2:mem:generator")
    val url = Seq(Some(baseUrl), maybeMode).flatten.mkString(";MODE=")

    // create fake application using in-memory database
    val app = FakeApplication(
      path = new File("dbgen").getCanonicalFile,
      config = Map(
        s"db.${databaseName}.url" -> url,
        s"db.${databaseName}.driver" -> driver))

    // create database plugin
    val dbPlugin = new BoneCPPlugin(app)
    try
    {
      // check if evolutions exist
      val script = Evolutions.evolutionScript(dbPlugin.api, new File("."), dbPlugin.getClass.getClassLoader, databaseName)

      if (script.size == 0)
        None // no evolutions found, skip code generation
      else
      {
        // run evolutions against database
        Evolutions.applyScript(dbPlugin.api, databaseName, script)

        // get list of tables for which code will be generated
        // also, we exclude the play evolutions table
        val db = Database.forDataSource(dbPlugin.api.getDataSource(databaseName))
        val excludedTables = Seq("play_evolutions")
        val model = db.withSession {
          implicit session =>
            val tables = H2Driver.getTables.list.filterNot(t => excludedTables contains t.name.name)
            createModel(tables, H2Driver)
        }

        // generate slick db code and write to file
        val codeGen = new SourceCodeGenerator(model)
        val fileName = outputContainer + ".scala"
        codeGen.writeToFile(
          profile = outputProfile,
          folder = outputDir.getPath,
          pkg = outputPackage,
          container = outputContainer,
          fileName = fileName)

        // return path of generated file
        Some(new File(outputDir.getPath + "/" + outputPackage.replace(".","/") + "/" + fileName))
      }
    }
    finally
    {
      dbPlugin.onStop() // cleanup database connections
    }

  }

}

/** Fake application needed for running evolutions outside normal Play app */
case class FakeApplication(override val path: java.io.File = new java.io.File("."),
                           override val classloader : ClassLoader = classOf[FakeApplication].getClassLoader,
                           private val config: Map[String, _ <: Any]) extends {
  override val sources = None
  override val mode = play.api.Mode.Dev
} with Application with WithDefaultConfiguration with WithDefaultGlobal with WithDefaultPlugins {

  override def configuration = play.api.Configuration.from(config)

}