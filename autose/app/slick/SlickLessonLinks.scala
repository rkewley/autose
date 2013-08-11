
package slick

import models.MdlLessonLinks
import models.Mdl

trait LessonLinksComponent  {
	this: Profile =>
	  
	import profile.simple._

	object LessonLinks extends Table[MdlLessonLinks]("LessonLinks") with Crud[MdlLessonLinks, Long]  {

      def vLessonLinkNumber = column[Long]("LessonLinkNumber", O.PrimaryKey, O.AutoInc)
      def vLink = column[String]("Link")
      def vDescription = column[String]("Description")
      def vIsFileLiink = column[Boolean]("IsFileLiink")
      def vLesson = column[Long]("Lesson")
      def vFaculty = column[Long]("Faculty")
      def * = vLessonLinkNumber.? ~ vLink ~ vDescription ~ vIsFileLiink ~ vLesson ~ vFaculty<> (MdlLessonLinks.apply _, MdlLessonLinks.unapply _)
      def forInsert = vLink ~ vDescription ~ vIsFileLiink ~ vLesson ~ vFaculty <> 
      ({t => MdlLessonLinks(None , t._1, t._2, t._3, t._4, t._5)},
      {(vLessonLinks: MdlLessonLinks) => Some(vLessonLinks.vLink, vLessonLinks.vDescription, vLessonLinks.vIsFileLiink, vLessonLinks.vLesson, vLessonLinks.vFaculty)})

	  def allQuery = {
	    AppDB.database.withSession { implicit session: Session =>
	      Query(LessonLinks)
	    }
	  }
	  
	  def all = {
	    AppDB.database.withSession { implicit session: Session =>
	      val q = Query(LessonLinks)
	      q.elements.toList
	    }
	  }
	  
	  def selectQuery(pk: Long)(implicit session: Session) = {
	      val q = Query(LessonLinks)
	      q.filter(p => p.vLessonLinkNumber === pk)
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

	  def insert(vLessonLinks: MdlLessonLinks): Long = { 
	    AppDB.database.withSession { implicit session: Session =>
          LessonLinks.forInsert returning LessonLinks.vLessonLinkNumber insert MdlLessonLinks(None, vLessonLinks.vLink, vLessonLinks.vDescription, vLessonLinks.vIsFileLiink, vLessonLinks.vLesson, vLessonLinks.vFaculty)

	    }
	  }
    

	  def update(vLessonLinks: MdlLessonLinks) {
	    AppDB.database.withSession { implicit session: Session =>
	      val q = selectQuery(vLessonLinks.vLessonLinkNumber.get)
	      val q2 = q.map(vLessonLinks => vLessonLinks.vLink ~ vLessonLinks.vDescription ~ vLessonLinks.vIsFileLiink ~ vLessonLinks.vLesson ~ vLessonLinks.vFaculty)
	      q2.update(vLessonLinks.vLink, vLessonLinks.vDescription, vLessonLinks.vIsFileLiink, vLessonLinks.vLesson, vLessonLinks.vFaculty)
	    }
	  }

	}
}
