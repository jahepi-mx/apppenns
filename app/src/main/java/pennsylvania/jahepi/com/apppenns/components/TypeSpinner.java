package pennsylvania.jahepi.com.apppenns.components;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Spinner;

import pennsylvania.jahepi.com.apppenns.entities.Type;

/**
 * Created by javier.hernandez on 30/03/2016.
 * Type Spinner component
 */
public class TypeSpinner extends Spinner {

    public TypeSpinner(Context context) {
        super(context);
    }

    public TypeSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setSelectedItem(Type type) {
        if (type == null) {
            return;
        }
        for (int i = 0; i < getCount(); i++) {
            Type typeObj = (Type) getItemAtPosition(i);
            if (typeObj.getId() == type.getId()) {
                setSelection(i);
                break;
            }
        }
    }
}
