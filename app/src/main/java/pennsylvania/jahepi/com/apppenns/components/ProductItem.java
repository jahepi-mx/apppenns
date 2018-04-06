package pennsylvania.jahepi.com.apppenns.components;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import pennsylvania.jahepi.com.apppenns.R;
import pennsylvania.jahepi.com.apppenns.entities.Product;

/**
 * Created by javier.hernandez on 06/04/2018.
 */
public class ProductItem extends LinearLayout implements View.OnClickListener {

    private Product product;
    private TextView nameTextView;
    private Button deleteBtn;

    public ProductItem(Context context, Product product) {
        super(context);

        TableLayout tableLayout = new TableLayout(getContext());
        TableRow row = new TableRow(getContext());
        TableLayout.LayoutParams rowParams = new TableLayout.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT);

        DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
        int margin = (int) (10f * dm.density);
        rowParams.setMargins(margin, margin, margin, margin);
        row.setLayoutParams(rowParams);
        tableLayout.addView(row, 0);

        this.product = product;

        nameTextView = new TextView(getContext());
        nameTextView.setTypeface(Typeface.DEFAULT_BOLD);
        nameTextView.setTextColor(Color.BLACK);
        nameTextView.setTextSize(4 * dm.density);
        nameTextView.setText(product.getId() + " - " + product.getName());
        row.addView(nameTextView);

        deleteBtn = new Button(getContext());
        deleteBtn.setText(R.string.txt_delete);
        deleteBtn.setOnClickListener(this);
        row.addView(deleteBtn);

        addView(tableLayout);
    }

    @Override
    public void onClick(View v) {
        LinearLayout linearLayout = (LinearLayout) v.getParent().getParent().getParent().getParent();
        linearLayout.removeView(this);
    }
}
