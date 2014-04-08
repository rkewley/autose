
package slick

import models.MdlGradedEventAMS
import models.Mdl

trait GradedEventAMSComponent  {
	this: Profile =>
	  
	import profile.simple._

	object GradedEventAMS extends Table[MdlGradedEventAMS]("GradedEventAMS") with Crud[MdlGradedEventAMS, Long]  {

      def vidGradedEventAMS = column[Long]("idGradedEventAMS", O.PrimaryKey, O.AutoInc)
      def vEventNumberAMS = column[Long]("EventNumberAMS")
      def vCourse = column[Long]("Course")
      def vName = column[String]("Name")
      def vDescription = column[String]("Description")
      def vDetailedDescription = column[String]("DetailedDescription")
      def vType = column[String]("Type")
      def vMaxPoints = column[Double]("MaxPoints")
      def vLesson = column[Long]("Lesson")
      def * = vidGradedEventAMS.? ~ vEventNumberAMS ~ vCourse ~ vName ~ vDescription ~ vDetailedDescription ~ vType ~ vMaxPoints ~ vLesson<> (MdlGradedEventAMS.apply _, MdlGradedEventAMS.unapply _)
      def forInsert = vEventNumberAMS ~ vCourse ~ vName ~ vDescription ~ vDetailedDescription ~ vType ~ vMaxPoints ~ vLesson <> 
      ({t => MdlGradedEventAMS(None , t._1, t._2, t._3, t._4, t._5, t._6, t._7, t._8)},
      {(vGradedEventAMS: MdlGradedEventAMS) => Some(vGradedEventAMS.vEventNumberAMS, vGradedEventAMS.vCourse, vGradedEventAMS.vName, vGradedEventAMS.vDescription, vGradedEventAMS.vDetailedDescription, vGradedEventAMS.vType, vGradedEventAMS.vMaxPoints, vGradedEventAMS.vLesson)})

	  def allQuery = {
	    AppDB.database.withSession { implicit session: Session =>
	      Query(GradedEventAMS)
	    }
	  }
	  
	  def all = {
	    AppDB.database.withSession { implicit session: Session =>
	      val q = Query(GradedEventAMS)
	      q.elements.toList
	    }
	  }
	  
	  def selectQuery(pk: Long)(implicit session: Session) = {
	      val q = Query(GradedEventAMS)
	      q.filter(p => p.vidGradedEventAMS === pk)
	  }

      def select(pk: Long) = {
	    AppDB.database.withSession { implicit session: Session =>
	      selectQuery(pk).elements.toList.headOption
	    }
	  }
	  
      def selectByCourse(course: Long) = {
	    AppDB.database.withSession { implicit session: Session =>
	      val q = Query(GradedEventAMS)
	      q.filter(p => p.vCourse === course).elements.toList
	    }
	  }
      
      def delete(pk: Long) {
	    AppDB.database.withSession { implicit session: Session =>
	      selectQuery(pk).delete
	    }
	  }

	  def insert(vGradedEventAMS: MdlGradedEventAMS): Long = { 
	    AppDB.database.withSession { implicit session: Session =>
          GradedEventAMS.forInsert returning GradedEventAMS.vidGradedEventAMS insert MdlGradedEventAMS(None, vGradedEventAMS.vEventNumberAMS, vGradedEventAMS.vCourse, vGradedEventAMS.vName, vGradedEventAMS.vDescription, vGradedEventAMS.vDetailedDescription, vGradedEventAMS.vType, vGradedEventAMS.vMaxPoints, vGradedEventAMS.vLesson)

	    }
	  }

	  def update(vGradedEventAMS: MdlGradedEventAMS) = {
	    AppDB.database.withSession { implicit session: Session =>
	      val q = selectQuery(vGradedEventAMS.vidGradedEventAMS.get)
	      val q2 = q.map(vGradedEventAMS => vGradedEventAMS.vEventNumberAMS ~ vGradedEventAMS.vCourse ~ vGradedEventAMS.vName ~ vGradedEventAMS.vDescription ~ vGradedEventAMS.vDetailedDescription ~ vGradedEventAMS.vType ~ vGradedEventAMS.vMaxPoints ~ vGradedEventAMS.vLesson)
	      q2.update(vGradedEventAMS.vEventNumberAMS, vGradedEventAMS.vCourse, vGradedEventAMS.vName, vGradedEventAMS.vDescription, vGradedEventAMS.vDetailedDescription, vGradedEventAMS.vType, vGradedEventAMS.vMaxPoints, vGradedEventAMS.vLesson)
	    }
	  }

	}
}
