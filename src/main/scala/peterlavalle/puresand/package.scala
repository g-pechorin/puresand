package peterlavalle

import java.io._

import scala.language.postfixOps
import scala.reflect.ClassTag


package object puresand {



	def os[O: ClassTag](act: (String, String) => O): O = {
		act(
			System.getProperty("os.name").takeWhile(' ' != (_: Char)).toLowerCase(),
			System.getProperty("os.arch").takeWhile(' ' != (_: Char)).toLowerCase()
		)

	}

	implicit class extendString(text: String) {
		def /(path: String): File = new File(text) / path

		def drop(prefix: String): String = {
			require(text.startsWith(prefix))
			text.substring(prefix.length)
		}

		def hashString: String = Integer.toHexString(Math.abs(text.hashCode))
	}

	implicit class extendFile(file: File) {
		def EnsureParent: File = {
			ParentFile.EnsureDirectory
			file.getAbsoluteFile
		}

		def ParentFile: File = file.getAbsoluteFile.getParentFile.getAbsoluteFile

		def EnsureDirectory: File = {
			require(file.getAbsoluteFile.isDirectory || file.getAbsoluteFile.mkdirs())
			file.getAbsoluteFile
		}

		def AbsolutePath: String = file.getAbsolutePath.replace('\\', '/')

		def /(path: String): File =
			if (path.startsWith("../"))
				new extendFile(file.getAbsoluteFile.getParentFile) / path.drop(3)
			else
				new File(file.getAbsoluteFile, path).getAbsoluteFile

		def Unlink(): Unit =
			Option(file.listFiles()).toList.flatten.map((_: File).getAbsoluteFile)
				.foreach {
					file: File =>
						if (file.isDirectory)
							file.Unlink()
						require(file.delete())
				}
	}


}
