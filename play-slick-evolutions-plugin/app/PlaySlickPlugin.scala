
import sbt._
import Keys._

object PlaySlickPlugin extends Plugin
{

  /** add the source generator */
  override lazy val projectSettings = Seq(
    sourceGenerators in Compile <+= (baseDirectory, sourceManaged in Compile) map(
      (baseDir, sourceManagedDir) => DatabaseFiles(baseDir, sourceManagedDir)
    )
  )

  /** defines how the database files are generated */
  val DatabaseFiles = (baseDir: File, sourceManagedDir: File) => {
    
    val confDir = baseDir / "conf" // configuration directory of the current Play app

    // create a cached generator that runs slick code generation only when evolution files have changed
    val cachedEvolutionsGenerator = FileFunction.cached(baseDir / "target" / "slick-code-cache", FilesInfo.lastModified, FilesInfo.exists) {
      (inFiles: Set[File]) => PlaySlickCodeGenerator.generate(sourceManagedDir, confDir)
    }

    // we're monitoring file changes in the conf/evolutions folder
    val evolutions = recursiveListFiles(confDir / "evolutions")
    cachedEvolutionsGenerator(evolutions.toSet).toSeq

    // Uncomment to run generator every time (for testing)
    //PlaySlickCodeGenerator.generate(sourceManagedDir, confDir).toSeq
  }

  // get a list of all files in directory, recursively
  private def recursiveListFiles(f: File): Seq[File] = {
    val files = Option(f.listFiles).toSeq.flatten
    files ++ files.filter(_.isDirectory).flatMap(recursiveListFiles)
  }

}