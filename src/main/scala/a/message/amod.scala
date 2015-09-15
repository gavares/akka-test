package a.message

import scala.util.{Success, Try}

/**
 *
 * Created by gavares on 9/13/15.
 */
object amod {
  case class Processed(cmdMsgId: String, result: Try[Any] = Success("OK")) extends Receipt
}
