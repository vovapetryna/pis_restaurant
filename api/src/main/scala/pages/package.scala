package object pages {

  def html(title: String)(data: scala.xml.Elem): scala.xml.Elem =
    <html>
      <head>
        <meta http-equiv="Content-Type" content="text/html;charset=UTF-8"/>
        <meta charset="utf-8"/>
        <title>{title}</title>
      </head>
      <body>
        {data}
        <a href={handlers.paths.logout}>Logout</a>
      </body>
    </html>

}
