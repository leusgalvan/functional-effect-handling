import cats.effect._
import cats.effect.implicits._
import cats._
import cats.data.StateT
import cats.implicits._
import cats.effect.unsafe.implicits.global

case class Activity()
case class Purchase()
case class Account()
case class Customer(name: String, accounts: List[Account], purchases: List[Purchase], activities: List[Activity])

def loadName(customerId: Long)(ref: Ref[IO, List[String]]): IO[String] =
  "Leandro".pure[IO].flatTap(_ => ref.update(logs => s"Loaded name for customer $customerId" :: logs))

def loadAccounts(customerId: Long)(ref: Ref[IO, List[String]]): IO[List[Account]] = {
  Nil.pure[IO].flatTap { accounts =>
    ref.update(logs => s"Loaded ${accounts.size} accounts for customer $customerId" :: logs)
  }
}

def loadPurchases(customerId: Long)(ref: Ref[IO, List[String]]): IO[List[Purchase]] =
  Nil.pure[IO].flatTap { purchases =>
    ref.update(logs => s"Loaded ${purchases.size} purchases for customer $customerId" :: logs)
  }

def loadActivities(customerId: Long)(ref: Ref[IO, List[String]]): IO[List[Activity]] =
  Nil.pure[IO].flatTap { activities =>
    ref.update(logs => s"Loaded ${activities.size} activities for customer $customerId" :: logs)
  }

def loadCustomer(customerId: Long)(ref: Ref[IO, List[String]]): IO[Customer] = {
  (loadName(customerId)(ref),
   loadAccounts(customerId)(ref),
   loadPurchases(customerId)(ref),
   loadActivities(customerId)(ref)
  ).parMapN { (name, accounts, purchases, activities) =>
    Customer(name, accounts, purchases, activities)
  }.flatTap { customer =>
    ref.update(logs => s"Loaded customer $customer" :: logs)
  }
}

val ids = List(12L, 54L, 24L, 90L)
val program =
  Ref.of[IO, List[String]](Nil).flatMap { ref =>
    ids.parTraverse(loadCustomer(_)(ref)).flatTap { _ =>
      ref.get.flatMap(logs => IO.println(logs.mkString("\n")))
    }
  }

val program2 =
  ids.parTraverse { id =>
    Ref.of[IO, List[String]](List.empty[String]).flatMap { ref =>
      loadCustomer(id)(ref).flatTap { _ =>
        ref.get.flatMap(logs => IO.println(logs.mkString("\n")))
      }
    }
  }

//program.unsafeRunSync()
program2.unsafeRunSync()