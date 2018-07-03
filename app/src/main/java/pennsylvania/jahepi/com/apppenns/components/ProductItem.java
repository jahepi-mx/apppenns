package pennsylvania.jahepi.com.apppenns.components;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import pennsylvania.jahepi.com.apppenns.R;
import pennsylvania.jahepi.com.apppenns.entities.Product;

/**
 * Created by javier.hernandez on 06/04/2018.
 * Product item component for storing the products for each task.
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
        this.product = product;
        this.nameTextView = (TextView) findViewById(R.id.productName);
        this.deleteBtn = (Button) findViewById(R.id.deleteProductBtn);
        this.quantityEditText = (EditText) findViewById(R.id.productQty);
        nameTextView.setText(product.getId() + " - " + product.getName());
        quantityEditText.setText(product.getQuantity() + "");
        quantityEditText.addTextChangedListener(this);
        deleteBtn.setOnClickListener(this);
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        listener.onDelete(this);
    }

    public void removeFromView() {
        LinearLayout linearLayout = (LinearLayout) this.getParent();
        linearLayout.removeView(this);
    }

    public interface ProductItemListener {
        void onDelete(ProductItem productItem);
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

    public Product getProduct() {
        return this.product;
    }
}
