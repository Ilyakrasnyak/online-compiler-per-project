package education_platform.services

import education_platform.domain.compiler._
import better.files._
import cats.effect.Sync

import scala.sys.process._

trait Compiler[F[_]] {

  def process[L](compileParams: CompileParams): F[String]

}

object Compiler {

  def make[F[_]: Sync]: Compiler[F] =
    new Compiler[F] {
      override def process[L](compileParams: CompileParams): F[String] =
        Sync[F].blocking {
          var res = ""
          File.usingTemporaryFile("temp", "py") { tempFile =>
            tempFile.appendText(compileParams.code)
            res = s"python ${tempFile.path}".!!
          }
          res
        }
    }
}
