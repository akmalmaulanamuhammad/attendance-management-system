package com.example.attendanceapp.ui.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.attendanceapp.R;
import com.example.attendanceapp.databinding.ItemAttendanceBinding;
import com.example.attendanceapp.models.HistoryResponse.AttendanceRecord;
import com.example.attendanceapp.utils.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.AttendanceViewHolder> {
    private List<AttendanceRecord> records;
    private final SimpleDateFormat inputFormat;
    private final SimpleDateFormat dateFormat;
    private final SimpleDateFormat timeFormat;

    public AttendanceAdapter() {
        this.records = new ArrayList<>();
        this.inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        this.dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        this.timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
    }

    @NonNull
    @Override
    public AttendanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAttendanceBinding binding = ItemAttendanceBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new AttendanceViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AttendanceViewHolder holder, int position) {
        holder.bind(records.get(position));
    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    public void setItems(List<AttendanceRecord> newRecords) {
        this.records = newRecords;
        notifyDataSetChanged();
    }

    public void addItems(List<AttendanceRecord> newRecords) {
        int startPosition = this.records.size();
        this.records.addAll(newRecords);
        notifyItemRangeInserted(startPosition, newRecords.size());
    }

    public void clearItems() {
        this.records.clear();
        notifyDataSetChanged();
    }

    class AttendanceViewHolder extends RecyclerView.ViewHolder {
        private final ItemAttendanceBinding binding;

        AttendanceViewHolder(ItemAttendanceBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(AttendanceRecord record) {
            try {
                Date date = inputFormat.parse(record.getCheckInTime());
                if (date != null) {
                    binding.tvDate.setText(dateFormat.format(date));
                    binding.tvTime.setText(timeFormat.format(date));
                }
            } catch (ParseException e) {
                binding.tvDate.setText(record.getCheckInTime());
                binding.tvTime.setText("");
            }

            // Set status with appropriate color
            binding.tvStatus.setText(record.getStatus());
            int statusColor;
            switch (record.getStatus()) {
                case "PRESENT":
                    statusColor = R.color.status_present;
                    break;
                case "LATE":
                    statusColor = R.color.status_late;
                    break;
                default:
                    statusColor = R.color.status_invalid;
                    break;
            }
            binding.tvStatus.setBackgroundTintList(
                    itemView.getContext().getColorStateList(statusColor));

            // Set location
            String location = itemView.getContext().getString(
                    R.string.location_format,
                    record.getLatitude(),
                    record.getLongitude()
            );
            binding.tvLocation.setText(location);

            // Load photo using Glide
            if (record.getPhotoUrl() != null && !record.getPhotoUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(Constants.BASE_URL + record.getPhotoUrl())
                        .placeholder(R.drawable.placeholder_photo)
                        .error(R.drawable.placeholder_photo)
                        .centerCrop()
                        .into(binding.ivPhoto);
            } else {
                binding.ivPhoto.setImageResource(R.drawable.placeholder_photo);
            }
        }
    }
}
