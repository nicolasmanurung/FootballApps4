package com.dicoding.nicolas.detail.prev


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.dicoding.nicolas.R
import com.dicoding.nicolas.api.ApiRepository
import com.dicoding.nicolas.detail.DetailActivity
import com.dicoding.nicolas.detailMatch.DetailMatchActivity
import com.dicoding.nicolas.model.ItemEvent
import com.google.gson.Gson
import org.jetbrains.anko.*
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.support.v4.swipeRefreshLayout


/**
 * A simple [Fragment] subclass.
 */
class PreviousMatch : Fragment(), AnkoComponent<Context>, PrevMatchView {
    private var matchs: MutableList<ItemEvent> = mutableListOf()
    private lateinit var adapter: PrevAdapter
    private lateinit var listMatch: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var presenter: PrevPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = createView(AnkoContext.create(requireContext()))
        progressBar = view.findViewById(R.id.progressBarPrev)
        listMatch = view.findViewById(R.id.rvPrevious)
        adapter = PrevAdapter(matchs, { itemEvent: ItemEvent ->
            onClick(itemEvent)
        })

        listMatch.adapter = adapter
        listMatch.setHasFixedSize(true)
        listMatch.layoutManager = LinearLayoutManager(this.activity)

        val request = ApiRepository()
        val gson = Gson()
        presenter = PrevPresenter(this, request, gson)

        presenter.getPrevMatch(DetailActivity.idLeague)
        return view
    }

    override fun createView(ui: AnkoContext<Context>): View = with(ui) {
        linearLayout {
            lparams(width = matchParent, height = wrapContent)
            orientation = LinearLayout.VERTICAL
            topPadding = dip(16)
            leftPadding = dip(16)
            rightPadding = dip(16)

            swipeRefresh = swipeRefreshLayout {
                setColorSchemeResources(
                    R.color.colorAccent,
                    android.R.color.holo_green_light,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_red_light
                )

                relativeLayout {
                    lparams(width = matchParent, height = wrapContent)

                    recyclerView {
                        id = R.id.rvPrevious
                        lparams(width = matchParent, height = wrapContent)
                    }

                    progressBar {
                        id = R.id.progressBarPrev
                    }.lparams {
                        centerHorizontally()
                    }
                }
            }
        }
    }

    override fun showPrevList(data: List<ItemEvent>) {
        matchs.clear()
        matchs.addAll(data)
        adapter.notifyDataSetChanged()
    }

    override fun showLoading() {
        progressBar.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        progressBar.visibility = View.INVISIBLE
    }

    private fun onClick(event: ItemEvent) {
        val intent = Intent(activity, DetailMatchActivity::class.java)
        intent.putExtra("idEvent", event.idEvent)
        intent.putExtra("idHomeTeam", event.idHomeTeam)
        intent.putExtra("idAwayTeam", event.idAwayTeam)
        startActivity(intent)
    }
}
