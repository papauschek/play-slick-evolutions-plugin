
import sbt._
import Keys._

object PlaySlickPlugin extends Plugin
{

  override lazy val projectSettings = Seq(

    sourceGenerators in Compile <+= (baseDirectory, state, sourceManaged in Compile) map(
      (b, s, sm) => RouteFiles(b, s, sm)
    )

  )

  // alternatively, by overriding `settings`, they could be automatically added to a Project
  // override val settings = Seq(...)

  val RouteFiles = (baseDirectory: File, state: State, generatedDir: File) => {

    // get a list of all files in directory, recursively
    def recursiveListFiles(f: File): Seq[File] = {
      val files = Option(f.listFiles).toSeq.flatten
      files ++ files.filter(_.isDirectory).flatMap(recursiveListFiles)
    }

    // run slick code generation only when evolution files have changed
    val cachedEvolutionsGenerator = FileFunction.cached(baseDirectory / "target" / "slick-code-cache", FilesInfo.lastModified, FilesInfo.exists) {
      (inFiles: Set[File]) => {
        // evolution files have changed: run code generator from dbGen module
        println("Database evolutions have changed. Generating Slick code.")
        val outputDir = generatedDir.getPath
        PlaySlickCodeGenerator.generate(outputDir)
        Set(file(outputDir + "/db/Tables.scala"))
      }
    }

    // we're monitoring file changes in the conf/evolutions folder
    val evolutions = recursiveListFiles(baseDirectory / "conf" / "evolutions")
    cachedEvolutionsGenerator(evolutions.toSet).toSeq
  }

}