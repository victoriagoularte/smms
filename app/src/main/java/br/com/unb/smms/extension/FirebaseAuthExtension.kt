package br.com.unb.smms.extension

import br.com.unb.smms.SmmsData
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*

fun Exception.getMessage() = when(this) {
    is FirebaseNetworkException -> "Tempo de resposta esgotado."
    is FirebaseAuthInvalidCredentialsException -> "Usuário ou senha incorretos."
    is FirebaseTooManyRequestsException -> "Bloqueamos todas as solicitações deste dispositivo devido a atividades incomuns. Tente mais tarde."
    is FirebaseAuthEmailException -> "Usuário não cadastrado."
    is FirebaseAuthUserCollisionException -> "Usuário já possui cadastro."
    is FirebaseAuthInvalidUserException -> "Usuário não encontrado."
    is FirebaseAuthWeakPasswordException -> "Senha fraca. Sua senha deve conter no mínimo 6 caracteres."
    else -> "Erro de conexao com Firebase"
}