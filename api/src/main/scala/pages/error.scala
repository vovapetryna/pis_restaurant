package pages

object error {
  def build(message: String, details: String): scala.xml.Elem =
    html("error") {
      <div>
        <h3>{message}</h3>
        <p>{details}</p>
      </div>
    }
}
