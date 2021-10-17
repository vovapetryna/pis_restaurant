package pages

object auth {
  def base(title: String, targetRoute: String, redirectRoute: String, redirectText: String): scala.xml.Elem =
    html(title) {
      <div>
        <h3>{title}</h3>
        <form action={targetRoute} method="post">
          <label for="login">Login:</label>
          <input type="text" id="login" name="login"/>
          <label for="password">Password:</label>
          <input type="password" id="password" name="password"/>
          <button type="submit">{title}</button>
        </form>
        <a href={redirectRoute}>{redirectText}</a>
      </div>
    }

  def registration: scala.xml.Elem =
    base("Registration", handlers.paths.registration, handlers.paths.authorization, "Authorize")

  def authorization: scala.xml.Elem =
    base("Authorization", handlers.paths.authorization, handlers.paths.registration, "Register")

  def logout: scala.xml.Elem =
    html("logout") {
      <div>
        <h3>Logged out</h3>
        <a href="/">Return to main</a>
      </div>
    }
}
