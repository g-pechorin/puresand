package peterlavalle.puresand

import java.io.File

import org.apache.commons.codec.digest.DigestUtils
import peterlavalle.puresand.NodeJS.T._

object TempDir {

	def apply[O](root: File = null)(act: File => O): O =
		cache(root) {
			folder: File =>
				val out: O = act(folder)
				folder.Unlink()
				out
		}

	def cache[O](root: File = null)(act: File => O): O = {
		import NodeJS.T._

		val folder: File =
			(if (null != root)
				root.getAbsoluteFile
			else
				"target" / DigestUtils
					.md5Hex(
						Thread.currentThread().getStackTrace
							.foldLeft("")((_: String) + (_: StackTraceElement) + "\n")
					).toUpperCase()).EnsureDirectory

		act(folder)
	}

}
