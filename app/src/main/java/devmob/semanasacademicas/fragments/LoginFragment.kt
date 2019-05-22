package devmob.semanasacademicas.fragments

import android.app.ProgressDialog
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
import devmob.semanasacademicas.R
import devmob.semanasacademicas.activities.LoginActivity
import devmob.semanasacademicas.activities.RegisterActivity
import devmob.semanasacademicas.activities.TelaPrincipal
import devmob.semanasacademicas.get
import devmob.semanasacademicas.users
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
            FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
                val campos = HashMap<String, Any>()
                campos["token"] = it.token
                FirebaseFirestore.getInstance().users[mAuth.currentUser!!.uid].set(campos, SetOptions.merge())
            }

            //startActivity<TelaPrincipal>()
            activity?.finish()
        }

        email_sign_in_button.setOnClickListener { attemptLogin() }
        email_register_button.setOnClickListener {
            activity!!.supportFragmentManager
                .beginTransaction()
                .replace(R.id.contentLogin, RegisterFragment())
                .commit()
        }
        testLogin.setOnClickListener {
            startActivity<LoginActivity>()
        }
    }


    private fun attemptLogin() {
        var focusView: View? = null
        val progressDialog = ProgressDialog.show(this.context, "Aguarde", "Processando...", true)

        email.error = null
        password.error = null

        val emailStr = email.text.toString().trim()
        val passwordStr = password.text.toString().trim()

        var validEmail = false
        var validPassword = false

        when {
            emailStr.isBlank() -> {
                email.error = getString(R.string.empty_email)
                email.requestFocus()
                progressDialog.dismiss()
            }
            !emailStr.contains('@') -> {
                email.error = getString(R.string.not_email)
                email.requestFocus()
                progressDialog.dismiss()
            }
            else -> validEmail = true
        }

        when {
            passwordStr.isBlank() -> {
                password.error = getString(R.string.empty_password)
                password.requestFocus()
                progressDialog.dismiss()
            }
            passwordStr.length < 6 -> {
                password.error = getString(R.string.short_password)
                password.requestFocus()
                progressDialog.dismiss()
            }
            else -> validPassword = true
        }

        if (validEmail && validPassword) {
            mAuth.signInWithEmailAndPassword(emailStr, passwordStr)
                .addOnCompleteListener {task ->
                    if (task.isSuccessful) {
                        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
                            val campos = HashMap<String, Any>()
                            campos["token"] = it.token
                            FirebaseFirestore.getInstance().users[task.result!!.user.uid].set(campos, SetOptions.merge())
                        }
                        progressDialog.dismiss()
                        activity?.finish()
                        //startActivity<TelaPrincipal>() //não é necessário chamar a activity TelaPrincipal pois já está iniciada
                    }
                    else {
                        progressDialog.dismiss()
                        try {
                            throw task.exception!!
                        } catch (e: FirebaseNetworkException) {
                            //Sem internet
                            alert(getString(R.string.no_internet)) {
                                okButton {}
                            }.show()
                        } catch (e: FirebaseAuthInvalidUserException) {
                            //Email nao existe ou desabilitado
                            email.error = getString(R.string.email_no_exists)
                            focusView = email
                        } catch (e: FirebaseAuthInvalidCredentialsException) {
                            //Senha incorreta
                            password.error = getString(R.string.incorrect_password)
                            focusView = password
                        }
                        focusView?.requestFocus()
                    }
                }
        } /*else {
            focusView = if (validEmail) password else email
            focusView?.requestFocus()
        }*/
    }

}
