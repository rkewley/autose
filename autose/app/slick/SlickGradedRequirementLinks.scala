
package slick

import models.MdlGradedRequirementLinks
import models.Mdl

trait GradedRequirementLinksComponent  {
	this: Profile =>
	  
	import profile.simple._

	object GradedRequirementLinks extends Table[MdlGradedRequirementLinks]("GradedRequirementLinks") with Crud[MdlGradedRequirementLinks, Long]  {

      def vidGradedRequirementLinks = column[Long]("idGradedRequirementLinks", O.PrimaryKey)
      def vLink = column[String]("Link")
      def vDescription = column[String]("Description")
      def vIsFileLink = column[Boolean]("IsFileLink")
      def vGradedRequirement = column[Long]("GradedRequirement")
      def * = vidGradedRequirementLinks.? ~ vLink ~ vDescription ~ vIsFileLink ~ vGradedRequirement<> (MdlGradedRequirementLinks.apply _, MdlGradedRequirementLinks.unapply _)
      def forInsert = vLink ~ vDescription ~ vIsFileLink ~ vGradedRequirement <> 
      ({t => MdlGradedRequirementLinks(None , t._1, t._2, t._3, t._4)},
      {(vGradedRequirementLinks: MdlGradedRequirementLinks) => Some(vGradedRequirementLinks.vLink, vGradedRequirementLinks.vDescription, vGradedRequirementLinks.vIsFileLink, vGradedRequirementLinks.vGradedRequirement)})

	  def allQuery = {
	    AppDB.database.withSession { implicit session: Session =>
	      Query(GradedRequirementLinks)
	    }
	  }
	  
	  def all = {
	    AppDB.database.withSession { implicit session: Session =>
	      val q = Query(GradedRequirementLinks)
	      q.elements.toList
	    }
	  }
	  
	  def selectQuery(pk: Long)(implicit session: Session) = {
	      val q = Query(GradedRequirementLinks)
	      q.filter(p => p.vidGradedRequirementLinks === pk)
	  }

      def select(pk: Long) = {
	    AppDB.database.withSession { implicit session: Session =>
	      selectQuery(pk).elements.toList.headOption
	    }
	  }
	  
      def selectByGradedRequirement(gradedRequirement: Long) = {
	    AppDB.database.withSession { implicit session: Session =>
	      val q = Query(GradedRequirementLinks)
	      q.filter(p => p.vGradedRequirement === gradedRequirement).elements.toList
	    }
	  }
	  
	  def delete(pk: Long) {
	    AppDB.database.withSession { implicit session: Session =>
	      selectQuery(pk).delete
	    }
	  }

	  def insert(vGradedRequirementLinks: MdlGradedRequirementLinks) {
	    AppDB.database.withSession { implicit session: Session =>
	      GradedRequirementLinks.forInsert insert MdlGradedRequirementLinks(None, vGradedRequirementLinks.vLink, vGradedRequirementLinks.vDescription, vGradedRequirementLinks.vIsFileLink, vGradedRequirementLinks.vGradedRequirement)

	    }
	  }
    

	  def update(vGradedRequirementLinks: MdlGradedRequirementLinks) {
	    AppDB.database.withSession { implicit session: Session =>
	      val q = selectQuery(vGradedRequirementLinks.vidGradedRequirementLinks.get)
	      val q2 = q.map(vGradedRequirementLinks => vGradedRequirementLinks.vLink ~ vGradedRequirementLinks.vDescription ~ vGradedRequirementLinks.vIsFileLink ~ vGradedRequirementLinks.vGradedRequirement)
	      q2.update(vGradedRequirementLinks.vLink, vGradedRequirementLinks.vDescription, vGradedRequirementLinks.vIsFileLink, vGradedRequirementLinks.vGradedRequirement)
	    }
	  }

	}
}
