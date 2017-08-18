package controllers

import java.io.{ByteArrayInputStream, File, InputStream}

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model._
import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import play.api.libs.iteratee.{Enumeratee, Enumerator, Iteratee, Traversable}
import play.api.mvc._
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import controllers.CourseLinksController.compositeAction
import controllers.CourseTopicsController.compositeAction
import models.NormalUser
import play.mvc.Result

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.{Failure, Success, Try}


object AmazonS3Controller extends Base {
  //val s3 = AmazonS3ClientBuilder.defaultClient()



  val awsCreds = new BasicAWSCredentials("AKIAIJAWZMUKVUJXBOAQ", "WoKX+n0TjEKlhhzOW+/mM6PcB8VWYIacFbREN7bp")
  val s3: AmazonS3 = AmazonS3ClientBuilder.standard.withCredentials(new AWSStaticCredentialsProvider(awsCreds)).build
  val bucket = Globals.s3BucketName

  def getS3File(key_name: String): Try[S3ObjectInputStream] = Try{
    println("Tryiing to get S3 file " + key_name)
    val stripedKey = Globals.stripOddCharacters(key_name)
    println("Stripped key is " + stripedKey)
    s3.getObject(bucket, stripedKey).getObjectContent
  }

  def getFile(is: InputStream): ChunkedResult[Array[Byte]] = {
    val fileContent: Enumerator[Array[Byte]] = Enumerator.fromStream(is)
    println("Sending file result")
    Results.Ok.stream(fileContent)
  }

  def sendS3File(key_name: String) = compositeAction(NormalUser) { implicit user => implicit template => implicit request =>
    println("Getting file " + key_name)
    getS3File(key_name) match {
      case Success(s3File) => getFile(s3File)
      case Failure(ex) =>
        println("File " + key_name + " not found")
        Results.NotFound(ex.getMessage)
    }
  }

  def uploadStream(key: String, enum: Enumerator[Array[Byte]], contentType: String)
                  (implicit ec: ExecutionContext): Future[CompleteMultipartUploadResult] = {
    import scala.collection.JavaConversions._
    val objectMetadata: ObjectMetadata = new ObjectMetadata()
    objectMetadata.setContentType(contentType)
    val initRequest = new InitiateMultipartUploadRequest(bucket, key).withSSEAwsKeyManagementParams(new SSEAwsKeyManagementParams())
      .withObjectMetadata(objectMetadata)
    val initResponse = s3.initiateMultipartUpload(initRequest)
    val uploadId = initResponse.getUploadId

    val rechunker: Enumeratee[Array[Byte], Array[Byte]] = Enumeratee.grouped {
      Traversable.takeUpTo[Array[Byte]](5 * 1024 * 1024) &>> Iteratee.consume()
    }

    val uploader = Iteratee.foldM[Array[Byte], Seq[PartETag]](Seq.empty) { case (etags, bytes) =>
      val uploadRequest = new UploadPartRequest()
        .withBucketName(bucket)
        .withKey(key)
        .withPartNumber(etags.length + 1)
        .withUploadId(uploadId)
        .withInputStream(new ByteArrayInputStream(bytes))
        .withPartSize(bytes.length)

      val etag = Future { s3.uploadPart(uploadRequest).getPartETag }
      etag.map(etags :+ _)
    }

    val futETags = enum &> rechunker |>>> uploader

    futETags.map { etags =>
      val compRequest = new CompleteMultipartUploadRequest(bucket, key, uploadId, etags.toBuffer[PartETag])
      s3.completeMultipartUpload(compRequest)
    }.recoverWith { case e: Exception =>
      s3.abortMultipartUpload(new AbortMultipartUploadRequest(bucket, key, uploadId))
      Future.failed(e)
    }

  }



  def uploadS3FileFuture(key_name: String, file: File, contentType: String): Try[CompleteMultipartUploadResult] = {
    val enum = Enumerator.fromFile(file)
    Try(Await.result(uploadStream(key_name, enum, contentType), 10 seconds))
  }
}
