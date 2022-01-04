package education_platform.domain.compiler

import derevo.cats._
import derevo.circe.magnolia.{decoder, encoder}
import derevo.derive
import education_platform.domain.task.Task
import enumeratum.{CirceEnum, Enum, EnumEntry}
import eu.timepit.refined.api.Refined
import eu.timepit.refined.auto._
import eu.timepit.refined.string.{MatchesRegex, Regex}
import eu.timepit.refined.types.string.NonEmptyString
import io.estatico.newtype.macros.newtype
import io.circe.refined._
import io.estatico.newtype.macros.newtype

object domain {

  sealed trait Language extends EnumEntry

  object Language extends Enum[Language] with CirceEnum[Language] {

    case object Python extends Language

    case object Scala extends Language

    override def values: IndexedSeq[Language] = findValues
  }

  type Python = Language.Python.type
  type Scala = Language.Scala.type

  type Rgx = "^((?!evil).)*$"
  type  Code = String Refined MatchesRegex[Rgx]

  @derive(decoder, encoder)
  case class CompileParams(
      task: Task,
      code: Code,
      language: Language,
      version: NonEmptyString
  )
}
