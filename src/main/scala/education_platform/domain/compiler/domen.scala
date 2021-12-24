package education_platform.domain.compiler

import derevo.cats._
import derevo.circe.magnolia.{ decoder, encoder }
import derevo.derive
import enumeratum.{CirceEnum, Enum, EnumEntry}
import eu.timepit.refined.api.Refined
import eu.timepit.refined.auto._
import eu.timepit.refined.cats._
import eu.timepit.refined.boolean.And
import eu.timepit.refined.numeric.NonNegative
import eu.timepit.refined.string.MatchesRegex
import eu.timepit.refined.types.string.NonEmptyString
import io.estatico.newtype.macros.newtype
import io.circe.refined._
import io.estatico.newtype.macros.newtype

object domen {

  sealed trait Language extends EnumEntry

  object Language extends Enum[Language] with CirceEnum[Language] {

    case object Python extends Language

    case object Scala extends Language

    override def values: IndexedSeq[Language] = findValues
  }

  type Python = Language.Python.type
  type Scala = Language.Scala.type

  @derive(decoder, encoder)
  case class CodeParam(value: NonEmptyString) {
    def toDomain: Code = Code(value.toLowerCase)
  }

  @derive(decoder, encoder, eqv, show)
  case class Code(value: String)

  @derive(decoder, encoder)
  case class VersionParam(value: NonEmptyString) {
    def toDomain: Version = Version(value.toString)
  }

  @derive(decoder, encoder, eqv, show)
  case class Version(value: String)

  @derive(decoder, encoder)
  case class CompileParams(
      code: NonEmptyString,
      language: Language,
      version: NonEmptyString
  )

}
