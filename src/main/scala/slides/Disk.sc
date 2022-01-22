import java.io.FileWriter
import scala.util.Using

case class Event(description: String)

class EventLog(filename: String) {
  def log(event: Event): Unit =
    Using(new FileWriter(filename, true)) { writer =>
      writer.write(s"${event.description}\n")
      writer.flush()
    }
}

case class OrderItem(description: String, amount: Double)
case class Order(id: Int, items: List[OrderItem])

def logItemsBought(order: Order)(eventLog: EventLog): Unit = {
  order.items.foreach { _ => eventLog.log(Event("Item bought")) }
}

val items = List(OrderItem("Cellphone", 700), OrderItem("Laptop", 1400))
val order = Order(1, items)
val log = new EventLog("functional-effect-handling/test.txt")

logItemsBought(order)(log)