package com.mayorgaerick.cazarpatos

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RankingAdapter(private val dataSet: ArrayList<Jugador>) : RecyclerView.Adapter<RecyclerView.ViewHolder>()  {
    private val TYPE_HEADER : Int = 0
    class ViewHolderHeader(view : View) : RecyclerView.ViewHolder(view){
        val textViewPosicion: TextView = view.findViewById(R.id.textViewPosicion)
        val textViewPatosCazados: TextView = view.findViewById(R.id.textViewPatosCazados)
        val textViewUsuario: TextView = view.findViewById(R.id.textViewUsuario)
    }
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewPosicion: TextView = view.findViewById(R.id.textViewPosicion)
        val textViewPatosCazados: TextView = view.findViewById(R.id.textViewPatosCazados)
        val textViewUsuario: TextView = view.findViewById(R.id.textViewUsuario)
    }
    override fun getItemViewType(position: Int): Int {
        if(position == 0){
            return TYPE_HEADER
        }
        return 1
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if(viewType == TYPE_HEADER){
            val header = LayoutInflater.from(parent.context).inflate(R.layout.ranking_list,parent,false)
            return ViewHolderHeader(header)
        }
        val header = LayoutInflater.from(parent.context).inflate(R.layout.ranking_list,parent,false)
        return ViewHolder(header)
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolderHeader){
            holder.textViewPosicion.text = "#"
            holder.textViewPosicion.paintFlags = holder.textViewPosicion.paintFlags or Paint.UNDERLINE_TEXT_FLAG
            holder.textViewPosicion.setTextColor(holder.textViewPosicion.context.getColor( R.color.colorPrimaryDark))
            holder.textViewPatosCazados.paintFlags = holder.textViewPatosCazados.paintFlags or Paint.UNDERLINE_TEXT_FLAG
            holder.textViewPatosCazados.setTextColor(holder.textViewPatosCazados.context.getColor( R.color.colorPrimaryDark))
            holder.textViewUsuario.paintFlags = holder.textViewUsuario.paintFlags or Paint.UNDERLINE_TEXT_FLAG
            holder.textViewUsuario.setTextColor(holder.textViewUsuario.context.getColor( R.color.colorPrimaryDark))
        }
        else if (holder is ViewHolder) {
            holder.textViewPosicion.text = position.toString()
            holder.textViewPatosCazados.text = dataSet[position-1].patosCazados.toString()
            holder.textViewUsuario.text = dataSet[position-1].usuario
        }
    }
    override fun getItemCount() = dataSet.size + 1
}
