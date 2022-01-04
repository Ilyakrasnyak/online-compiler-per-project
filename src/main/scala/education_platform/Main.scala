package education_platform

import cats.effect._
import doobie.util.ExecutionContexts
import education_platform.config.{Config, DatabaseConfig}
import education_platform.modules.HttpApi
import education_platform.resources.{AppResources, MkHttpServer}
import education_platform.services.{CompilerService, SolutionService}
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger

import scala.concurrent.ExecutionContext

object Main extends IOApp.Simple {

  implicit val logger: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]

  override def run: IO[Unit] = {
    val server =
      for {
        cfg <- Resource.eval(Config.load[IO])
        _ <- Resource.eval(DatabaseConfig.initializeDb[IO](cfg.dbConfig))
        execCon <- ExecutionContexts.fixedThreadPool[IO](32)
        transactor <- DatabaseConfig.dbTransactor[IO](execCon, cfg.dbConfig)
        resource <- AppResources.make(transactor)
        compiler = CompilerService.make[IO]
        solutionService = SolutionService.make[IO](compiler, resource.taskRepository)
        api = HttpApi.make[IO](compiler)
        server <- MkHttpServer[IO].newEmber(cfg.httpServerConfig, api.httpApp)
      } yield server
    server.useForever
  }
}
