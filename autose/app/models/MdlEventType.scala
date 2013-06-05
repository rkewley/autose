    
 package models
    
 case class MdlEventType (
 	vidEventType : Option[Long],
	vEventType : String,
	vDescription : String
    ) extends Mdl[Long] {
    
      def this() = this(Option(0), "", "")

  	  def validate: Boolean = true
    
	  def validationErrors: String = ""
    
      def selectIdentifier: (String, String) = vidEventType.get.toString -> vDescription
    
      def compare(a: MdlEventType, b: MdlEventType) = a.vidEventType.get.compareTo(b.vidEventType.get)
        
      def primaryKey = vidEventType
}
    