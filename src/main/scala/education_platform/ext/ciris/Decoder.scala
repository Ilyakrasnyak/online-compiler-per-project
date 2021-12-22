package education_platform.ext.ciris

import _root_.ciris.ConfigDecoder
import education_platform.ext.derevo.Derive

object configDecoder extends Derive[Decoder.Id]

object Decoder {
  type Id[A] = ConfigDecoder[String, A]
}
