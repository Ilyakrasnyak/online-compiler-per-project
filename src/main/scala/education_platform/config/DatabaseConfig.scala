package education_platform.config

import cats.effect.{Async, Resource, Sync}
import cats.syntax.functor._
import derevo.circe.magnolia.decoder
import derevo.derive
import doobie.Transactor
import doobie.hikari.HikariTransactor
import org.flywaydb.core.Flyway

import java.util.concurrent.ExecutorService
import scala.concurrent.ExecutionContext

@derive(decoder)
final case class ConnectionConfig(threadPoolSize: Int, connectionPoolSize: Int)

@derive(decoder)
final case class DatabaseConfig(
    host: String,
    port: Int,
    name: String,
    user: String,
    password: String,
    connection: ConnectionConfig
)

object DatabaseConfig {

  def jdbcUrl(host: String, port: Int, dbName: String): String =
    s"jdbc:postgresql://$host:$port/$dbName"

  def dbTransactor[F[_]: Async](
      connEc: ExecutionContext,
      config: DatabaseConfig
  ): Resource[F, Transactor[F]] = {

    for {
      transactor <- HikariTransactor
        .newHikariTransactor[F](
          driverClassName = "org.postgresql.Driver",
          url = jdbcUrl(config.host, config.port, config.name),
          user = config.user,
          pass = config.password,
          connectEC = connEc
        )
      _ <- Resource.eval(
        transactor
          .configure(dataSource =>
            Async[F].delay(
              dataSource.setMaximumPoolSize(
                config.connection.connectionPoolSize
              )
            )
          )
      )

    } yield transactor
  }

  /** Runs the flyway migrations against the target database */
  def initializeDb[F[_]](config: DatabaseConfig)(implicit S: Sync[F]): F[Unit] =
    S.delay {
      Flyway.configure
        .dataSource(
          jdbcUrl(config.host, config.port, config.name),
          config.user,
          config.password
        )
        .load
        .migrate()
    }.void
}
