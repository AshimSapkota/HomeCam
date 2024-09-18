package com.surendramaran.yolov9tflite

import android.content.Context
import javax.mail.util.ByteArrayDataSource
import android.net.Uri
import java.util.*
import javax.mail.*
import javax.mail.internet.*
import javax.activation.*

class EmailSender(
    private val context: Context,
    private val senderEmail: String,
    private val senderPassword: String,
    private val recipientEmail: String
) {
    fun sendEmail(subject: String, body: String, imageUri: Uri) {
        val properties = Properties().apply {
            put("mail.smtp.auth", "true")
            put("mail.smtp.starttls.enable", "true")
            put("mail.smtp.host", "smtp.gmail.com")
            put("mail.smtp.port", "587")
        }

        val session = Session.getInstance(properties, object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(senderEmail, senderPassword)
            }
        })

        try {
            val message = MimeMessage(session).apply {
                setFrom(InternetAddress(senderEmail))
                setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail))
                setSubject(subject)

                val multipart = MimeMultipart().apply {
                    addBodyPart(MimeBodyPart().apply { setText(body) })
                    addBodyPart(MimeBodyPart().apply {
                        val inputStream = context.contentResolver.openInputStream(imageUri)
                        setDataHandler(DataHandler(ByteArrayDataSource(inputStream, "image/jpeg")))
                        setFileName("detected_person.jpg")
                    })
                }

                setContent(multipart)
            }

            Transport.send(message)
        } catch (e: MessagingException) {
            throw e
        }
    }
}