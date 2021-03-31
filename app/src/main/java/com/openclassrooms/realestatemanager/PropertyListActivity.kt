package com.openclassrooms.realestatemanager

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.core.widget.NestedScrollView
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.openclassrooms.realestatemanager.databinding.PropertyListBinding
import com.openclassrooms.realestatemanager.main.PropertyListViewModel
import com.openclassrooms.realestatemanager.main.PropertyListViewModelFactory
import com.openclassrooms.realestatemanager.model.Property
import com.openclassrooms.realestatemanager.newproperty.NewPropertyActivity
import com.openclassrooms.realestatemanager.propertylist.PropertyListAdapter
import com.openclassrooms.realestatemanager.room.RealEstateApplication

/**
 * An activity representing a list of Pings. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a [PropertyDetailActivity] representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
class PropertyListActivity : AppCompatActivity() {
    private val viewModel: PropertyListViewModel by viewModels {
        PropertyListViewModelFactory((application as RealEstateApplication).propertyRepository)
    }
    private lateinit var binding: PropertyListBinding
    private lateinit var adapter: PropertyListAdapter

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private var twoPane: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_property_list)

        configureBinding()
        getProperties()
        goToNewPropertyActivity()

//        val toolbar = findViewById<Toolbar>(R.id.toolbar)
//        setSupportActionBar(toolbar)
//        toolbar.title = title
//
//        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                    .setAction("Action", null).show()
//        }


        if (findViewById<NestedScrollView>(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            twoPane = true
            Log.d("TAG", "twoPane: $twoPane")
        }

//        setupRecyclerView(findViewById(R.id.property_recyclerView))
    }

    private fun configureBinding() {
        binding = PropertyListBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    private fun getProperties() {
        viewModel.allProperty.observe(this, Observer { properties ->
            configureRecycleView(properties)
            Log.d("RV", "getProperties: $properties")
        })
    }

    private fun configureRecycleView(propertyList: List<Property>) {
        binding.propertyRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@PropertyListActivity)
            adapter = PropertyListAdapter(propertyList, twoPane) { property -> itemClicked(property) }
        }
    }

    private fun itemClicked(property: Property) {
        Toast.makeText(this, "Clicked: ${property.id}", Toast.LENGTH_SHORT).show()
        if (twoPane) {
            val fragment = PropertyDetailFragment().apply {
                arguments = Bundle().apply {
                    putString(PropertyDetailFragment.ARG_ITEM_ID, property.id.toString())
                }
            }
            this.supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.item_detail_container, fragment)
                    .commit()
        } else {
            val intent = Intent(this, PropertyDetailActivity::class.java).apply {
                putExtra(PropertyDetailFragment.ARG_ITEM_ID, property.id)
            }
            startActivity(intent)
        }
    }

    private fun goToNewPropertyActivity() {
        binding.fabNewProperty.setOnClickListener {
            startActivity(Intent(this, NewPropertyActivity::class.java))
        }
    }

//    private fun setupRecyclerView(recyclerView: RecyclerView) {
//        recyclerView.adapter = SimpleItemRecyclerViewAdapter(this, DummyContent.ITEMS, twoPane)
//    }

//    class SimpleItemRecyclerViewAdapter(private val parentActivity: PropertyListActivity,
//                                        private val values: List<DummyContent.DummyItem>,
//                                        private val twoPane: Boolean) :
//            RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder>() {
//
//        private val onClickListener: View.OnClickListener
//
//        init {
//            onClickListener = View.OnClickListener { v ->
//                val item = v.tag as DummyContent.DummyItem
//                if (twoPane) {
//                    val fragment = PropertyDetailFragment().apply {
//                        arguments = Bundle().apply {
//                            putString(PropertyDetailFragment.ARG_ITEM_ID, item.id)
//                        }
//                    }
//                    parentActivity.supportFragmentManager
//                            .beginTransaction()
//                            .replace(R.id.property_detail_container, fragment)
//                            .commit()
//                } else {
//                    val intent = Intent(v.context, PropertyDetailActivity::class.java).apply {
//                        putExtra(PropertyDetailFragment.ARG_ITEM_ID, item.id)
//                    }
//                    v.context.startActivity(intent)
//                }
//            }
//        }

//        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//            val view = LayoutInflater.from(parent.context)
//                    .inflate(R.layout.property_list_content, parent, false)
//            return ViewHolder(view)
//        }
//
//        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//            val item = values[position]
//            holder.idView.text = item.id
//            holder.contentView.text = item.content
//
//            with(holder.itemView) {
//                tag = item
//                setOnClickListener(onClickListener)
//            }
//        }
//
//        override fun getItemCount() = values.size
//
//        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//            val idView: TextView = view.findViewById(R.id.id_text)
//            val contentView: TextView = view.findViewById(R.id.content)
//        }
//    }
}