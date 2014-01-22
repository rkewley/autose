
package slick

import models.MdlMappingGradedEvent
import models.Mdl

trait MappingGradedEventComponent  {
	this: Profile =>
	  
	import profile.simple._

	object MappingGradedEvent extends Table[MdlMappingGradedEvent]("MappingGradedEvent") with Crud[MdlMappingGradedEvent, Long]  {

      def vidMappingGradedEvent = column[Long]("idMappingGradedEvent", O.PrimaryKey, O.AutoInc)
      def vGradedEvent = column[Long]("GradedEvent")
      def vGradedEventAMS = column[Long]("GradedEventAMS")
      def * = vidMappingGradedEvent.? ~ vGradedEvent ~ vGradedEventAMS<> (MdlMappingGradedEvent.apply _, MdlMappingGradedEvent.unapply _)
      def forInsert = vGradedEvent ~ vGradedEventAMS <> 
      ({t => MdlMappingGradedEvent(None , t._1, t._2)},
      {(vMappingGradedEvent: MdlMappingGradedEvent) => Some(vMappingGradedEvent.vGradedEvent, vMappingGradedEvent.vGradedEventAMS)})

	  def allQuery = {
	    AppDB.database.withSession { implicit session: Session =>
	      Query(MappingGradedEvent)
	    }
	  }
	  
	  def all = {
	    AppDB.database.withSession { implicit session: Session =>
	      val q = Query(MappingGradedEvent)
	      q.elements.toList
	    }
	  }
	  
	  def selectQuery(pk: Long)(implicit session: Session) = {
	      val q = Query(MappingGradedEvent)
	      q.filter(p => p.vidMappingGradedEvent === pk)
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

	  def insert(vMappingGradedEvent: MdlMappingGradedEvent): Long = { 
	    AppDB.database.withSession { implicit session: Session =>
          MappingGradedEvent.forInsert returning MappingGradedEvent.vidMappingGradedEvent insert MdlMappingGradedEvent(None, vMappingGradedEvent.vGradedEvent, vMappingGradedEvent.vGradedEventAMS)

	    }
	  }
    

	  def update(vMappingGradedEvent: MdlMappingGradedEvent) {
	    AppDB.database.withSession { implicit session: Session =>
	      val q = selectQuery(vMappingGradedEvent.vidMappingGradedEvent.get)
	      val q2 = q.map(vMappingGradedEvent => vMappingGradedEvent.vGradedEvent ~ vMappingGradedEvent.vGradedEventAMS)
	      q2.update(vMappingGradedEvent.vGradedEvent, vMappingGradedEvent.vGradedEventAMS)
	    }
	  }

	}
}
