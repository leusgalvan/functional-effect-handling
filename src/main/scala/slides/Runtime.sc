import cats.effect.IO

case class OrderItems
case class Order(

                )
def generateReport(order: List[Order]): Report

def fetchOrder(id: Long): IO[Order]