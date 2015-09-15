package a.message

import scala.util.{Success, Try}

/**
 *
 * Created by gavares on 9/13/15.
 */

object Message {
  val DELIVERY_ID = "deliveryId"
  val CAUSATION_ID = "causationId"
}

abstract class Message(var metaData: Map[String, Any] = Map.empty) {

  def id: String

  def causedBy(msgOpt: Option[Message]): this.type = msgOpt match {
    case Some(msg) => causedBy(msg)
    case None => this
  }

  def causedBy(msg: Message): this.type = {
    metaData = metaData ++ Map(Message.CAUSATION_ID -> msg.id)
    this
  }


  def withMetaData(md: Map[String, Any]): this.type = {
    metaData = metaData ++ md
    this
  }

  def deliveryId: Option[Long] = metaData.get(Message.DELIVERY_ID) match {
    case Some(id: Long) => Some(id)
    case _ => Option.empty[Long]
  }



  def deliveryReceipt(srcId: String, msg: Any): Receipt = deliveryReceipt(srcId, Success(msg) )

  def deliveryReceipt(srcId: String, result: Try[Any] = Success("OK")): Receipt = {
    if (deliveryId.isDefined) alod.Processed(deliveryId.get, result) else amod.Processed(srcId, result)
  }

}
