
package slick

import models.MdlEventType
import models.Mdl

trait EventTypeComponent  {
	this: Profile =>
	  
	import profile.simple._

	object EventType extends Table[MdlEventType]("EventType") with Crud[MdlEventType, Long]  {

      def vidEventType = column[Long]("idEventType", O.PrimaryKey)
      def vEventType = column[String]("EventType")
      def vDescription = column[String]("Description")
      def * = vidEventType.? ~ vEventType ~ vDescription<> (MdlEventType.apply _, MdlEventType.unapply _)
      def forInsert = vEventType ~ vDescription <> 
      ({t => MdlEventType(None , t._1, t._2)},
      {(vEventType: MdlEventType) => Some(vEventType.vEventType, vEventType.vDescription)})

	  def allQuery = {
	    AppDB.database.withSession { implicit session: Session =>
	      Query(EventType)
	    }
	  }
	  
	  def all = {
	    AppDB.database.withSession { implicit session: Session =>
	      val q = Query(EventType)
	      q.elements.toList
	    }
	  }
	  
	  def selectQuery(pk: Long)(implicit session: Session) = {
	      val q = Query(EventType)
	      q.filter(p => p.vidEventType === pk)
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

	  def insert(vEventType: MdlEventType) {
	    AppDB.database.withSession { implicit session: Session =>
	      EventType.forInsert insert MdlEventType(None, vEventType.vEventType, vEventType.vDescription)

	    }
	  }
    

	  def update(vEventType: MdlEventType) {
	    AppDB.database.withSession { implicit session: Session =>
	      val q = selectQuery(vEventType.vidEventType.get)
	      val q2 = q.map(vEventType => vEventType.vEventType ~ vEventType.vDescription)
	      q2.update(vEventType.vEventType, vEventType.vDescription)
	    }
	  }

	}
}
