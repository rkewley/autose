
package slick

import models.MdlRequiredCourses
import models.Mdl

trait RequiredCoursesComponent  {
	this: Profile =>
	  
	import profile.simple._

	object RequiredCourses extends Table[MdlRequiredCourses]("RequiredCourses") with Crud[MdlRequiredCourses, Long]  {

      def vidRequiredCourses = column[Long]("idRequiredCourses", O.PrimaryKey, O.AutoInc)
      def vProgram = column[Long]("Program")
      def vCourse = column[String]("Course")
      def * = vidRequiredCourses.? ~ vProgram ~ vCourse<> (MdlRequiredCourses.apply _, MdlRequiredCourses.unapply _)
      def forInsert = vProgram ~ vCourse <> 
      ({t => MdlRequiredCourses(None , t._1, t._2)},
      {(vRequiredCourses: MdlRequiredCourses) => Some(vRequiredCourses.vProgram, vRequiredCourses.vCourse)})

	  def allQuery = {
	    AppDB.database.withSession { implicit session: Session =>
	      Query(RequiredCourses)
	    }
	  }
	  
	  def all = {
	    AppDB.database.withSession { implicit session: Session =>
	      val q = Query(RequiredCourses)
	      q.elements.toList
	    }
	  }
	  
	  def selectQuery(pk: Long)(implicit session: Session) = {
	      val q = Query(RequiredCourses)
	      q.filter(p => p.vidRequiredCourses === pk)
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

	  def insert(vRequiredCourses: MdlRequiredCourses): Long = {
	    AppDB.database.withSession { implicit session: Session =>
	      RequiredCourses.forInsert returning RequiredCourses.vidRequiredCourses insert MdlRequiredCourses(None, vRequiredCourses.vProgram, vRequiredCourses.vCourse)

	    }
	  }
    

	  def update(vRequiredCourses: MdlRequiredCourses) {
	    AppDB.database.withSession { implicit session: Session =>
	      val q = selectQuery(vRequiredCourses.vidRequiredCourses.get)
	      val q2 = q.map(vRequiredCourses => vRequiredCourses.vProgram ~ vRequiredCourses.vCourse)
	      q2.update(vRequiredCourses.vProgram, vRequiredCourses.vCourse)
	    }
	  }

	}
}
