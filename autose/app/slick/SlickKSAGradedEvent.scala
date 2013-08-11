
package slick

import models.MdlKSAGradedEvent
import models.Mdl

trait KSAGradedEventComponent  {
	this: Profile =>
	  
	import profile.simple._

	object KSAGradedEvent extends Table[MdlKSAGradedEvent]("KSAGradedEvent") with Crud[MdlKSAGradedEvent, Long]  {
 
      def vidKSAGradedEvent = column[Long]("idKSAGradedEvent", O.PrimaryKey, O.AutoInc)
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
      
      def selectByGradedEvent(idGradedEvent: Long) = {
	    AppDB.database.withSession { implicit session: Session =>
	      val q = Query(KSAGradedEvent)
	      q.filter(p => p.vGradedEvent === idGradedEvent).elements.toList	      
	    }
      }
      
      def joinGradedEventByKSAQuery(idKSA: Long) = {
	    AppDB.database.withSession { implicit session: Session =>
	      // First pull all graded events that have sub-events associated with idKSA
	      val subEvents = for {
	        (subevent, ge) <- AppDB.dal.KSASubGradedEvent.joinSubGradedEventByKSAQuery(idKSA) innerJoin AppDB.dal.GradedRequirements on (_._3 === _.vGradedEventIndex)
	      } yield (ge.vGradedEventIndex, ge.vGradedEventName, ge.vCourse)
	      // Now pull all events associated with idKSA
	      val events = for {
	        (ge, ksage) <- AppDB.dal.GradedRequirements innerJoin AppDB.dal.KSAGradedEvent on (_.vGradedEventIndex === _.vGradedEvent)
	           if ksage.vKSA === idKSA
	      } yield (ge.vGradedEventIndex, ge.vGradedEventName, ge.vCourse)
	      // Return the union of the two queries
	      //subEvents.union(events)
	      events
	    }	    
      }

      def joinGradedEventByKSA(idKSA: Long) = {
	    AppDB.database.withSession { implicit session: Session =>
          joinGradedEventByKSAQuery(idKSA).elements.toList
	    }
      }
	  
	  def delete(pk: Long) {
	    AppDB.database.withSession { implicit session: Session =>
	      selectQuery(pk).delete
	    }
	  }

	  def insert(vKSAGradedEvent: MdlKSAGradedEvent): Long = {
	    AppDB.database.withSession { implicit session: Session =>
	      KSAGradedEvent.forInsert returning KSAGradedEvent.vidKSAGradedEvent insert MdlKSAGradedEvent(None, vKSAGradedEvent.vKSA, vKSAGradedEvent.vGradedEvent)

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
