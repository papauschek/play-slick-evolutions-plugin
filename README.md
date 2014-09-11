play-slick-evolutions-plugin
============================

A Play Framework SBT Plugin for Auto-Generating Slick Code. Still a prototype.

How to include
--------------

To use it in your Play project, download the code and add the following two lines to your `project/plugins.sbt` (replace the path to play-slick-evolutions-plugin as needed)

```
lazy val root = project.in(file(".")).dependsOn(playSlickPlugin)
lazy val playSlickPlugin = file(new File("./play-slick-evolutions-plugin").getAbsolutePath)
```

How to use
----------

1. Have some database evolutions (X.sql) in your /conf/evolutions/{databaseName} dir.
2. Configure the code generator in your application.conf:
  * `db.default.generator.profile` Required: The Slick profile you want to use in your app (e.g. `scala.slick.driver.MySQLDriver`)
  * `db.default.generator.mode` Optional: the compatibility mode that H2 should use for reading your evolutions (e.g. `MySQL`)


Enable JodaTime support
-----------------------

Want to use JodaTime instead of java.sql.Timestamp?

Add Slick-JodaTime mapper to your dependencies in your `build.sbt`:
```
libraryDependencies ++= Seq(
  ...
  "com.github.tototoshi" %% "slick-joda-mapper" % "1.0.0"
)
```

Configure the database in your `application.conf`:
```
db.default.generator.enableJodaTime = true
```

Make sure you import the implicits whenever you query against JodaTime values:

```
import com.github.tototoshi.slick.MySQLJodaSupport._
```
(replace MySQL with your database driver)




LICENSE
-----------------------
This is free and unencumbered software released into the public domain.

Anyone is free to copy, modify, publish, use, compile, sell, or
distribute this software, either in source code form or as a compiled
binary, for any purpose, commercial or non-commercial, and by any
means.

In jurisdictions that recognize copyright laws, the author or authors
of this software dedicate any and all copyright interest in the
software to the public domain. We make this dedication for the benefit
of the public at large and to the detriment of our heirs and
successors. We intend this dedication to be an overt act of
relinquishment in perpetuity of all present and future rights to this
software under copyright law.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR
OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
OTHER DEALINGS IN THE SOFTWARE.

For more information, please refer to <http://unlicense.org/>
