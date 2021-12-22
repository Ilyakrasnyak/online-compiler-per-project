package education_platform.config

import cats.effect.Async
import ciris._
import com.comcast.ip4s._
import education_platform.config.AppEnvironment.{Prod, Test}
import education_platform.config.types._

object Config {

  // Ciris promotes configuration as code
  def load[F[_]: Async]: F[AppConfig] =
    env("SC_APP_ENV")
      .as[AppEnvironment]
      .flatMap {
        case Test =>
          default[F]
        case Prod =>
          default[F]
      }
      .load[F]

  def default[F[_]]: ConfigValue[F, AppConfig] = {
    ConfigValue.default(
      AppConfig(
        HttpServerConfig(
          host = host"0.0.0.0",
          port = port"8080"
        )
      )
    )
  }
}
