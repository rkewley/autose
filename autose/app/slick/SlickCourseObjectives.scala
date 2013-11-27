
package slick

import models.MdlCourseObjectives
import models.Mdl

trait CourseObjectivesComponent  {
	this: Profile =>
	  
	import profile.simple._

	object CourseObjectives extends Table[MdlCourseObjectives]("CourseObjectives") with Crud[MdlCourseObjectives, Long]  {

      def vObjectiveID = column[Long]("ObjectiveID", O.PrimaryKey, O.AutoInc)
      def vCourseIDNumber = column[Long]("CourseIDNumber")
      def vObjective = column[String]("Objective")
      def * = vObjectiveID.? ~ vCourseIDNumber ~ vObjective<> (MdlCourseObjectives.apply _, MdlCourseObjectives.unapply _)
      def forInsert = vCourseIDNumber ~ vObjective <> 
      ({t => MdlCourseObjectives(None , t._1, t._2)},
      {(vCourseObjectives: MdlCourseObjectives) => Some(vCourseObjectives.vCourseIDNumber, vCourseObjectives.vObjective)})

	  def allQuery = {
	    AppDB.database.withSession { implicit session: Session =>
	      Query(CourseObjectives)
	    }
	  }
	  
	  def all = {
	    AppDB.database.withSession { implicit session: Session =>
	      val q = Query(CourseObjectives)
	      q.elements.toList
	    }
	  }
	  
	  def selectQuery(pk: Long)(implicit session: Session) = {
	      val q = Query(CourseObjectives)
	      q.filter(p => p.vObjectiveID === pk)
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

	  def insert(vCourseObjectives: MdlCourseObjectives): Long = {
	    AppDB.database.withSession { implicit session: Session =>
	      CourseObjectives.forInsert returning CourseObjectives.vObjectiveID insert MdlCourseObjectives(None, vCourseObjectives.vCourseIDNumber, vCourseObjectives.vObjective)

	    }
	  }
    

	  def update(vCourseObjectives: MdlCourseObjectives) {
	    AppDB.database.withSession { implicit session: Session =>
	      val q = selectQuery(vCourseObjectives.vObjectiveID.get)
	      val q2 = q.map(vCourseObjectives => vCourseObjectives.vCourseIDNumber ~ vCourseObjectives.vObjective)
	      q2.update(vCourseObjectives.vCourseIDNumber, vCourseObjectives.vObjective)
	    }
	  }
	  
	  def courseObjectivesForCourse(course: Long) = {
	    AppDB.database.withSession { implicit session: Session =>
	      val q = Query(CourseObjectives)
	      q.filter(p => p.vCourseIDNumber === course).elements.toList
	    }
	  }
	  

	}
}
