package org.apache.mahout.exampleApp1

import org.apache.log4j.Logger
import org.apache.mahout.exampleApp1._
import org.apache.mahout.sparkbindings.test.DistributedSparkSuite
import org.apache.mahout.sparkbindings.test.LoggerConfiguration
import org.scalatest.FunSuite

class sparseMatMatmulSuite extends FunSuite with DistributedSparkSuite  {


  val log: Logger = Logger.getLogger(this.getClass)


  test("simpleSparseMatMatMull") {

    val sMmm = new sparseMatMatMul(500, 100, 1000, 2, .01, 1234L)
    val wallClockTime: Long =
      sMmm.timeSparseDRMMMul(m  = 500, n = 1000, s = 1000, para  = 2, pctDense = .01, seed = 1234L)

    assert(wallClockTime > 0)

    log.info("wall clock time: " + wallClockTime)
  }

}
