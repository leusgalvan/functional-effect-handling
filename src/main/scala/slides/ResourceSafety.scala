package slides

import cats.effect._
import cats.effect.implicits._
import cats._
import cats.implicits._
import cats.effect.unsafe.implicits.global

import java.io.{File, FileWriter}
import scala.concurrent.CancellationException

object ResourceSafety {
  def main(args: Array[String]): Unit = {
    def rand(low: Int, high: Int): IO[Int] =
      IO((low + math.random() * (high - low)).toInt)

    def emitForever(low: Int, high: Int): IO[Unit] = {
      rand(low, high).flatMap { n =>
        IO.println(s"Emitted: $n")
      }.foreverM
    }

    def emitWhile(low: Int, high: Int, p: Int => Boolean): IO[Unit] = {
      rand(low, high).flatMap { n =>
        if(p(n)) IO.println(s"Emitted: $n") *> emitWhile(low, high, p)
        else IO.canceled
      }
    }

    trait RowEncoder[A] {
      def encode(a: A): String
    }
    case class Person(name: String, age: Int)
    implicit val personEncoder: RowEncoder[Person] = new RowEncoder[Person] {
      override def encode(p: Person): String = s"${p.name},${p.age}"
    }
    def writeAll[A](objects: List[A], file: File)(implicit encoder: RowEncoder[A]): IO[Unit] = {
//      for {
//        fw      <- IO.blocking(new FileWriter(file))
//        contents = objects.map(encoder.encode).mkString("\n")
//        _       <- IO.blocking(fw.write(contents))
//        _       <- IO.blocking(fw.flush())
//        _       <- IO.blocking(fw.close())
//      } yield ()
      val contents = objects.map(encoder.encode).mkString("\n")
      def use(fw: FileWriter): IO[Unit] =
        IO.blocking(fw.write(contents)) *> IO.blocking(fw.flush())
      def release(fw: FileWriter): IO[Unit] = IO.blocking(fw.close())
      IO.blocking(new FileWriter(file)).bracket(use)(release)
    }

    val file = new File("test")
    file.createNewFile()

    writeAll(List(Person("Leandro", 36), Person("Martin", 31)), file).unsafeRunSync()

  }
}