package education_platform.domain.task

import education_platform.domain.compiler.domain.Code

object TemplateInjector {

  def inject(template: String, code: Code): String = {
    template.replaceAll("<code>", code.value)
  }

}
