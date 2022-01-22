import cats.Parallel
import cats.data.{NonEmptyChain, NonEmptyList, Validated}
import cats.implicits._
import cats.effect.implicits._
import cats.instances.EitherInstances
type ErrorOr[A] = Either[String, A]
Parallel[ErrorOr]
List(1.asRight[String]).parSequence

type Valid[A] = Either[NonEmptyChain[String], A]

def validateName(name: String): Valid[String] =
  Either.cond(
    name.forall(_.isLetter),
    name,
    NonEmptyChain("Names should only contain letters")
  )

def validateAge(age: Int): Valid[Int] =
  Either.cond(
    age >= 21,
    age,
    NonEmptyChain("Age should be 21 or more")
  )

case class Person(name: String, age: Int)

def validatePerson(name: String, age: Int): Valid[Person] =
  (validateName(name), validateAge(age)).mapN { (name, age) =>
    Person(name, age)
  }

validatePerson("Leandro", 30)
validatePerson("Martin", 16)
validatePerson("3ug3n14", 31)
validatePerson("Al1c14", 14)