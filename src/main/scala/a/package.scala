import java.util.UUID

import org.joda.time.DateTime

import scala.util.Random

/**
 *
 * Created by gavares on 9/12/15.
 */
package object a {

  def genCartId = Random.alphanumeric.take(8).mkString
  def uuid = UUID.randomUUID().toString
  def now = DateTime.now


}
