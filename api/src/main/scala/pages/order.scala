package pages

object order {

  def create(records: Seq[models.MenuRecord]): scala.xml.Elem =
    html("order") {
      <div>
        <h3>Order</h3>
        <form action={handlers.paths.order} method="post" id="order_form"></form>
        <table>
          {records.map { record =>
            <tr>
              <input type="hidden" id={s"${record.id}_id"} name="record_id" value={s"${record.id}"} form="order_form"/>
              <td>{record.name}</td>
              <td>{record.price}</td>
              <td>
                <label for={s"${record.id}_count"}></label>
                <input type="number" min="0" max="10" step="1" id={s"${record.id}"} name="count" form="order_form"/>
              </td>
            </tr>
          }}
        </table>
        <button type="submit" form="order_form">Create</button>
        <a href={handlers.paths.orders}>My Orders</a>
      </div>
    }

  def list(records: Seq[models.Order.Represent]): scala.xml.Elem =
    html("orders") {
      <div>
        <h3>Orders list</h3>
        <table>
          <tr>
            <th>User</th><th>Status</th><th>Name</th><th>Count</th>
          </tr>
          {records.flatMap { record =>
          record.records.map { row =>
            <tr>
              <td>{record.user.login}</td>
              <td>{record.status.value}</td>
              <td>{row.recordName}</td>
              <td>{row.count}</td>
            </tr>
          }
        }}
        </table>
        <a href={handlers.paths.order}>New Order</a>
      </div>
    }

  def updateList(records: Seq[models.Order.Represent]): scala.xml.Elem =
    html("orders") {
      <div>
        <h3>All Orders</h3>
        <table>
          <tr>
            <th>User</th><th>Update Status</th><th>Name</th><th>Count</th>
          </tr>
          {records.flatMap { record =>
          record.records.map { row =>
            <tr>
              <td>{record.user.login}</td>
              <td>
                <form action={handlers.paths.orderUpdate} method="post" id={s"${record.id}_${row.id}_update"}>
                  <input type="hidden" id={s"${record.id}_${row.id}_id"} name="id" value={s"${record.id}"} form={s"${record.id}_${row.id}_update"}/>
                  <select name="status" id={s"${record.id}_${row.id}_status"} form={s"${record.id}_${row.id}_update"}>
                    {models.Status.values.map(v => <option value={v.value}>{v.value}</option>)}
                  </select>
                </form>
              </td>
              <td>{record.status.value}</td>
              <td>{row.recordName}</td>
              <td>{row.count}</td>
              <td><button type="submit" form={s"${record.id}_${row.id}_update"}>Update</button></td>
            </tr>
          }
        }}
        </table>
        <a href={handlers.paths.menu}>Update Menu</a>
      </div>
    }

}
