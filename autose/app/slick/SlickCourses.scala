
package slick

import models.MdlCoursesSlick
import models.Mdl

trait CoursesSlickComponent  {
	this: Profile =>
	  
	import profile.simple._

	object Courses extends Table[MdlCoursesSlick]("Courses") with Crud[MdlCoursesSlick, Long]  {

      def vidCourse = column[Long]("idCourse", O.PrimaryKey)
      def vCourseIDNumber = column[String]("CourseIDNumber")
      def vAcademicYear = column[Long]("AcademicYear")
      def vAcademicTerm = column[Long]("AcademicTerm")
      def vCourseName = column[String]("CourseName")
      def vCourseDirector = column[Long]("CourseDirector")
      def vProgramDirector = column[Long]("ProgramDirector")
      def vCourseDescriptionRedbook = column[String]("CourseDescriptionRedbook")
      def vCreditHours = column[Double]("CreditHours")
      def vPrerequisites = column[String]("Prerequisites")
      def vCorequisites = column[String]("Corequisites")
      def vDisqualifiers = column[String]("Disqualifiers")
      def vCourseStrategy = column[String]("CourseStrategy")
      def vCriteriaForPassing = column[String]("CriteriaForPassing")
      def vAdminInstructions = column[String]("AdminInstructions")
      def vDepartmentID = column[Long]("DepartmentID")
      def vCourseWebsite = column[Boolean]("CourseWebsite")
      def vCourseDescriptionWebsite = column[String]("CourseDescriptionWebsite")
      def * = vidCourse.? ~ vCourseIDNumber ~ vAcademicYear ~ vAcademicTerm ~ vCourseName ~ vCourseDirector ~ vProgramDirector ~ vCourseDescriptionRedbook ~ vCreditHours ~ vPrerequisites ~ vCorequisites ~ vDisqualifiers ~ vCourseStrategy ~ vCriteriaForPassing ~ vAdminInstructions ~ vDepartmentID ~ vCourseWebsite ~ vCourseDescriptionWebsite<> (MdlCoursesSlick.apply _, MdlCoursesSlick.unapply _)
      def forInsert = vCourseIDNumber ~ vAcademicYear ~ vAcademicTerm ~ vCourseName ~ vCourseDirector ~ vProgramDirector ~ vCourseDescriptionRedbook ~ vCreditHours ~ vPrerequisites ~ vCorequisites ~ vDisqualifiers ~ vCourseStrategy ~ vCriteriaForPassing ~ vAdminInstructions ~ vDepartmentID ~ vCourseWebsite ~ vCourseDescriptionWebsite <> 
      ({t => MdlCoursesSlick(None , t._1, t._2, t._3, t._4, t._5, t._6, t._7, t._8, t._9, t._10, t._11, t._12, t._13, t._14, t._15, t._16, t._17)},
      {(vCourses: MdlCoursesSlick) => Some(vCourses.vCourseIDNumber, vCourses.vAcademicYear, vCourses.vAcademicTerm, vCourses.vCourseName, vCourses.vCourseDirector, vCourses.vProgramDirector, vCourses.vCourseDescriptionRedbook, vCourses.vCreditHours, vCourses.vPrerequisites, vCourses.vCorequisites, vCourses.vDisqualifiers, vCourses.vCourseStrategy, vCourses.vCriteriaForPassing, vCourses.vAdminInstructions, vCourses.vDepartmentID, vCourses.vCourseWebsite, vCourses.vCourseDescriptionWebsite)})

	  def allQuery = {
	    AppDB.database.withSession { implicit session: Session =>
	      Query(Courses)
	    }
	  }
	  
	  def all = {
	    AppDB.database.withSession { implicit session: Session =>
	      val q = Query(Courses)
	      q.elements.toList
	    }
	  }
	  
	  def selectQuery(pk: Long)(implicit session: Session) = {
	      val q = Query(Courses)
	      q.filter(p => p.vidCourse === pk)
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

	  def insert(vCourses: MdlCoursesSlick) {
	    AppDB.database.withSession { implicit session: Session =>
	      Courses.forInsert insert MdlCoursesSlick(None, vCourses.vCourseIDNumber, vCourses.vAcademicYear, vCourses.vAcademicTerm, vCourses.vCourseName, vCourses.vCourseDirector, vCourses.vProgramDirector, vCourses.vCourseDescriptionRedbook, vCourses.vCreditHours, vCourses.vPrerequisites, vCourses.vCorequisites, vCourses.vDisqualifiers, vCourses.vCourseStrategy, vCourses.vCriteriaForPassing, vCourses.vAdminInstructions, vCourses.vDepartmentID, vCourses.vCourseWebsite, vCourses.vCourseDescriptionWebsite)

	    }
	  }
    

	  def update(vCourses: MdlCoursesSlick) {
	    AppDB.database.withSession { implicit session: Session =>
	      val q = selectQuery(vCourses.vidCourse.get)
	      val q2 = q.map(vCourses => vCourses.vCourseIDNumber ~ vCourses.vAcademicYear ~ vCourses.vAcademicTerm ~ vCourses.vCourseName ~ vCourses.vCourseDirector ~ vCourses.vProgramDirector ~ vCourses.vCourseDescriptionRedbook ~ vCourses.vCreditHours ~ vCourses.vPrerequisites ~ vCourses.vCorequisites ~ vCourses.vDisqualifiers ~ vCourses.vCourseStrategy ~ vCourses.vCriteriaForPassing ~ vCourses.vAdminInstructions ~ vCourses.vDepartmentID ~ vCourses.vCourseWebsite ~ vCourses.vCourseDescriptionWebsite)
	      q2.update(vCourses.vCourseIDNumber, vCourses.vAcademicYear, vCourses.vAcademicTerm, vCourses.vCourseName, vCourses.vCourseDirector, vCourses.vProgramDirector, vCourses.vCourseDescriptionRedbook, vCourses.vCreditHours, vCourses.vPrerequisites, vCourses.vCorequisites, vCourses.vDisqualifiers, vCourses.vCourseStrategy, vCourses.vCriteriaForPassing, vCourses.vAdminInstructions, vCourses.vDepartmentID, vCourses.vCourseWebsite, vCourses.vCourseDescriptionWebsite)
	    }
	  }

	}
}