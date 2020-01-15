/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.apache.mahout.exampleApp1

import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
import org.apache.mahout.sparkbindings.SparkDistributedContext
import org.apache.mahout.sparkbindings
import org.apache.log4j.Logger
import org.apache.mahout._
import org.apache.mahout.math._
import math.Matrix
import math.drm.DistributedContext
import math.drm._
import scalabindings._
import RLikeOps._
import RLikeDrmOps._

import scala.reflect



class sparseMatMatMul(val _m: Int,
                  val _n: Int,
                  val _s: Int,
                  val _para: Int = 5,
                  val _pctDense: Double = .01,
                  val _seed: Long = 1234L) {

  protected implicit var mahoutCtx: DistributedContext = _

  val log: Logger = Logger.getLogger(this.getClass())

  def timeSparseDRMMMul(m: Int = _m,
                      n: Int = _n,
                      s: Int = _s,
                      para: Int = _para,
                      pctDense: Double = _pctDense,
                      seed: Long = _seed): Long = {


    val drmA = drmParallelizeEmpty(m , s, para).mapBlock(){
      case (keys,block:Matrix) =>
        val R =  scala.util.Random
        R.setSeed(seed)
        val blockB = new SparseRowMatrix(block.nrow, block.ncol)
        blockB := {x => if (R.nextDouble < pctDense) R.nextDouble else x }
        (keys -> blockB)
    }
    val drmB = drmParallelizeEmpty(s , n, para).mapBlock(){
      case (keys,block:Matrix) =>
        val R =  scala.util.Random
        R.setSeed(seed + 1)
        val blockB = new SparseRowMatrix(block.nrow, block.ncol)
        blockB := {x => if (R.nextDouble < pctDense) R.nextDouble else x }
        (keys -> blockB)
    }

    var i: Int = 0
    //var drmC: DrmLike[Int] = drmParallelizeEmpty(m,s,para)
    var time = System.currentTimeMillis()
    for (i <- 1 to 10) {
      val drmC = (drmA %*% drmB)

      // trigger computation by calling numRows()
      drmC.numRows()
    }

    time = System.currentTimeMillis() - time

    // this timer is meant as a sanity check and an apples to apples test, for comparing multiplication methods across
    // mahout only.
    time
  }

}

  /*object sparseMatMatMul {

    def main(args: Array[String]): Unit = {

      //defaults
      var master: String = "array[1]"
      var mxM: Int = 200000
      var mxS: Int = 300000
      var mxN: Int = 200000
      var para: Int = 6
      var pctDense: Double = 0.01
      var seed: Long = 1234L
/**
      case :
        (array   (master.getClass()),
             mxM.asInstanceOf[arg[1].getRuntime.get],
             mxS.asInstanceOf[Int],
             mxN.asInstanceOf[Int],
             para.asInstanceOf[Int],
             pctDense.asInstanceOf[Int],
             seed.asInstanceOf[Long]) => (true, true, true, true, true, true, true)
      case : _
        log.fatal("Usage:" +
   Spark       "Spark master  %s"+
          )
**/


      val sparkConf = new SparkConf()
      sparkConf.setMaster(master)
        .setAppName("SparseMatMatMul-example")

      val sctx: SparkContext = new SparkContext(sparkConf)
      implicit val ctx= new SparkDistributedContext(sctx)

      val sMmm = new sparseMatMatMul(mxM,
                        mxS,
                        mxN,
                        para,
                        pctDense,
                        seed)

      val wallClockTime: Long = sMmm.timeSparseDRMMMul()

      val log = Logger.getLogger(sMmm.getClass)

     // log.trace(String.format("tWallclock Tome to multiply drmA %*% drmB =  $L", wallClockTime))

    }
*/}
