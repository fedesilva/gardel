package gardel.core.util

object Bench {

  def nanoBench(block: => Unit) = {
    val startTime = System.nanoTime()
    block
    (BigDecimal(System.nanoTime()) - BigDecimal(startTime))/BigDecimal(1000000)
  }

  def milliBench(block: => Unit) = {
    val startTime = System.currentTimeMillis()
    block
    (System.currentTimeMillis() - startTime)
  }

}

