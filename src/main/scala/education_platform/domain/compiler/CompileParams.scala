package education_platform.domain.compiler

import derevo.cats.{eqv, show}
import derevo.circe.magnolia.{decoder, encoder}
import derevo.derive
import enumeratum.{Enum, EnumEntry}
import eu.timepit.refined.auto.autoUnwrap
import eu.timepit.refined.boolean.And
import eu.timepit.refined.numeric.NonNegative
import eu.timepit.refined.string.MatchesRegex
import eu.timepit.refined.types.string.NonEmptyString
import io.circe.{Decoder, Encoder}
import io.estatico.newtype.macros.newtype

import java.util.UUID
import scala.util.control.NoStackTrace

sealed trait Language extends EnumEntry

object Language extends Enum[Language] {

  case object Python extends Language

  case object Scala extends Language

  override def values: IndexedSeq[Language] = findValues
}

@newtype
case class CodeParam(value: NonEmptyString) {
  def toDomain: Code = Code(value.toLowerCase)
}

@derive(decoder, encoder, eqv, show)
@newtype
case class Code(value: String)

@newtype
case class VersionParam(value: NonNegative And MatchesRegex["^((?!evil).)*$"]) {
  def toDomain: Version = Version(value.toString)
}

@derive(decoder, encoder, eqv, show)
@newtype
case class Version(value: String)

@derive(decoder, encoder)
case class CompileParams(code: CodeParam, language: Language, versionParam: VersionParam)


