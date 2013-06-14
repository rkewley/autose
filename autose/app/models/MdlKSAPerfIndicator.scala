    
 package models
 
 import play.api.data.Forms._
 import play.api.data._
 import play.api.data.format.Formats._

    
 case class MdlKSAPerfIndicator (
 	vidKSAPerfIndicator : Option[Long],
	vKSA : Long,
	vPerformanceIndicator : Long
    ) extends Mdl[Long] {
    
      def this() = this(Option(0), 0, 0)

  	  def validate: Boolean = true
    
	  def validationErrors: String = ""
    
      def selectIdentifier: (String, String) = vidKSAPerfIndicator.get.toString -> vidKSAPerfIndicator.get.toString
    
      def compare(a: MdlKSAPerfIndicator, b: MdlKSAPerfIndicator) = a.vidKSAPerfIndicator.get.compareTo(b.vidKSAPerfIndicator.get)
        
      def primaryKey = vidKSAPerfIndicator
}

object MdlKSAPerfIndicatorList {
  val formList = Form[MdlKSAPerfIndicatorList](
    mapping (
	"fidKSAPerfIndicator" -> optional(of[Long]),
	"fKSA" -> list(of[Long]),
	"fPerformanceIndicator" -> of[Long]
    )(MdlKSAPerfIndicatorList.apply)(MdlKSAPerfIndicatorList.unapply)
  )    
  }
 
 case class MdlKSAPerfIndicatorList (
 	vidKSAPerfIndicator : Option[Long],
	vKSA : List[Long],
	vPerformanceIndicator : Long
    ) {
    
    def getList = {
      vKSA.map(ksa => 
        new MdlKSAPerfIndicator(vidKSAPerfIndicator, ksa, vPerformanceIndicator))
    }
 }
 
