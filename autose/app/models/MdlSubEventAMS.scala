    
 package models
 
 import slick.AppDB
    
 case class MdlSubEventAMS (
 	vidSubEventAMS : Option[Long],
	vGradedEventNumberAMS : Long,
	vSubEventNumberAMS : Long,
	vGradedEvent : Long,
	vName : String,
	vDescription : String,
	vPoints : Double
    ) extends Mdl[Long] {
    
      def this() = this(Option(0), 0, 0, 0, "", "", 0.0)

  	  def validate: Boolean = true
    
	  def validationErrors: String = ""
    
      def selectIdentifier: (String, String) = vidSubEventAMS.get.toString -> {
        val gradedEventName = AppDB.dal.GradedEventAMS.select(vGradedEvent).get.vName
        gradedEventName + ", " + vName + ", " + vDescription
      }
    
      def compare(a: MdlSubEventAMS, b: MdlSubEventAMS) = a.vidSubEventAMS.get.compareTo(b.vidSubEventAMS.get)
        
      def primaryKey = vidSubEventAMS
}
    