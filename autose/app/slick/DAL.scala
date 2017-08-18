package slick

import scala.slick.driver.ExtendedProfile
 
class DAL(override val profile: ExtendedProfile) extends ProgramsComponent 
	with ProgramOutcomesComponent 
	with GradedRequirementsComponent
	with EventTypeComponent
	with SubGradedEventComponent
	with KSAGradedEventComponent
	with KSASubGradedEventComponent
	with KSAPerfIndicatorComponent
	with PerformanceIndicatorComponent
	with GradedRequirementLinksComponent
	with ProgramEducationalObjectivesComponent
	with PEOtoSLOComponent 
	with KSAComponent
	with CoursesSlickComponent
	with LessonsComponent
	with LessonKSAComponent
	with DefinitionsComponent
	with LessonLinksComponent
	with CourseObjectivesComponent
	with RequiredCoursesComponent
	with ElectiveGroupComponent
	with ElectiveGroupCourseComponent
	with StudentsComponent
	with GradedEventAMSComponent
	with SubEventAMSComponent
	with GradesSubEventAMSComponent
	with MappingGradedEventComponent
	with MappingSubEventComponent
	with KSASubEventAMSComponent
	with GradedEventAMSLinksComponent
	with PerfIndKSASubEventAMSComponent
	with GradesAMSComponent
  with LessonObjectiveComponent
  with EvaluationComponent
	with CourseObjPerfIndComponent
  with EvalCourseObjSubEventComponent
  with SubEventCourseObjComponent
	with Profile {
 
  import profile.simple._
 
}