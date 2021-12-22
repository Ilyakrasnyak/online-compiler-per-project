package education_platform.domain.compiler

import derevo.circe.magnolia.{decoder, encoder}
import derevo.derive

@derive(decoder, encoder)
case class CompileParams(code: String, language: String, version: String)
