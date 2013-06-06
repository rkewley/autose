    
 package models
 
 import play.api.data.Forms._
 import play.api.data._
 import play.api.data.format.Formats._

 case class MdlKSASubGradedEvent (
 	vidKSASubGradedEvent : Option[Long],
	vKSA : Long,
	vSubGradedEvent : Long
    ) extends Mdl[Long] {
    
      def this() = this(Option(0), 0, 0)

  	  def validate: Boolean = true
    
	  def validationErrors: String = ""
    
      def selectIdentifier: (String, String) = vidKSASubGradedEvent.get.toString -> vidKSASubGradedEvent.get.toString
    
      def compare(a: MdlKSASubGradedEvent, b: MdlKSASubGradedEvent) = a.vidKSASubGradedEvent.get.compareTo(b.vidKSASubGradedEvent.get)
        
      def primaryKey = vidKSASubGradedEvent
}
 
object MdlKSASubGradedEventList {
  val formList = Form[MdlKSASubGradedEventList](
    mapping (
	"fidKSASubGradedEvent" -> optional(of[Long]),
	"fKSA" -> list(of[Long]),
	"fSubGradedEvent" -> of[Long]
    )(MdlKSASubGradedEventList.apply)(MdlKSASubGradedEventList.unapply)
  )
  }
 
 case class MdlKSASubGradedEventList (
 	vidKSASubGradedEvent : Option[Long],
	vKSA : List[Long],
	vSubGradedEvent : Long
    ) {
    
    def getList = {
      vKSA.map(ksa => 
        new MdlKSASubGradedEvent(vidKSASubGradedEvent, ksa, vSubGradedEvent))
    }
 }
 
 

    