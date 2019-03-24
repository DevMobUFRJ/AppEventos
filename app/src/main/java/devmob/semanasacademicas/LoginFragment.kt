package devmob.semanasacademicas

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_login_or_register.*

import kotlinx.android.synthetic.main.fragment_login.*
import org.jetbrains.anko.*
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.intentFor
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.viewPager
import java.util.HashMap

/**
 * A login screen that offers login via email/password.
 */
class LoginFragment : Fragment() {
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
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
            emailStr.isBlank() -> email.error = "Email nao pode ser vazio"
            !emailStr.contains('@') -> email.error = "Isso nao e um email"
            else -> validEmail = true
        }

        when {
            passwordStr.isBlank() -> password.error = "Senha nao pode ser vazia"
            passwordStr.length < 6 -> password.error = "Senha muito curta"
            else -> validPassword = true
        }

        if (validEmail && validPassword) {
            mAuth.signInWithEmailAndPassword(emailStr, passwordStr)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        val campos = HashMap<String, Any>()
                        campos["token"] = FirebaseInstanceId.getInstance().token!!

                        FirebaseFirestore.getInstance()
                            .collection("users").document(mAuth.uid!!).set(campos, SetOptions.merge())

                        startActivity<TelaPrincipal>()
                        activity?.finish()

                    }
                    else {
                        try {
                            throw it.exception!!
                        } catch (e: FirebaseNetworkException) {
                            //Sem internet
                            alert("Sem conexao com a internet") {
                                okButton {}
                            }
                        } catch (e: FirebaseAuthInvalidCredentialsException) {
                            //Senha incorreta
                            password.error = "Senha incorreta"
                            focusView = password
                        } catch (e: FirebaseAuthInvalidUserException) {
                            //Email nao existe ou desabilitado
                            email.error = "Email nao cadastrado"
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
