
package slick

import models.MdlSubEventCourseObj
import models.Mdl

trait SubEventCourseObjComponent  {
	this: Profile =>
	  
	import profile.simple._

	object SubEventCourseObj extends Table[MdlSubEventCourseObj]("SubEventCourseObj") with Crud[MdlSubEventCourseObj, Long]  {

      def vidSubEventCourseObj = column[Long]("idSubEventCourseObj", O.PrimaryKey)
      def vSubEventAMS = column[Long]("SubEventAMS")
      def vCourseObj = column[Long]("CourseObj")
      def * = vidSubEventCourseObj.? ~ vSubEventAMS ~ vCourseObj<> (MdlSubEventCourseObj.apply _, MdlSubEventCourseObj.unapply _)
      def forInsert = vSubEventAMS ~ vCourseObj <> 
      ({t => MdlSubEventCourseObj(None , t._1, t._2)},
      {(vSubEventCourseObj: MdlSubEventCourseObj) => Some(vSubEventCourseObj.vSubEventAMS, vSubEventCourseObj.vCourseObj)})

	  def allQuery = {
	    AppDB.database.withSession { implicit session: Session =>
	      Query(SubEventCourseObj)
	    }
	  }
	  
	  def all = {
	    AppDB.database.withSession { implicit session: Session =>
	      val q = Query(SubEventCourseObj)
	      q.elements.toList
	    }
	  }
	  
	  def selectQuery(pk: Long)(implicit session: Session) = {
	      val q = Query(SubEventCourseObj)
	      q.filter(p => p.vidSubEventCourseObj === pk)
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

	  def insert(vSubEventCourseObj: MdlSubEventCourseObj): Long = {
	    AppDB.database.withSession { implicit session: Session =>
	      SubEventCourseObj.forInsert insert MdlSubEventCourseObj(None, vSubEventCourseObj.vSubEventAMS, vSubEventCourseObj.vCourseObj)

	    }
	  }
    

	  def update(vSubEventCourseObj: MdlSubEventCourseObj) {
	    AppDB.database.withSession { implicit session: Session =>
	      val q = selectQuery(vSubEventCourseObj.vidSubEventCourseObj.get)
	      val q2 = q.map(vSubEventCourseObj => vSubEventCourseObj.vSubEventAMS ~ vSubEventCourseObj.vCourseObj)
	      q2.update(vSubEventCourseObj.vSubEventAMS, vSubEventCourseObj.vCourseObj)
	    }
	  }

	}
}
