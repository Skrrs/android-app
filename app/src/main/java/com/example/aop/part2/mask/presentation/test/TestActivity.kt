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
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.example.aop.part2.mask.R
import com.example.aop.part2.mask.domain.request.API
import com.example.aop.part2.mask.domain.response.GradeEnum
import com.example.aop.part2.mask.presentation.main.MainActivity
import com.example.aop.part2.mask.presentation.result.ResultActivity
import com.example.aop.part2.mask.utils.api.RetrofitClass
import com.example.aop.part2.mask.utils.record.CountUpView
import com.example.aop.part2.mask.utils.record.RecordButton
import com.example.aop.part2.mask.utils.record.SoundVisualizerView
import com.example.aop.part2.mask.utils.record.State
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
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
    private val recordingFilePath2: String by lazy {
        "${Environment.getExternalStorageDirectory().absolutePath}/Download/${Date().time}recording.wav"
    }

    // DB
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val databaseReference = FirebaseDatabase.getInstance().reference

    // Retrofit (API)
    private var rtf: Retrofit? = null

    // record & play
    private var recordFile: File? = null
    private var recorder: MediaRecorder? = null
    private var player: MediaPlayer? = null
    private var state = State.BEFORE_RECORDING
        set(value) {
            field = value
            confirmButton.isEnabled =
                (value == State.AFTER_RECORDING) || (value == State.ON_PLAYING)
            recordButton.updateIconWithState(value)
        }

    // TODO - Get level from MainActivity
