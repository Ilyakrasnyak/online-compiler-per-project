package education_platform

import cats.effect._
import education_platform.config.Config
import education_platform.modules.HttpApi
import education_platform.resources.MkHttpServer
import education_platform.services.CompilerService
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger

object Main extends IOApp.Simple {

  implicit val logger: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]

  override def run: IO[Unit] = {
    val server =
      for {
        cfg <- Resource.eval(Config.load[IO])
        compiler = CompilerService.make[IO]
        api = HttpApi.make[IO](compiler)
        server <- MkHttpServer[IO].newEmber(cfg.httpServerConfig, api.httpApp)
      } yield server
    server.useForever
  }
}
