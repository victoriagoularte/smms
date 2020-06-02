package br.com.unb.smms.repository

import com.google.firebase.auth.FirebaseAuth


class LoginRepository() {

    private var auth = FirebaseAuth.getInstance()

    fun signIn(email: String, password: String): String? {

        auth.setLanguageCode("pt-br")
        return if(auth.signInWithEmailAndPassword(email, password).exception != null) {
            auth.signInWithEmailAndPassword(email, password).exception!!.message
        } else null

    }

    fun register(email: String, password: String): String? {

        auth.setLanguageCode("pt-br")
        return if(auth.createUserWithEmailAndPassword(email, password).exception != null) {
            auth.createUserWithEmailAndPassword(email, password).exception!!.message
        } else null

    }


}