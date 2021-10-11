package pages

object error {
  def build(message: String): scala.xml.Elem =
    html("error") {
      <div>{message}</div>
    }
}
