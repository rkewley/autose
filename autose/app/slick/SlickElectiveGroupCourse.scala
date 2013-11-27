
package slick

import models.MdlElectiveGroupCourse
import models.Mdl

trait ElectiveGroupCourseComponent  {
	this: Profile =>
	  
	import profile.simple._

	object ElectiveGroupCourse extends Table[MdlElectiveGroupCourse]("ElectiveGroupCourse") with Crud[MdlElectiveGroupCourse, Long]  {

      def vidElectiveGroupCourse = column[Long]("idElectiveGroupCourse", O.PrimaryKey, O.AutoInc)
      def vElectiveGroup = column[Long]("ElectiveGroup")
      def vCourse = column[String]("Course")
      def vRequired = column[Boolean]("Required")
      def * = vidElectiveGroupCourse.? ~ vElectiveGroup ~ vCourse ~ vRequired<> (MdlElectiveGroupCourse.apply _, MdlElectiveGroupCourse.unapply _)
      def forInsert = vElectiveGroup ~ vCourse ~ vRequired <> 
      ({t => MdlElectiveGroupCourse(None , t._1, t._2, t._3)},
      {(vElectiveGroupCourse: MdlElectiveGroupCourse) => Some(vElectiveGroupCourse.vElectiveGroup, vElectiveGroupCourse.vCourse, vElectiveGroupCourse.vRequired)})

	  def allQuery = {
	    AppDB.database.withSession { implicit session: Session =>
	      Query(ElectiveGroupCourse)
	    }
	  }
	  
	  def all = {
	    AppDB.database.withSession { implicit session: Session =>
	      val q = Query(ElectiveGroupCourse)
	      q.elements.toList
	    }
	  }
	  
	  def selectQuery(pk: Long)(implicit session: Session) = {
	      val q = Query(ElectiveGroupCourse)
	      q.filter(p => p.vidElectiveGroupCourse === pk)
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

	  def insert(vElectiveGroupCourse: MdlElectiveGroupCourse): Long = {
	    AppDB.database.withSession { implicit session: Session =>
	      ElectiveGroupCourse.forInsert returning ElectiveGroupCourse.vidElectiveGroupCourse insert MdlElectiveGroupCourse(None, vElectiveGroupCourse.vElectiveGroup, vElectiveGroupCourse.vCourse, vElectiveGroupCourse.vRequired)

	    }
	  }
    

	  def update(vElectiveGroupCourse: MdlElectiveGroupCourse) {
	    AppDB.database.withSession { implicit session: Session =>
	      val q = selectQuery(vElectiveGroupCourse.vidElectiveGroupCourse.get)
	      val q2 = q.map(vElectiveGroupCourse => vElectiveGroupCourse.vElectiveGroup ~ vElectiveGroupCourse.vCourse ~ vElectiveGroupCourse.vRequired)
	      q2.update(vElectiveGroupCourse.vElectiveGroup, vElectiveGroupCourse.vCourse, vElectiveGroupCourse.vRequired)
	    }
	  }
	  
	  def coursesForElectiveGroup(electiveGroup: Long): List[MdlElectiveGroupCourse] = {
	    AppDB.database.withSession { implicit session: Session =>
	      val q = Query(ElectiveGroupCourse)
	      q.filter(p => p.vElectiveGroup === electiveGroup).elements.toList
	    }
	    
	  }

	}
}
