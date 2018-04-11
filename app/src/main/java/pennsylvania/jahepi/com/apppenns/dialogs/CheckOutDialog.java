package pennsylvania.jahepi.com.apppenns.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import pennsylvania.jahepi.com.apppenns.CustomApplication;
import pennsylvania.jahepi.com.apppenns.R;
import pennsylvania.jahepi.com.apppenns.Util;
import pennsylvania.jahepi.com.apppenns.activities.TaskViewActivity;
import pennsylvania.jahepi.com.apppenns.adapters.FileAttachmentAdapter;
import pennsylvania.jahepi.com.apppenns.adapters.ProductAdapter;
import pennsylvania.jahepi.com.apppenns.components.ProductItem;
import pennsylvania.jahepi.com.apppenns.components.filechooser.activities.FileChooserActivity;
import pennsylvania.jahepi.com.apppenns.entities.Attachment;
import pennsylvania.jahepi.com.apppenns.entities.Product;
import pennsylvania.jahepi.com.apppenns.entities.TaskActivity;

/**
 * Created by javier.hernandez on 08/03/2016.
 */
public class CheckOutDialog extends AlertDialog implements View.OnClickListener, AdapterView.OnItemClickListener, ProductItem.ProductItemListener {

    public final static int REQUEST_CODE_FILE_FROM_CHECKOUT = 8;
    public final static int REQUEST_IMAGE_CAPTURE_FROM_CHECKOUT = 16;

    private EditText editText, generalCommentEditText, competenceCommentEditText;
    private MultiAutoCompleteTextView emailTextiew;
    private DialogListener listener;
    private AutoCompleteEmailAdapter adapter;
    private Button yesBtn, noBtn;
    private ListView attachmentList;
    public FileAttachmentAdapter fileAttachmentAdapter;
    public File photoFile;
    private TaskViewActivity parentActivity;
    private HashMap<Integer, CheckBox> checkboxesHashMap = new HashMap<>();
    private AutoCompleteTextView productTextView;
    private ProductAdapter productAdapter;
    private LinearLayout productSearchLinearLayout, productsLinearLayout;
    private ArrayList<Product> taskProducts;
    private View view;

    public CheckOutDialog(Context context, TaskViewActivity parentActivity, DialogListener listener) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = (View) inflater.inflate(R.layout.checkout_dialog, null);
        this.parentActivity = parentActivity;
        editText = (EditText) view.findViewById(R.id.taskConclusionEditText);
        generalCommentEditText = (EditText) view.findViewById(R.id.taskGeneralCommentEditText);
        competenceCommentEditText = (EditText) view.findViewById(R.id.taskCompetenceCommentEditText);
        adapter = new AutoCompleteEmailAdapter(context, R.layout.generic_item);
        emailTextiew = (MultiAutoCompleteTextView) view.findViewById(R.id.emailText);
        emailTextiew.setAdapter(adapter);
        emailTextiew.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        taskProducts = new ArrayList<>();

        this.buildActivitiesCheckboxes(view);
        this.buildProductSearch(view);

        fileAttachmentAdapter = new FileAttachmentAdapter(this.parentActivity, R.layout.file_item, parentActivity.task.isCheckout());
        attachmentList = (ListView) view.findViewById(R.id.attachmentsListView);
        if (parentActivity.task != null) {
            fileAttachmentAdapter.addAll(parentActivity.task.getAttachmentsFromConclusion());
        }
        attachmentList.setAdapter(fileAttachmentAdapter);

        int numberOfAttachments = fileAttachmentAdapter.getCount();
        ViewGroup.LayoutParams params = attachmentList.getLayoutParams();
        int size = numberOfAttachments == 0 ? 160 : numberOfAttachments * 160;
        params.height = size;
        attachmentList.setLayoutParams(params);

        fileAttachmentAdapter.setChangeListener(new FileAttachmentAdapter.FileAttachmentAdapterListener() {
            @Override
            public void onChange(Attachment attachment) {
                int numberOfAttachments = fileAttachmentAdapter.getCount();
                ViewGroup.LayoutParams params = attachmentList.getLayoutParams();
                int size = numberOfAttachments == 0 ? 160 : numberOfAttachments * 160;
                params.height = size;
                attachmentList.setLayoutParams(params);
                CheckOutDialog.this.parentActivity.updateTask(true);
            }

            @Override
            public void onRemove(Attachment attachment) {
                int numberOfAttachments = fileAttachmentAdapter.getCount();
                ViewGroup.LayoutParams params = attachmentList.getLayoutParams();
                int size = numberOfAttachments == 0 ? 160 : numberOfAttachments * 160;
                params.height = size;
                attachmentList.setLayoutParams(params);
                CheckOutDialog.this.parentActivity.updateTask(true);
            }
        });

