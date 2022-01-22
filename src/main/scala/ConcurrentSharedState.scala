import cats._
import cats.effect._
import cats.effect.implicits._
import cats.implicits._

object ConcurrentSharedState extends IOApp {
  case class Activity()
  case class Purchase()
  case class Account()
  case class Customer(
    id: Long,
    name: String,
    accounts: List[Account],
    activities: List[Activity],
    purchases: List[Purchase]
  )

  def loadName(id: Long)(ref: Ref[IO, List[String]]): IO[String] = {
    ref.update(logs => s"Loading name for customer $id" :: logs) *>
    IO.pure(s"Customer $id")
  }

  def loadAccounts(id: Long)(ref: Ref[IO, List[String]]): IO[List[Account]] =
    ref.update(logs => s"Loading accounts for customer $id" :: logs) *>
    IO.pure(List(Account()))

  def loadActivities(id: Long)(ref: Ref[IO, List[String]]): IO[List[Activity]] =
    ref.update(logs => s"Loading activities for customer $id" :: logs) *>
    IO.pure(List(Activity()))

  def loadPurchases(id: Long)(ref: Ref[IO, List[String]]): IO[List[Purchase]] =
    ref.update(logs => s"Loading purchases for customer $id" :: logs) *>
    IO.pure(List(Purchase()))

  def loadCustomer(id: Long)(ref: Ref[IO, List[String]]): IO[Customer] =
    (
      loadName(id)(ref),
      loadAccounts(id)(ref),
      loadActivities(id)(ref),
      loadPurchases(id)(ref)
    ).parMapN { case (name, accounts, activities, purchases) =>
      Customer(id, name, accounts, activities, purchases)
    }

  override def run(args: List[String]): IO[ExitCode] = {
    List(2L, 5L, 10L)
      .parTraverse { id =>
        Ref.of[IO, List[String]](Nil).flatMap { ref =>
          loadCustomer(id)(ref)
            .flatTap(_ => ref.get.flatTap(logs => IO.println(logs.mkString("\n"))))
        }
      }
      .flatTap(IO.println)
      .as(ExitCode.Success)
  }
}