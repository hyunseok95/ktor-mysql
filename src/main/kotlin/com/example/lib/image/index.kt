package com.example.lib.image

import java.awt.image.BufferedImage

class ImageUtilClass(){
    /**
    val file = File("static/image/room/uuid_main.jpg")
    val image: BufferedImage = ImageIO.read(file)
    val resizedImage = resizeImage(image, 300, 300)
    val result = ImageIO.write(resizedImage,"jpg",File("static/image/room/resized.jpg"))

    var file2 = File("static/image/room/resized.jpg")
    call.response.header(
    HttpHeaders.ContentDisposition,
    ContentDisposition.Attachment
    .withParameter(
    ContentDisposition.Parameters.FileName,
    "main.jpg")
    .toString()
    )
    call.respondFile(file2)
     */
    fun resizeImage(
        originalImage: BufferedImage,
        targetWidth: Int,
        targetHeight: Int
    ): BufferedImage {
        val resizedImage = BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB)
        val graphics2D = resizedImage.createGraphics()
        graphics2D.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null)
        graphics2D.dispose()
        return resizedImage
    }
}

