package fileutils
import org.scalatest.FunSuite

class ExecTestSuite extends FunSuite {

  test("timeout") {
    import scala.concurrent.duration._
    var l = List[String]()
    intercept[java.util.concurrent.TimeoutException](
      exec("sleep 5000", atMost = 1 milliseconds) { ln =>
        l = ln :: l
      }())
  }

  test("plain echo") {
    var l = List[String]()
    val b = exec(List("echo", "asdf")) { ln =>
      l = ln :: l
    }()
    assertResult(List("asdf")) { l }
    assertResult(0) { b }
  }

  test("plain echo 2") {
    var l = List[String]()
    val b = exec("uname -a")()()
    assertResult(0) { b }
  }

  test("stderr") {
    var l = List[String]()
    val b = exec(List("sh", "sdfsda")) { ln =>
      } { ln =>
      l = ln :: l
    }
    assertResult(List("sh: sdfsda: No such file or directory")) { l }
    assertResult(127) { b }
  }

  test("stdout 2") {
    var l = List[String]()
    val b = exec(List("sh", "-c", "echo a;echo a")) { ln =>
      l = ln :: l
    }()
    assertResult(List("a", "a")) { l }
    assertResult(0) { b }
  }

  test("stdout stderr") {
    var l = List[String]()
    var l2 = List[String]()
    exec(List("sh", "-c", "echo a;dfds;echo b;asdf")) { ln =>
      l2 = ln :: l2
    } { ln =>
      l = ln :: l
    }
    assertResult(List("a", "b")) { l2.reverse }
    assertResult(
      List("sh: dfds: command not found", "sh: asdf: command not found")) {
      l.reverse
    }
  }

}
