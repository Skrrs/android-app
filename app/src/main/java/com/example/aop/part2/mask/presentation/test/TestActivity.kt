package com.example.aop.part2.mask.presentation.test

import android.Manifest
import android.annotation.SuppressLint
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
import com.example.aop.part2.mask.domain.dto.GradeDto
import com.example.aop.part2.mask.domain.request.API
import com.example.aop.part2.mask.domain.response.CommonResponse
import com.example.aop.part2.mask.domain.response.GradeEnum
import com.example.aop.part2.mask.domain.response.result.GradeResult
import com.example.aop.part2.mask.domain.response.result.TestResult
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
                (value == State.AFTER_RECORDING) || (value == State.ON_PLAYING) || (value == State.AFTER_PLAYING)
            recordButton.updateIconWithState(value)
        }
    private var mediaPlayer: MediaPlayer? = null

    // TODO - Get level from MainActivity

//    private val level: String = "beginner"
    private var email: String = ""
    private var token: String = ""
    private var level: Int = 0

    // TODO - Get data from Server
    private var i: Int = 0
    private var index: ArrayList<Int>? = arrayListOf()
    private var sentence: ArrayList<String>? = arrayListOf()
    private var voiceUrl: ArrayList<String>? = arrayListOf()
    private var pron: ArrayList<String>? = arrayListOf()
    private var english: ArrayList<String>? = arrayListOf()

    private var currentIndex: Int = 0
    private var currentVoice: String? = null
    private var problem: ArrayList<Int>? = arrayListOf()
    private var correct: ArrayList<Int>? = arrayListOf()
    private var wrong: ArrayList<Int>? = arrayListOf()
    private var isSolved : Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        rtf = RetrofitClass().getRetrofitInstance()
        requestAudioPermission()
        initVariables()
        initViews()
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

    @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n")
    private fun bindViews() {
        currentIndex = index?.get(i)!!
        currentVoice = voiceUrl?.get(i)

        val logo = findViewById<AppCompatButton>(R.id.header_logo)
        logo.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java) //
            startActivity(intent)
            finish()
        }
        // TODO - API request 추가 ?

        soundVisualizerView.onRequestCurrentAmplitude = {
            recorder?.maxAmplitude ?: 0
        }
        // ###################################################################
        var starActivation = false
        val btnStar = findViewById<AppCompatButton>(R.id.btnStar)
        btnStar.setOnClickListener {
            starActivation = !starActivation
            if(starActivation) {
                btnStar.background = this.resources.getDrawable(R.drawable.ic_star)
                if (!problem?.contains(currentIndex)!!){
                    problem!!.add(currentIndex)
                }
            }else{
                btnStar.background = this.resources.getDrawable(R.drawable.ic_emptystar)
                if (problem?.contains(currentIndex)!!){
                    problem!!.remove(currentIndex)
                }
            }
        }
        val rightButton = findViewById<AppCompatButton>(R.id.btnRight)
        val text: TextView = findViewById(R.id.textView)
        val pronText: TextView = findViewById(R.id.pronView)
        val englishText: TextView = findViewById(R.id.englishView)
        val pageNum: TextView = findViewById(R.id.pageView)
        var replayActivation = false
        val btnReplay = findViewById<AppCompatButton>(R.id.btnReplay)

        // TODO - voiceUrl 출력
//        try {
//            mp!!.setDataSource(currentVoice)
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }
//        mp!!.prepareAsync()

        mediaPlayer = MediaPlayer()
        mediaPlayer?.apply {
            setDataSource(currentVoice)
            prepare()
        }
        btnReplay.setOnClickListener {
            replayActivation = true
            btnReplay.background = this.resources.getDrawable(R.drawable.ic_replay)
            if(!mediaPlayer!!.isPlaying){
                mediaPlayer!!.start()
            }
        }
        text.text = "${i+1}. ${sentence?.get(i)}"
        pronText.text = pron?.get(i)
        englishText.text = english?.get(i)
        pageNum.text = "${i+1} / 5"

