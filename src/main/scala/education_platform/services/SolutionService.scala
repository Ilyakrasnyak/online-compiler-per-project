package education_platform.services

import cats.implicits._
import education_platform.domain.compiler._
import education_platform.domain.compiler.Compiler._
import education_platform.domain.task.{TaskRepository, TemplateInjector}
import better.files._
import cats.effect.Sync
import cats.effect.syntax.all._
import education_platform.domain.compiler.domain._


trait SolutionService[F[_]] {
  def process[L: Compiler](compileParams: CompileParams): F[String]
}

object SolutionService {

  def make[F[_]: Sync](
      compiler: CompilerService[F],
      taskRepo: TaskRepository[F]
  ): SolutionService[F] =
    new SolutionService[F] {
      override def process[L: Compiler](cp: CompileParams): F[String] =
        for {
          task <- taskRepo.get(cp.task.title)
          result <- compiler.process[L](
            TemplateInjector.inject(task.template, cp.code)
          )
        } yield result
    }
}
