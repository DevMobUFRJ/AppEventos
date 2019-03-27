package devmob.semanasacademicas.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import devmob.semanasacademicas.R
import devmob.semanasacademicas.activities.TelaPrincipal
import kotlinx.android.synthetic.main.activity_login_or_register.*
import kotlinx.android.synthetic.main.fragment_login.*
import org.jetbrains.anko.*
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.startActivity

class LoginFragment : Fragment() {

    private lateinit var mAuth: FirebaseAuth

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
            = inflater.inflate(R.layout.fragment_login, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mAuth = FirebaseAuth.getInstance()

        if(mAuth.currentUser != null){
            startActivity<TelaPrincipal>()
            activity?.finish()
        }

        email_sign_in_button.setOnClickListener { attemptLogin() }
        email_register_button.setOnClickListener { activity?.mPager!!.currentItem = 1 }
    }


    private fun attemptLogin() {
        var focusView: View? = null

        email.error = null
        password.error = null

        val emailStr = email.text.toString().trim()
        val passwordStr = password.text.toString().trim()

        var validEmail = false
        var validPassword = false

        when {
            emailStr.isBlank() -> email.error = getString(R.string.empty_email)
            !emailStr.contains('@') -> email.error = getString(R.string.not_email)
            else -> validEmail = true
        }

        when {
            passwordStr.isBlank() -> password.error = getString(R.string.empty_password)
            passwordStr.length < 6 -> password.error = getString(R.string.short_password)
            else -> validPassword = true
        }

        if (validEmail && validPassword) {
            mAuth.signInWithEmailAndPassword(emailStr, passwordStr)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        startActivity<TelaPrincipal>()
                        activity?.finish()
                    }
                    else {
                        try {
                            throw it.exception!!
                        } catch (e: FirebaseNetworkException) {
                            //Sem internet
                            alert(getString(R.string.no_internet)) {
                                okButton {}
                            }.show()
                        } catch (e: FirebaseAuthInvalidCredentialsException) {
                            //Senha incorreta
                            password.error = getString(R.string.incorrect_password)
                            focusView = password
                        } catch (e: FirebaseAuthInvalidUserException) {
                            //Email nao existe ou desabilitado
                            email.error = getString(R.string.email_no_exists)
                            focusView = email
                        }
                        focusView?.requestFocus()
                    }
                }
        } else {
            focusView = if (validEmail) password else email
            focusView?.requestFocus()
        }
    }

}
