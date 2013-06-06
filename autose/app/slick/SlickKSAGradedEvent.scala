
package slick

import models.MdlKSAGradedEvent
import models.Mdl

trait KSAGradedEventComponent  {
	this: Profile =>
	  
	import profile.simple._

	object KSAGradedEvent extends Table[MdlKSAGradedEvent]("KSAGradedEvent") with Crud[MdlKSAGradedEvent, Long]  {

      def vidKSAGradedEvent = column[Long]("idKSAGradedEvent", O.PrimaryKey)
      def vKSA = column[Long]("KSA")
      def vGradedEvent = column[Long]("GradedEvent")
      def * = vidKSAGradedEvent.? ~ vKSA ~ vGradedEvent<> (MdlKSAGradedEvent.apply _, MdlKSAGradedEvent.unapply _)
      def forInsert = vKSA ~ vGradedEvent <> 
      ({t => MdlKSAGradedEvent(None , t._1, t._2)},
      {(vKSAGradedEvent: MdlKSAGradedEvent) => Some(vKSAGradedEvent.vKSA, vKSAGradedEvent.vGradedEvent)})

	  def allQuery = {
	    AppDB.database.withSession { implicit session: Session =>
	      Query(KSAGradedEvent)
	    }
	  }
	  
	  def all = {
	    AppDB.database.withSession { implicit session: Session =>
	      val q = Query(KSAGradedEvent)
	      q.elements.toList
	    }
	  }
	  
	  def selectQuery(pk: Long)(implicit session: Session) = {
	      val q = Query(KSAGradedEvent)
	      q.filter(p => p.vidKSAGradedEvent === pk)
	  }

      def select(pk: Long) = {
	    AppDB.database.withSession { implicit session: Session =>
	      selectQuery(pk).elements.toList.headOption
	    }
	  }
	  
	  def delete(pk: Long) {
	    AppDB.database.withSession { implicit session: Session =>
	      selectQuery(pk).delete
	    }
	  }

	  def insert(vKSAGradedEvent: MdlKSAGradedEvent) {
	    AppDB.database.withSession { implicit session: Session =>
	      KSAGradedEvent.forInsert insert MdlKSAGradedEvent(None, vKSAGradedEvent.vKSA, vKSAGradedEvent.vGradedEvent)

	    }
	  }
    

	  def update(vKSAGradedEvent: MdlKSAGradedEvent) {
	    AppDB.database.withSession { implicit session: Session =>
	      val q = selectQuery(vKSAGradedEvent.vidKSAGradedEvent.get)
	      val q2 = q.map(vKSAGradedEvent => vKSAGradedEvent.vKSA ~ vKSAGradedEvent.vGradedEvent)
	      q2.update(vKSAGradedEvent.vKSA, vKSAGradedEvent.vGradedEvent)
	    }
	  }

	}
}
