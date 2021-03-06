package ai.dragonfly.math.vector

import ai.dragonfly.math.util.Demonstrable

import scala.scalajs.js.annotation.{JSExport, JSExportAll}

@JSExportAll
object WeightedVector extends Demonstrable {
  @JSExport("apply")
  def apply(weight:Double, vector:Vector):WeightedVector = {
    new WeightedVector(vector).addWeight(weight)
  }

  override def demo(implicit sb:StringBuilder = new StringBuilder()):StringBuilder = {
    val wv0 = WeightedVector(0.5, Vector3(1.1, 2.5, 0.1))
    sb.append(s"\tWeightedVector: $wv0")
    sb.append(s"\tWeightedVector.weighted: ${wv0.weighted}")
  }

  override def name: String = "WeightedVector"
}

@JSExportAll
case class WeightedVector(unweighted:Vector) {
  private var weight:Double = 0.0
  def addWeight(w: Double): WeightedVector = {
    weight = weight + w
    this
  }
  def weighted: Vector = unweighted.copy().scale(weight)
}