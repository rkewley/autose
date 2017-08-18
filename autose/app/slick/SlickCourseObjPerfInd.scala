
package slick

import models.MdlCourseObjPerfInd
import models.Mdl

trait CourseObjPerfIndComponent  {
	this: Profile =>
	  
	import profile.simple._

	object CourseObjPerfInd extends Table[MdlCourseObjPerfInd]("CourseObjPerfInd") with Crud[MdlCourseObjPerfInd, Long]  {

      def vidCourseObjPerfInd = column[Long]("idCourseObjPerfInd", O.PrimaryKey)
      def vCourseObjective = column[Long]("CourseObjective")
      def vPerformanceIndicator = column[Long]("PerformanceIndicator")
      def * = vidCourseObjPerfInd.? ~ vCourseObjective ~ vPerformanceIndicator<> (MdlCourseObjPerfInd.apply _, MdlCourseObjPerfInd.unapply _)
      def forInsert = vCourseObjective ~ vPerformanceIndicator <> 
      ({t => MdlCourseObjPerfInd(None , t._1, t._2)},
      {(vCourseObjPerfInd: MdlCourseObjPerfInd) => Some(vCourseObjPerfInd.vCourseObjective, vCourseObjPerfInd.vPerformanceIndicator)})

	  def allQuery = {
	    AppDB.database.withSession { implicit session: Session =>
	      Query(CourseObjPerfInd)
	    }
	  }

		def selectByPerfIndicator(idPerfIndicator: Long)(implicit Session: Session): List[MdlCourseObjPerfInd] = {
			allQuery.filter(x => x.vPerformanceIndicator === idPerfIndicator).elements.toList
		}
	  
	  def all = {
	    AppDB.database.withSession { implicit session: Session =>
	      val q = Query(CourseObjPerfInd)
	      q.elements.toList
	    }
	  }
	  
	  def selectQuery(pk: Long)(implicit session: Session) = {
	      val q = Query(CourseObjPerfInd)
	      q.filter(p => p.vidCourseObjPerfInd === pk)
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

	  def insert(vCourseObjPerfInd: MdlCourseObjPerfInd): Long = {
	    AppDB.database.withSession { implicit session: Session =>
	      CourseObjPerfInd.forInsert insert MdlCourseObjPerfInd(None, vCourseObjPerfInd.vCourseObjective, vCourseObjPerfInd.vPerformanceIndicator)

	    }
	  }
    

	  def update(vCourseObjPerfInd: MdlCourseObjPerfInd) {
	    AppDB.database.withSession { implicit session: Session =>
	      val q = selectQuery(vCourseObjPerfInd.vidCourseObjPerfInd.get)
	      val q2 = q.map(vCourseObjPerfInd => vCourseObjPerfInd.vCourseObjective ~ vCourseObjPerfInd.vPerformanceIndicator)
	      q2.update(vCourseObjPerfInd.vCourseObjective, vCourseObjPerfInd.vPerformanceIndicator)
	    }
	  }

	}
}
