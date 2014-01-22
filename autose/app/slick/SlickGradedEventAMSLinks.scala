
package slick

import models.MdlGradedEventAMSLinks
import models.Mdl

trait GradedEventAMSLinksComponent  {
	this: Profile =>
	  
	import profile.simple._

	object GradedEventAMSLinks extends Table[MdlGradedEventAMSLinks]("GradedEventAMSLinks") with Crud[MdlGradedEventAMSLinks, Long]  {

      def vidGradedEventAMSLinks = column[Long]("idGradedEventAMSLinks", O.PrimaryKey, O.AutoInc)
      def vLink = column[String]("Link")
      def vDescription = column[String]("Description")
      def vIsFileLink = column[Boolean]("IsFileLink")
      def vvGradedEventAMS = column[Long]("vGradedEventAMS")
      def * = vidGradedEventAMSLinks.? ~ vLink ~ vDescription ~ vIsFileLink ~ vvGradedEventAMS<> (MdlGradedEventAMSLinks.apply _, MdlGradedEventAMSLinks.unapply _)
      def forInsert = vLink ~ vDescription ~ vIsFileLink ~ vvGradedEventAMS <> 
      ({t => MdlGradedEventAMSLinks(None , t._1, t._2, t._3, t._4)},
      {(vGradedEventAMSLinks: MdlGradedEventAMSLinks) => Some(vGradedEventAMSLinks.vLink, vGradedEventAMSLinks.vDescription, vGradedEventAMSLinks.vIsFileLink, vGradedEventAMSLinks.vvGradedEventAMS)})

	  def allQuery = {
	    AppDB.database.withSession { implicit session: Session =>
	      Query(GradedEventAMSLinks)
	    }
	  }
	  
	  def all = {
	    AppDB.database.withSession { implicit session: Session =>
	      val q = Query(GradedEventAMSLinks)
	      q.elements.toList
	    }
	  }
	  
	  def selectQuery(pk: Long)(implicit session: Session) = {
	      val q = Query(GradedEventAMSLinks)
	      q.filter(p => p.vidGradedEventAMSLinks === pk)
	  }

      def selectByGradedEventAMS(gradedEventAMS: Long) = {
	    AppDB.database.withSession { implicit session: Session =>
	      val q = Query(GradedEventAMSLinks)
	      q.filter(p => p.vvGradedEventAMS === gradedEventAMS).elements.toList
	    }
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

	  def insert(vGradedEventAMSLinks: MdlGradedEventAMSLinks): Long = { 
	    AppDB.database.withSession { implicit session: Session =>
          GradedEventAMSLinks.forInsert returning GradedEventAMSLinks.vidGradedEventAMSLinks insert MdlGradedEventAMSLinks(None, vGradedEventAMSLinks.vLink, vGradedEventAMSLinks.vDescription, vGradedEventAMSLinks.vIsFileLink, vGradedEventAMSLinks.vvGradedEventAMS)

	    }
	  }
    

	  def update(vGradedEventAMSLinks: MdlGradedEventAMSLinks) {
	    AppDB.database.withSession { implicit session: Session =>
	      val q = selectQuery(vGradedEventAMSLinks.vidGradedEventAMSLinks.get)
	      val q2 = q.map(vGradedEventAMSLinks => vGradedEventAMSLinks.vLink ~ vGradedEventAMSLinks.vDescription ~ vGradedEventAMSLinks.vIsFileLink ~ vGradedEventAMSLinks.vvGradedEventAMS)
	      q2.update(vGradedEventAMSLinks.vLink, vGradedEventAMSLinks.vDescription, vGradedEventAMSLinks.vIsFileLink, vGradedEventAMSLinks.vvGradedEventAMS)
	    }
	  }

	}
}
