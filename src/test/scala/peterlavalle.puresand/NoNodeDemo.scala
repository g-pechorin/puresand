package peterlavalle.puresand


import scala.sys.error

object NoNodeDemo {

	def main(args: Array[String]): Unit = {
		//
		//val pure =
		//	"https://github.com/purescript/purescript/releases/download/v0.14.2/win64.tar.gz"
		//
		//val spago =
		//	"https://github.com/purescript/spago/releases/download/0.20.3/Windows.tar.gz"
		//

		val dump = "target" / "nonode"

		Extract(dump)
		error("did i do it?")
	}

}
