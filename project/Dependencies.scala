import sbt.*

object Dependencies {

  private val bootstrapPlayVersion = "8.5.0"
  private val hmrcMongoPlayVersion = "1.8.0"

  val compile: Seq[ModuleID] = Seq(
    "org.scalameta" %% "munit" % "1.0.0"
  )

  val test: Seq[ModuleID] = Seq(
    "org.scalatest" %% "scalatest" % "3.2.18"
  ).map(_ % Test)

  // only add additional dependencies here - it test inherit test dependencies above already
  val itDependencies: Seq[ModuleID] = Seq()

  def apply(): Seq[ModuleID] = compile ++ test

}
