package education_platform.resources

import cats.effect.std.Console
import cats.effect.{Async, Resource}
import cats.syntax.all._
import doobie.Transactor
import education_platform.resources.repository.DoobieTaskRepository
import fs2.io.net.Network
import org.typelevel.log4cats.Logger

sealed case class AppResources[F[_]](
    taskRepository: DoobieTaskRepository[F]
)

object AppResources {

  def make[F[_]: Async: Console: Logger: Network](
      transactor: Transactor[F]
  ): Resource[F, AppResources[F]] = {
    Resource.eval(
      Async[F].delay(
        AppResources(
          DoobieTaskRepository(transactor)
        )
      )
    )
  }
}
