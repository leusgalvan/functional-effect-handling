import java.io.File
import cats._, cats.implicits._, cats.data._

class IdGenerator {
  var counter: Int = 0

  def next(): Int = {
    counter += 1
    counter
  }
}

case class Person(id: Int, name: String, group: Int)

def createPerson(name: String)(idGenerator: IdGenerator): Person = {
  val id = idGenerator.next()
  val group = id % 2
  Person(id, name, group)
}

def createPerson2(name: String)(idGenerator: IdGenerator): Person = {
  val group = idGenerator.next() % 2
  Person(idGenerator.next(), name, group)
}

val idGen = new IdGenerator()
createPerson("Leandro")(idGen)
createPerson("Martin")(idGen)
createPerson("Eugenia")(idGen)

createPerson2("Leandro")(idGen)
createPerson2("Martin")(idGen)
createPerson2("Eugenia")(idGen)

case class Point(x: Double, y: Double)
def distance(p1: Point, p2: Point): Double = {
  val dx = p2.x - p1.x
  val dy = p2.y - p1.y
  math.sqrt(dx * dx + dy * dy)
}

distance(Point(1, 2), Point(4, 6))

def distance2(p1: Point, p2: Point): Double = {
  math.sqrt((p2.x - p1.x) * (p2.x - p1.x) + (p2.y - p1.y) * (p2.y - p1.y))
}

distance2(Point(1, 2), Point(4, 6))

def add(x: Int, y: Int): Int = x + y

def mult(x: Int, y: Int): Int = {
  println("Multiplying numbers...")
  x * y
}

def save(file: File, person: Person): Unit

class IdGenerator2 {
  def next(): State[Int, Int] =
    for {
      _  <- State.modify[Int](_ + 1)
      id <- State.get
    } yield id
}

val idGen = new IdGenerator2
def createPersonMany(name: String, idGenerator: IdGenerator2): State[Int, Person] = {
  val nextId = idGenerator.next()

}

