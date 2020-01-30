package com.amitozsingh.chatapp.Fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.amitozsingh.chatapp.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_chatting.*

import com.amitozsingh.chatapp.utils.USER_EMAIL




/**
 * A simple [Fragment] subclass.
 */


import kotlinx.android.synthetic.main.fragment_chatting.*

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import android.content.Context.INPUT_METHOD_SERVICE
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amitozsingh.chatapp.Activities.ChattingActivity
import com.amitozsingh.chatapp.Activities.MessagesActivity
import com.amitozsingh.chatapp.MessagesAdapter

import com.amitozsingh.chatapp.Services.FriendServices
import com.amitozsingh.chatapp.UserFriendsAdapter
import com.amitozsingh.chatapp.utils.*
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.android.synthetic.main.fragment_friendslist.*





/**
 * A simple [Fragment] subclass.
 */
class ChattingFragment : BaseFragment() {


    val FRIEND_DETAILS_EXTRA = "FRIEND_DETAILS_EXTRA"
    companion object {
        fun newInstance(friendDetails: ArrayList<String>):ChattingFragment {
            val arguments = Bundle()
            arguments.putStringArrayList("FRIEND_DETAILS_EXTRA", friendDetails)
            val chattingFragment = ChattingFragment()
            chattingFragment.setArguments(arguments)

            return chattingFragment
        }
    }

    private var mFriendEmailString: String? = null
    private var mFriendPictureString: String? = null
    private var mFriendNameString: String? = null
    private var mUserEmailString: String? = null

    private var mGetAllMessagesReference: DatabaseReference? = null
    private var mGetAllMessagesListener: ValueEventListener? = null


    var mActivity: MessagesActivity?=null
    private var mAdapter: MessagesAdapter? = null

    private var mSocket: Socket? = null
    private var mLiveFriendsService: FriendServices? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        mSocket= IO.socket(LOCAL_HOST)
        mSocket!!.connect()
        mLiveFriendsService = FriendServices().getInstance();

        val friendDetails = arguments!!.getStringArrayList(FRIEND_DETAILS_EXTRA)
        mFriendEmailString = friendDetails!![0]
        // mFriendPictureString = friendDetails[1]
        mFriendNameString = friendDetails[1]
        mUserEmailString = mSharedPreferences!!.getString(USER_EMAIL, "")

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chatting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        Picasso.get()
//            .load(mFriendPictureString)
//            .into(mFriendPicture);
        sendArrow.setOnClickListener {
            setmSendMessage()
        }

        fragment_messages_friendName.setText(mFriendNameString)

        mAdapter= MessagesAdapter(activity as ChattingActivity, mUserEmailString!!)

        mGetAllMessagesReference = FirebaseDatabase.getInstance().getReference().child(FIRE_BASE_PATH_USER_MESSAGES)
            .child(encodeEmail(mUserEmailString)).child(encodeEmail(mFriendEmailString));


        mGetAllMessagesListener = mLiveFriendsService?.getAllMessages(fragment_messages_recyclerView,fragment_messages_friendName,fragment_messages_friendPicture,mAdapter!!,mUserEmailString!!);

        mGetAllMessagesReference!!.addValueEventListener(mGetAllMessagesListener!!)


        fragment_messages_recyclerView.layoutManager= LinearLayoutManager(context, RecyclerView.VERTICAL,false)
        fragment_messages_recyclerView.setAdapter(mAdapter)

    }


    fun setmSendMessage() {
        if (fragment_messages_messageBox.getText().toString().equals("")) {
            Toast.makeText(activity, "Message Can't Be Blank", Toast.LENGTH_SHORT).show()
        } else {


            val newMessageRefernce = mGetAllMessagesReference?.push()
            val message = Message(
                newMessageRefernce?.key!!,
                fragment_messages_messageBox.getText().toString(),
                mUserEmailString!!,
                mSharedPreferences?.getString(USER_PICTURE, "")!!
            )

            newMessageRefernce.setValue(message)

            mCompositeSubscription!!.add(
                mLiveFriendsService?.sendMessage(
                    mSocket!!,
                    mUserEmailString!!,
                    mSharedPreferences?.getString(USER_PICTURE, "")!!,
                    fragment_messages_messageBox.getText().toString(),
                    mFriendEmailString!!,
                    mSharedPreferences?.getString(USER_NAME, "")!!
                )
            )




        }
    }

    override fun onDestroy() {
        super.onDestroy()
        //FriendslistFragment.newInstance()
    }

    override fun onDestroyView() {
        super.onDestroyView()


        if (mGetAllMessagesListener != null) {
            mGetAllMessagesReference?.removeEventListener(mGetAllMessagesListener!!)
        }


    }


}
