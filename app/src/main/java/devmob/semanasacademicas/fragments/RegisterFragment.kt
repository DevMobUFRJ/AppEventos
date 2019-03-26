package devmob.semanasacademicas.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import devmob.semanasacademicas.R
import devmob.semanasacademicas.activities.TelaPrincipal

import kotlinx.android.synthetic.main.fragment_register.*
import org.jetbrains.anko.*
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.startActivity

/**
 * A login screen that offers login via email/password.
 */
class RegisterFragment : Fragment() {
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
            = inflater.inflate(R.layout.fragment_register, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Set up the login form.

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        email_register_button_2.setOnClickListener { registerFirebase() }
    }


    private fun registerFirebase() {
        var focusView: View? = null

        emailR.error = null
        passwordR.error = null
        username.error = null

        val emailStr = emailR.text.toString().trim()
        val passwordStr = passwordR.text.toString().trim()
        val name = username.text.toString().trim()

        var validEmail = false
        var validPassword = false
        var validUserName = false

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

        when{
            name.isBlank() -> username.error = "Nome não pode ser vazio"
            else -> validUserName = true
        }

        if (validEmail && validPassword && validUserName) {
            mAuth.createUserWithEmailAndPassword(emailStr, passwordStr)
                .addOnCompleteListener {
                    if (it.isSuccessful){
                        alert("Conta criada").show()
                        mAuth.signInWithEmailAndPassword(emailStr, passwordStr).addOnSuccessListener {
                            val user = mAuth.currentUser!!

                            val changeBuilder = UserProfileChangeRequest.Builder()
                                .setDisplayName(name).build()

                            user.updateProfile(changeBuilder).addOnCompleteListener {
                                val campos = HashMap<String, Any>()
                                campos["uid"] = user.uid
                                campos["email"] = user.email!!
                                campos["token"] = FirebaseInstanceId.getInstance().token!!

                                db.collection("users").document(user.uid).set(campos).addOnCompleteListener {
                                    startActivity<TelaPrincipal>()
                                    activity?.finish()
                                }
                            }


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
            focusView = when{
                !validEmail -> emailR
                !validPassword -> passwordR
                !validUserName -> username
                else -> null
            }
            focusView?.requestFocus()
        }
    }
}
