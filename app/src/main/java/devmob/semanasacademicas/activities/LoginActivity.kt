package devmob.semanasacademicas.activities

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import android.util.Log
import android.view.View
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
import org.jetbrains.anko.alert
import org.jetbrains.anko.okButton
import org.jetbrains.anko.startActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mAuth = FirebaseAuth.getInstance()
        mAuth.useAppLanguage()

        txtRegister.setOnClickListener {
            finish()
            startActivity<RegisterActivity>()
            overridePendingTransition(R.anim.enter_from_left, R.anim.fade_out)
        }

        btnLogin.setOnClickListener {
            attemptLogin()
        }

    }

    fun attemptLogin(){

        val progressDialog = ProgressDialog.show(this, "Aguarde", "Conectando...", true)

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
                        finish() //fecha a tela de login e vai para a tela principal
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
