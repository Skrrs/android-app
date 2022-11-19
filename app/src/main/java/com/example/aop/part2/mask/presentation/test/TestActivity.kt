package com.example.aop.part2.mask.presentation.test

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.example.aop.part2.mask.R
import com.example.aop.part2.mask.domain.request.API
import com.example.aop.part2.mask.domain.response.CommonResponse
import com.example.aop.part2.mask.domain.response.GradeEnum
import com.example.aop.part2.mask.domain.response.result.GradeResult
import com.example.aop.part2.mask.presentation.main.MainActivity
import com.example.aop.part2.mask.utils.api.RetrofitClass
import com.example.aop.part2.mask.utils.record.CountUpView
import com.example.aop.part2.mask.utils.record.RecordButton
import com.example.aop.part2.mask.utils.record.SoundVisualizerView
import com.example.aop.part2.mask.utils.record.State
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import java.io.File
import java.util.*

class TestActivity : AppCompatActivity() {
    private val soundVisualizerView: SoundVisualizerView by lazy {
        findViewById(R.id.soundVisualizeView)
    }
    private val recordTimeTextView: CountUpView by lazy {
        findViewById(R.id.txt_recordTime)
    }
    private val confirmButton: Button by lazy {
        findViewById(R.id.confirmButton)
    }
    private val recordButton: RecordButton by lazy {
        findViewById(R.id.recordButton)
    }
    private val requiredPermissions = arrayOf(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )
    private val recordingFilePath: String by lazy {
        "${externalCacheDir?.absolutePath}/recording.3gp"
    }
    private val recordingFilePath2: String by lazy{
        "${Environment.getExternalStorageDirectory().absolutePath}/Download/${Date().time}recording.wav"
    }

    // DB
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var userDB: DatabaseReference
    private val databaseReference = FirebaseDatabase.getInstance().reference

    // Retrofit (API)
    private var rtf : Retrofit? = null

