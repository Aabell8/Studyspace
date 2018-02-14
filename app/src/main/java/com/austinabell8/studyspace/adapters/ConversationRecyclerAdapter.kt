package com.austinabell8.studyspace.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.austinabell8.studyspace.R
import com.austinabell8.studyspace.model.Conversation
import com.austinabell8.studyspace.utils.SearchListListener

/**
 * Created by austi on 2018-02-10.
 */
class ConversationRecyclerAdapter(context: Context, results: List<Conversation>,
                            private val itemListener: SearchListListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val mContext : Context = context
    private val mResults : List<Conversation> = results

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val mView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_conversation, parent, false)
        return SearchViewHolder(mView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val conversation = mResults[position]
        val rHolder = holder as SearchViewHolder

        //Update data in SearchViewHolder
        rHolder.name.text = conversation.from
        rHolder.preview.text = conversation.preview
        rHolder.regularLayout.setOnClickListener { v -> itemListener.recyclerViewListClicked(v, rHolder.layoutPosition) }

//        if (r.photoUrl!=null){
////            fun <T> RequestBuilder<T>.withPlaceholder() = apply(RequestOptions().placeholder(R.drawable.placeholder))
//            Glide.with(mContext).load(r.photoUrl).into(rHolder.profilePic)
//        }
    }

    override fun getItemCount(): Int {
        return mResults.size
    }

    private inner class SearchViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {

        internal var regularLayout: RelativeLayout = view.findViewById(R.id.widget_conversation_list_item)
        internal var profilePic: ImageView = view.findViewById(R.id.image_profile_pic)
        internal var name: TextView = view.findViewById(R.id.text_from)
        internal var preview: TextView = view.findViewById(R.id.text_preview)
    }

}