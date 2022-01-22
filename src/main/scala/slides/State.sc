import cats._
import cats.implicits._
import cats.data._

case class Person(id: Int, name : String)

class InMemoryPersonDB(initialPersonById: Map[Int, Person]) {
  var personById: Map[Int, Person] = initialPersonById

  def add(person: Person): Unit = personById += (person.id -> person)
  def delete(id: Int): Unit = personById -= id
  def find(id: Int): Option[Person] = personById.get(id)
}

val program1: Option[Person] = {
  val db = new InMemoryPersonDB(Map.empty)
  db.add(Person(1, "Leandro"))
  db.delete(1)
  db.add(Person(1, "Leandro"))
  db.find(1)
}

program1

val program2 = {
  val db = new InMemoryPersonDB(Map.empty)
  val addLeandro = db.add(Person(1, "Leandro"))
  addLeandro
  db.delete(1)
  addLeandro
  db.find(1)
}

program2

type Db = Map[Int, Person]
class InMemoryPersonDBState {
  def add(person: Person): State[Db, Unit] = State.modify(_ + (person.id -> person))
  def delete(id: Int): State[Db, Unit] = State.modify(_ - id)
  def find(id: Int): State[Db, Option[Person]] = State.inspect(_.get(id))
}

val db = new InMemoryPersonDBState

val program1: Option[Person] = {
  val db = new InMemoryPersonDBState
  for {
    _ <- db.add(Person(1, "Leandro"))
    _ <- db.delete(1)
    _ <- db.add(Person(1, "Leandro"))
    p <- db.find(1)
  } yield p
}.runA(Map.empty).value

val program2: Option[Person] = {
  val db = new InMemoryPersonDBState
  val addOp = db.add(Person(1, "Leandro"))
  for {
    _ <- addOp
    _ <- db.delete(1)
    _ <- addOp
    p <- db.find(1)
  } yield p
}.runA(Map.empty).value