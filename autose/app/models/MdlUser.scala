    
 package models
    
 case class MdlUser (
 	vemail : String,
	vpassword : String,
	vpermissions : String
    )  {
   
	  def permission: Permission = vpermissions match {
	    case "Administrator" => Administrator
			case "Faculty" => Faculty
	    case "NormalUser" => NormalUser
	    case (_) => throw new Exception("Cannot map permission type " + vpermissions)
	  }

	  def hasEditPermission: Boolean = permission match {
				case Administrator => true
				case Faculty => true
				case _ => false
		}
    
      def this() = this("", "", "NormalUser")

  	  def validate: Boolean = true
    
	  def validationErrors: String = ""
    
      def selectIdentifier: (String, String) = vemail.toString -> vemail.toString
    
      def compare(a: MdlUser, b: MdlUser) = a.vemail.compareTo(b.vemail)
}
    