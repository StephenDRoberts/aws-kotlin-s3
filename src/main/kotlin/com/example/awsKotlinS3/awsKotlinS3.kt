package com.example.awsKotlinS3

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.s3.model.PutObjectResult
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.multipart.MultipartFile

@SpringBootApplication
class awsKotlinS3

fun main(args: Array<String>) {
    runApplication<awsKotlinS3>(*args)
}

@Configuration
class AWSConfiguration() {

    @Bean
    fun amazonS3Builder() = AmazonS3ClientBuilder.defaultClient()
}

@Controller
class AWSController(val amazonS3: AmazonS3) {

    @ResponseBody
    @GetMapping("/get-bucket-info")
    fun getBucketInformation(@RequestParam("bucket-name") bucketName: String): AwsS3ListObjectResponse {
        return if (amazonS3.doesBucketExistV2(bucketName)) {
            val objectListing = amazonS3.listObjects(bucketName)

            val fileNames = objectListing.objectSummaries.map { it.key }

            AwsS3ListObjectResponse(
                    objectListing.bucketName,
                    fileNames
            )
        } else {
            AwsS3ListObjectResponse()
        }
    }

    @ResponseBody
    @PostMapping("/file-upload")
    fun handleFileUpload(
            @RequestParam("bucket-name") bucketName: String,
            @RequestParam("file") file: MultipartFile): PutObjectResult{

        println(file.originalFilename)
        println(file.size)

        val result = amazonS3.putObject(bucketName, file.originalFilename, file.inputStream, ObjectMetadata())

        return result
    }
}


data class AwsS3ListObjectResponse(
        val bucketName: String = "N/A",
        val files: List<String> = emptyList()
)
