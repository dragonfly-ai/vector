package ai.dragonfly.math.stats.kernel

/**
 * Created by clifton on 5/16/15.
 */

import ai.dragonfly.math.util.Demonstrable
import ai.dragonfly.math.vector.Vector

import scala.scalajs.js.annotation.{JSExport, JSExportAll}

@JSExportAll
object Kernel extends Demonstrable {
  override def demo(implicit sb:StringBuilder = new StringBuilder()):StringBuilder = {
    val r = 32
    val dk = DiscreteKernel(GaussianKernel(r))

    sb.append(dk.totalWeights)

    var count = 0
    for (dy <- -r to r; dx <- -r to r) count = count + 1
    sb.append(count)
  }

  override def name: String = "Kernel"
}

@JSExportAll
trait Kernel {
  val radius: Double
  lazy val radiusSquared:Double = radius * radius

  def weight(magnitudeSquared: Double): Double
  def weight(v: Vector): Double
  def weight(v1: Vector, v2: Vector): Double = weight(v1.copy().subtract(v2))
//  def scaledWeight(v1: VectorN, v2: VectorN): Double = weight(v1, v2) * v1.getFrequency * v2.getFrequency

  def distance(v: Vector): Double = v.magnitude()
  def distance(v1: Vector, v2: Vector): Double = v1.copy().subtract(v2).magnitude()

  lazy val discretize: DiscreteKernel = DiscreteKernel(this)
}

@JSExportAll
object GaussianKernel {
  @JSExport("apply")
  def apply(radius: Double): GaussianKernel = {
    val sigma: Double = radius / 3.0
    val denominator: Double = 2.0 * (sigma * sigma)
    GaussianKernel(radius, sigma, denominator, 1.0 / Math.sqrt(Math.PI * denominator))
  }
}

@JSExportAll
case class GaussianKernel(radius: Double, sigma: Double, denominator: Double, c: Double) extends Kernel {
  def weight(v: Vector): Double = weight(v.magnitudeSquared())

  def weight(magnitudeSquared: Double): Double = {
    if (magnitudeSquared > radiusSquared) 0.0
    else c * Math.pow(Math.E, -(magnitudeSquared / denominator))
  }
}

@JSExportAll
case class EpanechnikovKernel(radius: Double) extends Kernel {
  def weight(v: Vector): Double = weight(v.magnitudeSquared())

  def weight(magnitudeSquared: Double): Double = {
    if (magnitudeSquared > radiusSquared) 0.0
    else 0.75 - 0.75 * (magnitudeSquared / radiusSquared)
  }

  //  def naiveWeight(v: VectorN): Double = naiveWeight(v.magnitudeSquared)
  //
  //  def naiveWeight(magnitudeSquared: Double): Double = {
  //    val l = Math.sqrt(magnitudeSquared) / radius
  //    if (l > 1) 0.0
  //    else 0.75 - 0.75 * (l * l)
  //  }
}

@JSExportAll
case class UniformKernel(radius: Double) extends Kernel {
  def weight(v: Vector): Double = weight(v.magnitudeSquared())

  def weight(magnitudeSquared: Double): Double = {
    if (magnitudeSquared > radiusSquared) 0.0
    else 1.0 / (Math.PI * radiusSquared)
  }
}

@JSExportAll
object DiscreteKernel {
  @JSExport("apply")
  def apply(k: Kernel): DiscreteKernel = {
    val maxMagSquared: Int = Math.ceil(k.radiusSquared).toInt
    val weights = new Array[Double](maxMagSquared + 1)
    for (d <- 0 to maxMagSquared) weights(d) = k.weight(d)
    DiscreteKernel(k.radius, weights)
  }
}

@JSExportAll
case class DiscreteKernel(radius: Double, weights: Array[Double]) extends Kernel {

  lazy val totalWeights:Double = {
    var total = 0.0
    val r = radius.toInt
    for (dy <- -r to r; dx <- -r to r) total = total + weight(dx*dx + dy*dy)
    total
  }

  override def weight(v: Vector): Double = weight(v.magnitudeSquared())

  def weight(magnitudeSquared: Double): Double = {
    if (magnitudeSquared > radiusSquared) 0.0
    else weights(magnitudeSquared.toInt)
  }

}
