package devmob.semanasacademicas.dialogs

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import devmob.semanasacademicas.R
import devmob.semanasacademicas.get
import devmob.semanasacademicas.setErrorAndFocus
import devmob.semanasacademicas.users
import kotlinx.android.synthetic.main.activity_register.*
import org.jetbrains.anko.okButton
import org.jetbrains.anko.support.v4.alert

class RegisterDialog: DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.RegisterDialog)
    }

    override fun onStart() {
        super.onStart()
        dialog?.run {
            window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?)
            = inflater.inflate(R.layout.activity_register, container, false)

    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        toolbar.title = "Cadastro"
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_material)
        toolbar.setNavigationOnClickListener {
            dismiss()
        }
        btnRegister.setOnClickListener{
            register()
        }
    }

    fun register() {
        val progressDialog = ProgressDialog.show(context, "Aguarde", "Conectando...", true)
        val name = txtName.text.toString().trim()
        val email = txtEmail.text.toString().trim()
        val pwd = txtPassword.text.toString().trim()
        val confirmPwd = txtConfirmPassword.text.toString().trim()
        val type = when {
            btnUFRJStudent.isChecked -> "Aluno da UFRJ"
            btnUFRJTeacher.isChecked -> "Professor da UFRJ"
            btnOtherStudent.isChecked -> "Aluno de outra instituição"
            btnOtherTeacher.isChecked -> "Professor de outra instituição"
            btnOther.isChecked -> "Outros"
            else-> ""
        }

        when {
            name.isEmpty() -> {
                txtName.setErrorAndFocus("Insira seu nome")
                progressDialog.dismiss()
            }
            email.isEmpty() -> {
                txtEmail.setErrorAndFocus("Insira seu email")
                progressDialog.dismiss()
            }
            !email.contains("@") -> {
                txtEmail.setErrorAndFocus("Email inválido")
                progressDialog.dismiss()
            }
            pwd.isEmpty() -> {
                txtPassword.setErrorAndFocus("Insira sua senha")
                progressDialog.dismiss()
            }
            pwd.length < 6 -> {
                txtPassword.setErrorAndFocus("Senha muito curto")
                progressDialog.dismiss()
            }
            confirmPwd != pwd -> {
                txtConfirmPassword.setErrorAndFocus("Senhas não conferem")
                progressDialog.dismiss()
            }
            type.isEmpty() -> {
                progressDialog.dismiss()
                alert("Por favor, selecione uma opção") { okButton {} }.show()
            }
            else -> {
                mAuth.createUserWithEmailAndPassword(email, pwd)
                    .addOnSuccessListener {
                        mAuth.signInWithEmailAndPassword(email, pwd)
                            .addOnSuccessListener {
                                val user = mAuth.currentUser!!
                                val changeBuilder = UserProfileChangeRequest.Builder().setDisplayName(name).build()
                                user.updateProfile(changeBuilder)
                                    .addOnSuccessListener {
                                        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
                                            val map = HashMap<String, Any>()
                                            map["uid"] = user.uid
                                            map["email"] = email
                                            map["token"] = it.token
                                            map["name"] = name
                                            map["type"] = type
                                            db.users[user.uid].set(map)
                                        }
                                    }
                                    .addOnFailureListener {
                                        progressDialog.dismiss()
                                        alert("${it.message}", "Algo deu errado"){ okButton {} }.show()
                                        Log.d("myDebug", "Falha ao atualizar nome.\n${it.javaClass.simpleName}: ${it.message}")
                                    }
                                progressDialog.dismiss()
                                alert("Usuário criado com sucesso") {
                                    okButton {}
                                }
                                (activity?.supportFragmentManager?.findFragmentByTag("LoginDialog") as DialogFragment).dismiss()
                                dismiss()

                            }
                            .addOnFailureListener {
                                progressDialog.dismiss()
                                alert("${it.message}", "Algo deu errado"){ okButton {} }.show()
                                Log.d("myDebug", "Falha ao fazer login.\n${it.javaClass.simpleName}: ${it.message}")
                            }
                    }
                    .addOnFailureListener {
                        progressDialog.dismiss()
                        alert("${it.message}", "Algo deu errado"){ okButton {} }.show()
                        Log.d("myDebug", "Falha ao criar usuário.\n${it.javaClass.simpleName}: ${it.message}")
                    }
            }


        }

    }
}