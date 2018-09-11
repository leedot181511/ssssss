package friendroid.bustracking.fragments

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import friendroid.bustracking.R
import friendroid.bustracking.activities.BaseActivity
import friendroid.bustracking.activities.HomeActivity
import friendroid.bustracking.activities.TeacherActivity
import friendroid.bustracking.activities.TransportControllerActivity
import friendroid.bustracking.adapters.SelectableBusAdapter
import friendroid.bustracking.entities.Bus
import kotlinx.android.synthetic.main.fragment_select_buses.*

class SelectBusesFragment : Fragment() {
    private lateinit var mAdapter: SelectableBusAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_select_buses, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val buses = ArrayList<Bus>()
        for (i in 1..10)
            Bus().also { b ->
                b.uid = "uid_$i"
                b.name = "Bus Name $i"
                b.identity = "email_$i@gmail.com"
                buses.add(b)
            }
        mAdapter = SelectableBusAdapter(buses)
        progressBar?.visibility = View.VISIBLE
        next_button?.isEnabled = false
        (activity as BaseActivity).delayed {
            view.findViewById<RecyclerView>(R.id.checkboxes)?.apply {
                adapter = mAdapter
                layoutManager = LinearLayoutManager(context)
            }
            progressBar?.visibility = View.INVISIBLE
            next_button?.isEnabled = true
        }

        view.findViewById<Button>(R.id.next_button)?.setOnClickListener {
            it.isEnabled = false
            progressBar.visibility = View.VISIBLE
            (activity as BaseActivity).delayed {
                if (activity is TeacherActivity) {
                    (activity as HomeActivity).showOnlineBuses()
                } else {
                    val options = arrayOf("Transport Controller", "Teacher")
                    activity?.apply {
                        AlertDialog.Builder(activity!!).setTitle("Select Role").setItems(options) { _, which ->
                            when (which) {
                                0 -> startActivity(Intent(activity, TransportControllerActivity::class.java))
                                1 -> startActivity(Intent(activity, TeacherActivity::class.java))
                            }
                            activity?.finish()
                        }.create().show()
                    }
                }
                progressBar?.visibility = View.INVISIBLE
                it.isEnabled = true
            }
        }
        checkItemCount()

    }
    override fun onResume() {
        super.onResume()
        activity?.title = getString(R.string.subscribed_buses)
    }

    private fun checkItemCount() {
        if (mAdapter.itemCount == 0) {
            view?.findViewById<RecyclerView>(R.id.checkboxes)?.visibility = View.GONE
            view?.findViewById<TextView>(R.id.emptyView)?.visibility = View.VISIBLE
        } else {
            view?.findViewById<TextView>(R.id.emptyView)?.visibility = View.GONE
            view?.findViewById<RecyclerView>(R.id.checkboxes)?.visibility = View.VISIBLE
        }
    }
}