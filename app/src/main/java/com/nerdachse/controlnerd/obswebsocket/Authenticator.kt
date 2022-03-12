package com.nerdachse.controlnerd.obswebsocket

import java.nio.charset.Charset
import java.security.MessageDigest
import android.util.Base64
/*
 * Source: https://github.com/obsproject/obs-websocket/blob/master/docs/generated/protocol.md#creating-an-authentication-string
 * To generate the authentication string, follow these steps:
 *
 * Concatenate the websocket password with the salt provided by the server (password + salt)
 * Generate an SHA256 binary hash of the result and base64 encode it, known as a base64 secret.
 * Concatenate the base64 secret with the challenge sent by the server (base64_secret + challenge)
 * Generate a binary SHA256 hash of that result and base64 encode it. You now have your authentication string.
*/
fun buildAuthenticationString(password: String, challenge: String, salt: String): String {
    val concatenated = password + salt
    val digest = MessageDigest.getInstance("SHA-256")
    val sha256Binary = digest.digest(concatenated.toByteArray())
    val base64Secret: ByteArray = Base64.encode(sha256Binary, Base64.NO_WRAP)
    val secretAndChallenge = digest.digest(base64Secret + challenge.toByteArray())
        Charset.forName("UTF-8")
    return Base64.encode(secretAndChallenge, Base64.NO_WRAP).toString(
        Charset.forName("UTF-8"))
}