        Button photoBtn = (Button) view.findViewById(R.id.photoBtn);
        photoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (cameraIntent.resolveActivity(CheckOutDialog.this.getContext().getPackageManager()) != null) {
                    photoFile = Util.createImageFile(CheckOutDialog.this.parentActivity.getCustomApplication().getAndroidId());
                    if (photoFile != null) {
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                        CheckOutDialog.this.parentActivity.startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE_FROM_CHECKOUT);
                    }
                }
            }
        });

        Button filesBtn = (Button) view.findViewById(R.id.filesBtn);
        filesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckOutDialog.this.parentActivity.startActivityForResult(new Intent(CheckOutDialog.this.parentActivity, FileChooserActivity.class), REQUEST_CODE_FILE_FROM_CHECKOUT);
            }
        });

        setView(view);
        setTitle(R.string.txt_confirm);
        setIcon(R.drawable.ubication_black);
        setMessage(context.getString(R.string.txt_confirm_checkout));
        noBtn = (Button) view.findViewById(R.id.noBtn);
        yesBtn = (Button) view.findViewById(R.id.yesBtn);
        this.listener = listener;

        noBtn.setOnClickListener(this);
        yesBtn.setOnClickListener(this);
    }

    private void buildProductSearch(View view) {
        CustomApplication app = parentActivity.getCustomApplication();
        boolean hasProducts = app.getProductsTotal() > 0;

        TableLayout tableLayout = (TableLayout) view.findViewById(R.id.tableLayout);
        DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
        int margin = (int) (10f * dm.density);
        int level = 8;
        if (hasProducts) {

            TableRow row = new TableRow(view.getContext());
            TableLayout.LayoutParams rowParams = new TableLayout.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
            rowParams.setMargins(margin, margin, margin, margin);
            row.setLayoutParams(rowParams);
            tableLayout.addView(row, level++);

            productSearchLinearLayout = new LinearLayout(view.getContext());
            productSearchLinearLayout.setOrientation(LinearLayout.VERTICAL);
            row.addView(productSearchLinearLayout);

            TextView textView = new TextView(view.getContext());
            textView.setText(R.string.label_products);
            textView.setTypeface(Typeface.DEFAULT_BOLD);
            textView.setTextSize(6 * dm.density);
            productSearchLinearLayout.addView(textView);

            productTextView = new AutoCompleteTextView(view.getContext());
            productSearchLinearLayout.addView(productTextView);

            productAdapter = new ProductAdapter(app, R.layout.generic_item);
            productTextView.setAdapter(productAdapter);
            productTextView.setOnItemClickListener(this);
        }

        TableRow row = new TableRow(view.getContext());
        TableLayout.LayoutParams rowParams = new TableLayout.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
        rowParams.setMargins(margin, margin, margin, margin);
        row.setLayoutParams(rowParams);
        tableLayout.addView(row, level);

        productsLinearLayout = new LinearLayout(view.getContext());
        productsLinearLayout.setOrientation(LinearLayout.VERTICAL);
        row.addView(productsLinearLayout);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (parentActivity.task != null && (parentActivity.task.isCheckout() || parentActivity.task.isCancelled())) {
            return;
        }
        Product product = productAdapter.getItem(position);
        productTextView.setText("");

        ProductItem productItem = new ProductItem(view.getContext(), product, this);
        productsLinearLayout.addView(productItem);
        taskProducts.add(product);
    }

    public void onDelete(ProductItem productItem) {
        if (parentActivity.task != null && (parentActivity.task.isCheckout() || parentActivity.task.isCancelled())) {
            return;
        }
        taskProducts.remove(productItem.getProduct());
        productItem.removeFromView();
    }

    private void buildActivitiesCheckboxes(View view) {

        CustomApplication app = parentActivity.getCustomApplication();
        ArrayList<TaskActivity> taskActivities = app.getTaskActivities();

        TableLayout tableLayout = (TableLayout) view.findViewById(R.id.tableLayout);
        TableRow row = new TableRow(view.getContext());
        TableLayout.LayoutParams rowParams = new TableLayout.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT);

        DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
        int margin = (int) (10f * dm.density);
        rowParams.setMargins(margin, margin, margin, margin);
        row.setLayoutParams(rowParams);
        tableLayout.addView(row, 6);

        if (taskActivities.size() > 0) {
            TextView textView = new TextView(view.getContext());
            textView.setText(R.string.label_activity);
            textView.setTypeface(Typeface.DEFAULT_BOLD);
            textView.setTextSize(6 * dm.density);
            row.addView(textView);
        }

        row = new TableRow(view.getContext());
        row.setLayoutParams(rowParams);

        if (taskActivities.size() > 0) {
            LinearLayout linearLayout = new LinearLayout(view.getContext());
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            row.addView(linearLayout);

            Iterator<TaskActivity> iterator = taskActivities.iterator();
            while (iterator.hasNext()) {
                TaskActivity taskActivity = iterator.next();
                CheckBox checkBox = new CheckBox(view.getContext());
                checkBox.setText(taskActivity.getName());
                checkBox.setId(taskActivity.getId());
                if (parentActivity.task != null && (parentActivity.task.isCheckout() || parentActivity.task.isCancelled())) {
                    checkBox.setEnabled(false);
                }
                linearLayout.addView(checkBox);
                checkboxesHashMap.put(taskActivity.getId(), checkBox);
            }
        }
        tableLayout.addView(row, 7);
    }

    public String getConclusion() {
        return editText.getText().toString();
    }

    public String getGeneralComment() {
        return generalCommentEditText.getText().toString();
    }

    public String getCompetenceComment() {
        return competenceCommentEditText.getText().toString();
    }

    public String getEmails() {
        return emailTextiew.getText().toString();
    }

    public ArrayList<Product> getTaskProducts() {
        return (ArrayList<Product>) taskProducts.clone();
    }

    public ArrayList<TaskActivity> getTaskActivities() {
        ArrayList<TaskActivity> taskActivities = new ArrayList<>();
        for (CheckBox cb : checkboxesHashMap.values()) {
            if (cb.isChecked()) {
                TaskActivity taskActivity = new TaskActivity();
                taskActivity.setName(cb.getText().toString());
                taskActivity.setId(cb.getId());
                taskActivities.add(taskActivity);
            }
        }
        return taskActivities;
    }

    public void setConclusion(String conclusion) {
        editText.setText(conclusion);
    }

    public void setGeneralComment(String comment) {
        generalCommentEditText.setText(comment);
    }

    public void setCompetenceComment(String comment) {
        competenceCommentEditText.setText(comment);
    }

    public void setEmails(String emails) {
        emailTextiew.setText(emails);
    }

    public void setTaskActivities(ArrayList<TaskActivity> taskActivities) {
        Iterator<TaskActivity> iterator = taskActivities.iterator();
        while (iterator.hasNext()) {
            TaskActivity taskActivity = iterator.next();
            if (checkboxesHashMap.containsKey(taskActivity.getId())) {
                CheckBox cb = checkboxesHashMap.get(taskActivity.getId());
                cb.setChecked(true);
            }
        }
    }

    public void setTaskProducts(ArrayList<Product> products) {
        taskProducts.clear();
        productsLinearLayout.removeAllViews();
        CustomApplication app = parentActivity.getCustomApplication();
        boolean hasProducts = app.getProductsTotal() > 0;
        if (hasProducts) {
            for (Product product : products) {
                ProductItem productItem = new ProductItem(view.getContext(), product, this);
                productsLinearLayout.addView(productItem);
                taskProducts.add(product);
            }
        }
    }

    @Override
    public void onClick(View v) {
        dismiss();
        if (v == yesBtn) {
            listener.accept(this);
        }
    }

    private static class AutoCompleteEmailAdapter extends ArrayAdapter<String> {

        public AutoCompleteEmailAdapter(Context context, int resource) {
            super(context, resource);
        }

        @Override
        public String getItem(int position) {
            try {
                return super.getItem(position);
            } catch (Exception exp) {
                return null;
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null || convertView.getTag() == null) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.generic_item, null);
                TextView textViewName = (TextView) convertView.findViewById(R.id.name);
                ViewHolder holder = new ViewHolder();
                holder.textViewName = textViewName;
                convertView.setTag(holder);
            }
            ViewHolder holder = (ViewHolder) convertView.getTag();
            String item = getItem(position);
            if (item != null) {
                holder.textViewName.setText(getItem(position));
            }
            return convertView;
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults results = new FilterResults();
                    if (constraint != null) {
                        String keyword = constraint.toString();
                        ArrayList<String> emails = ((CustomApplication) getContext().getApplicationContext()).getUserEmails(keyword);
                        if (emails.size() > 0) {
                            results.values = emails;
                            results.count = emails.size();
                        }
                    }
                    return results;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count > 0) {
                        clear();
                        addAll((ArrayList<String>) results.values);
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }
            };
            return filter;
        }

        static class ViewHolder {
            TextView textViewName;
        }
    }
}
