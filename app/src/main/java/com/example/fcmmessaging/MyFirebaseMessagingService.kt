package com.example.fcmmessaging

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        Log.d(TAG, "From: ${remoteMessage.from}")


        remoteMessage.data.isNotEmpty().let {
            Log.d(TAG, "Message data payload: " + remoteMessage.data)

        }

        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
        }
    }

    private fun updateNotificationCount() {

        val database = FirebaseDatabase.getInstance()
        val countReference = database.getReference("notificationCount")

        GlobalScope.launch(Dispatchers.Main) {
            try {
                val snapshot = withContext(Dispatchers.IO) {
                    countReference.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            // Update the count when a message is received
                            var count = dataSnapshot.getValue(Int::class.java) ?: 0
                            count++
                            countReference.setValue(count)
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.e(TAG, "Error updating count", error.toException())
                        }
                    })
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception updating count", e)
            }
        }
    }


    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")
    }

    companion object {
        private const val TAG = "MyFirebaseMsgService"
    }
}


