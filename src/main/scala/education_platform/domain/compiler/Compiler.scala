package education_platform.domain.compiler

import education_platform.domain.compiler.Language.{Python, Scala}

trait Compiler[A] {
  def fileExt: String

  def shellCommand: String
}

object Compiler {

  implicit def compiler[T :Compiler]: Compiler[Language] = new Compiler[Language] {
    override def fileExt: String = implicitly[Compiler[T]].fileExt

    override def shellCommand: String = implicitly[Compiler[T]].shellCommand
  }

  implicit val pythonCompiler: Compiler[Python.type] = new Compiler[Python.type] {
    override def fileExt = "py"

    override def shellCommand = "python"
  }

  implicit val scalaCompiler: Compiler[Scala.type] = new Compiler[Scala.type] {
    override def fileExt = "sc"

    override def shellCommand = "scala"
  }
}
