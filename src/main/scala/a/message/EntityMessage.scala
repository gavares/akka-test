package a.message

/**
 *
 * Created by gavares on 9/13/15.
 */
trait EntityMessage {
  def entityId: String
  def payload: Any
}
