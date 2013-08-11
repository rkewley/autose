
package slick

import models.MdlSubGradedEvent
import models.Mdl

trait SubGradedEventComponent  {
	this: Profile =>
	  
	import profile.simple._

	object SubGradedEvent extends Table[MdlSubGradedEvent]("SubGradedEvent") with Crud[MdlSubGradedEvent, Long]  {

      def vidSubGradedEvent = column[Long]("idSubGradedEvent", O.PrimaryKey, O.AutoInc)
      def vGradedEvent = column[Long]("GradedEvent")
      def vDescription = column[String]("Description")
      def vPoints = column[Double]("Points")
      def * = vidSubGradedEvent.? ~ vGradedEvent ~ vDescription ~ vPoints<> (MdlSubGradedEvent.apply _, MdlSubGradedEvent.unapply _)
      def forInsert = vGradedEvent ~ vDescription ~ vPoints <> 
      ({t => MdlSubGradedEvent(None , t._1, t._2, t._3)},
      {(vSubGradedEvent: MdlSubGradedEvent) => Some(vSubGradedEvent.vGradedEvent, vSubGradedEvent.vDescription, vSubGradedEvent.vPoints)})

	  def allQuery = {
	    AppDB.database.withSession { implicit session: Session =>
	      Query(SubGradedEvent)
	    }
	  }
	  
	  def all = {
	    AppDB.database.withSession { implicit session: Session =>
	      val q = Query(SubGradedEvent)
	      q.elements.toList
	    }
	  }
	  
	  def selectQuery(pk: Long)(implicit session: Session) = {
	      val q = Query(SubGradedEvent)
	      q.filter(p => p.vidSubGradedEvent === pk)
	  }

      def select(pk: Long) = {
	    AppDB.database.withSession { implicit session: Session =>
	      selectQuery(pk).elements.toList.headOption
	    }
	  }
	  
      def selectByGradedEvent(gradedEvent: Long) = {
	    AppDB.database.withSession { implicit session: Session =>
	      val q = Query(SubGradedEvent)
	      q.filter(p => p.vGradedEvent === gradedEvent).elements.toList
	    }
	  }
	  
	  def delete(pk: Long) {
	    AppDB.database.withSession { implicit session: Session =>
	      selectQuery(pk).delete
	    }
	  }

	  def insert(vSubGradedEvent: MdlSubGradedEvent): Long = {
	    AppDB.database.withSession { implicit session: Session =>
	      SubGradedEvent.forInsert returning SubGradedEvent.vidSubGradedEvent insert MdlSubGradedEvent(None, vSubGradedEvent.vGradedEvent, vSubGradedEvent.vDescription, vSubGradedEvent.vPoints)

	    }
	  }
    

	  def update(vSubGradedEvent: MdlSubGradedEvent) {
	    AppDB.database.withSession { implicit session: Session =>
	      val q = selectQuery(vSubGradedEvent.vidSubGradedEvent.get)
	      val q2 = q.map(vSubGradedEvent => vSubGradedEvent.vGradedEvent ~ vSubGradedEvent.vDescription ~ vSubGradedEvent.vPoints)
	      q2.update(vSubGradedEvent.vGradedEvent, vSubGradedEvent.vDescription, vSubGradedEvent.vPoints)
	    }
	  }

	}
}
