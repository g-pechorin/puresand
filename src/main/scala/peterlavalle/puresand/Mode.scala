package peterlavalle.puresand

import java.io._
import java.lang
import java.util.zip.{GZIPInputStream, ZipEntry, ZipInputStream}

import org.apache.commons.compress.archivers.ArchiveEntry
import org.apache.commons.compress.archivers.tar.{TarArchiveEntry, TarArchiveInputStream}

import scala.language.postfixOps
import scala.reflect.ClassTag
import scala.sys.process.{Process, ProcessLogger}

import java.io.File
trait Mode {
	def |(mode: Mode): Mode =
		(file: File) => {
			apply(file)
			mode(file)
		}

	def flag: Int = sys.error("no flag here")

	def apply(file: File): Unit
}

object Mode{


	case object Read extends Mode {
		override def apply(file: File): Unit = if (!file.canRead) require(file.setReadable(true))

		override def flag: Int = 4
	}

	case object Write extends Mode {
		override def apply(file: File): Unit = if (!file.canWrite) require(file.setWritable(true))

		override def flag: Int = 2
	}

	case object Execute extends Mode {
		override def apply(file: File): Unit = if (!file.canExecute) require(file.setExecutable(true))

		override def flag: Int = 1
	}

	def apply(mode:Int) : Mode =
		List(Read, Write, Execute)
		.filterNot(m=>0 == (mode & m.flag))
		.reduce((_:Mode) |(_:Mode))
}
