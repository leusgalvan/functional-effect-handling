sealed abstract class AccountError(msg: String)
  extends Exception(msg)

case class InsufficientBalanceError(
  amountToWithdraw: Double,
  balance: Double
) extends AccountError(
  s"Can't withdraw $amountToWithdraw. Insufficient balance: $balance"
)

case class Person(id: Int, name: String)
case class Account(id: Int, balance: Double, owner: Person) {
  def withdraw(amount: Double): Either[AccountError, Account] =
    if(amount > balance)
      Left(InsufficientBalanceError(amount, balance))
    else
      Right(copy(balance = balance - amount))
}

val joe = Person(1, "Joe")
val account = Account(1, 1000, joe)
//account.withdraw(1500)

val program1: Double = {
  val person = Person(1, "Joe")
  val account = Account(1, 1000, person)
  if(person.name == "Joe") {
    5000
  } else {
    account.withdraw(2000).fold(_ => 0, _.balance)
  }
}

val program2: Double = {
  val person = Person(1, "Joe")
  val account = Account(1, 1000, person)
  val withdrawnAccount = account.withdraw(2000)
  if(person.name == "Joe") {
    5000
  } else {
    withdrawnAccount.fold(_ => 0, _.balance)
  }
}