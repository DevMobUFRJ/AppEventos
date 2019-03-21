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
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_login.*

import kotlinx.android.synthetic.main.activity_register.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.okButton
import org.jetbrains.anko.startActivity

/**
 * A login screen that offers login via email/password.
 */
class RegisterActivity : AppCompatActivity() {
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        // Set up the login form.

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        email_register_button_2.setOnClickListener { registerFirebase() }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private fun registerFirebase() {
        var focusView: View? = null

        emailR.error = null
        passwordR.error = null

        val emailStr = emailR.text.toString().trim()
        val passwordStr = passwordR.text.toString().trim()

        var validEmail = false
        var validPassword = false

        when{
            emailStr.isBlank() -> emailR.error = "Email nao pode ser vazio"
            !emailStr.contains('@') -> emailR.error = "Isso nao e um email"
            else -> validEmail = true
        }

        when {
            passwordStr.isBlank() -> passwordR.error = "Senha nao pode ser vazia"
            passwordStr.length < 6 -> passwordR.error = "Senha muito curta"
            else -> validPassword = true
        }

        if (validEmail && validPassword) {
            mAuth.createUserWithEmailAndPassword(emailStr, passwordStr)
                .addOnCompleteListener {
                    if (it.isSuccessful){
                        alert("Conta criada").show()
                        mAuth.signInWithEmailAndPassword(emailStr, passwordStr).addOnSuccessListener {
                            val user = mAuth.currentUser!!

                            val campos = HashMap<String, Any>()
                            campos["uid"] = user.uid
                            campos["email"] = user.email!!
                            campos["token"] = FirebaseInstanceId.getInstance().token!!

                            db.collection("users").document(user.uid).set(campos)
                            startActivity<TelaPrincipal>()
                        }

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
                            //Email malformado
                            emailR.error = "Email nao esta certo"
                            focusView = emailR
                        } catch (e: FirebaseAuthUserCollisionException) {
                            //Email ja usado
                            emailR.error = "Esse email ja esta em uso"
                            focusView = emailR
                        }
                        focusView?.requestFocus()
                    }
                }
        } else {
            focusView = if (validEmail) passwordR else emailR
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
