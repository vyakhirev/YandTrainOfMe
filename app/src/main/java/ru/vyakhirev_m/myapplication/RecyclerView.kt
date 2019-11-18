package ru.vyakhirev_m.myapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.single_train_item.view.*

interface IBaseListItem {
    @LayoutRes
    fun getLayoutId(): Int
}

abstract class SimpleListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    protected val items: ArrayList<IBaseListItem> = ArrayList()
    override fun getItemCount() = items.size
    override fun getItemViewType(position: Int) = items[position].getLayoutId()

    protected fun inflateByViewType(context: Context?, viewType: Int, parent: ViewGroup) =
        LayoutInflater.from(context).inflate(viewType, parent, false)

    fun add(newItem: IBaseListItem) {
        items.add(newItem)
        notifyDataSetChanged()
    }

//    fun add(newItems: List<IBaseListItem>) {
//            items.addAll(newItems)
//            notifyDataSetChanged()
//    }

//    fun addAtPosition(pos: Int, newItem: IBaseListItem) {
//        items.add(pos, newItem)
//        notifyDataSetChanged()
//    }

    fun clearAll() {
        items.clear()
        notifyDataSetChanged()
    }

//    fun remove(position: Int) {
//        items.removeAt(position)
//        notifyDataSetChanged()
//    }
}

class TrainItem(
    val departure: String?,
    val arrival: String?,
    val transportTitle: String?,
    val threadTitle: String?,
    val expressType: String?
) :
    IBaseListItem {
    override fun getLayoutId(): Int {
        return R.layout.single_train_item
    }

}

class TrainViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val departure = view.departure
    val arrival = view.arrival
    val transport_title = view.transport_title
    val thread_title = view.thread_title
    val express_type = view.express_type
}


class TrainsListAdapter : SimpleListAdapter() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val context = parent.context

        return when (viewType) {
            R.layout.single_train_item -> TrainViewHolder(
                inflateByViewType(
                    context,
                    viewType,
                    parent
                )
            )
            else -> throw IllegalStateException("There is no match with current layoutId")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (holder) {

            is TrainViewHolder -> {
                val trainItem = items[position] as TrainItem
                holder.arrival.text = trainItem.arrival
                holder.departure.text = trainItem.departure
                if (trainItem.transportTitle.equals("Стандарт плюс", true)) {
                    holder.transport_title.text = trainItem.transportTitle
                    holder.transport_title.visibility = View.VISIBLE
                }
                holder.thread_title.text = trainItem.threadTitle
                if (trainItem.expressType != null) {
                    holder.express_type.text = trainItem.expressType
                    holder.express_type.visibility = View.VISIBLE
                }
            }

            else -> throw IllegalStateException("There is no match with current holder instance")
        }
    }
}

