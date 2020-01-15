package org.apache.mahout.exampleApp1

import org.apache.log4j.Logger
import org.apache.mahout.exampleApp1._
import org.apache.mahout.sparkbindings.test.DistributedSparkSuite
import org.scalatest.FunSuite

class sparseMatMatmulSuite extends FunSuite with DistributedSparkSuite  {


  val log: Logger = Logger.getLogger(this.getClass())


  test("simpleSparseMatMatMull") {
      val simpleSparseMatMatMull = new sparseMatMatMul(
        _m  = 500,
        _n = 1000,
        _s = 1000,
        _para: Int = 2,
        _pctDense = .01,
        _seed = 1234L,
      )
    val wallClockTime: Long = sMmm.timeSparseDRMMMul()

    assert(wallClockTime > 0)

    log.info("wall clock time: " + wallClockTime)
  }

}
