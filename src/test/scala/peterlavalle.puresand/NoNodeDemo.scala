package peterlavalle.puresand

import peterlavalle.puresand.NoNode.extract

import scala.sys.error

object NoNodeDemo {

	def main(args: Array[String]): Unit = {
		import NodeJS.T._
		//
		//val pure =
		//	"https://github.com/purescript/purescript/releases/download/v0.14.2/win64.tar.gz"
		//
		//val spago =
		//	"https://github.com/purescript/spago/releases/download/0.20.3/Windows.tar.gz"
		//

		val dump = "target" / "nonode"

		extract(dump)
		error("did i do it?")
	}

}
