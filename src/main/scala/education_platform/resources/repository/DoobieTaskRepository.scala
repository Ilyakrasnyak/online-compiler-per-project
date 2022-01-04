package education_platform.resources.repository

import cats.effect.Async
import cats.syntax.all._
import doobie._
import Fragments.{or, whereAndOpt}
import com.github.dwickern.macros.NameOf.{nameOf, nameOfType}
import doobie.implicits._
import education_platform.domain.task.{Task, TaskRepository}

import java.time.{OffsetDateTime, ZoneOffset}

private object TaskSQL {
  implicit val dbLogger: LogHandler = DbLogger.logHandler

  def insert(task: Task): Update0 =
    sql"""
    INSERT INTO TASK (
     TITLE, TEMPLATE
    )
    VALUES (${task.title}, ${task.template})
    """.update

  def select(title: String): Query0[Task] = sql"""
    SELECT
      TITLE,
      TEMPLATE
    FROM TASK WHERE TITLE = $title LIMIT 1
    FOR UPDATE
    """.query[Task]

  def update(task: Task): Update0 =
    sql"""
        UPDATE PAYMENT_REPORT SET
        WHERE TITLE = ${task.title}
    """.update
}

class DoobieTaskRepository[F[_]: Async](val xa: Transactor[F])
    extends TaskRepository[F] {

  val className: String = nameOfType[DoobieTaskRepository[F]]

  override def insert(task: Task): F[Unit] =
    TaskSQL
      .insert(task)
      .withUniqueGeneratedKeys[Long]("id")
      .transact(xa)
      .void

  override def update(task: Task): F[Unit] =
    TaskSQL
      .update(task)
      .run
      .transact(xa)
      .void

  override def get(title: String): F[Task] =
    TaskSQL
      .select(title)
      .unique
      .transact(xa)

}

object DoobieTaskRepository {

  def apply[F[_]: Async](
      xa: Transactor[F]
  ): DoobieTaskRepository[F] =
    new DoobieTaskRepository(xa)

}
