package devmob.semanasacademicas

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.support.v7.app.AppCompatActivity
import android.os.Build
import android.os.Bundle
import android.view.View
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.iid.FirebaseInstanceId

import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.okButton
import org.jetbrains.anko.startActivity
import java.util.HashMap

/**
 * A login screen that offers login via email/password.
 */
class LoginActivity : AppCompatActivity() {
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mAuth = FirebaseAuth.getInstance()

        if(mAuth.currentUser != null) startActivity<TelaPrincipal>()

        email_sign_in_button.setOnClickListener { attemptLogin() }
        email_register_button.setOnClickListener { startActivity<RegisterActivity>() }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
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


    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private fun showProgress(show: Boolean) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        val shortAnimTime = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()

        login_form.visibility = if (show) View.GONE else View.VISIBLE
        login_form.animate()
            .setDuration(shortAnimTime)
            .alpha((if (show) 0 else 1).toFloat())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    login_form.visibility = if (show) View.GONE else View.VISIBLE
                }
            })

        login_progress.visibility = if (show) View.VISIBLE else View.GONE
        login_progress.animate()
            .setDuration(shortAnimTime)
            .alpha((if (show) 1 else 0).toFloat())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    login_progress.visibility = if (show) View.VISIBLE else View.GONE
                }
            })

    }
}
