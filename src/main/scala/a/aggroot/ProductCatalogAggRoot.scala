package a.aggroot

import a.PermissionDeniedError
import a.aggroot.CompanyView.Protocol.GetCompany
import a.commands.ProductCommand
import a.commands.ProductCommands._
import a.events.ProductEvent
import a.events.ProductEvents._
import akka.actor.ActorRef


/**
 *
 * Created by gavares on 9/13/15.
 */
case class Product(sku: String, name: String, internalDesc: String, desc: String, active: Boolean)


case class ProductCatalog(companyId: String, products: Seq[Product] = Nil)
  extends AggregateState[ProductEvent, ProductCatalog]
{

  def apply(evt: ProductEvent): ProductCatalog = evt match {

    case e: ProductCreated =>
      val p = Product(e.sku, e.displayName, e.desc, e.internalDesc, active = false)
      val updatedProducts = products.filterNot(_.sku == e.sku) :+ p
      setProds(updatedProducts)

    case e: ProductActivated => mapProduct(e.sku)( _.copy(active = true) )

    case e: ProductDeactivated => mapProduct(e.sku)( _.copy(active = false) )

    case e: ProductNameUpdated => mapProduct(e.sku){ p => p.copy(name = e.name) }

    case e: ProductDescUpdated => mapProduct(e.sku)(_.copy(desc = e.desc))

    case e: ProductInternalDescUpdated => mapProduct(e.sku)(_.copy(internalDesc = e.internalDesc))
  }

  def withSku(sku: String): Option[Product] = products.find(_.sku == sku)

  private def mapProduct(sku: String)(f: Product => Product) = {
    val updatedProducts = products.map {
      case p: Product if p.sku == sku => f(p)
      case p => p
    }
    setProds(updatedProducts)
  }

  private def setProds(products: Seq[Product]) = copy(products = products)
}


class ProductCatalogAggRoot(companyMgr: ActorRef) extends AggRoot[ProductCommand, ProductEvent, ProductCatalog] {

  override def handleCommand(cmd: ProductCommand) = {
    val evt = cmd match {
      case c: CreateProduct => ProductCreated(c.companyId, c.sku, c.name, c.desc, c.internalDesc)
      case c: Activate => ProductActivated(c.companyId, c.sku)
      case c: Deactivate => ProductDeactivated(c.companyId, c.sku)
      case c: SetDesc => ProductDescUpdated(c.companyId, c.sku, c.desc)
      case c: SetInternalDesc => ProductInternalDescUpdated(c.companyId, c.sku, c.internalDesc)
      case c: SetName => ProductNameUpdated(c.companyId, c.sku, c.name)
    }

    val user = cmd.userId
    validateWrite(cmd, user) {
      doPersist(evt)
    }
  }

  def companyId = state.companyId

  private def validateWrite(c: ProductCommand, uId: String)(blk: => Unit) = {
    companyMgr ! GetCompany(c.companyId)
    context.become {
      case Some(c: Company) if c.userCanWrite(uId) =>
        blk
        unstashAll()
        context.unbecome()

      case None | Some(_) =>
        acknowledgeCommand(scala.util.Failure(PermissionDeniedError(uId, c.cmdName)))
        unstashAll()
        context.unbecome()

      case _ => stash()
    }
  }

  override val stateFactory: AggregateStateFactory[ProductEvent, ProductCatalog] = new AggregateStateFactory[ProductEvent, ProductCatalog] {

    override def apply = {
      case evt: ProductCreated => new ProductCatalog(evt.companyId).apply(evt)
    }
  }
}
