package devmob.semanasacademicas.dialogs

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.iid.FirebaseInstanceId
import devmob.semanasacademicas.R
import devmob.semanasacademicas.get
import devmob.semanasacademicas.setErrorAndFocus
import devmob.semanasacademicas.users
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_tela_principal.*
import kotlinx.android.synthetic.main.app_bar_tela_principal.*
import org.jetbrains.anko.okButton
import org.jetbrains.anko.support.v4.alert

class LoginDialog : DialogFragment() {

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.LoginDialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?)
            = inflater.inflate(R.layout.activity_login, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mAuth = FirebaseAuth.getInstance()
        mAuth.useAppLanguage()

        txtRegister.setOnClickListener {
            RegisterDialog().show(activity?.supportFragmentManager!!.beginTransaction(), "RegisterDialog")
//            dismiss()
//            startActivity<RegisterActivity>()
        }

        btnLogin.setOnClickListener {
            attemptLogin()
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.run {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }
    }

    fun attemptLogin(){
        val progressDialog = ProgressDialog.show(this.context, "Aguarde", "Conectando...", true)

        val email = txtEmail.text.toString().trim()
        val pwd = txtPassword.text.toString().trim()

        when {
            email.isEmpty() -> {
                txtEmail.setErrorAndFocus("Insira seu email")
                progressDialog.dismiss()
            }
            !email.contains("@") -> {
                txtEmail.setErrorAndFocus("Email inválido")
                progressDialog.dismiss()
            }
            pwd.isEmpty() -> {
                txtEmail.setErrorAndFocus("Insira sua senha")
                progressDialog.dismiss()
            }
            else -> {
                mAuth.signInWithEmailAndPassword(email, pwd)
                    .addOnSuccessListener {result->
                        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
                            val newToken = HashMap<String, Any>()
                            newToken["token"] = it.token
                            FirebaseFirestore.getInstance().users[result!!.user.uid].set(newToken, SetOptions.merge())
                        }
                        progressDialog.dismiss()
                        dismiss()
//                        finish() //fecha a tela de login e vai para a tela principal
                    }
                    .addOnFailureListener {
                        progressDialog.dismiss()
                        when (it){

                            is FirebaseNetworkException -> {
                                alert("Sem conexão com a internet"){
                                    okButton {  }
                                }.show()
                            }
                            is FirebaseAuthInvalidUserException -> {
                                txtEmail.setErrorAndFocus("Email não cadastrado")
                            }
                            is FirebaseAuthInvalidCredentialsException -> {
                                txtPassword.setErrorAndFocus("Senha incorreta")
                            }
                            else -> {
                                alert(it.message.toString(), "Algo deu errado") {
                                    okButton {  }
                                }.show()
                                Log.d("loginDebug", "${it.javaClass.simpleName}: ${it.message}")
                            }
                        }
                    }
            }
        }

    }
}