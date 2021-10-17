package pages

object menu {

  def build(records: Seq[models.MenuRecord]): scala.xml.Elem =
    html("Menu") {
      <div>
        <h2>Update menu records</h2>
        <table>
          <tr>
            <th>Name</th><th>Price</th><th>Delete?</th>
          </tr>
          {records.map { record =>
            <form action={handlers.paths.menuUpdate} method="post" name="record_form" id={s"${record.id}_record_form"}>
              <input type="hidden" id={s"${record.id}_id"} value={s"${record.id}"} name="id"/>
            </form>
            <tr>
              <td>
                <label for={s"${record.id}_name"}></label>
                <input type="text" id={s"${record.id}_name"} value={record.name} name="name" form={s"${record.id}_record_form"}/>
              </td>
              <td>
                <label for={s"${record.id}_price"}></label>
                <input type="number" min="0" step="0.5" id={s"${record.id}_price"} value={s"${record.price}"} name="price" form={s"${record.id}_record_form"}/>
              </td>
              <td>
                <label for={s"${record.id}_delete"}></label>
                <input type="checkbox" id={s"${record.id}_delete"} name="delete" form={s"${record.id}_record_form"}/>
              </td>
              <td>
                <button type="submit" form={s"${record.id}_record_form"}>Update</button>
              </td>
            </tr>
          }}
        </table>
        <br></br>
        <h2>Create menu record</h2>
        <form action={handlers.paths.menu} method="post" name="add_record_form" id="add_record_form">
          <label for="name">Name: </label>
          <input type="text" id="name" name="name"/>
          <label for="price">Price: </label>
          <input type="text" id="price" name="price"/>
          <button type="submit">Create</button>
        </form>

        <a href={handlers.paths.orders}>All Orders List</a>
      </div>
    }

}
