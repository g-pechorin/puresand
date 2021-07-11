package peterlavalle.puresand

import java.io.{File, FileInputStream, FileOutputStream}
import java.util.zip.GZIPInputStream

import org.apache.commons.compress.archivers.tar.{TarArchiveEntry, TarArchiveInputStream}

import scala.sys.process.Process

object Extract {

	def apply(dump: File): File = {

		val tars = {
			os {
				case ("windows", "amd64") =>
					("win64.tar.gz", "Windows.tar.gz")

				case ("mac", "x86_64") =>
					("macos.tar.gz", "macOS.tar.gz")

				case ("linux", "amd64") =>
					("linux64.tar.gz", "Linux.tar.gz")

				case ("linux", "i386") =>
					System.err.println("I should warn you that I'm using the amd64 build on an i386 platform - this probably won't work and will give a no-such-file error")
					("linux64.tar.gz", "Linux.tar.gz")


				// ("linux64.tar.gz", "Linux.tar.gz")
				// ("macos.tar.gz", "macOS.tar.gz")
			} match {
				case (pure, spago) =>

					List(
						("https://github.com/purescript/purescript/releases/download/v0.14.2/" + pure, "purescript/"),
						("https://github.com/purescript/spago/releases/download/0.20.3/" + spago, ""),
					)

			}
		}

		tars.foreach {
			case (tar, fix) =>
				val file: File = dump / tar.hashString

				// download
				import java.net.URL

				import sys.process._
				if (!file.isFile)
					require {
						"" == (new URL(tar) #> file.EnsureParent !!)
					}

				// unpack
				val tarStream =
					new TarArchiveInputStream(new GZIPInputStream(new FileInputStream(file)))

				Stream.continually(tarStream.getNextTarEntry)
					.takeWhile(null !=)
					.filterNot((_: TarArchiveEntry).isDirectory)
					.foreach {
						entry: TarArchiveEntry =>
							val file: File =
								dump / entry.getName.drop(fix)

							val out =
								new FileOutputStream(
									file.EnsureParent
								)
							out.write(
								tarStream.fill(entry.getSize.toInt)
							)
							out.close()

							Mode(
								entry.getMode
							)(file)

					}


		}

		// find out which/what file we'll use
		val spago: File =
			os {
				case ("windows", "amd64") =>
					dump / "spago.exe"

				case ("linux", "i386" | "amd64") |
						 ("mac", "x86_64") =>
					dump / "spago"
			}

		// this might fail on Windows ... it works for me because I'm using cmder and opening stuff with start
		Process("ls -la", dump).!!
			.split("[\r \t]*\n")
			.map("on spago extract > " + _)
			.foreach(println)

		assume(spago.isFile, "assumed that spago file would exist")
		require(spago.canExecute, "spago file must be executable")

		spago
	}

}