//        toastMsg("문제를 불러왔습니다!")
        rightButton.setOnClickListener {
            soundVisualizerView.clearVisualization()
            recordTimeTextView.clearCountTime()
            mediaPlayer!!.release()
            mediaPlayer = null
            if (!isSolved){
                wrong?.add(currentIndex)
                problem?.add(currentIndex)
            }
            isSolved = false
            i++
            if (i < 5) {
                starActivation = false
                btnStar.background = this.resources.getDrawable(R.drawable.ic_emptystar)
                replayActivation = false
                btnReplay.background = this.resources.getDrawable(R.drawable.ic_play)
                toastMsg("다음 문제")
                currentIndex = index!!.get(i)
                currentVoice = voiceUrl?.get(i)
                text.text = sentence?.get(i)
                pronText.text = pron?.get(i)
                englishText.text = english?.get(i)
                pageNum.text = "${i+1} / 5"
                mediaPlayer = MediaPlayer()
                mediaPlayer?.apply {
                    setDataSource(currentVoice)
                    prepare()
                }
            } else {
                toastMsg("학습이 끝났습니다.")
                val intent = Intent(this, ResultActivity::class.java) //
                intent.putExtra("token", token )
                intent.putExtra("email", email)
                intent.putExtra("problem", problem)
                intent.putExtra("correct", correct)
                intent.putExtra("wrong", wrong)
                intent.putExtra("index", index)
                intent.putExtra("sentence", sentence)
                intent.putExtra("level", level)
                startActivity(intent)
                finish()
            }
        }
        // ###################################################################

        confirmButton.setOnClickListener {
            stopPlaying()
            state = State.BEFORE_RECORDING
            isSolved = true
            if (recordFile != null) {
                val requestFile = RequestBody.create(MediaType.parse("audio/wav"), recordFile)
                val record = MultipartBody.Part.createFormData("file", recordFile!!.name, requestFile)
                callGradeProblem(token, record, currentIndex, sentence!![i])
            } else {
                Log.e("recordFile", "Error record file is null")
            }
        }

        recordButton.setOnClickListener {
            when (state) {
                State.BEFORE_RECORDING -> {
                    soundVisualizerView.clearVisualization()
                    recordTimeTextView.clearCountTime()
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
                State.AFTER_PLAYING -> {
                    stopRecording()
                }
            }
        }
    }

    // Get TestProblems (API)
    private fun callProblems() {
//        toastMsg("문제를 불러오고 있습니다 ...")
        val api = rtf?.create(API::class.java)
        //TODO - callAPI
        val callAPI = api?.requestTest(email = email, level = level, token = token)
        callAPI?.enqueue(object : retrofit2.Callback<CommonResponse<TestResult>> {
            override fun onResponse(call: Call<CommonResponse<TestResult>>, response: Response<CommonResponse<TestResult>>) {
                if (response.isSuccessful) {
                    Log.d("Get Test Problems Success", response.code().toString())

                    // assign : response data from Server
                    index = response.body()?.result?.index
                    sentence = response.body()?.result?.sentence
                    voiceUrl = response.body()?.result?.voiceUrl
                    pron = response.body()?.result?.pron
                    english = response.body()?.result?.english
//                    val msg = response.body()?.message
                    bindViews()
                }else{
                    toastMsg(response.toString())
                    Log.d("Get Test Problems : Code 400 Error", response.toString())
                }
            }
            override fun onFailure(call: Call<CommonResponse<TestResult>>, t: Throwable) {
                Log.d("Get Test Problems : Code 500 Error", t.toString())
            }
        })
    }

    // POST Problem to get Grade (API)
    private fun callGradeProblem(token: String, record: MultipartBody.Part, currentIndex: Int, answer: String) {
        val api = rtf?.create(API::class.java)
        val gradeDto = GradeDto(answer)

        var result: Int = 1
        // assignResultDialog(result)
        val callAPI = api?.requestGrade(token = token, record = record, answer = answer)
        callAPI?.enqueue(object : retrofit2.Callback<CommonResponse<GradeResult>> {
            override fun onResponse(call: Call<CommonResponse<GradeResult>>, response: Response<CommonResponse<GradeResult>>) {
                if (response.isSuccessful) {
                    Log.d("GradeProblem Success", response.code().toString())

                    toastMsg("채점중입니다 ...")

                    // TODO - 주석해제 (result 값 받아오기)
                    result = response.body()?.result?.index!!
                    when(result){
                        1 -> {
                            // "GREAT !!"
                            if (wrong?.contains(currentIndex)!!){
                                wrong!!.remove(currentIndex)
                            }
                            if (problem?.contains(currentIndex)!!){
                                problem!!.remove(currentIndex)
                            }
                            correct?.add(currentIndex)
                            gradeDialog("GREAT ^^")
                        }
                        2 -> {
                            // "GOOD :)"
                            if (wrong?.contains(currentIndex)!!){
                                wrong!!.remove(currentIndex)
                            }
                            if (problem?.contains(currentIndex)!!){
                                problem!!.remove(currentIndex)
                            }
                            correct?.add(currentIndex)
                            gradeDialog("GOOD :)")
                        }
                        3 -> {
                            // "BAD :("
                            wrong?.add(currentIndex)
                            problem?.add(currentIndex)
                            gradeDialog("BAD :(")
                        }
                    }
                } else{
                    toastMsg(response.toString())
                    Log.d("GradeProblem : Code 400 Error", response.toString())
                }
            }
            override fun onFailure(call: Call<CommonResponse<GradeResult>>, t: Throwable) {
                Log.d("GradeProblem : Code 500 Error", t.toString())
            }
        })
    }

    private fun initVariables() {
        state = State.BEFORE_RECORDING
        // assignProblems()
        // token 값과 email 값 읽어오기
        if (intent.hasExtra("token")) {
            token = intent.getStringExtra("token").toString()
        }
        if(intent.hasExtra("email")) {
            email = intent.getStringExtra("email").toString()
        }
//        databaseReference.child("token").get().addOnSuccessListener {
//            Log.i("firebase", "Got value ${it.value}")
//            token = it.value as String
//        }.addOnFailureListener {
//            Log.e("firebase", "Error getting data", it)
//        }
//        databaseReference.child("email").get().addOnSuccessListener {
//            Log.i("firebase: email", "Got value ${it.value}")
//            email = it.value as String
//        }.addOnFailureListener {
//            Log.e("firebase: email", "Error getting data", it)
//        }
        if (intent.hasExtra("level")) {
            level = intent.getIntExtra("level", 0)
        }
        callProblems()
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
            state = State.AFTER_PLAYING
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
        state = State.AFTER_PLAYING
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
        AlertDialog.Builder(this)
            .setTitle("발음 채점 결과")
            .setMessage(msg)
            .setNegativeButton("확인") { _, _ ->  }
            .show()
    }

    // TODO - 음성 출력
    private fun voiceOutput() {

    }

    private fun toastMsg(msg: String) {
        Toast.makeText(
            this,
            msg,
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun assignProblems() {
        index = arrayListOf(2, 1, 5, 4, 3, 10, 7, 8, 9, 6)
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
    }

    private fun assignResultDialog(result :Int){
        when (result) {
            1 -> {
                // "GREAT !!"
                gradeDialog("GREAT ^^")
            }
            2 -> {
                // "GOOD :)"
                gradeDialog("GOOD :)")
            }
            3 -> {
                // "BAD :("
                gradeDialog("BAD :(")
            }
        }
    }

    companion object {
        private const val REQUEST_RECORD_AUDIO_PERMISSION = 201
    }
}