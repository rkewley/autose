
package slick

import models.MdlMappingSubEvent
import models.Mdl

trait MappingSubEventComponent  {
	this: Profile =>
	  
	import profile.simple._

	object MappingSubEvent extends Table[MdlMappingSubEvent]("MappingSubEvent") with Crud[MdlMappingSubEvent, Long]  {

      def vidMappingSubEvent = column[Long]("idMappingSubEvent", O.PrimaryKey, O.AutoInc)
      def vSubEvent = column[Long]("SubEvent")
      def vSubEventAMS = column[Long]("SubEventAMS")
      def * = vidMappingSubEvent.? ~ vSubEvent ~ vSubEventAMS<> (MdlMappingSubEvent.apply _, MdlMappingSubEvent.unapply _)
      def forInsert = vSubEvent ~ vSubEventAMS <> 
      ({t => MdlMappingSubEvent(None , t._1, t._2)},
      {(vMappingSubEvent: MdlMappingSubEvent) => Some(vMappingSubEvent.vSubEvent, vMappingSubEvent.vSubEventAMS)})

	  def allQuery = {
	    AppDB.database.withSession { implicit session: Session =>
	      Query(MappingSubEvent)
	    }
	  }
	  
	  def all = {
	    AppDB.database.withSession { implicit session: Session =>
	      val q = Query(MappingSubEvent)
	      q.elements.toList
	    }
	  }
	  
	  def selectQuery(pk: Long)(implicit session: Session) = {
	      val q = Query(MappingSubEvent)
	      q.filter(p => p.vidMappingSubEvent === pk)
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

	  def insert(vMappingSubEvent: MdlMappingSubEvent): Long = { 
	    AppDB.database.withSession { implicit session: Session =>
          MappingSubEvent.forInsert returning MappingSubEvent.vidMappingSubEvent insert MdlMappingSubEvent(None, vMappingSubEvent.vSubEvent, vMappingSubEvent.vSubEventAMS)

	    }
	  }
    

	  def update(vMappingSubEvent: MdlMappingSubEvent) {
	    AppDB.database.withSession { implicit session: Session =>
	      val q = selectQuery(vMappingSubEvent.vidMappingSubEvent.get)
	      val q2 = q.map(vMappingSubEvent => vMappingSubEvent.vSubEvent ~ vMappingSubEvent.vSubEventAMS)
	      q2.update(vMappingSubEvent.vSubEvent, vMappingSubEvent.vSubEventAMS)
	    }
	  }

	}
}
