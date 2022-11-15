package rajvi.projects.myapplication

import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.BackgroundColorSpan
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    var sourceString:String=""
    var targetString:String=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
            btnSearch.setOnClickListener {
            btnSearch.isClickable=false
            sourceString=etSearch.text.toString()
            targetString=etTarget.text.toString()
            tvLPSFakeResultString.text=""
            tvLPSResultString.text=""
            tvResultString.text=""
            horizontalScrollView.visibility = View.VISIBLE
            hsv.visibility = View.VISIBLE
            textView4.visibility = View.VISIBLE
            textView5.visibility = View.VISIBLE
                if (TextUtils. isEmpty(sourceString) || TextUtils. isEmpty(targetString))
                {
                    Toast. makeText( this,"Empty field not allowed!", Toast. LENGTH_SHORT). show();
                    horizontalScrollView.visibility = View.INVISIBLE
                    hsv.visibility = View.INVISIBLE
                    textView4.visibility = View.INVISIBLE
                    textView5.visibility = View.INVISIBLE
                }
                else KMPSearch(targetString, sourceString)
        }
        btnClear.setOnClickListener{
            tvLPSFakeResultString.text=""
            tvLPSResultString.text=""
            tvResultString.text=""
            textView5.text=""
            textView4.text=""
            etSearch.setText(" ")
            etTarget.setText(" ")
            resultIndex.text=""
            horizontalScrollView.visibility = View.INVISIBLE
            hsv.visibility = View.INVISIBLE
            textView4.visibility = View.INVISIBLE
            textView5.visibility = View.INVISIBLE
        }
    }
    fun KMPSearch(pat: String, txt: String) {
        GlobalScope.launch(Dispatchers.Main){
            val str = SpannableString(sourceString)
            val strLPS=SpannableString(targetString)
            var strFakeLPS=""
            val M = pat.length
            val N = txt.length
            // lps will hold longest prefix string
            val lps = IntArray(M)
            var j = 0 // pat index
            var q=0
            var w=0
            // preprocessing the pattern
            computeLPSArray(pat, M, lps)
            var i = 0 // txt index
            var c=-1
            horizontalScrollView.scrollTo(0,0)
            while (i < N) {
                horizontalScrollView.scrollTo(i*15,0)
                strFakeLPS=sourceString.substring(0,i-j)
                tvLPSFakeResultString.text=strFakeLPS
                if (pat[j] == txt[i]) {
                    if(j==0){
                        if(i>0)
                            strFakeLPS=sourceString.substring(0,i)
                        else
                            strFakeLPS=sourceString.substring(0,0)
                    }
                    strFakeLPS=sourceString.substring(0,i-j)
                    str.setSpan(BackgroundColorSpan(Color.GREEN), i-j, i+1, 0)
                    strLPS.setSpan(BackgroundColorSpan(Color.GREEN),0,j+1,0)
                    tvResultString.text = str
                    tvLPSResultString.text=strLPS
                    tvLPSFakeResultString.text=strFakeLPS
                    delay(500)
                    j++
                    i++
                }
                if (j == M) {
                    Toast.makeText( this@MainActivity, "FOUND AT INDEX ${i - j}", Toast.LENGTH_SHORT).show()
                    resultIndex.text = resultIndex.text as String + ( i - j) + ' '
                    if(c+1<i-j)
                        str.setSpan(BackgroundColorSpan(Color.WHITE), c+1, i-j, 0)
                    str.setSpan(BackgroundColorSpan(Color.YELLOW), i - j, i - j + targetString.length, 0)
                    q=i-j
                    w=i - j + targetString.length
                    strLPS.setSpan(BackgroundColorSpan(Color.YELLOW), 0, targetString.length, 0)
                    tvResultString.text = str
                    tvLPSResultString.text=strLPS
                    delay(500)
                    strLPS.setSpan(BackgroundColorSpan(Color.WHITE), 0, targetString.length, 0)
                    c=i-j+targetString.length-1;
                    j = lps[j - 1]
                }
                else if (i < N && pat[j] != txt[i]) {

                    strLPS.setSpan(BackgroundColorSpan(Color.WHITE), 0, targetString.length, 0)
                    // Do not match lps[0..lps[j-1]] characters,
                    // they will match anyway
                    if (j != 0) {
                        str.setSpan(BackgroundColorSpan(Color.WHITE), c+1, i, 0)
                        str.setSpan(BackgroundColorSpan(Color.RED), i, i+1, 0)
                        strLPS.setSpan(BackgroundColorSpan(Color.RED), j, j+1, 0)
                        tvResultString.text = str
                        tvLPSResultString.text=strLPS
                        delay(500)
                        strLPS.setSpan(BackgroundColorSpan(Color.WHITE), j, j+1, 0)
                        str.setSpan(BackgroundColorSpan(Color.WHITE), i, i+1, 0)
                        j = lps[j - 1]
                        strFakeLPS=sourceString.substring(0,i-1)
                    }
                    else {
                        str.setSpan(BackgroundColorSpan(Color.RED), i, i+1, 0)
                        strLPS.setSpan(BackgroundColorSpan(Color.RED), j, j+1, 0)
                        tvResultString.text = str
                        tvLPSResultString.text=strLPS
                        delay(500)
                        strLPS.setSpan(BackgroundColorSpan(Color.WHITE), j, j+1, 0)
                        str.setSpan(BackgroundColorSpan(Color.WHITE), i, i+1, 0)
                        i = i + 1
                        strFakeLPS=sourceString.substring(0,i-1)
                    }
                }
                tvResultString.text = str
                tvLPSResultString.text=strLPS
                tvLPSFakeResultString.text=strFakeLPS

            }
            str.setSpan(BackgroundColorSpan(Color.WHITE), c+1, N, 0)
            str.setSpan(BackgroundColorSpan(Color.YELLOW), q,w, 0)
            strLPS.setSpan(BackgroundColorSpan(Color.WHITE), 0, targetString.length, 0)
            tvResultString.text=str
            tvLPSResultString.text=strLPS
            btnSearch.isClickable=true
        }
    }

    fun computeLPSArray(pat: String, M: Int, lps: IntArray) {
        var len = 0 // previous lprefix string length
        var i = 1
        lps[0] = 0 // lps[0] is always 0

        while (i < M) {
            if (pat[i] == pat[len]) {
                len++
                lps[i] = len
                i++
            } else  // (pat[i] != pat[len])
            {
                if (len != 0) {
                    len = lps[len - 1]
                }

                else  // if (len == 0)
                {
                    lps[i] = len
                    i++
                }
            }
        }
    }
}