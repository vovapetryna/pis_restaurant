package pages

object error {
  def build(message: String): scala.xml.Elem =
    html("error") {
      <div>
        <h3>{message}</h3>
        <a href={handlers.paths.index}>Return to Main</a>
      </div>
    }
}
