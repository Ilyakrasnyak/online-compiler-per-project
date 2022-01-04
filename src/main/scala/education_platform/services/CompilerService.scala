package education_platform.services

import education_platform.domain.compiler._
import education_platform.domain.compiler.Compiler._
import better.files._
import cats.effect.Sync
import cats.effect.syntax.all._
import education_platform.domain.compiler.domain._

import scala.sys.process._

trait CompilerService[F[_]] {
  def process[L: Compiler](code: String): F[String]
}

object CompilerService {

  def make[F[_]: Sync]: CompilerService[F] =
    new CompilerService[F] {
      override def process[L: Compiler](code: String): F[String] =
        createFile[L].bracket { tempFile =>
          Sync[F].delay {
            tempFile.appendText(code)
            s"${Compiler[L].shellCommand} ${tempFile.path}".!!
          }
        } (deleteFile)

      private def createFile[L: Compiler]: F[File] =
        Sync[F].delay(File("temp", Compiler[L].fileExt))

      private def deleteFile(f: File): F[Unit] =
        Sync[F].delay(f.delete())
    }
}
