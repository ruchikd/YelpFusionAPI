package com.example.weedmapschallenge.UI.SearchBusinesses.View.ViewAdapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weedmapschallenge.YelpDataModels.Business
import com.example.weedmapschallenge.R
import com.example.weedmapschallenge.UI.SearchBusinesses.Model.BusinessesModel
import com.example.weedmapschallenge.Utils.AppData
import java.lang.Exception
import java.lang.StringBuilder
import java.net.URL

class ExploreBusinessRecyclerViewAdapter: RecyclerView.Adapter<ExploreBusinessRecyclerViewAdapter.ExploreBusinessViewHolder> {
    private val TAG: String = this::class.java.simpleName
    private lateinit var context: Context

    private lateinit var businessModelList: List<BusinessesModel>

    constructor(context: Context, businessModelList: List<BusinessesModel>){
        this.context = context
        this.businessModelList= businessModelList
    }

    class ExploreBusinessViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var businessImageView: ImageView
        lateinit var businessNameTextView: TextView
        lateinit var businessDetailsTextView: TextView
        lateinit var businessReviewsTextView: TextView

        init {
            this.businessImageView = itemView.findViewById(R.id.business_image_view)
            this.businessNameTextView = itemView.findViewById(R.id.business_name_text_view)
            this.businessDetailsTextView = itemView.findViewById(R.id.business_details_text_view)
            this.businessReviewsTextView = itemView.findViewById(R.id.business_reviews_text_veiw)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExploreBusinessViewHolder {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.business_list_card_view, parent, false)
        val exploreBusinessListViewAdapterHolder: ExploreBusinessViewHolder =
            ExploreBusinessViewHolder(
                view
            )

        return exploreBusinessListViewAdapterHolder

    }

    override fun getItemCount(): Int {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        return businessModelList.size
    }

    override fun onBindViewHolder(holder: ExploreBusinessViewHolder, position: Int) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        holder.businessNameTextView.setText(businessModelList[position].businessName)
        val businessDetails = businessModelList[position].businessLocation.address1 + " " + businessModelList[position].businessLocation.address2 + " " +
                businessModelList[position].businessLocation.address3 + " " + businessModelList[position].businessLocation.city + " : " +
                businessModelList[position].businessPhoneNumber
        holder.businessDetailsTextView.setText(businessDetails)

        if (businessModelList[position].businessImage != null){
            try {
                var url: URL = URL(businessModelList[position].businessImage)
                var bmp: Bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                holder.businessImageView.setImageBitmap(bmp)
            } catch (e: Exception){
                Log.e(TAG, e.localizedMessage)
            }
        }

        var reviews: StringBuilder = StringBuilder()
        var reviewCountToShow: Int = 0
        for (review in businessModelList[position].businessReviews){
            reviewCountToShow += 1
            reviews.append(review.user.name + " said: (" + review.time_created.split(" ")[0] + ")")
            reviews.append(System.getProperty("line.separator"))
            reviews.append(review.text)
            reviews.append(System.getProperty("line.separator"))
            reviews.append(System.getProperty("line.separator"))
            if (reviewCountToShow == AppData.MAX_REVIEWS_TO_SHOW) {
                break
            }
        }

        holder.businessReviewsTextView.setText(reviews.toString())

    }
}