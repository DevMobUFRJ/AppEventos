package devmob.semanasacademicas.activities

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.Fade
import android.transition.Slide
import android.util.Log
import android.view.View
import android.view.Window
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import devmob.semanasacademicas.R
import devmob.semanasacademicas.setErrorAndFocus
import devmob.semanasacademicas.get
import devmob.semanasacademicas.users
import kotlinx.android.synthetic.main.activity_register.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.okButton
import org.jetbrains.anko.startActivity

class RegisterActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.title = "Cadastro"

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId == btnUFRJStudent.id || checkedId == btnOtherStudent.id){
                layoutCourse.visibility = View.VISIBLE
            }
            else {
                layoutCourse.visibility = View.GONE
            }
        }

        btnRegister.setOnClickListener{
            register()
        }
    }





    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {


        startActivity<LoginActivity>()
        finish()
        overridePendingTransition(R.anim.fade_in, R.anim.exit_to_left)
    }

    fun register() {

        val progressDialog = ProgressDialog.show(this, "Aguarde", "Conectando...", true)
        val name = txtName.text.toString().trim()
        val email = txtEmail.text.toString().trim()
        val pwd = txtPassword.text.toString().trim()
        val confirmPwd = txtConfirmPassword.text.toString().trim()
        val course = txtCourse.text.toString().trim()
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
            (type == "Aluno da UFRJ" || type == "Aluno de outra instituição") && course.isEmpty() -> {
                progressDialog.dismiss()
                txtCourse.setErrorAndFocus("Curso não informado")
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
                                if (type == "Aluno da UFRJ" || type == "Aluno de outra instituição") map["course"] = course
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
                        finish()
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






















