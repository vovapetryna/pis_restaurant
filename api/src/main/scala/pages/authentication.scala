package pages

object authentication {
  def build(title: String, targetRoute: String): scala.xml.Elem =
    html(title) {
      <div>
        <h3>{title}</h3>
        <form action={targetRoute} method="post">
          <label for="login">Login:</label>
          <input type="text" id="login" name="login"/>
          <label for="password">Password:</label>
          <input type="password" id="password" name="password"/>
          <button type="submit">Register</button>
        </form>
      </div>
    }
}
