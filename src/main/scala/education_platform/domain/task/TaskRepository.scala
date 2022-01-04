package education_platform.domain.task

trait TaskRepository[F[_]] {

  def insert(task: Task): F[Unit]

  def get(title: String): F[Task]

  def update(task: Task): F[Unit]

}
