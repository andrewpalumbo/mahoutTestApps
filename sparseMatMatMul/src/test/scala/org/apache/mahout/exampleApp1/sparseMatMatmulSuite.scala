package org.apache.mahout.exampleApp1

import org.apache.log4j.Logger
import org.apache.mahout.exampleApp1._
import org.apache.mahout.sparkbindings.test.DistributedSparkSuite
import org.apache.mahout.sparkbindings.test.LoggerConfiguration
import org.scalatest.FunSuite

class sparseMatMatmulSuite extends FunSuite with DistributedSparkSuite  {


  val log: Logger = Logger.getLogger(this.getClass)


  test("simpleSparseMatMatMull") {

    val sMmm = sparseMatMatMul( )
    val wallClockTime: Long = sMmm.timeSparseDRMMMul(_m  = 500, _n = 1000, _s = 1000, _para  = 2, _pctDense = .01, _seed = 1234L)

    assert(wallClockTime > 0)

    log.info("wall clock time: " + wallClockTime)
  }

}
