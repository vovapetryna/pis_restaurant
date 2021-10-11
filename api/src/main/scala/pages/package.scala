package object pages {

  def html(title: String)(data: scala.xml.Elem): scala.xml.Elem =
    <html>
      <head>
        <title>{title}</title>
      </head>
      <body>{data}</body>
    </html>

}
