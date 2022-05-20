package com.latdaniella.birthdaytracker
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.backendless.Backendless
import com.backendless.async.callback.AsyncCallback
import com.backendless.exceptions.BackendlessFault
import com.backendless.persistence.DataQueryBuilder
import com.latdaniella.birthdaytracker.databinding.ActivityBirthdayListBinding


class BirthdayListActivity : AppCompatActivity() {

    lateinit var binding : ActivityBirthdayListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBirthdayListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.fabBirthdayListCreateNew.setOnClickListener {

            val detailIntent = Intent(this, BirthdayDetailActivity::class.java)
            startActivity(detailIntent)
        }
    }

    override fun onStart() {
        super.onStart()


        //load data from database
        // put it in the recyclerview
        loadDataFromBackendless()
    }


    //making a small class inside the class instead of making a whole new class
    private fun loadDataFromBackendless() {

        //retrieve only objects whose ownerId matches the user's objectId
        val objectId = Backendless.UserService.CurrentUser().objectId
        val whereClause = "ownerId = '$objectId'"
        val queryBuilder = DataQueryBuilder.create()
        queryBuilder.whereClause = whereClause


        // retrieves all the objects regardless of owner
        // but adding the query builder will now search with the whereClause
        Backendless.Data.of(Person::class.java).find(queryBuilder, object : AsyncCallback<List<Person>?>{
            override fun handleResponse(foundPeople: List<Person>?) {
                Log.d("BirthdayList", "handleResponse: ${foundPeople}")
                val adapter = BirthdayAdapter((foundPeople ?: listOf<Person>()))
                binding.recyclerViewBirthdayListPeople.adapter = adapter
                binding.recyclerViewBirthdayListPeople.layoutManager = LinearLayoutManager(this@BirthdayListActivity)
            }

            override fun handleFault(fault: BackendlessFault?) {
                Log.d("BirthdayList", "handleFault: ${fault?.message}")
            }
        })
        //making a small class inside the class instead of making a whole new class ^^
    }
}