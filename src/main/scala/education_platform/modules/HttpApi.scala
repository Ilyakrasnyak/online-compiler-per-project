package education_platform.modules

import scala.concurrent.duration._
import cats.effect.Async
import education_platform.routes.CompilerRoutes
import org.http4s._
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.middleware._
import education_platform.services.CompilerService

object HttpApi {
  def make[F[_]: Async](
      compiler: CompilerService[F]
  ): HttpApi[F] =
    new HttpApi[F](compiler) {}
}

sealed abstract class HttpApi[F[_]: Async] private (
    compiler: CompilerService[F]
) {

  private val compilerRoutes = CompilerRoutes[F](compiler).routes

  private val routes: HttpRoutes[F] = Router("v1/" -> compilerRoutes)

  private val middleware: HttpRoutes[F] => HttpRoutes[F] = {
    { http: HttpRoutes[F] =>
      AutoSlash(http)
    } andThen { http: HttpRoutes[F] =>
      CORS(http)
    } andThen { http: HttpRoutes[F] =>
      Timeout(60.seconds)(http)
    }
  }

  private val loggers: HttpApp[F] => HttpApp[F] = {
    { http: HttpApp[F] =>
      RequestLogger.httpApp(logHeaders = true, logBody = true)(http)
    } andThen { http: HttpApp[F] =>
      ResponseLogger.httpApp(logHeaders = true, logBody = true)(http)
    }
  }

  val httpApp: HttpApp[F] = loggers(middleware(routes).orNotFound)

}
