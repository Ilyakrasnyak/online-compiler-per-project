package education_platform.domain.compiler

import education_platform.domain.compiler.domen._

trait Compiler[A] {
  def fileExt: String

  def shellCommand: String
}

object Compiler {

  def apply[A](implicit compiler: Compiler[A]): Compiler[A] = compiler

  implicit val pythonCompiler: Compiler[Python] = new Compiler[Python] {
    override def fileExt = "py"

    override def shellCommand = "python"
  }

  implicit val scalaCompiler: Compiler[Scala] = new Compiler[Scala] {
    override def fileExt = "sc"

    override def shellCommand = "scala"
  }
}
