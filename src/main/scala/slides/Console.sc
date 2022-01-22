def log(msg: String): Unit = println(msg)

case class OrderItem(description: String, amount: Double)
//case class Order(id: Int, items: List[OrderItem]) {
//  def total: Double = items.map(_.amount).sum
//}
case class Order(id: Int, items: List[OrderItem]) {
  def total: Double = {
    println(s"Computing total for order $id")
    items.map(_.amount).sum
  }
}
//def discountedPrice(order: Order): Double = {
//  if(order.total > 2000) order.total * 0.8
//  else                   order.total
//}

def discountedPrice(order: Order): Double = {
  val total = order.total
  if(total > 2000) total * 0.8
  else             total
}

val items = List(OrderItem("Cellphone", 700), OrderItem("Laptop", 1400))
val order = Order(1, items)
discountedPrice(order)