    // record & play
    private var recordFile : File? = null
    private var recorder: MediaRecorder? = null
    private var player: MediaPlayer? = null
    private var state = State.BEFORE_RECORDING
        set(value) {
            field = value
            confirmButton.isEnabled =
                (value == State.AFTER_RECORDING) || (value == State.ON_PLAYING)
            recordButton.updateIconWithState(value)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        requestAudioPermission()
        initViews()
        bindViews()
        initVariavbles()

        val logo = findViewById<AppCompatButton>(R.id.header_logo)
        logo.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java) //
            startActivity(intent)
            finish()
        }

        // TODO - API request 추가 ?
        val btnStar = findViewById<AppCompatButton>(R.id.btnStar)
        val btnReplay = findViewById<AppCompatButton>(R.id.btnReplay)

    }

    private fun getCurrentUserID(): String{
        if (auth.currentUser == null){
            Toast.makeText(this, "로그인이 되어있지 않습니다.", Toast.LENGTH_SHORT).show()
            finish()
        }
        return auth.currentUser?.uid.orEmpty()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        val audioRecordPermissionGranted =
            requestCode == REQUEST_RECORD_AUDIO_PERMISSION &&
                    grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED

        if (!audioRecordPermissionGranted) {
            if (!shouldShowRequestPermissionRationale(permissions.first())) {
                showPermissionExplanationDialog()
            } else {
                finish()
            }
        }
    }

    private fun requestAudioPermission() {
        requestPermissions(requiredPermissions, REQUEST_RECORD_AUDIO_PERMISSION)
    }

    private fun initViews() {
        recordButton.updateIconWithState(state)
    }

    private fun bindViews() {
        rtf = RetrofitClass().getRetrofitInstance()
        // TODO - DB?
        // db = userDb.getInstance(applicationContext)
        userDB = Firebase.database.reference.child("Users")
        val currentUserDB = userDB.child(getCurrentUserID())

        soundVisualizerView.onRequestCurrentAmplitude = {
            recorder?.maxAmplitude ?: 0
        }
        confirmButton.setOnClickListener {
            stopPlaying()
            soundVisualizerView.clearVisualization()
            recordTimeTextView.clearCountTime()
            state = State.BEFORE_RECORDING
            if(recordFile != null){
                val requestFile = RequestBody.create(MediaType.parse("audio/wav"), recordFile)
                val record = MultipartBody.Part.createFormData("file", recordFile!!.name,requestFile)
                databaseReference.child("token").get().addOnSuccessListener {
                    Log.i("firebase", "Got value ${it.value}")
                    callGradeProblem(it.value as String, record)
                }.addOnFailureListener{
                    Log.e("firebase", "Error getting data", it)
                }
                // TODO - DB?
//                CoroutineScope(Dispatchers.IO).launch {
//                    tkList = db?.tokenDao()?.getAll()
//                    callGradeProblem(record)
//                }
            }
        }
        recordButton.setOnClickListener {
            when (state) {
                State.BEFORE_RECORDING -> {
                    startRecording()
                }
                State.ON_RECORDING -> {
                    stopRecording()
                }
                State.AFTER_RECORDING -> {
                    startPlaying()
                }
                State.ON_PLAYING -> {
                    stopPlaying()
                }
            }
        }
    }

    private fun callGradeProblem(token: String, body: MultipartBody.Part){
        val api = rtf?.create(API::class.java)
        val strToken = "Bearer ${token}"
        // val dto = RecordDto(body)
        val callAPI = api?.requestGrade(token = strToken, body)

        var result: GradeEnum = GradeEnum.PERFECT
        callAPI?.enqueue(object : retrofit2.Callback<CommonResponse<GradeResult>> {
            override fun onResponse(call: Call<CommonResponse<GradeResult>>, response: Response<CommonResponse<GradeResult>>) {
                if (response.isSuccessful) {
                    Log.d("GradeProblem Success", response.code().toString())

                    toastMsg("채점중입니다 ...")

                    // TODO - 주석해제
                    // result = response.body()?.result?.index!!
                    when(result){
                        GradeEnum.PERFECT -> {
                            // "PERFECT !!!"
                            gradeDialog("PERFECT ><")
                        }
                        GradeEnum.GREAT -> {
                            // "GREAT !!"
                            gradeDialog("GREAT ^^")
                        }
                        GradeEnum.GOOD -> {
                            // "GOOD :)"
                            gradeDialog("GOOD :)")
                        }
                        GradeEnum.BAD -> {
                            // "BAD :("
                            gradeDialog("BAD :(")
                        }
                    }
                } else{
                    Log.d("GradeProblem : Code 400 Error", response.toString())
                }
            }
            override fun onFailure(call: Call<CommonResponse<GradeResult>>, t: Throwable) {
                Log.d("GradeProblem : Code 500 Error", t.toString())
            }
        })
    }

    private fun initVariavbles() {
        state = State.BEFORE_RECORDING
    }

    private fun startRecording() {
        recorder = MediaRecorder()
            .apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
//                setOutputFile(recordingFilePath) // 따로 저장하지 않고 cache 에 저장.
                setOutputFile(recordingFilePath2)
                prepare()
            }
        recordFile = File(recordingFilePath2)
        recorder?.start()
        soundVisualizerView.startVisualizing(false)
        recordTimeTextView.startCountUp()
        state = State.ON_RECORDING
    }

    private fun stopRecording() {
        recorder?.run {
            stop()
            release() //메모리 해제
        }
        recorder = null
        soundVisualizerView.stopVisualizing()
        recordTimeTextView.stopCountUp()
        state = State.AFTER_RECORDING
    }

    private fun startPlaying() {
        player = MediaPlayer().apply {
//            setDataSource(recordingFilePath) // cache에 저장된 녹음된 파일 읽어오기
            setDataSource(recordingFilePath2)
            prepare()
        }
        player?.setOnCompletionListener {
            stopPlaying()
            state = State.AFTER_RECORDING
        }

        player?.start()
        soundVisualizerView.startVisualizing(true)
        recordTimeTextView.startCountUp()
        state = State.ON_PLAYING
    }

    private fun stopPlaying() {
        player?.release()
        player = null
        soundVisualizerView.stopVisualizing()
        recordTimeTextView.stopCountUp()
        state = State.AFTER_RECORDING
    }

    private fun showPermissionExplanationDialog() {
        AlertDialog.Builder(this)
            .setMessage("녹음 권한을 켜주셔야지 앱을 정상적으로 사용할 수 있습니다. 앱 설정 화면으로 진입하셔서 권한을 켜주세요.")
            .setPositiveButton("권한 변경하러 가기") { _, _ -> navigateToAppSetting() }
            .setNegativeButton("앱 종료하기") { _, _ -> finish() }
            .show()
    }

    private fun navigateToAppSetting() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri: Uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent)
    }

    private fun gradeDialog(msg: String) {
        AlertDialog.Builder(this)
            .setMessage(msg)
            .setPositiveButton("다음 문제로 넘어가기") { _, _ -> nextProblem() }
            .setNegativeButton("다시 시도하기") { _, _ -> finish() }
            .show()
    }

    private fun toastMsg(msg : String){
        Toast.makeText(
            this,
            msg,
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun nextProblem() {}

    override fun onStart() {
        super.onStart()

    }

    companion object {
        private const val REQUEST_RECORD_AUDIO_PERMISSION = 201
    }
}