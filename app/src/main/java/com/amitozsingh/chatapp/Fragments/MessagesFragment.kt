package com.amitozsingh.chatapp.Fragments


import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.amitozsingh.chatapp.Activities.BaseActivity
import com.amitozsingh.chatapp.Activities.MessagesActivity

import com.amitozsingh.chatapp.R
import com.amitozsingh.chatapp.Services.AccountServices
import com.google.android.material.bottomnavigation.BottomNavigationView
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.android.synthetic.main.activity_messages.*
import com.google.android.material.badge.BadgeDrawable
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DatabaseReference

import com.amitozsingh.chatapp.Activities.ChattingActivity
import com.amitozsingh.chatapp.ChatroomAdapter
import com.amitozsingh.chatapp.MessagesAdapter
import com.amitozsingh.chatapp.Services.FriendServices
import com.amitozsingh.chatapp.utils.*
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_chatting.*
import kotlinx.android.synthetic.main.fragment_messages.*


/**
 * A simple [Fragment] subclass.
 */
class MessagesFragment : BaseFragment() ,ChatroomAdapter.ChatRoomListener{
    override fun OnChatRoomClicked(chatRoom: ChatRoom) {

    }

    //var mActivity: MessagesActivity? = null

    private var mLiveFriendsService: FriendServices? = null
//
//    private var mAllFriendRequestsReference: DatabaseReference? = null
//    private var mAllFriendRequestsListener: ValueEventListener? = null

    private var mAdapter: ChatroomAdapter? = null


    private var mUserChatRoomReference: DatabaseReference? = null
    private var mUserChatRoomListener: ValueEventListener? = null

    private var mUserEmailString: String? = null

//    private var mUsersNewMessagesReference: DatabaseReference? = null
//    private var mUsersNewMessagesListener: ValueEventListener? = null

    companion object {
        fun newInstance(): MessagesFragment{
            return MessagesFragment()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mLiveFriendsService = FriendServices().getInstance();
        mUserEmailString = mSharedPreferences!!.getString(USER_EMAIL,"");
    }
    //var bott: BottomNavigationView?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_messages, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        bott = mActivity?.findViewById(R.id.bottom_nav_main)
//
//       // bott?.getOrCreateBadge(R.id.itemfriends)?.number = 5
//
//        val badge = bott?.getOrCreateBadge(R.id.itemfriends)
//        badge?.number=5
//
//        badge?.setVisible(true)

        mAdapter= ChatroomAdapter(activity as MessagesActivity,this,mUserEmailString!!)

        mUserChatRoomReference = FirebaseDatabase.getInstance().getReference()
            .child(FIRE_BASE_PATH_USER_CHAT_ROOMS).child(encodeEmail(mUserEmailString));

        mUserChatRoomListener = mLiveFriendsService?.getAllChatRooms(fragment_messages_recyclerView,fragment_inbox_message,mAdapter!!);

        mUserChatRoomReference?.addValueEventListener(mUserChatRoomListener!!)




    }

}
