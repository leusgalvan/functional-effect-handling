import cats.effect._
import cats.effect.implicits._
import cats._
import cats.implicits._
import cats.effect.unsafe.implicits.global

import java.io.{File, FileInputStream, FileOutputStream}

def write(bytes: Array[Byte], fos: FileOutputStream): IO[Unit] = ???
def read(fis: FileInputStream): IO[Array[Byte]] = ???
def encrypt(bytes: Array[Byte]): IO[Array[Byte]] = ???
def encryptFile(sourceFile: File, destFile: File): IO[Unit] = {
  val acquireReader = IO.blocking(new FileInputStream(sourceFile))
  def close(ac: AutoCloseable): IO[Unit] = IO.blocking(ac.close())
  val acquireWriter = IO.blocking(new FileOutputStream(destFile))

  acquireReader.bracket { reader =>
    acquireWriter.bracket { writer =>
      read(reader).flatMap(encrypt).flatMap(write(_, writer))
    }(close)
  }(close)
}

def encryptFile2(sourceFile: File, destFile: File): IO[Unit] = {
  val acquireReader = IO.blocking(new FileInputStream(sourceFile))
  def close(ac: AutoCloseable): IO[Unit] = IO.blocking(ac.close())
  val acquireWriter = IO.blocking(new FileOutputStream(destFile))

  val readerRes = Resource.make[IO, FileInputStream](acquireReader)(close)
  val writerRes = Resource.make[IO, FileOutputStream](acquireWriter)(close)
  val readerAndWriterRes = readerRes.flatMap(r => writerRes.map(w => (r, w)))
  readerAndWriterRes.use { case (reader, writer) =>
    read(reader).flatMap(encrypt).flatMap(write(_, writer))
  }
}

