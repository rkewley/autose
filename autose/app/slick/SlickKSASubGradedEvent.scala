
package slick

import models.MdlKSASubGradedEvent
import models.Mdl
import scala.slick.lifted._

trait KSASubGradedEventComponent  {
	this: Profile =>
	  
	import profile.simple._

	object KSASubGradedEvent extends Table[MdlKSASubGradedEvent]("KSASubGradedEvent") with Crud[MdlKSASubGradedEvent, Long]  {

      def vidKSASubGradedEvent = column[Long]("idKSASubGradedEvent", O.PrimaryKey)
      def vKSA = column[Long]("KSA")
      def vSubGradedEvent = column[Long]("SubGradedEvent")
      def * = vidKSASubGradedEvent.? ~ vKSA ~ vSubGradedEvent <> (MdlKSASubGradedEvent.apply _, MdlKSASubGradedEvent.unapply _)
      def forInsert = vKSA ~ vSubGradedEvent <> 
      ({t => MdlKSASubGradedEvent(None , t._1, t._2)},
      {(vKSASubGradedEvent: MdlKSASubGradedEvent) => Some(vKSASubGradedEvent.vKSA, vKSASubGradedEvent.vSubGradedEvent)})

	  def allQuery = {
	    AppDB.database.withSession { implicit session: Session =>
	      Query(KSASubGradedEvent)
	    }
	  }
	  
	  def all = {
	    AppDB.database.withSession { implicit session: Session =>
	      val q = Query(KSASubGradedEvent)
	      q.elements.toList
	    }
	  }
	  
	  def selectQuery(pk: Long)(implicit session: Session) = {
	      val q = Query(KSASubGradedEvent)
	      q.filter(p => p.vidKSASubGradedEvent === pk)
	  }
	  
      def selectBySubGradedEvent(idSubGradedEvent: Long) = {
	    AppDB.database.withSession { implicit session: Session =>
	      val q = Query(KSASubGradedEvent)
	      q.filter(p => p.vSubGradedEvent === idSubGradedEvent).elements.toList	      
	    }
      }	  

      def select(pk: Long) = {
	    AppDB.database.withSession { implicit session: Session =>
	      selectQuery(pk).elements.toList.headOption
	    }
	  }
	  
      def joinSubGradedEventByKSAQuery(idKSA: Long) = {
	    AppDB.database.withSession { implicit session: Session =>
	      val result1 = for {
	        (ge, ksage) <- AppDB.dal.SubGradedEvent innerJoin AppDB.dal.KSASubGradedEvent on (_.vidSubGradedEvent === _.vSubGradedEvent)
	           if ksage.vKSA === idKSA
	      } yield (ge.vidSubGradedEvent, ge.vDescription, ge.vGradedEvent)
	      result1
	    }	    
      }

      def joinSubGradedEventByKSA(idKSA: Long) = {
	    AppDB.database.withSession { implicit session: Session =>
          joinSubGradedEventByKSAQuery(idKSA).elements.toList
	    }
      }
	  
	  def delete(pk: Long) {
	    AppDB.database.withSession { implicit session: Session =>
	      selectQuery(pk).delete
	    }
	  }

	  def insert(vKSASubGradedEvent: MdlKSASubGradedEvent) {
	    AppDB.database.withSession { implicit session: Session =>
	      KSASubGradedEvent.forInsert insert MdlKSASubGradedEvent(None, vKSASubGradedEvent.vKSA, vKSASubGradedEvent.vSubGradedEvent)

	    }
	  }
    

	  def update(vKSASubGradedEvent: MdlKSASubGradedEvent) {
	    AppDB.database.withSession { implicit session: Session =>
	      val q = selectQuery(vKSASubGradedEvent.vidKSASubGradedEvent.get)
	      val q2 = q.map(vKSASubGradedEvent => vKSASubGradedEvent.vKSA ~ vKSASubGradedEvent.vSubGradedEvent)
	      q2.update(vKSASubGradedEvent.vKSA, vKSASubGradedEvent.vSubGradedEvent)
	    }
	  }

	}
}
