
package slick

import models.MdlGradesAMS
import models.Mdl

trait GradesAMSComponent  {
	this: Profile =>
	  
	import profile.simple._

	object GradesAMS extends Table[MdlGradesAMS]("GradesAMS") with Crud[MdlGradesAMS, Long]  {

      def vidGradesAMS = column[Long]("idGradesAMS", O.PrimaryKey, O.AutoInc)
      def vStudent = column[Long]("Student")
      def vGradedEventAMS = column[Long]("GradedEventAMS")
      def vPoints = column[Double]("Points")
      def vStudentId = column[String]("StudentId")
      def * = vidGradesAMS.? ~ vStudent ~ vGradedEventAMS ~ vPoints ~ vStudentId<> (MdlGradesAMS.apply _, MdlGradesAMS.unapply _)
      def forInsert = vStudent ~ vGradedEventAMS ~ vPoints ~ vStudentId <> 
      ({t => MdlGradesAMS(None , t._1, t._2, t._3, t._4)},
      {(vGradesAMS: MdlGradesAMS) => Some(vGradesAMS.vStudent, vGradesAMS.vGradedEventAMS, vGradesAMS.vPoints, vGradesAMS.vStudentId)})

	  def allQuery = {
	    AppDB.database.withSession { implicit session: Session =>
	      Query(GradesAMS)
	    }
	  }
	  
	  def all = {
	    AppDB.database.withSession { implicit session: Session =>
	      val q = Query(GradesAMS)
	      q.elements.toList
	    }
	  }
	  
	  def selectQuery(pk: Long)(implicit session: Session) = {
	      val q = Query(GradesAMS)
	      q.filter(p => p.vidGradesAMS === pk)
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

	  def insert(vGradesAMS: MdlGradesAMS): Long = { 
	    AppDB.database.withSession { implicit session: Session =>
          GradesAMS.forInsert returning GradesAMS.vidGradesAMS insert MdlGradesAMS(None, vGradesAMS.vStudent, vGradesAMS.vGradedEventAMS, vGradesAMS.vPoints, vGradesAMS.vStudentId)

	    }
	  }
    

	  def update(vGradesAMS: MdlGradesAMS) {
	    AppDB.database.withSession { implicit session: Session =>
	      val q = selectQuery(vGradesAMS.vidGradesAMS.get)
	      val q2 = q.map(vGradesAMS => vGradesAMS.vStudent ~ vGradesAMS.vGradedEventAMS ~ vGradesAMS.vPoints ~ vGradesAMS.vStudentId)
	      q2.update(vGradesAMS.vStudent, vGradesAMS.vGradedEventAMS, vGradesAMS.vPoints, vGradesAMS.vStudentId)
	    }
	  }

	}
}
