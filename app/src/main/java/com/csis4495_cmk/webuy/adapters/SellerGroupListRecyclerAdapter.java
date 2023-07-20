package com.csis4495_cmk.webuy.adapters;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.csis4495_cmk.webuy.R;
import com.csis4495_cmk.webuy.models.Group;
import com.csis4495_cmk.webuy.models.ProductStyle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.checkerframework.checker.units.qual.C;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class SellerGroupListRecyclerAdapter extends RecyclerView.Adapter<SellerGroupListRecyclerAdapter.ViewHolder> {
    private Context context;
    private List<Group> groups = new ArrayList<>();

    private List<String> groupImgUrls = new ArrayList<>();

    private List<String> groupIds = new ArrayList<>();

    private Map<ViewHolder, CountDownTimer> countDowns = new HashMap<>();

    public SellerGroupListRecyclerAdapter() {
    }

    public SellerGroupListRecyclerAdapter(Context context, List<Group> groups) {
        this.context = context;
        this.groups = groups;

    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    public List<String> getGroupIds() {
        return groupIds;
    }

    public void setGroupIds(List<String> groupIds) {
        this.groupIds = groupIds;
    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        super.onViewRecycled(holder);

        if (countDowns.containsKey(holder)) {
            countDowns.get(holder).cancel();
            countDowns.remove(holder);
        }
    }

    @NonNull
    @Override
    public SellerGroupListRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_seller_group_detail, parent, false);
        SellerGroupListRecyclerAdapter.ViewHolder viewHolder = new SellerGroupListRecyclerAdapter.ViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SellerGroupListRecyclerAdapter.ViewHolder holder, int position) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference dBRef = firebaseDatabase.getReference();
        DatabaseReference groupRef = dBRef.child("Group");

        Group g = groups.get(position);
        String productId = g.getProductId();
        String groupImg = g.getGroupImages().get(0);
        String groupId = groupIds.get(position);

        StorageReference storageReference = FirebaseStorage.getInstance().getReference("ProductImage");
        StorageReference imageReference = storageReference.child(productId + "/" + groupImg);
        imageReference.getDownloadUrl().addOnSuccessListener(uri -> {
            // Got the download URL and pass it to Picasso to download, show in ImageView and caching
            Picasso.get().load(uri.toString()).into(holder.groupImg);
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle errors, if image doesn't exist, show a default image
                holder.groupImg.setImageResource(R.drawable.baseline_add_photo_alternate_50);
            }
        });

        holder.groupName.setText(g.getGroupName());
        holder.groupDescription.setText(g.getDescription());
        holder.groupPrice.setText(g.getGroupPrice());

        if (countDowns.containsKey(holder)) {
            countDowns.get(holder).cancel();
        }

        long currentTime = System.currentTimeMillis();
        Long startTimestamp = g.getStartTimestamp();
        Long endTimestamp = g.getEndTimestamp();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm yyyy-MM-dd");

        if (startTimestamp != 0) {
            Date startTime = new Date(startTimestamp);

            holder.groupStart.setText("From " + simpleDateFormat.format(startTime));

            if (currentTime < startTimestamp && g.getStatus() != 2 ) {
                long tillOpen = startTimestamp - System.currentTimeMillis();

                CountDownTimer timer = new CountDownTimer(tillOpen, 60000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        long days = TimeUnit.MILLISECONDS.toDays(millisUntilFinished);
                        millisUntilFinished -= TimeUnit.DAYS.toMillis(days);

                        long hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished);
                        millisUntilFinished -= TimeUnit.HOURS.toMillis(hours);

                        long minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished);
                        millisUntilFinished -= TimeUnit.MINUTES.toMillis(minutes);

                        String timeTillOpen = String.format(Locale.getDefault(), "%dD %dH %dM", days, hours, minutes);

                        holder.countDown.setText("Opens in " + timeTillOpen);
                    }

                    @Override
                    public void onFinish() {
                        DatabaseReference selectedGp = groupRef.child(groupId);
                        selectedGp.child("status").setValue(1).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Log.d(TAG, "Group status is updated: 1");

                                }else{
                                    Log.d(TAG, "Group status update error", task.getException());

                                }
                            }
                        });

                    }

                }.start();;

                countDowns.put(holder,timer);

            }
        } else {
            holder.groupStart.setVisibility(View.GONE);
            holder.countDown.setVisibility(View.GONE);
        }

        if (endTimestamp != 0 ) {
            Date endTime = new Date(endTimestamp);
            holder.groupEnd.setText("To " + simpleDateFormat.format(endTime));

            if (endTimestamp > currentTime && currentTime >= startTimestamp && g.getStatus() != 2) {
                long remainingTime = endTimestamp - currentTime;
                CountDownTimer timer = new CountDownTimer(remainingTime, 60000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        long days = TimeUnit.MILLISECONDS.toDays(millisUntilFinished);
                        millisUntilFinished -= TimeUnit.DAYS.toMillis(days);

                        long hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished);
                        millisUntilFinished -= TimeUnit.HOURS.toMillis(hours);

                        long minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished);
                        millisUntilFinished -= TimeUnit.MINUTES.toMillis(minutes);

                        String timeRemaining = String.format(Locale.getDefault(), "%dD %dH %dM", days, hours, minutes);

                        holder.countDown.setText("Ends in " + timeRemaining);
                    }

                    @Override
                    public void onFinish() {
                        DatabaseReference selectedGp = groupRef.child(groupId);
                        selectedGp.child("status").setValue(2).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Log.d(TAG, "Group status is updated: 2");
                                    holder.countDown.setVisibility(View.GONE);
                                }else{
                                    Log.d(TAG, "Group status update error", task.getException());

                                }
                            }
                        });

                    }
                }.start();;

                countDowns.put(holder,timer);

            }

        } else {
            holder.groupEnd.setVisibility(View.GONE);
            holder.countDown.setVisibility(View.GONE);
        }

        if((currentTime > endTimestamp && endTimestamp != 0 ) || g.getStatus() == 2){
            holder.countDown.setVisibility(View.VISIBLE);
            holder.countDown.setText("Group Closed");
        }

    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView groupImg;
        TextView groupName;
        TextView groupPrice;

        TextView groupDescription;
        TextView groupStart;
        TextView groupEnd;

        TextView countDown;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            groupImg = itemView.findViewById(R.id.img_group_img);
            groupName = itemView.findViewById(R.id.tv_group_name);
            groupDescription = itemView.findViewById(R.id.tv_group_description);
            groupPrice = itemView.findViewById(R.id.tv_group_price);
            groupStart = itemView.findViewById(R.id.tv_group_start);
            groupEnd = itemView.findViewById(R.id.tv_group_end);
            countDown = itemView.findViewById(R.id.tv_group_count_down);
        }
    }
}
