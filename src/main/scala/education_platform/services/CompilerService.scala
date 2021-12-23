package education_platform.services

import education_platform.domain.compiler._
import better.files._
import cats.effect.Sync
import cats.effect.syntax.all._


import scala.sys.process._

trait CompilerService[F[_]] {

  def process(compileParams: CompileParams)(implicit c: Compiler[compileParams.language.type]): F[String]

}

object CompilerService {

  def make[F[_] : Sync]: CompilerService[F] =
    new CompilerService[F] {
      override def process(compileParams: CompileParams)(implicit c: Compiler[compileParams.language.type]): F[String] =
        Sync[F].delay(File("temp", c.fileExt)).bracket {
          tempFile =>
            Sync[F].delay {
              tempFile.appendText(compileParams.code)
              s"${c.shellCommand} ${tempFile.path}".!!
            }
        } {
          tempFile => Sync[F].delay(tempFile.delete())
        }
    }
}
