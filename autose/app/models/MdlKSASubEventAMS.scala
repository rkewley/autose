    
 package models
    
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
    