package education_platform.modules

import cats.syntax.all._
import cats.effect.Async
import education_platform.routes.{CompilerRoutes, SolutionRoutes}
import org.http4s._
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.middleware._
import education_platform.services.{CompilerService, SolutionService}

sealed abstract class HttpApi[F[_]: Async] private (
    compiler: CompilerService[F],
    solutionService: SolutionService[F]
) {

  private val compilerRoutes = CompilerRoutes[F](compiler).routes
  private val solutionRoutes = SolutionRoutes[F](solutionService).routes
  private val openRoutes = solutionRoutes <+> compilerRoutes

  private val routes: HttpRoutes[F] = Router("v1/" -> openRoutes)

  private val loggers: HttpApp[F] => HttpApp[F] = {
    { http: HttpApp[F] =>
      RequestLogger.httpApp(logHeaders = true, logBody = true)(http)
    } andThen { http: HttpApp[F] =>
      ResponseLogger.httpApp(logHeaders = true, logBody = true)(http)
    }
  }

  val httpApp: HttpApp[F] = loggers(routes.orNotFound)
}

object HttpApi {
  def make[F[_]: Async](
      compiler: CompilerService[F],
      solutionService: SolutionService[F]
  ): HttpApi[F] =
    new HttpApi[F](compiler, solutionService) {}
}
