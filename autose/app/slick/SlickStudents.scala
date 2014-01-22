
package slick

import models.MdlStudents
import models.Mdl

trait StudentsComponent  {
	this: Profile =>
	  
	import profile.simple._

	object Students extends Table[MdlStudents]("Students") with Crud[MdlStudents, Long]  {

      def vidStudents = column[Long]("idStudents", O.PrimaryKey, O.AutoInc)
      def vStudentId = column[String]("StudentId")
      def vLastname = column[String]("Lastname")
      def vFirstname = column[String]("Firstname")
      def vMajorAMS = column[String]("MajorAMS")
      def vGradYear = column[Long]("GradYear")
      def vProgram = column[Long]("Program")
      def * = vidStudents.? ~ vStudentId ~ vLastname ~ vFirstname ~ vMajorAMS ~ vGradYear ~ vProgram<> (MdlStudents.apply _, MdlStudents.unapply _)
      def forInsert = vStudentId ~ vLastname ~ vFirstname ~ vMajorAMS ~ vGradYear ~ vProgram <> 
      ({t => MdlStudents(None , t._1, t._2, t._3, t._4, t._5, t._6)},
      {(vStudents: MdlStudents) => Some(vStudents.vStudentId, vStudents.vLastname, vStudents.vFirstname, vStudents.vMajorAMS, vStudents.vGradYear, vStudents.vProgram)})

	  def allQuery = {
	    AppDB.database.withSession { implicit session: Session =>
	      Query(Students)
	    }
	  }
	  
	  def all = {
	    AppDB.database.withSession { implicit session: Session =>
	      val q = Query(Students)
	      q.elements.toList
	    }
	  }
	  
	  def selectQuery(pk: Long)(implicit session: Session) = {
	      val q = Query(Students)
	      q.filter(p => p.vidStudents === pk)
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

	  def insert(vStudents: MdlStudents): Long = { 
	    AppDB.database.withSession { implicit session: Session =>
          Students.forInsert returning Students.vidStudents insert MdlStudents(None, vStudents.vStudentId, vStudents.vLastname, vStudents.vFirstname, vStudents.vMajorAMS, vStudents.vGradYear, vStudents.vProgram)

	    }
	  }
    

	  def update(vStudents: MdlStudents) {
	    AppDB.database.withSession { implicit session: Session =>
	      val q = selectQuery(vStudents.vidStudents.get)
	      val q2 = q.map(vStudents => vStudents.vStudentId ~ vStudents.vLastname ~ vStudents.vFirstname ~ vStudents.vMajorAMS ~ vStudents.vGradYear ~ vStudents.vProgram)
	      q2.update(vStudents.vStudentId, vStudents.vLastname, vStudents.vFirstname, vStudents.vMajorAMS, vStudents.vGradYear, vStudents.vProgram)
	    }
	  }

	}
}
