package pennsylvania.jahepi.com.apppenns.components;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import pennsylvania.jahepi.com.apppenns.R;
import pennsylvania.jahepi.com.apppenns.entities.Product;

/**
 * Created by javier.hernandez on 06/04/2018.
 */
public class ProductItem extends LinearLayout implements View.OnClickListener, TextWatcher {

    private Product product;
    private TextView nameTextView;
    private EditText quantityEditText;
    private Button deleteBtn;
    private ProductItemListener listener;

    public ProductItem(Context context, Product product, ProductItemListener listener) {
        super(context);

        inflate(getContext(), R.layout.product_line, this);
        /*
        TableLayout tableLayout = new TableLayout(getContext());
        TableRow row = new TableRow(getContext());
        TableLayout.LayoutParams rowParams = new TableLayout.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT);

        DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
        int margin = (int) (10f * dm.density);
        rowParams.setMargins(margin, margin, margin, margin);
        row.setLayoutParams(rowParams);
        tableLayout.addView(row, 0);
        */
        this.product = product;
        this.nameTextView = (TextView) findViewById(R.id.productName);
        this.deleteBtn = (Button) findViewById(R.id.deleteProductBtn);
        this.quantityEditText = (EditText) findViewById(R.id.productQty);

        /*

        nameTextView = new TextView(getContext());
        nameTextView.setTypeface(Typeface.DEFAULT_BOLD);
        nameTextView.setTextColor(Color.BLACK);
        nameTextView.setTextSize(4 * dm.density);
        */
        nameTextView.setText(product.getId() + " - " + product.getName());
        /*row.addView(nameTextView);

        quantityEditText = new EditText(getContext());
        quantityEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        */
        quantityEditText.setText(product.getQuantity() + "");
        /*
        quantityEditText.setTypeface(Typeface.DEFAULT_BOLD);
        quantityEditText.setTextColor(Color.CYAN);
        quantityEditText.setTextSize(4 * dm.density);
        quantityEditText.setWidth((int) dm.density * 80);
        */
        quantityEditText.addTextChangedListener(this);
        /*row.addView(quantityEditText);

        deleteBtn = new Button(getContext());
        deleteBtn.setText(R.string.txt_delete);
        */
        deleteBtn.setOnClickListener(this);

        //row.addView(deleteBtn);

        //addView(tableLayout);

        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        LinearLayout linearLayout = (LinearLayout) v.getParent().getParent().getParent();
        listener.onDelete(product);
        linearLayout.removeView(this);
    }

    public interface ProductItemListener {
        void onDelete(Product product);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.length() > 0) {
            product.setQuantity(Integer.valueOf(s.toString()));
        } else {
            product.setQuantity(0);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
