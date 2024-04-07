package com.demo.chat

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import io.grpc.stub.CallStreamObserver
import java.util.concurrent.TimeUnit

class SignupActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var etSignupPhone: EditText
    private lateinit var etSignupOTP: EditText
    private lateinit var btnSignup: Button
    private lateinit var btnGetOTP: ImageView
    private var verificationId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        firebaseAuth = FirebaseAuth.getInstance()
        etSignupPhone = findViewById(R.id.signupMail)
        etSignupOTP = findViewById(R.id.signupPassword)
        btnSignup = findViewById(R.id.btnSignup)
        btnGetOTP = findViewById(R.id.btnGetOTP)

        if(firebaseAuth.currentUser != null){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnGetOTP.setOnClickListener{
            if (TextUtils.isEmpty(etSignupPhone.text.toString())) {
                Toast.makeText(this@SignupActivity, "Please enter phone number.", Toast.LENGTH_SHORT).show()
            } else {
                val phoneNumber = etSignupPhone.text.toString()
                Toast.makeText(this, "Please wait", Toast.LENGTH_SHORT).show()
                sendVerificationCode(phoneNumber)
            }
        }

        btnSignup.setOnClickListener{
            if (TextUtils.isEmpty(etSignupOTP.text.toString())) {
                Toast.makeText(this@SignupActivity, "Please enter OTP", Toast.LENGTH_SHORT).show()
            } else {
                verifyCode(etSignupOTP.text.toString())
            }
        }
    }

    private fun sendVerificationCode(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(mCallBack)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private val mCallBack: PhoneAuthProvider.OnVerificationStateChangedCallbacks =
        object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onCodeSent(s: String, forceResendingToken: PhoneAuthProvider.ForceResendingToken) {
                super.onCodeSent(s, forceResendingToken)
                verificationId = s
            }

            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                val code = phoneAuthCredential.smsCode
                if (code != null) {
                    etSignupOTP.setText(code)
                    verifyCode(code)
                }
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Toast.makeText(this@SignupActivity, "Please enter valid number", Toast.LENGTH_SHORT).show()
            }
        }

    private fun verifyCode(code: String) {
        val credential = PhoneAuthProvider.getCredential(verificationId!!, code)
        signInWithCredential(credential)
    }

    private fun signInWithCredential(credential: PhoneAuthCredential) {
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(OnCompleteListener<AuthResult> { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    if (user != null) {
                        val userId = user.uid
                    }else {
                        Toast.makeText(this@SignupActivity, "User is null", Toast.LENGTH_SHORT).show()
                    }

                    val i = Intent(this@SignupActivity, UserDetailActivity::class.java)
                    startActivity(i)
                    finish()
                } else {
                    Toast.makeText(this@SignupActivity, "Error", Toast.LENGTH_SHORT).show()

                }
            })
    }
}