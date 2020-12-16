package br.com.unb.smms.view.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import br.com.unb.smms.R
import br.com.unb.smms.SmmsData
import br.com.unb.smms.SmmsException
import br.com.unb.smms.databinding.FragmentLoginBinding
import br.com.unb.smms.extension.getMessage
import br.com.unb.smms.extension.isValidEmail
import br.com.unb.smms.extension.toJson
import br.com.unb.smms.interactor.LoginInteractor
import br.com.unb.smms.security.SecurityConstants
import br.com.unb.smms.security.getEncrypSharedPreferences
import br.com.unb.smms.viewmodel.LoginViewModel
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Fragment() {

    companion object {
        const val INVALID_USERID = -9999
    }

    private lateinit var binding: FragmentLoginBinding
    private var auth = FirebaseAuth.getInstance()

    private val viewModel: LoginViewModel by lazy {
        ViewModelProviders.of(this@LoginFragment).get(LoginViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)

        binding.viewModel = viewModel
        binding.fragment = this@LoginFragment
        binding.lifecycleOwner = this

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.resultRegister.observe(viewLifecycleOwner, Observer {
            when (it) {
                is SmmsData.Success -> Toast.makeText(context, it.data, Toast.LENGTH_SHORT).show()
                is SmmsData.Error -> Toast.makeText(
                    context,
                    it.error.localizedMessage,
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    fun signIn(view: View) {

        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()

        try {
            if (!email.isValidEmail()) {
                throw SmmsException(
                    context?.getString(R.string.invalid_email),
                    LoginInteractor.INVALID_USERID
                )
            } else {
                auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {

                    getEncrypSharedPreferences(requireContext()).edit()
                        .putString(SecurityConstants.UID_FIREBASE, it.user!!.uid).apply()

                    goToChooseApplication()
                }.addOnFailureListener {
                    Toast.makeText(context, it.getMessage(), Toast.LENGTH_LONG).show()
                }
            }
        } catch (e: Exception) {
            Toast.makeText(context, e.localizedMessage, Toast.LENGTH_LONG).show()
        }
    }

    fun register(view: View) {

        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()

        try {
            if (!email.isValidEmail()) {
                throw SmmsException(
                    context?.getString(R.string.invalid_email),
                    LoginInteractor.INVALID_USERID
                )
            } else {
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(requireActivity()) {
                    if (it.isSuccessful) {
                        Toast.makeText(context, "Cadastro realizado com sucesso.", Toast.LENGTH_LONG).show()
                        goToChooseApplication()
                    } else {
                        Toast.makeText(context, "Falha ao cadastrar usuário.", Toast.LENGTH_LONG).show()
                    }
                }
            }
        } catch (e: Exception) {
            Toast.makeText(context, e.localizedMessage, Toast.LENGTH_LONG).show()
        }

    }

    private fun goToChooseApplication() {
        findNavController().navigate(R.id.action_loginFragment_to_selectSocialMediaActivity)
    }


}
