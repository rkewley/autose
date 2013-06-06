    
 package models
 
 import play.api.data.Forms._
 import play.api.data._
 import play.api.data.format.Formats._
    
 case class MdlKSAGradedEvent (
 	vidKSAGradedEvent : Option[Long],
	vKSA : Long,
	vGradedEvent : Long
    ) extends Mdl[Long] {
    
      def this() = this(Option(0), 0, 0)

  	  def validate: Boolean = true
    
	  def validationErrors: String = ""
    
      def selectIdentifier: (String, String) = vidKSAGradedEvent.get.toString -> vidKSAGradedEvent.get.toString
    
      def compare(a: MdlKSAGradedEvent, b: MdlKSAGradedEvent) = a.vidKSAGradedEvent.get.compareTo(b.vidKSAGradedEvent.get)
        
      def primaryKey = vidKSAGradedEvent
}
 
object MdlKSAGradedEventList {
  val formList = Form[MdlKSAGradedEventList](
    mapping (
	"fidKSAGradedEvent" -> optional(of[Long]),
	"fKSA" -> list(of[Long]),
	"fGradedEvent" -> of[Long]
    )(MdlKSAGradedEventList.apply)(MdlKSAGradedEventList.unapply)
  )    
  }
 
 case class MdlKSAGradedEventList (
 	vidKSAGradedEvent : Option[Long],
	vKSA : List[Long],
	vGradedEvent : Long
    ) {
    
    def getList = {
      vKSA.map(ksa => 
        new MdlKSAGradedEvent(vidKSAGradedEvent, ksa, vGradedEvent))
    }
 }
 
 
