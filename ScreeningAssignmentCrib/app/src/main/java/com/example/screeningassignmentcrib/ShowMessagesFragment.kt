package com.example.screeningassignmentcrib

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.database.Cursor
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Telephony
import android.text.format.DateFormat
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.fragment_show_messages.*
import java.util.*


class ShowMessagesFragment : Fragment() {

    var REQUEST_PHONE_CALL = 1
    val smsList: MutableList<String> = ArrayList()
    var count1 = 0
    var curdays = 0
    var searchDate = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_show_messages, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (context?.let {
                ContextCompat.checkSelfPermission(
                    it.applicationContext,
                    Manifest.permission.READ_SMS
                )
            } != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                context?.applicationContext as Activity,
                arrayOf(Manifest.permission.READ_SMS),
                REQUEST_PHONE_CALL
            )
        }
        btnSubmit.setOnClickListener {
            curdays = etDays.text.toString().trim().toInt()
            searchDate = getPreviousDate(etDays.text.toString().trim().toInt())

            getDataByPhoneNumber(etNumber.text.toString().trim())



            for (i in 0..curdays) {
                var data = getPreviousDate(curdays - i)
                Log.d("TAG", "onCreate:curdays ${curdays - i}")

                if (isValid(data)) {
                    searchDate = data
                    Log.d("TAG", "onCreate: done ")
                    break
                } else {
                    Log.d("TAG", "onCreate: false ")
                }
            }


            if (count1 > 0) {
                tvCount.text = "${count1} number of messages found"
            } else {
                tvCount.text = "Sorry, no messages found"
            }
            count1 = 0;
        }
    }

    private fun isValid(searchDate: String): Boolean {
        for (i in 0..smsList.size - 1) {
            if (smsList[i] == searchDate) {
                count1 = i;
            } else if (count1 > 0) {
                count1 = i;
                return true
            }
        }
        count1 = 0;
        return false
    }


    private fun getDataByPhoneNumber(numbers: String) {
        val inboxURI: Uri = Uri.parse("content://sms/inbox")

        val cr = context?.contentResolver
        val c: Cursor? = cr?.query(inboxURI, null, Telephony.Sms.ADDRESS, null, null)
        var totalSMS = 0
        if (c != null) {
            totalSMS = c.count
            if (c.moveToFirst()) {
                for (i in 0..totalSMS - 1) {

                    var smsDate = c.getString(c.getColumnIndexOrThrow(Telephony.Sms.DATE));
                    var number = c.getString(c.getColumnIndexOrThrow(Telephony.Sms.ADDRESS));
                    var dateFormat = Date(smsDate.toLong());
                    if (number.contains(numbers)) {
                        val s: CharSequence = DateFormat.format("dd-MMM-yyyy", dateFormat.time)
                        Log.d("TAG", "onCreate: $i" + number)
                        Log.d("TAG", "onCreate: $i" + s)
                        smsList.add(s.toString())
                    }
                    c.moveToNext();
                }
            }
            c.close()
        } else {
            Toast.makeText(context, "No message to show!", Toast.LENGTH_SHORT).show();
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun getPreviousDate(days: Int): String {
        val car = Calendar.getInstance()
        car.add(Calendar.DATE, -days)
        val df = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault())
        val formattedDate: String = df.format(car.time)
        return formattedDate
        Log.d("TAG", "onCreate: time current " + formattedDate)
    }

}