//    private val level: String? = intent.getStringExtra("level")
    private val level: String = "beginner"
    private var email: String = ""
    private var token: String = ""

    // TODO - Get data from Server
    private var i: Int = 0
    private var index: ArrayList<Int>? = arrayListOf()
    private var sentence: ArrayList<String>? = arrayListOf()
    private var voiceUrl: ArrayList<String>? = arrayListOf()
    private var pron: ArrayList<String>? = arrayListOf()
    private var english: ArrayList<String>? = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        rtf = RetrofitClass().getRetrofitInstance()
        callProblems()
        requestAudioPermission()
        initViews()
        bindViews()
        initVariavbles()
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
        val logo = findViewById<AppCompatButton>(R.id.header_logo)
        logo.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java) //
            startActivity(intent)
            finish()
        }
        // TODO - API request 추가 ?
        val btnStar = findViewById<AppCompatButton>(R.id.btnStar)
        btnStar.setOnClickListener {
            callAddFavorite()
        }
        // val btnReplay = findViewById<AppCompatButton>(R.id.btnReplay)

        soundVisualizerView.onRequestCurrentAmplitude = {
            recorder?.maxAmplitude ?: 0
        }
        // ###################################################################
        val rightButton = findViewById<AppCompatButton>(R.id.btnRight)
        val text: TextView = findViewById(R.id.textView)
        val pronText: TextView = findViewById(R.id.pronView)
        val englishText: TextView = findViewById(R.id.englishView)
        val pageNum: TextView = findViewById(R.id.pageView)

        text.setText(sentence?.get(i))
        pronText.setText(pron?.get(i))
        englishText.setText(english?.get(i))
        pageNum.setText("${i+1} / 10")
        rightButton.setOnClickListener {
            i++
            if (i < 10) {
                toastMsg("다음 문제")
                text.setText(sentence?.get(i))
                pronText.setText(pron?.get(i))
                englishText.setText(english?.get(i))
                pageNum.setText("${i + 1} / 10")
            } else {
                toastMsg("학습이 끝났습니다.")
                val intent = Intent(this, ResultActivity::class.java) //
                startActivity(intent)
                finish()
            }
        }
        // ###################################################################

        confirmButton.setOnClickListener {
            stopPlaying()
            soundVisualizerView.clearVisualization()
            recordTimeTextView.clearCountTime()
            state = State.BEFORE_RECORDING
            //
            if (recordFile != null) {
                val requestFile = RequestBody.create(MediaType.parse("audio/wav"), recordFile)
                val record =
                    MultipartBody.Part.createFormData("file", recordFile!!.name, requestFile)
                databaseReference.child("token").get().addOnSuccessListener {
                    Log.i("firebase", "Got value ${it.value}")
                    callGradeProblem(it.value as String, record)
                }.addOnFailureListener {
                    Log.e("firebase", "Error getting data", it)
                }
            } else {
                Log.e("recordFile", "Error record file is null")
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

    // Get TestProblems (API)
    private fun callProblems() {
        val api = rtf?.create(API::class.java)
        val strToken = "Bearer ${token}"
        if (level != null) {
            Log.d("#### DEBUG : ", level)
        }

        // ######
        index = arrayListOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
        sentence = arrayListOf(
            "지금 몇 시예요?",
            "세시 삼십 분이에요.",
            "벌써 그렇게 됐어요?",
            "서둘러야겠어요.",
            "왜 그러세요?",
            "네시에 친구와 약속이 있어요.",
            "어디서 만나기로 하셨는데요?",
            "여의도 케이비에스 본관 앞에서 만나기로 했어요.",
            "지금 출발하면 늦지 않으실 거예요.",
            "먼저 가서 미안해요. 그럼 내일 또 만나요."
        )
        pron = arrayListOf(
            "jigeum myeotssiyeyo?",
            "Sesi samsippunieyo.",
            "Beolsseo geureoke dwaesseoyo?",
            "Seodulleoyagesseoyo.",
            "Wae geureoseyo?",
            "Nesi-e chin-guwa yakssogi isseoyo.",
            "Eodiseo managiro hasyeonneundeyo?",
            "Yeo-uido KBS bon-gwan apeseo managiro haesseoyo.",
            "Jigeum chulbalhamyeon neujji aneusil kkeoyeyo.",
            "Meonjeo gaseo mianhaeyo. Geureum nae-il tto manayo."
        )
        english = arrayListOf(
            "What time is it right now?",
            "It is half past three/It is three thirty.",
            "Already?",
            "I'd better hurry.",
            "How come?",
            "I have an appointment at four with a friend.",
            "Where are you meeting him/her?",
            "In front of the main KBS building.",
            "You won't be late if you leave now",
            "Sorry for leaving you. I'll see you tomorrow."
        )
        // ######

        //TODO - callAPI
//        val callAPI = api?.requestTest(email = email, level = level, token = strToken)
//        callAPI?.enqueue(object : retrofit2.Callback<CommonResponse<TestResult>> {
//            override fun onResponse(call: Call<CommonResponse<TestResult>>, response: Response<CommonResponse<TestResult>>) {
//                if (response.isSuccessful) {
//                    Log.d("Get Test Problems Success", response.code().toString())
//                    toastMsg("문제를 불러오고 있습니다 ...")
//
//                    // assign : response data from Server
//                    index = response.body()?.result?.index
//                    sentence = response.body()?.result?.sentence
//                    voiceUrl = response.body()?.result?.voiceUrl
//                    pron = response.body()?.result?.pron
//                    english = response.body()?.result?.english
////                    val msg = response.body()?.message
//                    getSentenceScreen(rbtn)
//
//                }else{
//                    Log.d("Get Test Problems : Code 400 Error", response.toString())
//                }
//            }
//            override fun onFailure(call: Call<CommonResponse<TestResult>>, t: Throwable) {
//                Log.d("Get Test Problems : Code 500 Error", t.toString())
//            }
//        })
    }

    // POST Problem to get Grade (API)
    private fun callGradeProblem(token: String, body: MultipartBody.Part) {
        val api = rtf?.create(API::class.java)
        val strToken = "Bearer ${token}"
        // val dto = RecordDto(body)

        var result: GradeEnum = GradeEnum.GREAT
        when (result) {
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

        // TODO - callAPI
//        val callAPI = api?.requestGrade(token = strToken, body)
//        callAPI?.enqueue(object : retrofit2.Callback<CommonResponse<GradeResult>> {
//            override fun onResponse(call: Call<CommonResponse<GradeResult>>, response: Response<CommonResponse<GradeResult>>) {
//                if (response.isSuccessful) {
//                    Log.d("GradeProblem Success", response.code().toString())
//
//                    toastMsg("채점중입니다 ...")
//
//                    // TODO - 주석해제 (result 값 받아오기)
//                    result = response.body()?.result?.index!!
//                    when(result){
//                        GradeEnum.GREAT -> {
//                            // "GREAT !!"
//                            gradeDialog("GREAT ^^")
//                        }
//                        GradeEnum.GOOD -> {
//                            // "GOOD :)"
//                            gradeDialog("GOOD :)")
//                        }
//                        GradeEnum.BAD -> {
//                            // "BAD :("
//                            gradeDialog("BAD :(")
//                        }
//                    }
//                } else{
//                    Log.d("GradeProblem : Code 400 Error", response.toString())
//                }
//            }
//            override fun onFailure(call: Call<CommonResponse<GradeResult>>, t: Throwable) {
//                Log.d("GradeProblem : Code 500 Error", t.toString())
//            }
//        })
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

    // TODO - dialog 꾸미기
    private fun gradeDialog(msg: String) {
//        var linear = LinearLayout(this)
//        var window = layoutInflater.inflate(R.layout.layout_dialog, linear, false)
//        val myLayout = layoutInflater.inflate(R.layout.layout_dialog, null)
//        val build = AlertDialog.Builder(view.context).apply{
//            setView(myLayout)
//        }
//        val dialog = build.create()
//        dialog.show()
//
//        myLayout.
        AlertDialog.Builder(this)
            .setTitle("발음 채점 결과")
            .setMessage(msg)
//            .setPositiveButton("추가하기") { _, _ -> callAddFavorite() }
            .setNegativeButton("확인") { _, _ ->  }
            .show()
    }


    private fun toastMsg(msg: String) {
        Toast.makeText(
            this,
            msg,
            Toast.LENGTH_SHORT
        ).show()
    }

    // TODO - 즐겨찾기 추가 기능
    private fun callAddFavorite() {
        // TODO -
    }

    override fun onStart() {
        super.onStart()
        // token 값과 email 값 읽어오기
        databaseReference.child("token").get().addOnSuccessListener {
            Log.i("firebase", "Got value ${it.value}")
            token = it.value as String
        }.addOnFailureListener {
            Log.e("firebase", "Error getting data", it)
        }
        databaseReference.child("email").get().addOnSuccessListener {
            Log.i("firebase: email", "Got value ${it.value}")
            email = it.value as String
        }.addOnFailureListener {
            Log.e("firebase: email", "Error getting data", it)
        }
    }

    companion object {
        private const val REQUEST_RECORD_AUDIO_PERMISSION = 201
    }
}