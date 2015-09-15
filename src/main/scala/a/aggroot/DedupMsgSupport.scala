package a.aggroot

import a.message.Message
import akka.actor.Actor.Receive

/**
 *
 * Created by gavares on 9/13/15.
 */
trait DedupMsgSupport {

  private var msgs: Vector[String] = Vector.empty[String]

  def dedup(duplicateHndlr: (Message) => Unit): Receive = {
    case m: Message if isDuplicate(m) => duplicateHndlr(m)
  }

  def isDuplicate(m: Message) = msgs.contains(m.id)

  def messageProcessed(m: Message) = msgs = msgs :+ m.id

}
