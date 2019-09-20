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

import org.apache.log4j.Logger
import org.apache.mahout._
import common.RandomUtils
import org.apache.mahout.math._
import {Matrices, Matrix}
import drm._
import scalabindings._
import RLikeOps._
import DrmLike._



class sparseMatMatMul(val _m: Int,
                  val _n: Int,
                  val _s: Int,
                  val _para: Int = 5,
                  val _pctDense: Double = .01,
                  val _seed: Long = 1234L) {

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

  var time = System.currentTimeMillis()

  val drmC = drmA %*% drmB

  // trigger computation
  drmC.numRows()

  time = System.currentTimeMillis() - time

  time
  }

}

  object sparseMatMatMul {
    def main(args: Array[String]): Unit = {
    val sMmm = new sparseMatMatMul(300000,
                          300000,
                          300000,
                          5,
                          .01,
                          1234L)
      val wallClopckTime: Long = sMmm.timeSparseDRMMMul()
      val log = Logger.getLogger(sMmm.getClass)
      log.trace(String.format("tWallclock Tome to multiply drmA %*% drmB =  $L",wallClopckTime))

    }
}
