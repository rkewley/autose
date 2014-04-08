    
 package models
 
 import play.api.data.Forms._
 import play.api.data._
 import play.api.data.format.Formats._

 case class MdlKSASubEventAMS (
 	vidKSASubEventAMS : Option[Long],
	vKSA : Long,
	vSubEventAMS : Long
    ) extends Mdl[Long] {
    
      def this() = this(Option(0), 0, 0)

  	  def validate: Boolean = true
    
	  def validationErrors: String = ""
    
      def selectIdentifier: (String, String) = vidKSASubEventAMS.get.toString -> vidKSASubEventAMS.get.toString
    
      def compare(a: MdlKSASubEventAMS, b: MdlKSASubEventAMS) = a.vidKSASubEventAMS.get.compareTo(b.vidKSASubEventAMS.get)
        
      def primaryKey = vidKSASubEventAMS
}

 object MdlKSASubEventAMSList {
  val formList = Form[MdlKSASubEventAMSList](
    mapping (
	"fidKSASubEventAMS" -> optional(of[Long]),
	"fKSA" -> list(of[Long]),
	"fSubEventAMS" -> of[Long]
    )(MdlKSASubEventAMSList.apply)(MdlKSASubEventAMSList.unapply)
  )    
  }
 
 case class MdlKSASubEventAMSList (
 	vidKSASubEventAMS : Option[Long],
	vKSA : List[Long],
	vSubEventAMS : Long
    ) {
    
    def getList = {
      vKSA.map(ksa => 
        new MdlKSASubEventAMS(vidKSASubEventAMS, ksa, vSubEventAMS))
    }
 }
 
