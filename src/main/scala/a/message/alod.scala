package a.message

import scala.util.{Success, Try}

/**
 * At-Least-Once Delivery protocol
 */
/***/
object alod {

  trait Delivered extends Receipt {
    def deliveryId: Long
  }

  case class Received(deliveryId: Long) extends Delivered

  case class Processed(deliveryId: Long, result: Try[Any] = Success("OK")) extends Delivered
}
