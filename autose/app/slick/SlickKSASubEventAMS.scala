
package slick

import models.MdlKSASubEventAMS
import models.Mdl

trait KSASubEventAMSComponent  {
	this: Profile =>
	  
	import profile.simple._

	object KSASubEventAMS extends Table[MdlKSASubEventAMS]("KSASubEventAMS") with Crud[MdlKSASubEventAMS, Long]  {

      def vidKSASubEventAMS = column[Long]("idKSASubEventAMS", O.PrimaryKey, O.AutoInc)
      def vKSA = column[Long]("KSA")
      def vSubEventAMS = column[Long]("SubEventAMS")
      def * = vidKSASubEventAMS.? ~ vKSA ~ vSubEventAMS<> (MdlKSASubEventAMS.apply _, MdlKSASubEventAMS.unapply _)
      def forInsert = vKSA ~ vSubEventAMS <> 
      ({t => MdlKSASubEventAMS(None , t._1, t._2)},
      {(vKSASubEventAMS: MdlKSASubEventAMS) => Some(vKSASubEventAMS.vKSA, vKSASubEventAMS.vSubEventAMS)})

	  def allQuery = {
	    AppDB.database.withSession { implicit session: Session =>
	      Query(KSASubEventAMS)
	    }
	  }
	  
	  def all = {
	    AppDB.database.withSession { implicit session: Session =>
	      val q = Query(KSASubEventAMS)
	      q.elements.toList
	    }
	  }
	  
	  def selectQuery(pk: Long)(implicit session: Session) = {
	      val q = Query(KSASubEventAMS)
	      q.filter(p => p.vidKSASubEventAMS === pk)
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

	  def insert(vKSASubEventAMS: MdlKSASubEventAMS): Long = { 
	    AppDB.database.withSession { implicit session: Session =>
          KSASubEventAMS.forInsert returning KSASubEventAMS.vidKSASubEventAMS insert MdlKSASubEventAMS(None, vKSASubEventAMS.vKSA, vKSASubEventAMS.vSubEventAMS)

	    }
	  }
    

	  def update(vKSASubEventAMS: MdlKSASubEventAMS) {
	    AppDB.database.withSession { implicit session: Session =>
	      val q = selectQuery(vKSASubEventAMS.vidKSASubEventAMS.get)
	      val q2 = q.map(vKSASubEventAMS => vKSASubEventAMS.vKSA ~ vKSASubEventAMS.vSubEventAMS)
	      q2.update(vKSASubEventAMS.vKSA, vKSASubEventAMS.vSubEventAMS)
	    }
	  }

	}
}
